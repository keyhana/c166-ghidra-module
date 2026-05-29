/*
 * MIT License
 * Copyright (c) 2024 Keyhan Asadi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 */
package ghidrainfineon;

import java.math.BigInteger;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.app.plugin.processors.sleigh.symbol.Symbol;
import ghidra.app.plugin.processors.sleigh.symbol.UseropSymbol;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSpace;
import ghidra.program.model.lang.*;
import ghidra.program.model.listing.Program;
import ghidra.program.model.listing.ProgramContext;
import ghidra.program.model.pcode.*;

/**
 * PCode injection for [rwm+#offset] address calculation.
 * 
 * If register is r0 (stack pointer) and offset < STACK_THRESHOLD: emit direct zext for stack analysis
 * If offset < STACK_THRESHOLD (non-r0): emit segment(0, reg + offset)
 * If offset >= STACK_THRESHOLD: emit segment(dpp, reg + (offset & 0x3FFF)) for DPP-paged access
 * 
 * This allows stack accesses to work cleanly while still supporting DPP paging for memory.
 */
public class RegOffsetAddr extends InjectPayloadCallother {
	private static final long STACK_THRESHOLD = 0x200; // Offsets below this are stack-like
	private static final long R0_REGISTER_OFFSET = 0;  // r0 is at offset 0 in register space
	
	private final SleighLanguage language;
	private final long uniqueBase;
	private int segmentUseropIndex = -1;
	private AddressSpace registerSpace;

	public RegOffsetAddr(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
		this.registerSpace = language.getAddressFactory().getRegisterSpace();
		
		// Find the "segment" userop index
		Symbol sym = language.getSymbolTable().findGlobalSymbol("segment");
		if (sym instanceof UseropSymbol) {
			segmentUseropIndex = ((UseropSymbol) sym).getIndex();
		}
	}
	
	/**
	 * Check if the varnode is the r0 register (stack pointer)
	 */
	private boolean isStackPointer(Varnode reg) {
		return reg.getAddress().getAddressSpace().equals(registerSpace) &&
		       reg.getOffset() == R0_REGISTER_OFFSET &&
		       reg.getSize() == 2;
	}

	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		Address currentAddr = con.baseAddr;
		Varnode regInput = con.inputlist.get(0);   // The register (e.g., r0 for stack)
		Varnode offsetInput = con.inputlist.get(1); // The immediate offset
		Varnode output = con.output.get(0);         // The 3-byte address result
		
		AddressSpace constSpace = language.getAddressFactory().getConstantSpace();
		AddressSpace uniqueSpace = language.getAddressFactory().getUniqueSpace();
		
		long offset = offsetInput.getOffset();
		
		// Use instruction address to create unique temp addresses
		long uniqueOffset = uniqueBase + ((currentAddr.getOffset() & 0xFFFFFF) >> 1) * 16;
		
		if (offset < STACK_THRESHOLD && isStackPointer(regInput)) {
			// Stack access with r0: emit direct zext without segment() for stack analysis
			return emitDirectStackAccess(currentAddr, regInput, offset, output,
					constSpace, uniqueSpace, uniqueOffset);
		} else if (offset < STACK_THRESHOLD) {
			// Small offset but not r0: use segment(0, reg + offset)
			return emitSimpleSegment(currentAddr, regInput, offset, output, 
					constSpace, uniqueSpace, uniqueOffset);
		} else {
			// DPP-paged access: segment(dpp, reg + (offset & 0x3FFF))
			return emitDppSegment(program, currentAddr, regInput, offset, output,
					constSpace, uniqueSpace, uniqueOffset);
		}
	}
	
	/**
	 * Emit direct stack access: output = r0 + offset
	 * No zext - just add and copy. This keeps the simple SP + offset pattern for stack analysis.
	 */
	private PcodeOp[] emitDirectStackAccess(Address addr, Varnode reg, long offset, Varnode output,
			AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[2];
		
		// 1. sum = r0 + offset (2-byte result)
		Varnode offsetConst = new Varnode(constSpace.getAddress(offset), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD, new Varnode[] { reg, offsetConst }, sum);
		
		// 2. output = copy(sum) - COPY from 2-byte to 3-byte implicitly zero-extends
		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.COPY, new Varnode[] { sum }, output);
		
		return ops;
	}
	
	/**
	 * Emit segment(0, reg + offset) for non-stack access with small offsets
	 */
	private PcodeOp[] emitSimpleSegment(Address addr, Varnode reg, long offset, Varnode output,
			AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[2];
		
		// 1. sum = reg + offset
		Varnode offsetConst = new Varnode(constSpace.getAddress(offset), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD, new Varnode[] { reg, offsetConst }, sum);
		
		// 2. output = segment(0, sum)
		Varnode useropId = new Varnode(constSpace.getAddress(segmentUseropIndex), 4);
		Varnode zeroConst = new Varnode(constSpace.getAddress(0), 2);
		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.CALLOTHER, 
				new Varnode[] { useropId, zeroConst, sum }, output);
		
		return ops;
	}
	
	/**
	 * Emit a paged-memory address calc for [rwm + #imm] when imm >= STACK_THRESHOLD.
	 *
	 * Resolution priority — same hierarchy as the C166 hardware:
	 *   1. EXTP active + page known   -> segment(Extp, reg + (offset & 0x3FFF))
	 *   2. EXTS active + segment known -> emit (Exts << 16) | (reg + offset)  (no segment(),
	 *                                     because the segment() pcodeop is page-shift only)
	 *   3. DPP[index of offset top bits] known -> segment(DPP, reg + (offset & 0x3FFF))
	 *   4. None known -> raw fallback: zext((reg + offset) & 0xFFFF) into the 3-byte address.
	 *
	 * The previous fallback `dppValue = dppIndex` produced phantom xrefs in the
	 * 0x0000..0xFFFF window because dppIndex is just the top two bits of offset
	 * (0..3) and segment(0..3, x) maps near zero, never to the binary's loaded
	 * pages. The raw fallback at least keeps the encoded address in sync with
	 * what the disassembly listing renders.
	 */
	private PcodeOp[] emitDppSegment(Program program, Address addr, Varnode reg, long offset,
			Varnode output, AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {

		ProgramContext progCtx = program.getProgramContext();

		// 1. EXTP override
		Long extpEnVal = readContext(progCtx, "ExtpEn", addr);
		if (extpEnVal != null && extpEnVal == 1L) {
			Long extpPage = readEffectiveExtValue(program, addr, "Extp", "ExtpReg");
			if (extpPage != null) {
				return emitPagedSegment(addr, reg, offset & 0x3FFF, extpPage & 0x3FFL,
						output, constSpace, uniqueSpace, uniqueOffset);
			}
			return emitRawFallback(addr, reg, offset, output, constSpace, uniqueSpace, uniqueOffset);
		}

		// 2. EXTS override (segment shift = 16 bits, full offset preserved)
		Long extsEnVal = readContext(progCtx, "ExtsEn", addr);
		if (extsEnVal != null && extsEnVal == 1L) {
			Long extsSeg = readEffectiveExtValue(program, addr, "Exts", "ExtsReg");
			if (extsSeg != null) {
				return emitSegmentShift16(addr, reg, offset & 0xFFFFL, extsSeg & 0xFFL,
						output, constSpace, uniqueSpace, uniqueOffset);
			}
			return emitRawFallback(addr, reg, offset, output, constSpace, uniqueSpace, uniqueOffset);
		}

		// 3. DPP-paged (default)
		int dppIndex = (int) ((offset >> 14) & 3);
		Long dppValue = getDppValue(program, addr, dppIndex);
		if (dppValue != null) {
			return emitPagedSegment(addr, reg, offset & 0x3FFF, dppValue,
					output, constSpace, uniqueSpace, uniqueOffset);
		}

		// 4. Nothing known — raw 16-bit fallback
		return emitRawFallback(addr, reg, offset, output, constSpace, uniqueSpace, uniqueOffset);
	}

	/**
	 * Emit `output = segment(page, reg + maskedOffset)` (page-shift = 14).
	 */
	private PcodeOp[] emitPagedSegment(Address addr, Varnode reg, long maskedOffset, long page,
			Varnode output, AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[2];

		Varnode offsetConst = new Varnode(constSpace.getAddress(maskedOffset), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD,
				new Varnode[] { reg, offsetConst }, sum);

		Varnode useropId = new Varnode(constSpace.getAddress(segmentUseropIndex), 4);
		Varnode pageConst = new Varnode(constSpace.getAddress(page), 2);
		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.CALLOTHER,
				new Varnode[] { useropId, pageConst, sum }, output);

		return ops;
	}

	/**
	 * Emit `output = (segment << 16) | (reg + offset)` for EXTS-overridden access.
	 * Cannot use the segment() pcodeop here because it always shifts by 14.
	 */
	private PcodeOp[] emitSegmentShift16(Address addr, Varnode reg, long offset, long segment,
			Varnode output, AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[3];

		// 1. sum:2 = reg + offset
		Varnode offsetConst = new Varnode(constSpace.getAddress(offset), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD,
				new Varnode[] { reg, offsetConst }, sum);

		// 2. zsum:3 = zext(sum)
		Varnode zsum = new Varnode(uniqueSpace.getAddress(uniqueOffset + 4), 3);
		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ZEXT, new Varnode[] { sum }, zsum);

		// 3. output:3 = zsum | (segment << 16)
		Varnode segConst = new Varnode(constSpace.getAddress(segment << 16), 3);
		ops[2] = new PcodeOp(addr, seqnum++, PcodeOp.INT_OR,
				new Varnode[] { zsum, segConst }, output);

		return ops;
	}

	/**
	 * Fallback when no DPP/EXTP/EXTS context is available: emit
	 * `output = zext((reg + offset) & 0xFFFF)`. Reference target then
	 * matches the disassembly operand and avoids creating phantom xrefs
	 * in the 0x0000..0xFFFF window.
	 */
	private PcodeOp[] emitRawFallback(Address addr, Varnode reg, long offset, Varnode output,
			AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[2];

		Varnode offsetConst = new Varnode(constSpace.getAddress(offset & 0xFFFFL), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD,
				new Varnode[] { reg, offsetConst }, sum);

		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ZEXT, new Varnode[] { sum }, output);
		return ops;
	}

	private Long readContext(ProgramContext progCtx, String regName, Address addr) {
		Register r = progCtx.getRegister(regName);
		if (r == null) return null;
		BigInteger v = progCtx.getValue(r, addr, false);
		return v == null ? null : v.longValue();
	}

	/**
	 * Resolve the actual EXTP/EXTS value at addr — immediate (Extp/Exts context
	 * register, regIdx = 0xF) or register-mode (Extp/Exts is irrelevant, look up
	 * GP register named via ExtpReg/ExtsReg).
	 */
	private Long readEffectiveExtValue(Program program, Address addr,
			String immRegName, String regIdxName) {
		ProgramContext progCtx = program.getProgramContext();

		Register regIdxReg = progCtx.getRegister(regIdxName);
		if (regIdxReg != null) {
			BigInteger regIdx = progCtx.getValue(regIdxReg, addr, false);
			if (regIdx != null) {
				int idx = regIdx.intValue() & 0xF;
				if (idx != 0xF) {
					Register gpReg = program.getRegister("r" + idx);
					if (gpReg != null) {
						BigInteger gpValue = progCtx.getValue(gpReg, addr, false);
						return gpValue == null ? null : gpValue.longValue();
					}
					return null;
				}
			}
		}

		Register immReg = progCtx.getRegister(immRegName);
		if (immReg == null) return null;
		BigInteger v = progCtx.getValue(immReg, addr, false);
		return v == null ? null : v.longValue();
	}

	/**
	 * Get the DPP register value for the given index
	 */
	private Long getDppValue(Program program, Address context, int dppIndex) {
		try {
			Register dppReg = program.getRegister("DPP" + dppIndex);
			if (dppReg != null) {
				BigInteger value = program.getProgramContext().getValue(dppReg, context, false);
				if (value != null) {
					return value.longValue() & 0x3FFL;
				}
			}
		} catch (Exception e) {
			// Fall through
		}
		return null;
	}
}


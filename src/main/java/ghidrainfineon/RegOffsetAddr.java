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
	 * Emit segment(dpp, reg + (offset & 0x3FFF)) for DPP-paged access
	 */
	private PcodeOp[] emitDppSegment(Program program, Address addr, Varnode reg, long offset, 
			Varnode output, AddressSpace constSpace, AddressSpace uniqueSpace, long uniqueOffset) {
		
		// Get DPP value based on offset's upper 2 bits
		int dppIndex = (int) ((offset >> 14) & 3);
		Long dppValue = getDppValue(program, addr, dppIndex);
		if (dppValue == null) {
			dppValue = (long) dppIndex; // Fallback
		}
		
		long maskedOffset = offset & 0x3FFF;
		
		int seqnum = 0;
		PcodeOp[] ops = new PcodeOp[2];
		
		// 1. sum = reg + maskedOffset
		Varnode offsetConst = new Varnode(constSpace.getAddress(maskedOffset), 2);
		Varnode sum = new Varnode(uniqueSpace.getAddress(uniqueOffset), 2);
		ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.INT_ADD, new Varnode[] { reg, offsetConst }, sum);
		
		// 2. output = segment(dpp, sum)
		Varnode useropId = new Varnode(constSpace.getAddress(segmentUseropIndex), 4);
		Varnode dppConst = new Varnode(constSpace.getAddress(dppValue), 2);
		ops[1] = new PcodeOp(addr, seqnum++, PcodeOp.CALLOTHER,
				new Varnode[] { useropId, dppConst, sum }, output);
		
		return ops;
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


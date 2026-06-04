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
import java.util.Set;

import ghidra.app.plugin.core.analysis.ConstantPropagationAnalyzer;
import ghidra.app.plugin.core.analysis.ConstantPropagationContextEvaluator;
import ghidra.app.util.importer.MessageLog;
import ghidra.framework.options.Options;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressFactory;
import ghidra.program.model.address.AddressOutOfBoundsException;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.address.AddressSpace;
import ghidra.program.model.data.DataType;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.*;
import ghidra.program.model.symbol.RefType;
import ghidra.program.util.ContextEvaluator;
import ghidra.program.util.SymbolicPropogator;
import ghidra.program.util.VarnodeContext;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

/**
 * Analyzer that applies C166 DPP/EXTP/EXTS address translation during constant propagation.
 * 
 * Overrides evaluateConstant to translate 16-bit addresses to 24-bit physical addresses.
 * The propagator handles operand detection and reference creation.
 */
public class C166AddressAnalyzer extends ConstantPropagationAnalyzer {

	private static final String PROCESSOR_NAME = "c166";
	private static final Set<String> SUPPORTED_PROCESSORS =
		Set.of("Infineon C167CR", "Infineon C167CS");

	static {
		for (String name : SUPPORTED_PROCESSORS) {
			ConstantPropagationAnalyzer.claimProcessor(name);
		}
	}
	private static final long PAGE_MASK = 0x3fffl;

	private final Register[] dppRegisters = new Register[4];
	private final Register[] gpRegisters = new Register[16];  // r0-r15
	private Register extp;
	private Register exts;
	private Register extpEn;
	private Register extsEn;
	private Register extpReg;
	private Register extsReg;
	private Register extpRegMode;
	private Register extsRegMode;

	public C166AddressAnalyzer() {
		super(PROCESSOR_NAME);
	}

	@Override
	public boolean canAnalyze(Program program) {
		String processor = program.getLanguage().getProcessor().toString();
		if (!SUPPORTED_PROCESSORS.contains(processor)) {
			return false;
		}

		this.processorName = processor;
		if (!super.canAnalyze(program)) {
			return false;
		}

		cacheRegisters(program);
		return true;
	}

	@Override
	public void registerOptions(Options options, Program program) {
		super.registerOptions(options, program);
	}

	@Override
	public boolean added(Program program, AddressSetView set, TaskMonitor monitor, MessageLog log)
			throws CancelledException {

		cacheRegisters(program);
		return super.added(program, set, monitor, log);
	}

	@Override
	public AddressSetView flowConstants(Program program, Address flowStart, AddressSetView flowSet,
			SymbolicPropogator symEval, TaskMonitor monitor) throws CancelledException {

		cacheRegisters(program);

		ContextEvaluator eval = new C166ContextEvaluator(program, monitor)
				.setTrustWritableMemory(trustWriteMemOption)
				.setMinSpeculativeOffset(minSpeculativeRefAddress)
				.setMaxSpeculativeOffset(maxSpeculativeRefAddress)
				.setMinStoreLoadOffset(minStoreLoadRefAddress)
				.setCreateComplexDataFromPointers(createComplexDataFromPointers);

		return symEval.flowConstants(flowStart, flowSet, eval, true, monitor);
	}

	private void cacheRegisters(Program program) {
		if (dppRegisters[0] != null) {
			return;
		}

		for (int i = 0; i < dppRegisters.length; i++) {
			dppRegisters[i] = program.getRegister("DPP" + i);
		}
		for (int i = 0; i < gpRegisters.length; i++) {
			gpRegisters[i] = program.getRegister("r" + i);
		}
		extp = program.getRegister("Extp");
		exts = program.getRegister("Exts");
		extpEn = program.getRegister("ExtpEn");
		extsEn = program.getRegister("ExtsEn");
		extpReg = program.getRegister("ExtpReg");
		extsReg = program.getRegister("ExtsReg");
		extpRegMode = program.getRegister("ExtpRegMode");
		extsRegMode = program.getRegister("ExtsRegMode");
	}

	private class C166ContextEvaluator extends ConstantPropagationContextEvaluator {

		private final Program program;
		private final AddressSpace ramSpace;

		C166ContextEvaluator(Program program, TaskMonitor monitor) {
			super(monitor);
			this.program = program;
			AddressFactory factory = program.getAddressFactory();
			AddressSpace dataSpace = factory.getAddressSpace("ram");
			this.ramSpace = dataSpace != null ? dataSpace : factory.getDefaultAddressSpace();
		}

		@Override
		public boolean evaluateDestination(VarnodeContext context, Instruction instr) {
			ProgramContext progCtx = program.getProgramContext();
			for (int i = 0; i < 4; i++) {
				Register dpp = dppRegisters[i];
				if (dpp == null) continue;
				BigInteger val = context.getValue(dpp, false);
				if (val != null) {
					try {
						progCtx.setValue(dpp, instr.getAddress(),
							instr.getAddress(), val);
					} catch (ContextChangeException e) {
						// ignore - can't set context in delay slot / flow override areas
					}
				}
			}
			return false;
		}

		/**
		 * Override evaluateConstant to translate 16-bit addresses to 24-bit using DPP/EXTP/EXTS.
		 *
		 * The propagator uses our returned address as the reference target,
		 * but uses the ORIGINAL offset for operand detection - so operands are found correctly!
		 */
		@Override
		public Address evaluateConstant(VarnodeContext context, Instruction instr, int pcodeop,
				Address constant, int size, DataType dataType, RefType refType) {
			
			// Only translate RAM space addresses
			if (constant == null || ramSpace == null) {
				return super.evaluateConstant(context, instr, pcodeop, constant, size, dataType, refType);
			}
			
			// Let parent filter out bad addresses first
			Address filtered = super.evaluateConstant(context, instr, pcodeop, constant, size, dataType, refType);
			if (filtered == null) {
				return null;
			}
			
			// Only translate if in RAM space and looks like a 16-bit offset
			if (!constant.getAddressSpace().equals(ramSpace)) {
				return filtered;
			}
			
			long raw = constant.getAddressableWordOffset();
			if (raw >= 0x10000) {
				// Already a 24-bit address
				return filtered;
			}
			
			// Translate the address
			Address translated = translateAddress(context, instr, raw);
			if (translated != null) {
				return translated;
			}
			
			// Translation failed - return filtered result from parent
			return filtered;
		}

		/**
		 * Translate a 16-bit address to 24-bit using DPP/EXTP/EXTS.
		 */
		private Address translateAddress(VarnodeContext context, Instruction instr, long raw) {
			Address instrAddr = instr.getAddress();
			ProgramContext progCtx = program.getProgramContext();
			
			// Check for EXTS override first (segment-based, uses full 16-bit offset)
			if (isContextEnabled(progCtx, instrAddr, extsEn)) {
				Long segment = getExtValue(context, progCtx, instrAddr, exts, extsReg, extsRegMode);
				if (segment != null) {
					segment = segment & 0xFFL;
					long resolved = (segment << 16) | (raw & 0xFFFFL);
					try {
						return ramSpace.getAddress(resolved, true);
					}
					catch (AddressOutOfBoundsException e) {
						return null;
					}
				}
				// EXTS enabled but value unavailable
				return null;
			}
			
			// Check for EXTP override (page-based, uses 14-bit offset)
			if (isContextEnabled(progCtx, instrAddr, extpEn)) {
				Long page = getExtValue(context, progCtx, instrAddr, extp, extpReg, extpRegMode);
				if (page != null) {
					page = page & 0x3FFL;
					long innerOffset = raw & PAGE_MASK;
					long resolved = (page << 14) | innerOffset;
					try {
						return ramSpace.getAddress(resolved, true);
					}
					catch (AddressOutOfBoundsException e) {
						return null;
					}
				}
				// EXTP enabled but value unavailable
				return null;
			}
			
			// Standard DPP translation
			int dppIndex = (int) ((raw >> 14) & 0x3);
			if (dppIndex >= dppRegisters.length) {
				return null;
			}

			Register dpp = dppRegisters[dppIndex];
			if (dpp == null) {
				return null;
			}

			BigInteger dppValue = context.getValue(dpp, false);
			if (dppValue == null) {
				return null;
			}

			long pageBase = dppValue.longValue() & 0x3FFL;
			long innerOffset = raw & PAGE_MASK;
			long resolved = (pageBase << 14) | innerOffset;

			try {
				return ramSpace.getAddress(resolved, true);
			}
			catch (AddressOutOfBoundsException e) {
				return null;
			}
		}

		/**
		 * Check if a context register is enabled (non-zero) using ProgramContext.
		 */
		private boolean isContextEnabled(ProgramContext progCtx, Address addr, Register register) {
			if (register == null) {
				return false;
			}
			BigInteger value = progCtx.getValue(register, addr, false);
			return value != null && !value.equals(BigInteger.ZERO);
		}

		/**
		 * Get the EXTP/EXTS value, checking if it's register-based or immediate.
		 *
		 * Mode is decided by the dedicated 1-bit context register
		 * (ExtpRegMode/ExtsRegMode). The earlier sentinel-based scheme
		 * (regIdx == 0xF) collided with the legitimate register index for
		 * r15.
		 */
		private Long getExtValue(VarnodeContext varnodeCtx, ProgramContext progCtx, Address addr,
				Register immReg, Register regIdxReg, Register regModeReg) {
			if (regModeReg != null) {
				BigInteger regMode = progCtx.getValue(regModeReg, addr, false);
				if (regMode != null && regMode.equals(BigInteger.ONE)) {
					if (regIdxReg != null) {
						BigInteger regIdx = progCtx.getValue(regIdxReg, addr, false);
						if (regIdx != null) {
							int idx = regIdx.intValue() & 0xF;
							if (idx < gpRegisters.length) {
								Register gpReg = gpRegisters[idx];
								if (gpReg != null) {
									BigInteger gpValue = varnodeCtx.getValue(gpReg, false);
									if (gpValue != null) {
										return gpValue.longValue();
									}
								}
							}
						}
					}
					return null;  // Register-based but value unknown
				}
			}

			// Immediate mode: use the context register value from ProgramContext
			if (immReg != null) {
				BigInteger immValue = progCtx.getValue(immReg, addr, false);
				if (immValue != null) {
					return immValue.longValue();
				}
			}

			return null;
		}
	}
}

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
import java.util.HashMap;
import java.util.Map;
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
import ghidra.program.model.lang.RegisterValue;
import ghidra.program.model.listing.*;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.RefType;
import ghidra.program.util.ContextEvaluator;
import ghidra.program.util.SymbolicPropogator;
import ghidra.program.util.VarnodeContext;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

/**
 * Analyzer that applies C166 DPP/EXTP/EXTS address translation during constant propagation.
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
	private static final long PAGE_MASK = 0x3fffL;

	private final Register[] dppRegisters = new Register[4];
	private Register extp;
	private Register exts;
	private Register extpEn;
	private Register extsEn;
	private Register extpValue;
	private Register extsValue;

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
		extp = program.getRegister("Extp");
		exts = program.getRegister("Exts");
		extpEn = program.getRegister("ExtpEn");
		extsEn = program.getRegister("ExtsEn");
		extpValue = program.getRegister("ExtpValue");
		extsValue = program.getRegister("ExtsValue");
	}

	private class C166ContextEvaluator extends ConstantPropagationContextEvaluator {

		private final Program program;
		private final AddressSpace ramSpace;
		private final Map<Address, Long> extpLatches = new HashMap<>();
		private final Map<Address, Long> extsLatches = new HashMap<>();

		C166ContextEvaluator(Program program, TaskMonitor monitor) {
			super(monitor);
			this.program = program;
			AddressFactory factory = program.getAddressFactory();
			AddressSpace dataSpace = factory.getAddressSpace("ram");
			this.ramSpace = dataSpace != null ? dataSpace : factory.getDefaultAddressSpace();
		}

		@Override
		public boolean evaluateContext(VarnodeContext context, Instruction instr) {
			String mnemonic = instr.getMnemonicString().toLowerCase();
			boolean page = mnemonic.equals("extp") || mnemonic.equals("extpr");
			boolean segment = mnemonic.equals("exts") || mnemonic.equals("extsr");
			if (!page && !segment) return false;

			Register source = null;
			Scalar count = null;
			for (Object object : instr.getOpObjects(0)) {
				if (object instanceof Register register) {
					source = register;
				}
				else if (object instanceof Scalar scalar) {
					count = scalar;
				}
			}
			if (source == null) return false;
			BigInteger value = context.getValue(source, false);
			if (value == null) return false;

			Instruction first = instr.getNext();
			if (count == null || first == null) return false;
			Instruction last = first;
			Map<Address, Long> latches = page ? extpLatches : extsLatches;
			latches.put(first.getAddress(), value.longValue());
			for (long i = 1; i < count.getUnsignedValue(); i++) {
				last = last.getNext();
				if (last == null) return false;
				latches.put(last.getAddress(), value.longValue());
			}

			Register target = page ? extpValue : extsValue;
			try {
				program.getProgramContext().setValue(target, first.getAddress(),
					last.getAddress(), value);
			}
			catch (ContextChangeException e) {
				throw new IllegalStateException("Cannot latch " + target + " over extension window", e);
			}
			return false;
		}

		/**
		 * Override evaluateConstant to translate 16-bit addresses to 24-bit using DPP/EXTP/EXTS.
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
				Long segment = getExtValue(progCtx, instrAddr, extsValue, exts);
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
				Long page = getExtValue(progCtx, instrAddr, extpValue, extp);
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

			BigInteger dppValue = progCtx.getValue(dpp, instr.getAddress(), false);
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

		/** Get the EXTP/EXTS value latched in ProgramContext. */
		private Long getExtValue(ProgramContext progCtx, Address addr, Register latchReg,
				Register valueReg) {
			Long flowValue = (latchReg == extpValue ? extpLatches : extsLatches).get(addr);
			if (flowValue != null) {
				return flowValue;
			}
			if (latchReg != null) {
				RegisterValue latchedValue = progCtx.getNonDefaultValue(latchReg, addr);
				if (latchedValue != null && latchedValue.hasValue()) {
					return latchedValue.getUnsignedValue().longValue();
				}
			}
			if (valueReg != null) {
				BigInteger immValue = progCtx.getValue(valueReg, addr, false);
				if (immValue != null) {
					return immValue.longValue();
				}
			}

			return null;
		}
	}
}

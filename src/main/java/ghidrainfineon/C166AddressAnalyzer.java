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
import java.util.List;
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
import ghidra.program.model.lang.OperandType;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.*;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.RefType;
import ghidra.program.model.symbol.SourceType;
import ghidra.program.util.ContextEvaluator;
import ghidra.program.util.SymbolicPropogator;
import ghidra.program.util.VarnodeContext;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

/**
 * Analyzer that post-processes indirect memory references to apply the C166 DPP/EXT paging rules.
 *
 * This logic runs after the standard constant propagation flow and rewrites data references that
 * are still expressed as 16-bit offsets by incorporating the active page register content.
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
	private Register extp;
	private Register exts;
	private Register extpEn;
	private Register extsEn;

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
		public boolean evaluateReference(VarnodeContext context, Instruction instr, int pcodeop,
				Address address, int size, DataType dataType, RefType refType) {

			if (!shouldTranslate(refType, address)) {
				return super.evaluateReference(context, instr, pcodeop, address, size, dataType,
					refType);
			}

			Address translated = translateAddress(context, address);
			if (translated == null) {
				return super.evaluateReference(context, instr, pcodeop, address, size, dataType,
					refType);
			}

			boolean allow = super.evaluateReference(context, instr, pcodeop, translated, size,
				dataType, refType);
			if (!allow) {
				return false;
			}

			removeLegacyReferences(instr, address);
			attachReference(context, instr, translated, refType, address);
			return false;
		}

		private boolean shouldTranslate(RefType refType, Address address) {
			if (address == null || ramSpace == null) {
				return false;
			}
			if (!address.getAddressSpace().equals(ramSpace)) {
				return false;
			}
			if (!(refType.isRead() || refType.isWrite() || refType.isData())) {
				return false;
			}
			long raw = address.getAddressableWordOffset();
			return raw < 0x10000;
		}

		private Address translateAddress(VarnodeContext context, Address candidate) {
			long raw = candidate.getAddressableWordOffset();
			
			// Check for EXTS override first (segment-based, uses full 16-bit offset)
			if (isEnabled(context, extsEn) && exts != null) {
				BigInteger segValue = context.getValue(exts, false);
				if (segValue != null) {
					long segment = segValue.longValue() & 0xFFL;
					long resolved = (segment << 16) | (raw & 0xFFFFL);
					if (resolved == raw) {
						return null;
					}
					try {
						return ramSpace.getAddress(resolved, true);
					}
					catch (AddressOutOfBoundsException e) {
						return null;
					}
				}
			}
			
			// Check for EXTP override (page-based, uses 14-bit offset)
			if (isEnabled(context, extpEn) && extp != null) {
				BigInteger pageValue = context.getValue(extp, false);
				if (pageValue != null) {
					long page = pageValue.longValue() & 0x3FFL;
					long innerOffset = raw & PAGE_MASK;
					long resolved = (page << 14) | innerOffset;
					if (resolved == raw) {
						return null;
					}
					try {
						return ramSpace.getAddress(resolved, true);
					}
					catch (AddressOutOfBoundsException e) {
						return null;
					}
				}
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
			if (resolved == raw) {
				return null;
			}

			try {
				return ramSpace.getAddress(resolved, true);
			}
			catch (AddressOutOfBoundsException e) {
				return null;
			}
		}

		private boolean isEnabled(VarnodeContext context, Register register) {
			if (register == null) {
				return false;
			}
			BigInteger value = context.getValue(register, false);
			return value != null && !value.equals(BigInteger.ZERO);
		}

		private void removeLegacyReferences(Instruction instr, Address staleAddress) {
			if (staleAddress == null) {
				return;
			}
			Reference[] refs = instr.getReferencesFrom();
			for (Reference ref : refs) {
				if (!staleAddress.equals(ref.getToAddress())) {
					continue;
				}
				if (ref.getSource() != SourceType.ANALYSIS) {
					continue;
				}
				instr.removeOperandReference(ref.getOperandIndex(), staleAddress);
			}
		}

		private void attachReference(VarnodeContext context, Instruction instr, Address target,
				RefType refType, Address original) {

			int opIndex = findOperandIndex(context, instr, original, refType);
			if (opIndex == Reference.MNEMONIC) {
				instr.addMnemonicReference(target, refType, SourceType.ANALYSIS);
			}
			else {
				instr.addOperandReference(opIndex, target, refType, SourceType.ANALYSIS);
			}
		}

		private int findOperandIndex(VarnodeContext context, Instruction instr, Address original,
				RefType refType) {

			if (original == null) {
				return Reference.MNEMONIC;
			}

			long wordOffset = original.getAddressableWordOffset();
			int numOperands = instr.getNumOperands();
			for (int i = 0; i < numOperands; i++) {
				int type = instr.getOperandType(i);

				if ((type & OperandType.ADDRESS) != 0) {
					Address opAddr = instr.getAddress(i);
					if (opAddr != null && opAddr.getAddressableWordOffset() == wordOffset) {
						return i;
					}
				}

				if ((type & OperandType.SCALAR) != 0) {
					Scalar scalar = instr.getScalar(i);
					if (scalar != null) {
						long value = scalar.getUnsignedValue();
						if (value == wordOffset || value == (wordOffset >> 1)) {
							return i;
						}
					}
				}

				if ((type & OperandType.REGISTER) != 0 && refType.isFlow()) {
					Register reg = instr.getRegister(i);
					if (reg != null && reg.isProgramCounter()) {
						return i;
					}
				}

				if ((type & OperandType.DYNAMIC) != 0) {
					List<Object> reps = instr.getDefaultOperandRepresentationList(i);
					if (reps == null || reps.isEmpty()) {
						continue;
					}
					long residue = wordOffset;
					for (Object rep : reps) {
						if (rep instanceof Scalar scalarRep) {
							residue -= scalarRep.getUnsignedValue();
						}
						else if (rep instanceof Register registerRep) {
							BigInteger regValue = context.getValue(registerRep, false);
							if (regValue != null) {
								residue -= regValue.longValue();
							}
						}
					}
					if (residue == 0) {
						return i;
					}
				}
			}

			return Reference.MNEMONIC;
		}
	}
}


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
import java.util.ArrayList;
import java.util.List;

import ghidra.app.cmd.disassemble.DisassembleCommand;
import ghidra.app.cmd.function.CreateFunctionCmd;
import ghidra.app.services.AbstractAnalyzer;
import ghidra.app.services.AnalysisPriority;
import ghidra.app.services.AnalyzerType;
import ghidra.app.util.importer.MessageLog;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.lang.OperandType;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.Listing;
import ghidra.program.model.listing.Program;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryAccessException;
import ghidra.program.model.pcode.JumpTable;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.RefType;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.SourceType;
import ghidra.program.model.listing.ProgramContext;
import ghidra.util.exception.CancelledException;
import ghidra.util.exception.InvalidInputException;
import ghidra.util.task.TaskMonitor;

/**
 * Automatically recovers C166 indirect {@code jmpi} switch tables during analysis.
 *
 * <p>C166 firmware dispatches state machines through {@code jmpi [Rw]} where the
 * target register is loaded from a word table. Two encodings appear:
 *
 * <pre>
 *   indexed:   mov Rw, [Rw + #tableOffset]   ; Rw already holds state*2
 *              jmpi cc, [Rw]
 *
 *   base+add:  add Rw, #tableOffset           ; (classic switch_load form)
 *              mov Rw, [Rw]
 *              jmpi cc, [Rw]
 * </pre>
 *
 * The SLEIGH {@code c166_switch_load} injector only resolves the {@code mov Rw,[Rw]}
 * (base+add) form. The indexed form leaves the {@code jmpi} as an unresolved dynamic
 * goto, so the decompiler follows it into data and fails with
 * {@code "No pcode could be generated"} (notably the seg-A CAPCOM interrupt handlers).
 *
 * <p>This analyzer reproduces the {@code C166SwitchOverride} script for every
 * {@code jmpi} automatically, handling both encodings, so no per-{@code jmpi} manual
 * script run is required (README "Known Limitations" #2). Table offsets are resolved
 * through the current DPP/EXTP/EXTS context (run after {@code C166AddressAnalyzer});
 * case counts are bounded exactly by the guarding {@code cmp} and by the next table in
 * memory, avoiding over-reads into adjacent tables.
 */
public class C166SwitchAnalyzer extends AbstractAnalyzer {

	private static final String NAME = "C166 Switch Table Recovery";
	private static final String DESCRIPTION =
		"Resolves C166 indirect jmpi switch tables (indexed mov Rw,[Rw+#off] and base+add forms) "
			+ "and writes decompiler jump-table overrides so RAM-switch handlers decompile.";

	private static final int MAX_BACKTRACK = 24;
	private static final int ABSOLUTE_CASE_CAP = 256;

	public C166SwitchAnalyzer() {
		super(NAME, DESCRIPTION, AnalyzerType.INSTRUCTION_ANALYZER);
		// Run after disassembly and after C166 DPP/address propagation so table
		// offsets resolve against settled DPP context.
		setPriority(AnalysisPriority.DATA_TYPE_PROPOGATION.after());
		setDefaultEnablement(true);
		setSupportsOneTimeAnalysis();
	}

	@Override
	public boolean canAnalyze(Program program) {
		String proc = program.getLanguage().getProcessor().toString();
		return proc.equals("Infineon C167CR") || proc.equals("Infineon C167CS");
	}

	@Override
	public boolean added(Program program, AddressSetView set, TaskMonitor monitor, MessageLog log)
			throws CancelledException {

		Listing listing = program.getListing();

		// Pass 1: collect every unresolved jmpi in the added set and the table it reads.
		List<Candidate> candidates = new ArrayList<>();
		for (Instruction insn : listing.getInstructions(set, true)) {
			monitor.checkCancelled();
			if (!"jmpi".equalsIgnoreCase(insn.getMnemonicString())) {
				continue;
			}
			if (alreadyResolved(insn)) {
				continue; // idempotent: injector or a previous pass handled it
			}
			if (program.getFunctionManager().getFunctionContaining(insn.getAddress()) == null) {
				continue;
			}
			Candidate c = analyzeJmpi(program, insn);
			if (c != null) {
				candidates.add(c);
			}
		}
		if (candidates.isEmpty()) {
			return false;
		}

		// Pass 2: bound each table by the next table in memory, then write overrides.
		candidates.sort((a, b) -> Long.compareUnsigned(a.tableAddr.getOffset(), b.tableAddr.getOffset()));
		int applied = 0;
		for (int i = 0; i < candidates.size(); i++) {
			monitor.checkCancelled();
			Candidate c = candidates.get(i);
			long byNeighbour = ABSOLUTE_CASE_CAP;
			if (i + 1 < candidates.size()) {
				long gap = candidates.get(i + 1).tableAddr.subtract(c.tableAddr);
				if (gap > 0) {
					byNeighbour = gap / 2;
				}
			}
			long limit = Math.min(byNeighbour, ABSOLUTE_CASE_CAP);
			if (c.maxCase >= 0) {
				limit = Math.min(limit, c.maxCase + 1L);
			}
			if (applyOverride(program, c, (int) limit, monitor)) {
				applied++;
			}
		}
		if (applied > 0) {
			log.appendMsg(NAME, "Resolved " + applied + " C166 jmpi switch table(s).");
		}
		return applied > 0;
	}

	/** A jmpi whose table address and (optional) max case have been located. */
	private static final class Candidate {
		final Instruction jmpi;
		final Address tableAddr;
		final int maxCase; // -1 if no guarding cmp was found

		Candidate(Instruction jmpi, Address tableAddr, int maxCase) {
			this.jmpi = jmpi;
			this.tableAddr = tableAddr;
			this.maxCase = maxCase;
		}
	}

	private boolean alreadyResolved(Instruction jmpi) {
		for (Reference ref : jmpi.getReferencesFrom()) {
			if (ref.getReferenceType().isComputed() && ref.getReferenceType().isJump()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Locate the dispatch table for a {@code jmpi} by scanning backward for either the
	 * indexed {@code mov Rw,[Rw+#off]} load or the base {@code add Rw,#off} offset, plus
	 * the optional {@code cmp} that bounds the case count. Returns {@code null} when no
	 * table offset can be found or the DPP-resolved table is unreadable.
	 */
	private Candidate analyzeJmpi(Program program, Instruction jmpi) {
		Long tableOffset = null;
		int maxCase = -1;
		Instruction cur = jmpi.getPrevious();
		for (int i = 0; i < MAX_BACKTRACK && cur != null; i++) {
			String m = cur.getMnemonicString().toLowerCase();
			if (tableOffset == null && (m.equals("mov") || m.equals("add"))) {
				Long off = largeDisplacement(cur);
				if (off != null) {
					tableOffset = off;
				}
			}
			if (maxCase < 0 && m.startsWith("cmp")) {
				Long v = smallScalar(cur);
				if (v != null) {
					maxCase = v.intValue();
				}
			}
			if (tableOffset != null && maxCase >= 0) {
				break;
			}
			cur = cur.getPrevious();
		}
		if (tableOffset == null) {
			return null;
		}
		Address tableAddr = resolveTableAddress(program, tableOffset, jmpi.getAddress());
		if (tableAddr == null || !program.getMemory().contains(tableAddr)) {
			return null;
		}
		return new Candidate(jmpi, tableAddr, maxCase);
	}

	/** Largest immediate/displacement (&gt; 0x1000) in any operand, scalar or dynamic. */
	private Long largeDisplacement(Instruction insn) {
		for (int op = 0; op < insn.getNumOperands(); op++) {
			int type = insn.getOperandType(op);
			if ((type & OperandType.SCALAR) != 0) {
				Scalar s = insn.getScalar(op);
				if (s != null && s.getUnsignedValue() > 0x1000) {
					return s.getUnsignedValue();
				}
			}
			if ((type & OperandType.DYNAMIC) != 0) {
				List<Object> reps = insn.getDefaultOperandRepresentationList(op);
				if (reps != null) {
					for (Object rep : reps) {
						if (rep instanceof Scalar s && s.getUnsignedValue() > 0x1000) {
							return s.getUnsignedValue();
						}
					}
				}
			}
		}
		return null;
	}

	/** Small scalar (&lt; 256, non-zero) operand, for the guarding {@code cmp}. */
	private Long smallScalar(Instruction insn) {
		for (int op = 0; op < insn.getNumOperands(); op++) {
			Scalar s = insn.getScalar(op);
			if (s != null) {
				long v = s.getUnsignedValue();
				if (v > 0 && v < 256) {
					return v;
				}
			}
		}
		return null;
	}

	/** DPP/EXTP/EXTS translation of a 16-bit table offset, mirroring CreateDPPReference. */
	private Address resolveTableAddress(Program program, long offset16, Address context) {
		ProgramContext ctx = program.getProgramContext();
		long innerOffset = offset16 & 0x3FFF;

		Register extsEn = program.getRegister("ExtsEn");
		Register exts = program.getRegister("Exts");
		if (extsEn != null && exts != null && isSet(ctx, extsEn, context)) {
			BigInteger seg = ctx.getValue(exts, context, false);
			if (seg != null) {
				return ram(program, (seg.longValue() & 0xFF) << 16 | (offset16 & 0xFFFF));
			}
		}
		Register extpEn = program.getRegister("ExtpEn");
		Register extp = program.getRegister("Extp");
		if (extpEn != null && extp != null && isSet(ctx, extpEn, context)) {
			BigInteger page = ctx.getValue(extp, context, false);
			if (page != null) {
				return ram(program, (page.longValue() & 0x3FF) << 14 | innerOffset);
			}
		}
		Register dpp = program.getRegister("DPP" + ((int) ((offset16 >> 14) & 0x3)));
		if (dpp == null) {
			return null;
		}
		BigInteger page = ctx.getValue(dpp, context, false);
		if (page == null) {
			return null; // DPP context unknown; cannot translate (do not guess)
		}
		return ram(program, (page.longValue() & 0x3FF) << 14 | innerOffset);
	}

	private boolean isSet(ProgramContext ctx, Register en, Address at) {
		BigInteger v = ctx.getValue(en, at, false);
		return v != null && v.equals(BigInteger.ONE);
	}

	private Address ram(Program program, long physical) {
		return program.getAddressFactory().getDefaultAddressSpace().getAddress(physical);
	}

	/**
	 * Read up to {@code limit} word entries, combine each with the code segment of the
	 * {@code jmpi}, disassemble and reference each target, and write the jump-table
	 * override. Stops early on an 0xFFFF padding word or a target outside code memory.
	 */
	private boolean applyOverride(Program program, Candidate c, int limit, TaskMonitor monitor)
			throws CancelledException {
		Memory mem = program.getMemory();
		Listing listing = program.getListing();
		long codeSegment = c.jmpi.getAddress().getOffset() & 0xFF0000L;

		ArrayList<Address> targets = new ArrayList<>();
		for (int i = 0; i < limit; i++) {
			Address entry = c.tableAddr.add(i * 2L);
			if (!mem.contains(entry)) {
				break;
			}
			int word;
			try {
				word = mem.getShort(entry) & 0xFFFF;
			}
			catch (MemoryAccessException e) {
				break;
			}
			if (word == 0xFFFF) {
				break; // padding / end of table
			}
			Address target = ram(program, codeSegment | word);
			if (!mem.contains(target) || !mem.getBlock(target).isExecute()) {
				break;
			}
			targets.add(target);
		}
		if (targets.isEmpty()) {
			return false;
		}

		Function func = program.getFunctionManager().getFunctionContaining(c.jmpi.getAddress());
		if (func == null) {
			return false;
		}

		for (Address target : targets) {
			if (listing.getInstructionAt(target) == null) {
				new DisassembleCommand(target, null, true).applyTo(program, monitor);
			}
			c.jmpi.addOperandReference(0, target, RefType.COMPUTED_JUMP, SourceType.ANALYSIS);
		}

		JumpTable table = new JumpTable(c.jmpi.getAddress(), targets, true, 0);
		try {
			table.writeOverride(func);
		}
		catch (InvalidInputException e) {
			return false;
		}
		CreateFunctionCmd.fixupFunctionBody(program, func, monitor);
		return true;
	}
}

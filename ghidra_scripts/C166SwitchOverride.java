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
// Creates a switch table override at the current jmpi instruction.
// Dynamically finds ADD (table offset) and CMP (max case) by scanning backwards.
// Automatically resolves DPP addressing for the switch table.
// Uses Ghidra's standard naming conventions (switchD_addr, caseD_N).
//
// Usage: Place cursor on a jmpi instruction and run the script.
//
// @category C166
// @keybinding ctrl shift S
// @menupath Analysis.Create C166 Switch Override

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import ghidra.app.cmd.disassemble.DisassembleCommand;
import ghidra.app.cmd.function.CreateFunctionCmd;
import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSet;
import ghidra.program.model.address.AddressSpace;
import ghidra.program.model.lang.OperandType;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.*;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryAccessException;
import ghidra.program.model.pcode.JumpTable;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.RefType;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.ReferenceManager;
import ghidra.program.model.symbol.SourceType;

public class C166SwitchOverride extends GhidraScript {

    private static final int MAX_BACKTRACK = 20;

    @Override
    protected void run() throws Exception {
        if (currentAddress == null) {
            printerr("No address selected");
            return;
        }

        Instruction jmpi = getInstructionAt(currentAddress);
        if (jmpi == null || !jmpi.getMnemonicString().equalsIgnoreCase("jmpi")) {
            printerr("Please place cursor on a JMPI instruction");
            return;
        }

        Function func = getFunctionContaining(currentAddress);
        if (func == null) {
            printerr("JMPI must be inside a function");
            return;
        }

        // Find table offset by looking back for ADD with scalar
        Long tableOffset = findTableOffset(jmpi);
        if (tableOffset == null) {
            printerr("Could not find ADD instruction with table offset");
            return;
        }

        // Find max case by looking back for CMP with scalar
        Integer maxCase = findMaxCase(jmpi);
        if (maxCase == null) {
            // Ask user
            String input = askString("Max Case", "Could not find CMP. Enter max case value:");
            if (input == null || input.isEmpty()) {
                return;
            }
            maxCase = Integer.decode(input);
        }

        // Find default address by looking for conditional jump
        Address defaultAddr = findDefaultAddress(jmpi);

        // Resolve the table address using DPP
        Address tableAddr = resolveTableAddress(tableOffset, jmpi.getAddress());
        if (tableAddr == null) {
            printerr("Could not resolve table address with DPP");
            return;
        }

        // Get code segment for target address construction
        long codeSegment = currentAddress.getOffset() & 0xFF0000;

        // Read switch targets from table
        ArrayList<Address> targets = readSwitchTargets(tableAddr, maxCase, codeSegment);
        if (targets == null || targets.isEmpty()) {
            printerr("Could not read switch targets from table");
            return;
        }

        // Create the switch structure with Ghidra conventions
        createSwitchStructure(jmpi, tableAddr, targets, defaultAddr, func);

        println("Successfully created switch with " + targets.size() + " cases at " + jmpi.getAddress());
    }

    /**
     * Scans backwards to find ADD instruction with table offset
     */
    private Long findTableOffset(Instruction jmpi) {
        Instruction current = jmpi.getPrevious();
        
        for (int i = 0; i < MAX_BACKTRACK && current != null; i++) {
            String mnemonic = current.getMnemonicString().toLowerCase();
            
            if (mnemonic.equals("add")) {
                // Look for scalar operand (the table offset)
                Long scalar = getScalarFromInstruction(current);
                if (scalar != null && scalar > 0x100) { // Table offset should be reasonably large
                    return scalar;
                }
            }
            
            current = current.getPrevious();
        }
        
        return null;
    }

    /**
     * Scans backwards to find CMP instruction with max case value
     */
    private Integer findMaxCase(Instruction jmpi) {
        Instruction current = jmpi.getPrevious();
        
        for (int i = 0; i < MAX_BACKTRACK && current != null; i++) {
            String mnemonic = current.getMnemonicString().toLowerCase();
            
            if (mnemonic.startsWith("cmp")) {
                // Look for scalar operand (the max case)
                Long scalar = getScalarFromInstruction(current);
                if (scalar != null && scalar < 256) { // Max case should be reasonable
                    return scalar.intValue();
                }
            }
            
            current = current.getPrevious();
        }
        
        return null;
    }

    /**
     * Scans backwards to find conditional jump to default case
     */
    private Address findDefaultAddress(Instruction jmpi) {
        Instruction current = jmpi.getPrevious();
        
        for (int i = 0; i < MAX_BACKTRACK && current != null; i++) {
            String mnemonic = current.getMnemonicString().toLowerCase();
            
            // Look for conditional jumps (jmpa, jmpr with condition)
            if ((mnemonic.startsWith("jmpa") || mnemonic.startsWith("jmpr")) && 
                !mnemonic.contains("cc_uc")) {
                
                // Try to get target address
                Address target = getJumpTarget(current);
                if (target != null) {
                    return target;
                }
            }
            
            current = current.getPrevious();
        }
        
        return null;
    }

    /**
     * Gets a scalar value from instruction operands
     */
    private Long getScalarFromInstruction(Instruction instr) {
        for (int i = 0; i < instr.getNumOperands(); i++) {
            int opType = instr.getOperandType(i);
            
            if ((opType & OperandType.SCALAR) != 0) {
                Scalar s = instr.getScalar(i);
                if (s != null) {
                    return s.getUnsignedValue();
                }
            }
            
            // Also check dynamic operands
            if ((opType & OperandType.DYNAMIC) != 0) {
                List<Object> reps = instr.getDefaultOperandRepresentationList(i);
                if (reps != null) {
                    for (Object rep : reps) {
                        if (rep instanceof Scalar) {
                            return ((Scalar) rep).getUnsignedValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets jump target from a jump instruction
     */
    private Address getJumpTarget(Instruction instr) {
        // Try address operand
        for (int i = 0; i < instr.getNumOperands(); i++) {
            Address addr = instr.getAddress(i);
            if (addr != null && !addr.equals(instr.getAddress())) {
                return addr;
            }
        }

        // Try references
        Reference[] refs = instr.getReferencesFrom();
        for (Reference ref : refs) {
            if (ref.getReferenceType().isJump()) {
                return ref.getToAddress();
            }
        }

        return null;
    }

    /**
     * Resolves the 24-bit table address using DPP registers
     */
    private Address resolveTableAddress(long offset16, Address context) {
        // Determine DPP index from bits 14-15
        int dppIndex = (int) ((offset16 >> 14) & 0x3);
        long innerOffset = offset16 & 0x3FFF;

        // Check for EXTS override
        Register extsEn = currentProgram.getRegister("ExtsEn");
        Register exts = currentProgram.getRegister("Exts");
        if (extsEn != null && exts != null) {
            BigInteger enVal = currentProgram.getProgramContext().getValue(extsEn, context, false);
            if (enVal != null && enVal.equals(BigInteger.ONE)) {
                BigInteger segVal = currentProgram.getProgramContext().getValue(exts, context, false);
                if (segVal != null) {
                    long segment = segVal.longValue() & 0xFF;
                    long resolved = (segment << 16) | (offset16 & 0xFFFF);
                    return getAddressInRam(resolved);
                }
            }
        }

        // Check for EXTP override
        Register extpEn = currentProgram.getRegister("ExtpEn");
        Register extp = currentProgram.getRegister("Extp");
        if (extpEn != null && extp != null) {
            BigInteger enVal = currentProgram.getProgramContext().getValue(extpEn, context, false);
            if (enVal != null && enVal.equals(BigInteger.ONE)) {
                BigInteger pageVal = currentProgram.getProgramContext().getValue(extp, context, false);
                if (pageVal != null) {
                    long page = pageVal.longValue() & 0x3FF;
                    long resolved = (page << 14) | innerOffset;
                    return getAddressInRam(resolved);
                }
            }
        }

        // Standard DPP translation
        Register dppReg = currentProgram.getRegister("DPP" + dppIndex);
        if (dppReg == null) {
            printerr("DPP" + dppIndex + " register not found");
            return null;
        }

        BigInteger dppVal = currentProgram.getProgramContext().getValue(dppReg, context, false);
        if (dppVal == null) {
            // Ask user for DPP value
            try {
                String input = askString("DPP" + dppIndex + " Unknown",
                    "Enter DPP" + dppIndex + " page value (hex, e.g., 0x24 for segment 0x90000):");
                if (input != null && !input.isEmpty()) {
                    long page = Long.decode(input) & 0x3FF;
                    long resolved = (page << 14) | innerOffset;
                    return getAddressInRam(resolved);
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        long page = dppVal.longValue() & 0x3FF;
        long resolved = (page << 14) | innerOffset;
        return getAddressInRam(resolved);
    }

    private Address getAddressInRam(long offset) {
        AddressSpace ramSpace = currentProgram.getAddressFactory().getAddressSpace("ram");
        if (ramSpace == null) {
            ramSpace = currentProgram.getAddressFactory().getDefaultAddressSpace();
        }
        return ramSpace.getAddress(offset);
    }

    /**
     * Reads switch target addresses from the table
     */
    private ArrayList<Address> readSwitchTargets(Address tableAddr, int maxCase, long codeSegment) {
        ArrayList<Address> targets = new ArrayList<>();
        Memory mem = currentProgram.getMemory();
        AddressSpace space = currentProgram.getAddressFactory().getDefaultAddressSpace();

        for (int i = 0; i <= maxCase; i++) {
            Address entryAddr = tableAddr.add(i * 2);
            try {
                // Read 16-bit target offset from table
                int targetOffset = mem.getShort(entryAddr) & 0xFFFF;
                // Combine with code segment
                long fullTarget = codeSegment | targetOffset;
                Address targetAddr = space.getAddress(fullTarget);
                targets.add(targetAddr);
            } catch (MemoryAccessException e) {
                printerr("Cannot read table entry at " + entryAddr);
                return null;
            }
        }

        return targets;
    }

    /**
     * Creates the switch structure with proper references
     * Note: JumpTable.writeOverride() creates its own labels (override::jmp_XXX::case_N)
     */
    private void createSwitchStructure(Instruction jmpi, Address tableAddr,
                                        ArrayList<Address> targets, Address defaultAddr,
                                        Function func) throws Exception {

        Address switchAddr = jmpi.getAddress();
        ReferenceManager refMgr = currentProgram.getReferenceManager();
        Listing listing = currentProgram.getListing();

        // Disassemble and create references for each case
        for (int i = 0; i < targets.size(); i++) {
            Address targetAddr = targets.get(i);

            // Disassemble target if needed
            if (listing.getInstructionAt(targetAddr) == null) {
                DisassembleCommand cmd = new DisassembleCommand(targetAddr, null, true);
                cmd.applyTo(currentProgram, monitor);
            }

            // Add reference from jmpi to target
            jmpi.addOperandReference(0, targetAddr, RefType.COMPUTED_JUMP, SourceType.ANALYSIS);

            // Add reference from table entry to target
            Address entryAddr = tableAddr.add(i * 2);
            refMgr.addMemoryReference(entryAddr, targetAddr, RefType.DATA, SourceType.ANALYSIS, 0);
        }

        // Create the JumpTable override for the decompiler
        // This automatically creates labels: override::jmp_XXX::case_N
        JumpTable jumpTable = new JumpTable(switchAddr, targets, true);
        jumpTable.writeOverride(func);

        // Fix up function body to include all switch targets
        CreateFunctionCmd.fixupFunctionBody(currentProgram, func, monitor);

        println("Table at: " + tableAddr);
        println("Cases: " + targets.size());
        if (defaultAddr != null) {
            println("Default: " + defaultAddr);
        }
    }
}

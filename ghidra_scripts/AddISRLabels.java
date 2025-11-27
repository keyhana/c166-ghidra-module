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

// Labels C166/C167 Interrupt Service Routines based on the vector table at 0x0000-0x0200.
// Disassembles vector entries if needed, follows jumps to find actual handlers,
// and renames thunks and functions appropriately.
//
// @category C166
// @keybinding ctrl shift I
// @menupath Analysis.Add C166 ISR Labels
// @author Keyhan Asadi

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.listing.*;
import ghidra.program.model.symbol.*;

import java.util.HashMap;
import java.util.Map;

public class AddISRLabels extends GhidraScript {

    // Map of vector address -> ISR name
    private static final Map<Long, String> ISR_VECTORS = new HashMap<>();
    
    static {
        // Reset and Traps
        ISR_VECTORS.put(0x0000L, "RESET");
        ISR_VECTORS.put(0x0008L, "NMITRAP");
        ISR_VECTORS.put(0x0010L, "STOTRAP");
        ISR_VECTORS.put(0x0018L, "STUTRAP");
        ISR_VECTORS.put(0x0028L, "BTRAP");
        
        // Reserved (0x002C-0x003C)
        ISR_VECTORS.put(0x002CL, "RESERVED_0B");
        ISR_VECTORS.put(0x0030L, "RESERVED_0C");
        ISR_VECTORS.put(0x0034L, "RESERVED_0D");
        ISR_VECTORS.put(0x0038L, "RESERVED_0E");
        ISR_VECTORS.put(0x003CL, "RESERVED_0F");
        
        // CAPCOM Registers 0-15
        ISR_VECTORS.put(0x0040L, "CC0INT");
        ISR_VECTORS.put(0x0044L, "CC1INT");
        ISR_VECTORS.put(0x0048L, "CC2INT");
        ISR_VECTORS.put(0x004CL, "CC3INT");
        ISR_VECTORS.put(0x0050L, "CC4INT");
        ISR_VECTORS.put(0x0054L, "CC5INT");
        ISR_VECTORS.put(0x0058L, "CC6INT");
        ISR_VECTORS.put(0x005CL, "CC7INT");
        ISR_VECTORS.put(0x0060L, "CC8INT");
        ISR_VECTORS.put(0x0064L, "CC9INT");
        ISR_VECTORS.put(0x0068L, "CC10INT");
        ISR_VECTORS.put(0x006CL, "CC11INT");
        ISR_VECTORS.put(0x0070L, "CC12INT");
        ISR_VECTORS.put(0x0074L, "CC13INT");
        ISR_VECTORS.put(0x0078L, "CC14INT");
        ISR_VECTORS.put(0x007CL, "CC15INT");
        
        // CAPCOM Timers 0-1
        ISR_VECTORS.put(0x0080L, "T0INT");
        ISR_VECTORS.put(0x0084L, "T1INT");
        
        // GPT1 Timers 2-4
        ISR_VECTORS.put(0x0088L, "T2INT");
        ISR_VECTORS.put(0x008CL, "T3INT");
        ISR_VECTORS.put(0x0090L, "T4INT");
        
        // GPT2 Timers 5-6 and CAPREL
        ISR_VECTORS.put(0x0094L, "T5INT");
        ISR_VECTORS.put(0x0098L, "T6INT");
        ISR_VECTORS.put(0x009CL, "CRINT");
        
        // A/D Converter
        ISR_VECTORS.put(0x00A0L, "ADCINT");
        ISR_VECTORS.put(0x00A4L, "ADEINT");
        
        // ASC0
        ISR_VECTORS.put(0x00A8L, "S0TINT");
        ISR_VECTORS.put(0x00ACL, "S0RINT");
        ISR_VECTORS.put(0x00B0L, "S0EINT");
        
        // SSC
        ISR_VECTORS.put(0x00B4L, "SCTINT");
        ISR_VECTORS.put(0x00B8L, "SCRINT");
        ISR_VECTORS.put(0x00BCL, "SCEINT");
        
        // CAPCOM Registers 16-28
        ISR_VECTORS.put(0x00C0L, "CC16INT");
        ISR_VECTORS.put(0x00C4L, "CC17INT");
        ISR_VECTORS.put(0x00C8L, "CC18INT");
        ISR_VECTORS.put(0x00CCL, "CC19INT");
        ISR_VECTORS.put(0x00D0L, "CC20INT");
        ISR_VECTORS.put(0x00D4L, "CC21INT");
        ISR_VECTORS.put(0x00D8L, "CC22INT");
        ISR_VECTORS.put(0x00DCL, "CC23INT");
        ISR_VECTORS.put(0x00E0L, "CC24INT");
        ISR_VECTORS.put(0x00E4L, "CC25INT");
        ISR_VECTORS.put(0x00E8L, "CC26INT");
        ISR_VECTORS.put(0x00ECL, "CC27INT");
        ISR_VECTORS.put(0x00F0L, "CC28INT");
        
        // CAPCOM Timers 7-8
        ISR_VECTORS.put(0x00F4L, "T7INT");
        ISR_VECTORS.put(0x00F8L, "T8INT");
        
        // PWM
        ISR_VECTORS.put(0x00FCL, "PWMINT");
        
        // CAN and External
        ISR_VECTORS.put(0x0100L, "XP0INT");
        ISR_VECTORS.put(0x0104L, "XP1INT");
        ISR_VECTORS.put(0x0108L, "XP2INT");
        ISR_VECTORS.put(0x010CL, "XP3INT");
        
        // CAPCOM Registers 29-31
        ISR_VECTORS.put(0x0110L, "CC29INT");
        ISR_VECTORS.put(0x0114L, "CC30INT");
        ISR_VECTORS.put(0x0118L, "CC31INT");
        
        // ASC0 Transmit Buffer
        ISR_VECTORS.put(0x011CL, "S0TBINT");
    }

    @Override
    protected void run() throws Exception {
        int labeled = 0;
        int skipped = 0;
        
        println("=== C166 ISR Label Script ===");
        println("Scanning interrupt vector table (0x0000-0x0200)...\n");
        
        for (Map.Entry<Long, String> entry : ISR_VECTORS.entrySet()) {
            long vectorAddr = entry.getKey();
            String isrName = entry.getValue();
            
            Address vecAddress = toAddr(vectorAddr);
            
            // Get the instruction at the vector location, disassemble if needed
            Instruction instr = getInstructionAt(vecAddress);
            if (instr == null) {
                // Try to disassemble at this address
                instr = disassembleAt(vecAddress);
                if (instr == null) {
                    println(String.format("  [SKIP] 0x%04X %s - Could not disassemble", vectorAddr, isrName));
                    skipped++;
                    continue;
                }
            }
            
            // Label the vector entry itself
            addLabel(vecAddress, "vec_" + isrName, true);
            
            // Find the jump target
            Address targetAddr = getJumpTarget(instr);
            if (targetAddr == null) {
                println(String.format("  [SKIP] 0x%04X %s - No jump target found", vectorAddr, isrName));
                skipped++;
                continue;
            }
            
            // Process the target (could be a thunk or the actual handler)
            Address handlerAddr = processTarget(targetAddr, isrName, 0);
            
            if (handlerAddr != null) {
                println(String.format("  [OK]   0x%04X %s -> handler at 0x%s", 
                    vectorAddr, isrName, handlerAddr.toString()));
                labeled++;
            } else {
                println(String.format("  [WARN] 0x%04X %s -> target at 0x%s (no function)", 
                    vectorAddr, isrName, targetAddr.toString()));
                // Still label the target even if not a function
                addLabel(targetAddr, isrName + "_handler", true);
                labeled++;
            }
        }
        
        println(String.format("\n=== Complete: %d labeled, %d skipped ===", labeled, skipped));
    }
    
    /**
     * Disassemble at the given address and return the instruction.
     */
    private Instruction disassembleAt(Address addr) {
        try {
            // Disassemble starting at this address
            disassemble(addr);
            
            // Return the instruction at this address
            return getInstructionAt(addr);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get the jump target from an instruction (handles jmps, jmpa, jmpr, etc.)
     */
    private Address getJumpTarget(Instruction instr) {
        if (instr == null) return null;
        
        String mnemonic = instr.getMnemonicString().toLowerCase();
        
        // Check for jump instructions
        if (mnemonic.startsWith("jmp") || mnemonic.equals("bra")) {
            // Get the flow destination
            Address[] flows = instr.getFlows();
            if (flows != null && flows.length > 0) {
                return flows[0];
            }
            
            // Try to get from operand reference
            Reference[] refs = instr.getReferencesFrom();
            for (Reference ref : refs) {
                RefType refType = ref.getReferenceType();
                if (refType.isJump() || refType.isFlow()) {
                    return ref.getToAddress();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Process a target address - follow thunks and rename functions.
     * Returns the final handler address.
     */
    private Address processTarget(Address targetAddr, String isrName, int depth) throws Exception {
        if (depth > 5) {
            // Prevent infinite loops
            return targetAddr;
        }
        
        Function func = getFunctionAt(targetAddr);
        
        // If no function exists, try to create one
        if (func == null) {
            // Check if there's an instruction here, disassemble if needed
            Instruction instr = getInstructionAt(targetAddr);
            if (instr == null) {
                instr = disassembleAt(targetAddr);
            }
            if (instr != null) {
                // Create a function
                func = createFunction(targetAddr, isrName + "_handler");
            }
        }
        
        if (func != null) {
            // Check if this is a thunk (single jump to another function)
            Address thunkedAddr = getThunkedAddress(func);
            
            if (thunkedAddr != null) {
                // This is a thunk - rename it as such and follow
                String thunkName = isrName + "_thunk";
                renameFunction(func, thunkName);
                
                // Process the actual target
                return processTarget(thunkedAddr, isrName, depth + 1);
            } else {
                // This is the actual handler
                renameFunction(func, isrName + "_handler");
                return targetAddr;
            }
        }
        
        // No function, just create a label
        addLabel(targetAddr, isrName + "_handler", true);
        return targetAddr;
    }
    
    /**
     * Check if a function is a thunk (just jumps to another address).
     * Returns the thunked address or null if not a thunk.
     */
    private Address getThunkedAddress(Function func) {
        if (func == null) return null;
        
        // Get the first instruction
        Address entry = func.getEntryPoint();
        Instruction instr = getInstructionAt(entry);
        
        if (instr == null) return null;
        
        String mnemonic = instr.getMnemonicString().toLowerCase();
        
        // Check if it's an unconditional jump
        if (mnemonic.startsWith("jmp") || mnemonic.equals("bra")) {
            // Check function body size - thunks are typically very small
            long bodySize = func.getBody().getNumAddresses();
            if (bodySize <= 4) { // Typical jump is 2-4 bytes
                Address[] flows = instr.getFlows();
                if (flows != null && flows.length > 0) {
                    return flows[0];
                }
            }
        }
        
        return null;
    }
    
    /**
     * Rename a function, handling default names specially.
     */
    private void renameFunction(Function func, String newName) throws Exception {
        if (func == null) return;
        
        String currentName = func.getName();
        
        // Check if it has a default name (FUN_xxxx or already an ISR name)
        if (currentName.startsWith("FUN_") || 
            currentName.startsWith("thunk_FUN_") ||
            currentName.equals(newName)) {
            
            try {
                func.setName(newName, SourceType.USER_DEFINED);
            } catch (Exception e) {
                // Name might already exist, try with address suffix
                String addrSuffix = "_" + func.getEntryPoint().toString().replace(":", "_");
                func.setName(newName + addrSuffix, SourceType.USER_DEFINED);
            }
        }
        // If it already has a meaningful name, don't overwrite
    }
    
    /**
     * Create or update a label at an address.
     */
    private void addLabel(Address addr, String name, boolean makePrimary) throws Exception {
        SymbolTable symbolTable = currentProgram.getSymbolTable();
        
        // Check if a symbol with this name already exists at this address
        Symbol[] existingSymbols = symbolTable.getSymbols(addr);
        for (Symbol sym : existingSymbols) {
            if (sym.getName().equals(name)) {
                return; // Already exists
            }
        }
        
        try {
            symbolTable.createLabel(addr, name, SourceType.USER_DEFINED);
        } catch (Exception e) {
            // Might fail if name is duplicate - ignore
        }
    }
}


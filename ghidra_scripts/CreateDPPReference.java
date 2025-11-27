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
// Creates a DPP-resolved data reference for the current instruction.
// Uses the C166 DPP registers to calculate the effective 24-bit address.
// @category C166
// @keybinding ctrl shift D
// @menupath Analysis.Create DPP Reference

import java.math.BigInteger;

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSpace;
import ghidra.program.model.lang.OperandType;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.RefType;
import ghidra.program.model.symbol.SourceType;

public class CreateDPPReference extends GhidraScript {

    @Override
    protected void run() throws Exception {
        if (currentAddress == null) {
            printerr("No address selected");
            return;
        }

        Instruction instr = getInstructionAt(currentAddress);
        if (instr == null) {
            printerr("No instruction at current address");
            return;
        }

        // Find a 16-bit value in the operands that could be an address
        Long offset16 = null;
        int operandIndex = -1;

        for (int i = 0; i < instr.getNumOperands(); i++) {
            int opType = instr.getOperandType(i);

            // Check for scalar (immediate) values
            if ((opType & OperandType.SCALAR) != 0) {
                Scalar scalar = instr.getScalar(i);
                if (scalar != null) {
                    long val = scalar.getUnsignedValue();
                    // Likely a 16-bit address if in valid range
                    if (val >= 0 && val <= 0xFFFF) {
                        offset16 = val;
                        operandIndex = i;
                        break;
                    }
                }
            }

            // Check for address operands
            if ((opType & OperandType.ADDRESS) != 0) {
                Address opAddr = instr.getAddress(i);
                if (opAddr != null) {
                    long val = opAddr.getOffset();
                    if (val >= 0 && val <= 0xFFFF) {
                        offset16 = val;
                        operandIndex = i;
                        break;
                    }
                }
            }

            // Check for DYNAMIC operands (e.g., [r6+#0xd31a])
            if ((opType & OperandType.DYNAMIC) != 0) {
                java.util.List<Object> reps = instr.getDefaultOperandRepresentationList(i);
                if (reps != null) {
                    for (Object rep : reps) {
                        if (rep instanceof Scalar) {
                            long val = ((Scalar) rep).getUnsignedValue();
                            if (val >= 0x100 && val <= 0xFFFF) {
                                offset16 = val;
                                operandIndex = i;
                                break;
                            }
                        }
                    }
                }
                if (offset16 != null) {
                    break;
                }
            }
        }

        if (offset16 == null) {
            // Ask user for the offset manually
            String input = askString("Enter 16-bit Offset",
                "Enter the 16-bit offset (hex, e.g. 0x8000):");
            if (input == null || input.isEmpty()) {
                return;
            }
            try {
                offset16 = Long.decode(input);
            } catch (NumberFormatException e) {
                printerr("Invalid number format: " + input);
                return;
            }
        }

        // Calculate effective address using DPP
        Address effectiveAddr = resolveWithDPP(instr.getAddress(), offset16);
        if (effectiveAddr == null) {
            printerr("Could not resolve DPP for offset 0x" + Long.toHexString(offset16));
            return;
        }

        // Determine reference type based on instruction
        RefType refType = guessRefType(instr);

        // Create the reference
        if (operandIndex >= 0) {
            instr.addOperandReference(operandIndex, effectiveAddr, refType, SourceType.USER_DEFINED);
        } else {
            instr.addMnemonicReference(effectiveAddr, refType, SourceType.USER_DEFINED);
        }

        println("Created " + refType.getName() + " reference to " + effectiveAddr +
                " (from 0x" + Long.toHexString(offset16) + ")");
    }

    private Address resolveWithDPP(Address context, long offset16) throws Exception {
        // Check for EXTS override first (segment-based)
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

        // Check for EXTP override (page-based)
        Register extpEn = currentProgram.getRegister("ExtpEn");
        Register extp = currentProgram.getRegister("Extp");
        if (extpEn != null && extp != null) {
            BigInteger enVal = currentProgram.getProgramContext().getValue(extpEn, context, false);
            if (enVal != null && enVal.equals(BigInteger.ONE)) {
                BigInteger pageVal = currentProgram.getProgramContext().getValue(extp, context, false);
                if (pageVal != null) {
                    long page = pageVal.longValue() & 0x3FF;
                    long innerOffset = offset16 & 0x3FFF;
                    long resolved = (page << 14) | innerOffset;
                    return getAddressInRam(resolved);
                }
            }
        }

        // Standard DPP translation
        int dppIndex = (int) ((offset16 >> 14) & 0x3);
        Register dppReg = currentProgram.getRegister("DPP" + dppIndex);
        if (dppReg == null) {
            printerr("DPP" + dppIndex + " register not found");
            return null;
        }

        BigInteger dppVal = currentProgram.getProgramContext().getValue(dppReg, context, false);
        if (dppVal == null) {
            // Ask user for DPP value
            String input = askString("DPP" + dppIndex + " Value Unknown",
                "Enter DPP" + dppIndex + " value (hex page number, e.g. 0x3):");
            if (input == null || input.isEmpty()) {
                return null;
            }
            try {
                long userDpp = Long.decode(input);
                long innerOffset = offset16 & 0x3FFF;
                long resolved = (userDpp << 14) | innerOffset;
                return getAddressInRam(resolved);
            } catch (NumberFormatException e) {
                printerr("Invalid number format: " + input);
                return null;
            }
        }

        long page = dppVal.longValue() & 0x3FF;
        long innerOffset = offset16 & 0x3FFF;
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

    private RefType guessRefType(Instruction instr) {
        String mnemonic = instr.getMnemonicString().toLowerCase();

        if (mnemonic.startsWith("call") || mnemonic.equals("calla") || mnemonic.equals("calli")) {
            return RefType.UNCONDITIONAL_CALL;
        }
        if (mnemonic.startsWith("jmp") || mnemonic.equals("jmpa") || mnemonic.equals("jmpi")) {
            return RefType.UNCONDITIONAL_JUMP;
        }
        if (mnemonic.startsWith("j")) {
            return RefType.CONDITIONAL_JUMP;
        }
        if (mnemonic.equals("mov") || mnemonic.equals("movb") || mnemonic.equals("movbs") ||
            mnemonic.equals("movbz")) {
            // Check if it's a load or store
            int firstOpType = instr.getOperandType(0);
            if ((firstOpType & OperandType.REGISTER) != 0) {
                return RefType.READ;
            }
            return RefType.WRITE;
        }

        // Default to data reference
        return RefType.DATA;
    }
}


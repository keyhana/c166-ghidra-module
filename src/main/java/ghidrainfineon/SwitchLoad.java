/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidrainfineon;

import java.math.BigInteger;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.app.plugin.processors.sleigh.symbol.Symbol;
import ghidra.app.plugin.processors.sleigh.symbol.UseropSymbol;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSpace;
import ghidra.program.model.lang.*;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.Program;
import ghidra.program.model.pcode.*;

/**
 * PCode injection for switch table loads.
 * 
 * Pattern: mov rX, [rX] followed by jmpi [rX]
 * 
 * When followed by jmpi, emits segment(dpp, ptr) for the load address so the
 * decompiler can properly resolve switch table entries.
 * 
 * When NOT followed by jmpi, falls back to a normal load.
 */
public class SwitchLoad extends InjectPayloadCallother {
	private final SleighLanguage language;
	private final long uniqueBase;
	private int segmentUseropIndex = -1;

	public SwitchLoad(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
		
		// Find the "segment" userop index
		Symbol sym = language.getSymbolTable().findGlobalSymbol("segment");
		if (sym instanceof UseropSymbol) {
			segmentUseropIndex = ((UseropSymbol) sym).getIndex();
		}
	}

	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		Address currentAddr = con.baseAddr;
		Varnode ptrInput = con.inputlist.get(0);  // The register (pointer to table entry)
		Varnode output = con.output.get(0);        // The loaded value
		
		AddressSpace ramSpace = language.getDefaultSpace();
		AddressSpace constSpace = language.getAddressFactory().getConstantSpace();
		AddressSpace uniqueSpace = language.getAddressFactory().getUniqueSpace();
		
		// Check if next instruction is jmpi
		boolean isSwitch = isFollowedByJmpi(program, currentAddr);
		
		if (isSwitch && segmentUseropIndex >= 0) {
			// This is a switch table load - emit segment(dpp, ptr) then load
			// Find the DPP value for the pointer
			Long dppValue = getDppForPointer(program, currentAddr, ptrInput);
			if (dppValue == null) {
				dppValue = 0L; // Default to DPP0
			}
			
			int seqnum = 0;
			PcodeOp[] ops = new PcodeOp[2];
			
			// 1. addr = segment(dpp, ptr)
			Varnode useropId = new Varnode(constSpace.getAddress(segmentUseropIndex), 4);
			Varnode dppConst = new Varnode(constSpace.getAddress(dppValue), 2);
			Varnode addrTemp = new Varnode(uniqueSpace.getAddress(uniqueBase), 3);
			
			Varnode[] segInputs = new Varnode[] { useropId, dppConst, ptrInput };
			ops[0] = new PcodeOp(currentAddr, seqnum++, PcodeOp.CALLOTHER, segInputs, addrTemp);
			
			// 2. output = *addr (load 2 bytes from the computed address)
			Varnode spaceId = new Varnode(constSpace.getAddress(ramSpace.getSpaceID()), 4);
			Varnode[] loadInputs = new Varnode[] { spaceId, addrTemp };
			ops[1] = new PcodeOp(currentAddr, seqnum++, PcodeOp.LOAD, loadInputs, output);
			
			return ops;
		} else {
			// Not a switch - do normal load: output = *ptr
			int seqnum = 0;
			PcodeOp[] ops = new PcodeOp[1];
			
			Varnode spaceId = new Varnode(constSpace.getAddress(ramSpace.getSpaceID()), 4);
			Varnode[] loadInputs = new Varnode[] { spaceId, ptrInput };
			ops[0] = new PcodeOp(currentAddr, seqnum++, PcodeOp.LOAD, loadInputs, output);
			
			return ops;
		}
	}
	
	/**
	 * Check if the next instruction is jmpi
	 */
	private boolean isFollowedByJmpi(Program program, Address currentAddr) {
		try {
			Instruction currentInstr = program.getListing().getInstructionAt(currentAddr);
			if (currentInstr == null) {
				return false;
			}
			
			Instruction nextInstr = currentInstr.getNext();
			if (nextInstr == null) {
				return false;
			}
			
			String mnemonic = nextInstr.getMnemonicString();
			return "jmpi".equalsIgnoreCase(mnemonic);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Get the DPP value for the given pointer register.
	 * Uses the upper 2 bits of a typical switch table address pattern.
	 */
	private Long getDppForPointer(Program program, Address context, Varnode ptrInput) {
		// For switch tables, we need to determine which DPP based on the pointer value
		// The pointer typically comes from: add rX, #tableBase
		// We look back to find the table base and determine DPP from that
		
		try {
			Instruction currentInstr = program.getListing().getInstructionAt(context);
			if (currentInstr == null) {
				return null;
			}
			
			// Look back for the ADD instruction that set up the table pointer
			Instruction prev = currentInstr.getPrevious();
			for (int i = 0; i < 10 && prev != null; i++) {
				String mnemonic = prev.getMnemonicString().toLowerCase();
				if (mnemonic.equals("add")) {
					// Try to get the immediate value (table base)
					for (int opIdx = 0; opIdx < prev.getNumOperands(); opIdx++) {
						if ((prev.getOperandType(opIdx) & OperandType.SCALAR) != 0) {
							ghidra.program.model.scalar.Scalar scalar = prev.getScalar(opIdx);
							if (scalar != null) {
								long tableBase = scalar.getUnsignedValue();
								int dppIndex = (int) ((tableBase >> 14) & 3);
								
								// Get the DPP register value
								Register dppReg = program.getRegister("DPP" + dppIndex);
								if (dppReg != null) {
									BigInteger dppValue = program.getProgramContext()
										.getValue(dppReg, context, false);
									if (dppValue != null) {
										return dppValue.longValue() & 0x3FFL;
									}
								}
								return (long) dppIndex; // Fallback to index as value
							}
						}
					}
				}
				prev = prev.getPrevious();
			}
		} catch (Exception e) {
			// Fall through to default
		}
		
		return null;
	}
}


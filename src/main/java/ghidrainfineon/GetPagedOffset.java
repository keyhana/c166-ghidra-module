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
import ghidra.program.model.lang.*;
import ghidra.program.model.listing.Program;
import ghidra.program.model.pcode.*;
import ghidra.program.model.address.Address;

public class GetPagedOffset extends InjectPayloadCallother {
	private final SleighLanguage language;
	private final long uniqueBase;

	public GetPagedOffset(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
	}

	private long getEffectiveAddress(Program program, Address baseAddress, long offset) {
		int dppNum = Math.toIntExact((offset & 0xC000) >> 14);
		long value = dppNum;
		BigInteger PageRegister = program.getProgramContext().getValue(program.getRegister("DPP" + dppNum), baseAddress, false);
		BigInteger ExtpEnRegister = program.getProgramContext().getValue(program.getRegister("ExtpEn"), baseAddress, false);
		BigInteger ExtsEnRegister = program.getProgramContext().getValue(program.getRegister("ExtsEn"), baseAddress, false);

		if (ExtpEnRegister != null && ExtpEnRegister.equals(BigInteger.ONE)) {
			BigInteger bigInteger = program.getProgramContext().getValue(program.getRegister("Extp"), baseAddress, false);
			if (bigInteger != null) {
				value = bigInteger.longValue() << 14;
                offset = offset & 0x3FFF;
			}
		} else if (ExtsEnRegister != null && ExtsEnRegister.equals(BigInteger.ONE)) {
			BigInteger bigInteger = program.getProgramContext().getValue(program.getRegister("Exts"), baseAddress, false);
			if (bigInteger != null) {
				value = bigInteger.longValue() << 16;
			}
		} else if (PageRegister != null) {
			value = PageRegister.longValue() << 14;
            offset = offset & 0x3FFF;
		}

		return value + offset;
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);

		long effectiveAddress = this.getEffectiveAddress(program, con.baseAddr, con.inputlist.getFirst().getOffset());

		emitter.emitAssignConstantToVarnode(con.output.getFirst(), effectiveAddress);
		return emitter.getPcodeOps();
	}
}

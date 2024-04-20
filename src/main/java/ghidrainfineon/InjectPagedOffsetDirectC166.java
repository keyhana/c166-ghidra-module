package ghidrainfineon;

import java.math.BigInteger;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;
import ghidra.program.model.listing.Program;
import ghidra.program.model.pcode.*;

public class InjectPagedOffsetDirectC166 extends InjectPayloadCallother {
	private SleighLanguage language;
	private long uniqueBase;

	public InjectPagedOffsetDirectC166(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		Integer memOffset = (int) con.inputlist.get(0).getOffset();
		Integer dppNum = (memOffset & 0xC000) >> 14;
		Register reg = program.getRegister("DPP" + dppNum);
		BigInteger value = program.getProgramContext().getValue(reg, con.baseAddr, false);
		int dppValue = memOffset;
		if (value != null) {
			dppValue = (value.intValue() << 14) + (memOffset & 0x3FFF);
		}
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);
		emitter.emitAssignConstantToVarnode(con.output.get(0), dppValue);
		return emitter.getPcodeOps();
	}

}

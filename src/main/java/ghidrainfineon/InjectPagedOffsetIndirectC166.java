package ghidrainfineon;

import java.math.BigInteger;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;
import ghidra.program.model.mem.MemoryAccessException;
import ghidra.program.model.listing.Program;
import ghidra.program.model.pcode.*;

public class InjectPagedOffsetIndirectC166 extends InjectPayloadCallother {
	private SleighLanguage language;
	private long uniqueBase;

	public InjectPagedOffsetIndirectC166(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) throws MemoryAccessException {
		Register ptrReg = program.getRegister(con.inputlist.get(0).getAddress());
		BigInteger ptrVal = program.getProgramContext().getValue(ptrReg, con.baseAddr, false);
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);
		if (ptrVal == null) {
			emitter.emitCopyVarnode(con.output.get(0), con.inputlist.get(0));
			return emitter.getPcodeOps();
		}
		
		Integer memOffset = ptrVal.intValue();
		Integer dppNum = (memOffset & 0xC000) >> 14;
		Register reg = program.getRegister("DPP" + dppNum);
		BigInteger value = program.getProgramContext().getValue(reg, con.baseAddr, false);
		int dppValue = memOffset;
		if (value != null) {
			dppValue = (value.intValue() << 14) + (memOffset & 0x3FFF);
		}
		emitter.emitAssignConstantToVarnode(con.output.get(0), dppValue);
		return emitter.getPcodeOps();
	}

}

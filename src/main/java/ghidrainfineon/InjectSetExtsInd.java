package ghidrainfineon;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;
import ghidra.program.model.mem.MemoryAccessException;
import ghidra.program.model.listing.*;
import ghidra.program.model.pcode.*;

public class InjectSetExtsInd extends InjectPayloadCallother {
	private SleighLanguage language;
	private long uniqueBase;
	
	public InjectSetExtsInd(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) throws MemoryAccessException {
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);
		emitter.emitAssignRegisterToRegister(con.inputlist.get(0), "Exts");
		return emitter.getPcodeOps();
	}
}

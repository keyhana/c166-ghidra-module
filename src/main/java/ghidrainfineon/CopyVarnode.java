package ghidrainfineon;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;
import ghidra.program.model.listing.*;
import ghidra.program.model.pcode.*;

public class CopyVarnode extends InjectPayloadCallother {
	private final SleighLanguage language;
	private final long uniqueBase;
	
	public CopyVarnode(String name, SleighLanguage language, long uniqueBase) {
		super(name);
		this.language = language;
		this.uniqueBase = uniqueBase;
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);
		emitter.emitCopyVarnode(con.output.getFirst(), con.inputlist.getFirst());
		return emitter.getPcodeOps();
	}
}

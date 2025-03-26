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
				value = bigInteger.longValue();
			}
		} else if (ExtsEnRegister != null && ExtsEnRegister.equals(BigInteger.ONE)) {
			BigInteger bigInteger = program.getProgramContext().getValue(program.getRegister("Exts"), baseAddress, false);
			if (bigInteger != null) {
				value = bigInteger.longValue();
			}
		} else if (PageRegister != null) {
			value = PageRegister.longValue();
		}

		return (value << 14) + (offset & 0x3FFF);
	}
	
	@Override
	public PcodeOp[] getPcode(Program program, InjectContext con) {
		PcodeOpEmitter emitter = new PcodeOpEmitter(this.language, con.baseAddr, this.uniqueBase);
		boolean constantAvailable = con.inputlist.getFirst().isConstant();

		if (!constantAvailable) {
			emitter.emitCopyVarnode(con.output.getFirst(), con.inputlist.getFirst());
			return emitter.getPcodeOps();
		}

		long effectiveAddress = this.getEffectiveAddress(program, con.baseAddr, con.inputlist.getFirst().getOffset());

		emitter.emitAssignConstantToVarnode(con.output.getFirst(), effectiveAddress);
		return emitter.getPcodeOps();
	}
}

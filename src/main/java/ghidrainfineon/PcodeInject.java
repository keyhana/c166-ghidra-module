package ghidrainfineon;

import java.util.Map;
import java.util.HashMap;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;

public class PcodeInject extends PcodeInjectLibrary {
	private Map<String, InjectPayloadCallother> implementedOps;

	public PcodeInject(SleighLanguage l) {
		super(l);
		implementedOps = new HashMap<>();
		implementedOps.put("GetPagedOffset", new InjectPagedOffsetDirectC166("offsetDirect", l, this.getUniqueBase()));
		uniqueBase += 32;
		implementedOps.put("GetPagedOffsetInd", new InjectPagedOffsetIndirectC166("offsetIndirect", l, this.getUniqueBase()));
		uniqueBase += 32;
		implementedOps.put("SetExtpIndCallOther", new InjectSetExtpInd("setExtpInd", l, this.getUniqueBase()));
		uniqueBase += 32;
		implementedOps.put("SetExtsIndCallOther", new InjectSetExtsInd("setExtsInd", l, this.getUniqueBase()));
	}
	
	@Override
	public InjectPayload allocateInject(String sourceName, String name, int tp) {
		if (tp == InjectPayload.CALLMECHANISM_TYPE) {
			return null;
		}
		if (tp == InjectPayload.CALLOTHERFIXUP_TYPE) {
			InjectPayload payload = implementedOps.get(name);
			if (payload != null) {
				return payload;
			}
		}
		return super.allocateInject(sourceName, name, tp);
	}

}

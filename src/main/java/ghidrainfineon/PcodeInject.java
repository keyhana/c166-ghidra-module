package ghidrainfineon;

import java.util.Map;
import java.util.HashMap;

import ghidra.app.plugin.processors.sleigh.SleighLanguage;
import ghidra.program.model.lang.*;

public class PcodeInject extends PcodeInjectLibrary {
	private final Map<String, InjectPayloadCallother> implementedOps;

	public PcodeInject(SleighLanguage l) {
		super(l);
		implementedOps = new HashMap<>();
		implementedOps.put("GetPagedOffset", new GetPagedOffset("getPagedOffset", l, this.getUniqueBase()));
		uniqueBase += 32;
		implementedOps.put("CopyVarnode", new CopyVarnode("copyVarnode", l, this.getUniqueBase()));
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

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
		uniqueBase += 0x100;
		implementedOps.put("c166_switch_load", new SwitchLoad("c166_switch_load", l, this.getUniqueBase()));
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

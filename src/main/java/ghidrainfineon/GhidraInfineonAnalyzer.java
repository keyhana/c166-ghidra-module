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

import ghidra.app.services.AbstractAnalyzer;
import ghidra.app.services.AnalyzerType;
import ghidra.app.services.AnalysisPriority;
import ghidra.program.model.lang.Language;
import ghidra.app.util.importer.MessageLog;
import ghidra.framework.options.Options;
import ghidra.program.model.address.AddressSetView;
import ghidra.program.model.listing.Program;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;

/**
 * Class to specify priority within the Automated Analysis pipeline.
 *
 */
public class GhidraInfineonAnalyzer extends AbstractAnalyzer {
	
	private static final String NAME = "C166 Analyzer";
	private static final String DESCRIPTION = "Analyze C166 processor";
	private static final String PROCESSOR_NAME = "c166";

	public GhidraInfineonAnalyzer() {
		super(NAME, DESCRIPTION, AnalyzerType.BYTE_ANALYZER); // assumes ELF Loader disassembled PLT section
		setDefaultEnablement(true);
		setPriority(AnalysisPriority.FORMAT_ANALYSIS);
	}

	@Override
	public boolean canAnalyze(Program program) {
		Language language = program.getLanguage();
		return PROCESSOR_NAME.equals(language.getProcessor().toString());
	}

	@Override
	public void registerOptions(Options options, Program program) {

		// TODO: If this analyzer has custom options, register them here

//		options.registerOption("Option name goes here", false, null,
//			"Option description goes here");
	}

	@Override
	public boolean added(Program program, AddressSetView set, TaskMonitor monitor, MessageLog log)
			throws CancelledException {
		return true;
	}
}

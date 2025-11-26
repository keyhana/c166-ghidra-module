package ghidrainfineon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ghidra.app.cmd.data.CreateDataCmd;
import ghidra.app.cmd.label.AddLabelCmd;
import ghidra.app.plugin.core.analysis.AutoAnalysisManager;
import ghidra.app.plugin.core.analysis.DecompilerSwitchAnalyzer;
import ghidra.app.services.AnalysisPriority;
import ghidra.app.util.importer.MessageLog;
import ghidra.program.model.address.*;
import ghidra.program.model.data.AbstractIntegerDataType;
import ghidra.program.model.data.ArrayDataType;
import ghidra.program.model.data.DataType;
import ghidra.program.model.data.DataTypeManager;
import ghidra.program.model.data.MutabilitySettingsDefinition;
import ghidra.program.model.lang.OperandType;
import ghidra.program.model.lang.Processor;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.Data;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.InstructionIterator;
import ghidra.program.model.listing.Listing;
import ghidra.program.model.listing.Program;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryAccessException;
import ghidra.program.model.scalar.Scalar;
import ghidra.program.model.symbol.FlowType;
import ghidra.program.model.symbol.Namespace;
import ghidra.program.model.symbol.RefType;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.ReferenceManager;
import ghidra.program.model.symbol.SourceType;
import ghidra.program.model.symbol.Symbol;
import ghidra.program.model.symbol.SymbolTable;
import ghidra.util.exception.DuplicateNameException;
import ghidra.util.exception.InvalidInputException;
import ghidra.util.Msg;
import ghidra.util.exception.CancelledException;
import ghidra.util.task.TaskMonitor;
import ghidra.docking.settings.SettingsDefinition;

/**
 * Recognises the canonical C166 jump-table sequence (normalized index,
 * pointer fetch, and {@code jmpi [rX]}) and lays down the computed jump
 * references directly from the table contents.
 */
public class C166SwitchAnalyzer extends DecompilerSwitchAnalyzer {

	private static final Set<String> SUPPORTED_PROCESSORS =
		Set.of("Infineon C167CR", "Infineon C167CS");

	private static final int MAX_BACKTRACK = 20;
	private static final int MAX_CASES = 64;
	private static final int TABLE_ENTRY_SIZE = 2;

	private final HashSet<Address> processed = new HashSet<>();

	public C166SwitchAnalyzer() {
		super();
		setDefaultEnablement(true);
		setPriority(AnalysisPriority.CODE_ANALYSIS);
	}

	@Override
	public boolean canAnalyze(Program program) {
		Processor processor = program.getLanguage().getProcessor();
		return processor != null && SUPPORTED_PROCESSORS.contains(processor.toString()) &&
			super.canAnalyze(program);
	}

	@Override
	public boolean added(Program program, AddressSetView set, TaskMonitor monitor, MessageLog log)
			throws CancelledException {

		// Note: We intentionally do NOT call super.added() here.
		// The parent DecompilerSwitchAnalyzer relies on decompiler heuristics that
		// don't work well for C166. If we let the parent run first, it may create
		// incomplete switch labels that cause our labelSwitchStructure() to skip
		// label creation (due to the "switchD" prefix check).
		// Our native pattern-based recovery handles C166 switches more accurately.
		runNativeSwitchRecovery(program, set, monitor);
		return true;
	}

	private void runNativeSwitchRecovery(Program program, AddressSetView set, TaskMonitor monitor)
			throws CancelledException {
		Listing listing = program.getListing();
		InstructionIterator iterator = listing.getInstructions(set, true);
		while (iterator.hasNext()) {
			monitor.checkCancelled();
			Instruction instruction = iterator.next();
			if (!isIndirectJump(instruction)) {
				continue;
			}
			if (processed.contains(instruction.getMinAddress())) {
				continue;
			}

			try {
				if (recoverSwitch(program, instruction, monitor)) {
					processed.add(instruction.getMinAddress());
				}
			}
			catch (MemoryAccessException e) {
				Msg.debug(this, "Failed to read switch table near " + instruction.getMinAddress(),
					e);
			}
		}
	}

	private boolean isIndirectJump(Instruction instruction) {
		FlowType flow = instruction.getFlowType();
		return flow.isJump() && flow.isComputed() &&
			"jmpi".equalsIgnoreCase(instruction.getMnemonicString());
	}

	private boolean recoverSwitch(Program program, Instruction jumpInstr, TaskMonitor monitor)
			throws MemoryAccessException, CancelledException {

		Register jumpRegister = getJumpRegister(jumpInstr);
		if (jumpRegister == null) {
			return false;
		}

		Instruction loadInstr = jumpInstr.getPrevious();
		if (loadInstr == null || !writesRegister(loadInstr, jumpRegister)) {
			return false;
		}

		Address tableBase = findTableBase(program, loadInstr, jumpRegister);
		if (tableBase == null) {
			return false;
		}

		int caseCount = findCaseCount(loadInstr);
		if (caseCount <= 0 || caseCount > MAX_CASES) {
			return false;
		}

		List<Address> caseTargets = readSwitchTargets(program, jumpInstr, tableBase, caseCount);
		if (caseTargets.isEmpty()) {
			return false;
		}

		applyComputedReferences(jumpInstr, caseTargets);
		disassembleTargets(program, caseTargets);
		labelSwitchStructure(program, jumpInstr, tableBase, caseTargets);
		return true;
	}

	private Register getJumpRegister(Instruction instruction) {
		int operandCount = instruction.getNumOperands();
		for (int opIndex = 0; opIndex < operandCount; opIndex++) {
			int type = instruction.getOperandType(opIndex);
			if ((type & OperandType.REGISTER) != 0) {
				Register register = instruction.getRegister(opIndex);
				Register parent = register != null ? register.getParentRegister() : null;
				if (parent != null && parent.getName().startsWith("r")) {
					return parent;
				}
				if (register != null && register.getName().startsWith("r")) {
					return register;
				}
			}

			if ((type & OperandType.DYNAMIC) != 0) {
				List<Object> repr = instruction.getDefaultOperandRepresentationList(opIndex);
				if (repr == null) {
					continue;
				}
				for (Object object : repr) {
					if (object instanceof Register register) {
						Register parent = register.getParentRegister();
						if (parent != null && parent.getName().startsWith("r")) {
							return parent;
						}
						if (register.getName().startsWith("r")) {
							return register;
						}
					}
				}
			}
		}

		return null;
	}

	private boolean writesRegister(Instruction instruction, Register register) {
		Register destination = instruction.getRegister(0);
		return destination != null && destination.equals(register);
	}

	private Address findTableBase(Program program, Instruction loadInstr, Register pointerRegister) {
		Instruction current = loadInstr.getPrevious();
		for (int i = 0; i < MAX_BACKTRACK && current != null; i++) {
			if (!writesRegister(current, pointerRegister) ||
				!"add".equalsIgnoreCase(current.getMnemonicString())) {
				current = current.getPrevious();
				continue;
			}

			for (int opIndex = 0; opIndex < current.getNumOperands(); opIndex++) {
				if ((current.getOperandType(opIndex) & OperandType.SCALAR) == 0) {
					continue;
				}
				Scalar scalar = current.getScalar(opIndex);
				if (scalar == null) {
					continue;
				}
				Address resolved = resolvePagedAddress(program, current, scalar.getUnsignedValue());
				if (resolved != null) {
					return resolved;
				}
			}
			current = current.getPrevious();
		}

		ReferenceManager refMgr = program.getReferenceManager();
		Reference[] refs = refMgr.getReferencesFrom(loadInstr.getMinAddress());
		for (Reference ref : refs) {
			if (ref.getReferenceType().isRead() && !ref.getToAddress().isExternalAddress()) {
				return ref.getToAddress();
			}
		}
		return null;
	}

	private Address resolvePagedAddress(Program program, Instruction instruction, long offset) {
		int dppIndex = (int) ((offset & 0xc000L) >> 14);
		long inner = offset & 0x3fffL;

		Register dppRegister = program.getRegister("DPP" + dppIndex);
		if (dppRegister == null) {
			return null;
		}

		BigInteger value = program.getProgramContext()
				.getValue(dppRegister, instruction.getMinAddress(), false);
		if (value == null) {
			return null;
		}

		long page = value.longValue() & 0xffffL;
		long effective = (page << 14) | inner;

		try {
			return program.getAddressFactory().getDefaultAddressSpace().getAddress(effective);
		}
		catch (AddressOutOfBoundsException e) {
			return null;
		}
	}

	private int findCaseCount(Instruction startInstr) {
		Instruction current = startInstr;
		for (int i = 0; i < MAX_BACKTRACK && current != null; i++) {
			if (!current.getMnemonicString().toLowerCase().startsWith("cmp")) {
				current = current.getPrevious();
				continue;
			}

			Scalar scalar = current.getScalar(1);
			if (scalar == null) {
				scalar = current.getScalar(0);
			}
			if (scalar == null) {
				current = current.getPrevious();
				continue;
			}

			long value = scalar.getUnsignedValue();
			if (value >= MAX_CASES) {
				return MAX_CASES;
			}
			return (int) value + 1;
		}
		return -1;
	}

	private List<Address> readSwitchTargets(Program program, Instruction jumpInstr, Address tableBase,
			int caseCount) throws MemoryAccessException {

		Memory memory = program.getMemory();
		Address instructionAddress = jumpInstr.getMinAddress();
		AddressSpace codeSpace = instructionAddress.getAddressSpace();
		long segmentBase = instructionAddress.getOffset() & 0xff0000L;

		List<Address> caseTargets = new ArrayList<>();

		for (int index = 0; index < caseCount; index++) {
			Address entryAddress;
			try {
				entryAddress = tableBase.add(index * TABLE_ENTRY_SIZE);
			}
			catch (AddressOutOfBoundsException e) {
				break;
			}

			short entryValue = memory.getShort(entryAddress);
			long offset = entryValue & 0xffffL;
			long targetOffset = segmentBase | offset;

			Address targetAddress;
			try {
				targetAddress = codeSpace.getAddress(targetOffset);
			}
			catch (AddressOutOfBoundsException e) {
				break;
			}

			if (program.getMemory().contains(targetAddress)) {
				caseTargets.add(targetAddress);
			}
		}

		return caseTargets;
	}

	private void applyComputedReferences(Instruction jumpInstr, List<Address> caseTargets) {
		removeExistingComputedRefs(jumpInstr);
		for (Address target : caseTargets) {
			jumpInstr.addMnemonicReference(target, RefType.COMPUTED_JUMP, SourceType.ANALYSIS);
		}
	}

	private void disassembleTargets(Program program, List<Address> caseTargets)
			throws CancelledException {
		if (caseTargets.isEmpty()) {
			return;
		}
		AddressSet disassemblyTargets = new AddressSet();
		for (Address target : caseTargets) {
			disassemblyTargets.add(target);
		}
		if (!disassemblyTargets.isEmpty()) {
			AutoAnalysisManager analysisMgr = AutoAnalysisManager.getAnalysisManager(program);
			analysisMgr.disassemble(disassemblyTargets);
		}
	}

	private void labelSwitchStructure(Program program, Instruction jumpInstr, Address tableBase,
			List<Address> caseTargets) {

		Address switchAddress = jumpInstr.getMinAddress();
		AddLabelCmd switchLabel = new AddLabelCmd(switchAddress, "switchD", SourceType.ANALYSIS);
		SymbolTable symbolTable = program.getSymbolTable();

		for (Symbol sym : symbolTable.getSymbols(switchAddress)) {
			if (sym.getName(false).startsWith(switchLabel.getLabelName())) {
				return;
			}
		}

		Namespace namespace = ensureSwitchNamespace(program, switchAddress);
		switchLabel.setNamespace(namespace);
		switchLabel.applyTo(program);

		labelCaseSymbols(program, caseTargets, namespace);
		labelSwitchTable(program, tableBase, caseTargets, namespace);
	}

	private Namespace ensureSwitchNamespace(Program program, Address switchAddress) {
		SymbolTable symbolTable = program.getSymbolTable();
		String switchName = "switchD_" + switchAddress;
		try {
			return symbolTable.createNameSpace(null, switchName, SourceType.ANALYSIS);
		}
		catch (DuplicateNameException e) {
			return symbolTable.getNamespace(switchName, null);
		}
		catch (InvalidInputException e) {
			Msg.debug(this, "Failed to create switch namespace for " + switchAddress, e);
			return null;
		}
	}

	private void labelCaseSymbols(Program program, List<Address> caseTargets, Namespace namespace) {
		SymbolTable symTable = program.getSymbolTable();
		for (int index = 0; index < caseTargets.size(); index++) {
			Address caseAddress = caseTargets.get(index);
			String caseName = "caseD_" + Integer.toHexString(index);
			AddLabelCmd caseLabel =
				new AddLabelCmd(caseAddress, caseName, namespace, SourceType.ANALYSIS);

			Symbol currentPrimary = symTable.getPrimarySymbol(caseAddress);
			if (currentPrimary != null && currentPrimary.getSource() == SourceType.ANALYSIS &&
				currentPrimary.getName().startsWith("Addr")) {
				currentPrimary.delete();
			}

			caseLabel.applyTo(program);
		}
	}

	private void labelSwitchTable(Program program, Address tableBase, List<Address> caseTargets,
			Namespace namespace) {

		DataTypeManager dataTypeManager = program.getDataTypeManager();
		DataType entryType = AbstractIntegerDataType.getUnsignedDataType(TABLE_ENTRY_SIZE,
			dataTypeManager);
		DataType fullType = caseTargets.size() > 1
				? new ArrayDataType(entryType, caseTargets.size(), TABLE_ENTRY_SIZE)
				: entryType;

		CreateDataCmd dataCmd = new CreateDataCmd(tableBase, true, fullType);
		if (dataCmd.applyTo(program)) {
			markDataAsConstant(program, tableBase);
		}

		ReferenceManager refMgr = program.getReferenceManager();
		for (int index = 0; index < caseTargets.size(); index++) {
			Address entryAddress;
			try {
				entryAddress = tableBase.add(index * TABLE_ENTRY_SIZE);
			}
			catch (AddressOutOfBoundsException e) {
				break;
			}

			refMgr.addMemoryReference(entryAddress, caseTargets.get(index), RefType.DATA,
				SourceType.ANALYSIS, 0);
		}

		if (namespace != null) {
			String dataName = "switchdataD_" + tableBase;
			AddLabelCmd dataLabel = new AddLabelCmd(tableBase, dataName, SourceType.ANALYSIS);
			dataLabel.setNamespace(namespace);
			dataLabel.applyTo(program);
		}
	}

	private void markDataAsConstant(Program program, Address addr) {
		Data data = program.getListing().getDataAt(addr);
		if (data == null) {
			return;
		}
		SettingsDefinition[] settings = data.getDataType().getSettingsDefinitions();
		for (SettingsDefinition definition : settings) {
			if (definition instanceof MutabilitySettingsDefinition mutability) {
				mutability.setChoice(data, MutabilitySettingsDefinition.CONSTANT);
			}
		}
	}

	private void removeExistingComputedRefs(Instruction instruction) {
		Reference[] refs = instruction.getReferencesFrom();
		for (Reference ref : refs) {
			if (ref.getReferenceType().isComputed()) {
				instruction.removeOperandReference(ref.getOperandIndex(), ref.getToAddress());
			}
		}
	}
}


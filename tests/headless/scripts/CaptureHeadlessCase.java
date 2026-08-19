// Captures deterministic whole-image disassembly and requested decompilations.
//@category C166

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import ghidra.app.decompiler.DecompInterface;
import ghidra.app.decompiler.DecompileResults;
import ghidra.app.util.headless.HeadlessScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressIterator;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.StackFrame;
import ghidra.program.model.listing.Variable;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.symbol.Reference;

public class CaptureHeadlessCase extends HeadlessScript {
    @Override
    protected void run() throws Exception {
        String[] args = getScriptArgs();
        if (args.length < 3 || args.length > 4) {
            throw new IllegalArgumentException(
                "usage: CaptureHeadlessCase.java <case.properties> <actual.disasm> " +
                "<decompiled.txt> [actual.stack]");
        }
        if (analysisTimeoutOccurred()) {
            throw new IllegalStateException("Headless analysis timed out");
        }

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) {
            properties.load(input);
        }
        Files.writeString(Path.of(args[1]), captureListing(), StandardCharsets.UTF_8);
        String decompiled = captureDecompilations(properties.getProperty("decompile", ""));
        Files.writeString(Path.of(args[2]), decompiled, StandardCharsets.UTF_8);
        if (args.length == 4) {
            String stack = captureStackFrames(properties.getProperty("stack", ""));
            Files.writeString(Path.of(args[3]), stack, StandardCharsets.UTF_8);
        }
        println(decompiled);
    }

    private String captureListing() throws Exception {
        StringBuilder output = new StringBuilder();
        Memory memory = currentProgram.getMemory();
        AddressIterator addresses = memory.getAllInitializedAddressSet().getAddresses(true);
        while (addresses.hasNext()) {
            Address address = addresses.next();
            Instruction instruction = getInstructionAt(address);
            if (instruction != null) {
                byte[] bytes = instruction.getBytes();
                output.append(formatAddress(address)).append('\t').append(hex(bytes)).append('\t')
                    .append(instruction);
                appendReferences(output, instruction);
                output.append('\n');
                for (int i = 1; i < bytes.length && addresses.hasNext(); i++) addresses.next();
            }
            else {
                output.append(formatAddress(address)).append('\t')
                    .append(String.format("%02X", memory.getByte(address) & 0xff))
                    .append("\t.byte 0x")
                    .append(String.format("%02X", memory.getByte(address) & 0xff)).append('\n');
            }
        }
        return output.toString();
    }

    private static void appendReferences(StringBuilder output, Instruction instruction) {
        Reference[] references = instruction.getReferencesFrom();
        if (references.length == 0) return;
        Arrays.sort(references, Comparator
            .comparingInt(Reference::getOperandIndex)
            .thenComparing(reference -> reference.getReferenceType().getName())
            .thenComparing(reference -> reference.getToAddress().toString()));
        output.append("\trefs=");
        for (int i = 0; i < references.length; i++) {
            if (i != 0) output.append(',');
            Reference reference = references[i];
            output.append(reference.getReferenceType().getName()).append('@')
                .append(formatAddress(reference.getToAddress()));
        }
    }

    private String captureDecompilations(String requested) throws Exception {
        if (requested.isBlank()) return "";
        StringBuilder output = new StringBuilder();
        DecompInterface decompiler = new DecompInterface();
        if (!decompiler.openProgram(currentProgram)) {
            throw new IllegalStateException("Cannot open decompiler: " + decompiler.getLastMessage());
        }
        try {
            for (String value : requested.split(",")) {
                Address address = toAddr(value.trim());
                Function function = getFunctionAt(address);
                if (function == null) throw new IllegalStateException("Missing function at " + address);
                DecompileResults result = decompiler.decompileFunction(function, 60, monitor);
                if (!result.decompileCompleted()) {
                    throw new IllegalStateException("Decompiler failed at " + address + ": " + result.getErrorMessage());
                }
                output.append("===== ").append(formatAddress(address)).append(" =====\n")
                    .append(result.getDecompiledFunction().getC()).append('\n');
            }
        }
        finally {
            decompiler.dispose();
        }
        return output.toString().replaceAll("(?m)[ \\t]+$", "").stripTrailing() + '\n';
    }

    private String captureStackFrames(String requested) throws Exception {
        StringBuilder output = new StringBuilder();
        for (String value : requested.split(",")) {
            Address address = toAddr(value.trim());
            Function function = getFunctionAt(address);
            if (function == null) throw new IllegalStateException("Missing function at " + address);
			StackFrame frame = function.getStackFrame();
			output.append(String.format("%s\tframe=%d\tlocals=%d\tparams=%d%n",
				address, frame.getFrameSize(), frame.getLocalSize(), frame.getParameterSize()));
			Variable[] variables = frame.getStackVariables();
            Arrays.sort(variables, Comparator
                .comparingInt(Variable::getStackOffset)
                .thenComparingInt(Variable::getLength));
            for (Variable variable : variables) {
                output.append(formatAddress(address))
                    .append("\toffset=").append(variable.getStackOffset())
                    .append("\tsize=").append(variable.getLength()).append('\n');
            }
        }
        return output.toString();
    }

    private static String formatAddress(Address address) {
        return String.format("%06X", address.getOffset());
    }

    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) result.append(String.format("%02X", value & 0xff));
        return result.toString();
    }
}

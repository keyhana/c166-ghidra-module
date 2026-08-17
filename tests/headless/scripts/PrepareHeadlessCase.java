// Seeds entry points declared by a headless regression case.
//@category C166

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Properties;

import ghidra.app.util.headless.HeadlessScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.lang.Register;

public class PrepareHeadlessCase extends HeadlessScript {
    @Override
    protected void run() throws Exception {
        String[] args = getScriptArgs();
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: PrepareHeadlessCase.java <case.properties>");
        }
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(args[0])) {
            properties.load(input);
        }
        seedRegisters(properties.getProperty("registers", ""));
        for (String value : required(properties, "entryPoints").split(",")) {
            Address address = toAddr(value.trim());
            if (!disassemble(address)) {
                throw new IllegalStateException("Cannot disassemble entry point " + address);
            }
            if (getFunctionAt(address) == null && createFunction(address, null) == null) {
                throw new IllegalStateException("Cannot create function at " + address);
            }
        }
    }

    private void seedRegisters(String requested) throws Exception {
        if (requested.isBlank()) return;
        Address start = currentProgram.getMemory().getMinAddress();
        Address end = currentProgram.getMemory().getMaxAddress();
        for (String assignment : requested.split(",")) {
            String[] parts = assignment.trim().split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid register assignment: " + assignment);
            }
            Register register = currentProgram.getRegister(parts[0].trim());
            if (register == null) {
                throw new IllegalArgumentException("Unknown register: " + parts[0].trim());
            }
            BigInteger value = new BigInteger(parts[1].trim().replaceFirst("^0[xX]", ""), 16);
            currentProgram.getProgramContext().setValue(register, start, end, value);
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing property: " + key);
        }
        return value;
    }
}

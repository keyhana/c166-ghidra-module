# C166 Ghidra Module

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A **Ghidra** extension for disassembling and decompiling **Infineon C166/C167** microcontroller binaries. Features advanced support for C166's segmented memory model including DPP (Data Page Pointer) address translation and switch table analysis.

## Features

### Core Functionality
- **Full Instruction Set** — All C166 instructions including extended, multiplication/division
- **DPP Address Translation** — Automatic resolution of 16-bit addresses to 24-bit physical addresses
- **EXTP/EXTS Support** — Extended page and segment override handling
- **Switch Table Analysis** — Automatic switch detection

### Included Scripts

| Script | Keybinding | Description |
|--------|------------|-------------|
| `AddISRLabels.java` | `Ctrl+Shift+I` | Label interrupt vector table and handlers |
| `CreateDPPReference.java` | `Ctrl+Shift+D` | Manually create DPP-resolved data references |
| `C166SwitchOverride.java` | `Ctrl+Shift+S` | Force switch table recognition in decompiler |

## C166 Memory Model

C166 uses a segmented memory architecture:

```
16-bit pointer → 24-bit physical address

Formula: physical = (DPP << 14) | (offset & 0x3FFF)

DPP0: 0x0000-0x3FFF
DPP1: 0x4000-0x7FFF
DPP2: 0x8000-0xBFFF
DPP3: 0xC000-0xFFFF
```

The module attempts to resolve these translations automatically, though manual intervention may be needed in some cases.

## Installation

1. **Download** the latest `.zip` from [Releases](https://github.com/keyhana/c166-ghidra-module/releases)
2. **Install** in Ghidra: `File` → `Install Extensions...` → `+` → Select `.zip`
3. **Restart** Ghidra

### Building from Source

```bash
# Set Ghidra installation path
export GHIDRA_INSTALL_DIR=/path/to/ghidra

# Build extension
./gradlew buildExtension

# Output: dist/ghidra_*_GhidraInfineon.zip
```

## Usage Tips

### Switch Tables Not Detected?
1. Place cursor on `jmpi` instruction
2. Press `Ctrl+Shift+S` to run switch override script
3. Script finds table offset and max case automatically

### Wrong Data References?
1. Place cursor on instruction with memory operand
2. Press `Ctrl+Shift+D` to create DPP-resolved reference
3. Script prompts for DPP value if unknown

### Setting DPP Values
For best results, set DPP register values in Ghidra:
1. Select address range
2. `Right-click` → `Set Register Values`
3. Set `DPP0`-`DPP3` to appropriate page values

## Calling Conventions

The module ships two calling conventions; pick per function via the
function signature editor (`F` in the listing) or set automatically by
the *Decompiler Parameter ID* analyzer.

| Name | Compiler | Word params | 32-bit / pointer pairs | Return word | Notes |
|------|----------|-------------|------------------------|-------------|-------|
| `__stdcall` (default) | Tasking c166 / generic | R12, R13, R14, R15 | R13/R12, R15/R14 | R4 (RL4 char) | Tasking convention; suitable for binaries built with Tasking c166 or unknown toolchains. |
| `__keil_c166` | Keil Cx66 / µVision | R8, R9, R10, R11, R12 | R9/R8, R11/R10 | R4 (RL4 char) | Keil convention; common in automotive ECUs and other Infineon C16x application code. Bit parameters (R15.0, R15.1, …) are not modeled. |

Both conventions return 32-bit values (long, far pointer) in the **R4/R5**
pair and assume **R0** as user stack pointer.

If the decompiler shows parameters on the stack and ignores values being
read from R8–R12 in a Keil-built binary, switch the function's calling
convention to `__keil_c166` — the decompiler will then recover the
parameters correctly.

## Supported Processors

- Infineon C167CR
- Infineon C167CS

## Known Limitations

- Manually overridden switches show case labels as addresses (e.g., `case 0x12345:`) instead of indices
- Nested switches may require manual script invocation for each `jmpi`

## Project Structure

```
GhidraInfineon/
├── data/languages/       # SLEIGH processor definitions
├── src/main/java/        # Analyzers and PCode injectors
├── ghidra_scripts/       # User scripts
└── agents.md             # Detailed technical documentation
```

See [`agents.md`](agents.md) for comprehensive technical documentation.

## License

MIT License — see [LICENSE](LICENSE) for details.

## Contributing

Contributions welcome! Fork, branch, and submit a PR.

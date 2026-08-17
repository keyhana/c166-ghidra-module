# Headless Regression Test Infrastructure

## Summary

Add `./gradlew headlessTest` command. Each case supplies raw binary, metadata, exact disassembly golden file. Headless Ghidra imports/analyzes binary, compares canonical whole-image listing, prints/saves requested decompilations. Same command runs in GitHub Actions with Ghidra 12.1.2.

## Implementation

- Stage current extension under `build/headless-test/`: compiled Java JAR, copied module data, freshly compiled `c166.sla`. Load stage through `ghidra.external.modules`; never modify `/opt/ghidra` or source tree.
- Define cases under `tests/headless/cases/<name>/`:
  - `input.bin`
  - `case.properties`: processor ID, compiler ID, load base, entry-point addresses, function addresses to decompile
  - `expected.disasm`
- Add preparation headless script:
  - Read case properties.
  - Seed disassembly/functions at declared entry points before analysis.
- Add capture headless script:
  - Traverse every initialized memory byte in address order.
  - Emit instruction records as `address<TAB>bytes<TAB>Instruction.toString()`.
  - Emit non-instruction bytes as deterministic `.byte` records, covering padding and tables.
  - Use UTF-8, LF endings, fixed-width 24-bit addresses, uppercase byte hex.
  - Decompile requested function entries; print output and save `decompiled.txt`.
  - Treat missing functions, decompiler failure, or analysis timeout as infrastructure failure.
- Runner invokes one fresh project per case using `BinaryLoader`, configured processor/compiler/base, bounded timeout, and one CPU. Compare actual versus expected byte-for-byte; show unified diff and return nonzero on mismatch.
- Keep decompiler content diagnostic-only in v1. Successful capture required; text features do not affect pass/fail yet.
- No golden-update command. Maintainer reviews actual output before manually replacing committed expectation.
- Fail when zero cases exist, preventing false-green CI.

## CI and Interfaces

- Public command: `./gradlew headlessTest`.
- Existing workflow keeps pinned Ghidra 12.1.2/JDK 21, then runs `headlessTest` before `buildExtension`.
- Upload `build/headless-test/results/` on success or failure, preserving actual disassembly, decompiled text, and logs.

## Test Plan

- Matching golden file passes.
- Changed mnemonic, operand, instruction length, undecoded byte, table byte, or missing instruction fails with readable diff.
- Both C167CR and C167CS processor IDs work through case metadata.
- Bad metadata, import failure, script exception, timeout, missing function, or decompiler failure returns nonzero.
- Requested decompilations appear in console and saved results.
- Temporary projects and staged extension stay inside ignored `build/` output.

## Assumptions

- User supplies at least one small binary case plus expected disassembly before CI can pass.
- Whole fixture is intentional regression surface, including non-code bytes.
- Raw fixtures are safe and redistributable; proprietary ECU images remain uncommitted.
- Ghidra 12.1.2 output defines v1 baseline; version upgrades require deliberate golden review.

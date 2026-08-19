# C166 Ghidra Module

Ghidra processor extension for Infineon C166/C167 devices. Core risk: translating
16-bit operands into correct 24-bit addresses without changing instruction or
decompiler semantics.

## Working Rules

1. Trace every caller and representation affected by a change: SLEIGH semantics,
   program context, analyzer references, injected p-code, and decompiler output.
2. Before changing instruction encoding, flags, addressing, or timing windows,
   read relevant instruction in `c166ism.md`. Treat manual as architectural source
   of truth; existing implementation may contain bug being fixed.
3. Reuse existing SLEIGH subtable, analyzer, injector, or script path before adding
   another translation layer.
4. Keep generated files out of source changes. Gradle compiles staged `c166.sla`
   for headless tests and removes source-tree copy before extension builds.
5. Leave smallest runnable regression that fails on original bug and checks
   externally visible result: disassembly, reference target, p-code, or decompilation.
6. Always checkout to main and fetch upstream changes and start from a new branch before starting to change code.

## Addressing Invariants

```text
DPP:  (DPP[offset >> 14] << 14) | (offset & 0x3FFF)
EXTP: (page << 14)              | (offset & 0x3FFF)
EXTS: (segment << 16)           | (offset & 0xFFFF)
```

- Resolution priority: EXTS, EXTP, DPP, then raw offset when context is unknown.
- EXTP uses low 14 offset bits; EXTS uses full 16-bit offset.
- EXTP/EXTS apply only for encoded instruction-count window.
- Read DPP and extension registers at instruction address context.
- Never derive missing DPP value from operand selector; that creates phantom refs.
- Listing references do not affect decompiler. Change decompiler behavior through
  p-code (`segment()`/injectors) or `JumpTable.writeOverride()`.

## Change Routing

| Change | Primary location |
|---|---|
| Instruction encoding, semantics, flags | `data/languages/c166.sinc` |
| Calling convention or callother mapping | `data/languages/c166.cspec` |
| Language/compiler registration | `data/languages/c166.ldefs` |
| SFR symbols or segment operation | `data/languages/*.pspec` |
| Static address references | `C166AddressAnalyzer.java`, `GetPagedOffset.java` |
| Register-offset p-code | `RegOffsetAddr.java` |
| Custom p-code operation | `PcodeInject.java` plus matching injector |
| Interactive repair workflow | `ghidra_scripts/` |
| Headless regression | `tests/headless/` and `build.gradle` |

Interactive scripts under `ghidra_scripts/` carry `@category C166`, `@keybinding`,
and `@menupath`. Headless test scripts need only `@category C166`.

## Verification

Set `GHIDRA_INSTALL_DIR` to installed Ghidra root, then run smallest relevant gate:

```bash
./gradlew headlessTest
./gradlew buildExtension
```

- SLEIGH or address-resolution changes: run both commands.
- Java-only analyzer/injector changes: run focused regression plus extension build.
- Script-only changes: exercise script headlessly when possible; otherwise report
  exact manual Ghidra workflow used.
- `extp-counter` intentionally exposes issue #28 until counter semantics are fixed;
  expected target is `0x0F029B`, not current `0x0F429B`.

Work is complete when relevant commands run, expected artifacts are inspected, and
any failure is identified as pre-existing, intentional regression, or introduced.

## Context Pointers

- Instruction semantics or encodings: read `c166ism.md` before editing.
- Installation, supported processors, scripts, and calling conventions: read
  `README.md`.
- Headless imports, projects, flags, or scripts: read
  `.agents/skills/ghidra-headless/SKILL.md` and its routed reference.
- Regression harness design and golden-file contract: read
  `plans/ghidra-headless-regression-tests.md`.

# CLAUDE.md

Ghidra processor extension for the **Infineon C166** 16-bit microcontroller family (C167CR, C167CS, and relatives). The core problem this module solves: C166 uses 16-bit pointers over a 24-bit address space, so addresses must be translated through DPP / EXTP / EXTS paging to land at the right physical location.

## ⚠️ Before changing any instruction semantics

**Consult `c166ism.md` first.** It is the full C166 Instruction Set Manual (the architectural source of truth): instruction syntax, operation semantics, condition-flag behavior (E, Z, V, C, N), addressing modes, and encodings. Verify behavior against it before editing `c166.sinc` or any flag/address logic — do not infer instruction behavior from existing code alone.

For deep architectural detail (switch handling, scripts, every edge case), see `agents.md`. This file is the essentials.

## Address translation (the heart of the module)

```
DPP:   physical = (DPP[offset>>14] << 14) | (offset & 0x3FFF)
EXTP:  physical = (page    << 14) | (offset & 0x3FFF)   # 14-bit offset
EXTS:  physical = (segment << 16) | (offset & 0xFFFF)   # full 16-bit offset
```

EXTP and EXTS temporarily override DPP for a short instruction window. **EXTS uses the full 16-bit offset; EXTP only the low 14 bits** — the most common source of off-by-page bugs. Resolution priority is EXTS → EXTP → DPP, with a raw-offset fallback when no context is known (never synthesize a page from `offset>>14` — that produces phantom xrefs near 0x0).

## Critical gotchas

- **The decompiler ignores disassembly references.** Creating a reference in the listing does *not* change decompiler output. To affect decompilation you must emit pcode (`segment()`) or use `JumpTable.writeOverride()`.
- **SLEIGH changes need a clean rebuild.** After editing `c166.sinc`, delete `data/languages/c166.sla` or the change won't take effect (symptoms: stale behavior, or `Unknown callother name` errors).
- **DPP values are context-sensitive.** Read them at the instruction's address context, not globally: `program.getProgramContext().getValue(dppReg, addr, false)`.
- **Prefer SLEIGH builtins for flags** (`carry`, `scarry`, `sborrow`) over hand-rolled bit math.

## Key files

| Task | Files |
|------|-------|
| Add / change an instruction | `data/languages/c166.sinc` (then delete `c166.sla`) |
| Add a pcode injector | `c166.sinc` + `c166.cspec` (`callotherfixup`) + `PcodeInject.java` + new `*.java` injector |
| Address-translation logic | `src/main/java/ghidrainfineon/C166AddressAnalyzer.java`, `RegOffsetAddr.java`, `GetPagedOffset.java` |
| Calling conventions / SFR symbols | `c166.cspec`, `c166cr.pspec`, `c167cs.pspec` |

## Build

```bash
./gradlew buildExtension          # output: dist/ghidra_<ver>_PUBLIC_<date>_*.zip
```

Delete `data/languages/c166.sla` first if you touched any SLEIGH file.

## Ghidra scripts

Scripts live in `ghidra_scripts/` and **must** carry `@category C166`, a `@keybinding`, and a `@menupath` annotation in their header comments.
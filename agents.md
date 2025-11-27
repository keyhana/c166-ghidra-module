# C166 Ghidra Module - Agent Documentation

> **Purpose**: This document provides comprehensive context for AI agents working on this Ghidra extension for the Infineon C166 microcontroller architecture.

---

## ⚠️ Important: Instruction Set Reference

**Before modifying, verifying, or implementing any C166 instruction**, consult:

```
c166ism.md
```

This file contains the complete **C166 Instruction Set Manual** (converted from PDF). It includes:
- Instruction syntax and operation semantics
- Condition flag behavior (E, Z, V, C, N) for each instruction
- Addressing modes and encoding formats
- Detailed descriptions and edge cases

**Always verify instruction behavior against this reference** before making changes to `c166.sinc`.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Directory Structure](#directory-structure)
3. [C166 Memory Model](#c166-memory-model)
4. [PCode Injection System](#pcode-injection-system)
5. [Switch Table Handling](#switch-table-handling)
6. [Analyzers](#analyzers)
7. [Ghidra Scripts](#ghidra-scripts)
8. [Edge Cases and Gotchas](#edge-cases-and-gotchas)
9. [Build and Development](#build-and-development)
10. [Known Limitations](#known-limitations)

---

## Architecture Overview

The C166 is a **16-bit microcontroller** with a **24-bit address space**. The key challenge is that C166 uses a **segmented memory model** where 16-bit pointers are translated to 24-bit physical addresses using **Data Page Pointers (DPP)** or **Extended Page/Segment (EXTP/EXTS)** registers.

### Key Architectural Features

| Feature | Description |
|---------|-------------|
| **Word Size** | 16-bit |
| **Address Space** | 24-bit (16MB) |
| **Pointer Size** | 16-bit (paged) or 24-bit (far) |
| **DPP Registers** | DPP0-DPP3 (10-bit page values) |
| **Paging Formula** | `physical = (DPP << 14) | (offset & 0x3FFF)` |

---

## Directory Structure

```
GhidraInfineon/
├── data/
│   └── languages/
│       ├── c166.slaspec       # Main SLEIGH entry point
│       ├── c166.sinc          # SLEIGH instruction definitions
│       ├── c166.cspec         # Compiler specification (calling conventions, callotherfixup)
│       ├── c166.ldefs         # Language definitions
│       ├── c166cr.pspec       # Processor spec for C167CR (includes segmentop)
│       ├── c167cs.pspec       # Processor spec for C167CS (includes segmentop)
│       └── c166.sla           # Compiled SLEIGH (auto-generated, delete to rebuild)
├── src/main/java/ghidrainfineon/
│   ├── C166AddressAnalyzer.java   # DPP-aware address resolution analyzer
│   ├── PcodeInject.java           # PCode injection library registration
│   ├── GetPagedOffset.java        # Injector for static address paging
│   ├── SwitchLoad.java            # Injector for switch table loads
│   └── PcodeOpEmitter.java        # Helper for emitting PCode operations
├── ghidra_scripts/
│   ├── CreateDPPReference.java    # Manual DPP reference creation script
│   └── C166SwitchOverride.java    # Manual switch table override script
├── build.gradle                   # Gradle build configuration
└── extension.properties           # Ghidra extension metadata
```

### File Purposes

| File | Purpose |
|------|---------|
| **c166.sinc** | Defines all C166 instructions, registers, and pcodeops. This is where `GetPagedOffset`, `segment`, and `c166_switch_load` are defined. |
| **c166.cspec** | Defines `callotherfixup` entries that map pcodeops to Java injectors. The `dynamic="true"` attribute enables runtime PCode injection. |
| **c166cr.pspec / c167cs.pspec** | Defines `segmentop` which tells the decompiler how to interpret `segment()` PCode for address calculation. |
| **PcodeInject.java** | Registers custom PCode injectors (`GetPagedOffset`, `SwitchLoad`) with Ghidra's language system. |

---

## C166 Memory Model

### DPP (Data Page Pointer) Translation

C166 uses a 4-page model for data access:

```
16-bit offset: [15:14] = DPP selector, [13:0] = page offset

DPP0: offsets 0x0000-0x3FFF
DPP1: offsets 0x4000-0x7FFF  
DPP2: offsets 0x8000-0xBFFF
DPP3: offsets 0xC000-0xFFFF

Physical address = (DPP[selector] << 14) | (offset & 0x3FFF)
```

### EXTP/EXTS Override

The `EXTP` and `EXTS` instructions temporarily override DPP translation:

| Instruction | Behavior | Formula |
|-------------|----------|---------|
| **EXTP** | Page-based override | `(Extp << 14) | (offset & 0x3FFF)` |
| **EXTS** | Segment-based override | `(Exts << 16) | (offset & 0xFFFF)` |

**Critical**: EXTS uses the **full 16-bit offset**, while EXTP uses only the **lower 14 bits**.

### Code Addressing

Code addresses use a different model - `jmpi` uses the current code segment:

```
jmpi [rX]  →  target = (current_IP & 0xFF0000) | rX_value
```

---

## PCode Injection System

### How It Works

1. **SLEIGH** (`c166.sinc`) defines `pcodeop` placeholders:
   ```sleigh
   define pcodeop GetPagedOffset;
   define pcodeop segment;
   define pcodeop c166_switch_load;
   ```

2. **Compiler Spec** (`c166.cspec`) maps pcodeops to injectors:
   ```xml
   <callotherfixup targetop="c166_switch_load">
       <pcode dynamic="true">
           <input name="ptr" size="2"/>
           <output name="val" size="2"/>
       </pcode>
   </callotherfixup>
   ```

3. **Processor Spec** (`c166cr.pspec`) registers the inject library:
   ```xml
   <property key="pcodeInjectLibraryClass" value="ghidrainfineon.PcodeInject"/>
   ```

4. **Java** (`PcodeInject.java`) provides runtime injection:
   ```java
   implementedOps.put("c166_switch_load", new SwitchLoad(...));
   ```

### The `segment` PCode Operation

Ghidra has built-in support for `segment()` - the decompiler recognizes it for switch analysis.

**Defined in `.pspec`:**
```xml
<segmentop space="ram" userop="segment" farpointer="no">
    <pcode>
        <input name="base" size="2"/>
        <input name="inner" size="2"/>
        <output name="res" size="3"/>
        <body><![CDATA[
            res = (zext(base) << 14) | zext(inner & 0x3FFF);
        ]]></body>
    </pcode>
</segmentop>
```

**Used by SwitchLoad.java:**
```java
// Emit: addr = segment(dppValue, ptrRegister)
Varnode useropId = new Varnode(constSpace.getAddress(segmentUseropIndex), 4);
ops[0] = new PcodeOp(addr, seqnum++, PcodeOp.CALLOTHER, 
    new Varnode[] { useropId, dppConst, ptrInput }, addrTemp);
```

---

## Switch Table Handling

### C166 Switch Pattern

```asm
cmp  rX, #maxCase       ; Compare index with max case
jmpr cc_ugt, default    ; Jump to default if > maxCase
shl  rX, #1             ; Multiply by 2 (table entry size)
add  rX, #tableAddr     ; Add table base address
mov  rX, [rX]           ; Load target from table ← c166_switch_load
jmpi [rX]               ; Jump to target
```

### Why Switch Handling Is Complex

1. **Disassembler vs Decompiler**: Creating references in the disassembler does NOT affect decompiler output.

2. **Decompiler needs `segment()`**: The decompiler only understands switch tables when it sees the `segment()` PCode operation.

3. **Pattern Detection**: `SwitchLoad.java` checks if `mov rX, [rX]` is followed by `jmpi` - if so, it emits `segment()` PCode.

### Two-Level Switch Handling

| Level | Component | Purpose |
|-------|-----------|---------|
| **Disassembly** | `C166SwitchOverride.java` script | Creates references, labels, and `JumpTable.writeOverride()` |
| **Decompilation** | `SwitchLoad.java` injector | Emits `segment()` PCode so decompiler understands the table |

### The `JumpTable.writeOverride()` API

```java
JumpTable jumpTable = new JumpTable(switchAddr, targets, true);
jumpTable.writeOverride(func);
```

This tells the decompiler: "At this address, there's a switch with these targets."

**Limitation**: Case labels show as addresses (e.g., `case 0xd08c6:`) instead of indices (`case 0:`). This is a Ghidra limitation - the override doesn't provide index-to-target mapping.

---

## Analyzers

### C166AddressAnalyzer

**File**: `src/main/java/ghidrainfineon/C166AddressAnalyzer.java`

**Purpose**: Post-processes data references to apply DPP/EXTP/EXTS translation.

**Key Features**:
- Extends `ConstantPropagationAnalyzer` with C166 context evaluation
- Checks `ExtsEn`, `ExtpEn` flags before DPP translation
- Handles both EXTS (segment-based) and EXTP (page-based) overrides differently
- Removes stale 16-bit references, creates correct 24-bit references

**Translation Priority**:
1. EXTS override (uses full 16-bit offset)
2. EXTP override (uses 14-bit offset)
3. Standard DPP translation

---

## Ghidra Scripts

### CreateDPPReference.java

**Keybinding**: `Ctrl+Shift+D`

**Purpose**: Manually create a DPP-resolved reference when automatic analysis fails.

**Usage**:
1. Place cursor on instruction with memory operand
2. Press `Ctrl+Shift+D`
3. Script extracts 16-bit offset, resolves DPP, creates reference

**Handles**:
- Scalar operands (`mov r0, #0x8000`)
- Address operands
- Dynamic operands (`[r6+#0xd31a]`)
- Manual input fallback

### C166SwitchOverride.java

**Keybinding**: `Ctrl+Shift+S`

**Purpose**: Force decompiler switch recognition when automatic analysis fails.

**Algorithm**:
1. Verify cursor is on `jmpi` instruction
2. Scan backwards to find `add` (table offset)
3. Scan backwards to find `cmp` (max case count)
4. Resolve table address using DPP
5. Read switch targets from table
6. Create references and call `JumpTable.writeOverride()`

**Does NOT use rigid patterns** - dynamically searches for relevant instructions within configurable backtrack distance.

---

## Edge Cases and Gotchas

### 1. SLEIGH Compilation Cache

**Problem**: Changes to `.sinc` files may not take effect.

**Solution**: Delete `c166.sla` and rebuild:
```bash
del data\languages\c166.sla
.\gradlew.bat buildExtension
```

### 2. Unique Space Collisions

**Problem**: PCode injectors that create temporary varnodes can have address collisions if multiple instructions use the same temp address.

**Solution in SwitchLoad.java**:
```java
// Use instruction address to create unique temp addresses
long uniqueOffset = uniqueBase + ((currentAddr.getOffset() & 0xFFFFFF) >> 1) * 4;
Varnode addrTemp = new Varnode(uniqueSpace.getAddress(uniqueOffset), 3);
```

### 3. EXTS vs EXTP Semantics

**EXTP** (Page):
```java
resolved = (page << 14) | (offset & 0x3FFF);  // Only 14 bits of offset
```

**EXTS** (Segment):
```java
resolved = (segment << 16) | (offset & 0xFFFF);  // Full 16 bits of offset
```

### 4. Decompiler Doesn't Use Disassembly References

Creating references in the listing window does NOT affect decompiler output. The decompiler re-analyzes PCode from scratch. To affect decompilation:
- Use PCode injection (`segment()`)
- Use `JumpTable.writeOverride()`

### 5. Context-Sensitive DPP Values

DPP register values can change throughout a function. Always use the correct address context:
```java
BigInteger dppValue = program.getProgramContext().getValue(dppReg, context, false);
```

### 6. `callotherfixup` Unknown Name Error

**Symptom**: `Unknown callother name in <callotherfixup>: c166_switch_load`

**Cause**: The pcodeop is defined in `.sinc` but `.sla` wasn't rebuilt.

**Solution**: Delete `.sla` file and rebuild.

### 7. SLEIGH Duplicate Symbol Error

**Symptom**: `c166.sinc:XXX: Duplicate symbol name: rwn`

**Cause**: Using same operand name twice in a pattern constraint.

**Solution**: Use predefined subtables like `RWnRWmEqual` instead of inline constraints.

---

## Build and Development

### Build Command

```bash
.\gradlew.bat buildExtension
```

Output: `dist/ghidra_<version>_PUBLIC_<date>_GhidraInfineon.zip`

### Install in Ghidra

1. `File` → `Install Extensions...`
2. Click `+` and select the `.zip` file
3. Restart Ghidra

### Development Workflow

1. Make changes to Java/SLEIGH files
2. Delete `.sla` if SLEIGH changed
3. Run `gradlew buildExtension`
4. Reinstall extension in Ghidra
5. Reopen target binary (or clear analysis and re-analyze)

### Testing PCode Injection

In Ghidra's Listing window:
1. Select instruction
2. Right-click → `Instruction Info...`
3. Check "Pcode" tab

Or use the PCode display in the decompiler window.

---

## Known Limitations

### 1. Switch Case Numbering

The decompiler shows cases as addresses, not indices:
```c
switch(...) {
    case 0xd08c6:  // Instead of case 0:
    case 0xd08d0:  // Instead of case 1:
}
```

This is because `JumpTable.writeOverride()` doesn't provide index-to-target mapping.

### 2. Nested Switches

The automatic switch detection may miss nested switches. Use `C166SwitchOverride.java` script manually for each `jmpi`.

### 3. Non-Standard Switch Patterns

Switches compiled with unusual optimization may not match the expected pattern. The script uses heuristics (backtrack search for `add` and `cmp`) but may need manual adjustment.

### 4. Far Pointers

Far (24-bit) pointers are partially supported. The module defines 3-byte pointer size but some operations may not handle them correctly.

---

## Quick Reference

### PCode Operations

| PcodeOp | Defined In | Purpose |
|---------|------------|---------|
| `GetPagedOffset` | c166.sinc | Static address DPP translation |
| `segment` | c166.sinc, *.pspec | Dynamic address translation (decompiler-aware) |
| `c166_switch_load` | c166.sinc | Switch table load marker |

### Register Context Names

| Register | Purpose |
|----------|---------|
| `DPP0`-`DPP3` | Data Page Pointers |
| `Extp` | Extended Page value |
| `Exts` | Extended Segment value |
| `ExtpEn` | EXTP enable flag |
| `ExtsEn` | EXTS enable flag |

### Key Files to Modify

| Task | Files |
|------|-------|
| Add new instruction | `c166.sinc` |
| Add PCode injection | `c166.sinc`, `c166.cspec`, `PcodeInject.java`, new Java class |
| Change address translation | `C166AddressAnalyzer.java` |
| Add SFR symbol | `c166cr.pspec` or `c167cs.pspec` |

---

## References

### Local References (in this repo)
- **`c166ism.md`** — C166 Instruction Set Manual (markdown, ~5500 lines). **Primary reference for instruction semantics and flag behavior.**
- `data/manuals/Infineon-c166_ism_v2.0_2001_03.pdf` — Original PDF manual

### External References
- [Ghidra SLEIGH Documentation](https://ghidra.re/courses/languages/html/sleigh.html)
- [Ghidra PCode Reference](https://ghidra.re/courses/languages/html/pcoderef.html)
- [Ghidra Source - PcodeInjectLibrary](https://github.com/NationalSecurityAgency/ghidra/tree/master/Ghidra/Framework/SoftwareModeling/src/main/java/ghidra/program/model/lang)


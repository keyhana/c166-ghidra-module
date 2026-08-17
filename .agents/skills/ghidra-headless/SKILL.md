---
name: ghidra-headless
description: Run Ghidra Headless Analyzer for importing, analyzing, or scripting C166/C167 binaries in this repository. Use for ghidra-analyzeHeadless, batch imports, headless scripts, C166 language selection, or existing Ghidra projects.
---

# Ghidra Headless Analyzer

Build smallest safe `ghidra-analyzeHeadless` command for requested operation.

## Workflow

1. Confirm binary with `command -v ghidra-analyzeHeadless`.
2. Choose exactly one mode: `-import <path>` for new binaries; `-process [pattern]` for project programs. Omit both only for program-independent scripts.
3. Choose local `<project-dir> <project-name>[/folder]` or existing `ghidra://...` repository.
4. For raw firmware, select repository definitions explicitly:
   - C167CR: `-loader BinaryLoader -processor C166:LE:16:default -cspec tasking`
   - C167CS: `-loader BinaryLoader -processor C166:CS:LE:16:default -cspec tasking`
   - Add `-loader-baseAddr <hex-address>` when known.
5. Add only requested scripts, recursion, timeout, logs, read-only behavior, overwrite, or CPU limit.
6. Quote paths containing spaces. Single-quote `-process` wildcards so shell does not expand them.
7. Show complete command before execution and state what it may change.
8. Check exit status and logs. Report processed files, warnings, timeouts, and saved/discarded changes.

## Safety

- Prefer `-readOnly` when inspecting existing projects.
- Never add `-overwrite`, `-deleteProject`, `-commit`, `-okToDelete`, or deletion disposition implicitly.
- Before overwrite, identify files at risk. Before commit, confirm repository and comment.
- Before `-okToDelete`, warn that process-mode deletion is permanent and can remove every version.
- Never put passwords or private-key passphrases in command arguments.

## Patterns

```bash
ghidra-analyzeHeadless <project-dir> <project-name> \
  -import <firmware.bin> \
  -loader BinaryLoader -loader-baseAddr <hex-address> \
  -processor C166:LE:16:default -cspec tasking
```

```bash
ghidra-analyzeHeadless <project-dir> <project-name> \
  -import <firmware.bin> \
  -scriptPath <repo>/ghidra_scripts \
  -preScript <ScriptName.java> [args...] \
  -loader BinaryLoader -processor C166:LE:16:default -cspec tasking
```

```bash
ghidra-analyzeHeadless <project-dir> <project-name> \
  -process '<pattern>' -recursive -readOnly \
  -postScript <ScriptName.java> [args...]
```

## Scripts

- Give script filename with extension, not path; use `-scriptPath` for directories.
- Repeat `-preScript` or `-postScript`; execution follows command order.
- Arguments immediately after script name appear in `getScriptArgs()`.
- Use matching `.properties` for `askXxx()` only when direct arguments are unsuitable; add `-propertiesPath` when stored elsewhere.
- Scripts without import/process must be program-independent.
- Use `HeadlessScript` only for continuation, shared values, import-directory, or analysis controls.
- Analysis toggles belong in pre-scripts; timeout checks belong in post-scripts.

Supplied documentation has no Ghidra version. Before uncommon flags or APIs, check `ghidra-analyzeHeadless -help` or installed Java API. Read `data/languages/c166.ldefs` for authoritative language/compiler IDs.

Read [references/headless-analyzer.md](references/headless-analyzer.md) for detailed semantics.

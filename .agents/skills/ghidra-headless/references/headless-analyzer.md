# Headless Analyzer Reference

Condensed from user-supplied Ghidra README; source version unspecified.

## Options

`-import` and `-process` are mutually exclusive. Multiple imports are allowed; process appears once.

| Option | Effect |
|---|---|
| `-recursive` | Descend imported directories or project subfolders. |
| `-readOnly` | Discard changes; ignore overwrite. |
| `-deleteProject` | Delete only project created during current import. |
| `-noanalysis` | Disable default auto-analysis. |
| `-analysisTimeoutPerFile N` | Interrupt analysis after N seconds, then run post-scripts. |
| `-overwrite` | Replace conflicting imports, subject to permissions/version state. |
| `-scriptlog PATH` | Capture built-in script print output. |
| `-log PATH` | Capture analyzer/non-script logs. |
| `-max-cpu N` | Limit cores; non-positive means one. |

Project cannot normally be processed while open in Ghidra. Bulk import ignores dotfiles unless explicitly named.

`BinaryLoader` supports block name, base address, file offset, length, and label options. Verify exact installed syntax with `ghidra-analyzeHeadless -help`.

## Wildcards

- Import wildcards are shell-expanded and OS-dependent.
- Process wildcards are Ghidra-expanded, accept only `*` and `?`, and match files.
- Quote process patterns: `-process 'a*'`.
- Wildcards cannot select project/repository location or folder.

## Scripts

Direct arguments use `String[] args = getScriptArgs();`. Quote arguments containing spaces.

For `askXxx()`, create `<ScriptName>.properties`; key joins prompt parameters with spaces and omits default value. Store beside script or use `-propertiesPath`. Direct arguments take precedence and are consumed in ask-call order.

Useful `HeadlessScript` methods:

```java
enableHeadlessAnalysis(true);
boolean enabled = isHeadlessAnalysisEnabled();
setHeadlessImportDirectory("path/to/folder");
boolean didTimeout = analysisTimeoutOccurred();
storeHeadlessValue(String key, Object value);
Object value = getStoredHeadlessValue(String key);
```

Analysis state and import directory persist during session. Set analysis in pre-scripts; check timeout in post-scripts.

## Disposition

| Option | Import | Process |
|---|---|---|
| `CONTINUE` | Continue; import. | Continue; save. |
| `ABORT` | Stop later stages; import. | Stop later stages; save. |
| `CONTINUE_THEN_DELETE` | Continue; do not import. | Continue; delete afterward. |
| `ABORT_AND_DELETE` | Stop; do not import. | Stop; delete. |

Use `setHeadlessContinuationOption(...)`; inspect with `getHeadlessContinuationOption()`. Effect begins after current outermost script. Process deletion requires `-okToDelete`, cannot run read-only, is permanent, and deletes all versions in versioned projects.

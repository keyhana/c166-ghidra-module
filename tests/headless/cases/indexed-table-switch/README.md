# indexed-table-switch

Exercises `C166SwitchAnalyzer` on the **indexed** dispatch-table jump the C166
CAPCOM interrupt handlers use:

```
cmp  r8, #1              ; case bound
jmpr cc_UGT, default
shl  r8, #1             ; state * 2 (word stride)
mov  r8, [r8 + #0x2000] ; load target from table (DPP0=0 -> table @ 0x2000)
jmpi cc_UC, [r8]
```

The `c166_switch_load` SLEIGH injector only fires for the `mov Rw,[Rw]` form, so
without the analyzer the `jmpi` is an unresolved dynamic goto and the decompiler
fails to structure the switch. With `c166SwitchAnalysis=true` the analyzer writes
a jump-table override (cases 0..1 -> 0x2020 / 0x2022, default 0x201e).

NOTE: `expected.disasm` / `expected.decomp` were authored without a local Ghidra
build; regenerate/verify them with `./gradlew headlessTest` before relying on CI
(the whole-image capture format is maintainer-canonical).

Regression for issue #55 (EXTR window boundary).

Each `extr #N`, for N = 1 through 4, is followed by N + 1 identical
`mov reg,#data16` instructions (opcode E6, register selector 91).
The manual's EXTR description requires the first N writes to use ESFR
0xF122 and the next write to use standard SFR 0xFF22. Repeating the
same instruction checks every position in the window and its expiration.
The golden listing checks both decoded operands and write references.

Before the fix, Ghidra 12.2 resolved the last instruction inside each window
to 0xFF22. Those four operand/reference mismatches were the only failures
in the headless suite.

The sibling `extr-bit-counter` case covers BCLR opcodes 8E, 9E, AE and BE
(bits 8 through 11), each with EXTR counts 1 through 4 and an access after
the window. It checks both the bit operand and read/write reference.

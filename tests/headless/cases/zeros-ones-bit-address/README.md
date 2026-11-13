Regression for issue #58. With standard SFR selection, bit operand 0x8E
denotes ZEROS at 0xFF1C and 0x8F denotes ONES at 0xFF1E. The erroneous
constructors displayed the unrelated RAM addresses 0xFD1C and 0xFD1E.

The fixture includes both constants, the actual RAM operands 0x0E/0x0F,
and an EXTR #2 window where 0x8E/0x8F must access ESFR 0xF11C/0xF11E.
It checks the return to standard SFR selection after the window. This follows
the bitoff addressing rules in c166ism.md section 6.1.

The constants have no memory-read p-code, so the golden expects no source
memory references for them. RAM and ESFR controls do have read references.

On unfixed main at a3e0f2b, the four standard ZEROS/ONES operands display
0xFD1C/0xFD1E. All RAM and EXTR controls already pass.

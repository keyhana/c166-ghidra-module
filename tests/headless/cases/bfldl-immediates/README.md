Regression for issue #57: BFLDL encodes its mask in byte 2 and data in
byte 3, opposite to BFLDH (c166ism.md, BFLDL/BFLDH instruction entries).

The first function uses the reported `0A CC FF 7A` vector. The following
functions initialize r4 to 0xA55A, modify it, and return it. Expected results:

| Function | Instruction | Result |
| --- | --- | --- |
| 0x000006 | BFLDL r4, #0xFF, #0x7A | 0xA57A |
| 0x000010 | BFLDL r4, #0x0F, #0x80 | 0xA5D0 |
| 0x00001A | BFLDL r4, #0x00, #0x01 | 0xA55B |
| 0x000024 | BFLDH r4, #0x0F, #0x80 | 0xA05A |

The partial and zero masks protect the manual's `(old & ~mask) | data`
semantics: data bits outside the mask still set destination bits. The
decompilation golden checks these values and preservation of the other byte.

On unfixed main at a3e0f2b, all four BFLDL operand pairs are reversed and
the r4 results are 0xA5FF, 0xA55F, and 0xA55A. The BFLDH control passes.

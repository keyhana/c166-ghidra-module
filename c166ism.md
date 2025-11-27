![](_page_0_Figure_1.jpeg)

Microcontrollers

![](_page_0_Picture_3.jpeg)

Never stop thinking.

#### **Edition 2001-03**

Published by Infineon Technologies AG, St.-Martin-Strasse 53, D-81541 München, Germany © Infineon Technologies AG 2001. All Rights Reserved.

#### **Attention please!**

The information herein is given to describe certain components and shall not be considered as warranted characteristics.

Terms of delivery and rights to technical change reserved.

We hereby disclaim any and all warranties, including but not limited to warranties of non-infringement, regarding circuits, descriptions and charts stated herein.

Infineon Technologies is an approved CECC manufacturer.

#### Information

For further information on technology, delivery terms and conditions and prices please contact your nearest Infineon Technologies Office in Germany or our Infineon Technologies Representatives worldwide.

#### Warnings

Due to technical requirements components may contain dangerous substances. For information on the types in question please contact your nearest Infineon Technologies Office.

Infineon Technologies Components may only be used in life-support devices or systems with the express written approval of Infineon Technologies, if a failure of such components can reasonably be expected to cause the failure of that life-support device or system, or to affect the safety or effectiveness of that device or system. Life support devices or systems are intended to be implanted in the human body, or to support and/or maintain and sustain and/or protect human life. If they fail, it is reasonable to assume that the health of the user or other persons may be endangered.

# Instruction Set Manual

for the C166 Family of Infineon 16-Bit Single-Chip Microcontrollers

### Microcontrollers

![](_page_2_Picture_4.jpeg)

| C166 Family Microcontroller Instruction Set Manual |                    |  |  |  |  |  |
|----------------------------------------------------|--------------------|--|--|--|--|--|
| <b>Revision History:</b>                           | V2.0, 2001-03      |  |  |  |  |  |
| Dravious Varaion.                                  | Varaion 1.0. 10.07 |  |  |  |  |  |

Previous Version: Version 1.2, 12.97 Version 1.1, 09-95

03.94

| Page         | Subjects (major changes since last revision)             |
|--------------|----------------------------------------------------------|
| all          | Converted to new company layout                          |
| 4 30         | Overview- and summary-tables reformatted                 |
| 2            | List of derivatives updated                              |
| 31           | Description template added                               |
| 34           | PSW image added                                          |
| 38           | Condition code table moved                               |
| 40           | Note for MUL/DIV added                                   |
| <b>42</b> ff | Immediate data for byte instructions corrected to #data8 |
| <b>52</b> f  | Note improved                                            |
| <b>62</b>    | Description of operation corrected                       |
| <b>72</b> ff | Description of division instructions improved            |
| 85           | Format description corrected                             |
| 86           | Description improved                                     |
| <b>101</b> f | Description of multiplication instructions improved      |
| 128          | Description of flags corrected                           |
| 132          | "bitoff" for ESFRs added                                 |
| 137          | Section moved                                            |
| 139          | Target address for "rel" corrected                       |
| 141          | General description improved                             |
| 142ff        | Timing examples converted to 25 MHz                      |
| 143          | Branch execution times corrected                         |
| 148f         | Keyword index introduced                                 |

### **We Listen to Your Comments**

Any information within this document that you feel is wrong, unclear or missing at all? Your feedback will help us to continuously improve the quality of this document. Please send your proposal (including a reference to this document) to:

mcdocu.comments@infineon.com

![](_page_4_Picture_1.jpeg)

| Table                                              | of Contents F                                                                                                                                                                                          | Page                                   |
|----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| 1                                                  | Introduction                                                                                                                                                                                           | 1                                      |
| 2                                                  | Overviews                                                                                                                                                                                              | 3                                      |
| 3.1<br>3.2<br>3.3<br>3.4<br>3.5                    | Summary Data Addressing Modes Branch Target Addressing Modes Multiply and Divide Operations Extension Operations Branch Condition Codes                                                                | 8<br>9<br>9                            |
| 4                                                  | Encoding                                                                                                                                                                                               | . 22                                   |
| 5                                                  | Detailed Description                                                                                                                                                                                   | . 31                                   |
| 6<br>6.1<br>6.2<br>6.3<br>6.4<br>6.5<br>6.6<br>6.7 | Addressing Modes Short Addressing Modes Long Addressing Mode Indirect Addressing Modes DPP Override Mechanism Constants within Instructions Instruction Range (#irang2) Branch Target Addressing Modes | 132<br>134<br>135<br>137<br>138<br>138 |
| <b>7</b><br>7.1<br>7.2<br>7.3                      | Instruction State Times Time Unit Definitions Minimum Execution Time Additional State Times                                                                                                            | 142<br>143                             |
| 8                                                  | Keyword Index                                                                                                                                                                                          | 148                                    |
|                                                    |                                                                                                                                                                                                        |                                        |

User's Manual V2.0, 2001-03

![](_page_5_Picture_1.jpeg)

Introduction

### 1 Introduction

The Infineon C166 Family of 16-bit microcontrollers offers devices that provide various levels of peripheral performance and programmability. This allows to equip each specific application with the microcontroller that fits best to the required functionality and performance.

Still the Infineon family concept provides an easy path to upgrade existing applications or to climb the next level of performance in order to realize a subsequent more sophisticated design. Two major characteristics enable this upgrade path to save and reuse almost all of the engineering efforts that have been made for previous designs:

- All family members are based on the same basic architecture
- All family members execute the same instructions (except for upgrades for new members)

The fact that all members execute basically the same instructions saves know-how with respect to the understanding of the controller itself and also with respect to the used tools (assembler, disassembler, compiler, etc.).

This instruction set manual provides an easy and direct access to the instructions of the Infineon 16-bit microcontrollers by listing them according to different criteria, and also unloads the technical manuals for the different devices from redundant information.

This manual also describes the different addressing mechanisms and the relation between the logical addresses used in a program and the resulting physical addresses. There is also information provided to calculate the execution time for specific instructions depending on the used address locations and also specific exceptions to the standard rules

### **Description Levels**

In the following sections the instructions are compiled according to different criteria in order to provide different levels of precision:

- Cross Reference Tables summarize all instructions in condensed tables
- The Instruction Set Summary groups the individual instructions into functional groups
- The Opcode Table
   references the instructions by their hexadecimal opcode
- The Instruction Description describes each instruction in full detail

![](_page_6_Picture_1.jpeg)

#### Introduction

All instructions listed in this manual are executed by the following devices (new derivatives will be added to this list):

- C161K, C161O, C161PI
- C161CS, C161JC, C161JI
- C163
- C164CI, C164SI, C164CM, C164SM
- C165
- C167CR, C167SR
- C167CS

A few instructions (ATOMIC and EXTended instructions) have been added for these devices and are not recognized by the following devices from the first generation of 16-bit microcontrollers:

- SAB 80C166, SAB 80C166W
- SAB 83C166, SAB 83C166W

These differences are noted for each instruction, where applicable.

![](_page_7_Picture_1.jpeg)

### 2 Overviews

The following compressed cross-reference tables quickly identify a specific instruction and provide basic information about it.

Two ordering schemes are included:

- The hexadecimal opcode of a specific instruction can be quickly identified with the respective mnemonic using the first compressed cross-reference table.
- The mnemonics and addressing modes of the various instructions are listed in the second table. The table shows which addressing modes may be used with a specific instruction and also the instruction length depending on the selected addressing mode. This reference helps to optimize instruction sequences in terms of code size and/or execution time.

Both ordering schemes (hexadecimal opcode and mnemonic) are provided in more detailed lists in the following sections of this manual.

Note: The ATOMIC and EXTended instructions are not available in the SAB 8XC166(W) devices.

They are marked in the cross-reference table.

![](_page_8_Picture_1.jpeg)

Table 1 Instruction Overview ordered by Hex-Code (lower half)

|            | 0x    | 1x    | 2x    | 3x    | 4x   | 5x   | 6x   | 7x    |
|------------|-------|-------|-------|-------|------|------|------|-------|
| <b>x</b> 0 | ADD   | ADDC  | SUB   | SUBC  | CMP  | XOR  | AND  | OR    |
| <b>x</b> 1 | ADDB  | ADDCB | SUBB  | SUBCB | СМРВ | XORB | ANDB | ORB   |
| <b>x2</b>  | ADD   | ADDC  | SUB   | SUBC  | СМР  | XOR  | AND  | OR    |
| х3         | ADDB  | ADDCB | SUBB  | SUBCB | СМРВ | XORB | ANDB | ORB   |
| <b>x</b> 4 | ADD   | ADDC  | SUB   | SUBC  | l    | XOR  | AND  | OR    |
| <b>x</b> 5 | ADDB  | ADDCB | SUBB  | SUBCB | l    | XORB | ANDB | ORB   |
| <b>x</b> 6 | ADD   | ADDC  | SUB   | SUBC  | СМР  | XOR  | AND  | OR    |
| <b>x7</b>  | ADDB  | ADDCB | SUBB  | SUBCB | СМРВ | XORB | ANDB | ORB   |
| <b>x8</b>  | ADD   | ADDC  | SUB   | SUBC  | СМР  | XOR  | AND  | OR    |
| <b>x9</b>  | ADDB  | ADDCB | SUBB  | SUBCB | СМРВ | XORB | ANDB | ORB   |
| хA         | BFLDL | BFLDH | ВСМР  | BMOVN | BMOV | BOR  | BAND | BXOR  |
| хВ         | MUL   | MULU  | PRIOR |       | DIV  | DIVU | DIVL | DIVLU |
| хC         | ROL   | ROL   | ROR   | ROR   | SHL  | SHL  | SHR  | SHR   |
| хD         | JMPR  | JMPR  | JMPR  | JMPR  | JMPR | JMPR | JMPR | JMPR  |
| хE         | BCLR  | BCLR  | BCLR  | BCLR  | BCLR | BCLR | BCLR | BCLR  |
| хF         | BSET  | BSET  | BSET  | BSET  | BSET | BSET | BSET | BSET  |

![](_page_9_Picture_1.jpeg)

Table 2 Instruction Overview ordered by Hex-Code (upper half)

|            | 8x    | 9x    | Ax     | Вх    | Сх    | Dx                 | Ex    | Fx   |
|------------|-------|-------|--------|-------|-------|--------------------|-------|------|
| х0         | CMPI1 | CMPI2 | CMPD1  | CMPD2 | MOVBZ | MOVBS              | MOV   | MOV  |
| <b>x1</b>  | NEG   | CPL   | NEGB   | CPLB  | _     | ATOMIC<br>EXTR     | MOVB  | MOVB |
| <b>x2</b>  | CMPI1 | CMPI2 | CMPD1  | CMPD2 | MOVBZ | MOVBS              | PCALL | MOV  |
| х3         | 1     | _     | _      | _     | -     | _                  | -     | MOVB |
| <b>x</b> 4 | MOV   | MOV   | MOVB   | MOVB  | MOV   | MOV                | MOVB  | MOVB |
| <b>x</b> 5 | ı     | _     | DISWDT | EINIT | MOVBZ | MOVBS              |       | _    |
| <b>x6</b>  | CMPI1 | CMPI2 | CMPD1  | CMPD2 | SCXT  | SCXT               | MOV   | MOV  |
| <b>x7</b>  | IDLE  | PWRDN | SRVWDT | SRST  | 1     | EXTP[R]<br>EXTS[R] | MOVB  | MOVB |
| <b>x8</b>  | MOV   | MOV   | MOV    | MOV   | MOV   | MOV                | MOV   | _    |
| <b>x9</b>  | MOVB  | MOVB  | MOVB   | MOVB  | MOVB  | MOVB               | MOVB  | _    |
| хA         | JB    | JNB   | JBC    | JNBS  | CALLA | CALLS              | JMPA  | JMPS |
| хВ         | _     | TRAP  | CALLI  | CALLR | RET   | RETS               | RETP  | RETI |
| хC         | _     | JMPI  | ASHR   | ASHR  | NOP   | EXTP[R]<br>EXTS[R] | PUSH  | POP  |
| хD         | JMPR  | JMPR  | JMPR   | JMPR  | JMPR  | JMPR               | JMPR  | JMPR |
| хE         | BCLR  | BCLR  | BCLR   | BCLR  | BCLR  | BCLR               | BCLR  | BCLR |
| хF         | BSET  | BSET  | BSET   | BSET  | BSET  | BSET               | BSET  | BSET |

![](_page_10_Picture_1.jpeg)

 Table 3
 Instruction Overview ordered by Mnemonic

| Mnemo-<br>nic(s)                                 | Addressing           | Modes                                | Bytes | Mnemo-<br>nic(s)                    | Addressing                                   | Modes                                              |    | Bytes     |
|--------------------------------------------------|----------------------|--------------------------------------|-------|-------------------------------------|----------------------------------------------|----------------------------------------------------|----|-----------|
| ADD[B]<br>ADDC[B]                                | Rwn,<br>Rwn,         | Rwm<br>[Rwi]                         | 2     | CPL[B]<br>NEG[B]                    | Rwn (Rbn) <sup>1)</sup>                      |                                                    |    | 2         |
| AND[B] OR[B] SUB[B] SUBC[B] XOR[B] <sup>1)</sup> | Rwn,<br>Rwn,<br>reg, | [Rwi+] #data3  #data16 <sup>2)</sup> | 2 4 4 | DIV<br>DIVL<br>DIVLU<br>DIVU        | Rwn                                          |                                                    |    | 2         |
| VOU[D]                                           | reg,<br>mem,         | mem<br>reg                           | 4     | MUL<br>MULU                         | Rwn,                                         | Rwm                                                |    | 2         |
| ASHR<br>ROL                                      | Rwn,<br>Rwn,         | Rwm<br>#data4                        | 2 2   | CMPD1<br>CMPD2                      | Rwn,                                         | #data4                                             |    | 2         |
| ROR<br>SHL<br>SHR                                |                      |                                      |       | CMPI1<br>CMPI2                      | Rwn,<br>Rwn,                                 | #data16<br>mem                                     |    | 4         |
| BAND<br>BCMP<br>BMOV<br>BMOVN<br>BOR<br>BXOR     | bitaddrZ.z,          | bitaddrQ.q                           | 4     | CMP<br>CMPB <sup>1)</sup>           | Rwn,<br>Rwn,<br>Rwn,<br>Rwn,<br>reg,<br>reg, | Rwm [Rwi] [Rwi+] #data3  #data16 <sup>2)</sup> mem |    | 2 2 2 4 4 |
| BCLR<br>BSET                                     | bitaddrQ.q           |                                      | 2     | CALLA<br>JMPA                       | cc,                                          | caddr                                              |    | 4         |
| BFLDH<br>BFLDL                                   | bitoffQ,             | #mask8,<br>#data8                    | 4     | CALLI<br>JMPI                       | CC,                                          | [Rwn]                                              |    | 2         |
| EXTS<br>EXTSR                                    | Rwm,<br>#seg,        | #irang2 <sup>3)</sup><br>#irang2     | 2 4   | EXTP<br>EXTPR                       | Rwm,<br>#pag,                                | #irang2<br>#irang2                                 | 3) | 2 4       |
| NOP<br>RET<br>RETI<br>RETS                       | _                    |                                      | 2     | SRST IDLE PWRDN SRVWDT DISWDT EINIT |                                              |                                                    |    | 4         |

![](_page_11_Picture_1.jpeg)

 Table 3
 Instruction Overview ordered by Mnemonic (cont'd)

| Mnemo-<br>nic(s)          | Addressing                             | Modes                                    | Bytes            | Mnemo-<br>nic(s)         | Addressing  | Modes   | Bytes | Dyice    |
|---------------------------|----------------------------------------|------------------------------------------|------------------|--------------------------|-------------|---------|-------|----------|
| MOV<br>MOVB <sup>1)</sup> | Rwn,<br>Rwn,                           | Rwm<br>#data4                            | 2 2              | CALLS<br>JMPS            | seg,        | caddr   | 4     | 1        |
|                           | Rwn,                                   | [Rwm]                                    | 2                | CALLR                    | rel         |         | 2     | <u> </u> |
|                           | Rwn,<br>  [Rwm],                       | [Rwm+]<br>Rwn                            | 2                | JMPR                     | CC,         | rel     | 2     | 2        |
|                           | [-Rwm],<br>[Rwn],<br>[Rwn+],<br>[Rwn], | Rwn<br>[Rwm]<br>[Rwm]<br>[Rwm+]          | 2 2 2            | JB<br>JBC<br>JNB<br>JNBS | bitaddrQ.q, | rel     | 4     | <u>_</u> |
|                           |                                        | "   1   402)                             |                  | PCALL                    | reg,        | caddr   | 4     | 1        |
|                           | reg,<br>Rwn,<br>[Rwm+#d16],<br>[Rwn],  | #data16 <sup>2)</sup> [Rwm+#d16] Rwn mem | 4<br>4<br>4<br>4 | POP<br>PUSH<br>RETP      | reg         |         | 2     | <u> </u> |
|                           | mem,                                   | [Rwn]                                    | 4                | SCXT                     | reg,        | #data16 | 4     | 1        |
|                           | reg,                                   | mem                                      | 4                |                          | reg,        | mem     | 4     | 1        |
|                           | mem,                                   | reg                                      | 4                | PRIOR                    | Rwn,        | Rwm     | 2     | 2        |
| MOVBS                     | Rwn,                                   | Rbm                                      | 2                | TRAP                     | #trap7      |         | 2     | 2        |
| MOVBZ                     | reg,<br>mem,                           | mem<br>reg                               | 4                | ATOMIC<br>EXTR           | #irang2     | 3       | 3) 2  | <u>-</u> |

<sup>1)</sup> Byte oriented instructions (suffix 'B') use byte registers (Rb instead of Rw), except for indirect addressing modes ([Rw] or [Rw+]).

<sup>2)</sup> Byte oriented instructions (suffix 'B') use #data8 instead of #data16.

<sup>3)</sup> The ATOMIC and EXTended instructions are not available in the SAB 8XC166(W) devices.

![](_page_12_Picture_1.jpeg)

### 3 Summary

This chapter summarizes the instructions by listing them according to their functional class. This enables the user to identify the right instruction(s) for a specific required function.

The following general explanations apply to this summary:

### 3.1 Data Addressing Modes

Rw: Word GPR (R0, R1, ..., R15)

Rb: Byte GPR (RL0, RH0, ..., RL7, RH7)

reg: SFR/ESFR or GPR

(in case of a byte operation on an SFR, only the low byte can be accessed

via 'reg')

mem: Direct word or byte memory location

[...]: Indirect word or byte memory location

(Any word GPR can be used as indirect address pointer, except for the arithmetic, logical and compare instructions, where only R0 to R3 are

allowed)

baddr: Direct bit in the bit-addressable memory area

bitoff: Direct word in the bit-addressable memory area

#datax: Immediate constant

(The number of significant bits which can be specified by the user is

represented by the respective appendix 'x')

#mask8: Immediate 8-bit mask used for bit-field modifications

### 3.2 Branch Target Addressing Modes

caddr: Direct 16-bit jump target address (updates the Instruction Pointer)

Rb: Byte GPR (RL0, RH0, ..., RL7, RH7)

seg: Direct 8-bit segment address<sup>1)</sup>

(updates the Code Segment Pointer)

rel: Signed 8-bit jump target word offset address relative to the Instruction

Pointer of the following instruction

#trap7: Immediate 7-bit trap or interrupt number

<sup>1)</sup> In the 8XC166(W) devices the segment is only a 2-bit number, due to the smaller address range.

![](_page_13_Picture_1.jpeg)

### 3.3 Multiply and Divide Operations

The MDL and MDH registers are implicit source and/or destination operands of the multiply and divide instructions.

### 3.4 Extension Operations

#pag10: Immediate 10-bit page address#seg8: Immediate 8-bit segment address#irang2: Immediate 2-bit instruction range

The extension instructions EXTP, EXTPR, EXTS, and EXTSR override the standard DPP addressing scheme, using immediate addresses instead.

Note: The EXTended instructions are not available in the SAB 8XC166(W) devices.

### 3.5 Branch Condition Codes

| cc: | cc_UC  | Unconditional                  |
|-----|--------|--------------------------------|
|     | cc_Z   | Zero                           |
|     | cc_NZ  | Not Zero                       |
|     | cc_V   | Overflow                       |
|     | cc_NV  | No Overflow                    |
|     | cc_N   | Negative                       |
|     | cc_NN  | Not Negative                   |
|     | cc_C   | Carry                          |
|     | cc_NC  | No Carry                       |
|     | cc_EQ  | Equal                          |
|     | cc_NE  | Not Equal                      |
|     | cc_ULT | Unsigned Less Than             |
|     | cc_ULE | Unsigned Less Than or Equal    |
|     | cc_UGE | Unsigned Greater Than or Equal |
|     | cc_UGT | <b>Unsigned Greater Than</b>   |
|     | cc_SLE | Signed Less Than or Equal      |
|     | cc_SGE | Signed Greater Than or Equal   |
|     | cc_SGT | Signed Greater Than            |
|     | cc_NET | Not Equal and Not End-of-Table |

Note: Condition codes can be specified symbolically within an instruction.

A detailed description of the condition codes can be found in Table 5.

![](_page_14_Picture_1.jpeg)

## Table 4 Instruction Set Summary

| Mnemonic |                | Description                                                                              |   |  |  |
|----------|----------------|------------------------------------------------------------------------------------------|---|--|--|
| Arithme  | tic Operations |                                                                                          |   |  |  |
| ADD      | Rw, Rw         | Add direct word GPR to direct GPR                                                        | 2 |  |  |
| ADD      | Rw, [Rw]       | Add indirect word memory to direct GPR                                                   | 2 |  |  |
| ADD      | Rw, [Rw+]      | Add indirect word memory to direct GPR and post-increment source pointer by 2            | 2 |  |  |
| ADD      | Rw, #data3     | Add immediate word data to direct GPR                                                    | 2 |  |  |
| ADD      | reg, #data16   | Add immediate word data to direct register                                               | 4 |  |  |
| ADD      | reg, mem       | Add direct word memory to direct register                                                | 4 |  |  |
| ADD      | mem, reg       | Add direct word register to direct memory                                                | 4 |  |  |
| ADDB     | Rb, Rb         | Add direct byte GPR to direct GPR                                                        | 2 |  |  |
| ADDB     | Rb, [Rw]       | Add indirect byte memory to direct GPR                                                   | 2 |  |  |
| ADDB     | Rb, [Rw+]      | Add indirect byte memory to direct GPR and post-increment source pointer by 1            | 2 |  |  |
| ADDB     | Rb, #data3     | Add immediate byte data to direct GPR                                                    | 2 |  |  |
| ADDB     | reg, #data8    | Add immediate byte data to direct register                                               | 4 |  |  |
| ADDB     | reg, mem       | Add direct byte memory to direct register                                                | 4 |  |  |
| ADDB     | mem, reg       | Add direct byte register to direct memory                                                | 4 |  |  |
| ADDC     | Rw, Rw         | Add direct word GPR to direct GPR with Carry                                             | 2 |  |  |
| ADDC     | Rw, [Rw]       | Add indirect word memory to direct GPR with Carry                                        | 2 |  |  |
| ADDC     | Rw, [Rw+]      | Add indirect word memory to direct GPR with Carry and post-increment source pointer by 2 | 2 |  |  |
| ADDC     | Rw, #data3     | Add immediate word data to direct GPR with Carry                                         | 2 |  |  |
| ADDC     | reg, #data16   | Add immediate word data to direct register with Carry                                    | 4 |  |  |
| ADDC     | reg, mem       | Add direct word memory to direct register with Carry                                     | 4 |  |  |
| ADDC     | mem, reg       | Add direct word register to direct memory with Carry                                     | 4 |  |  |
| ADDCB    | Rb, Rb         | Add direct byte GPR to direct GPR with Carry                                             | 2 |  |  |
| ADDCB    | Rb, [Rw]       | Add indirect byte memory to direct GPR with Carry                                        | 2 |  |  |
| ADDCB    | Rb, [Rw+]      | Add indirect byte memory to direct GPR with Carry and post-increment source pointer by 1 | 2 |  |  |
| ADDCB    | Rb, #data3     | Add immediate byte data to direct GPR with Carry                                         | 2 |  |  |

![](_page_15_Picture_1.jpeg)

Table 4Instruction Set Summary (cont'd)

| Mnemo   | nic            | Description                                                                                     | Bytes |
|---------|----------------|-------------------------------------------------------------------------------------------------|-------|
| Arithme | tic Operations | s (cont'd)                                                                                      |       |
| ADDCB   | reg, #data8    | Add immediate byte data to direct register with Carry                                           | 4     |
| ADDCB   | reg, mem       | Add direct byte memory to direct register with Carry                                            | 4     |
| ADDCB   | mem, reg       | Add direct byte register to direct memory with Carry                                            | 4     |
| SUB     | Rw, Rw         | Subtract direct word GPR from direct GPR                                                        | 2     |
| SUB     | Rw, [Rw]       | Subtract indirect word memory from direct GPR                                                   | 2     |
| SUB     | Rw, [Rw+]      | Subtract indirect word memory from direct GPR and post-increment source pointer by 2            | 2     |
| SUB     | Rw, #data3     | Subtract immediate word data from direct GPR                                                    | 2     |
| SUB     | reg, #data16   | Subtract immediate word data from direct register                                               | 4     |
| SUB     | reg, mem       | Subtract direct word memory from direct register                                                | 4     |
| SUB     | mem, reg       | Subtract direct word register from direct memory                                                | 4     |
| SUBB    | Rb, Rb         | Subtract direct byte GPR from direct GPR                                                        | 2     |
| SUBB    | Rb, [Rw]       | Subtract indirect byte memory from direct GPR                                                   | 2     |
| SUBB    | Rb, [Rw+]      | Subtract indirect byte memory from direct GPR and post-increment source pointer by 1            | 2     |
| SUBB    | Rb, #data3     | Subtract immediate byte data from direct GPR                                                    | 2     |
| SUBB    | reg, #data8    | Subtract immediate byte data from direct register                                               | 4     |
| SUBB    | reg, mem       | Subtract direct byte memory from direct register                                                | 4     |
| SUBB    | mem, reg       | Subtract direct byte register from direct memory                                                | 4     |
| SUBC    | Rw, Rw         | Subtract direct word GPR from direct GPR with Carry                                             | 2     |
| SUBC    | Rw, [Rw]       | Subtract indirect word memory from direct GPR with Carry                                        | 2     |
| SUBC    | Rw, [Rw+]      | Subtract indirect word memory from direct GPR with Carry and post-increment source pointer by 2 | 2     |
| SUBC    | Rw, #data3     | Subtract immediate word data from direct GPR with Carry                                         | 2     |
| SUBC    | reg, #data16   | Subtract immediate word data from direct register with Carry                                    | 4     |
| SUBC    | reg, mem       | Subtract direct word memory from direct register with Carry                                     | 4     |

![](_page_16_Picture_1.jpeg)

Table 4Instruction Set Summary (cont'd)

| Mnemoi  | nic           | Description                                                                                     | Bytes |
|---------|---------------|-------------------------------------------------------------------------------------------------|-------|
| Arithme | tic Operation | s (cont'd)                                                                                      |       |
| SUBC    | mem, reg      | Subtract direct word register from direct memory with Carry                                     | 4     |
| SUBCB   | Rb, Rb        | Subtract direct byte GPR from direct GPR with Carry                                             | 2     |
| SUBCB   | Rb, [Rw]      | Subtract indirect byte memory from direct GPR with Carry                                        | 2     |
| SUBCB   | Rb, [Rw+]     | Subtract indirect byte memory from direct GPR with Carry and post-increment source pointer by 1 | 2     |
| SUBCB   | Rb, #data3    | Subtract immediate byte data from direct GPR with Carry                                         | 2     |
| SUBCB   | reg, #data8   | Subtract immediate byte data from direct register with Carry                                    | 4     |
| SUBCB   | reg, mem      | Subtract direct byte memory from direct register with Carry                                     | 4     |
| SUBCB   | mem, reg      | Subtract direct byte register from direct memory with Carry                                     | 4     |
| MUL     | Rw, Rw        | Signed multiply direct GPR by direct GPR (16-bit × 16-bit)                                      | 2     |
| MULU    | Rw, Rw        | Unsigned multiply direct GPR by direct GPR (16-bit × 16-bit)                                    | 2     |
| DIV     | Rw            | Signed divide register MDL by direct GPR (16-bit ÷ 16-bit)                                      | 2     |
| DIVL    | Rw            | Signed long divide register MD by direct GPR (32-bit ÷ 16-bit)                                  | 2     |
| DIVLU   | Rw            | Unsigned long divide register MD by direct GPR (32-bit ÷ 16-bit)                                | 2     |
| DIVU    | Rw            | Unsigned divide register MDL by direct GPR (16-bit ÷ 16-bit)                                    | 2     |
| CPL     | Rw            | Complement direct word GPR                                                                      | 2     |
| CPLB    | Rb            | Complement direct byte GPR                                                                      | 2     |
| NEG     | Rw            | Negate direct word GPR                                                                          | 2     |
| NEGB    | Rb            | Negate direct byte GPR                                                                          | 2     |

![](_page_17_Picture_1.jpeg)

| Table 4 Instruction Set Summary ( | (cont'd) |  |
|-----------------------------------|----------|--|
|-----------------------------------|----------|--|

| Mnemonic |              | Description                                                                             |   |  |
|----------|--------------|-----------------------------------------------------------------------------------------|---|--|
| Logical  | Instructions |                                                                                         |   |  |
| AND      | Rw, Rw       | Bitwise AND direct word GPR with direct GPR                                             | 2 |  |
| AND      | Rw, [Rw]     | Bitwise AND indirect word memory with direct GPR                                        | 2 |  |
| AND      | Rw, [Rw+]    | Bitwise AND indirect word memory with direct GPR and post-increment source pointer by 2 | 2 |  |
| AND      | Rw, #data3   | Bitwise AND immediate word data with direct GPR                                         | 2 |  |
| AND      | reg, #data16 | Bitwise AND immediate word data with direct register                                    | 4 |  |
| AND      | reg, mem     | Bitwise AND direct word memory with direct register                                     | 4 |  |
| AND      | mem, reg     | Bitwise AND direct word register with direct memory                                     | 4 |  |
| ANDB     | Rb, Rb       | Bitwise AND direct byte GPR with direct GPR                                             | 2 |  |
| ANDB     | Rb, [Rw]     | Bitwise AND indirect byte memory with direct GPR                                        | 2 |  |
| ANDB     | Rb, [Rw+]    | Bitwise AND indirect byte memory with direct GPR and post-increment source pointer by 1 | 2 |  |
| ANDB     | Rb, #data3   | Bitwise AND immediate byte data with direct GPR                                         | 2 |  |
| ANDB     | reg, #data8  | Bitwise AND immediate byte data with direct register                                    | 4 |  |
| ANDB     | reg, mem     | Bitwise AND direct byte memory with direct register                                     | 4 |  |
| ANDB     | mem, reg     | Bitwise AND direct byte register with direct memory                                     | 4 |  |
| OR       | Rw, Rw       | Bitwise OR direct word GPR with direct GPR                                              | 2 |  |
| OR       | Rw, [Rw]     | Bitwise OR indirect word memory with direct GPR                                         | 2 |  |
| OR       | Rw, [Rw+]    | Bitwise OR indirect word memory with direct GPR and post-increment source pointer by 2  | 2 |  |
| OR       | Rw, #data3   | Bitwise OR immediate word data with direct GPR                                          | 2 |  |
| OR       | reg, #data16 | Bitwise OR immediate word data with direct register                                     | 4 |  |
| OR       | reg, mem     | Bitwise OR direct word memory with direct register                                      | 4 |  |
| OR       | mem, reg     | Bitwise OR direct word register with direct memory                                      | 4 |  |
| ORB      | Rb, Rb       | Bitwise OR direct byte GPR with direct GPR                                              | 2 |  |
| ORB      | Rb, [Rw]     | Bitwise OR indirect byte memory with direct GPR                                         | 2 |  |
| ORB      | Rb, [Rw+]    | Bitwise OR indirect byte memory with direct GPR and post-increment source pointer by 1  | 2 |  |
| ORB      | Rb, #data3   | Bitwise OR immediate byte data with direct GPR                                          | 2 |  |

![](_page_18_Picture_1.jpeg)

| Table 4 Instruction Set Summ | ary | (cont'd) |
|------------------------------|-----|----------|
|------------------------------|-----|----------|

| Mnemo  | onic             | Description                                                                             | Bytes |
|--------|------------------|-----------------------------------------------------------------------------------------|-------|
| Logica | I Instructions ( | cont'd)                                                                                 |       |
| ORB    | reg, #data8      | Bitwise OR immediate byte data with direct register                                     | 4     |
| ORB    | reg, mem         | Bitwise OR direct byte memory with direct register                                      | 4     |
| ORB    | mem, reg         | Bitwise OR direct byte register with direct memory                                      | 4     |
| XOR    | Rw, Rw           | Bitwise XOR direct word GPR with direct GPR                                             | 2     |
| XOR    | Rw, [Rw]         | Bitwise XOR indirect word memory with direct GPR                                        | 2     |
| XOR    | Rw, [Rw+]        | Bitwise XOR indirect word memory with direct GPR and post-increment source pointer by 2 | 2     |
| XOR    | Rw, #data3       | Bitwise XOR immediate word data with direct GPR                                         | 2     |
| XOR    | reg, #data16     | Bitwise XOR immediate word data with direct register                                    | 4     |
| XOR    | reg, mem         | Bitwise XOR direct word memory with direct register                                     | 4     |
| XOR    | mem, reg         | Bitwise XOR direct word register with direct memory                                     | 4     |
| XORB   | Rb, Rb           | Bitwise XOR direct byte GPR with direct GPR                                             | 2     |
| XORB   | Rb, [Rw]         | Bitwise XOR indirect byte memory with direct GPR                                        | 2     |
| XORB   | Rb, [Rw+]        | Bitwise XOR indirect byte memory with direct GPR and post-increment source pointer by 1 | 2     |
| XORB   | Rb, #data3       | Bitwise XOR immediate byte data with direct GPR                                         | 2     |
| XORB   | reg, #data8      | Bitwise XOR immediate byte data with direct register                                    | 4     |
| XORB   | reg, mem         | Bitwise XOR direct byte memory with direct register                                     | 4     |
| XORB   | mem, reg         | Bitwise XOR direct byte register with direct memory                                     | 4     |

### **Prioritize Instruction**

| PRIOR Rw, Rw | Determine number of shift cycles to normalize direct | 2 |
|--------------|------------------------------------------------------|---|
|              | word GPR and store result in direct word GPR         |   |

![](_page_19_Picture_1.jpeg)

 Table 4
 Instruction Set Summary (cont'd)

| Mnemor           | nic               | Description                                                                               | Bytes |
|------------------|-------------------|-------------------------------------------------------------------------------------------|-------|
| Boolear          | n Bit Manipula    | tion Operations                                                                           |       |
| BCLR             | baddr             | Clear direct bit                                                                          | 2     |
| BSET             | baddr             | Set direct bit                                                                            | 2     |
| BMOV             | baddr, baddr      | Move direct bit to direct bit                                                             | 4     |
| BMOVN            | baddr, baddr      | Move negated direct bit to direct bit                                                     | 4     |
| BAND             | baddr, baddr      | AND direct bit with direct bit                                                            | 4     |
| BOR              | baddr, baddr      | OR direct bit with direct bit                                                             | 4     |
| BXOR             | baddr, baddr      | XOR direct bit with direct bit                                                            | 4     |
| ВСМР             | baddr, baddr      | Compare direct bit to direct bit                                                          | 4     |
| BFLDH<br>#mask8, | bitoff,<br>#data8 | Bitwise modify masked high byte of bit-addressable direct word memory with immediate data | 4     |
| BFLDL<br>#mask8, | bitoff,<br>#data8 | Bitwise modify masked low byte of bit-addressable direct word memory with immediate data  | 4     |
| CMP              | Rw, Rw            | Compare direct word GPR to direct GPR                                                     | 2     |
| CMP              | Rw, [Rw]          | Compare indirect word memory to direct GPR                                                | 2     |
| CMP              | Rw, [Rw+]         | Compare indirect word memory to direct GPR and post-increment source pointer by 2         | 2     |
| CMP              | Rw, #data3        | Compare immediate word data to direct GPR                                                 | 2     |
| CMP              | reg, #data16      | Compare immediate word data to direct register                                            | 4     |
| СМР              | reg, mem          | Compare direct word memory to direct register                                             | 4     |
| СМРВ             | Rb, Rb            | Compare direct byte GPR to direct GPR                                                     | 2     |
| СМРВ             | Rb, [Rw]          | Compare indirect byte memory to direct GPR                                                | 2     |
| СМРВ             | Rb, [Rw+]         | Compare indirect byte memory to direct GPR and post-increment source pointer by 1         | 2     |
| СМРВ             | Rb, #data3        | Compare immediate byte data to direct GPR                                                 | 2     |
| СМРВ             | reg, #data8       | Compare immediate byte data to direct register                                            | 4     |
| СМРВ             | reg, mem          | Compare direct byte memory to direct register                                             | 4     |

![](_page_20_Picture_1.jpeg)

| Table 4 | Instruction Set Summary | (cont'd)  |
|---------|-------------------------|-----------|
| IUNICI  | moti action cot camma y | (OOIIL G) |

| Mnemoi | nic          | Description                                                      | Bytes |
|--------|--------------|------------------------------------------------------------------|-------|
| Compar | e and Loop C | ontrol Instructions                                              |       |
| CMPD1  | Rw, #data4   | Compare immediate word data to direct GPR and decrement GPR by 1 | 2     |
| CMPD1  | Rw, #data16  | Compare immediate word data to direct GPR and decrement GPR by 1 | 4     |
| CMPD1  | Rw, mem      | Compare direct word memory to direct GPR and decrement GPR by 1  | 4     |
| CMPD2  | Rw, #data4   | Compare immediate word data to direct GPR and decrement GPR by 2 | 2     |
| CMPD2  | Rw, #data16  | Compare immediate word data to direct GPR and decrement GPR by 2 | 4     |
| CMPD2  | Rw, mem      | Compare direct word memory to direct GPR and decrement GPR by 2  | 4     |
| CMPI1  | Rw, #data4   | Compare immediate word data to direct GPR and increment GPR by 1 | 2     |
| CMPI1  | Rw, #data16  | Compare immediate word data to direct GPR and increment GPR by 1 | 4     |
| CMPI1  | Rw, mem      | Compare direct word memory to direct GPR and increment GPR by 1  | 4     |
| CMPI2  | Rw, #data4   | Compare immediate word data to direct GPR and increment GPR by 2 | 2     |
| CMPI2  | Rw, #data16  | Compare immediate word data to direct GPR and increment GPR by 2 | 4     |
| CMPI2  | Rw, mem      | Compare direct word memory to direct GPR and increment GPR by 2  | 4     |

### **Shift and Rotate Instructions**

| SHL | Rw, Rw     | Shift left direct word GPR; number of shift cycles specified by direct GPR     | 2 |
|-----|------------|--------------------------------------------------------------------------------|---|
| SHL | Rw, #data4 | Shift left direct word GPR; number of shift cycles specified by immediate data | 2 |
| SHR | Rw, Rw     | Shift right direct word GPR; number of shift cycles specified by direct GPR    | 2 |

![](_page_21_Picture_1.jpeg)

| Table 4 | Instruction Set Summary | (cont'd) | ) |
|---------|-------------------------|----------|---|
|---------|-------------------------|----------|---|

| Mnemo                                  | nic        | Description                                                                                           | Bytes |
|----------------------------------------|------------|-------------------------------------------------------------------------------------------------------|-------|
| Shift and Rotate Instructions (cont'd) |            |                                                                                                       |       |
| SHR                                    | Rw, #data4 | Shift right direct word GPR; number of shift cycles specified by immediate data                       | 2     |
| ROL                                    | Rw, Rw     | Rotate left direct word GPR;<br>number of shift cycles specified by direct GPR                        | 2     |
| ROL                                    | Rw, #data4 | Rotate left direct word GPR; number of shift cycles specified by immediate data                       | 2     |
| ROR                                    | Rw, Rw     | Rotate right direct word GPR; number of shift cycles specified by direct GPR                          | 2     |
| ROR                                    | Rw, #data4 | Rotate right direct word GPR; number of shift cycles specified by immediate data                      | 2     |
| ASHR                                   | Rw, Rw     | Arithmetic (sign bit) shift right direct word GPR; number of shift cycles specified by direct GPR     | 2     |
| ASHR                                   | Rw, #data4 | Arithmetic (sign bit) shift right direct word GPR; number of shift cycles specified by immediate data | 2     |

### **Data Movement**

| MOV | Rw, Rw       | Move direct word GPR to direct GPR                                                       | 2 |
|-----|--------------|------------------------------------------------------------------------------------------|---|
| MOV | Rw, #data4   | Move immediate word data to direct GPR                                                   | 2 |
| MOV | reg, #data16 | Move immediate word data to direct register                                              | 4 |
| MOV | Rw, [Rw]     | Move indirect word memory to direct GPR                                                  | 2 |
| MOV | Rw, [Rw+]    | Move indirect word memory to direct GPR and post-increment source pointer by 2           | 2 |
| MOV | [Rw], Rw     | Move direct word GPR to indirect memory                                                  | 2 |
| MOV | [-Rw], Rw    | Pre-decrement destination pointer by 2 and move direct word GPR to indirect memory       | 2 |
| MOV | [Rw], [Rw]   | Move indirect word memory to indirect memory                                             | 2 |
| MOV | [Rw+], [Rw]  | Move indirect word memory to indirect memory and post-increment destination pointer by 2 | 2 |
| MOV | [Rw], [Rw+]  | Move indirect word memory to indirect memory and post-increment source pointer by 2      | 2 |

![](_page_22_Picture_1.jpeg)

Table 4Instruction Set Summary (cont'd)

| Mnemo   | nic                | Description                                                                              |   |
|---------|--------------------|------------------------------------------------------------------------------------------|---|
| Data Mo | ovement (cont'     | d)                                                                                       |   |
| MOV     | Rw,<br>[Rw + #d16] | Move indirect word memory by base plus constant to direct word GPR                       | 4 |
| MOV     | [Rw + #d16],<br>Rw | Move direct word GPR to indirect memory by base plus constant                            | 4 |
| MOV     | [Rw], mem          | Move direct word memory to indirect memory                                               | 4 |
| MOV     | mem, [Rw]          | Move indirect word memory to direct memory                                               | 4 |
| MOV     | reg, mem           | Move direct word memory to direct register                                               | 4 |
| MOV     | mem, reg           | Move direct word register to direct memory                                               | 4 |
| MOVB    | Rb, Rb             | Move direct byte GPR to direct GPR                                                       | 2 |
| MOVB    | Rb, #data4         | Move immediate byte data to direct GPR                                                   | 2 |
| MOVB    | reg, #data8        | Move immediate byte data to direct register                                              | 4 |
| MOVB    | Rb, [Rw]           | Move indirect byte memory to direct GPR                                                  | 2 |
| MOVB    | Rb, [Rw+]          | Move indirect byte memory to direct GPR and post-increment source pointer by 1           | 2 |
| MOVB    | [Rw], Rb           | Move direct byte GPR to indirect memory                                                  | 2 |
| MOVB    | [-Rw], Rb          | Pre-decrement destination pointer by 1 and move direct byte GPR to indirect memory       | 2 |
| MOVB    | [Rw], [Rw]         | Move indirect byte memory to indirect memory                                             | 2 |
| MOVB    | [Rw+], [Rw]        | Move indirect byte memory to indirect memory and post-increment destination pointer by 1 | 2 |
| MOVB    | [Rw], [Rw+]        | Move indirect byte memory to indirect memory and post-increment source pointer by 1      | 2 |
| MOVB    | Rb,<br>[Rw + #d16] | Move indirect byte memory by base plus constant to direct byte GPR                       | 4 |
| MOVB    | [Rw + #d16],<br>Rb | Move direct byte GPR to indirect memory by base plus constant                            | 4 |
| MOVB    | [Rw], mem          | Move direct byte memory to indirect memory                                               | 4 |
| MOVB    | mem, [Rw]          | Move indirect byte memory to direct memory                                               | 4 |
| MOVB    | reg, mem           | Move direct byte memory to direct register                                               | 4 |
| MOVB    | mem, reg           | Move direct byte register to direct memory                                               | 4 |

![](_page_23_Picture_1.jpeg)

### Table 4 Instruction Set Summary (cont'd)

| Mnemonic           | Description                                                         | Bytes |
|--------------------|---------------------------------------------------------------------|-------|
| Data Movement (con | ıt'd)                                                               |       |
| MOVBS Rw, Rb       | Move direct byte GPR with sign extension to direct word GPR         | 2     |
| MOVBS reg, mem     | Move direct byte memory with sign extension to direct word register | 4     |
| MOVBS mem, reg     | Move direct byte register with sign extension to direct word memory | 4     |
| MOVBZ Rw, Rb       | Move direct byte GPR with zero extension to direct word GPR         | 2     |
| MOVBZ reg, mem     | Move direct byte memory with zero extension to direct word register | 4     |
| MOVBZ mem, reg     | Move direct byte register with zero extension to direct word memory | 4     |

### **Jump and Call Instructions**

| JMPA  | cc, caddr  | Jump absolute if condition is met                                        | 4 |
|-------|------------|--------------------------------------------------------------------------|---|
| JMPI  | cc, [Rw]   | Jump indirect if condition is met                                        | 2 |
| JMPR  | cc, rel    | Jump relative if condition is met                                        | 2 |
| JMPS  | seg, caddr | Jump absolute to a code segment                                          | 4 |
| JB    | baddr, rel | Jump relative if direct bit is set                                       | 4 |
| JBC   | baddr, rel | Jump relative and clear bit if direct bit is set                         | 4 |
| JNB   | baddr, rel | Jump relative if direct bit is not set                                   | 4 |
| JNBS  | baddr, rel | Jump relative and set bit if direct bit is not set                       | 4 |
| CALLA | cc, caddr  | Call absolute subroutine if condition is met                             | 4 |
| CALLI | cc, [Rw]   | Call indirect subroutine if condition is met                             | 2 |
| CALLR | rel        | Call relative subroutine                                                 | 2 |
| CALLS | seg, caddr | Call absolute subroutine in any code segment                             | 4 |
| PCALL | reg, caddr | Push direct word register onto system stack and call absolute subroutine | 4 |
| TRAP  | #trap7     | Call interrupt service routine via immediate trap number                 | 2 |
|       |            |                                                                          | 1 |

![](_page_24_Picture_1.jpeg)

### Table 4 Instruction Set Summary (cont'd)

| Mnemonic | Description | Bytes |
|----------|-------------|-------|
|----------|-------------|-------|

### **Return Instructions**

| RET      | Return from intra-segment subroutine                                                | 2 |
|----------|-------------------------------------------------------------------------------------|---|
| RETS     | Return from inter-segment subroutine                                                | 2 |
| RETP reg | Return from intra-segment subroutine and pop direct word register from system stack | 2 |
| RETI     | Return from interrupt service subroutine                                            | 2 |

### System Control<sup>1)</sup>

| SRST   |                    | Software Reset                                     | 4 |  |  |  |
|--------|--------------------|----------------------------------------------------|---|--|--|--|
| IDLE   |                    | Enter Idle Mode                                    | 4 |  |  |  |
| PWRDN  |                    | Enter Power Down Mode (supposes NMI-pin being low) | 4 |  |  |  |
| SRVWD  | T                  | Service Watchdog Timer                             |   |  |  |  |
| DISWDT | -                  | Disable Watchdog Timer                             | 4 |  |  |  |
| EINIT  |                    | Signify End-of-Initialization on RSTOUT-pin        | 4 |  |  |  |
| ATOMIC | apple 2 #irang2    | Begin ATOMIC sequence                              | 2 |  |  |  |
| EXTR   | #irang2            | Begin EXTended Register sequence                   | 2 |  |  |  |
| EXTP   | Rw, #irang2        | Begin EXTended Page sequence                       |   |  |  |  |
| EXTP   | #pag10,<br>#irang2 | Begin EXTended Page sequence                       | 4 |  |  |  |
| EXTPR  | Rw, #irang2        | Begin EXTended Page and Register sequence          | 2 |  |  |  |
| EXTPR  | #pag10,<br>#irang2 | Begin EXTended Page and Register sequence          | 4 |  |  |  |
| EXTS   | Rw, #irang2        | Begin EXTended Segment sequence                    | 2 |  |  |  |
| EXTS   | #seg8,<br>#irang2  | Begin EXTended Segment sequence                    | 4 |  |  |  |
| EXTSR  | Rw, #irang2        | Begin EXTended Segment and Register sequence       | 2 |  |  |  |
| EXTSR  | #seg8,<br>#irang2  | Begin EXTended Segment and Register sequence       | 4 |  |  |  |

![](_page_25_Picture_1.jpeg)

### Table 4 Instruction Set Summary (cont'd)

| Mnemonic | Description | Bytes |
|----------|-------------|-------|
|----------|-------------|-------|

### **System Stack Instructions**

| POP  | reg          | Pop direct word register from system stack                                          | 2 |
|------|--------------|-------------------------------------------------------------------------------------|---|
| PUSH | reg          | Push direct word register onto system stack                                         | 2 |
| SCXT | reg, #data16 | Push direct word register onto system stack and update register with immediate data | 4 |
| SCXT | reg, mem     | Push direct word register onto system stack and update register with direct memory  | 4 |

### **Miscellaneous**

| NOP | Null operation | 2 |
|-----|----------------|---|

<sup>1)</sup> The ATOMIC and EXTended instructions are not available in the SAB 8XC166(W) devices.

![](_page_26_Picture_1.jpeg)

### 4 Encoding

The following pages list the instructions of the 16-bit microcontrollers ordered by their hexadecimal opcodes. This helps to identify specific instructions when reading executable code, i.e. during the debugging phase.

The explanations below should help to read the tables on the following pages:

### **Extended Opcodes**

These instructions (ADD[C][B], SUB[C][B], CMP[B], AND[B], [X]OR[B]) are encoded by means of additional bits (1/2) in the operand field of the instruction. For these instructions only the lowest four GPRs, R0 to R3, can be used as indirect address pointers.

 $\begin{array}{llllllllllllllllllllllllllllllllllll$ 

The following instructions are encoded by means of two additional bits in the operand field of the instruction.

Note: The ATOMIC and EXTended instructions are not available in the SAB 8XC166(W) devices.

 $00xx.xxxx_B$ : EXTS or ATOMIC

 $01xx.xxxx_{B}$ : EXTP

 $10xx.xxxx_R$ : EXTSR or EXTR

11xx.xxxx<sub>B</sub>: EXTPR

#### **Conditional JMPR Instructions**

The condition code to be tested for the JMPR instructions is specified by the opcode. Two mnemonic representation alternatives exist for some of the condition codes (condition codes are described in **Table 5**).

#### **BCLR and BSET Instructions**

The position of the bit to be set or to be cleared is specified by the opcode. The operand 'bitoff.n' (n = 0 to 15) refers to a particular bit within a bitaddressable word.

### **Undefined Opcodes**

A hardware trap occurs when one of the undefined opcodes signified by '-' is decoded by the CPU.

Note: The 8XC166(W) devices also do not recognize ATOMIC and EXTended instructions, but rather decode an undefined opcode.

![](_page_27_Picture_1.jpeg)

| Нех | Bytes | Mnemonic           | Operands                                   | Нех | Bytes | Mnemonic            | Operands                                   |
|-----|-------|--------------------|--------------------------------------------|-----|-------|---------------------|--------------------------------------------|
| 00  | 2     | ADD                | Rw, Rw                                     | 10  | 2     | ADDC                | Rw, Rw                                     |
| 01  | 2     | ADDB               | Rb, Rb                                     | 11  | 2     | ADDCB               | Rb, Rb                                     |
| 02  | 4     | ADD                | reg, mem                                   | 12  | 4     | ADDC                | reg, mem                                   |
| 03  | 4     | ADDB               | reg, mem                                   | 13  | 4     | ADDCB               | reg, mem                                   |
| 04  | 4     | ADD                | mem, reg                                   | 14  | 4     | ADDC                | mem, reg                                   |
| 05  | 4     | ADDB               | mem, reg                                   | 15  | 4     | ADDCB               | mem, reg                                   |
| 06  | 4     | ADD                | reg, #data16                               | 16  | 4     | ADDC                | reg, #data16                               |
| 07  | 4     | ADDB               | reg, #data8                                | 17  | 4     | ADDCB               | reg, #data8                                |
| 08  | 2     | ADD <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 | 18  | 2     | ADDC <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 |
| 09  | 2     | ADDB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 | 19  | 2     | ADDCB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 |
| 0A  | 4     | BFLDL              | bitoff, #mask8,<br>#data8                  | 1A  | 4     | BFLDH               | bitoff, #mask8,<br>#data8                  |
| 0B  | 2     | MUL                | Rw, Rw                                     | 1B  | 2     | MULU                | Rw, Rw                                     |
| 0C  | 2     | ROL                | Rw, Rw                                     | 1C  | 2     | ROL                 | Rw, #data4                                 |
| 0D  | 2     | JMPR               | cc_UC, rel                                 | 1D  | 2     | JMPR                | cc_NET, rel                                |
| 0E  | 2     | BCLR               | bitoff.0                                   | 1E  | 2     | BCLR                | bitoff.1                                   |
| 0F  | 2     | BSET               | bitoff.0                                   | 1F  | 2     | BSET                | bitoff.1                                   |

![](_page_28_Picture_1.jpeg)

| Нех | Bytes | Mnemonic           | Operands                                   | Нех | Bytes | Mnemonic            | Operands                                   |
|-----|-------|--------------------|--------------------------------------------|-----|-------|---------------------|--------------------------------------------|
| 20  | 2     | SUB                | Rw, Rw                                     | 30  | 2     | SUBC                | Rw, Rw                                     |
| 21  | 2     | SUBB               | Rb, Rb                                     | 31  | 2     | SUBCB               | Rb, Rb                                     |
| 22  | 4     | SUB                | reg, mem                                   | 32  | 4     | SUBC                | reg, mem                                   |
| 23  | 4     | SUBB               | reg, mem                                   | 33  | 4     | SUBCB               | reg, mem                                   |
| 24  | 4     | SUB                | mem, reg                                   | 34  | 4     | SUBC                | mem, reg                                   |
| 25  | 4     | SUBB               | mem, reg                                   | 35  | 4     | SUBCB               | mem, reg                                   |
| 26  | 4     | SUB                | reg, #data16                               | 36  | 4     | SUBC                | reg, #data16                               |
| 27  | 4     | SUBB               | reg, #data8                                | 37  | 4     | SUBCB               | reg, #data8                                |
| 28  | 2     | SUB <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 | 38  | 2     | SUBC <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 |
| 29  | 2     | SUBB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 | 39  | 2     | SUBCB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 |
| 2A  | 4     | BCMP               | bitaddr, bitaddr                           | ЗА  | 4     | BMOVN               | bitaddr, bitaddr                           |
| 2B  | 2     | PRIOR              | Rw, Rw                                     | 3B  | _     | _                   | _                                          |
| 2C  | 2     | ROR                | Rw, Rw                                     | 3С  | 2     | ROR                 | Rw, #data4                                 |
| 2D  | 2     | JMPR               | cc_EQ, rel or cc_Z, rel                    | 3D  | 2     | JMPR                | cc_NE, rel or cc_NZ, rel                   |
| 2E  | 2     | BCLR               | bitoff.2                                   | 3E  | 2     | BCLR                | bitoff.3                                   |
| 2F  | 2     | BSET               | bitoff.2                                   | 3F  | 2     | BSET                | bitoff.3                                   |

![](_page_29_Picture_1.jpeg)

| Hex | Bytes | Mnemonic           | Operands                                   | Нех | Bytes | Mnemonic           | Operands                                   |
|-----|-------|--------------------|--------------------------------------------|-----|-------|--------------------|--------------------------------------------|
| 40  | 2     | CMP                | Rw, Rw                                     | 50  | 2     | XOR                | Rw, Rw                                     |
| 41  | 2     | СМРВ               | Rb, Rb                                     | 51  | 2     | XORB               | Rb, Rb                                     |
| 42  | 4     | CMP                | reg, mem                                   | 52  | 4     | XOR                | reg, mem                                   |
| 43  | 4     | СМРВ               | reg, mem                                   | 53  | 4     | XORB               | reg, mem                                   |
| 44  | _     | _                  | _                                          | 54  | 4     | XOR                | mem, reg                                   |
| 45  | _     | _                  | _                                          | 55  | 4     | XORB               | mem, reg                                   |
| 46  | 4     | СМР                | reg, #data16                               | 56  | 4     | XOR                | reg, #data16                               |
| 47  | 4     | СМРВ               | reg, #data8                                | 57  | 4     | XORB               | reg, #data8                                |
| 48  | 2     | CMP <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 | 58  | 2     | XOR <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 |
| 49  | 2     | CMPB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 | 59  | 2     | XORB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 |
| 4A  | 4     | BMOV               | bitaddr, bitaddr                           | 5A  | 4     | BOR                | bitaddr, bitaddr                           |
| 4B  | 2     | DIV                | Rw                                         | 5B  | 2     | DIVU               | Rw                                         |
| 4C  | 2     | SHL                | Rw, Rw                                     | 5C  | 2     | SHL                | Rw, #data4                                 |
| 4D  | 2     | JMPR               | cc_V, rel                                  | 5D  | 2     | JMPR               | cc_NV, rel                                 |
| 4E  | 2     | BCLR               | bitoff.4                                   | 5E  | 2     | BCLR               | bitoff.5                                   |
| 4F  | 2     | BSET               | bitoff.4                                   | 5F  | 2     | BSET               | bitoff.5                                   |

![](_page_30_Picture_1.jpeg)

| Нех | Bytes | Mnemonic           | Operands                                   | Нех | Bytes | Mnemonic          | Operands                                   |
|-----|-------|--------------------|--------------------------------------------|-----|-------|-------------------|--------------------------------------------|
| 60  | 2     | AND                | Rw, Rw                                     | 70  | 2     | OR                | Rw, Rw                                     |
| 61  | 2     | ANDB               | Rb, Rb                                     | 71  | 2     | ORB               | Rb, Rb                                     |
| 62  | 4     | AND                | reg, mem                                   | 72  | 4     | OR                | reg, mem                                   |
| 63  | 4     | ANDB               | reg, mem                                   | 73  | 4     | ORB               | reg, mem                                   |
| 64  | 4     | AND                | mem, reg                                   | 74  | 4     | OR                | mem, reg                                   |
| 65  | 4     | ANDB               | mem, reg                                   | 75  | 4     | ORB               | mem, reg                                   |
| 66  | 4     | AND                | reg, #data16                               | 76  | 4     | OR                | reg, #data16                               |
| 67  | 4     | ANDB               | reg, #data8                                | 77  | 4     | ORB               | reg, #data8                                |
| 68  | 2     | AND <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 | 78  | 2     | OR <sup>1)</sup>  | Rw, [Rw +] or<br>Rw, [Rw] or<br>Rw, #data3 |
| 69  | 2     | ANDB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 | 79  | 2     | ORB <sup>1)</sup> | Rb, [Rw +] or<br>Rb, [Rw] or<br>Rb, #data3 |
| 6A  | 4     | BAND               | bitaddr, bitaddr                           | 7A  | 4     | BXOR              | bitaddr, bitaddr                           |
| 6B  | 2     | DIVL               | Rw                                         | 7B  | 2     | DIVLU             | Rw                                         |
| 6C  | 2     | SHR                | Rw, Rw                                     | 7C  | 2     | SHR               | Rw, #data4                                 |
| 6D  | 2     | JMPR               | cc_N, rel                                  | 7D  | 2     | JMPR              | cc_NN, rel                                 |
| 6E  | 2     | BCLR               | bitoff.6                                   | 7E  | 2     | BCLR              | bitoff.7                                   |
| 6F  | 2     | BSET               | bitoff.6                                   | 7F  | 2     | BSET              | bitoff.7                                   |

![](_page_31_Picture_1.jpeg)

| Hex | Bytes | Mnemonic | Operands                 | Нех | Bytes | Mnemonic | Operands                  |
|-----|-------|----------|--------------------------|-----|-------|----------|---------------------------|
| 80  | 2     | CMPI1    | Rw, #data4               | 90  | 2     | CMPI2    | Rw, #data4                |
| 81  | 2     | NEG      | Rw                       | 91  | 2     | CPL      | Rw                        |
| 82  | 4     | CMPI1    | Rw, mem                  | 92  | 4     | CMPI2    | Rw, mem                   |
| 83  | -     | _        | _                        | 93  | _     | _        | _                         |
| 84  | 4     | MOV      | [Rw], mem                | 94  | 4     | MOV      | mem, [Rw]                 |
| 85  | _     | _        | _                        | 95  | _     | _        | _                         |
| 86  | 4     | CMPI1    | Rw, #data16              | 96  | 4     | CMPI2    | Rw, #data16               |
| 87  | 4     | IDLE     | _                        | 97  | 4     | PWRDN    | _                         |
| 88  | 2     | MOV      | [-Rw], Rw                | 98  | 2     | MOV      | Rw, [Rw+]                 |
| 89  | 2     | MOVB     | [-Rw], Rb                | 99  | 2     | MOVB     | Rb, [Rw+]                 |
| 8A  | 4     | JB       | bitaddr, rel             | 9A  | 4     | JNB      | bitaddr, rel              |
| 8B  | _     | _        | _                        | 9B  | 2     | TRAP     | #trap7                    |
| 8C  | -     | _        | _                        | 9C  | 2     | JMPI     | cc, [Rw]                  |
| 8D  | 2     | JMPR     | cc_C, rel or cc_ULT, rel | 9D  | 2     | JMPR     | cc_NC, rel or cc_UGE, rel |
| 8E  | 2     | BCLR     | bitoff.8                 | 9E  | 2     | BCLR     | bitoff.9                  |
| 8F  | 2     | BSET     | bitoff.8                 | 9F  | 2     | BSET     | bitoff.9                  |

![](_page_32_Picture_1.jpeg)

| Hex        | Bytes | Mnemonic | Operands     | Нех | Bytes | Mnemonic | Operands     |
|------------|-------|----------|--------------|-----|-------|----------|--------------|
| A0         | 2     | CMPD1    | Rw, #data4   | ВО  | 2     | CMPD2    | Rw, #data4   |
| A1         | 2     | NEGB     | Rb           | B1  | 2     | CPLB     | Rb           |
| A2         | 4     | CMPD1    | Rw, mem      | B2  | 4     | CMPD2    | Rw, mem      |
| A3         | _     | _        | _            | ВЗ  | _     | _        | _            |
| A4         | 4     | MOVB     | [Rw], mem    | B4  | 4     | MOVB     | mem, [Rw]    |
| <b>A</b> 5 | 4     | DISWDT   | _            | B5  | 4     | EINIT    | _            |
| A6         | 4     | CMPD1    | Rw, #data16  | B6  | 4     | CMPD2    | Rw, #data16  |
| A7         | 4     | SRVWDT   | _            | B7  | 4     | SRST     | _            |
| A8         | 2     | MOV      | Rw, [Rw]     | B8  | 2     | MOV      | [Rw], Rw     |
| A9         | 2     | MOVB     | Rb, [Rw]     | B9  | 2     | MOVB     | [Rw], Rb     |
| AA         | 4     | JBC      | bitaddr, rel | ВА  | 4     | JNBS     | bitaddr, rel |
| AB         | 2     | CALLI    | cc, [Rw]     | BB  | 2     | CALLR    | rel          |
| AC         | 2     | ASHR     | Rw, Rw       | ВС  | 2     | ASHR     | Rw, #data4   |
| AD         | 2     | JMPR     | cc_SGT, rel  | BD  | 2     | JMPR     | cc_SLE, rel  |
| AE         | 2     | BCLR     | bitoff.10    | BE  | 2     | BCLR     | bitoff.11    |
| AF         | 2     | BSET     | bitoff.10    | BF  | 2     | BSET     | bitoff.11    |

![](_page_33_Picture_1.jpeg)

| Нех | Bytes | Mnemonic | Operands            | Нех | Bytes | Mnemonic                                         | Operands                         |
|-----|-------|----------|---------------------|-----|-------|--------------------------------------------------|----------------------------------|
| C0  | 2     | MOVBZ    | Rw, Rb              | D0  | 2     | MOVBS                                            | Rw, Rb                           |
| C1  | _     | _        | _                   | D1  | 2     | ATOMIC <sup>2)</sup><br>or EXTR <sup>2)</sup>    | #irang2                          |
| C2  | 4     | MOVBZ    | reg, mem            | D2  | 4     | MOVBS                                            | reg, mem                         |
| C3  | _     | _        | _                   | D3  | _     | _                                                | _                                |
| C4  | 4     | MOV      | [Rw+#data16],<br>Rw | D4  | 4     | MOV                                              | Rw,<br>[Rw + #data16]            |
| C5  | 4     | MOVBZ    | mem, reg            | D5  | 4     | MOVBS                                            | mem, reg                         |
| C6  | 4     | SCXT     | reg, #data16        | D6  | 4     | SCXT                                             | reg, mem                         |
| C7  | _     | _        | _                   | D7  | 4     | EXTP(R) <sup>2)</sup> ,<br>EXTS(R) <sup>2)</sup> | #pag10,#irang2<br>#seg8, #irang2 |
| C8  | 2     | MOV      | [Rw], [Rw]          | D8  | 2     | MOV                                              | [Rw+], [Rw]                      |
| C9  | 2     | MOVB     | [Rw], [Rw]          | D9  | 2     | MOVB                                             | [Rw+], [Rw]                      |
| CA  | 4     | CALLA    | cc, addr            | DA  | 4     | CALLS                                            | seg, caddr                       |
| СВ  | 2     | RET      | _                   | DB  | 2     | RETS                                             | _                                |
| CC  | 2     | NOP      | _                   | DC  | 2     | EXTP(R) <sup>2)</sup> ,<br>EXTS(R) <sup>2)</sup> | Rw, #irang2                      |
| CD  | 2     | JMPR     | cc_SLT, rel         | DD  | 2     | JMPR                                             | cc_SGE, rel                      |
| CE  | 2     | BCLR     | bitoff.12           | DE  | 2     | BCLR                                             | bitoff.13                        |
| CF  | 2     | BSET     | bitoff.12           | DF  | 2     | BSET                                             | bitoff.13                        |

![](_page_34_Picture_1.jpeg)

| Нех        | Bytes | Mnemonic | Operands            | Нех | Bytes | Mnemonic | Operands              |
|------------|-------|----------|---------------------|-----|-------|----------|-----------------------|
| E0         | 2     | MOV      | Rw, #data4          | FO  | 2     | MOV      | Rw, Rw                |
| E1         | 2     | MOVB     | Rb, #data4          | F1  | 2     | MOVB     | Rb, Rb                |
| E2         | 4     | PCALL    | reg, caddr          | F2  | 4     | MOV      | reg, mem              |
| E3         | _     | _        | _                   | F3  | 4     | MOVB     | reg, mem              |
| E4         | 4     | MOVB     | [Rw+#data16],<br>Rb | F4  | 4     | MOVB     | Rb,<br>[Rw + #data16] |
| E5         | _     | _        | _                   | F5  | _     | _        | _                     |
| E6         | 4     | MOV      | reg, #data16        | F6  | 4     | MOV      | mem, reg              |
| <b>E</b> 7 | 4     | MOVB     | reg, #data8         | F7  | 4     | MOVB     | mem, reg              |
| E8         | 2     | MOV      | [Rw], [Rw+]         | F8  | _     | _        | _                     |
| E9         | 2     | MOVB     | [Rw], [Rw+]         | F9  | _     |          | _                     |
| EA         | 4     | JMPA     | cc, caddr           | FA  | 4     | JMPS     | seg, caddr            |
| EB         | 2     | RETP     | reg                 | FB  | 2     | RETI     | _                     |
| EC         | 2     | PUSH     | reg                 | FC  | 2     | POP      | reg                   |
| ED         | 2     | JMPR     | cc_UGT, rel         | FD  | 2     | JMPR     | cc_ULE, rel           |
| EE         | 2     | BCLR     | bitoff.14           | FE  | 2     | BCLR     | bitoff.15             |
| EF         | 2     | BSET     | bitoff.14           | FF  | 2     | BSET     | bitoff.15             |

![](_page_35_Picture_1.jpeg)

### 5 Detailed Description

This chapter describes each instruction in detail. The example further down on this page lists the elements of a description and demonstrates how the information given for each instruction is arranged.

The next pages explain the elements of an instruction description (see example), and then all instructions are listed individually. The instructions are ordered alphabetically.

| MNEM        | Short             | Short Description               |            |          |               |            |  |
|-------------|-------------------|---------------------------------|------------|----------|---------------|------------|--|
| Syntax      | MNEM              | MNEM operand(s)                 |            |          |               |            |  |
| Operation   | definitio         | on in ps                        | eudo-co    | ode      |               |            |  |
| [Data Types | BIT   B           | BIT   BYTE   WORD   DOUBLEWORD] |            |          |               |            |  |
| Description | Verbal            | descrip                         | tion of t  | he instr | <i>uction</i> | 's effect. |  |
| [Note       | Additio           | Additional hints.]              |            |          |               |            |  |
| Condition   | E                 | Z                               | V          | С        | N             |            |  |
| Flags       | ?                 | ?                               | ?          | ?        | ?             |            |  |
|             | E Effe            | ect of th                       | nis instru | uction o | n flag i      | E.         |  |
|             | <b>Z</b> Effe     | ect of th                       | is instru  | uction o | n flag 🏻      | <i>Z</i> . |  |
|             | <b>V</b> Effe     | ect of th                       | is instru  | uction o | n flag        | V.         |  |
|             | C Effe            | ect of th                       | nis instru | uction o | n flag (      | <i>C</i> . |  |
|             | N Effe            | ect of th                       | is instru  | uction o | n flag i      | N.         |  |
| Addressing  | Mnemo             | Mnemonic Format                 |            |          |               |            |  |
| Modes       | <i>MNEM</i><br>[] | ope                             | rand(s)    |          |               | encoding   |  |

![](_page_36_Picture_1.jpeg)

#### **Instruction Name**

MNEM Specifies the mnemonic opcode of the instruction in oversized bold lettering for easy reference. The mnemonics have been chosen with regard to the particular operation which is performed by the specified instruction. These mnemonics are also used by tools such as assemblers.

Short D. Short description which is also used in the compact tables on the previous pages.

### **Syntax**

Specifies the mnemonic opcode and the required formal operands of the instruction as used in the following subsection 'Operation'. There are instructions with either none, one, two or three operands, which must be separated from each other by commas:

```
MNEMONIC {op1 {,op2 {,op3 } } }
```

The syntax for the actual operands of an instruction depends on the selected addressing mode. All of the addressing modes available are summarized at the end of each single instruction description. In contrast to the syntax for the instructions described in the following, the assembler provides much more flexibility in writing C166 Family programs (e.g. by generic instructions and by automatically selecting appropriate addressing modes whenever possible), and thus it eases the use of the instruction set.

Note: For more information about this item please refer to the Assembler manual.

### **Operation**

This part presents a logical description of the operation performed by an instruction by means of a symbolic formula or a high level language construct (pseudo code). The following symbols are used to represent data movement, arithmetic or logical operators:

| Diadic Operation  | ons: (opX) | operator (opY)                                |
|-------------------|------------|-----------------------------------------------|
| $\leftarrow$      | (opY) is   | MOVED into (opX)                              |
| +                 | (opX) is   | ADDED to (opY)                                |
| -                 | (opY) is   | SUBTRACTED from (opX)                         |
| ×                 | (opX) is   | MULTIPLIED by (opY)                           |
| ÷                 | (opX) is   | <b>DIVIDED</b> by (opY)                       |
| $\wedge$          | (opX) is   | logically ANDed with (opY)                    |
| V                 | (opX) is   | logically <b>OR</b> ed with (opY)             |
| $\oplus$          | (opX) is   | logically <b>EXCLUSIVELY OR</b> ed with (opY) |
| $\Leftrightarrow$ | (opX) is   | COMPARED against (opY)                        |
| mod               | (opX) is   | divided MODULO (opY)                          |

![](_page_37_Picture_1.jpeg)

Monadic Operations: operator (opX)

¬ (opX) is logically COMPLEMENTED

Missing or existing parentheses signify whether the used operand specifies an immediate constant value, an address or a pointer to an address, as follows:

opX Specifies the immediate constant value of opX

(opX) Specifies the contents of opX

 $(opX_n)$  Specifies the contents of bit n of opX

((opX)) Specifies the contents of the contents of opX,

i.e. opX is used as pointer to the actual operand

The following operands will also be used in the operational description:

CP Context Pointer register

CSP Code Segment Pointer register

MD Multiply/Divide register (32 bits wide),

consists of (16-bit) registers MDH and MDL

MDL, MDH Multiply/Divide Low and High registers

(both 16 bits wide)

PSW Program Status Word register
SP System Stack Pointer register
SYSCON System Configuration register

C Carry condition flag in register PSW

V Overflow condition flag in register PSW

SGTDIS Segmentation Disable bit in register SYSCON

count Temporary variable for an intermediate storage of

the number of shift or rotate cycles which remain to

complete the shift or rotate operation

tmp Temporary variable for an intermediate result 0, 1, 2, ... Constant values due to the data format of the

specified operation

![](_page_38_Picture_1.jpeg)

### **Data Types**

This part specifies the particular data type according to the instruction. Basically, the following data types are possible:

BIT, BYTE, WORD, DOUBLEWORD

Except for those instructions which extend byte data to word data, all instructions have only one particular data type. Note that the data types mentioned in this subsection do not consider accesses to indirect address pointers or to the system stack which are always performed with word data. Moreover, no data type is specified for System Control Instructions and for those of the branch instructions which do not access any explicitly addressed data.

#### **Description**

This part provides a brief verbal description of the action that is executed by the respective instruction. Also hints are given on using the instruction itself, its operands, and its flags.

#### **Note**

In some cases additional notes point out special circumstances. These notes shall help the user to avoid faulty operation of his/her software.

Conditional instructions refer here to the condition codes listed in Table 5.

### **Condition Flags**

This part reflects the state of the N, C, V, Z and E flags in the PSW register which is the state after execution of the corresponding instruction, except if the PSW register itself was specified as the destination operand of that instruction (see Note).

The condition flags are displayed in the way they appear in register PSW:

#### **PSW**

![](_page_38_Figure_16.jpeg)

The resulting state of the flags is represented by symbols as follows:

![](_page_39_Picture_1.jpeg)

### **Symbolic Settings for Condition Flags**

The flag value depends on the result of the instruction and is set/cleared according to the following standard rules:

N = 1: MSB of the result is set N = 0: MSB of the result is not set

C = 1: Carry occurred during operationC = 0 No Carry occurred during operation

V = 1: Arithmetic Overflow occurred during operation
 V = 0 No Arithmetic Overflow occurred during operation

Z = 1: Result equals zero

Z = 0: Result does not equal zero

E = 1: Source operand represents the...

E = 0: Source operand does not represent the...

...lowest negative number (8000<sub>H</sub>/80<sub>H</sub> for word/byte data)

'S' The flag is set/cleared according to special rules which deviate from

the described standard. For more details see instruction pages

(below) or the ALU status flags description.

'-' The flag is not affected by the operation.

'0' The flag is cleared by the operation.

'NOR'
The flag contains the logical NOR of the two specified bit operands.

'AND'
The flag contains the logical AND of the two specified bit operands.

'OR'
The flag contains the logical OR of the two specified bit operands.

'XOR'

'B' The flag contains the original value of the specified bit operand.

'B' The flag contains the complemented value of the specified bit

operand.

Note: If the PSW register was specified as the destination operand of an instruction, the condition flags can not be interpreted as just described, because the PSW register is modified depending on the data format of the instruction as follows:

For word operations, register PSW is overwritten with the word result. For byte operations, the non-addressed byte is cleared and the addressed byte is overwritten. For bit or bit-field operations on the PSW register, only the specified bits are modified. Supposed that the condition flags were not selected as destination bits, they stay unchanged. This means that they keep the state after execution of the previous instruction.

In any case, if the PSW was the destination operand of an instruction, the PSW flags do NOT represent the condition flags of this instruction as usual.

![](_page_40_Picture_1.jpeg)

### **Addressing Modes**

This part specifies, which combinations of different addressing modes are available for the required operands. Mostly, the selected addressing mode combination is specified by the opcode of the corresponding instruction. However, there are some arithmetic and logical instructions where the addressing mode combination is not specified by the (identical) opcodes but by particular bits within the operand field.

The addressing mode entries are made up of three elements:

- **Mnemonic** shows an example of what operands the respective instruction will accept.
- **Format** specifies the format of the instruction (symbols are explained on **Page 37**) as it is represented in the assembler listing. The figure below shows the reference between the instruction format representation of the assembler and the corresponding internal organization of such an instruction format (N = nibble = 4 bits).
- **Number of Bytes** specifies the size of an instruction in bytes. All C166 Family instructions consist of 2 bytes or 4 bytes (single word or double word instruction).

![](_page_40_Figure_9.jpeg)

Figure 1 Instruction Format Representation

![](_page_41_Picture_1.jpeg)

### **Symbols for the Instruction Format**

00<sub>H</sub> through FF<sub>H</sub> Instruction Opcodes (Hex)

0, 1 Constant Values (bits)

:.... Each of the 4 characters immediately following a colon represents a

single bit

:..ii 2-bit short GPR address (Rw<sub>i</sub>)

SS Code segment number 'seg' (byte value)<sup>1)</sup>

:..##2-bit immediate constant (#irang2):.###3-bit immediate constant (#data3)

c 4-bit condition code specification (cc), see also **Table 5** 

n 4-bit short GPR address (Rw<sub>n</sub> or Rb<sub>n</sub>) m 4-bit short GPR address (Rw<sub>m</sub> or Rb<sub>m</sub>)

q 4-bit position of the source bit within the word specified by QQ

z 4-bit position of the destination bit within the word specified by ZZ

# 4-bit immediate constant (#data4)

t:ttt0 7-bit trap number (#trap7)

QQ8-bit word address of the source bit (bitoff)rr8-bit relative target address word offset (rel)

RR 8-bit word address (reg)

ZZ 8-bit word address of the destination bit (bitoff)

## 8-bit immediate constant (#data8)

## xx 8-bit immediate constant

(represented by #data16, where byte xx is not significant)

@ @ 8-bit immediate constant (#mask8)

MM MM 16-bit address (mem or caddr; low byte, high byte)

## ## 16-bit immediate constant (#data16; low byte, high byte)

<sup>&</sup>lt;sup>1)</sup> For the SAB 8xC166 devices the segment number is a 2-bit value (:..ss) due to the smaller addressing range of 256 KByte (compared to 16 MByte).

![](_page_42_Picture_1.jpeg)

#### **Condition Code**

Some instructions (JUMP, CALL, ...) are executed only if a specific condition is true, and are skipped otherwise. The condition which has to be fulfilled for the execution of the respective instruction is specified in the so-called condition code. **Table 5** summarizes the 16 possible condition codes that can be used within Call and Branch instructions. The table shows the mnemonic abbreviations, the test that is executed for a specific condition, and the internal representation by a 4-bit number.

**Table 5** Condition Code Encoding

| Condition Code<br>Mnemonic (cc) | Test                   | Description                    | Encoding (c)   |
|---------------------------------|------------------------|--------------------------------|----------------|
| cc_UC                           | 1 = 1                  | Unconditional                  | 0 <sub>H</sub> |
| cc_Z                            | Z = 1                  | Zero                           | 2 <sub>H</sub> |
| cc_NZ                           | Z = 0                  | Not zero                       | 3 <sub>H</sub> |
| cc_V                            | V = 1                  | Overflow                       | 4 <sub>H</sub> |
| cc_NV                           | V = 0                  | No overflow                    | 5 <sub>H</sub> |
| cc_N                            | N = 1                  | Negative                       | 6 <sub>H</sub> |
| cc_NN                           | N = 0                  | Not negative                   | 7 <sub>H</sub> |
| cc_C                            | C = 1                  | Carry                          | 8 <sub>H</sub> |
| cc_NC                           | C = 0                  | No carry                       | 9 <sub>H</sub> |
| cc_EQ                           | Z = 1                  | Equal                          | 2 <sub>H</sub> |
| cc_NE                           | Z = 0                  | Not equal                      | 3 <sub>H</sub> |
| cc_ULT                          | C = 1                  | Unsigned less than             | 8 <sub>H</sub> |
| cc_ULE                          | (Z∨C) = 1              | Unsigned less than or equal    | F <sub>H</sub> |
| cc_UGE                          | C = 0                  | Unsigned greater than or equal | 9 <sub>H</sub> |
| cc_UGT                          | (Z∨C) = 0              | Unsigned greater than          | E <sub>H</sub> |
| cc_SLT                          | (N⊕V) = 1              | Signed less than               | C <sub>H</sub> |
| cc_SLE                          | (Z∨(N⊕V)) = 1          | Signed less than or equal      | B <sub>H</sub> |
| cc_SGE                          | (N⊕V) = 0              | Signed greater than or equal   | D <sub>H</sub> |
| cc_SGT                          | $(Z\lor(N\oplus V))=0$ | Signed greater than            | A <sub>H</sub> |
| cc_NET                          | (Z∨E) = 0              | Not equal AND not end of table | 1 <sub>H</sub> |

![](_page_43_Picture_1.jpeg)

#### **Peculiarities of the ATOMIC and EXTended Instructions**

These instructions (ATOMIC, EXTR, EXTP, EXTS, EXTPR, EXTSR) disable standard and PEC interrupts and class A traps during a sequence of the following 1 ... 4 instructions. The length of the sequence is determined by an operand (op1 or op2, depending on the instruction). The EXTended instruction additionally change the addressing mechanism during this sequence (see detailed instruction description).

The ATOMIC and EXTended instructions become active immediately, i.e. no interrupt/ PEC request or ClassA trap is accepted during the execution of the ATOMIC (EXTx) instruction and the following locked instructions (see #irang2). All instructions requiring multiple cycles or hold states to be executed are regarded as one instruction in this sense. Any instruction type can be used with the ATOMIC and EXTended instructions.

**ATTENTION:** When a ClassB trap interrupts an ATOMIC or EXTended sequence, this sequence is terminated, the interrupt lock is removed and the standard condition is restored, before the trap routine is executed! The remaining instructions of the terminated sequence that are executed after returning from the trap routine will run under standard conditions!

Within a ClassA or ClassB trap service routine EXTend instructions do not work (i.e. override the DPP mechanism) as long as any of the ClassB trap flags is set.

**ATTENTION:** There is only ONE counter to control the length of an ATOMIC or EXTend sequence, i.e. issuing an ATOMIC or EXTend instruction within a sequence will reload the counter with the value of the new instruction. ATOMIC and EXTend instructions can be nested to generate longer locked sequences.

When using the ATOMIC and EXTended instructions with other system control or branch instructions, please note that the counter counts any executed instruction.

Note: The ATOMIC and EXTended instructions are not available in the SAB 8XC166(W) devices.

User's Manual V2.0, 2001-03

![](_page_44_Picture_1.jpeg)

#### **Peculiarities of Multiplication and Division Instructions**

Multiplications and divisions are interruptible to optimize the interrupt response time. Bit MDC.MDRIU indicates that register MD is currently in use, bit PSW.MULIP indicates an interrupted multiplication. Chapter "System Programming" of the respective User's Manual describes the handling of interrupted multiplications and divisions.

Bit MDRIU is set at the start of a MUL instruction (not when the instruction is resumed) or upon a write to register MDL or MDH. Bit MDRIU is cleared upon a read from register MDL. Bit MDRIU is affected by a write to register MDC, of course.

When the MUL instruction is interrupted, bit MULIP is set in the PSW of the interrupting routine, i.e. after pushing the previous PSW onto stack. When returning from the interrupt bit MULIP must be set/cleared according to the next executed instruction.

Note: For the first instruction after RETI bit MULIP = '1' prevents the multiplicand from being reloaded (the intermediate result resides in MD).

This mechanism will disturb the operand fetching if another instruction (than the continued multiplication) is executed after RETI.

For standard interrupt handling (return to interrupted multiplication) this is done automatically. Task schedulers must keep track of interrupted multiplications in each task.

The following pages of this section contain a detailed description of each instruction of the C166 Family in alphabetical order.

User's Manual 40 V2.0, 2001-03

![](_page_45_Picture_1.jpeg)

### ADD Integer Addition ADD

Syntax ADD op1, op2

**Operation**  $(op1) \leftarrow (op1) + (op2)$ 

**Data Types** WORD

**Description** Performs a 2's complement binary addition of the source operand

specified by op2 and the destination operand specified by op1. The

sum is then stored in op1.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | * | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic overflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- C Set if a carry is generated from the most significant bit of the specified data type. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemonio | C                                    | Format      | Bytes |
|----------|--------------------------------------|-------------|-------|
| ADD      | Rw <sub>n</sub> , Rw <sub>m</sub>    | 00 nm       | 2     |
| ADD      | Rw <sub>n</sub> , [Rw <sub>i</sub> ] | 08 n:10ii   | 2     |
| ADD      | $Rw_n$ , $[Rw_i+]$                   | 08 n:11ii   | 2     |
| ADD      | Rw <sub>n</sub> , #data3             | 08 n:0###   | 2     |
| ADD      | reg, #data16                         | 06 RR ## ## | 4     |
| ADD      | reg, mem                             | 02 RR MM MM | 4     |
| ADD      | mem, reg                             | 04 RR MM MM | 4     |

![](_page_46_Picture_1.jpeg)

### **ADDB** Integer Addition

**ADDB** 

Syntax ADDB op1, op2

**Operation**  $(op1) \leftarrow (op1) + (op2)$ 

**Data Types** BYTE

**Description** Performs a 2's complement binary addition of the source operand

specified by op2 and the destination operand specified by op1. The

sum is then stored in op1.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | * | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic overflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- C Set if a carry is generated from the most significant bit of the specified data type. Cleared otherwise.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemonic |                                      | Format      | Bytes |
|----------|--------------------------------------|-------------|-------|
| ADDB     | Rb <sub>n</sub> , Rb <sub>m</sub>    | 01 nm       | 2     |
| ADDB     | Rb <sub>n</sub> , [Rw <sub>i</sub> ] | 09 n:10ii   | 2     |
| ADDB     | $Rb_n$ , $[Rw_i+]$                   | 09 n:11ii   | 2     |
| ADDB     | Rb <sub>n</sub> , #data3             | 09 n:0###   | 2     |
| ADDB     | reg, #data8                          | 07 RR ## xx | 4     |
| ADDB     | reg, mem                             | 03 RR MM MM | 4     |
| ADDB     | mem, reg                             | 05 RR MM MM | 4     |

![](_page_47_Picture_1.jpeg)

### **ADDC**

### **Integer Addition with Carry**

**ADDC** 

Syntax ADDC op1, op2

**Operation**  $(op1) \leftarrow (op1) + (op2) + (C)$ 

Data Types WORD

**Description** 

Performs a 2's complement binary addition of the source operand specified by op2, the destination operand specified by op1 and the previously generated carry bit. The sum is then stored in op1. This instruction can be used to perform multiple precision arithmetic.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | S | * | * | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero and the previous Z flag was set. Cleared otherwise.
- V Set if an arithmetic overflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- C Set if a carry is generated from the most significant bit of the specified data type. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemor | nic                               | Format      | Bytes |
|--------|-----------------------------------|-------------|-------|
| ADDC   | Rw <sub>n</sub> , Rw <sub>m</sub> | 10 nm       | 2     |
| ADDC   | $Rw_n$ , $[Rw_i]$                 | 18 n:10ii   | 2     |
| ADDC   | $Rw_n$ , $[Rw_i+]$                | 18 n:11ii   | 2     |
| ADDC   | Rw <sub>n</sub> , #data3          | 18 n:0###   | 2     |
| ADDC   | reg, #data16                      | 16 RR ## ## | 4     |
| ADDC   | reg, mem                          | 12 RR MM MM | 4     |
| ADDC   | mem, reg                          | 14 RR MM MM | 4     |

![](_page_48_Picture_1.jpeg)

### **ADDCB**

### **Integer Addition with Carry**

**ADDCB** 

Syntax ADDCB op1, op2

**Operation**  $(op1) \leftarrow (op1) + (op2) + (C)$ 

**Data Types** BYTE

**Description** 

Performs a 2's complement binary addition of the source operand specified by op2, the destination operand specified by op1 and the previously generated carry bit. The sum is then stored in op1. This instruction can be used to perform multiple precision arithmetic.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | S | * | * | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero and the previous Z flag was set. Cleared otherwise.
- V Set if an arithmetic overflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- C Set if a carry is generated from the most significant bit of the specified data type. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemoni | ic                                    | Format      | Bytes |
|---------|---------------------------------------|-------------|-------|
| ADDCB   | Rb <sub>n</sub> , Rb <sub>m</sub>     | 11 nm       | 2     |
| ADDCB   | Rb <sub>n</sub> , [Rw <sub>i</sub> ]  | 19 n:10ii   | 2     |
| ADDCB   | Rb <sub>n</sub> , [Rw <sub>i</sub> +] | 19 n:11ii   | 2     |
| ADDCB   | Rb <sub>n</sub> , #data3              | 19 n:0###   | 2     |
| ADDCB   | reg, #data8                           | 17 RR ## xx | 4     |
| ADDCB   | reg, mem                              | 13 RR MM MM | 4     |
| ADDCB   | mem, reg                              | 15 RR MM MM | 4     |
|         |                                       |             |       |

![](_page_49_Picture_1.jpeg)

AND Logical AND

Syntax AND op1, op2

**Operation**  $(op1) \leftarrow (op1) \land (op2)$ 

Data Types WORD

**Description** Performs a bitwise logical AND of the source operand specified by

op2 and the destination operand specified by op1. The result is

then stored in op1.

| Е | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | nic                                   | Format      | Bytes |
|------------|-------|---------------------------------------|-------------|-------|
| Modes      | AND   | Rw <sub>n</sub> , Rw <sub>m</sub>     | 60 nm       | 2     |
|            | AND   | Rw <sub>n</sub> , [Rw <sub>i</sub> ]  | 68 n:10ii   | 2     |
|            | AND   | Rw <sub>n</sub> , [Rw <sub>i</sub> +] | 68 n:11ii   | 2     |
|            | AND   | Rw <sub>n</sub> , #data3              | 68 n:0###   | 2     |
|            | AND   | reg, #data16                          | 66 RR ## ## | 4     |
|            | AND   | reg, mem                              | 62 RR MM MM | 4     |
|            | AND   | mem, reg                              | 64 RR MM MM | 4     |

![](_page_50_Picture_1.jpeg)

ANDB Logical AND ANDB

Syntax ANDB op1, op2

**Operation**  $(op1) \leftarrow (op1) \land (op2)$ 

**Data Types** BYTE

**Description** Performs a bitwise logical AND of the source operand specified by

op2 and the destination operand specified by op1. The result is

then stored in op1.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemor | nic                               | Format      | Bytes |
|------------|--------|-----------------------------------|-------------|-------|
| Modes      | ANDB   | Rb <sub>n</sub> , Rb <sub>m</sub> | 61 nm       | 2     |
|            | ANDB   | $Rb_n$ , $[Rw_i]$                 | 69 n:10ii   | 2     |
|            | ANDB   | $Rb_n$ , $[Rw_i+]$                | 69 n:11ii   | 2     |
|            | ANDB   | Rb <sub>n</sub> , #data3          | 69 n:0###   | 2     |
|            | ANDB   | reg, #data8                       | 67 RR ## xx | 4     |
|            | ANDB   | reg, mem                          | 63 RR MM MM | 4     |
|            | ANDB   | mem. rea                          | 65 RR MM MM | 4     |

![](_page_51_Picture_1.jpeg)

### **ASHR**

### **Arithmetic Shift Right**

### **ASHR**

Syntax

**Operation** 

$$(count) \leftarrow (op2)$$

$$(V) \leftarrow 0$$

$$(C) \leftarrow 0$$

DO WHILE (count)  $\neq$  0

$$(V) \leftarrow (C) \lor (V)$$

$$(C) \leftarrow (op1_0)$$

$$(op1_n) \leftarrow (op1_{n+1}) [n = 0 ... 14]$$

$$(count) \leftarrow (count) - 1$$

**END WHILE** 

### **Data Types**

#### **WORD**

### **Description**

Arithmetically shifts the destination word operand op1 right by as many times as specified in the source operand op2. To preserve the sign of the original operand op1, the most significant bits of the result are filled with zeros if the original MSB was a 0 or with ones if the original MSB was a 1. The Overflow flag is used as a Rounding flag. The LSB is shifted into the Carry. Only shift values between 0 and 15 are allowed. When using a GPR as the count control, only the least significant 4 bits are used.

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | S | S | * |

- E Always cleared.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if in any cycle of the shift operation a 1 is shifted out of the carry flag. Cleared for a shift count of zero.
- C The carry flag is set according to the last LSB shifted out of op1. Cleared for a shift count of zero.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemor | nic                               | Format | Bytes |
|------------|--------|-----------------------------------|--------|-------|
| Modes      | ASHR   | Rw <sub>n</sub> , Rw <sub>m</sub> | AC nm  | 2     |
|            | ASHR   | Rw <sub>n</sub> , #data4          | BC #n  | 2     |

![](_page_52_Picture_1.jpeg)

### **ATOMIC** Begin ATOMIC Sequence

**ATOMIC** 

Syntax ATOMIC op1

**Operation** (count)  $\leftarrow$  (op1) [1  $\leq$  op1  $\leq$  4]

Disable interrupts and Class A traps

DO WHILE ((count)  $\neq$  0 AND Class\_B\_trap\_condition  $\neq$  TRUE)

Next Instruction (count)  $\leftarrow$  (count) - 1

END WHILE (count) = 0

Enable interrupts and traps

#### **Description**

Causes standard and PEC interrupts and class A hardware traps to be disabled for a specified number of instructions. The ATOMIC instruction becomes immediately active such that no additional NOPs are required.

Depending on the value of op1, the period of validity of the ATOMIC sequence extends over the sequence of the next 1 to 4 instructions being executed after the ATOMIC instruction. All instructions requiring multiple cycles or hold states to be executed are regarded as one instruction in this sense. Any instruction type

can be used with the ATOMIC instruction.

Note Please see additional notes on Page 39.

The ATOMIC instruction is not available in the SAB 8XC166(W)

devices.

Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| - | - | - | - | - |

**E** Not affected.

**Z** Not affected.

**V** Not affected.

C Not affected.

Not affected.

AddressingMnemonicFormatBytesModesATOMIC #irang2D1:00##-02

![](_page_53_Picture_1.jpeg)

BAND Bit Logical AND BAND

Syntax BAND op1, op2

**Operation**  $(op1) \leftarrow (op1) \land (op2)$ 

**Data Types** BIT

**Description** Performs a single bit logical AND of the source bit specified by op2

and the destination bit specified by op1. The result is then stored

in op1.

Condition Flags

| E | Z   | V  | C   | N   |
|---|-----|----|-----|-----|
| 0 | NOR | OR | AND | XOR |

**E** Always cleared.

**Z** Contains the logical NOR of the two specified bits.

V Contains the logical OR of the two specified bits.

**C** Contains the logical AND of the two specified bits.

**N** Contains the logical XOR of the two specified bits.

AddressingMnemonicFormatBytesModesBANDbitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>6A QQ ZZ qz4

![](_page_54_Picture_1.jpeg)

BCLR Bit Clear BCLR

Syntax BCLR op1

**Operation**  $(op1) \leftarrow 0$ 

**Data Types** BIT

**Description** Clears the bit specified by op1. This instruction is primarily used for

peripheral and system control.

**Condition Flags** 

| E | Z              | V | С | N |
|---|----------------|---|---|---|
| 0 | $\overline{B}$ | 0 | 0 | В |

E Always cleared.

**Z** Contains the logical negation of the previous state of the specified bit.

V Always cleared.

**C** Always cleared.

N Contains the previous state of the specified bit.

AddressingMnemonicFormatBytesModesBCLRbitaddr $_{Q,q}$ qE QQ2

![](_page_55_Picture_1.jpeg)

BCMP Bit to Bit Compare BCMP

Syntax BCMP op1, op2

**Operation**  $(op1) \Leftrightarrow (op2)$ 

**Data Types** BIT

**Description** Performs a single bit comparison of the source bit specified by

operand op1 to the source bit specified by operand op2. No result is written by this instruction. Only the condition codes are updated.

**Note** The meaning of the condition flags for the BCMP instruction is

different from the meaning of the flags for the other compare

instructions.

**Condition Flags** 

| Е | Z   | V  | С   | N   |
|---|-----|----|-----|-----|
| 0 | NOR | OR | AND | XOR |

E Always cleared.

**Z** Contains the logical NOR of the two specified bits.

V Contains the logical OR of the two specified bits.

**C** Contains the logical AND of the two specified bits.

N Contains the logical XOR of the two specified bits.

AddressingMnemonicFormatBytesModesBCMPbitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>2A QQ ZZ qz4

![](_page_56_Picture_1.jpeg)

**BFLDH** Bit Field High Byte

**BFLDH** 

Syntax BFLDH op1, op2, op3

**Operation**  $(tmp) \leftarrow (op1)$ 

(high byte (tmp))  $\leftarrow$  ((high byte (tmp)  $\land \neg op2$ )  $\lor op3$ )

 $(op1) \leftarrow (tmp)$ 

**Data Types** WORD

**Description** Replaces those bits in the high byte of the destination word

operand op1 which are selected by a '1' in the AND mask op2 with the bits at the corresponding positions in the OR mask specified by

op3.

**Note** op1 bits which shall remain unchanged must have a '0' in the

respective bit of both the AND mask op2 and the OR mask op3. Otherwise a '1' in op3 will set the corresponding op1 bit (see

"Operation").

If the target operand (op1) features bit-protection only the bits marked by a '1' in the mask operand (op2) will be updated.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | 0 | 0 | * |

**E** Always cleared.

**Z** Set if the word result equals zero. Cleared otherwise.

**V** Always cleared.

**C** Always cleared.

**N** Set if the most significant bit of the word result is set. Cleared otherwise.

**Addressing** Mnemonic Format Bytes

Modes BFLDH bitoff<sub>O</sub>, #mask8, #data8 1A QQ ## @ @ 4

![](_page_57_Picture_1.jpeg)

### **BFLDL** Bit Field Low Byte

**BFLDL** 

Syntax BFLDL op1, op2, op3

**Operation**  $(tmp) \leftarrow (op1)$ 

(low byte (tmp))  $\leftarrow$  ((low byte (tmp)  $\land \neg op2$ )  $\lor op3$ )

 $(op1) \leftarrow (tmp)$ 

Data Types WORD

**Description** Replaces those bits in the low byte of the destination word operand

op1 which are selected by a '1' in the AND mask op2 with the bits at the corresponding positions in the OR mask specified by op3.

Note op1 bits which shall remain unchanged must have a '0' in the

respective bit of both the AND mask op2 and the OR mask op3. Otherwise a '1' in op3 will set the corresponding op1 bit (see

"Operation").

If the target operand (op1) features bit-protection only the bits marked by a '1' in the mask operand (op2) will be updated.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | 0 | 0 | * |

E Always cleared.

**Z** Set if the word result equals zero. Cleared otherwise.

V Always cleared.

C Always cleared.

**N** Set if the most significant bit of the word result is set. Cleared otherwise.

# AddressingMnemonicFormatBytesModesBFLDLbitoffQ, #mask8, #data80A QQ @@ ##4

![](_page_58_Picture_1.jpeg)

BMOV Bit to Bit Move BMOV

Syntax BMOV op1, op2

**Operation**  $(op1) \leftarrow (op2)$ 

**Data Types** BIT

**Description** Moves a single bit from the source operand specified by op2 into

the destination operand specified by op1. The source bit is

examined and the flags are updated accordingly.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | B | 0 | 0 | В |

**E** Always cleared.

**Z** Contains the logical negation of the previous state of the source bit.

V Always cleared.

**C** Always cleared.

N Contains the previous state of the source bit.

AddressingMnemonicFormatBytesModesBMOVbitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>4A QQ ZZ qz4

![](_page_59_Picture_1.jpeg)

**BMOVN** Bit to Bit Move and Negate

**BMOVN** 

Syntax BMOVN op1, op2

**Operation**  $(op1) \leftarrow \neg (op2)$ 

**Data Types** BIT

**Description** Moves the complement of a single bit from the source operand

specified by op2 into the destination operand specified by op1. The source bit is examined and the flags are updated accordingly.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | B | 0 | 0 | В |

E Always cleared.

**Z** Contains the logical negation of the previous state of the source bit.

V Always cleared.

**C** Always cleared.

N Contains the previous state of the source bit.

AddressingMnemonicFormatBytesModesBMOVN bitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>3A QQ ZZ qz4

![](_page_60_Picture_1.jpeg)

BOR Bit Logical OR BOR

Syntax BOR op1, op2

**Operation**  $(op1) \leftarrow (op1) \lor (op2)$ 

**Data Types** BIT

**Description** Performs a single bit logical OR of the source bit specified by

operand op2 with the destination bit specified by operand op1. The

ORed result is then stored in op1.

Condition Flags

| E | Z   | V  | C   | N   |
|---|-----|----|-----|-----|
| 0 | NOR | OR | AND | XOR |

E Always cleared.

**Z** Contains the logical NOR of the two specified bits.

V Contains the logical OR of the two specified bits.

**C** Contains the logical AND of the two specified bits.

N Contains the logical XOR of the two specified bits.

AddressingMnemonicFormatBytesModesBORbitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>5A QQ ZZ qz4

![](_page_61_Picture_1.jpeg)

BSET Bit Set BSET

Syntax BSET op1

**Operation**  $(op1) \leftarrow 1$ 

**Data Types** BIT

**Description** Sets the bit specified by op1. This instruction is primarily used for

peripheral and system control.

**Condition Flags** 

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | B | 0 | 0 | В |

E Always cleared.

**Z** Contains the logical negation of the previous state of the specified bit.

**V** Always cleared.

**C** Always cleared.

N Contains the previous state of the specified bit.

AddressingMnemonicFormatBytesModesBSETbitaddr $_{Q,q}$ qF QQ2

![](_page_62_Picture_1.jpeg)

BXOR Bit Logical XOR BXOR

Syntax BXOR op1, op2

**Operation**  $(op1) \leftarrow (op1) \oplus (op2)$ 

**Data Types** BIT

**Description** Performs a single bit logical EXCLUSIVE OR of the source bit

specified by operand op2 with the destination bit specified by

operand op1. The XORed result is then stored in op1.

Condition Flags

| E | Z   | V  | С   | N   |
|---|-----|----|-----|-----|
| 0 | NOR | OR | AND | XOR |

E Always cleared.

**Z** Contains the logical NOR of the two specified bits.

V Contains the logical OR of the two specified bits.

**C** Contains the logical AND of the two specified bits.

N Contains the logical XOR of the two specified bits.

AddressingMnemonicFormatBytesModesBXORbitaddr<sub>Z,z</sub>, bitaddr<sub>Q,q</sub>7A QQ ZZ qz4

![](_page_63_Picture_1.jpeg)

**CALLA** 

### **Call Subroutine Absolute**

CALLA

**Syntax** 

CALLA op1, op2

**Operation** 

IF (op1) THEN (SP)  $\leftarrow$  (SP) - 2 ((SP))  $\leftarrow$  (IP) (IP)  $\leftarrow$  op2 ELSE

next instruction

**END IF** 

**Description** 

If the condition specified by op1 is met, a branch to the absolute memory location specified by the second operand op2 is taken. The value of the instruction pointer, IP, is placed onto the system stack. Because the IP always points to the instruction following the branch instruction, the value stored on the system stack represents the return address of the calling routine. If the condition is not met, no action is taken and the next instruction is executed normally.

Note

The condition codes for op1 are defined in Table 5.

### Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | _ | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

C Not affected.

Not affected.

**Addressing** 

Mnemonic

Format

**Bytes** 

Modes

CALLA cc, caddr

CA c0 MM MM

![](_page_64_Picture_1.jpeg)

**CALLI** 

### **Call Subroutine Indirect**

**CALLI** 

**Syntax** 

CALLI op1, op2

**Operation** 

IF (op1) THEN (SP)  $\leftarrow$  (SP) - 2 ((SP))  $\leftarrow$  (IP) (IP)  $\leftarrow$  op2 ELSE

next instruction

**END IF** 

**Description** 

If the condition specified by op1 is met, a branch to the location specified indirectly by the second operand op2 is taken. The value of the instruction pointer, IP, is placed onto the system stack. Because the IP always points to the instruction following the branch instruction, the value stored on the system stack represents the return address of the calling routine. If the condition is not met, no action is taken and the next instruction is executed normally.

Note

The condition codes for op1 are defined in Table 5.

### Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

C Not affected.

Not affected.

Addressing

**Modes** 

Mnemonic CALLI  $cc, [Rw_n]$ 

Format AB cn Bytes

![](_page_65_Picture_1.jpeg)

### **CALLR**

### **Call Subroutine Relative**

**CALLR** 

**Syntax** 

CALLR op1

**Operation** 

$$(SP) \leftarrow (SP) - 2$$

$$((SP)) \leftarrow (IP)$$

$$(IP) \leftarrow (IP) + sign\_extend (op1)$$

**Description** 

A branch is taken to the location specified by the instruction pointer, IP, plus the relative displacement, op1. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the instruction pointer (IP) is placed onto the system stack. Because the IP always points to the instruction following the branch instruction, the value stored on the system stack represents the return address of the calling routine. The value of the IP used in the target address calculation is the address of the instruction following the CALLR instruction.

Condition Flags

| Е | Z | V | С | N |
|---|---|---|---|---|
| - | _ | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

Not affected.

**Addressing** 

Mnemonic

Format

Bytes

**Modes** 

CALLR rel

BB rr

![](_page_66_Picture_1.jpeg)

### **CALLS**

### **Call Inter-Segment Subroutine**

**CALLS** 

**Syntax** 

**Operation** 

$$(SP) \leftarrow (SP) - 2$$
  
 $((SP)) \leftarrow (CSP)$   
 $(SP) \leftarrow (SP) - 2$   
 $((SP)) \leftarrow (IP)$   
 $(CSP) \leftarrow op1$   
 $(IP) \leftarrow op2$ 

**Description** 

A branch is taken to the absolute location specified by op2 within the segment specified by op1. The value of the instruction pointer (IP) is placed onto the system stack. Because the IP always points to the instruction following the branch instruction, the value stored on the system stack represents the return address to the calling routine. The previous value of the CSP is also placed on the system stack to insure correct return to the calling segment.

Condition Flags

| Е | Z | V | С | N |
|---|---|---|---|---|
| _ | - | 1 | - | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

**N** Not affected.

**Addressing** 

Modes

Mnemonic
CALLS seg, caddr

Format

Bytes

DA SS MM MM 4

![](_page_67_Picture_1.jpeg)

CMP Integer Compare CMP

Syntax CMP op1, op2

**Operation**  $(op1) \Leftrightarrow (op2)$ 

**Data Types** WORD

Description

The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. The flags are set according to the rules of subtraction. The operands remain unchanged.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemo | nic                                  | Format      | Bytes |
|-------|--------------------------------------|-------------|-------|
| CMP   | Rw <sub>n</sub> , Rw <sub>m</sub>    | 40 nm       | 2     |
| CMP   | Rw <sub>n</sub> , [Rw <sub>i</sub> ] | 48 n:10ii   | 2     |
| CMP   | $Rw_n$ , $[Rw_i+]$                   | 48 n:11ii   | 2     |
| CMP   | Rw <sub>n</sub> , #data3             | 48 n:0###   | 2     |
| CMP   | reg, #data16                         | 46 RR ## ## | 4     |
| CMP   | reg, mem                             | 42 RR MM MM | 4     |

![](_page_68_Picture_1.jpeg)

### **CMPB** Integer Compare

**CMPB** 

Syntax CMPB op1, op2

**Operation**  $(op1) \Leftrightarrow (op2)$ 

**Data Types** BYTE

**Description** 

The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. The flags are set according to the rules of subtraction. The operands remain unchanged.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemor | nic                                   | Format      | Bytes |
|--------|---------------------------------------|-------------|-------|
| CMPB   | Rb <sub>n</sub> , Rb <sub>m</sub>     | 41 nm       | 2     |
| CMPB   | Rb <sub>n</sub> , [Rw <sub>i</sub> ]  | 49 n:10ii   | 2     |
| CMPB   | Rb <sub>n</sub> , [Rw <sub>i</sub> +] | 49 n:11ii   | 2     |
| CMPB   | Rb <sub>n</sub> , #data3              | 49 n:0###   | 2     |
| CMPB   | reg, #data8                           | 47 RR ## xx | 4     |
| CMPB   | reg, mem                              | 43 RR MM MM | 4     |
|        |                                       |             |       |

![](_page_69_Picture_1.jpeg)

### CMPD1

### **Integer Compare and Decrement by 1**

CMPD1

**Syntax** 

CMPD1 op1, op2

**Operation** 

 $(op1) \Leftrightarrow (op2)$ 

 $(op1) \leftarrow (op1) - 1$ 

**Data Types** 

**WORD** 

**Description** 

This instruction is used to enhance the performance and flexibility of loops. The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. Operand op1 may specify ONLY GPR registers. Once the subtraction has completed, the operand op1 is decremented by one. Using the set flags, a branch instruction can then be used in conjunction with this instruction to form common high level language FOR loops of any range.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemoni | C                         | Format      | Bytes |
|------------|---------|---------------------------|-------------|-------|
| Modes      | CMPD1   | Rw <sub>n</sub> , #data4  | A0 #n       | 2     |
|            | CMPD1   | Rw <sub>n</sub> , #data16 | A6 Fn ## ## | 4     |
|            | CMPD1   | Rw <sub>n</sub> , mem     | A2 Fn MM MM | 4     |

![](_page_70_Picture_1.jpeg)

### CMPD2 Integer Compare and Decrement by 2 CMPD2

Syntax CMPD2 op1, op2

**Operation**  $(op1) \Leftrightarrow (op2)$ 

 $(op1) \leftarrow (op1) - 2$ 

Data Types WORD

**Description** This instruction is used to enhance the performance and flexibility

of loops. The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. Operand op1 may specify ONLY GPR registers. Once the subtraction has completed, the operand op1 is decremented by two. Using the set flags, a branch instruction can then be used in conjunction with this instruction to form common high level language FOR loops of any range.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemoni | С                         | Format      | Bytes |
|------------|---------|---------------------------|-------------|-------|
| Modes      | CMPD2   | Rw <sub>n</sub> , #data4  | B0 #n       | 2     |
|            | CMPD2   | Rw <sub>n</sub> , #data16 | B6 Fn ## ## | 4     |
|            | CMPD2   | Rw <sub>n</sub> , mem     | B2 Fn MM MM | 4     |

![](_page_71_Picture_1.jpeg)

### CMPI1

### **Integer Compare and Increment by 1**

CMPI1

**Syntax** 

CMPI1 op1, op2

**Operation** 

 $(op1) \Leftrightarrow (op2)$ 

 $(op1) \leftarrow (op1) + 1$ 

**Data Types** 

WORD

**Description** 

This instruction is used to enhance the performance and flexibility of loops. The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. Operand op1 may specify ONLY GPR registers. Once the subtraction has completed, the operand op1 is incremented by one. Using the set flags, a branch instruction can then be used in conjunction with this instruction to form common high level language FOR loops of any range.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| ivinemon | IIC                       | Format      | Bytes |
|----------|---------------------------|-------------|-------|
| CMPI1    | Rw <sub>n</sub> , #data4  | 80 #n       | 2     |
| CMPI1    | Rw <sub>n</sub> , #data16 | 86 Fn ## ## | 4     |
| CMPI1    | Rw <sub>n</sub> , mem     | 82 Fn MM MM | 4     |

![](_page_72_Picture_1.jpeg)

### CMPI2

### **Integer Compare and Increment by 2**

CMPI2

Bytes 2 4

4

**Syntax** 

CMPI2 op1, op2

**Operation** 

 $(op1) \Leftrightarrow (op2)$ 

 $(op1) \leftarrow (op1) + 2$ 

**Data Types** 

WORD

**Description** 

This instruction is used to enhance the performance and flexibility of loops. The source operand specified by op1 is compared to the source operand specified by op2 by performing a 2's complement binary subtraction of op2 from op1. Operand op1 may specify ONLY GPR registers. Once the subtraction has completed, the operand op1 is incremented by two. Using the set flags, a branch instruction can then be used in conjunction with this instruction to form common high level language FOR loops of any range.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.

92 Fn MM MM

- **C** Set if a borrow is generated. Cleared otherwise.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemonic |                                                       | Format               |
|------------|----------|-------------------------------------------------------|----------------------|
| Modes      |          | Rw <sub>n</sub> , #data4<br>Rw <sub>n</sub> , #data16 | 90 #n<br>96 Fn ## ## |

Rw<sub>n</sub>, mem

CMPI2

![](_page_73_Picture_1.jpeg)

CPL Integer One's Complement CPL

Syntax CPL op1

**Operation**  $(op1) \leftarrow \neg (op1)$ 

Data Types WORD

**Description** Performs a 1's complement of the source operand specified by

op1. The result is stored back into op1.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesCPLRwn91 n02

![](_page_74_Picture_1.jpeg)

**CPLB** Integer One's Complement

**CPLB** 

Syntax

CPL op1

**Operation** 

 $(op1) \leftarrow \neg (op1)$ 

**Data Types** 

**BYTE** 

**Description** 

Performs a 1's complement of the source operand specified by op1. The result is stored back into op1.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesCPLB $Rb_n$ B1 n02

![](_page_75_Picture_1.jpeg)

### **DISWDT** Disable Watchdog Timer

**DISWDT** 

Syntax DISWDT

**Operation** Disable the watchdog timer

**Description** This instruction disables the watchdog timer. The watchdog timer

is enabled by a reset. The DISWDT instruction allows the

watchdog timer to be disabled for applications which do not require a watchdog function. Following a reset, this instruction can be executed at any time until either a Service Watchdog Timer instruction (SRVWDT) or an End of Initialization instruction (EINIT) are executed. Once one of these instructions has been executed,

the DISWDT instruction will have no effect.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 1 | - | - | 1 | - |

**E** Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

**N** Not affected.

AddressingMnemonicFormatBytesModesDISWDTA5 5A A5 A54

![](_page_76_Picture_1.jpeg)

DIV 16-by-16 Signed Division DIV

Syntax DIV op1

**Operation** MDRIU = 1

 $(MDL) \leftarrow (MDL) / (op1)$  $(MDH) \leftarrow (MDL) \mod (op1)$ 

Data Types WORD

**Description** Performs a signed 16-bit by 16-bit division of the low order word

stored in the MD register by the source word operand op1. The signed quotient is then stored in the low order word of the MD register (MDL) and the remainder is stored in the high order word

of the MD register (MDH).

**Note** DIV is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

**E** Always cleared.

**Z** Set if result equals zero. Cleared otherwise.

V Set if an arithmetic overflow occurred, i.e. if the divisor (op1) was zero (the result in MDH and MDL is not valid in this case). Cleared otherwise.

**C** Always cleared.

**N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesDIVRwn4B nn2

![](_page_77_Picture_1.jpeg)

DIVL 32-by-16 Signed Division DIVL

Syntax DIVL op1

**Operation** MDRIU = 1

 $(MDL) \leftarrow (MD) / (op1)$  $(MDH) \leftarrow (MD) \mod (op1)$ 

Data Types WORD, DOUBLEWORD

**Description** Performs an extended signed 32-bit by 16-bit division of the two

words stored in the MD register by the source word operand op1. The signed quotient is then stored in the low order word of the MD register (MDL) and the remainder is stored in the high order word

of the MD register (MDH).

**Note** DIVL is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

- **E** Always cleared.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic overflow occurred, i.e. the quotient cannot be represented in a word data type, or if the divisor (op1) was zero (the result in MDH and MDL is not valid in this case). Cleared otherwise.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesDIVLRwn6B nn2

![](_page_78_Picture_1.jpeg)

**DIVLU** 32-by-16 Unsigned Division

**DIVLU** 

Syntax DIVLU op1

**Operation** MDRIU = 1

 $(MDL) \leftarrow (MD) / (op1)$  $(MDH) \leftarrow (MD) \mod (op1)$ 

Data Types WORD, DOUBLEWORD

**Description** Performs an extended unsigned 32-bit by 16-bit division of the two

words stored in the MD register by the source word operand op1. The unsigned quotient is then stored in the low order word of the MD register (MDL) and the remainder is stored in the high order

word of the MD register (MDH).

**Note** DIVLU is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

**E** Always cleared.

**Z** Set if result equals zero. Cleared otherwise.

V Set if an arithmetic overflow occurred, i.e. the quotient cannot be represented in a word data type, or if the divisor (op1) was zero (the result in MDH and MDL is not valid in this case). Cleared otherwise.

C Always cleared.

N Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesDIVLURwn7B nn2

![](_page_79_Picture_1.jpeg)

**DIVU** 16-by-16 Unsigned Division

DIVU

Syntax DIVU op1

**Operation** MDRIU = 1

 $(MDL) \leftarrow (MDL) / (op1)$  $(MDH) \leftarrow (MDL) \mod (op1)$ 

**Data Types** WORD

**Description** Performs an unsigned 16-bit by 16-bit division of the low order

word stored in the MD register by the source word operand op1. The signed quotient is then stored in the low order word of the MD register (MDL) and the remainder is stored in the high order word

of the MD register (MDH).

**Note** DIVU is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

**E** Always cleared.

**Z** Set if result equals zero. Cleared otherwise.

V Set if an arithmetic overflow occurred, i.e. if the divisor (op1) was zero (the result in MDH and MDL is not valid in this case). Cleared otherwise.

C Always cleared.

**N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesDIVURwn5B nn2

![](_page_80_Picture_1.jpeg)

EINIT End of Initialization EINIT

Syntax EINIT

**Operation** End of Initialization

**Description** This instruction is used to signal the end of the initialization portion

of a program. After a reset, the reset output pin  $\overline{\text{RSTOUT}}$  is pulled low. It remains low until the EINIT instruction has been executed at which time it goes high. This enables the program to signal the

external circuitry that it has successfully initialized the

microcontroller. After the EINIT instruction has been executed, execution of the Disable Watchdog Timer instruction (DISWDT)

has no effect.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | _ | _ | _ | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

**N** Not affected.

AddressingMnemonicFormatBytesModesEINITB5 4A B5 B54

![](_page_81_Picture_1.jpeg)

### EXTR Begin EXTended Register Sequence EXTR

Syntax EXTR op1

**Operation** (count)  $\leftarrow$  (op1) [1  $\leq$  op1  $\leq$  4]

Disable interrupts and Class A traps

SFR\_range = Extended

DO WHILE ((count) ≠ 0 AND Class\_B\_trap\_condition ≠ TRUE)

Next Instruction (count) ← (count) - 1

END WHILE (count) = 0

SFR\_range = Standard Enable interrupts and traps

**Description** Causes all SFR or SFR bit accesses via the 'reg', 'bitoff' or 'bitaddr'

addressing modes being made to the Extended SFR space for a specified number of instructions. During their execution both standard/PEC interrupts and class A hardware traps are locked. The value of op1 defines the length of the effected instruction

sequence.

Note Please see additional notes on Page 39.

The EXTR instruction is not available in the SAB 8XC166(W)

devices.

**Condition Flags** 

| E | Z | V | C | N |
|---|---|---|---|---|
| - | _ | - | - | - |

**E** Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

N Not affected.

AddressingMnemonicFormatBytesModesEXTR #irang2D1:10##-02

![](_page_82_Picture_1.jpeg)

### **EXTP**

### **Begin EXTended Page Sequence**

**EXTP** 

**Syntax** 

EXTP op1, op2

**Operation** 

 $(count) \leftarrow (op2) [1 \le op2 \le 4]$ 

Disable interrupts and Class A traps

Data\_Page = (op1)

DO WHILE ((count)  $\neq$  0 AND Class\_B\_trap\_condition  $\neq$  TRUE)

Next Instruction (count) ← (count) - 1

END WHILE (count) = 0

 $Data_Page = (DPPx)$ 

Enable interrupts and traps

### **Description**

Overrides the standard DPP addressing scheme of the long and indirect addressing modes for a specified number of instructions. During their execution both standard/PEC interrupts and class A hardware traps are locked. The EXTP instruction becomes immediately active such that no additional NOPs are required. For any long ('mem') or indirect ([...]) address in the EXTP instruction sequence, the 10-bit page number (address bits A23 - A14) is not determined by the contents of a DPP register but by the value of op1 itself. The 14-bit page offset (address bits A13 - A0) is derived from the long or indirect address as usual. The value of op2 defines the length of the effected instruction sequence.

**Note** 

Please see additional notes on Page 39.

The EXTP instruction is not available in the SAB 8XC166(W) devices.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | - | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

Not affected.

![](_page_83_Picture_1.jpeg)

EXTP continued ... EXTP

**Addressing** Mnemonic Format Bytes

Modes EXTP Rwm, #irang2 DC :01##-m 2

EXTP #pag, #irang2 D7 :01##-0 pp 0:00pp 4

![](_page_84_Picture_1.jpeg)

**EXTPR** Begin EXTended Page and

**EXTPR** 

**Register Sequence** 

Syntax EXTPR op1, op2

**Operation** (count)  $\leftarrow$  (op2) [1  $\leq$  op2  $\leq$  4]

Disable interrupts and Class A traps

Data Page = (op1) AND SFR range = Extended

DO WHILE ((count)  $\neq$  0 AND Class\_B\_trap\_condition  $\neq$  TRUE)

Next Instruction (count) ← (count) - 1

END WHILE (count) = 0

Data\_Page = (DPPx) AND SFR\_range = Standard

Enable interrupts and traps

**Description** Overrides the standard DPP addressing scheme of the long and

indirect addressing modes and causes all SFR or SFR bit

accesses via the 'reg', 'bitoff' or 'bitaddr' addressing modes being

made to the Extended SFR space for a specified number of instructions. During their execution both standard/PEC interrupts

and class A hardware traps are locked.

For any long ('mem') or indirect ([...]) address in the EXTP

instruction sequence, the 10-bit page number (address bits A23 - A14) is not determined by the contents of a DPP register but by the value of op1 itself. The 14-bit page offset (address bits A13 - A0)

is derived from the long or indirect address as usual.

The value of op2 defines the length of the effected instruction

sequence.

**Note** Please see additional notes on Page 39.

The EXTPR instruction is not available in the SAB 8XC166(W)

devices.

![](_page_85_Picture_1.jpeg)

**EXTPR** continued ... **EXTPR** 

**Condition** Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | _ | - | - | - |

Not affected. Ε

Ζ Not affected.

Not affected.

Not affected.

Not affected.

**Addressing** 

Mnemonic

Format

**Bytes** 

Modes

Rwm, #irang2 **EXTPR** #pag, #irang2 **EXTPR** 

DC:11##-m

2 D7:11##-0 pp 0:00pp

User's Manual 81 V2.0, 2001-03

![](_page_86_Picture_1.jpeg)

### EXTS Begin EXTended Segment Sequence EXTS

Syntax EXTS op1, op2

**Operation** (count)  $\leftarrow$  (op2)  $[1 \le op2 \le 4]$ 

Disable interrupts and Class A traps

Data\_Segment = (op1)

DO WHILE ((count) ≠ 0 AND Class\_B\_trap\_condition ≠ TRUE)

Next Instruction (count) ← (count) - 1

END WHILE (count) = 0

 $Data_Page = (DPPx)$ 

Enable interrupts and traps

#### **Description**

Overrides the standard DPP addressing scheme of the long and indirect addressing modes for a specified number of instructions. During their execution both standard/PEC interrupts and class A hardware traps are locked. The EXTS instruction becomes immediately active such that no additional NOPs are required. For any long ('mem') or indirect ([...]) address in an EXTS instruction sequence, the value of op1 determines the 8-bit segment (address bits A23 - A16) valid for the corresponding data access. The long or indirect address itself represents the 16-bit segment offset (address bits A15 - A0).

The value of op2 defines the length of the effected instruction sequence.

**Note** 

Please see additional notes on Page 39.

The EXTS instruction is not available in the SAB 8XC166(W) devices.

## Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected

**Z** Not affected.

**V** Not affected.

C Not affected.

Not affected.

![](_page_87_Picture_1.jpeg)

| EXTS       | continu      | ued                           |                                | EXTS   |
|------------|--------------|-------------------------------|--------------------------------|--------|
| Addressing | Mnemor       | nic                           | Format                         | Bytes  |
| Modes      | EXTS<br>EXTS | Rwm, #irang2<br>#seg, #irang2 | DC :00##-m<br>D7 :00##-0 ss 00 | 2<br>4 |

![](_page_88_Picture_1.jpeg)

**EXTSR** Begin EXTended Segment

**EXTSR** 

**Syntax** 

EXTSR op1, op2

**Operation** 

 $(count) \leftarrow (op2) [1 \le op2 \le 4]$ 

and Register Sequence

Disable interrupts and Class A traps

Data Segment = (op1) AND SFR range = Extended

DO WHILE ((count)  $\neq$  0 AND Class B trap condition  $\neq$  TRUE)

Next Instruction (count) ← (count) - 1

END WHILE (count) = 0

Data\_Page = (DPPx) AND SFR\_range = Standard

Enable interrupts and traps

**Description** 

Overrides the standard DPP addressing scheme of the long and indirect addressing modes and causes all SFR or SFR bit accesses via the 'reg', 'bitoff' or 'bitaddr' addressing modes being made to the Extended SFR space for a specified number of instructions. During their execution both standard/PEC interrupts and class A hardware traps are locked. The EXTSR instruction becomes immediately active such that no additional NOPs are required.

For any long ('mem') or indirect ([...]) address in an EXTSR instruction sequence, the value of op1 determines the 8-bit segment (address bits A23 - A16) valid for the corresponding data access. The long or indirect address itself represents the 16-bit segment offset (address bits A15 - A0).

The value of op2 defines the length of the effected instruction sequence.

Note

Please see additional notes on Page 39.

The EXTSR instruction is not available in the SAB 8XC166(W)

devices.

![](_page_89_Picture_1.jpeg)

| <b>EXTSR</b> | continued |
|--------------|-----------|
|--------------|-----------|

**EXTSR** 

| <b>Condition</b> |
|------------------|
| Flags            |

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | - | - |

Not affected. E

Ζ Not affected.

Not affected.

Not affected.

Not affected.

### **Addressing**

Mnemonic Modes **EXTSR** 

Format **Bytes** 2

Rwm, #irang2 DC:10##-m #seg, #irang2 **EXTSR** D7:10##-0 SS 00 4

![](_page_90_Picture_1.jpeg)

IDLE Enter Idle Mode IDLE

Syntax IDLE

**Operation** Enter Idle Mode

**Description** This instruction causes the device to enter idle mode or sleep

mode (if provided by the device). In both modes the CPU is powered down. In idle mode the peripherals remain running, while

in sleep mode also the peripherals are powered down. The device remains powered down until a peripheral interrupt (only possible in

Idle mode) or an external interrupt occurs.

Sleep mode must be selected before executing the IDLE

instruction.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

**Condition Flags** 

| E | Z | V | C | N |
|---|---|---|---|---|
| _ | - | - | - | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

**N** Not affected.

AddressingMnemonicFormatBytesModesIDLE87 78 87 874

![](_page_91_Picture_1.jpeg)

JB Relative Jump if Bit Set JB

Syntax JB op1, op2
Operation JE (op1) 1 THEN

**Operation** IF (op1) = 1 THEN

 $(IP) \leftarrow (IP) + sign\_extend (op2)$ 

ELSE

**Next Instruction** 

**END IF** 

**Data Types** BIT

**Description** If the bit specified by op1 is set, program execution continues at the

location of the instruction pointer, IP, plus the specified

displacement, op2. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the IP used in the target address calculation is the address of the instruction following the JB instruction. If the specified bit is clear, the instruction following the JB instruction is

executed.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | _ | _ |

**E** Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

Not affected.

AddressingMnemonicFormatBytesModesJBbitaddr<sub>Q,q</sub>, rel8A QQ rr q04

![](_page_92_Picture_1.jpeg)

### **JBC**

### Relative Jump if Bit Set and Clear Bit

**JBC** 

**Syntax** 

**Operation** 

IF 
$$(op1) = 1$$
 THEN

$$(op1) = 0$$

$$(IP) \leftarrow (IP) + sign\_extend (op2)$$

ELSE

**Next Instruction** 

**END IF** 

**Data Types** 

BIT

**Description** 

If the bit specified by op1 is set, program execution continues at the location of the instruction pointer, IP, plus the specified displacement, op2. The bit specified by op1 is cleared, allowing implementation of semaphore operations. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the IP used in the target address calculation is the address of the instruction following the JBC instruction. If the specified bit was clear, the instruction following the JBC instruction is executed.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | B | 0 | 0 | В |

E Always cleared.

**Z** Contains logical negation of the previous state of the specified bit.

V Always cleared.

**C** Always cleared.

N Contains the previous state of the specified bit.

### **Addressing**

| wner | nonic |  |
|------|-------|--|
|      |       |  |

### Format

**Bytes** 

**Modes** 

JBC bitaddr<sub>Q,q</sub>, rel

AA QQ rr q0

![](_page_93_Picture_1.jpeg)

**JMPA** 

### **Absolute Conditional Jump**

**JMPA** 

**Syntax** 

JMPA op1, op2

**Operation** 

IF (op1) = 1 THEN

 $(IP) \leftarrow op2$ 

ELSE

**Next Instruction** 

**END IF** 

**Description** 

If the condition specified by op1 is met, a branch to the absolute address specified by op2 is taken. If the condition is not met, no action is taken, and the instruction following the JMPA instruction is executed normally.

Note

The condition codes for op1 are defined in Table 5.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

C Not affected.

Not affected.

**Addressing** 

Mnemonic

**Format** 

**Bytes** 

**Modes** 

**JMPA** 

cc, caddr

EA c0 MM MM

![](_page_94_Picture_1.jpeg)

JMPI Indirect Conditional Jump

**JMPI** 

SyntaxJMPIop1, op2OperationIF (op1) = 1 THEN

 $(IP) \leftarrow op2$ 

**ELSE** 

**Next Instruction** 

**END IF** 

**Description** If the condition specified by op1 is met, a branch to the absolute

address specified by op2 is taken. If the condition is not met, no action is taken, and the instruction following the JMPI instruction is

executed normally.

Note The condition codes for op1 are defined in Table 5.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

Not affected.

AddressingMnemonicFormatBytesModesJMPIcc, [Rwn]9C cn2

![](_page_95_Picture_1.jpeg)

**JMPR** 

### **Relative Conditional Jump**

**JMPR** 

**Syntax** 

JMPR op1, op2

**Operation** 

IF (op1) = 1 THEN

 $(IP) \leftarrow (IP) + sign\_extend (op2)$ 

ELSE

**Next Instruction** 

**END IF** 

**Description** 

If the condition specified by op1 is met, program execution continues at the location of the instruction pointer, IP, plus the specified displacement, op2. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the IP used in the target address calculation is the address of the instruction following the JMPR instruction. If the specified condition is not met, program execution continues normally with the instruction following the JMPR instruction.

Note

The condition codes for op1 are defined in Table 5.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | _ | - |

E Not affected.

**Z** Not affected.

V Not affected.

C Not affected.

**N** Not affected.

AddressingMnemonicFormatBytesModesJMPRcc, relcD rr2

![](_page_96_Picture_1.jpeg)

JMPS Absolute Inter-Segment Jump JMPS

Syntax JMPS op1, op2

**Operation** (CSP)  $\leftarrow$  op1

 $(IP) \leftarrow op2$ 

**Description** Branches unconditionally to the absolute address specified by op2

within the segment specified by op1.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

N Not affected.

AddressingMnemonicFormatBytesModesJMPSseg, caddrFA SS MM MM4

![](_page_97_Picture_1.jpeg)

JNB Relative Jump if Bit Clear

**JNB** 

Syntax JNB op1, op2

**Operation** IF (op1) = 0 THEN

 $(IP) \leftarrow (IP) + sign\_extend (op2)$ 

ELSE

**Next Instruction** 

**END IF** 

**Data Types** 

BIT

**Description** 

If the bit specified by op1 is clear, program execution continues at the location of the instruction pointer, IP, plus the specified displacement, op2. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the IP used in the target address calculation is the address of the instruction following the JNB instruction. If the specified bit is set, the instruction following the JNB instruction is executed.

# Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| - | - | _ | - | - |

**E** Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

Not affected.

AddressingMnemonicFormatBytesModesJNBbitaddr<sub>Q,q</sub>, rel9A QQ rr q04

![](_page_98_Picture_1.jpeg)

### **JNBS**

### Relative Jump if Bit Clear and Set Bit

**JNBS** 

**Syntax** 

JNBS op1, op2

**Operation** 

IF (op1) = 0 THEN

(op1) = 1

 $(IP) \leftarrow (IP) + sign\_extend (op2)$ 

**ELSE** 

**Next Instruction** 

**END IF** 

**Data Types** 

BIT

**Description** 

If the bit specified by op1 is clear, program execution continues at the location of the instruction pointer, IP, plus the specified displacement, op2. The bit specified by op1 is set, allowing implementation of semaphore operations. The displacement is a two's complement number which is sign extended and counts the relative distance in words. The value of the IP used in the target address calculation is the address of the instruction following the JNBS instruction. If the specified bit was set, the instruction following the JNBS instruction is executed.

# Condition Flags

| Е | Z | V | С | N |
|---|---|---|---|---|
| 0 | B | 0 | 0 | В |

**E** Always cleared.

**Z** Contains logical negation of the previous state of the specified bit.

V Always cleared.

**C** Always cleared.

N Contains the previous state of the specified bit.

**Addressing** 

Mnemonic

**Format** 

**Bytes** 

**Modes** 

**JNBS** 

bitaddr<sub>Q.a</sub>, rel

BA QQ rr q0

![](_page_99_Picture_1.jpeg)

MOV Move Data MOV

Syntax MOV op1, op2

**Operation**  $(op1) \leftarrow (op2)$ 

**Data Types** WORD

**Description** Moves the contents of the source operand specified by op2 to the

location specified by the destination operand op1. The contents of the moved data is examined, and the condition codes are updated

accordingly.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the source operand op2 equals zero. Cleared otherwise.
- **V** Not affected.
- **C** Not affected.
- N Set if the most significant bit of the source operand op2 is set. Cleared otherwise.

![](_page_100_Picture_1.jpeg)

| MOV        | contin                                                             | ued                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |                                                                                                                         | MOV                                                                     |
|------------|--------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| Addressing | Mnemoi                                                             | nic                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | Format                                                                                                                  | Bytes                                                                   |
| Modes      | MOV<br>MOV<br>MOV<br>MOV<br>MOV<br>MOV<br>MOV<br>MOV<br>MOV<br>MOV | Rw <sub>n</sub> , Rw <sub>m</sub> Rw <sub>n</sub> , #data4 reg, #data16 Rw <sub>n</sub> , [Rw <sub>m</sub> ] Rw <sub>n</sub> , [Rw <sub>m</sub> +] [Rw <sub>m</sub> ], Rw <sub>n</sub> [-Rw <sub>m</sub> ], Rw <sub>n</sub> [Rw <sub>n</sub> ], [Rw <sub>m</sub> ] [Rw <sub>n</sub> +], [Rw <sub>m</sub> ] [Rw <sub>n</sub> +], [Rw <sub>m</sub> +] Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> , [Rw <sub>m</sub> +  Rw <sub>n</sub> ] reg, mem | F0 nm E0 #n E6 RR ## ## A8 nm 98 nm B8 nm B8 nm C8 nm D8 nm D8 nm E8 nm D4 nm ## ## C4 nm ## ## 84 0n MM MM 94 0n MM MM | 2<br>2<br>4<br>2<br>2<br>2<br>2<br>2<br>2<br>2<br>4<br>4<br>4<br>4<br>4 |
|            | MOV                                                                | mem, reg                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | F6 RR MM MM                                                                                                             | 4                                                                       |

![](_page_101_Picture_1.jpeg)

MOVB Move Data MOVB

Syntax MOVB op1, op2

**Operation**  $(op1) \leftarrow (op2)$ 

**Data Types** BYTE

**Description** Moves the contents of the source operand specified by op2 to the

location specified by the destination operand op1. The contents of the moved data is examined, and the condition codes are updated

accordingly.

| E | Z | V | C | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the source operand op2 equals zero. Cleared otherwise.
- **V** Not affected.
- **C** Not affected.
- N Set if the most significant bit of the source operand op2 is set. Cleared otherwise.

![](_page_102_Picture_1.jpeg)

MOVB continued ... MOVB

| C                                            | Format                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | Bytes                                                |
|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------|
| Rb <sub>n</sub> , Rb <sub>m</sub>            | F1 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| Rb <sub>n</sub> , #data4                     | E1 #n                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| reg, #data8                                  | E7 RR ## xx                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| Rb <sub>n</sub> , [Rw <sub>m</sub> ]         | A9 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| Rb <sub>n</sub> , [Rw <sub>m</sub> +]        | 99 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| [Rw <sub>m</sub> ], Rb <sub>n</sub>          | B9 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| [-Rw <sub>m</sub> ], Rb <sub>n</sub>         | 89 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| [Rw <sub>n</sub> ], [Rw <sub>m</sub> ]       | C9 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| $[Rw_n+], [Rw_m]$                            | D9 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| $[Rw_n], [Rw_m+]$                            | E9 nm                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 2                                                    |
| Rb <sub>n</sub> , [Rw <sub>m</sub> +#data16] | F4 nm ## ##                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| [Rw <sub>m</sub> +#data16], Rb <sub>n</sub>  | E4 nm ## ##                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| [Rw <sub>n</sub> ], mem                      | A4 0n MM MM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| mem, [Rw <sub>n</sub> ]                      | B4 0n MM MM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| reg, mem                                     | F3 RR MM MM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
| mem, reg                                     | F7 RR MM MM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | 4                                                    |
|                                              | Rb <sub>n</sub> , Rb <sub>m</sub> Rb <sub>n</sub> , #data4 reg, #data8 Rb <sub>n</sub> , [Rw <sub>m</sub> ] Rb <sub>n</sub> , [Rw <sub>m</sub> +] [Rw <sub>m</sub> ], Rb <sub>n</sub> [-Rw <sub>m</sub> ], Rb <sub>n</sub> [Rw <sub>n</sub> ], [Rw <sub>m</sub> ] [Rw <sub>n</sub> ], [Rw <sub>m</sub> ] [Rw <sub>n</sub> ], [Rw <sub>m</sub> +] Rb <sub>n</sub> , [Rw <sub>m</sub> +] Rb <sub>n</sub> , [Rw <sub>m</sub> +#data16] [Rw <sub>m</sub> +#data16], Rb <sub>n</sub> [Rw <sub>n</sub> ], mem mem, [Rw <sub>n</sub> ] reg, mem | $\begin{array}{llllllllllllllllllllllllllllllllllll$ |

![](_page_103_Picture_1.jpeg)

MOVBS Move Byte Sign Extend

**MOVBS** 

Syntax MOVBS op1, op2

**Operation** (low byte op1)  $\leftarrow$  (op2)

IF  $(op2_7) = 1$  THEN  $(high byte op1) \leftarrow FF_H$ 

**ELSE** 

(high byte op1)  $\leftarrow$  00<sub>H</sub>

END IF

Data Types WORD, BYTE

**Description** Moves and sign extends the contents of the source byte specified

by op2 to the word location specified by the destination operand

op1. The contents of the moved data is examined, and the

condition codes are updated accordingly.

## Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | - | _ | * |

E Always cleared.

**Z** Set if the value of the source operand op2 equals zero. Cleared otherwise.

**V** Not affected.

C Not affected.

N Set if the most significant bit of the source operand op2 is set. Cleared otherwise.

| Addressing | Mnemonic |                                   | Format      | Bytes |
|------------|----------|-----------------------------------|-------------|-------|
| Modes      | MOVBS F  | Rw <sub>n</sub> , Rb <sub>m</sub> | D0 mn       | 2     |
|            | MOVBS r  | eg, mem                           | D2 RR MM MM | 4     |
|            | MOVBS r  | nem, reg                          | D5 RR MM MM | 4     |

![](_page_104_Picture_1.jpeg)

MOVBZ Move Byte Zero Extend MOVBZ

Syntax MOVBZ op1, op2

**Operation** (low byte op1)  $\leftarrow$  (op2)

(high byte op1)  $\leftarrow$  00<sub>H</sub>

Data Types WORD, BYTE

**Description** Moves and zero extends the contents of the source byte specified

by op2 to the word location specified by the destination operand

op1. The contents of the moved data is examined, and the

condition codes are updated accordingly.

**Condition Flags** 

| Ε | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | - | - | 0 |

**E** Always cleared.

**Z** Set if the value of the source operand op2 equals zero. Cleared otherwise.

V Not affected.

C Not affected.

N Always cleared.

| Addressing | Mnemonic                                                  | Format               | Bytes  |
|------------|-----------------------------------------------------------|----------------------|--------|
| Modes      | MOVBZ Rw <sub>n</sub> , Rb <sub>m</sub><br>MOVBZ reg, mem | C0 mn<br>C2 RR MM MM | 2<br>4 |
|            | MOVBZ mem. rea                                            | C5 RR MM MM          | 4      |

![](_page_105_Picture_1.jpeg)

MUL Signed Multiplication MUL

Syntax MUL op1, op2

**Operation** MDRIU = 1

 $(MD) \leftarrow (op1) \times (op2)$ 

Data Types WORD

**Description** Performs a 16-bit by 16-bit signed multiplication using the two

words specified by operands op1 and op2 respectively. The signed

32-bit result is placed in the MD register.

**Note** MUL is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

E Always cleared.

**Z** Set if the result equals zero. Cleared otherwise.

V This bit is set if the result cannot be represented in a word data type. Cleared otherwise.

**C** Always cleared.

**N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesMUL $Rw_n$ ,  $Rw_m$ 0B nm2

![](_page_106_Picture_1.jpeg)

MULU Unsigned Multiplication

MULU

Syntax MULU op1, op2

**Operation** MDRIU = 1

 $(MD) \leftarrow (op1) \times (op2)$ 

Data Types WORD

**Description** Performs a 16-bit by 16-bit unsigned multiplication using the two

words specified by operands op1 and op2 respectively. The

unsigned 32-bit result is placed in the MD register.

**Note** MULU is interruptable.

Please see additional description on Page 40.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | S | 0 | * |

E Always cleared.

**Z** Set if the result equals zero. Cleared otherwise.

V This bit is set if the result cannot be represented in a word data type. Cleared otherwise.

**C** Always cleared.

**N** Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesMULURwn, Rwm1B nm2

![](_page_107_Picture_1.jpeg)

NEG Integer Two's Complement NEG

Syntax NEG op1

**Operation**  $(op1) \leftarrow 0 - (op1)$ 

Data Types WORD

**Description** Performs a binary 2's complement of the source operand specified

by op1. The result is then stored in op1.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- N Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesNEGRwn81 n02

![](_page_108_Picture_1.jpeg)

NEGB Integer Two's Complement NEGB

Syntax NEGB op1

**Operation**  $(op1) \leftarrow 0 - (op1)$ 

**Data Types** BYTE

**Description** Performs a binary 2's complement of the source operand specified

by op1. The result is then stored in op1.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- N Set if the most significant bit of the result is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesNEGBRbnA1 n02

![](_page_109_Picture_1.jpeg)

NOP No Operation NOP

Syntax NOP

**Operation** No Operation

**Description** This instruction causes a null operation to be performed. A null

operation causes no change in the status of the flags.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | _ | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

N Not affected.

AddressingMnemonicFormatBytesModesNOPCC 002

![](_page_110_Picture_1.jpeg)

OR Logical OR

Syntax OR op1, op2

**Operation**  $(op1) \leftarrow (op1) \lor (op2)$ 

Data Types WORD

**Description** Performs a bitwise logical OR of the source operand specified by

op2 and the destination operand specified by op1. The result is

then stored in op1.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- **V** Always cleared.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | onic                              | Format      | Bytes |
|------------|-------|-----------------------------------|-------------|-------|
| Modes      | OR    | Rw <sub>n</sub> , Rw <sub>m</sub> | 70 nm       | 2     |
|            | OR    | $Rw_n$ , $[Rw_i]$                 | 78 n:10ii   | 2     |
|            | OR    | $Rw_n$ , $[Rw_i+]$                | 78 n:11ii   | 2     |
|            | OR    | Rw <sub>n</sub> , #data3          | 78 n:0###   | 2     |
|            | OR    | reg, #data16                      | 76 RR ## ## | 4     |
|            | OR    | reg, mem                          | 72 RR MM MM | 4     |
|            | OR    | mem. rea                          | 74 RR MM MM | 4     |

![](_page_111_Picture_1.jpeg)

ORB Logical OR

Syntax ORB op1, op2

**Operation**  $(op1) \leftarrow (op1) \lor (op2)$ 

**Data Types** BYTE

**Description** Performs a bitwise logical OR of the source operand specified by

op2 and the destination operand specified by op1. The result is

then stored in op1.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- **V** Always cleared.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | nic                                   | Format      | Bytes |
|------------|-------|---------------------------------------|-------------|-------|
| Modes      | ORB   | Rb <sub>n</sub> , Rb <sub>m</sub>     | 71 nm       | 2     |
|            | ORB   | Rb <sub>n</sub> , [Rw <sub>i</sub> ]  | 79 n:10ii   | 2     |
|            | ORB   | Rb <sub>n</sub> , [Rw <sub>i</sub> +] | 79 n:11ii   | 2     |
|            | ORB   | Rb <sub>n</sub> , #data3              | 79 n:0###   | 2     |
|            | ORB   | reg, #data8                           | 77 RR ## xx | 4     |
|            | ORB   | reg, mem                              | 73 RR MM MM | 4     |
|            | ORB   | mem. rea                              | 75 RR MM MM | 4     |

![](_page_112_Picture_1.jpeg)

### **PCALL**

### Push Word and Call Subroutine Absolute PCALL

**Syntax** 

**Operation** 

$$\begin{array}{l} (\mathsf{tmp}) \leftarrow (\mathsf{op1}) \\ (\mathsf{SP}) \leftarrow (\mathsf{SP}) - 2 \\ ((\mathsf{SP})) \leftarrow (\mathsf{tmp}) \\ (\mathsf{SP}) \leftarrow (\mathsf{SP}) - 2 \\ ((\mathsf{SP})) \leftarrow (\mathsf{IP}) \\ (\mathsf{IP}) \leftarrow \mathsf{op2} \end{array}$$

### **Data Types**

#### WORD

**Description** 

Pushes the word specified by operand op1 and the value of the instruction pointer, IP, onto the system stack, and branches to the absolute memory location specified by the second operand op2. Because IP always points to the instruction following the branch instruction, the value stored on the system stack represents the return address of the calling routine.

# Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of the pushed operand op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the pushed operand op1 equals zero. Cleared otherwise.
- **V** Not affected.
- **C** Not affected.
- **N** Set if the most significant bit of the pushed operand op1 is set. Cleared otherwise.

Addressing Mnemonic

Format Bytes

Modes PCALL reg, caddr

E2 RR MM MM

![](_page_113_Picture_1.jpeg)

**POP** 

### **Pop Word from System Stack**

POP

**Syntax** 

POP op1

**Operation** 

$$(tmp) \leftarrow ((SP))$$
  
 $(SP) \leftarrow (SP) + 2$ 

 $(op1) \leftarrow (tmp)$ 

**Data Types** 

WORD

**Description** 

Pops one word from the system stack specified by the Stack Pointer into the operand specified by op1. The Stack Pointer is then incremented by two.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of the popped word represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the popped word equals zero. Cleared otherwise.
- V Not affected.
- **C** Not affected.
- **N** Set if the most significant bit of the popped word is set. Cleared otherwise.

Addressing

Mnemonic

Format

Bytes

**Modes** 

POP

reg

FC RR

![](_page_114_Picture_1.jpeg)

### PRIOR Prioritize Register

**PRIOR** 

Syntax PRIOR op1, op2

**Operation**  $(tmp) \leftarrow (op2)$   $(count) \leftarrow 0$ 

DO WHILE  $(tmp_{15}) \neq 1$  AND  $(count) \neq 15$  AND  $(op2) \neq 0$ 

 $(tmp_n) \leftarrow (tmp_{n-1})$  $(count) \leftarrow (count) + 1$ 

END WHILE (op1) ← (count)

**Data Types** WORD

**Description** This instruction stores a count value in the word operand specified

by op1 indicating the number of single bit shifts required to normalize the operand op2 so that its MSB is equal to one. If the source operand op2 equals zero, a zero is written to operand op1

and the zero flag is set. Otherwise the zero flag is cleared.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | 0 | 0 | 0 |

**E** Always cleared.

**Z** Set if the source operand op2 equals zero. Cleared otherwise.

V Always cleared.

**C** Always cleared.

**N** Always cleared.

AddressingMnemonicFormatBytesModesPRIORRwn, Rwm2B nm2

![](_page_115_Picture_1.jpeg)

### **PUSH**

### **Push Word on System Stack**

**PUSH** 

**Syntax** 

PUSH op1

**Operation** 

$$(tmp) \leftarrow (op1)$$
  
 $(SP) \leftarrow (SP) - 2$   
 $((SP)) \leftarrow (tmp)$ 

**Data Types** 

WORD

**Description** 

Moves the word specified by operand op1 to the location in the internal system stack specified by the Stack Pointer, after the Stack Pointer has been decremented by two.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of the pushed word represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the pushed word equals zero. Cleared otherwise.
- V Not affected.
- **C** Not affected.
- **N** Set if the most significant bit of the pushed word is set. Cleared otherwise.

Addressing

Mnemonic

Format

**Bytes** 

**Modes** 

PUSH

reg

EC RR

![](_page_116_Picture_1.jpeg)

PWRDN Enter Power Down Mode PWRDN

Syntax PWRDN

**Operation** Enter Power Down Mode

**Description** This instruction causes the part to enter the power down mode. In

this mode, all peripherals and the CPU are powered down until the

part is externally reset.

To further control the action of this instruction, the PWRDN instruction is only enabled when the non-maskable interrupt pin  $(\overline{\text{NMI}})$  is in the low state. Otherwise, this instruction has no effect.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | _ | _ | - |

**E** Not affected.

**Z** Not affected.

**V** Not affected.

C Not affected.

Not affected.

AddressingMnemonicFormatBytesModesPWRDN97 68 97 974

![](_page_117_Picture_1.jpeg)

RET Return from Subroutine RET

Syntax RET

**Operation** (IP)  $\leftarrow$  ((SP))

 $(SP) \leftarrow (SP) + 2$ 

**Description** Returns from a subroutine. The IP is popped from the system

stack. Execution resumes at the instruction following the CALL

instruction in the calling routine.

Condition E Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

N Not affected.

AddressingMnemonicFormatBytesModesRETCB 002

![](_page_118_Picture_1.jpeg)

### **RETI** Return from Interrupt Routine

**RETI** 

**Syntax** 

**RETI** 

**Operation** 

$$(IP) \leftarrow ((SP))$$
  
 $(SP) \leftarrow (SP) + 2$ 

$$(CSP) \leftarrow ((SP))$$
  
 $(SP) \leftarrow (SP) + 2$ 

END IF

$$(PSW) \leftarrow ((SP))$$
  
 $(SP) \leftarrow (SP) + 2$ 

### **Description**

Returns from an interrupt routine. The PSW, IP, and CSP are popped off the system stack. Execution resumes at the instruction which had been interrupted. The previous system state is restored after the PSW has been popped. The CSP is only popped if segmentation is enabled. This is indicated by the SGTDIS bit in the SYSCON register.

**Condition Flags** 

| E | Z | V | С | N |
|---|---|---|---|---|
| S | S | S | S | S |

**E** Restored from the PSW popped from stack.

**Z** Restored from the PSW popped from stack.

**V** Restored from the PSW popped from stack.

**C** Restored from the PSW popped from stack.

**N** Restored from the PSW popped from stack.

AddressingMnemonicFormatBytesModesRETIFB 882

![](_page_119_Picture_1.jpeg)

### **RETP**

### **Return from Subroutine and Pop Word**

**RETP** 

**Syntax** 

**Operation** 

$$(IP) \leftarrow ((SP))$$
  
 $(SP) \leftarrow (SP) + 2$   
 $(tmp) \leftarrow ((SP))$   
 $(SP) \leftarrow (SP) + 2$ 

$$(op1) \leftarrow (tmp)$$

**Data Types** 

**WORD** 

**Description** 

Returns from a subroutine. The IP is first popped from the system stack and then the next word is popped from the system stack into the operand specified by op1. Execution resumes at the instruction following the CALL instruction in the calling routine.

# **Condition Flags**

| E | Z | V | C | N |
|---|---|---|---|---|
| * | * | - | - | * |

- E Set if the value of the word popped into operand op1 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if the value of the word popped into operand op1 equals zero. Cleared otherwise.
- **V** Not affected.
- **C** Not affected.
- N Set if the most significant bit of the word popped into operand op1 is set. Cleared otherwise.

AddressingMnemonicFormatBytesModesRETPregEB RR2

![](_page_120_Picture_1.jpeg)

RETS Return from Inter-Segment Subroutine RETS

Syntax RETS

**Operation** (IP)  $\leftarrow$  ((SP))

$$(SP) \leftarrow (SP) + 2$$

$$(\mathsf{CSP}) \leftarrow ((\mathsf{SP}))$$

$$(SP) \leftarrow (SP) + 2$$

**Description** Returns from an inter-segment subroutine. The IP and CSP are

popped from the system stack. Execution resumes at the

instruction following the CALLS instruction in the calling routine.

**Condition Flags** 

| Е | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

**V** Not affected.

**C** Not affected.

N Not affected.

AddressingMnemonicFormatBytesModesRETSDB 002

![](_page_121_Picture_1.jpeg)

ROL Rotate Left ROL

**Syntax** 

ROL op1, op2

**Operation** 

 $(count) \leftarrow (op2)$ 

 $(C) \leftarrow 0$ 

DO WHILE (count)  $\neq$  0

 $(C) \leftarrow (op1_{15})$ 

 $(op1_n) \leftarrow (op1_{n-1}) [n = 1 \dots 15]$ 

 $(op1_0) \leftarrow (C)$ 

 $(count) \leftarrow (count) - 1$ 

**END WHILE** 

**Data Types** 

**WORD** 

**Description** 

Rotates the destination word operand op1 left by as many times as specified by the source operand op2. Bit 15 is rotated into Bit 0 and into the Carry. Only shift values between 0 and 15 are allowed. When using a GPR as the count control, only the least significant 4 bits are used.

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | 0 | S | * |

- E Always cleared.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- C The carry flag is set according to the last MSB shifted out of op1. Cleared for a rotate count of zero.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | nic                               | Format | Bytes |
|------------|-------|-----------------------------------|--------|-------|
| Modes      | ROL   | Rw <sub>n</sub> , Rw <sub>m</sub> | 0C nm  | 2     |
|            | ROL   | Rw <sub>n</sub> , #data4          | 1C #n  | 2     |

![](_page_122_Picture_1.jpeg)

### ROR Rotate Right ROR

**Syntax** 

ROR op1, op2

**Operation** 

$$(count) \leftarrow (op2)$$

$$(V) \leftarrow 0$$

DO WHILE (count)  $\neq$  0

$$(\mathsf{V}) \leftarrow (\mathsf{V}) \vee (\mathsf{C})$$

$$(C) \leftarrow (op1_0)$$

$$(op1_n) \leftarrow (op1_{n+1}) [n = 0 \dots 14]$$

$$(op1_{15}) \leftarrow (C)$$

$$(count) \leftarrow (count) - 1$$

**END WHILE** 

**Data Types** 

**WORD** 

### **Description**

Rotates the destination word operand op1 right by as many times as specified by the source operand op2. Bit 0 is rotated into Bit 15 and into the Carry. Only shift values between 0 and 15 are allowed. When using a GPR as the count control, only the least significant 4 bits are used.

| E | Z | V | C | N |
|---|---|---|---|---|
| 0 | * | S | S | * |

- **E** Always cleared.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if in any cycle of the rotate operation a '1' is shifted out of the carry flag. Cleared for a rotate count of zero.
- C The carry flag is set according to the last LSB shifted out of op1. Cleared for a rotate count of zero.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Addressing Mnemonic |                                   | Format | Bytes |
|------------|---------------------|-----------------------------------|--------|-------|
| Modes      | ROR                 | Rw <sub>n</sub> , Rw <sub>m</sub> | 2C nm  | 2     |
|            | ROR                 | Rw <sub>n</sub> , #data4          | 3C #n  | 2     |

![](_page_123_Picture_1.jpeg)

SCXT Switch Context SCXT

Syntax SCXT op1, op2

**Operation**  $(tmp1) \leftarrow (op1)$ 

 $(tmp2) \leftarrow (op2)$   $(SP) \leftarrow (SP) - 2$  $((SP)) \leftarrow (tmp1)$ 

 $(op1) \leftarrow (tmp2)$ 

Data Types WORD

**Description** Used to switch contexts for any register. Switching context is a

push and load operation. The contents of the register specified by the first operand, op1, are pushed onto the stack. That register is then loaded with the value specified by the second operand, op2.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| - | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

C Not affected.

Not affected.

AddressingMnemonicFormatBytesModesSCXTreg, #data16C6 RR ## ##4SCXTreg, memD6 RR MM MM4

![](_page_124_Picture_1.jpeg)

SHL Shift Left SHL

**Syntax** 

SHL op1, op2

**Operation** 

 $(count) \leftarrow (op2)$ 

 $(C) \leftarrow 0$ 

DO WHILE (count)  $\neq$  0

 $(C) \leftarrow (op1_{15})$ 

 $(op1_n) \leftarrow (op1_{n-1}) [n = 1 \dots 15]$ 

 $(op1_0) \leftarrow 0$ 

 $(count) \leftarrow (count) - 1$ 

**END WHILE** 

**Data Types** 

WORD

**Description** 

Shifts the destination word operand op1 left by as many times as specified by the source operand op2. The least significant bits of the result are filled with zeros accordingly. The MSB is shifted into the Carry. Only shift values between 0 and 15 are allowed. When using a GPR as the count control, only the least significant 4 bits are used.

# Condition Flags

| Е | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | 0 | S | * |

**E** Always cleared.

**Z** Set if result equals zero. Cleared otherwise.

**V** Always cleared.

C The carry flag is set according to the last MSB shifted out of op1. Cleared for a shift count of zero.

N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | nic                               | Format | Bytes |
|------------|-------|-----------------------------------|--------|-------|
| Modes      | SHL   | Rw <sub>n</sub> , Rw <sub>m</sub> | 4C nm  | 2     |
|            | SHL   | Rw <sub>n</sub> . #data4          | 5C #n  | 2     |

![](_page_125_Picture_1.jpeg)

SHR Shift Right SHR

**Syntax** 

SHR op1, op2

**Operation** 

$$(count) \leftarrow (op2)$$

$$(C) \leftarrow 0$$

$$(V) \leftarrow 0$$

DO WHILE (count)  $\neq$  0

$$(V) \leftarrow (C) \lor (V)$$

$$(C) \leftarrow (op1_0)$$

$$(op1_n) \leftarrow (op1_{n+1}) [n = 0 ... 14]$$

$$(op1_{15}) \leftarrow 0$$

$$(count) \leftarrow (count) - 1$$

**END WHILE** 

#### **Data Types**

#### **WORD**

### **Description**

Shifts the destination word operand op1 right by as many times as specified by the source operand op2. The most significant bits of the result are filled with zeros accordingly. Since the bits shifted out effectively represent the remainder, the Overflow flag is used instead as a Rounding flag. This flag together with the Carry flag helps the user to determine whether the remainder bits lost were greater than, less than or equal to one half an LSB. Only shift values between 0 and 15 are allowed. When using a GPR as the count control, only the least significant 4 bits are used.

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | * | S | S | * |

- **E** Always cleared.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if in any cycle of the shift operation a '1' is shifted out of the carry flag. Cleared for a shift count of zero.
- C The carry flag is set according to the last LSB shifted out of op1. Cleared for a shift count of zero.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

![](_page_126_Picture_1.jpeg)

| SHR        | continued  |                                                               |                | SHR    |
|------------|------------|---------------------------------------------------------------|----------------|--------|
| Addressing | Mnemo      | nic                                                           | Format         | Bytes  |
| Modes      | SHR<br>SHR | Rw <sub>n</sub> , Rw <sub>m</sub><br>Rw <sub>n</sub> , #data4 | 6C nm<br>7C #n | 2<br>2 |

![](_page_127_Picture_1.jpeg)

SRST Software Reset SRST

Syntax SRST

**Operation** Software Reset

**Description** This instruction is used to perform a software reset. A software

reset has a similar effect on the microcontroller as an externally

applied hardware reset.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| 0 | 0 | 0 | 0 | 0 |

**E** Not affected.

**Z** Not affected.

V Not affected.

**C** Not affected.

Not affected.

AddressingMnemonicFormatBytesModesSRSTB7 48 B7 B74

![](_page_128_Picture_1.jpeg)

# **SRVWDT** Service Watchdog Timer

**SRVWDT** 

Syntax SRVWDT

**Operation** Service Watchdog Timer

**Description** This instruction services the Watchdog Timer. It reloads the high

order byte of the Watchdog Timer with a preset value and clears the low byte on every occurrence. Once this instruction has been

executed, the watchdog timer cannot be disabled.

**Note** To insure that this instruction is not accidentally executed, it is

implemented as a protected instruction.

Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| _ | - | - | - | - |

E Not affected.

**Z** Not affected.

V Not affected.

C Not affected.

Not affected.

AddressingMnemonicFormatBytesModesSRVWDTA7 58 A7 A74

![](_page_129_Picture_1.jpeg)

### SUB Integer Subtraction

**SUB** 

SyntaxSUBop1, op2Operation $(op1) \leftarrow (op1) - (op2)$ 

Data Types WORD

**Description** Perfo

Performs a 2's complement binary subtraction of the source operand specified by op2 from the destination operand specified by op1. The result is then stored in op1.

# Condition Flags

| <u>E</u> | Z | V | С | N |
|----------|---|---|---|---|
| *        | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- C Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemonic |                                       | Format      | Bytes |
|----------|---------------------------------------|-------------|-------|
| SUB      | Rw <sub>n</sub> , Rw <sub>m</sub>     | 20 nm       | 2     |
| SUB      | $Rw_n$ , $[Rw_i]$                     | 28 n:10ii   | 2     |
| SUB      | Rw <sub>n</sub> , [Rw <sub>i</sub> +] | 28 n:11ii   | 2     |
| SUB      | Rw <sub>n</sub> , #data3              | 28 n:0###   | 2     |
| SUB      | reg, #data16                          | 26 RR ## ## | 4     |
| SUB      | reg, mem                              | 22 RR MM MM | 4     |
| SUB      | mem, reg                              | 24 RR MM MM | 4     |

![](_page_130_Picture_1.jpeg)

### **SUBB**

### **Integer Subtraction**

**SUBB** 

SyntaxSUBBop1, op2Operation $(op1) \leftarrow (op1) - (op2)$ 

**Data Types** BYTE

**Description** Per

Performs a 2's complement binary subtraction of the source operand specified by op2 from the destination operand specified by op1. The result is then stored in op1.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemonic |                                       | Format      | Bytes |
|----------|---------------------------------------|-------------|-------|
| SUBB     | Rb <sub>n</sub> , Rb <sub>m</sub>     | 21 nm       | 2     |
| SUBB     | Rb <sub>n</sub> , [Rw <sub>i</sub> ]  | 29 n:10ii   | 2     |
| SUBB     | Rb <sub>n</sub> , [Rw <sub>i</sub> +] | 29 n:11ii   | 2     |
| SUBB     | Rb <sub>n</sub> , #data3              | 29 n:0###   | 2     |
| SUBB     | reg, #data8                           | 27 RR ## xx | 4     |
| SUBB     | reg, mem                              | 23 RR MM MM | 4     |
| SUBB     | mem, reg                              | 25 RR MM MM | 4     |

![](_page_131_Picture_1.jpeg)

### **SUBC**

### **Integer Subtraction with Carry**

**SUBC** 

**Syntax** 

SUBC op1, op2

**Operation** 

 $(op1) \leftarrow (op1) - (op2) - (C)$ 

**Data Types** 

**WORD** 

**Description** 

Performs a 2's complement binary subtraction of the source operand specified by op2 and the previously generated carry bit from the destination operand specified by op1. The result is then stored in op1. This instruction can be used to perform multiple precision arithmetic.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | S | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero and the previous Z flag was set. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemonic |                                       | Format      | Bytes |
|----------|---------------------------------------|-------------|-------|
| SUBC     | Rw <sub>n</sub> , Rw <sub>m</sub>     | 30 nm       | 2     |
| SUBC     | Rw <sub>n</sub> , [Rw <sub>i</sub> ]  | 38 n:10ii   | 2     |
| SUBC     | Rw <sub>n</sub> , [Rw <sub>i</sub> +] | 38 n:11ii   | 2     |
| SUBC     | Rw <sub>n</sub> , #data3              | 38 n:0###   | 2     |
| SUBC     | reg, #data16                          | 36 RR ## ## | 4     |
| SUBC     | reg, mem                              | 32 RR MM MM | 4     |
| SUBC     | mem, reg                              | 34 RR MM MM | 4     |

![](_page_132_Picture_1.jpeg)

### **SUBCB**

### **Integer Subtraction with Carry**

**SUBCB** 

Syntax SUBCB op1, op2

**Operation**  $(op1) \leftarrow (op1) - (op2) - (C)$ 

**Data Types** BYTE

Description

Performs a 2's complement binary subtraction of the source operand specified by op2 and the previously generated carry bit from the destination operand specified by op1. The result is then stored in op1. This instruction can be used to perform multiple precision arithmetic.

# Condition Flags

| E | Z | V | С | N |
|---|---|---|---|---|
| * | S | * | S | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero and the previous Z flag was set. Cleared otherwise.
- V Set if an arithmetic underflow occurred, i.e. the result cannot be represented in the specified data type. Cleared otherwise.
- **C** Set if a borrow is generated. Cleared otherwise.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemoni | ic                                    | Format      | Bytes |
|---------|---------------------------------------|-------------|-------|
| SUBCB   | Rb <sub>n</sub> , Rb <sub>m</sub>     | 31 nm       | 2     |
| SUBCB   | Rb <sub>n</sub> , [Rw <sub>i</sub> ]  | 39 n:10ii   | 2     |
| SUBCB   | Rb <sub>n</sub> , [Rw <sub>i</sub> +] | 39 n:11ii   | 2     |
| SUBCB   | Rb <sub>n</sub> , #data3              | 39 n:0###   | 2     |
| SUBCB   | reg, #data8                           | 37 RR ## xx | 4     |
| SUBCB   | reg, mem                              | 33 RR MM MM | 4     |
| SUBCB   | mem, reg                              | 35 RR MM MM | 4     |

![](_page_133_Picture_1.jpeg)

### TRAP Software Trap TRAP

Syntax TRAP op1

**Operation**  $(SP) \leftarrow (SP) - 2$   $((SP)) \leftarrow (PSW)$ 

IF (SYSCON.SGTDIS = 0) THEN

 $(SP) \leftarrow (SP) - 2$  $((SP)) \leftarrow (CSP)$ 

 $(CSP) \leftarrow 0$ END IF

(SP) ← (SP) - 2

 $((\mathsf{SP})) \leftarrow (\mathsf{IP})$ 

 $(IP) \leftarrow zero\_extend (op1 \times 4)$ 

### **Description**

Invokes a trap or interrupt routine based on the specified operand, op1. The invoked routine is determined by branching to the specified vector table entry point. This routine has no indication of whether it was called by software or hardware. System state is preserved identically to hardware interrupt entry except that the CPU priority level is not affected. The RETI, return from interrupt, instruction is used to resume execution after the trap or interrupt routine has completed. The CSP is pushed if segmentation is enabled. This is indicated by the SGTDIS bit in the SYSCON register.

# Condition Flags

| E | Z | V | C | N |
|---|---|---|---|---|
| _ | - | - | - | - |

E Not affected.

Z Not affected.

V Not affected.

**C** Not affected.

Not affected.

AddressingMnemonicFormatBytesModesTRAP#trap79B t:ttt02

![](_page_134_Picture_1.jpeg)

XOR Logical Exclusive OR XOR

Syntax XOR op1, op2

**Operation**  $(op1) \leftarrow (op1) \oplus (op2)$ 

Data Types WORD

**Description** Performs a bitwise logical EXCLUSIVE OR of the source operand

specified by op2 and the destination operand specified by op1. The

result is then stored in op1.

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- N Set if the most significant bit of the result is set. Cleared otherwise.

| Addressing | Mnemo | nic                                   | Format      | Bytes |
|------------|-------|---------------------------------------|-------------|-------|
| Modes      | XOR   | Rw <sub>n</sub> , Rw <sub>m</sub>     | 50 nm       | 2     |
|            | XOR   | Rw <sub>n</sub> , [Rw <sub>i</sub> ]  | 58 n:10ii   | 2     |
|            | XOR   | Rw <sub>n</sub> , [Rw <sub>i</sub> +] | 58 n:11ii   | 2     |
|            | XOR   | Rw <sub>n</sub> , #data3              | 58 n:0###   | 2     |
|            | XOR   | reg, #data16                          | 56 RR ## ## | 4     |
|            | XOR   | reg, mem                              | 52 RR MM MM | 4     |
|            | XOR   | mem, reg                              | 54 RR MM MM | 4     |

![](_page_135_Picture_1.jpeg)

### XORB Logical Exclusive OR

**XORB** 

Syntax XORB op1, op2

**Operation**  $(op1) \leftarrow (op1) \oplus (op2)$ 

**Data Types** BYTE

**Description** Performs a bitwise logical EXCLUSIVE OR of the source operand

specified by op2 and the destination operand specified by op1. The

result is then stored in op1.

# **Condition Flags**

| E | Z | V | С | N |
|---|---|---|---|---|
| * | * | 0 | 0 | * |

- E Set if the value of op2 represents the lowest possible negative number. Cleared otherwise. Used to signal the end of a table.
- **Z** Set if result equals zero. Cleared otherwise.
- V Always cleared.
- **C** Always cleared.
- **N** Set if the most significant bit of the result is set. Cleared otherwise.

| Mnemor | nic                                  | Format      | Bytes |
|--------|--------------------------------------|-------------|-------|
| XORB   | Rb <sub>n</sub> , Rb <sub>m</sub>    | 51 nm       | 2     |
| XORB   | Rb <sub>n</sub> , [Rw <sub>i</sub> ] | 59 n:10ii   | 2     |
| XORB   | $Rb_n$ , $[Rw_i+]$                   | 59 n:11ii   | 2     |
| XORB   | Rb <sub>n</sub> , #data3             | 59 n:0###   | 2     |
| XORB   | reg, #data8                          | 57 RR ## xx | 4     |
| XORB   | reg, mem                             | 53 RR MM MM | 4     |
| XORB   | mem, reg                             | 55 RR MM MM | 4     |
|        |                                      |             |       |

![](_page_136_Picture_1.jpeg)

### **6** Addressing Modes

The Infineon 16-bit microcontrollers provide a lot of powerful addressing modes for access to word, byte and bit data (short, long, indirect), or to specify the target address of a branch instruction (absolute, relative, indirect). The different addressing modes use different formats and cover different scopes.

### 6.1 Short Addressing Modes

All of these addressing modes use an implicit base offset address to specify a 24-bit physical address (18-bit address for the SAB 8XC166(W) devices).

Short addressing modes permit access to the GPR, SFR/ESFR or bit-addressable memory space by specifying just 8 bits within an instruction:

### Physical Address = Base Address + $\Delta \times$ Short Address

*Note:*  $\Delta$  *is 1 for byte GPRs,*  $\Delta$  *is 2 for word GPRs and SFRs/ESFRs.* 

Table 6 Short Addressing

| Mnemo<br>-nic | Physical             | Address                                 | Short<br>Range | Address<br>e                      | Scope   | of Access                      |
|---------------|----------------------|-----------------------------------------|----------------|-----------------------------------|---------|--------------------------------|
| Rw            | (CP)                 | + 2 × Rw                                | Rw             | = 0 15                            | GPRs    | (word)                         |
| Rb            | (CP)                 | + 1 × Rb                                | Rb             | = 0 15                            | GPRs    | (byte)                         |
| reg           | 00'FE00 <sub>H</sub> | + 2 × reg                               | reg            | = 00 <sub>H</sub> EF <sub>H</sub> | SFRs    | (word, low byte)               |
|               | 00'F000 <sub>H</sub> | + 2 × reg                               | reg            | = 00 <sub>H</sub> EF <sub>H</sub> | ESFRs   | (word, low byte) <sup>1)</sup> |
|               | (CP)                 | + 2 × (reg∧0F <sub>H</sub> )            | reg            | $= F0_H \dots FF_H$               | GPRs    | (word)                         |
|               | (CP)                 | + 1 × (reg∧0F <sub>H</sub> )            | reg            | = F0 <sub>H</sub> FF <sub>H</sub> | GPRs    | (byte)                         |
| bitoff        | 00'FD00 <sub>H</sub> | $+2 \times bitoff$                      | bitoff         | = 00 <sub>H</sub> 7F <sub>H</sub> | RAM     | bit word offset                |
|               | 00'FF00 <sub>H</sub> | + $2 \times (bitoff \land 7F_H)$        | bitoff         | = 80 <sub>H</sub> EF <sub>H</sub> | SFR     | bit word offset                |
|               | 00'F100 <sub>H</sub> | + 2 × (bitoff $\land$ 7F <sub>H</sub> ) | bitoff         | = 80 <sub>H</sub> EF <sub>H</sub> | ESFR    | bit word offset                |
|               | (CP)                 | + 2 × (bitoff $\land$ 0F <sub>H</sub> ) | bitoff         | = F0 <sub>H</sub> FF <sub>H</sub> | GPR     | bit word offset                |
| bitaddr       | Word offs            | et as with bitoff.                      | bitoff         | = 00 <sub>H</sub> FF <sub>H</sub> | Any sir | igle bit                       |
|               | Immediate            | e bit position.                         | bitpos         | s = 0 15                          |         |                                |

<sup>1)</sup> The Extended Special Function Register (ESFR) area is not available in the SAB 8XC166(W) devices.

![](_page_137_Picture_1.jpeg)

Rw, Rb:

Specifies direct access to any GPR in the currently active context (register bank). Both 'Rw' and 'Rb' require four bits in the instruction format. The base address of the current register bank is determined by the content of register CP. 'Rw' specifies a 4-bit word GPR address relative to the base address (CP), while 'Rb' specifies a 4 bit byte GPR address relative to the base address (CP).

reg:

Specifies direct access to any (E)SFR or GPR in the currently active context (register bank). 'reg' requires eight bits in the instruction format. Short 'reg' addresses from  $00_H$  to  $EF_H$  always specify (E)SFRs. In that case, the factor ' $\Delta$ ' equates 2 and the base address is 00'FE $00_H$  for the standard SFR area or 00'F0 $00_H$  for the extended ESFR area. 'reg' accesses to the ESFR area require a preceding EXT\*R instruction to switch the base address (not available in the SAB 8XC166(W) devices). Depending on the opcode of an instruction, either the total word (for word operations) or the low byte (for byte operations) of an SFR can be addressed via 'reg'. Note that the high byte of an SFR cannot be accessed via the 'reg' addressing mode. Short 'reg' addresses from F0 $_H$  to FF $_H$  always specify GPRs. In that case, only the lower four bits of 'reg' are significant for physical address generation, and thus it can be regarded as being identical to the address generation described for the 'Rb' and 'Rw' addressing modes.

bitoff:

Specifies direct access to any word in the bit-addressable memory space. 'bitoff' requires eight bits in the instruction format. Depending on the specified 'bitoff' range, different base addresses are used to generate physical addresses: Short 'bitoff' addresses from  $00_H$  to  $7F_H$  use 00'FD00 $_H$  as a base address, and thus they specify the 128 highest internal RAM word locations (00'FD00 $_H$  to 00'FDFE $_H$ ). Short 'bitoff' addresses from  $80_H$  to EF $_H$  use 00'FF00 $_H$  as a base address to specify the highest internal SFR word locations (00'FF00 $_H$  to 00'FFDE $_H$ ) or use 00'F100 $_H$  as a base address to specify the highest internal ESFR word locations (00'F100 $_H$  to 00'F1DE $_H$ ). 'bitoff' accesses to the ESFR area require a preceding EXT\*R instruction to switch the base address (not available in the SAB 8XC166(W) devices). For short 'bitoff' addresses from F0 $_H$  to FF $_H$ , only the lowest four bits and the contents of the CP register are used to generate the physical address of the selected word GPR.

bitaddr:

Any bit address is specified by a word address within the bit-addressable memory space (see 'bitoff'), and by a bit position ('bitpos') within that word. Thus, 'bitaddr' requires twelve bits in the instruction format.

![](_page_138_Picture_1.jpeg)

### 6.2 Long Addressing Mode

This addressing mode uses one of the four DPP registers to specify a physical 24-bit address (18-bit for the SAB 8XC166(W) devices). Any word or byte data within the entire address space can be accessed with this mode. An override mechanism for the DPP addressing scheme is also supported (see Section 6.4).

Note: Word accesses on odd byte addresses are not executed, but rather trigger a hardware trap.

After reset, the DPP registers are initialized in a way that all long addresses are directly mapped onto the identical physical addresses.

Any long 16-bit address consists of two portions, which are interpreted in different ways. Bits 13 ... 0 specify a 14-bit data page offset, while bits 15 ... 14 specify the Data Page Pointer (1 of 4), which is to be used to generate the physical 24-bit (or 18-bit) address (see Figure 2).

![](_page_138_Figure_8.jpeg)

Figure 2 Interpretation of a 16-bit Long Address

The supported address space is up to 16 MByte (256 KByte for the SAB 8XC166(W) devices), so only the lower ten bits (two bits, respectively) of the selected DPP register content are concatenated with the 14-bit data page offset to build the physical address.

User's Manual V2.0, 2001-03

![](_page_139_Picture_1.jpeg)

The long addressing mode is referred to by the mnemonic 'mem'.

Table 7 Long Addressing

| Mnemonic | Physica          | al Address                                                                                                                                              | Long Address<br>Range                                                                                                                                    | Scope of Access  |
|----------|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|------------------|
| mem      | (DPP1)<br>(DPP2) | II mem <sub>\</sub> 3FFF <sub>H</sub> II mem <sub>\</sub> 3FFF <sub>H</sub> II mem <sub>\</sub> 3FFF <sub>H</sub> II mem <sub>\</sub> 3FFF <sub>H</sub> | 0000 <sub>H</sub> 3FFF <sub>H</sub><br>4000 <sub>H</sub> 7FFF <sub>H</sub><br>8000 <sub>H</sub> BFFF <sub>H</sub><br>C000 <sub>H</sub> FFFF <sub>H</sub> | Any Word or Byte |
| mem      | pag              | II mem∧3FFF <sub>H</sub>                                                                                                                                | 0000 <sub>H</sub> FFFF <sub>H</sub> (14-bit)                                                                                                             | Any Word or Byte |
| mem      | seg              | II mem                                                                                                                                                  | 0000 <sub>H</sub> FFFF <sub>H</sub> (16-bit)                                                                                                             | Any Word or Byte |

### 6.3 Indirect Addressing Modes

These addressing modes can be regarded as a combination of short and long addressing modes. This means that long 16-bit addresses are specified indirectly by the contents of a word GPR, which is specified directly by a short 4-bit address ('Rw' = 0 to 15). There are indirect addressing modes, which add a constant value to the GPR contents before the long 16-bit address is calculated. Other indirect addressing modes allow decrementing or incrementing the indirect address pointers (GPR content) by 2 or 1 (referring to words or bytes).

In each case, one of the four DPP registers is used to specify physical 24-bit addresses (18-bit for the SAB 8XC166(W) devices). Any word or byte data within the entire memory space can be addressed indirectly.

Note: The exceptions for instructions EXTP(R) and EXTS(R), i.e. overriding the DPP mechanism, apply in the same way as described for the long addressing modes.

Some instructions only use the lowest four word GPRs ( $R_i = R3 ... R0$ ) as indirect address pointers, which are specified via short 2-bit addresses in that case.

Note: Word accesses on odd byte addresses are not executed, but rather trigger a hardware trap.

After reset, the DPP registers are initialized in a way that all indirect long addresses are directly mapped onto the identical physical addresses.

![](_page_140_Picture_1.jpeg)

Physical addresses are generated from indirect address pointers via the following algorithm:

1. Calculate the physical address of the word GPR, which is used as indirect address pointer, using the specified short address ('Rw') and the current register bank base address (CP).

**GPR Address = (CP) + 2**  $\times$  **Short Address** 

2. Pre-decremented indirect address pointers ('-Rw') are decremented by a data-type-dependent value ( $\Delta = 1$  for byte operations,  $\Delta = 2$  for word operations), before the long 16-bit address is generated:

(GPR Address) = (GPR Address) -  $\Delta$ ; [optional step!]

3. Calculate the long 16-bit address by adding a constant value (if selected) to the content of the indirect address pointer:

Long Address = (GPR Pointer) + Constant

4. Calculate the physical 24-bit (or 18-bit) address using the resulting long address and the corresponding DPP register content (see long 'mem' addressing modes).

Physical Address = (DPPi) + Page offset

5. Post-Incremented indirect address pointers ('Rw+') are incremented by a data-type-dependent value ( $\Delta$  = 1 for byte operations,  $\Delta$  = 2 for word operations):

**(GPR Pointer) = (GPR Pointer) +**  $\Delta$  ; [optional step!]

The following indirect addressing modes are provided:

 Table 8
 Indirect Addressing

| Mnemonic          | Particularities                                                                                                                              |
|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| [Rw]              | Most instructions accept any GPR (R15 R0) as indirect address pointer.  Some instructions, however, only accept the lower four GPRs (R3 R0). |
| [Rw+]             | The specified indirect address pointer is automatically post-incremented by 2 or 1 (for word or byte data operations) after the access.      |
| [-Rw]             | The specified indirect address pointer is automatically pre-decremented by 2 or 1 (for word or byte data operations) before the access.      |
| [Rw<br>+ #data16] | The specified 16-bit constant is added to the indirect address pointer, before the long address is calculated.                               |

User's Manual 136 V2.0, 2001-03

![](_page_141_Picture_1.jpeg)

#### 6.4 DPP Override Mechanism

The DPP override mechanism temporarily bypasses the standard DPP addressing scheme. The EXTP(R) and EXTS(R) instructions override this addressing mechanism. Instruction EXTP(R) replaces the content of the respective DPP register (i.e. the data page number) with a direct page number, while instruction EXTS(R) concatenates the complete 16-bit long address with the specified segment base address.

The overriding page or segment may be specified directly as a constant (#pag, #seg) or indirectly via a word GPR (Rw).

The override mechanism is valid for the number of instructions specified in the #irang parameter of the respective EXTend instruction.

![](_page_141_Figure_7.jpeg)

Figure 3 Overriding the DPP Mechanism

Note: The EXTend instruction (and hence the override mechanism) are not available in the SAB 8XC166(X) devices.

![](_page_142_Picture_1.jpeg)

#### 6.5 Constants within Instructions

The C166 Family instruction set also supports the use of wordwide or bytewide immediate constants. For an optimum utilization of the available code storage, these constants are represented in the instruction formats by either 3, 4, 8, or 16 bits. Thus, short constants are always zero-extended while long constants are truncated if necessary to match the data format required for the particular operation (see **Table 9**):

Table 9 Constants

| Mnemonic | Word Operation            | Byte Operation           |
|----------|---------------------------|--------------------------|
| #data3   | 0000 <sub>H</sub> + data3 | 00 <sub>H</sub> + data3  |
| #data4   | 0000 <sub>H</sub> + data4 | 00 <sub>H</sub> + data4  |
| #data8   | 0000 <sub>H</sub> + data8 | data8                    |
| #data16  | data16                    | data16 ∧ FF <sub>H</sub> |
| #mask    | 0000 <sub>H</sub> + mask  | mask                     |

Note: Immediate constants are always signified by a leading number sign '#'.

### 6.6 Instruction Range (#irang2)

The effect of the ATOMIC and EXTend instructions is valid only for a limited scope and can be defined for the following 1 ... 4 instructions. This instruction range (1 ... 4) is coded in the 2-bit constant #irang2 and is represented by the values 0 ... 3.

![](_page_143_Picture_1.jpeg)

### 6.7 Branch Target Addressing Modes

Different addressing modes are provided to specify the target address and segment of jump or call instructions. Relative, absolute and indirect modes can be used to update the Instruction Pointer register (IP), while the Code Segment Pointer register (CSP) can only be updated with an absolute value. A special mode is provided to address the interrupt and trap jump vector table, which resides in the lowest portion of code segment 0.

Table 10 Branch Addressing

| Mnemonic | Target Address                                                     | Target Segment     | Valid Address Range                                |
|----------|--------------------------------------------------------------------|--------------------|----------------------------------------------------|
| caddr    | (IP) = caddr                                                       | -current-          | caddr = 0000 <sub>H</sub> FFFE <sub>H</sub>        |
| rel      | $(IP) = (IP) + 2 \times rel$<br>$(IP) = (IP) - 2 \times (rel + 1)$ | -current-          | rel = $00_H \dots 7F_H$<br>rel = $80_H \dots FF_H$ |
| [Rw]     | $(IP) = ((CP) + 2 \times Rw)$                                      | -current-          | Rw = 0 15                                          |
| seg      | _                                                                  | (CSP) = seg        | seg = 0 255(3)                                     |
| #trap7   | $(IP) = 0000_{H} + 4 \times trap7$                                 | $(CSP) = 0000_{H}$ | trap7 = 00 <sub>H</sub> 7F <sub>H</sub>            |

![](_page_144_Picture_1.jpeg)

**caddr:** Specifies an absolute 16-bit code address within the current segment.

Branches MAY NOT be taken to odd code addresses. Therefore, the least significant bit of 'caddr' must always contain a '0', otherwise a hardware trap

would occur.

rel: This mnemonic represents an 8-bit signed word offset address relative to

the current Instruction Pointer contents, which points to the instruction after the branch instruction. Depending on the offset address range, either forward ('rel' =  $00_H$  to  $7F_H$ ) or backward ('rel' =  $80_H$  to  $FF_H$ ) branches are

possible.

The branch instruction itself is repeatedly executed, when 'rel' = '-1' ( $FF_H$ ) for a word-sized branch instruction, or 'rel' = '-2' ( $FE_H$ ) for a double-word-

sized branch instruction.

[Rw]: In this case, the 16-bit branch target instruction address is determined

indirectly by the content of a word GPR. In contrast to indirect data addresses, indirectly specified code addresses are NOT calculated via additional pointer registers (e.g. DPP registers). Branches MAY NOT be taken to odd code addresses. Therefore, the least significant bit of the address pointer GPR must always contain a '0', otherwise a hardware trap

would occur.

**seg:** Specifies an absolute code segment number. 256 different code segments are supported, where the eight bits of the 'seg' operand value are used for

updating the lower half of register CSP.

Note: The SAB 8XC166(W) devices support only 4 different code segments, where only the two lower bits of the 'seg' operand value

are used for updating the CSP register.

#trap7: Specifies a particular interrupt or trap number for branching to the

corresponding interrupt or trap service routine via a jump vector table. Trap numbers from  $00_H$  to  $7F_H$  can be specified, which allow to access any double word code location within the address range  $00'0000_H$  ...  $00'01FC_H$ 

in code segment 0 (i.e. the interrupt jump vector table).

For the association of trap numbers with the corresponding interrupt or trap sources please refer to chapter "Interrupt and Trap Functions" in the

respective User's Manual.

![](_page_145_Picture_1.jpeg)

### 7 Instruction State Times

Basically, the time to execute an instruction depends on where the instruction is fetched from, and where possible operands are read from or written to. The fastest processing mode is to execute a program fetched from the internal program memory (ROM, OTP, Flash). In that case most of the instructions can be processed within just one machine cycle, which is also the general minimum execution time.

All external memory accesses are performed by the on-chip External Bus Controller (EBC), which works in parallel with the CPU. Mostly, instructions from external memory cannot be processed as fast as instructions from the internal ROM, because some data transfers, which internally can be performed in parallel, have to be performed sequentially via the external interface. In contrast to execution from the internal program memory, the time required to process an external program additionally depends on the length of the instructions and operands, on the selected bus mode, and on the duration of an external memory cycle, which is partly selectable by the user.

Processing a program from the internal RAM space is not as fast as execution from the internal ROM area, but it offers a lot of flexibility (e.g. for loading temporary programs into the internal RAM via the chip's serial interface, or end-of-line programming via the bootstrap loader). Execution from the on-chip extension RAM (XRAM) is faster than execution from the internal RAM (IRAM).

The following description allows evaluating the minimum and maximum program execution times. This will be sufficient for most requirements. For an exact determination of the instructions' state times it is recommended to use the facilities provided by simulators or emulators.

In general the execution time of an instruction is composed of several additive units:

- The minimum instruction state time represents the number of clock cycles required to step through the instruction pipeline or to execute the instruction (MUL, DIV).
- Operand reads can increase the instruction's execution time if the operand ...
  - is read from the on-chip program memory space.
  - is read from the IRAM immediately after a preceding write to the IRAM.
  - is read from external resources (or from the XRAM) via the EBC.
- **Operand writes** can increase the instruction's execution time if the target is in the external memory and the write cycle conflicts with another external memory operation.
- Jumps to the on-chip program memory can increase the instruction's execution time if the jump target is a non-aligned doubleword-instruction.
- **Testing branch conditions** can increase the instruction's execution time if the previous instruction has written to register PSW.

User's Manual V2.0, 2001-03

![](_page_146_Picture_1.jpeg)

#### 7.1 Time Unit Definitions

This section defines the subsequently used time units, summarizes the minimum (standard) state times of the 16-bit microcontroller instructions, and describes the exceptions from that standard timing.

The following time units are used to describe the instructions' processing times:

 $f_{CPU}$ : CPU operating frequency, may vary depending on the employed device

type and on the actual operating mode of the device.

**State:** One state time is specified as the duration of one CPU clock period.

Henceforth, one State is used as the basic time unit, because it represents the shortest period of time which has to be considered for instruction timing

evaluations.

1 State =  $1/f_{CPU}$  [s] = 40 ns for  $f_{CPU}$  = 25 MHz

ACT: The ALE (Address Latch Enable) Cycle Time specifies the time required to perform one external memory access. One ALE Cycle Time consists of either two (for demultiplexed external bus modes) or three (for multiplexed external bus modes) state times plus a number of state times, which is determined by the number of waitstates programmed in the MCTC (Memory Cycle Time Control) and MTTC (Memory Tristate Time Control) bit fields of the SYSCON/BUSCONx registers.

In case of **demultiplexed** external bus modes:

1 ACT = 
$$(2 + (15 - MCTC) + (1 - MTTC))$$
 States  
= 80 ns ... 720 ns for  $f_{CPLI} = 25$  MHz

In case of **multiplexed** external bus modes:

1 ACT = 
$$(3 + (15 - MCTC) + (1 - MTTC))$$
 States  
= 120 ns ... 760 ns for  $f_{CPU} = 25$  MHz

The total time  $(T_{tot})$ , which a particular part of a program takes to be processed, can be calculated by the sum of the single instruction processing times  $(T_{IN})$  of the considered instructions plus an offset value of 6 state times which considers the solitary filling of the pipeline, as follows:

$$T_{tot} = T_{I1} + T_{I2} + ... + T_{IN} + 6$$
 States

The time  $T_{IN}$ , which a single instruction takes to be processed, consists of a minimum number of instruction states ( $T_{Imin}$ ) plus an additional number of instruction states and/ or ALE Cycle Times ( $T_{Iadd}$ ), as follows:

$$T_{IN} = T_{Imin} + T_{Iadd}$$

![](_page_147_Picture_1.jpeg)

#### 7.2 Minimum Execution Time

The minimum number of state times to process an instruction is required if the instruction is fetched from the internal program memory ( $T_{lmin}(PM)$ ). The minimum number of state times for instructions fetched from the internal RAM ( $T_{lmin}(RAM)$ ), or of ALE Cycle Times for instructions fetched from the external memory ( $T_{lmin}(ext)$ ), can be easily calculated by adding the indicated additional times.

Most of the 16-bit microcontroller instructions require a minimum of two state times - except some of the branches, the multiplication, the division, and a special move instruction. In case of execution from internal program memory there is no execution time dependency on the instruction length, except for some special branch situations. The injected target instruction of a cache jump instruction can be considered for timing evaluations as if being executed from the internal program memory, regardless of which memory area the rest of the current program is really fetched from.

For some of the branch instructions **Table 11** represents two execution times:

- standard execution time for a taken branch
- reduced execution time for a branch,
  - which is not taken, because the specified condition is not met
  - which can be serviced by the jump cache

The respective longer execution time result from the fact that after a taken branch the current instruction stream is broken and the pipeline has to be refilled.

**Table 11** Minimum Instruction State Times [Unit = States / ns]

| Instruction(s)          | T <sub>Imin</sub> (PM) [States] | T <sub>Imin</sub> (PM) [ns](@ 25 MHz) |
|-------------------------|---------------------------------|---------------------------------------|
| CALLI, CALLA            | 4 or 2                          | 160 or 80                             |
| CALLS, CALLR, PCALL     | 4                               | 160                                   |
| JB, JBC, JNB, JNBS      | 4 or 2                          | 160 or 80                             |
| JMPS                    | 4                               | 160                                   |
| JMPA, JMPI, JMPR        | 4 or 2                          | 160 or 80                             |
| MUL, MULU               | 10                              | 400                                   |
| DIV, DIVL, DIVU, DIVLU  | 20                              | 800                                   |
| RET, RETI, RETP, RETS   | 4                               | 160                                   |
| MOV[B] Rn, [Rm+#data16] | 4                               | 160                                   |
| TRAP                    | 4                               | 160                                   |
| All other instructions  | 2                               | 80                                    |

![](_page_148_Picture_1.jpeg)

#### Instructions executed from the internal RAM

The minimum instruction time (see **Table 11**) must be extended by an instruction-length dependent number of state times, as follows:

For 2-byte instructions:  $T_{Imin}(RAM) = T_{Imin}(PM) + 4$  States For 4-byte instructions:  $T_{Imin}(RAM) = T_{Imin}(PM) + 6$  States

### **Instructions executed from the External Memory**

The minimum instruction time (see **Table 11**) must be extended by an instruction-length dependent number of ALE Cycle Times. This number depends on the instruction length (2-byte or 4-byte) and on the data bus width (8-bit or 16-bit).

Accesses to the on-chip XRAM are controlled by the EBC and therefore also must be considered as external accesses.

For 2-byte instructions:  $T_{Imin}(ext) = T_{Imin}(PM) + 1$  ACT For 4-byte instructions:  $T_{Imin}(ext) = T_{Imin}(PM) + 2$  ACT

Note: For instructions fetched from external memory via an 8-bit data bus, the minimum number of required ALE Cycle Times is twice the given number (16-bit bus).

User's Manual V2.0, 2001-03

![](_page_149_Picture_1.jpeg)

#### 7.3 Additional State Times

In most cases the given execution time also includes the handling of the involved operands (if any). Some operand accesses, however, can extend the execution time of an instruction ( $T_{IN}$ ). Since the additional time  $T_{ladd}$  is mostly caused by internal instruction pipelining, it often will be possible to evade these timing effects in time-critical program modules by means of a suitable rearrangement of the corresponding instruction sequences. Simulators and emulators offer a lot of facilities, which support the user in optimizing his program whenever required.

### **Operand Reads from Internal Program Memory**

Both byte and word operand reads always require 2 additional state times.

```
-T_{ladd} = 2 States
```

### **Operand Reads from Internal RAM via Indirect Addressing Modes**

Reading a GPR or any other directly addressed operand within the internal RAM space does NOT cause additional state times. However, reading an indirectly addressed internal RAM operand will extend the processing time by 1 state time, if the preceding instruction auto-increments or auto-decrements a GPR as shown in the following example:

```
MOV R1, [R0+] ;auto-increment R0
MOV [R3], [R2] ;if R2 points into IRAM space: Tadd = 1 State
```

In this case, the additional time can easily be avoided by putting another suitable instruction before the instruction indirectly reading the internal RAM.

```
- T<sub>ladd</sub> = 0 or 1 State
```

#### **Operand Reads from an SFR**

Mostly, SFR read accesses do NOT require additional processing time. In some rare cases, however, either one or two additional state times will be caused by particular SFR operations, as follows:

Reading an SFR immediately after an instruction, which writes to the internal SFR space, as shown in the following example:

```
MOV T0, #1000h ; write to Timer 0
ADD R3, T1 ; read from Timer 1: Tadd = 1 State
```

Reading register PSW immediately after an instruction, which implicitly updates the condition flags, as shown in the following example:

```
ADD R0, #1000h ;implicit modification of PSW flags
BAND C, Z ;read from PSW: Tadd = 2 States
```

![](_page_150_Picture_1.jpeg)

Implicitly incrementing or decrementing register SP immediately after an instruction, which explicitly writes to register SP, as shown in the following example:

```
MOV SP, #0FB00h ; explicit update of the stack pointer
SCXT R1, #1000h ; implicit decrement of SP: Tadd = 2 States
```

In these cases, the extra state times can be avoided by putting other suitable instructions before the instruction  $I_{n+1}$  reading the SFR.

```
- T<sub>ladd</sub> = 0 or 1 or 2 State(s)
```

### **Operand Reads from External Memory**

Any external operand reading via a 16-bit wide data bus requires one additional ALE Cycle Time. Reading word operands via an 8-bit wide data bus takes twice as much time (2 ALE Cycle Times) as the reading of byte operands.

$$-$$
 T<sub>ladd</sub> = 1 or 2 ACT

### **Operand Writes to External Memory**

Writing an external operand via a 16-bit wide data bus takes one additional ALE Cycle Time. For timing calculations of external program parts, this extra time must always be considered. The value of  $T_{ladd}$  which must be considered for timing evaluations of internal program parts, may fluctuate between 0 state times and 1 ALE Cycle Time. This is because external writes are normally performed in parallel to other CPU operations. Thus,  $T_{ladd}$  could already have been considered in the standard processing time of another instruction. Writing a word operand via an 8-bit wide data bus requires twice as much time (2 ALE Cycle Times) as the writing of a byte operand.

```
- T<sub>ladd</sub> = 0 or 1 or 2 ACT
```

### **Testing Branch Conditions**

Mostly, NO extra time is required for conditional branch instructions to decide whether a branch condition is met or not. However, an additional state time is required if the preceding instruction writes to register PSW, as shown in the following example:

```
BSET USR0 ;Explicit write to PSW

JMPR cc_Z, JumpTarget ;Test condition flag in PSW: Tadd = 1 State
```

In this case, the extra state time can simply be intercepted by putting another suitable instruction before the conditional branch instruction.

```
-T_{ladd} = 0 or 1 State
```

User's Manual V2.0, 2001-03

![](_page_151_Picture_1.jpeg)

### **Jumps into the Internal Program Memory**

The minimum time of 4 state times for standard jumps into the internal ROM space will be extended by 2 additional state times, if the branch target is a double-word instruction at a non-aligned double-word location (xxx2<sub>H</sub>, xxx6<sub>H</sub>, xxxA<sub>H</sub>, xxxE<sub>H</sub>), as shown in the following example:

```
ORG #0FFEh ;Any non-aligned double-word location

LABEL JumpTarget

... ;Any double-word instruction

...

JMPA cc UC, JumpTarget ;If standard branch is taken: Tadd = 2 States
```

A cache jump, which normally requires just 2 state times, will be extended by 2 additional state times, if both the cached jump target instruction and its successor instruction are non-aligned double-word instructions, as shown in the following example:

```
ORG #12FAh ;Any non-aligned double-word location

LABEL JumpTarget

... ;Any double-word instruction

;Any double-word instruction

;Any double-word instruction

;Any double-word instruction

;Any double-word instruction

;If cache jump is taken: Tadd = 2 States
```

If required, these extra state times can be avoided by allocating double word jump target instructions to aligned double word addresses (xxx0<sub>H</sub>, xxx4<sub>H</sub>, xxx8<sub>H</sub>, xxxC<sub>H</sub>).

```
-T_{ladd} = 0 or 2 States
```

User's Manual V2.0, 2001-03

![](_page_152_Picture_1.jpeg)

**Keyword Index** 

### 8 Keyword Index

This section lists a number of keywords which refer to specific details of the C166 Family instruction set. This helps to quickly find the answer to specific questions about the C166 Family instruction set, e.g. addressing, encoding, etc.

| A                                | E                             |
|----------------------------------|-------------------------------|
| Additional state times 145       | Encoding of opcodes 22        |
| Addressing                       | Execution time 141            |
| indirect 135                     | additional states 145         |
| long 134                         | minimum 143                   |
| modes 36                         | Extend                        |
| short 132                        | instructions 39               |
| Addressing modes                 | operations 9                  |
| branch target 8, 139             |                               |
| data 8                           | F                             |
| Arithmetic instructions 10       | Flags 35                      |
| В                                | Format of instructions 31     |
| Bit manipulation instructions 15 |                               |
| Branch                           | Indirect addressing 135       |
| condition codes 9, 38            | Instruction                   |
| target addressing modes 8, 139   | execution time 141            |
|                                  | format 31                     |
| C                                | format symbols 37             |
| Call instructions 19             | overview 6                    |
| Compare instructions 16          | Instructions                  |
| Condition                        | arithmetic 10                 |
| code 9, 38                       | bit manipulation 15           |
| flags 35                         | compare and loop 16           |
| Constants 138                    | data move 17                  |
| Control instructions 20          | extend 39                     |
| D                                | jump and call 19              |
| _                                | logical 13                    |
| Data                             | return 20                     |
| addressing modes 8               | shift and rotate 16           |
| move instructions 17             | stack 21<br>system control 20 |
| types 34 DPP override 137        | System Control 20             |
| DIT OVERTICE TO!                 | J                             |
|                                  | Jump instructions 19          |

![](_page_153_Picture_1.jpeg)

**Keyword Index** 

Logical instructions 13 Long addressing 134 Loop instructions 16

### M

Minimum execution time 143 Mnemonic summary 10 Move instructions 17

### 0

Opcode
encoding 22
overview 4
Operands 33
Operators 32
Override mechanism 137
Overview
instruction 6
opcode 4

### P

**PSW 34** 

### R

Return instructions 20 Rotate instructions 16

### S

Shift instructions 16
Short addressing 132
Stack instructions 21
State times 141
Summary
mnemonics 10
Symbols for instr. format 37
System control instructions 20

### Infineon goes for Business Excellence

"Business excellence means intelligent approaches and clearly defined processes, which are both constantly under review and ultimately lead to good operating results.

Better operating results and business excellence mean less idleness and wastefulness for all of us, more professional success, more accurate information, a better overview and, thereby, less frustration and more satisfaction."

Dr. Ulrich Schumacher

http://www.infineon.com
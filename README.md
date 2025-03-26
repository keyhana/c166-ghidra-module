# C166 Ghidra Module

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A **Ghidra** module that adds support for disassembling and decompiling the **C166 architecture**. While still under development, this module correctly handles basic disassembly and decompilation tasks.

## Features

- **Disassembly**: C166 binaries.
- **Decompilation**: Basic C166 code decompilation.
- **Ghidra Integration**: Easy integration with Ghidra.

## Supported Instructions and CPU Features

| **Category**                    | **Status**                      |
|---------------------------------|---------------------------------|
| **Basic Instructions**          | Supported                       |
| **Extended Instructions**       | Supported                       |
| **Division and Multiplication** | Needs improvement in decompiler |
| **User Stack Model**            | Supported                       |


## Installation

1. **Download the Extension**: Visit the [Releases page](https://github.com/keyhana/c166-ghidra-module/releases) and download the latest `.zip` file.
2. **Install in Ghidra**:
    - Open Ghidra.
    - Go to `File` -> `Install Extensions...`.
    - Click `+` and select the downloaded `.zip` file.
    - Install the extension and restart Ghidra.

## Project Status

The module is in early development. Contributions and feedback are welcome to improve accuracy and expand functionality.

## License

Licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Feel free to fork the repo, create a branch, and submit a pull request with your improvements.

#!/bin/bash

# Destination directory where 'data' and 'Module.manifest' will be copied
DEST_DIR="/opt/ghidra/Ghidra/Processors/C166"

# Check if 'data' directory exists in CWD
if [ ! -d "data" ]; then
    echo "Error: 'data' directory does not exist in the current working directory."
    exit 1
fi

# Check if 'Module.manifest' file exists in CWD
if [ ! -f "Module.manifest" ]; then
    echo "Error: 'Module.manifest' file does not exist in the current working directory."
    exit 1
fi

# Check if destination directory exists
if [ -d "$DEST_DIR" ]; then
    # Remove existing destination directory
    echo "Removing existing destination directory $DEST_DIR..."
    rm -rf "$DEST_DIR"
fi

# Create destination directory
mkdir -p "$DEST_DIR"

# Copy 'data' directory and 'Module.manifest' file to destination
echo "Copying 'data' directory and 'Module.manifest' file to $DEST_DIR..."
cp -r "data" "$DEST_DIR/"
cp "Module.manifest" "$DEST_DIR/"

echo "Copy completed successfully."

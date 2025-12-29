---
id: 'cli'
title: 'CLI Tool'
sidebar_label: 'CLI Tool'
---

# Fesod CLI

Apache Fesod CLI is a command-line tool for processing Excel spreadsheets. It allows you to read, write, convert, and inspect spreadsheet files directly from the terminal.

## Features

- **Read**: Extract data from Excel files and output in JSON or CSV format
- **Write**: Create Excel files from JSON data
- **Convert**: Convert between different spreadsheet formats (XLS ↔ XLSX)
- **Info**: Display detailed information about spreadsheet files

## Installation

### Download

Download the latest release from the [Fesod Releases](https://github.com/apache/fesod/releases) page.

```bash
# Extract the distribution
tar -xzf apache-fesod-2.0.0-bin.tar.gz
cd apache-fesod-2.0.0-bin
```

### Directory Structure

```text
apache-fesod-2.0.0-bin/
├── bin/                    # Executable scripts
│   ├── fesod-cli           # Unix/Linux/macOS launcher
│   └── fesod-cli.bat       # Windows launcher
├── lib/                    # Fesod modules
├── lib/ext/                # Third-party dependencies
├── conf/                   # Configuration files
└── licenses/               # License files
```

### Requirements

- **Java 8** or higher
- Supported operating systems: Linux, macOS, Windows

### Verify Installation

```bash
# Unix/Linux/macOS
./bin/fesod-cli --version

# Windows
bin\fesod-cli.bat --version
```

## Quick Start

### Read Excel File

```bash
# Output as JSON (default)
fesod-cli read data.xlsx

# Output as CSV
fesod-cli read data.xlsx --format csv

# Read specific sheet
fesod-cli read data.xlsx --sheet "Sales Data"

# Save output to file
fesod-cli read data.xlsx --output result.json
```

### Convert File Format

```bash
# Convert XLS to XLSX
fesod-cli convert legacy.xls modern.xlsx

# Convert XLSX to XLS
fesod-cli convert data.xlsx data.xls
```

### Display File Information

```bash
fesod-cli info data.xlsx
```

Example output:

```json
{
  "fileName": "data.xlsx",
  "fileSize": 15360,
  "format": "XLSX",
  "sheets": [
    {
      "name": "Sheet1",
      "rows": 100,
      "columns": 5
    }
  ]
}
```

### Write Data to Excel

```bash
# Create Excel from JSON data
fesod-cli write data.json output.xlsx

# Specify sheet name
fesod-cli write data.json output.xlsx --sheet-name "Report"
```

## Commands Reference

### Global Options

| Option | Description |
|--------|-------------|
| `--help`, `-h` | Show help message |
| `--version`, `-V` | Show version information |
| `--verbose`, `-v` | Enable verbose logging |

### read

Read spreadsheet data and output in specified format.

```bash
fesod-cli read <file> [options]
```

**Arguments:**

| Argument | Description |
|----------|-------------|
| `<file>` | Input file path (required) |

**Options:**

| Option | Description | Default |
|--------|-------------|---------|
| `--format`, `-f` | Output format: `json`, `csv` | `json` |
| `--sheet`, `-s` | Sheet name or index | `0` (first sheet) |
| `--output`, `-o` | Output file path | stdout |
| `--all` | Read all sheets | `false` |

**Examples:**

```bash
# Read first sheet as JSON
fesod-cli read sales.xlsx

# Read specific sheet by name
fesod-cli read sales.xlsx --sheet "Q1 Report"

# Read specific sheet by index (0-based)
fesod-cli read sales.xlsx --sheet 2

# Read all sheets
fesod-cli read sales.xlsx --all

# Export as CSV
fesod-cli read sales.xlsx --format csv --output sales.csv
```

### write

Write data from JSON to spreadsheet.

```bash
fesod-cli write <input> <output> [options]
```

**Arguments:**

| Argument | Description |
|----------|-------------|
| `<input>` | Input data file (JSON) |
| `<output>` | Output spreadsheet file |

**Options:**

| Option | Description | Default |
|--------|-------------|---------|
| `--input-format` | Input data format: `json` | `json` |
| `--sheet-name` | Sheet name | `Sheet1` |

**Examples:**

```bash
# Create Excel from JSON
fesod-cli write data.json report.xlsx

# Custom sheet name
fesod-cli write data.json report.xlsx --sheet-name "Monthly Report"
```

**JSON Input Format:**

```json
[
  {"name": "Alice", "age": 30, "city": "New York"},
  {"name": "Bob", "age": 25, "city": "Los Angeles"},
  {"name": "Charlie", "age": 35, "city": "Chicago"}
]
```

### convert

Convert spreadsheet between different formats.

```bash
fesod-cli convert <input> <output>
```

**Arguments:**

| Argument | Description |
|----------|-------------|
| `<input>` | Input file path |
| `<output>` | Output file path |

**Supported Conversions:**

| From | To |
|------|-----|
| `.xls` | `.xlsx` |
| `.xlsx` | `.xls` |
| `.xlsx` | `.csv` |
| `.xls` | `.csv` |

**Examples:**

```bash
# XLS to XLSX
fesod-cli convert legacy.xls modern.xlsx

# XLSX to XLS
fesod-cli convert data.xlsx data.xls

# Excel to CSV
fesod-cli convert data.xlsx data.csv
```

### info

Display spreadsheet file information.

```bash
fesod-cli info <file>
```

**Arguments:**

| Argument | Description |
|----------|-------------|
| `<file>` | Input file path |

**Examples:**

```bash
fesod-cli info report.xlsx
```

### version

Display version information.

```bash
fesod-cli version
```

### help

Display help information.

```bash
# General help
fesod-cli help

# Command-specific help
fesod-cli help read
fesod-cli read --help
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `FESOD_HOME` | Installation directory | Auto-detected |
| `JAVA_HOME` | Java installation path | Auto-detected |
| `FESOD_JAVA_OPTS` | JVM options | `-Xms128m -Xmx1g` |

### Examples

```bash
# Increase memory for large files
export FESOD_JAVA_OPTS="-Xms256m -Xmx4g"
fesod-cli read large-file.xlsx

# Set Java home
export JAVA_HOME=/usr/lib/jvm/java-11
fesod-cli --version
```

### Configuration File

Configuration file location: `conf/default-config.yaml`

```yaml
# Default output format
output:
  format: json
  encoding: UTF-8

# Read options
read:
  defaultSheet: 0
  includeHeaders: true

# Write options
write:
  defaultSheetName: Sheet1
```

## Troubleshooting

### Common Errors

#### "Java is not installed or not in PATH"

**Solution:** Install Java 8 or higher, or set the `JAVA_HOME` environment variable.

```bash
# Linux/macOS
export JAVA_HOME=/path/to/jdk

# Windows
set JAVA_HOME=C:\Path\To\JDK
```

#### "Cannot find lib directory"

**Solution:** Set the `FESOD_HOME` environment variable.

```bash
export FESOD_HOME=/path/to/apache-fesod-2.0.0-bin
```

#### "OutOfMemoryError" when processing large files

**Solution:** Increase the heap size.

```bash
export FESOD_JAVA_OPTS="-Xms512m -Xmx4g"
fesod-cli read large-file.xlsx
```

#### Script execution error on Linux: "No such file or directory"

**Cause:** Windows line endings (CRLF) in the script file.

**Solution:**

```bash
# Convert to Unix line endings
dos2unix bin/fesod-cli
# Or
sed -i 's/\r$//' bin/fesod-cli
```

## Examples

### Batch Processing

```bash
#!/bin/bash
# Convert all XLS files in a directory to XLSX

for file in *.xls; do
    output="${file%.xls}.xlsx"
    fesod-cli convert "$file" "$output"
    echo "Converted: $file -> $output"
done
```

### Pipeline with jq

```bash
# Extract specific fields using jq
fesod-cli read data.xlsx | jq '.[] | {name, email}'

# Count rows
fesod-cli read data.xlsx | jq 'length'

# Filter by condition
fesod-cli read data.xlsx | jq '[.[] | select(.age > 30)]'
```

### Export Multiple Sheets

```bash
# Read all sheets and save to separate files
fesod-cli read multi-sheet.xlsx --all | \
  jq -c 'to_entries[]' | \
  while read sheet; do
    name=$(echo "$sheet" | jq -r '.key')
    echo "$sheet" | jq '.value' > "${name}.json"
  done
```

## See Also

- [Fesod Sheet API Documentation](/docs/sheet/read/simple)
- [Migration Guide](/docs/migration/from-fastexcel)
- [GitHub Repository](https://github.com/apache/fesod)

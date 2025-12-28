# Apache Fesod CLI Tool

Apache Fesod CLI 是一个命令行工具，用于快速处理电子表格文件。

## 功能特性

- ✅ **完全兼容 JDK 8+**
- ✅ **简单易用**: `fesod-cli read data.xlsx --format json`
- ✅ **跨平台支持**: Linux、macOS、Windows
- ✅ **零配置**: 解压即用
- ✅ **轻量级**: 打包后 ~30MB
- ✅ **模块化**: 支持未来扩展

## 快速开始

### 安装

下载并解压分发包：

```bash
tar -xzf fesod-cli-2.0.0-bin.tar.gz
cd fesod-cli-2.0.0
```

### 使用示例

#### 读取 Excel 文件

```bash
# 读取 Excel 文件并输出 JSON
./bin/fesod-cli read data.xlsx --format json

# 读取 Excel 文件并输出 CSV
./bin/fesod-cli read data.xlsx --format csv

# 读取 Excel 文件并输出 XML
./bin/fesod-cli read data.xlsx --format xml

# 读取指定 sheet
./bin/fesod-cli read data.xlsx --sheet 0 --format json

# 读取所有 sheet
./bin/fesod-cli read data.xlsx --all --format json

# 输出到文件
./bin/fesod-cli read data.xlsx --format json --output output.json
```

#### 转换文件格式

```bash
# 转换 Excel 格式
./bin/fesod-cli convert input.xls output.xlsx
```

#### 写入 Excel 文件

```bash
# 从 JSON 写入 Excel
./bin/fesod-cli write data.json output.xlsx --sheet-name "Sheet1"
```

#### 查看文件信息

```bash
# 显示文件信息
./bin/fesod-cli info data.xlsx
```

#### 查看版本信息

```bash
./bin/fesod-cli version
```

## 命令参考

### read

读取电子表格数据并输出为指定格式。

**用法：**
```bash
fesod-cli read <file> [选项]
```

**选项：**
- `--format, -f`: 输出格式 (json, csv, xml)，默认: json
- `--sheet, -s`: Sheet 名称或索引，默认: 0
- `--output, -o`: 输出文件路径，默认: stdout
- `--all`: 读取所有 sheet

### convert

在不同格式之间转换电子表格。

**用法：**
```bash
fesod-cli convert <input> <output>
```

### write

将 JSON/CSV 数据写入电子表格。

**用法：**
```bash
fesod-cli write <input> <output> [选项]
```

**选项：**
- `--input-format`: 输入数据格式 (json, csv)，默认: json
- `--sheet-name`: Sheet 名称，默认: Sheet1

### info

显示电子表格文件信息。

**用法：**
```bash
fesod-cli info <file>
```

### version

显示版本信息。

**用法：**
```bash
fesod-cli version
```

## 配置

CLI 工具支持通过配置文件进行配置。默认配置文件位置：`~/.fesod/config.yaml`

示例配置：

```yaml
defaults:
  outputFormat: json
  encoding: UTF-8

read:
  autoTrim: true
  ignoreEmptyRows: true

write:
  autoCreateDirectories: true
```

## 系统要求

- Java 8 或更高版本
- Linux、macOS 或 Windows

## 许可证

Apache License 2.0

## 更多信息

- 文档: https://fesod.apache.org/docs/cli
- 问题反馈: https://github.com/apache/fesod/issues


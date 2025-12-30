---
id: 'cli'
title: '命令行工具'
sidebar_label: '命令行工具'
---

# Fesod CLI 命令行工具

Apache Fesod CLI 是一个用于处理 Excel 电子表格的命令行工具。它可以让你直接在终端中读取、写入、转换和查看电子表格文件。

## 功能特性

- **读取 (read)**：从 Excel 文件中提取数据，输出为 JSON 或 CSV 格式
- **写入 (write)**：从 JSON 数据创建 Excel 文件
- **转换 (convert)**：在不同电子表格格式之间转换 (XLS ↔ XLSX)，支持多工作表转换
- **信息 (info)**：显示电子表格文件的详细信息

## 安装

### 下载

从 [Fesod 发布页面](https://github.com/apache/fesod/releases) 下载最新版本。

```bash
# 解压分发包
tar -xzf apache-fesod-2.0.0-bin.tar.gz
cd apache-fesod-2.0.0-bin
```

### 目录结构

```text
apache-fesod-2.0.0-bin/
├── bin/                    # 可执行脚本
│   ├── fesod-cli           # Unix/Linux/macOS 启动脚本
│   └── fesod-cli.bat       # Windows 启动脚本
├── lib/                    # Fesod 模块
├── lib/ext/                # 第三方依赖
├── conf/                   # 配置文件
└── licenses/               # 许可证文件
```

### 系统要求

- **Java 8** 或更高版本
- 支持的操作系统：Linux、macOS、Windows

### 验证安装

```bash
# Unix/Linux/macOS
./bin/fesod-cli --version

# Windows
bin\fesod-cli.bat --version
```

## 快速开始

### 读取 Excel 文件

```bash
# 输出为 JSON（默认）
fesod-cli read data.xlsx

# 输出为 CSV
fesod-cli read data.xlsx --format csv

# 读取指定工作表
fesod-cli read data.xlsx --sheet "销售数据"

# 保存输出到文件
fesod-cli read data.xlsx --output result.json
```

### 转换文件格式

```bash
# XLS 转 XLSX（所有工作表）
fesod-cli convert legacy.xls modern.xlsx

# XLSX 转 XLS（所有工作表）
fesod-cli convert data.xlsx data.xls

# 仅转换指定工作表
fesod-cli convert data.xlsx output.xlsx --sheet 0
```

### 显示文件信息

```bash
fesod-cli info data.xlsx
```

示例输出：

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

### 写入数据到 Excel

```bash
# 从 JSON 数据创建 Excel
fesod-cli write data.json output.xlsx

# 指定工作表名称
fesod-cli write data.json output.xlsx --sheet-name "报表"
```

## 命令参考

### 全局选项

| 选项 | 描述 |
|-----|------|
| `--help`, `-h` | 显示帮助信息 |
| `--version`, `-V` | 显示版本信息 |
| `--verbose`, `-v` | 启用详细日志 |

### read 命令

读取电子表格数据并以指定格式输出。

```bash
fesod-cli read <file> [options]
```

**参数：**

| 参数 | 描述 |
|-----|------|
| `<file>` | 输入文件路径（必需） |

**选项：**

| 选项 | 描述 | 默认值 |
|-----|------|-------|
| `--format`, `-f` | 输出格式：`json`, `csv` | `json` |
| `--sheet`, `-s` | 工作表名称或索引 | `0`（第一个工作表） |
| `--output`, `-o` | 输出文件路径 | 标准输出 |
| `--all` | 读取所有工作表 | `false` |

**示例：**

```bash
# 读取第一个工作表为 JSON
fesod-cli read sales.xlsx

# 按名称读取指定工作表
fesod-cli read sales.xlsx --sheet "第一季度报表"

# 按索引读取指定工作表（从 0 开始）
fesod-cli read sales.xlsx --sheet 2

# 读取所有工作表
fesod-cli read sales.xlsx --all

# 导出为 CSV（包含列标题）
fesod-cli read sales.xlsx --format csv --output sales.csv
```

:::info CSV 格式
当使用 `--format csv` 时，输出会包含从数据第一行提取的列标题，让 CSV 结构更易于理解。
:::

### write 命令

从 JSON 数据写入电子表格。

```bash
fesod-cli write <input> <output> [options]
```

**参数：**

| 参数 | 描述 |
|-----|------|
| `<input>` | 输入数据文件（JSON） |
| `<output>` | 输出电子表格文件 |

**选项：**

| 选项 | 描述 | 默认值 |
|-----|------|-------|
| `--input-format` | 输入数据格式：`json` | `json` |
| `--sheet-name` | 工作表名称 | `Sheet1` |

**示例：**

```bash
# 从 JSON 创建 Excel
fesod-cli write data.json report.xlsx

# 自定义工作表名称
fesod-cli write data.json report.xlsx --sheet-name "月度报表"
```

**JSON 输入格式：**

```json
[
  {"姓名": "张三", "年龄": 30, "城市": "北京"},
  {"姓名": "李四", "年龄": 25, "城市": "上海"},
  {"姓名": "王五", "年龄": 35, "城市": "广州"}
]
```

### convert 命令

在不同电子表格格式之间转换。

```bash
fesod-cli convert <input> <output> [options]
```

**参数：**

| 参数 | 描述 |
|-----|------|
| `<input>` | 输入文件路径 |
| `<output>` | 输出文件路径 |

**选项：**

| 选项 | 描述 | 默认值 |
|-----|------|-------|
| `--sheet`, `-s` | 要转换的工作表索引（从 0 开始） | 所有工作表 |
| `--sheet-name`, `-n` | 要转换的工作表名称 | 所有工作表 |
| `--all`, `-a` | 转换所有工作表（显式） | `true` |

**支持的转换：**

| 源格式 | 目标格式 |
|-------|---------|
| `.xls` | `.xlsx` |
| `.xlsx` | `.xls` |
| `.xlsx` | `.csv` |
| `.xls` | `.csv` |

**示例：**

```bash
# 转换所有工作表（默认行为）
fesod-cli convert legacy.xls modern.xlsx

# 仅转换第一个工作表（索引 0）
fesod-cli convert data.xlsx data.xls --sheet 0

# 按名称转换指定工作表
fesod-cli convert data.xlsx output.xlsx --sheet-name "销售数据"

# Excel 转 CSV（仅第一个工作表）
fesod-cli convert data.xlsx data.csv --sheet 0

# 显式转换所有工作表
fesod-cli convert multi-sheet.xlsx output.xlsx --all
```

:::tip 提示
默认情况下，`convert` 命令会转换输入文件的**所有工作表**以保持数据完整性。如果只需要特定工作表，请使用 `--sheet` 或 `--sheet-name` 选项。
:::

### info 命令

显示电子表格文件信息。

```bash
fesod-cli info <file>
```

**参数：**

| 参数 | 描述 |
|-----|------|
| `<file>` | 输入文件路径 |

**示例：**

```bash
fesod-cli info report.xlsx
```

### version 命令

显示版本信息。

```bash
fesod-cli version
```

### help 命令

显示帮助信息。

```bash
# 通用帮助
fesod-cli help

# 命令特定帮助
fesod-cli help read
fesod-cli read --help
```

## 配置

### 环境变量

| 变量 | 描述 | 默认值 |
|-----|------|-------|
| `FESOD_HOME` | 安装目录 | 自动检测 |
| `JAVA_HOME` | Java 安装路径 | 自动检测 |
| `FESOD_JAVA_OPTS` | JVM 选项 | `-Xms128m -Xmx1g` |

### 示例

```bash
# 增加内存以处理大文件
export FESOD_JAVA_OPTS="-Xms256m -Xmx4g"
fesod-cli read large-file.xlsx

# 设置 Java 路径
export JAVA_HOME=/usr/lib/jvm/java-11
fesod-cli --version
```

### 配置文件

配置文件位置：`conf/default-config.yaml`

```yaml
# 默认输出格式
output:
  format: json
  encoding: UTF-8

# 读取选项
read:
  defaultSheet: 0
  includeHeaders: true

# 写入选项
write:
  defaultSheetName: Sheet1
```

## 故障排除

### 常见错误

#### "Java is not installed or not in PATH"

**解决方案：** 安装 Java 8 或更高版本，或设置 `JAVA_HOME` 环境变量。

```bash
# Linux/macOS
export JAVA_HOME=/path/to/jdk

# Windows
set JAVA_HOME=C:\Path\To\JDK
```

#### "Cannot find lib directory"

**解决方案：** 设置 `FESOD_HOME` 环境变量。

```bash
export FESOD_HOME=/path/to/apache-fesod-2.0.0-bin
```

#### 处理大文件时出现 "OutOfMemoryError"

**解决方案：** 增加堆内存大小。

```bash
export FESOD_JAVA_OPTS="-Xms512m -Xmx4g"
fesod-cli read large-file.xlsx
```

#### Linux 上脚本执行错误："没有那个文件或目录"

**原因：** 脚本文件包含 Windows 行尾符（CRLF）。

**解决方案：**

```bash
# 转换为 Unix 行尾符
dos2unix bin/fesod-cli
# 或者
sed -i 's/\r$//' bin/fesod-cli
```

## 使用示例

### 批量处理

```bash
#!/bin/bash
# 将目录中所有 XLS 文件转换为 XLSX

for file in *.xls; do
    output="${file%.xls}.xlsx"
    fesod-cli convert "$file" "$output"
    echo "已转换: $file -> $output"
done
```

### 配合 jq 使用

```bash
# 使用 jq 提取特定字段
fesod-cli read data.xlsx | jq '.[] | {name, email}'

# 统计行数
fesod-cli read data.xlsx | jq 'length'

# 按条件筛选
fesod-cli read data.xlsx | jq '[.[] | select(.age > 30)]'
```

### 导出多个工作表

```bash
# 读取所有工作表并保存到单独文件
fesod-cli read multi-sheet.xlsx --all | \
  jq -c 'to_entries[]' | \
  while read sheet; do
    name=$(echo "$sheet" | jq -r '.key')
    echo "$sheet" | jq '.value' > "${name}.json"
  done
```

## 相关链接

- [Fesod Sheet API 文档](/docs/sheet/read/simple)
- [迁移指南](/docs/migration/from-fastexcel)
- [GitHub 仓库](https://github.com/apache/fesod)

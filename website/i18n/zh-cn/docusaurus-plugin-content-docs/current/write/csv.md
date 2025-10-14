---
id: 'csv'
title: 'CSV'
---

# 写入 CSV 文件

本章节介绍如何使用 FastExcel 来写入自定义 CSV 文件。

## 概述

FastExcel 通过不同的参数设计进行 CSV 的写入。其底层使用了 [Apache Commons CSV](https://commons.apache.org/proper/commons-csv)，也支持通过直接设置 [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) 进行设定来达成写入的目标。

主要的参数如下：

| 名称 | 默认值 | 描述 |
| :--- | :--- | :--- |
| `delimiter` | `,` (逗号) | 字段分隔符。推荐使用 `CsvConstant` 中预定义的常量，例如 `CsvConstant.AT` (`@`)、`CsvConstant.TAB` 等。 |
| `quote` | `"` (双引号) | 字段引用符号，推荐使用 `CsvConstant` 中预定义的常量，例如 `CsvConstant.DOUBLE_QUOTE` (`"`)。 |
| `recordSeparator` | `CRLF` | 记录（行）分隔符。根据操作系统不同而变化，例如 `CsvConstant.CRLF` (Windows) 或 `CsvConstant.LF` (Unix/Linux)。 |
| `nullString` | `null` | 用于表示 `null` 值的字符串。注意这与空字符串 `""` 不同。 |
| `escape` | `null` | 转义字符，确认是否进行特殊符号的转义。 |

---

## 参数详解与示例

下面将详细介绍每一个参数的用法，并提供代码示例。

### delimiter

`delimiter` 用于指定 CSV 文件中的字段分隔符。默认值为英文逗号 `,`。同时，FastExcel 提供了一些常量 `CsvConstant`，用于简化使用。

#### 代码示例

如果 CSV 文件使用 `\u0000` 作为分隔符，可以如下设置：

```java
@Test
public void delimiterDemo() {
    String csvFile = "path/to/your.csv";
    FastExcel.write(csvFile, DemoData.class)
            .csv()
            .delimiter(CsvConstant.UNICODE_EMPTY)
            .doWrite(data());
}
```

### quote

`quote` 用于指定包裹字段的引用符号。默认值为双引号 `"`。当字段内容本身包含分隔符或换行符时，建议设置。
> 注意不可和 `recordSeparator` 的设置重复，建议结合`QuoteMode`使用。

#### 代码示例

```java
@Test
public void quoteDemo() {
    String csvFile = "path/to/your.csv";
    FastExcel.write(csvFile, DemoData.class)
            .csv()
            .quote(CsvConstant.DOUBLE_QUOTE, QuoteMode.MINIMAL)
            .doWrite(data());
}
```

### recordSeparator

`recordSeparator` 用于指定文件中的换行符。不同操作系统的换行符可能不同（例如，Windows 使用 `CRLF`，而 Unix/Linux 使用 `LF`）。

#### 代码示例

```java
@Test
public void recordSeparatorDemo() {
    String csvFile = "path/to/your.csv";
    FastExcel.write(csvFile, DemoData.class)
            .csv()
            .recordSeparator(CsvConstant.LF)
            .doWrite(data());
}
```

### nullString

`nullString` 用于写入文件中将 `null` 值置换成特定字符串。例如，可以将 `null` 对象置换成字符串 `"N/A"`。

#### 代码示例

```java
@Test
public void nullStringDemo() {
    String csvFile = "path/to/your.csv";
    FastExcel.write(csvFile, DemoData.class)
            .csv()
            .nullString("N/A")
            .doWrite(data());
}
```

### escape

`escape` 用于指定转义字符。当使用了 `escape`，输出的 CSV 有包含会保留显示。

#### 代码示例

```java
@Test
public void escapeDemo() {
    String csvFile = "path/to/your.csv";
    FastExcel.write(csvFile, DemoData.class)
            .csv()
            .escape(CsvConstant.BACKSLASH)
            .doWrite(data());
}
```

## CSVFormat 设置详解与示例

支持直接构建一个 `CSVFormat` 对象。
> 目前 FastExcel 仍然支持，但并非最推荐的使用方法。

### 代码示例

```java
@Test
public void csvFormatDemo() {
    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(CsvConstant.AT).build();
    String csvFile = "path/to/your.csv";

    try (ExcelWriter excelWriter = FastExcel.write(csvFile, DemoData.class).excelType(ExcelTypeEnum.CSV).build()) {
        WriteWorkbookHolder writeWorkbookHolder = excelWriter.writeContext().writeWorkbookHolder();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        // 判断是否为CsvWorkbook实例
        if (workbook instanceof CsvWorkbook) {
            CsvWorkbook csvWorkbook = (CsvWorkbook) workbook;
            csvWorkbook.setCsvFormat(csvFormat);
            writeWorkbookHolder.setWorkbook(csvWorkbook);
        }
        WriteSheet writeSheet = FastExcel.writerSheet(0).build();
        excelWriter.write(data(), writeSheet);
    }
}
```

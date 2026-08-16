---
id: 'csv'
title: 'CSV'
---

# 读取 CSV 文件

本章节介绍如何使用 Fesod 来读取自定义 CSV 文件。

## 概述

Fesod 通过不同的参数设计进行 CSV
的读取。其底层使用了 [Apache Commons CSV](https://commons.apache.org/proper/commons-csv)
，也支持通过直接设置 [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html)
进行设定来达成读取的目标。

主要的参数如下：

| 名称                | 默认值       | 描述                                                                                    |
|:------------------|:----------|:--------------------------------------------------------------------------------------|
| `delimiter`       | `,` (逗号)  | 字段分隔符。推荐使用 `CsvConstant` 中预定义的常量，例如 `CsvConstant.AT` (`@`)、`CsvConstant.TAB` 等。       |
| `quote`           | `"` (双引号) | 字段引用符号，推荐使用 `CsvConstant` 中预定义的常量，例如 `CsvConstant.DOUBLE_QUOTE` (`"`)。                |
| `recordSeparator` | `CRLF`    | 记录（行）分隔符。根据操作系统不同而变化，例如 `CsvConstant.CRLF` (Windows) 或 `CsvConstant.LF` (Unix/Linux)。 |
| `nullString`      | `null`    | 用于表示 `null` 值的字符串。注意这与空字符串 `""` 不同。                                                   |
| `escape`          | `null`    | 转义字符，用于转义引用符号自身。                                                                      |
| `includeColumnIndexes` | `null`    | 指定需要读取的列索引列表（从 0 开始）。未指定的列将被跳过，且读取的数据会被重新映射为从 0 开始的连续索引。                   |

---

## 参数详解与示例

下面将详细介绍每一个参数的用法，并提供代码示例。

### delimiter

`delimiter` 用于指定 CSV 文件中的字段分隔符。默认值为英文逗号 `,`。同时，Fesod 提供了一些常量 `CsvConstant`，用于简化使用。

#### 代码示例

如果 CSV 文件使用 `\u0000` 作为分隔符，可以如下设置：

```java
@Test
public void delimiterDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .delimiter(CsvConstant.UNICODE_EMPTY)
            .doReadSync();
}
```

### quote

`quote` 用于指定包裹字段的引用符号。默认值为双引号 `"`。当字段内容本身包含分隔符或换行符时，建议设置。

> 注意不可和 `recordSeparator` 的设置重复，建议结合 `QuoteMode` 使用

#### 代码示例

```java
@Test
public void quoteDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .quote(CsvConstant.DOUBLE_QUOTE, QuoteMode.MINIMAL)
            .doReadSync();
}
```

### recordSeparator

`recordSeparator` 用于指定文件中的换行符。不同操作系统的换行符可能不同（例如，Windows 使用 `CRLF`，而 Unix/Linux 使用 `LF`）。

#### 代码示例

```java
@Test
public void recordSeparatorDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .recordSeparator(CsvConstant.LF)
            .doReadSync();
}
```

### nullString

`nullString` 用于定义文件中代表 `null` 值的特定字符串。例如，可以将字符串 `"N/A"` 解析为 `null` 对象。

#### 代码示例

```java
@Test
public void nullStringDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .nullString("N/A")
            .doReadSync();
}
```

### escape

`escape` 用于指定转义字符，当引用符号（`quote`）本身出现在字段值中时，可以使用转义字符来处理。

#### 代码示例

```java
@Test
public void escapeDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .escape(CsvConstant.BACKSLASH)
            .doReadSync();
}
```

### includeColumnIndexes

`includeColumnIndexes` 用于指定只读取 CSV 文件中的某些列。未选中的列会在解析过程中被跳过，且解析出的列索引会被重新映射为从 0 开始的连续索引。

#### 代码示例

```java
@Test
public void includeColumnIndexesDemo() {
        String csvFile = "path/to/your.csv";
        // 指定读取第 0、2、4 列（例如第 1、3、5 列）
        List<Integer> includeColumnIndexes = Arrays.asList(0, 2, 4);

        try (ExcelReader excelReader = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener()).build()) {
            ReadSheet readSheet = FesodSheet.readSheet(0)
                                            .includeColumnIndexes(includeColumnIndexes)
                                            .build();
           excelReader.read(readSheet);
        }
}
```

## CSVFormat 设置详解与示例

支持直接构建一个 `CSVFormat` 对象。
> 目前 Fesod 仍然支持，但并非最推荐的使用方法。

### 代码示例

```java
@Test
public void csvFormatDemo() {

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(CsvConstant.AT).build();
    String csvFile = "path/to/your.csv";

    try (ExcelReader excelReader = FesodSheet.read(csvFile, DemoData.class, new DemoDataListener()).build()) {
        ReadWorkbookHolder readWorkbookHolder = excelReader.analysisContext().readWorkbookHolder();
        // 判断是否为CsvReadWorkbookHolder实例
        if (readWorkbookHolder instanceof CsvReadWorkbookHolder) {
            CsvReadWorkbookHolder csvReadWorkbookHolder = (CsvReadWorkbookHolder) readWorkbookHolder;
            csvReadWorkbookHolder.setCsvFormat(csvFormat);
        }
        ReadSheet readSheet = FesodSheet.readSheet(0).build();
        excelReader.read(readSheet);
    }
}
```

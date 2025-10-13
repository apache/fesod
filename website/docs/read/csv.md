---
id: 'csv'
title: 'CSV'
---

# Reading CSV Files

This chapter introduces how to use FastExcel to read custom CSV files.

## Overview

FastExcel reads CSV files through different parameter configurations. It uses [Apache Commons CSV](https://commons.apache.org/proper/commons-csv) as the underlying implementation and also supports direct configuration through [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html) settings to achieve reading objectives.

The main parameters are as follows:

| Name | Default Value | Description |
| :--- | :--- | :--- |
| `delimiter` | `,` (comma) | Field delimiter. It's recommended to use predefined constants from `CsvConstant`, such as `CsvConstant.AT`(`@`), `CsvConstant.TAB`, etc. |
| `quote` | `"` (double quote) | Field quote character. It's recommended to use predefined constants from `CsvConstant`, such as `CsvConstant.DOUBLE_QUOTE`(`"`). |
| `recordSeparator` | `CRLF` | Record (line) separator. Varies by operating system, such as `CsvConstant.CRLF`(Windows) or `CsvConstant.LF`(Unix/Linux). |
| `nullString` | `null` | String used to represent `null` values. Note this is different from an empty string `""`. |
| `escape` | `null` | Escape character used to escape quote characters themselves. |

---

## Parameter Details and Examples

The following sections will explain each parameter in detail with code examples.

### delimiter

`delimiter` specifies the field separator in CSV files. The default value is a comma `,`.
Additionally, FastExcel provides constants in `CsvConstant` to simplify usage.

#### Code Example

If the CSV file uses `\u0000` as the separator, you can configure it as follows:

```java
@Test
public void delimiterDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FastExcel.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .delimiter(CsvConstant.UNICODE_EMPTY)
            .doReadSync();
}
```

### quote

`quote` specifies the quote character that wraps fields. The default value is double quote `"`.
This should be set when field content contains delimiters or line breaks.

> Note: This cannot be the same as the `recordSeparator` setting. It's recommended to use in combination with `QuoteMode`.

#### Code Example

```java
@Test
public void quoteDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FastExcel.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .quote(CsvConstant.DOUBLE_QUOTE, QuoteMode.MINIMAL)
            .doReadSync();
}
```

### recordSeparator

`recordSeparator` specifies the line separator in the file. Different operating systems may use different line separators (for example, Windows uses `CRLF`, while Unix/Linux uses `LF`).

#### Code Example

```java
@Test
public void recordSeparatorDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FastExcel.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .recordSeparator(CsvConstant.LF)
            .doReadSync();
}
```

### nullString

`nullString` defines a specific string in the file that represents `null` values. For example, you can parse the string `"N/A"` as a `null` object.

#### Code Example

```java
@Test
public void nullStringDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FastExcel.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .nullString("N/A")
            .doReadSync();
}
```

### escape

`escape` specifies an escape character that can be used when quote characters (`quote`) appear within field values.

#### Code Example

```java
@Test
public void escapeDemo() {
    String csvFile = "path/to/your.csv";
    List<DemoData> dataList = FastExcel.read(csvFile, DemoData.class, new DemoDataListener())
            .csv()
            .escape(CsvConstant.BACKSLASH)
            .doReadSync();
}
```

## CSVFormat Configuration Details and Examples

Supports directly building a `CSVFormat` object.
> FastExcel currently still supports this approach, but it's not the most recommended usage method.

### Code Example

```java
@Test
public void csvFormatDemo() {

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(CsvConstant.AT).build();
    String csvFile = "path/to/your.csv";

    try (ExcelReader excelReader = FastExcel.read(csvFile, DemoData.class, new DemoDataListener()).build()) {
        ReadWorkbookHolder readWorkbookHolder = excelReader.analysisContext().readWorkbookHolder();
        // Check if it's an instance of CsvReadWorkbookHolder
        if (readWorkbookHolder instanceof CsvReadWorkbookHolder) {
            CsvReadWorkbookHolder csvReadWorkbookHolder = (CsvReadWorkbookHolder) readWorkbookHolder;
            csvReadWorkbookHolder.setCsvFormat(csvFormat);
        }

        ReadSheet readSheet = FastExcel.readSheet(0).build();
        excelReader.read(readSheet);
    }
}
```

---
id: 'csv'
title: 'CSV'
---

# Writing CSV Files

This chapter introduces how to use Fesod to write custom CSV files.

## Overview

Fesod writes CSV files through different parameter configurations. It
uses [Apache Commons CSV](https://commons.apache.org/proper/commons-csv) as the underlying implementation and also
supports direct configuration
through [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html)
settings to achieve writing objectives.

The main parameters are as follows:

| Name              | Default Value      | Description                                                                                                                               |
|:------------------|:-------------------|:------------------------------------------------------------------------------------------------------------------------------------------|
| `delimiter`       | `,` (comma)        | Field delimiter. It's recommended to use predefined constants from `CsvConstant`, such as `CsvConstant.AT` (`@`), `CsvConstant.TAB`, etc. |
| `quote`           | `"` (double quote) | Field quote character. It's recommended to use predefined constants from `CsvConstant`, such as `CsvConstant.DOUBLE_QUOTE` (`"`).         |
| `recordSeparator` | `CRLF`             | Record (line) separator. Varies by operating system, such as `CsvConstant.CRLF` (Windows) or `CsvConstant.LF` (Unix/Linux).               |
| `nullString`      | `null`             | String used to represent `null` values. Note this is different from an empty string `""`.                                                 |
| `escape`          | `null`             | Escape character, determines whether to escape special characters.                                                                        |

---

## Parameter Details and Examples

The following sections will explain each parameter in detail with code examples.

### delimiter

`delimiter` specifies the field separator in CSV files. The default value is a comma `,`. Additionally, Fesod
provides constants in `CsvConstant` to simplify usage.

#### Code Example

If the CSV file uses `\u0000` as the separator, you can configure it as follows:

```java

@Test
public void delimiterDemo() {
    String csvFile = "path/to/your.csv";
    FesodSheet.write(csvFile, DemoData.class)
            .csv()
            .delimiter(CsvConstant.UNICODE_EMPTY)
            .doWrite(data());
}
```

### quote

`quote` specifies the quote character that wraps fields. The default value is double quote `"`. This should be set when
field content contains delimiters or line breaks.
> Note: This cannot be the same as the `recordSeparator` setting. It's recommended to use in combination with
`QuoteMode`.

#### Code Example

```java

@Test
public void quoteDemo() {
    String csvFile = "path/to/your.csv";
    FesodSheet.write(csvFile, DemoData.class)
            .csv()
            .quote(CsvConstant.DOUBLE_QUOTE, QuoteMode.MINIMAL)
            .doWrite(data());
}
```

### recordSeparator

`recordSeparator` specifies the line separator in the file. Different operating systems may use different line
separators (for example, Windows uses `CRLF`, while Unix/Linux uses `LF`).

#### Code Example

```java

@Test
public void recordSeparatorDemo() {
    String csvFile = "path/to/your.csv";
    FesodSheet.write(csvFile, DemoData.class)
            .csv()
            .recordSeparator(CsvConstant.LF)
            .doWrite(data());
}
```

### nullString

`nullString` is used to replace `null` values with a specific string when writing to the file.
For example, you can replace `null` objects with the string `"N/A"`.

#### Code Example

```java

@Test
public void nullStringDemo() {
    String csvFile = "path/to/your.csv";
    FesodSheet.write(csvFile, DemoData.class)
            .csv()
            .nullString("N/A")
            .doWrite(data());
}
```

### escape

`escape` specifies the escape character. When `escape` is used, the output CSV will preserve and display the content.

#### Code Example

```java

@Test
public void escapeDemo() {
    String csvFile = "path/to/your.csv";
    FesodSheet.write(csvFile, DemoData.class)
            .csv()
            .escape(CsvConstant.BACKSLASH)
            .doWrite(data());
}
```

## CSVFormat Configuration Details and Examples

Supports directly building a `CSVFormat` object.
> Fesod currently still supports this approach, but it's not the most recommended usage method.

### Code Example

```java

@Test
public void csvFormatDemo() {
    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(CsvConstant.AT).build();
    String csvFile = "path/to/your.csv";

    try (ExcelWriter excelWriter = FesodSheet.write(csvFile, DemoData.class).excelType(ExcelTypeEnum.CSV).build()) {
        WriteWorkbookHolder writeWorkbookHolder = excelWriter.writeContext().writeWorkbookHolder();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        // Check if it's an instance of CsvWorkbook
        if (workbook instanceof CsvWorkbook) {
            CsvWorkbook csvWorkbook = (CsvWorkbook) workbook;
            csvWorkbook.setCsvFormat(csvFormat);
            writeWorkbookHolder.setWorkbook(csvWorkbook);
        }
        WriteSheet writeSheet = FesodSheet.writerSheet(0).build();
        excelWriter.write(data(), writeSheet);
    }
}
```

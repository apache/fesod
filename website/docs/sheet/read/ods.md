---
id: 'ods'
title: 'ODS'
---

# ODS File Format Support

This chapter introduces how to read and write ODS (OpenDocument Spreadsheet) files using Fesod.

## Overview

ODS (OpenDocument Spreadsheet) is an open standard spreadsheet file format defined by OASIS. It is widely used in:

- LibreOffice Calc
- Apache OpenOffice Calc
- Google Sheets (export format)
- Many other open-source office suites

Fesod provides full support for reading and writing ODS files using the same API as other formats (XLSX, XLS, CSV).

## Reading ODS Files

### Basic Reading

Reading ODS files follows the same pattern as reading other spreadsheet formats:

```java
@Test
public void readOds() {
    String fileName = "path/to/demo.ods";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

### Explicit Type Specification

When reading from an InputStream without a file extension, you can explicitly specify the file type:

```java
@Test
public void readOdsFromStream() {
    InputStream inputStream = getOdsInputStream();

    FesodSheet.read(inputStream, DemoData.class, new DemoDataListener())
            .excelType(ExcelTypeEnum.ODS)
            .sheet()
            .doRead();
}
```

### Multiple Sheets

ODS files support multiple sheets, and you can read them the same way as other formats:

```java
@Test
public void readMultipleSheets() {
    String fileName = "path/to/demo.ods";

    // Read all sheets
    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .doReadAll();

    // Or read specific sheets by index
    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet(0)  // First sheet
            .doRead();
}
```

## Writing ODS Files

### Basic Writing

Writing ODS files is straightforward:

```java
@Test
public void writeOds() {
    String fileName = "path/to/output.ods";
    List<DemoData> dataList = generateData();

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(dataList);
}
```

### Explicit Type Specification

You can explicitly specify the output type:

```java
@Test
public void writeOdsExplicit() {
    String fileName = "path/to/output.ods";
    List<DemoData> dataList = generateData();

    FesodSheet.write(fileName, DemoData.class)
            .excelType(ExcelTypeEnum.ODS)
            .sheet("MySheet")
            .doWrite(dataList);
}
```

### Writing to OutputStream

When writing to an OutputStream, explicitly specify the file type:

```java
@Test
public void writeOdsToStream() throws IOException {
    OutputStream outputStream = new FileOutputStream("output.ods");
    List<DemoData> dataList = generateData();

    FesodSheet.write(outputStream, DemoData.class)
            .excelType(ExcelTypeEnum.ODS)
            .sheet("Sheet1")
            .doWrite(dataList);
}
```

## Supported Features

| Feature | Support Status |
|---------|---------------|
| Basic Read/Write | ✅ Full Support |
| Multiple Sheets | ✅ Full Support |
| String Data | ✅ Full Support |
| Numeric Data | ✅ Full Support |
| Date/Time Data | ✅ Full Support |
| Boolean Data | ✅ Full Support |
| Formulas | ⚠️ Basic Support |
| Styles | ⚠️ Basic Support |
| Images | ⚠️ Limited Support |
| Comments | ⚠️ Limited Support |
| Encryption | ❌ Not Supported |

## Dependencies

ODS support is provided through the Apache ODF Toolkit. The dependency is automatically included when you use Fesod:

```xml
<dependency>
    <groupId>org.odftoolkit</groupId>
    <artifactId>odfdom-java</artifactId>
</dependency>
```

## Notes

1. **File Detection**: ODS files are automatically detected by their `.ods` extension. When reading from streams without a file extension, Fesod will automatically detect ODS format by checking the ZIP internal structure (ODS files contain a `mimetype` file or `content.xml`). However, it's recommended to use `excelType(ExcelTypeEnum.ODS)` to specify the format explicitly for better performance and reliability.

2. **1904 Date System**: ODS format uses the ISO 8601 date standard (1900 date system) and does not support Excel's 1904 date windowing. If you set `use1904windowing(true)` when writing ODS files, a warning will be logged and the setting will be ignored. ODS always uses the standard 1900 date system.

3. **Performance**: ODS reading and writing performance is comparable to XLSX for typical use cases.

4. **Compatibility**: Files created by Fesod are compatible with LibreOffice, OpenOffice, and other applications that support the ODF standard.

5. **Formula Syntax**: ODS uses a different formula syntax than Excel. Cross-format formula conversion is not automatically performed.

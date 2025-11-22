---
id: 'sheet'
title: 'Sheet'
---

# Sheet

This chapter introduces how to configure Sheets to read data.

## Reading Multiple Sheets

### Overview

You can read multiple sheets from a spreadsheet file, but the same sheet cannot be read repeatedly.

### Code Example

#### Reading All Sheets

```java

@Test
public void readAllSheet() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
}
```

---

## Reading Specific Sheets

### Overview

You can read a specific sheet from a spreadsheet file, supporting specification by sheet index or name.

> **Note:** Sheet names are limited to 31 characters in Microsoft Excel. When reading sheets by name, use the actual
> sheet name that appears in the Microsoft Excel file.

### Code Example

```java

@Test
public void readSingleSheet() {
    String fileName = "path/to/demo.xlsx";

    try (ExcelReader excelReader = FesodSheet.read(fileName).build()) {
        // Sheet index
        ReadSheet sheet1 = FesodSheet.readSheet(0).head(DemoData.class)
                .registerReadListener(new DemoDataListener()).build();
        // Sheet name
        ReadSheet sheet2 = FesodSheet.readSheet("Sheet2").head(DemoData.class)
                .registerReadListener(new DemoDataListener()).build();
        excelReader.read(sheet1, sheet2);
    }
}
```

---

## Ignoring Hidden Sheets

### Overview

By setting the `ignoreHiddenSheet` parameter to true, data from sheets in "hidden" state will not be read.
This supports both **"normal hidden"** and **"very hidden"** states.

### Code Example

```java

@Test
public void exceptionRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .ignoreHiddenSheet(Boolean.TRUE)
            .sheet()
            .doRead();
}
```

> In Microsoft Excel, Sheets have two hidden states: "normal hidden (xlSheetHidden)" and "very hidden (
> xlSheetVeryHidden)". Very hidden can be set through `VBA`, and in this case, the hidden sheet cannot be unhidden
> through
> the "Unhide" operation.

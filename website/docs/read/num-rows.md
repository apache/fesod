---
id: 'num-rows'
title: 'Rows'
---

# Reading Rows
This chapter introduces how to read a specified number of rows.

## Overview

During data analysis and processing, quickly viewing the first few rows of an Excel file can help us better understand the data structure and content. 
By default, FastExcel reads all data from the entire Excel file. 
However, by setting the `numRows` parameter, you can limit the number of rows to read. 0 means no limit on the number of rows, i.e., read all rows. The row count includes header rows.

## All Sheets

### Code Example

```java
@Test
public void allSheetRead() {
    // Read the first 100 rows
    FastExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
            for (DemoData demoData : dataList) {
                log.info("Read one record: {}", JSON.toJSONString(demoData));
            }
        })).numRows(100).sheet().doRead();
}
```

---

## Single Sheet

### Code Example
```java
@Test
public void singleSheetRead() {
    try (ExcelReader excelReader = FastExcel.read(fileName, DemoData.class, new DemoDataListener()).build()) {
        ReadSheet readSheet = FastExcel.readSheet(0).build();
        readSheet.setNumRows(100); // Read the first 100 rows
        excelReader.read(readSheet);
    }
}
```

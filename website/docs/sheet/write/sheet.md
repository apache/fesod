---
id: 'sheet'
title: 'Sheet'
---

# Sheet

This chapter introduces how to configure Sheets to write data.

> **Note:** Excel limits sheet names to 31 characters. Longer names are automatically trimmed with a warning logged.

## Writing to the Same Sheet

### Overview

Write data in batches to the same Sheet.

### Code Example

```java

@Test
public void writeSingleSheet() {
    String fileName = "repeatedWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        for (int i = 0; i < 5; i++) {
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

![img](/img/docs/write/repeatedWrite.png)

---

## Writing to Multiple Sheets

### Overview

Write data in batches to multiple Sheets, enabling paginated writing for large data volumes.

### Code Example

```java

@Test
public void writeMultiSheet() {
    String fileName = "repeatedWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        for (int i = 0; i < 5; i++) {
            WriteSheet writeSheet = FesodSheet.writerSheet(i, "Sheet" + i).build();
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

![img](/img/docs/write/repeatedWrite.png)

---

## Writing Using Tables

### Overview

Supports using multiple Tables within a single Sheet for segmented writing.

### Code Example

```java

@Test
public void tableWrite() {
    String fileName = "tableWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Table示例").build();
        WriteTable table1 = FesodSheet.writerTable(0).build();
        WriteTable table2 = FesodSheet.writerTable(1).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### Result

![img](/img/docs/write/tableWrite.png)

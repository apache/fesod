---
id: 'sheet'
title: 'Sheet'
---

# Sheet

This chapter introduces how to configure Sheets to write data.

## Writing to the Same Sheet

### Overview

Write data in batches to the same Sheet.

### Code Example

```java
@Test
public void writeSingleSheet() {
    String fileName = "repeatedWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FastExcel.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FastExcel.writerSheet("Sheet1").build();
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

    try (ExcelWriter excelWriter = FastExcel.write(fileName, DemoData.class).build()) {
        for (int i = 0; i < 5; i++) {
            WriteSheet writeSheet = FastExcel.writerSheet(i, "Sheet" + i).build();
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

    try (ExcelWriter excelWriter = FastExcel.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FastExcel.writerSheet("Table示例").build();
        WriteTable table1 = FastExcel.writerTable(0).build();
        WriteTable table2 = FastExcel.writerTable(1).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### Result

![img](/img/docs/write/tableWrite.png)

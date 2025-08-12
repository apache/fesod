---
id: 'sheet'
title: 'Sheet'
---

# Sheet 页
本章节将介绍设置 Sheet 来写入数据的使用。

## 写入同一个 Sheet

### 概述
分批写入数据到同一个 Sheet。

### 代码示例
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

### 结果
![img](/img/docs/write/repeatedWrite.png)

---

## 写入多个 Sheet

### 概述
分批写入数据到多个 Sheet，可实现大数据量的分页写入。

### 代码示例
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

### 结果
![img](/img/docs/write/repeatedWrite.png)

---

## 使用 Table 写入

### 概述
支持在一个 Sheet 中使用多个 Table 分块写入。

### 代码示例
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

### 结果
![img](/img/docs/write/tableWrite.png)

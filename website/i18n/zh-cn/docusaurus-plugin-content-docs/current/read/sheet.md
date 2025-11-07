---
id: 'sheet'
title: 'Sheet'
---

# Sheet 页

本章节将介绍设置 Sheet 来读取数据的使用

## 读取多个 Sheet

### 概述

可以读取 Excel 文件中的多个 Sheet，且同一个 Sheet 不可重复读取。

### 代码示例

#### 读取全部 Sheet

```java
@Test
public void readAllSheet() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
}
```

---

## 读取指定 Sheet

### 概述

可以读取 Excel 文件具体的某个 Sheet，支持指定 Sheet 的索引或名称

> **注意：** Excel 中 Sheet 名称最多为 31 个字符。按名称读取时，请使用文件中实际显示的 Sheet 名称。

### 代码示例

```java
@Test
public void readSingleSheet() {
    String fileName = "path/to/demo.xlsx";

    try (ExcelReader excelReader = FastExcel.read(fileName).build()) {
        // Sheet 索引
        ReadSheet sheet1 = FastExcel.readSheet(0).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
        // Sheet 名
        ReadSheet sheet2 = FastExcel.readSheet("Sheet2").head(DemoData.class).registerReadListener(new DemoDataListener()).build();
        excelReader.read(sheet1, sheet2);
    }
}
```

---

## 忽略隐藏 Sheet

### 概述

通过设置 `ignoreHiddenSheet` 参数为 true ，此时不会读取“隐藏”状态的 Sheet 中的数据。支持“普通隐藏”和“绝对隐藏”。

### 代码示例

```java
@Test
public void exceptionRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
            .ignoreHiddenSheet(Boolean.TRUE)
            .sheet()
            .doRead();
}
```

> 微软 Excel 中，Sheet 有“普通隐藏(xlSheetHidden)”和“绝对隐藏(xlSheetVeryHidden)”两种状态，绝对隐藏可通过 `VBA` 来设置，此时隐藏的 Sheet 无法通过“取消隐藏”的操作来取消。

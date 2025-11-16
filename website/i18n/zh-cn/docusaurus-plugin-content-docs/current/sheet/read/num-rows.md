---
id: 'num-rows'
title: '行数'
---

# 读取行

本章节将介绍读取指定的行数。

## 概述

在数据分析和处理过程中，快速查看电子表格文件的前几行内容可以帮助我们更好地理解数据结构和内容。默认情况下，Fesod
会读取整个文件的所有数据，通过设置 `numRows` 参数可以限制读取的行数。0 表示不限制行数，即读取所有行，行数包括表头行。

## 所有的 Sheet

### 代码示例

```java

@Test
public void allSheetRead() {
    // 读取前100行
    FesodSheet.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
        for (DemoData demoData : dataList) {
            log.info("读取到一条数据{}", JSON.toJSONString(demoData));
        }
    })).numRows(100).sheet().doRead();
}
```

---

## 单个 sheet

### 代码示例

```java

@Test
public void singleSheetRead() {
    try (ExcelReader excelReader = FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).build()) {
        ReadSheet readSheet = FesodSheet.readSheet(0).build();
        readSheet.setNumRows(100); // 读取前100行
        excelReader.read(readSheet);
    }
}
```

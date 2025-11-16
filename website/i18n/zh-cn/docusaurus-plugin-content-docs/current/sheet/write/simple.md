---
id: 'simple'
title: '简单写入'
---

# 简单写入

本章节介绍如何使用 Fesod 完成简单电子表格写入

## 概述

使用 Fesod 进行简单的电子表格数据写入，可以快速地将实体对象写入电子表格文件，是最基本、最常用的写入方式。

## 代码示例

### POJO 类

与电子表格结构对应的 POJO 类 `DemoData`

```java

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    @ExcelIgnore
    private String ignore; // 忽略的字段
}
```

### 数据列表

```java
private List<DemoData> data() {
    List<DemoData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("String" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}
```

### 写入方式

Fesod 提供了多种写入方式，包括 `Lambda` 表达式、数据列表、`ExcelWriter` 对象等。

#### `Lambda` 表达式

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(() -> data());
}
```

#### 数据列表

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(data());
}
```

#### `ExcelWriter` 对象

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        excelWriter.write(data(), writeSheet);
    }
}
```

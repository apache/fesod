---
id: 'simple'
title: '简单读取'
---

# 简单读取

本章节介绍如何使用 FastExcel 完成简单 Excel 读取

## 数据监听器

### 概述

FastExcel 提供监听器机制，用于在读取 Excel 文件时对每一行数据进行处理。

### 使用

数据监听器需要实例化并支持多种不同的使用方式。

#### 实例化

监听器不能被 Spring 管理，每次读取 Excel 文件时需要重新实例化。

#### `Lambda` 表达式

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new PageReadListener<>(dataList -> {
        for (DemoData demoData : dataList) {
            log.info("读取到一条数据: {}", JSON.toJSONString(demoData));
        }
    })).sheet().doRead();
}
```

#### 匿名内部类

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {
        @Override
        public void invoke(DemoData data, AnalysisContext context) {
           log.info("读取到一条数据: {}", JSON.toJSONString(data));
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("所有数据读取完成！");
        }
    }).sheet().doRead();
}
```

#### 数据监听器

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## POJO 类与监听器

### 概述

FastExcel 提供了一种简单的方式来读取 Excel 文件。用户只需定义一个 POJO 类来表示数据结构，然后通过 FastExcel 的监听器机制读取数据。

### POJO 类

与 Excel 结构对应的 POJO 类 `DemoData`

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### 数据监听器

`DemoDataListener` 是一个自定义监听器，用于处理从 Excel 中读取的数据。

```java
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("读取到一条数据: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据读取完成！");
    }
}
```

### 代码示例

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## 无 POJO 类与监听器

### 概述

FastExcel 支持不定义 POJO 类直接读取 Excel 文件，通过 `Map<Integer, String>` 直接读取数据，其中的键为**列索引**，值为**单元格数据**。

### 数据监听器

```java
@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("读取到一条数据: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据读取完成！");
    }
}
```

---

## 同步读取

### 概述

使用 `doReadSync` 方法直接将 Excel 数据读取为**内存**中的列表，这种方法推荐用于**数据量较小**的场景。读取的数据可以是 POJO 对象列表或 Map 列表。

### POJO 类

与 Excel 结构对应的 POJO 类 `DemoData`

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### 代码示例

#### 读取为 POJO 对象列表

```java
@Test
public void synchronousReadToObjectList() {
    String fileName = "path/to/demo.xlsx";

    // POJO 列表
    List<DemoData> list = FastExcel.read(fileName)
            .head(DemoData.class)
            .sheet()
            .doReadSync();

    for (DemoData data : list) {
        log.info("读取到的数据: {}", JSON.toJSONString(data));
    }
}
```

#### 读取为 Map 列表

在不使用 POJO 情况下，可以将每一行读取为 Map，键为列索引，值为单元格内容。

```java
@Test
public void synchronousReadToMapList() {
    String fileName = "path/to/demo.xlsx";

    // Map 列表
    List<Map<Integer, String>> list = FastExcel.read(fileName)
            .sheet()
            .doReadSync();

    for (Map<Integer, String> data : list) {
        log.info("读取到的数据: {}", JSON.toJSONString(data));
    }
}
```

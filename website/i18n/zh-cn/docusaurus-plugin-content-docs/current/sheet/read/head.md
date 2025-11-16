---
id: 'head'
title: '表头'
---

# 表头

本章节将介绍读取电子表格中的表头数据。

## 读取表头数据

### 概述

可以通过重写监听器的 `invokeHead` 方法获取表头信息。

### 数据监听器

```java

@Slf4j
public class DemoHeadDataListener extends AnalysisEventListener<DemoData> {
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到表头数据: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
```

### 代码示例

```java

@Test
public void headerRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoHeadDataListener())
            .sheet()
            .doRead();
}
```

---

## 多行表头读取

### 概述

通过设置 `headRowNumber` 参数或根据实体类的表头注解自动解析多行表头。

### 代码示例

```java

@Test
public void complexHeaderRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            // 设置多行表头的行数，默认为 1
            .headRowNumber(2)
            .doRead();
}
```

---

## 表头 POJO

### 概述

通过使用 `head()` 方法设置表头 POJO。

### 代码示例

```java

@Test
public void headerPojoRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, new DemoDataListener())
            .head(DemoData.class)
            .sheet()
            .doRead();
}
```

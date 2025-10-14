---
id: 'pojo'
title: '实体类'
---

# POJO

本章节将介绍通过设置 POJO 来读取数据的使用

## 按列名或列下标读取

### 概述

您可以通过指定**列名**或**列下标**来读取 Excel 数据。这使得与动态生成的 Excel 文件交互更加灵活。

### 示例代码

#### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class IndexOrNameData {
    @ExcelProperty(index = 2)
    private Double doubleData;

    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;
}
```

#### 代码示例

```java
@Test
public void indexOrNameRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, IndexOrNameData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## 单元格对象

### 概述

使用 `CellData` 类型接收单元格数据以支持公式和多种单元格格式。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class CellDataReadDemoData {
    private CellData<String> string;
    private CellData<Date> date;
    private CellData<Double> doubleData;
    private CellData<String> formulaValue;
}
```

### 代码示例

```java
@Test
public void cellDataRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, CellDataReadDemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## 无 POJO 类与监听器

### 概述

FastExcel 支持不定义 POJO 类直接读取 Excel 文件，通过 `Map<Integer, String>` 直接读取数据。

### 数据监听器

```java
@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("读取到一条数据:{}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据读取完成！");
    }
}
```

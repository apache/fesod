---
id: 'fill'
title: '填充'
---

# 填充

本章节介绍如何使用 Fesod 来填充数据到文件中。

## 简单填充

### 概述

基于模板文件，通过对象或 Map 填充数据到电子表格中。

### POJO 类

```java

@Getter
@Setter
@EqualsAndHashCode
public class FillData {
    private String name;
    private double number;
    private Date date;
}
```

```java
@Getter
@Setter
@EqualsAndHashCode
public class MultiRowFillData {
    private Integer no;
    private String string1;
    private String string2;
    private String string3;
    private LocalDate localDate1;
    private LocalDate localDate2;
    private Long long1;
    private Long long2;
}
```

### 代码示例

```java

@Test
public void simpleFill() {
    String templateFileName = "path/to/simple.xlsx";

    // 方案1：基于对象填充
    FillData fillData = new FillData();
    fillData.setName("张三");
    fillData.setNumber(5.2);
    FesodSheet.write("simpleFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(fillData);

    // 方案2：基于 Map 填充
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三");
    map.put("number", 5.2);
    FesodSheet.write("simpleFillMap.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(map);
}
```

### 模板

![img](/img/docs/fill/simpleFill_file.png)

### 结果

![img](/img/docs/fill/simpleFill_result.png)

---

## 填充列表

### 概述

填充多个数据项到模板列表中，支持内存批量操作和文件缓存分批填充。

### 代码示例

```java

@Test
public void listFill() {
    String templateFileName = "path/to/list.xlsx";

    // 方案1：一次性填充所有数据
    FesodSheet.write("listFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data());

    // 方案2：分批填充
    try (ExcelWriter writer = FesodSheet.write("listFillBatch.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();
        writer.fill(data(), writeSheet);
        writer.fill(data(), writeSheet);
    }
}
```

### 模板

![img](/img/docs/fill/listFill_file.png)

### 结果

![img](/img/docs/fill/listFill_result.png)

---

## 列表填充合并策略

### 概述

在处理列表数据填充时，模板中可能定义了复杂的跨行或跨列合并结构。默认情况下，Fesod 不会自动合并跨行跨列单元格。但是，您可以使用 `mergeStrategy` 参数来控制合并行为。

### 合并策略

- **NONE**: 不进行任何自动合并（默认）。
- **AUTO**: Fesod 会参照模板行的合并结构，自动对生成的每一行数据应用相同的合并区域。
- **MERGE_CELL_STYLE**: 在 `AUTO` 的基础上，将 **锚定单元格（左上角单元格）** 的样式应用到整个合并区域内的所有单元格。
  - *注意：过多的单元格样式实例可能导致性能问题，并可能超出单元格样式数量限制（.xlsx 格式为 64000 个，.xls 格式为 4000 个），请在数据量较大时谨慎使用。*

### 代码示例

#### `FillMergeStrategy.NONE`

```java

@Test
public void listMultiRowFill() {
    String templateFileName = "path/to/list.xlsx";

    FesodSheet.write("listMultiRowFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data(), FillConfig.builder().mergeStrategy(FillMergeStrategy.NONE).build());
}
```

##### 模板

![img](/img/docs/fill/listMultiRowFill_file_zhCN.png)

##### 结果

![img](/img/docs/fill/listMultiRowFill_file_result_zhCN.png)

#### `FillMergeStrategy.AUTO`

```java

@Test
public void listMultiRowFill() {
    String templateFileName = "path/to/list.xlsx";

    FesodSheet.write("listMultiRowFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data(), FillConfig.builder().mergeStrategy(FillMergeStrategy.AUTO).build());
}
```

##### 模板

![img](/img/docs/fill/listMultiRowFillWithAutoMerge_file_zhCN.png)

##### 结果

![img](/img/docs/fill/listMultiRowFillWithAutoMerge_file_result_zhCN.png)

#### `FillMergeStrategy.MERGE_CELL_STYLE`

```java

@Test
public void listMultiRowFill() {
    String templateFileName = "path/to/list.xlsx";

    FesodSheet.write("listMultiRowFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data(), FillConfig.builder().mergeStrategy(FillMergeStrategy.MERGE_CELL_STYLE).build());
}
```

##### 模板

![img](/img/docs/fill/listMultiRowFillWithAutoMergeAndUnifyStyle_file_zhCN.png)

##### 结果

![img](/img/docs/fill/listMultiRowFillWithAutoMergeAndUnifyStyle_file_result_zhCN.png)

> 注：`MERGE_CELL_STYLE`模式下，为什么部分单元格没有样式？  
> 这是因为模板变量未覆盖这些单元格，所以模板中的样式未被复制。有些没有被模板变量覆盖的单元格之所以看起来有线条，是由于周围单元格的边框渲染造成的。

---

## 复杂填充

### 概述

在模板中填充多种数据类型，包括列表和普通变量。

### 代码示例

```java

@Test
public void complexFill() {
    String templateFileName = "path/to/complex.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 填充列表数据，开启 forceNewRow
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        writer.fill(data(), config, writeSheet);

        // 填充普通变量
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        map.put("total", 1000);
        writer.fill(map, writeSheet);
    }
}
```

### 模板

![img](/img/docs/fill/complexFill_file.png)

### 结果

![img](/img/docs/fill/complexFill_result.png)

---

## 大数据量填充

### 概述

优化大数据量填充性能，确保模板列表在最后一行，后续数据通过 `WriteTable` 填充。

### 代码示例

```java

@Test
public void complexFillWithTable() {
    String templateFileName = "path/to/complexFillWithTable.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFillWithTable.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 填充列表数据
        writer.fill(data(), writeSheet);

        // 填充其他变量
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);

        // 填充统计信息
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(Arrays.asList(null, null, null, "统计: 1000"));
        writer.write(totalList, writeSheet);
    }
}
```

### 模板

![img](/img/docs/fill/complexFillWithTable_file.png)

### 结果

![img](/img/docs/fill/complexFillWithTable_result.png)

---

## 横向填充

### 概述

将列表数据横向填充，适用于动态列数场景。

### 代码示例

```java

@Test
public void horizontalFill() {
    String templateFileName = "path/to/horizontal.xlsx";

    try (ExcelWriter writer = FesodSheet.write("horizontalFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        FillConfig config = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        writer.fill(data(), config, writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);
    }
}
```

### 模板

![img](/img/docs/fill/horizontalFill_file.png)

### 结果

![img](/img/docs/fill/horizontalFill_result.png)

---

## 多列表组合填充

### 概述

支持多个列表同时填充，列表之间通过前缀区分。

### 代码示例

```java

@Test
public void compositeFill() {
    String templateFileName = "path/to/composite.xlsx";

    try (ExcelWriter writer = FesodSheet.write("compositeFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 使用 FillWrapper 进行多列表填充
        writer.fill(new FillWrapper("data1", data()), writeSheet);
        writer.fill(new FillWrapper("data2", data()), writeSheet);
        writer.fill(new FillWrapper("data3", data()), writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        writer.fill(map, writeSheet);
    }
}
```

### 模板

![img](/img/docs/fill/compositeFill_file.png)

### 结果

![img](/img/docs/fill/compositeFill_result.png)

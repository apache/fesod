---
id: 'head'
title: '表头'
---

# 表头

本章节将介绍写入电子表格中的表头数据。

## 复杂头写入

### 概述

支持设置多级表头，通过 `@ExcelProperty` 注解指定主标题和子标题。

### POJO 类

```java

@Getter
@Setter
@EqualsAndHashCode
public class ComplexHeadData {
    @ExcelProperty({"主标题", "字符串标题"})
    private String string;
    @ExcelProperty({"主标题", "日期标题"})
    private Date date;
    @ExcelProperty({"主标题", "数字标题"})
    private Double doubleData;
}
```

### 代码示例

```java

@Test
public void complexHeadWrite() {
    String fileName = "complexHeadWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ComplexHeadData.class)
            .sheet()
            .doWrite(data());
}
```

### 结果

![img](/img/docs/write/complexHeadWrite.png)

---

## 动态头写入

### 概述

实时生成动态表头，适用于表头内容动态变化的场景。

### 代码示例

```java

@Test
public void dynamicHeadWrite() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
            Collections.singletonList("动态字符串标题"),
            Collections.singletonList("动态数字标题"),
            Collections.singletonList("动态日期标题"));

    FesodSheet.write(fileName)
            .head(head)
            .sheet()
            .doWrite(data());
}
```

### 结果

![img](/img/docs/write/dynamicHeadWrite.png)

---

## 表头合并策略

### 概述

默认情况下，Fesod 会自动合并名称相同的表头单元格。但是，您可以使用 `headerMergeStrategy` 参数来控制合并行为。

### 合并策略

- **NONE**: 不进行任何自动合并。
- **HORIZONTAL_ONLY**: 仅水平合并（同一行内的相同单元格）。
- **VERTICAL_ONLY**: 仅垂直合并（同一列内的相同单元格）。
- **FULL_RECTANGLE**: 仅合并完整的矩形区域（所有单元格名称相同）。
- **AUTO**: 自动合并（默认）。

### 代码示例

```java
@Test
public void dynamicHeadWriteWithStrategy() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
        Collections.singletonList("动态字符串标题"),
        Collections.singletonList("动态数字标题"),
        Collections.singletonList("动态日期标题"));

    FesodSheet.write(fileName)
        .head(head)
        .headerMergeStrategy(HeaderMergeStrategy.FULL_RECTANGLE)
        .sheet()
        .doWrite(data());
}
```

### 常见使用场景

**禁用合并**: 使用 `NONE` 完全禁用自动合并：

```java
FesodSheet.write(fileName)
    .head(head)
    .headerMergeStrategy(HeaderMergeStrategy.NONE)
    .sheet()
    .doWrite(data());
```

**注意**: 旧的 `automaticMergeHead` 参数仍然支持以保持向后兼容。当未设置 `headerMergeStrategy` 时，行为由 `automaticMergeHead` 决定。

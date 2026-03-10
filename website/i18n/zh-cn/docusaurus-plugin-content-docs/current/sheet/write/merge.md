---
id: 'merge'
title: '单元格合并'
---

# 单元格合并

本章节介绍写入 Excel 文件时的单元格合并策略。

## 概述

生成 Excel 报表时，您可能需要合并单元格 — 例如，合并列中重复的类别名称，或创建汇总行。Fesod 支持两种方式：基于注解的合并和基于策略的合并。

## 基于注解的合并

### 概述

使用 `@ContentLoopMerge(eachRow = N)` 注解字段，自动合并该列中每 N 行的单元格。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoMergeData {
    @ContentLoopMerge(eachRow = 2)
    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ExcelProperty("数字标题")
    private Double doubleData;
}
```

### 代码示例

```java
@Test
public void annotationMergeWrite() {
    String fileName = "annotationMergeWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, DemoMergeData.class)
        .sheet()
        .doWrite(data());
}
```

### 效果

```text
| 字符串标题 | 日期标题            | 数字标题 |
|-----------|---------------------|---------|
| String0   | 2025-01-01 00:00:00 | 0.56    |
| (已合并)   | 2025-01-01 00:00:00 | 0.56    |
| String1   | 2025-01-01 00:00:00 | 0.56    |
| (已合并)   | 2025-01-01 00:00:00 | 0.56    |
```

---

## 基于策略的合并

### 概述

注册 `LoopMergeStrategy(eachRow, columnIndex)` 作为写入处理器。更加灵活 — 可在运行时配置并应用于任意列。

### 代码示例

```java
@Test
public void strategyMergeWrite() {
    String fileName = "strategyMergeWrite" + System.currentTimeMillis() + ".xlsx";

    // 合并第 0 列中每 2 行
    LoopMergeStrategy strategy = new LoopMergeStrategy(2, 0);
    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(strategy)
        .sheet()
        .doWrite(data());
}
```

---

## 自定义合并策略

### 概述

对于复杂的合并逻辑（例如基于数据内容的合并），可继承 `AbstractMergeStrategy` 类实现。

### 代码示例

```java
public class CustomMergeStrategy extends AbstractMergeStrategy {
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (relativeRowIndex != null && relativeRowIndex % 2 == 0 && head.getColumnIndex() == 0) {
            // 注意：+1 假设只有一行表头。多行表头场景请使用 cell.getRowIndex() 代替。
            int startRow = relativeRowIndex + 1;
            int endRow = startRow + 1;
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
        }
    }
}
```

使用方式：

```java
@Test
public void customMergeWrite() {
    String fileName = "customMergeWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomMergeStrategy())
        .sheet()
        .doWrite(data());
}
```

---

## 使用场景对比

| 方式 | 最佳适用场景 |
|:---|:---|
| `@ContentLoopMerge` | 固定模式的简单合并，内置于数据类中 |
| `LoopMergeStrategy` | 动态合并，运行时配置 |
| `AbstractMergeStrategy` | 基于数据内容或自定义规则的复杂合并 |

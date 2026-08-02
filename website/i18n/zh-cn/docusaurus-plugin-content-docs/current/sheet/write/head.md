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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="3">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">12</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">动态字符串标题</td><td class="xl-head">动态数字标题</td><td class="xl-head">动态日期标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

---

## 表头合并策略

### 概述

默认情况下，Fesod 会自动合并名称相同的表头单元格。但是，您可以使用 `headerMergeStrategy` 参数来控制合并行为。

### 合并策略

- **NONE**: 不进行任何自动合并。
- **HORIZONTAL_ONLY**: 仅水平合并（同一表头行内相邻的列）。
- **VERTICAL_ONLY**: 仅垂直合并（同一列内相邻的行）。
- **FULL_RECTANGLE**: 双向合并，但仅在重复的名称构成完整矩形区域时才合并。
- **AUTO**: 双向合并（默认），但当垂直合并从非首行开始时，还要求两行**正上方**的单元格内容相同 —— 详见示例下方的说明。

合并是由相邻单元格中重复出现的相同名称驱动的，因此只有**多级**表头才需要关心合并策略。下面的示例使用一个三级表头，
名称在水平和垂直两个方向上都有重复：

### 代码示例

```java
@Test
public void dynamicHeadWriteWithStrategy() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
        Arrays.asList("主标题", "编号", "编号"),
        Arrays.asList("主标题", "A 组", "姓名"),
        Arrays.asList("主标题", "A 组", "年龄"),
        Arrays.asList("主标题", "B 组", "姓名"),
        Arrays.asList("主标题", "B 组", "年龄"));

    FesodSheet.write(fileName)
        .head(head)
        .headerMergeStrategy(HeaderMergeStrategy.FULL_RECTANGLE)
        .sheet()
        .doWrite(data());
}
```

#### NONE

每个表头单元格都各自独立：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">编号</td><td class="xl-head">A 组</td><td class="xl-head">A 组</td><td class="xl-head">B 组</td><td class="xl-head">B 组</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">编号</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td></tr>
</tbody>
</table>
</div>

#### HORIZONTAL_ONLY

合并 `A1:E1`、`B2:C2`、`D2:E2`

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">编号</td><td class="xl-head" colspan="2">A 组</td><td class="xl-head" colspan="2">B 组</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">编号</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td></tr>
</tbody>
</table>
</div>

#### VERTICAL_ONLY

只合并 `A2:A3`

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td><td class="xl-head">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head" rowspan="2">编号</td><td class="xl-head">A 组</td><td class="xl-head">A 组</td><td class="xl-head">B 组</td><td class="xl-head">B 组</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td></tr>
</tbody>
</table>
</div>

#### FULL_RECTANGLE

合并 `A1:E1`、`A2:A3`、`B2:C2`、`D2:E2`

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head" rowspan="2">编号</td><td class="xl-head" colspan="2">A 组</td><td class="xl-head" colspan="2">B 组</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td></tr>
</tbody>
</table>
</div>

#### AUTO

合并 `A1:E1`、`B2:C2`、`D2:E2`，但不会合并 `A2:A3`：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">主标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">编号</td><td class="xl-head" colspan="2">A 组</td><td class="xl-head" colspan="2">B 组</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">编号</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td><td class="xl-head">姓名</td><td class="xl-head">年龄</td></tr>
</tbody>
</table>
</div>

:::tip
`AUTO` 的水平合并与 `FULL_RECTANGLE` 完全一致，两者的区别只在**垂直**合并上。`AUTO` 会看*重复的名称从哪一行开始*：

- **从表头第 1 行开始**：直接合并，没有额外条件，因为第 1 行上方没有可供比较的行。某列表头为
  `编号` / `编号` / `编号` 时，在 `AUTO` 下同样会合并成一个纵向单元格，与 `FULL_RECTANGLE` 一致。
- **从更下面的行开始**：只有当两行**正上方**的单元格内容相同时才会合并。

上面的示例属于第二种情况。`编号` 在第 2、3 行重复，`AUTO` 于是比较它们各自上方的内容：第 2 行上方是 `主标题`，
第 3 行上方却是 `编号` 自身，两者不同，因此这两个单元格保持独立。`FULL_RECTANGLE` 不做这个比较，会合并 `A2:A3`。

该条件是逐对行判断的，所以 `AUTO` 可能只合并重复区段的**一部分**。例如某列表头为 `主标题` / `B` / `B` / `B` 时，
`AUTO` 只会合并第 3–4 行（两者上方都是 `B`），第 2 行单独保留；而 `FULL_RECTANGLE` 会把第 2–4 行合并为一个单元格。

因此，如果希望某个在各级中重复出现的列标题合并成一个纵向单元格，请显式选择 `FULL_RECTANGLE` 或 `VERTICAL_ONLY`。
:::

### 常见使用场景

**禁用合并**: 使用 `NONE` 完全禁用自动合并：

```java
FesodSheet.write(fileName)
    .head(head)
    .headerMergeStrategy(HeaderMergeStrategy.NONE)
    .sheet()
    .doWrite(data());
```

**注意**: 旧的 `automaticMergeHead` 参数仍然支持以保持向后兼容。当未设置 `headerMergeStrategy` 时，行为由 `automaticMergeHead` 决定（`true` → `AUTO`，`false` → `NONE`）。

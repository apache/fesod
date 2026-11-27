---
id: 'fill'
title: '填充'
---

<!--
- Licensed to the Apache Software Foundation (ASF) under one or more
- contributor license agreements.  See the NOTICE file distributed with
- this work for additional information regarding copyright ownership.
- The ASF licenses this file to You under the Apache License, Version 2.0
- (the "License"); you may not use this file except in compliance with
- the License.  You may obtain a copy of the License at
-
-   http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-->

# 填充

本章节介绍如何使用 Fesod 来填充数据到文件中。

## 占位符语法

模板通过 `{}` 占位符标记需要填充的单元格，大括号里的内容决定了填充方式：

| 占位符 | 含义 | 由谁填充 |
| --- | --- | --- |
| `{name}` | 单个变量 | `doFill(object)`、`doFill(map)` |
| `{.name}` | 列表中每一项的 `name` 属性 | `doFill(list)`、`fill(list, ...)` |
| `{data1.name}` | 同上，但取自名为 `data1` 的列表 | `fill(new FillWrapper("data1", list), ...)` |
| `\{name\}` | 用 `\` 转义，不会被解析 | 不填充 |

开头的 `.` 决定了单元格会按列表的每一项重复：默认向下重复，也可以通过 `FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL)` 向右重复。`.` 之前的文字指明数据来自哪个列表，因此一个模板中可以并存多个列表。

一个单元格里可以混合多个占位符和普通文本，例如 `{name}今年{number}岁了`。填充时没有提供的占位符会被清空，而不是原样留在表格里：用列表填充 `{name}`，或者用单个对象填充 `{.name}`，都会清空该单元格，只保留周围的文本。

转义只是让大括号不被解析，而 `\` 本身只有在同一个单元格里还存在真实占位符时才会被去掉。如果单元格里没有别的内容，`\{name\}` 会原样写出，包括反斜杠。

## Excel 原生表格

列表填充会识别 XLSX 模板中通过“插入 → 表格”创建的 Excel 原生表格，它与 Fesod 的
`WriteTable` API 是两个概念。写入结束时，Fesod 根据最终行位置同步表格范围和 AutoFilter
范围，支持重复填充、插行引起的表格移动，以及两种 `inMemory` 模板模式。

列表占位符应位于表格的数据行。存在汇总行或需要保留列表下方内容时，启用插行：

```java
FillConfig config = FillConfig.builder().forceNewRow(true).build();
excelWriter.fill(data, config, writeSheet);
```

表格范围包含汇总行，AutoFilter 范围不包含汇总行。不开启插行时，表格可以向空闲行扩展；
会覆盖汇总行或其他表格的填充会报错。插行会移动整行，因此与这些行相交的其他表格也会相应
移动或扩展。其他工作表、受影响行之外的表格保持范围不变；空列表保持表格不变。

表格名称、列元数据、样式、计算列定义和汇总定义会被保留。混合公式中的普通 A1 引用移动时，
结构化引用语法保持不变。Fesod 保留原生计算列定义，并要求 Excel 打开结果时重新计算；
它不会计算这些公式，也不会为每个新增行写入公式缓存值。

当前支持只有一个表头行、至多一个汇总行的表格按行扩展。横向扩列、列元数据不一致、表格
重叠、名称重复或超过工作表行列上限时会报错。此功能不自动创建原生表格，也不扩展到 XLS。

通过 POI 克隆工作表时，请使用 `inMemory(true)`，在填充前克隆原始模板。POI 会创建独立的
表格部件和名称，后续填充单独跟踪副本。克隆已填充的运行时工作表及复杂公式重映射不在此范围内。

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

### 数据列表

```java
private List<FillData> data() {
    List<FillData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
        FillData fillData = new FillData();
        fillData.setName("张三" + i);
        fillData.setNumber(5.2);
        fillData.setDate(new Date());
        list.add(fillData);
    }
    return list;
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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>复杂</td><td>忽略</td><td>空</td></tr>
<tr><td class="xl-chrome">2</td><td>{name}</td><td>{number}</td><td>{name}今年{number}岁了</td><td>\{name\}忽略，{name}</td><td>空{.empty}</td></tr>
</tbody>
</table>
</div>

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>复杂</td><td>忽略</td><td>空</td></tr>
<tr><td class="xl-chrome">2</td><td>张三</td><td class="xl-num">5.2</td><td>张三今年5.2岁了</td><td>{name}忽略，张三</td><td>空</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>日期</td></tr>
<tr><td class="xl-chrome">2</td><td>{.name}</td><td>{.number}</td><td>{.date}</td></tr>
</tbody>
</table>
</div>

### 结果

方案1：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>日期</td></tr>
<tr><td class="xl-chrome">2</td><td>张三0</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">3</td><td>张三1</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">4</td><td>张三2</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>张三9</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
</tbody>
</table>
</div>

方案2：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>日期</td></tr>
<tr><td class="xl-chrome">2</td><td>张三0</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>张三9</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">12</td><td>张三0</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">21</td><td>张三9</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：{date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">{.name}</td><td class="xl-fill-bright-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td></td><td></td><td>统计:{total}</td></tr>
</tbody>
</table>
</div>

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：2024年11月20日</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">张三0</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三0</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fc-red">张三1</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三1</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fc-red">张三2</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三2</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-fc-red">张三9</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三9</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td></td><td></td><td></td><td>统计:1000</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：{date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">{.name}</td><td class="xl-fill-bright-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
</tbody>
</table>
</div>

### 结果

最终文件与上面的复杂填充相同，区别在于生成方式：模板到列表行为止，不再为 `{total}` 预留一行，统计信息在填充之后通过 `writer.write(...)` 追加，因此列表可以任意增长，下方没有需要被顶下去的行。

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：2024年11月20日</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">张三0</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三0</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fc-red">张三1</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三1</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fc-red">张三2</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三2</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-fc-red">张三9</td><td class="xl-fill-bright-green xl-num">5.2</td><td>张三9</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td></td><td></td><td></td><td>统计: 1000</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-fc-red">{.name}</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-fill-bright-green xl-num">{.number}</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>{.name}</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td>时间：{date}</td><td></td><td></td></tr>
</tbody>
</table>
</div>

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">⋯</td><td class="xl-chrome">L</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-fc-red">张三0</td><td class="xl-fc-red">张三1</td><td class="xl-fc-red">张三2</td><td class="xl-muted">…</td><td class="xl-fc-red">张三9</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-num">5.2</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>张三0</td><td>张三1</td><td>张三2</td><td class="xl-muted">…</td><td>张三9</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td>时间：2024年11月20日</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>
</div>

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
        // data1 在模板中是横向排列的，因此使用横向填充
        FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        writer.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
        writer.fill(new FillWrapper("data2", data()), writeSheet);
        writer.fill(new FillWrapper("data3", data()), writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        writer.fill(map, writeSheet);
    }
}
```

### 模板

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-fc-red">{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-fill-bright-green">{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td>{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>时间：{date}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>姓名</td><td>数字</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fc-red">{data2.name}</td><td class="xl-fill-bright-green">{data2.number}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td></td><td></td><td></td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">11</td><td></td><td></td><td></td><td class="xl-fc-red">{data3.name}</td><td class="xl-fill-bright-green">{data3.number}</td></tr>
</tbody>
</table>
</div>

### 结果

- `data1` 采用横向填充，十条数据在四个模板行上从 `C` 列排到 `L` 列。
- `data2` 和 `data3` 则是纵向填充，分别占据第 9 到 18 行的 `A`/`B` 列和第 11 到 20 行的 `D`/`E` 列。
- 用同一个列表名调用 `fill` 会接着往后追加，参见[填充列表](#填充列表)。

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">⋯</td><td class="xl-chrome">L</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-fc-red">张三0</td><td class="xl-fc-red">张三1</td><td class="xl-fc-red">张三2</td><td class="xl-muted">…</td><td class="xl-fc-red">张三9</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-num">5.2</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>张三0</td><td>张三1</td><td>张三2</td><td class="xl-muted">…</td><td>张三9</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>时间：2026-07-31 20:04:59</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>姓名</td><td>数字</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fc-red">张三0</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-fc-red">张三1</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td>姓名</td><td>数字</td><td></td><td></td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fc-red">张三2</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">张三0</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-fc-red">张三3</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">张三1</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td></td></tr>
<tr><td class="xl-chrome">18</td><td class="xl-fc-red">张三9</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">张三7</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">19</td><td></td><td></td><td></td><td class="xl-fc-red">张三8</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">20</td><td></td><td></td><td></td><td class="xl-fc-red">张三9</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
</tbody>
</table>
</div>

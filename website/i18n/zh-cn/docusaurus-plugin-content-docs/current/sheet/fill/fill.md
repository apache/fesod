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

## 列表填充合并策略

### 概述

在处理列表数据填充时，模板中可能定义了复杂的跨行或跨列合并结构。默认情况下，Fesod 不会自动合并跨行跨列单元格。但是，您可以使用 `mergeStrategy` 参数来控制合并行为。

### 合并策略

- **NONE**：不进行任何自动合并（默认）。
- **AUTO**：Fesod 会参照模板行的合并结构，自动对生成的每一行数据应用相同的合并区域。
- **MERGE_CELL_STYLE**：在 `AUTO` 的基础上，将 **锚定单元格（左上角单元格）** 的样式应用到整个合并区域内的所有单元格。
  - *注意：过多的单元格样式实例可能导致性能问题，并可能超出单元格样式数量限制（.xlsx 格式为 64000 个，.xls 格式为 4000 个），请在数据量较大时谨慎使用。*

### POJO 类

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
    private Long number1;
    private Long number2;
}
```

### 数据列表

```java
private List<MultiRowFillData> data1() {
    List<FillData> list = ListUtils.newArrayList();
    for (int i = 0; i < 5; i++) {
        MultiRowFillData fillData = new MultiRowFillData();
        fillData.setNo(i);
        fillData.setString1("字符串1");
        fillData.setString2("字符串2");
        fillData.setString3("字符串3");
        fillData.setLocalDate1(LocalDate.now());
        fillData.setLocalDate2(LocalDate.now());
        fillData.setNumber1(100L);
        fillData.setNumber2(200L);
        list.add(fillData);
    }
    return list;
}
```

### 代码示例

#### `FillMergeStrategy.NONE`

```java

@Test
public void listMultiRowFill() {
    String templateFileName = "path/to/list.xlsx";

    FesodSheet.write("listMultiRowFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data1(), FillConfig.builder().mergeStrategy(FillMergeStrategy.NONE).build());
}
```

##### 模板

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td>序号（跨行合并）</td><td>字符串1-2（常规）</td><td>字符串3（跨行合并）</td><td colspan="2">日期1-2（跨列合并）</td><td colspan="2">数字1（跨行跨列合并）</td><td>数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num" rowspan="2">{.no}</td><td>{.string1}</td><td rowspan="2">{.string3}</td><td class="xl-num" colspan="2">{.localDate1}</td><td class="xl-num" colspan="2" rowspan="2">{.number1}</td><td class="xl-num">{.number2}</td></tr>
<tr><td class="xl-chrome">3</td><td>{.string2}</td><td class="xl-num" colspan="2">{.localDate2}</td><td></td></tr>
</tbody>
</table>
</div>

##### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td>序号（跨行合并）</td><td>字符串1-2（常规）</td><td>字符串3（跨行合并）</td><td colspan="2">日期1-2（跨列合并）</td><td colspan="2">数字1（跨行跨列合并）</td><td>数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num" rowspan="2">0</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num">1</td><td>字符串1</td><td>字符串3</td><td class="xl-num">2026-02-01</td><td></td><td class="xl-num">100</td><td></td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>字符串2</td><td></td><td class="xl-num">2026-02-01</td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-num">2</td><td>字符串1</td><td>字符串3</td><td class="xl-num">2026-02-01</td><td></td><td class="xl-num">100</td><td></td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">7</td><td></td><td>字符串2</td><td></td><td class="xl-num">2026-02-01</td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-num">3</td><td>字符串1</td><td>字符串3</td><td class="xl-num">2026-02-01</td><td></td><td class="xl-num">100</td><td></td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">9</td><td></td><td>字符串2</td><td></td><td class="xl-num">2026-02-01</td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-num">4</td><td>字符串1</td><td>字符串3</td><td class="xl-num">2026-02-01</td><td></td><td class="xl-num">100</td><td></td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">11</td><td></td><td>字符串2</td><td></td><td class="xl-num">2026-02-01</td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td>序号（跨行合并）</td><td>字符串1-2（常规）</td><td>字符串3（跨行合并）</td><td colspan="2">日期1-2（跨列合并）</td><td colspan="2">数字1（跨行跨列合并）</td><td>数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num" rowspan="2">{.no}</td><td>{.string1}</td><td rowspan="2">{.string3}</td><td class="xl-num" colspan="2">{.localDate1}</td><td class="xl-num" colspan="2" rowspan="2">{.number1}</td><td class="xl-num">{.number2}</td></tr>
<tr><td class="xl-chrome">3</td><td>{.string2}</td><td class="xl-num" colspan="2">{.localDate2}</td><td></td></tr>
</tbody>
</table>
</div>

##### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td>序号（跨行合并）</td><td>字符串1-2（常规）</td><td>字符串3（跨行合并）</td><td colspan="2">日期1-2（跨列合并）</td><td colspan="2">数字1（跨行跨列合并）</td><td>数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num" rowspan="2">0</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num" rowspan="2">1</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">5</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-num" rowspan="2">2</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">7</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-num" rowspan="2">3</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">9</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-num" rowspan="2">4</td><td>字符串1</td><td rowspan="2">字符串3</td><td colspan="2" class="xl-num">2026-02-01</td><td class="xl-num" colspan="2" rowspan="2">100</td><td class="xl-num">200</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串2</td><td class="xl-num" colspan="2">2026-02-01</td><td></td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-border">序号（跨行合并）</td><td class="xl-border">字符串1-2（常规）</td><td class="xl-border">字符串3（跨行合并）</td><td class="xl-border" colspan="2">日期1-2（跨列合并）</td><td class="xl-border" colspan="2">数字1（跨行跨列合并）</td><td class="xl-border">数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num xl-border" rowspan="2">{.no}</td><td class="xl-border">{.string1}</td><td class="xl-border" rowspan="2">{.string3}</td><td class="xl-num xl-border" colspan="2">{.localDate1}</td><td class="xl-num xl-border" colspan="2" rowspan="2">{.number1}</td><td class="xl-num xl-border">{.number2}</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-border">{.string2}</td><td class="xl-num xl-border" colspan="2">{.localDate2}</td><td class="xl-border"></td></tr>
</tbody>
</table>
</div>

##### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-border">序号（跨行合并）</td><td class="xl-border">字符串1-2（常规）</td><td class="xl-border">字符串3（跨行合并）</td><td class="xl-border" colspan="2">日期1-2（跨列合并）</td><td class="xl-border" colspan="2">数字1（跨行跨列合并）</td><td class="xl-border">数字2（常规）</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num xl-border" rowspan="2">0</td><td class="xl-border">字符串1</td><td class="xl-border" rowspan="2">字符串3</td><td colspan="2" class="xl-num xl-border">2026-02-01</td><td class="xl-num xl-border" colspan="2" rowspan="2">100</td><td class="xl-num xl-border">200</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-border">字符串2</td><td class="xl-num xl-border" colspan="2">2026-02-01</td><td class="xl-border"></td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num xl-border" rowspan="2">1</td><td class="xl-border">字符串1</td><td class="xl-border" rowspan="2">字符串3</td><td colspan="2" class="xl-num xl-border">2026-02-01</td><td class="xl-num xl-border xl-border-right-none" colspan="2" rowspan="2">100</td><td class="xl-num xl-border">200</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-border">字符串2</td><td class="xl-num xl-border" colspan="2">2026-02-01</td><td class="xl-border__horizontal_default"></td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-num xl-border" rowspan="2">2</td><td class="xl-border">字符串1</td><td class="xl-border" rowspan="2">字符串3</td><td colspan="2" class="xl-num xl-border">2026-02-01</td><td class="xl-num xl-border xl-border-right-none" colspan="2" rowspan="2">100</td><td class="xl-num xl-border">200</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-border">字符串2</td><td class="xl-num xl-border" colspan="2">2026-02-01</td><td class="xl-border__horizontal_default"></td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-num xl-border" rowspan="2">3</td><td class="xl-border">字符串1</td><td class="xl-border" rowspan="2">字符串3</td><td colspan="2" class="xl-num xl-border">2026-02-01</td><td class="xl-num xl-border xl-border-right-none" colspan="2" rowspan="2">100</td><td class="xl-num xl-border">200</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-border">字符串2</td><td class="xl-num xl-border" colspan="2">2026-02-01</td><td class="xl-border__horizontal_default"></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-num xl-border" rowspan="2">4</td><td class="xl-border">字符串1</td><td class="xl-border" rowspan="2">字符串3</td><td colspan="2" class="xl-num xl-border">2026-02-01</td><td class="xl-num xl-border_bottom_default xl-border-right-none" colspan="2" rowspan="2">100</td><td class="xl-num xl-border">200</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-border">字符串2</td><td class="xl-num xl-border" colspan="2">2026-02-01</td><td></td></tr>
</tbody>
</table>
</div>

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

---
id: 'pojo'
title: '实体类'
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

# POJO

本章节将介绍通过设置 POJO 来写入。

## 根据参数只导出指定列

### 概述

通过设置列名集合动态选择要导出的列，支持忽略列或仅导出特定列。

集合中填写的是 **POJO 的字段名**，而非表头标题。下面两个示例都使用 [简单写入](./simple.md) 中的 `DemoData` 类和 `data()`
方法，其字段为 `string`、`date` 和 `doubleData`。

### 代码示例

#### 忽略指定列

除列出的字段外，其余字段都会写入：

```java
@Test
public void excludeColumnWrite() {
    String fileName = "excludeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> excludeColumns = Collections.singleton("date");
    FesodSheet.write(fileName, DemoData.class)
        .excludeColumnFieldNames(excludeColumns)
        .sheet()
        .doWrite(data());
}
```

**结果**

`date` 字段被去掉，其余两列保留：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

#### 仅导出指定列

只有列出的字段会写入：

```java
@Test
public void includeColumnWrite() {
    String fileName = "includeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> includeColumns = Collections.singleton("date");
    FesodSheet.write(fileName, DemoData.class)
        .includeColumnFieldNames(includeColumns)
        .sheet()
        .doWrite(data());
}
```

**结果**

只保留 `date` 字段：

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">日期标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

---

## 指定写入的列顺序

### 概述

指定写入列顺序。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class IndexData {
    @ExcelProperty(value = "字符串标题", index = 0)
    private String string;
    @ExcelProperty(value = "日期标题", index = 1)
    private Date date;
    @ExcelProperty(value = "数字标题", index = 3)
    private Double doubleData;
}
```

### 代码示例

#### `index` 属性

通过 `@ExcelProperty` 注解的 `index` 属性指定列顺序。

```java
@Test
public void indexWrite() {
    String fileName = "indexWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, IndexData.class)
        .sheet()
        .doWrite(data());
}
```

**结果**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td></td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

:::note
**C 列的空白是刻意为之。** `index` 是从 0 开始的绝对列位置，而不是排序键：示例中三个字段声明的是 `0`、`1` 和 `3`，因此位置
`2` 上没有写入任何内容，输出中就保留了这一处空列。若希望三列紧挨在一起，请把它们编号为 `0`、`1`、`2`。
:::

#### includeColumnFieldNames

列的顺序取决于 POJO，而不是集合。即使把 `doubleData` 写在 `string` 前面，输出中仍然是 `string` 在前，因为字段就是按这个顺序声明的：

```java
@Test
public void includeColumnOrderWrite() {
    String fileName = "includeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> includeColumns = new LinkedHashSet<>(Arrays.asList("doubleData", "string"));
    FesodSheet.write(fileName, DemoData.class)
        .includeColumnFieldNames(includeColumns)
        .sheet()
        .doWrite(data());
}
```

**结果**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>
</div>

#### orderByIncludeColumn

加上 `.orderByIncludeColumn(true)`，即可改为按集合的顺序排列：

```java
@Test
public void orderByIncludeColumnWrite() {
    String fileName = "includeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> includeColumns = new LinkedHashSet<>(Arrays.asList("doubleData", "string"));
    FesodSheet.write(fileName, DemoData.class)
        .includeColumnFieldNames(includeColumns)
        .orderByIncludeColumn(true)
        .sheet()
        .doWrite(data());
}
```

**结果**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">数字标题</td><td class="xl-head">字符串标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">0.56</td><td>字符串0</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>
</div>

此时集合的迭代顺序必须稳定 - 用 `LinkedHashSet` 或 `List`，不要用 `HashSet`。

---

## 不创建对象的写入

### 概述

直接使用 `List<List<String>>` 定义头和数据写入，无需创建实体类。

### 代码示例

```java
@Test
public void noModelWrite() {
    String fileName = "noModelWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName)
        .head(head()) // 动态头
        .sheet("无对象写入")
        .doWrite(dataList());
}

private List<List<String>> head() {
    return Arrays.asList(
        Collections.singletonList("字符串标题"),
        Collections.singletonList("数字标题"),
        Collections.singletonList("日期标题"));
}

private List<List<Object>> dataList() {
    List<List<Object>> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(Arrays.asList("字符串" + i, 0.56, new Date()));
    }
    return list;
}
```

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">数字标题</td><td class="xl-head">日期标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

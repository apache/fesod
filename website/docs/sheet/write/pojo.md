---
id: 'pojo'
title: 'POJO'
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

This chapter introduces how to write data by configuring POJO classes.

## Export Only Specified Columns Based on Parameters

### Overview

Dynamically select columns to export by setting a collection of column names, supporting ignoring columns or exporting
only specific columns.

The collection holds **POJO field names**, not header titles. Both examples below use the `DemoData` class and the
`data()` method from [Simple Writing](./simple.md), whose fields are `string`, `date` and `doubleData`.

### Code Examples

#### Ignore Specified Columns

Everything except the listed fields is written:

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

**Result**

The `date` field is gone, the other two remain:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

#### Export Only Specified Columns

Only the listed fields are written:

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

**Result**

Only the `date` field is kept:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

---

## Specify Column Order for Writing

### Overview

Specify column order.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class IndexData {
    @ExcelProperty(value = "String Title", index = 0)
    private String string;
    @ExcelProperty(value = "Date Title", index = 1)
    private Date date;
    @ExcelProperty(value = "Number Title", index = 3)
    private Double doubleData;
}
```

### Code Example

#### `index` attribute

Using the `index` attribute of the `@ExcelProperty` annotation.

```java
@Test
public void indexWrite() {
    String fileName = "indexWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, IndexData.class)
        .sheet()
        .doWrite(data());
}
```

**Result**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td></td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

:::note
Column **C is empty on purpose**. `index` is an absolute, 0-based column position, not a sort key: the fields declare
`0`, `1` and `3`, so nothing is written at position `2` and the gap is preserved in the output. To place the three
columns side by side, number them `0`, `1`, `2`.
:::

#### includeColumnFieldNames

The columns come out in the POJO's order, not the collection's. Listing `doubleData` before `string` still writes
`string` first, because that is the order the fields are declared in:

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

**Result**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>
</div>

#### orderByIncludeColumn

Add `.orderByIncludeColumn(true)` to follow the collection's order instead:

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

**Result**

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Number Title</td><td class="xl-head">String Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">0.56</td><td>String0</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>
</div>

The collection needs a stable iteration order for this - a `LinkedHashSet` or a `List`, not a `HashSet`.

---

## Writing Without Creating Objects

### Overview

Write data directly using `List<List<String>>` to define headers and data without creating entity classes.

### Code Example

```java
@Test
public void noModelWrite() {
    String fileName = "noModelWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName)
        .head(head()) // Dynamic headers
        .sheet("Write Without Object")
        .doWrite(dataList());
}

private List<List<String>> head() {
    return Arrays.asList(
        Collections.singletonList("String Title"),
        Collections.singletonList("Number Title"),
        Collections.singletonList("Date Title"));
}

private List<List<Object>> dataList() {
    List<List<Object>> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(Arrays.asList("String" + i, 0.56, new Date()));
    }
    return list;
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Number Title</td><td class="xl-head">Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

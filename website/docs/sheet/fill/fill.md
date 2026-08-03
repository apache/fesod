---
id: 'fill'
title: 'Fill'
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

# Fill

This section explains how to use Fesod to fill data into files.

## Placeholder Syntax

A template marks the cells to fill with `{}` placeholders. What sits inside the braces decides how
the cell is filled:

| Placeholder | Meaning | Filled by |
| --- | --- | --- |
| `{name}` | a single variable | `doFill(object)`, `doFill(map)` |
| `{.name}` | the `name` property of every item of a list | `doFill(list)`, `fill(list, ...)` |
| `{data1.name}` | the same, for the list named `data1` | `fill(new FillWrapper("data1", list), ...)` |
| `\{name\}` | escaped with `\`, never parsed | nothing |

The leading `.` is what makes a cell repeat once per item - downwards by default, or across the
columns with `FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL)`. The text before the
`.` names which list the items come from, so one template can hold several lists side by side.

A cell may mix several placeholders with ordinary text, as in `{name} is {number} years old this
year`. A placeholder the fill does not supply is cleared rather than left in the sheet: filling a
list against `{name}`, or an object against `{.name}`, empties the cell and keeps only the text
around it.

Escaping stops the braces from being parsed, but the `\` characters are only removed when the same
cell also holds a real placeholder. In a cell that contains nothing else, `\{name\}` is written out
exactly as typed, backslashes included.

## Simple Fill

### Overview

Fill data into spreadsheet based on a template file using objects or Map.

### POJO Class

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

### Data List

```java
private List<FillData> data() {
    List<FillData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
        FillData fillData = new FillData();
        fillData.setName("John Doe" + i);
        fillData.setNumber(5.2);
        fillData.setDate(new Date());
        list.add(fillData);
    }
    return list;
}
```

### Code Example

```java

@Test
public void simpleFill() {
    String templateFileName = "path/to/simple.xlsx";

    // Approach 1: Fill based on object
    FillData fillData = new FillData();
    fillData.setName("John");
    fillData.setNumber(5.2);
    FesodSheet.write("simpleFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(fillData);

    // Approach 2: Fill based on Map
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John");
    map.put("number", 5.2);
    FesodSheet.write("simpleFillMap.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(map);
}
```

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Complex</td><td>Ignored</td><td>Empty</td></tr>
<tr><td class="xl-chrome">2</td><td>{name}</td><td>{number}</td><td>{name} is {number} years old</td><td>\{name\} ignored，{name}</td><td>Empty{.empty}</td></tr>
</tbody>
</table>
</div>

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Complex</td><td>Ignored</td><td>Empty</td></tr>
<tr><td class="xl-chrome">2</td><td>John Doe</td><td class="xl-num">5.2</td><td>John Doe is 5.2 years old</td><td>{name} ignored，John Doe</td><td>Empty</td></tr>
</tbody>
</table>
</div>

---

## Fill List

### Overview

Fill multiple data items into a template list, supporting in-memory batch operations and file cache batch filling.

### Code Example

```java

@Test
public void listFill() {
    String templateFileName = "path/to/list.xlsx";

    // Approach 1: Fill all data at once
    FesodSheet.write("listFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data());

    // Approach 2: Batch filling
    try (ExcelWriter writer = FesodSheet.write("listFillBatch.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();
        writer.fill(data(), writeSheet);
        writer.fill(data(), writeSheet);
    }
}
```

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Date</td></tr>
<tr><td class="xl-chrome">2</td><td>{.name}</td><td>{.number}</td><td>{.date}</td></tr>
</tbody>
</table>
</div>

### Result

Approach 1:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Date</td></tr>
<tr><td class="xl-chrome">2</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">3</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">4</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
</tbody>
</table>
</div>

Approach 2:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Date</td></tr>
<tr><td class="xl-chrome">2</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">12</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">21</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2026-07-31 19:55:44</td></tr>
</tbody>
</table>
</div>

---

## Complex Fill

### Overview

Fill various data types in a template, including lists and regular variables.

### Code Example

```java

@Test
public void complexFill() {
    String templateFileName = "path/to/complex.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Fill list data, with forceNewRow enabled
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        writer.fill(data(), config, writeSheet);

        // Fill regular variables
        Map<String, Object> map = new HashMap<>();
        map.put("date", "November 20, 2024");
        map.put("total", 1000);
        writer.fill(map, writeSheet);
    }
}
```

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: {date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">{.name}</td><td class="xl-fill-bright-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td></td><td></td><td>Total:{total}</td></tr>
</tbody>
</table>
</div>

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: November 20, 2024</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td></td><td></td><td></td><td>Total:1000</td></tr>
</tbody>
</table>
</div>

---

## Complex Fill with Large Data

### Overview

Optimize performance for filling large data, ensuring the template list is at the last row, and subsequent data is
filled using `WriteTable`.

### Code Example

```java

@Test
public void complexFillWithTable() {
    String templateFileName = "path/to/complexFillWithTable.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFillWithTable.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Fill list data
        writer.fill(data(), writeSheet);

        // Fill list data
        Map<String, Object> map = new HashMap<>();
        map.put("date", "November 20, 2024");
        writer.fill(map, writeSheet);

        // Fill statistical information
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(Arrays.asList(null, null, null, "Total: 1000"));
        writer.write(totalList, writeSheet);
    }
}
```

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: {date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">{.name}</td><td class="xl-fill-bright-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
</tbody>
</table>
</div>

### Result

The file comes out the same as Complex Fill above. What changes is how it gets there. The template
stops at the list row instead of reserving a row for `{total}`, and the total is appended afterwards
with `writer.write(...)`, so the list can grow to any length without rows below it to push down.

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: November 20, 2024</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-fill-bright-green xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td></td><td></td><td></td><td>Total: 1000</td></tr>
</tbody>
</table>
</div>

---

## Horizontal Fill

### Overview

Fill list data horizontally, suitable for scenarios with dynamic column numbers.

### Code Example

```java

@Test
public void horizontalFill() {
    String templateFileName = "path/to/horizontal.xlsx";

    try (ExcelWriter writer = FesodSheet.write("horizontalFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        FillConfig config = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        writer.fill(data(), config, writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", "November 20, 2024");
        writer.fill(map, writeSheet);
    }
}
```

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-fc-red">{.name}</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-fill-bright-green xl-num">{.number}</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>{.name}</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td>Time: {date}</td><td></td><td></td></tr>
</tbody>
</table>
</div>

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">…</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">5</td><td>Time: November 20, 2024</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>
</div>

---

## Fill Multiple Lists Together

### Overview

Support filling multiple lists simultaneously, with prefixes to differentiate between lists.

### Code Example

```java

@Test
public void compositeFill() {
    String templateFileName = "path/to/composite.xlsx";

    try (ExcelWriter writer = FesodSheet.write("compositeFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Use FillWrapper for filling multiple lists
        // data1 is laid out across the columns, so it is filled horizontally
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

### Template

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-fc-red">{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-fill-bright-green">{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td>{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>Time: {date}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>Name</td><td>Number</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fc-red">{data2.name}</td><td class="xl-fill-bright-green">{data2.number}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td></td><td></td><td></td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">11</td><td></td><td></td><td></td><td class="xl-fc-red">{data3.name}</td><td class="xl-fill-bright-green">{data3.number}</td></tr>
</tbody>
</table>
</div>

### Result

- `data1` is filled horizontally, so its ten items run across the columns from `C` to `L` on each of the four template rows.
- `data2` and `data3` are filled downwards instead, occupying `A`/`B` in rows 9 to 18 and `D`/`E` in rows 11 to 20.
- Calling `fill` again with the same list name appends to it, as in [Fill List](#fill-list).

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">⋯</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-fc-red">John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-fill-bright-green xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>Time: 2026-07-31 20:04:59</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>Name</td><td>Number</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td>Name</td><td>Number</td><td></td><td></td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td></td></tr>
<tr><td class="xl-chrome">18</td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">19</td><td></td><td></td><td></td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
<tr><td class="xl-chrome">20</td><td></td><td></td><td></td><td class="xl-fc-red">John Doe</td><td class="xl-fill-bright-green xl-num">5.2</td><td></td><td></td></tr>
</tbody>
</table>
</div>

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

### Code Example

```java

@Test
public void simpleFill() {
    String templateFileName = "path/to/simple.xlsx";

    // Approach 1: Fill based on object
    FillData fillData = new FillData();
    fillData.setName("John Doe");
    fillData.setNumber(5.2);
    FesodSheet.write("simpleFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(fillData);

    // Approach 2: Fill based on Map
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John Doe");
    map.put("number", 5.2);
    FesodSheet.write("simpleFillMap.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(map);
}
```

### Template

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Complex</td><td>Ignored</td><td>Empty</td></tr>
<tr><td class="xl-chrome">2</td><td>{name}</td><td>{number}</td><td>{name} is {number} years old this year</td><td>\{name\} ignored，{name}</td><td>Empty{.empty}</td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Complex</td><td>Ignored</td></tr>
<tr><td class="xl-chrome">2</td><td>John Doe</td><td class="xl-num">5.2</td><td>John Doe is 5.2 years old this year</td><td>{name} ignored，John Doe</td></tr>
</tbody>
</table>

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

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Date</td></tr>
<tr><td class="xl-chrome">2</td><td>{.name}</td><td>{.number}</td><td>{.date}</td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>Name</td><td>Number</td><td>Date</td></tr>
<tr><td class="xl-chrome">2</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">3</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">4</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">5</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">6</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">7</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">8</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">9</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">10</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">11</td><td>John Doe</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>

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

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: {date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">{.name}</td><td class="xl-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td></td><td></td><td>Total:{total}</td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: November 20, 2024</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">15</td><td></td><td></td><td></td><td>Total:1000</td></tr>
</tbody>
</table>

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

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: {date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">{.name}</td><td class="xl-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>Statistics</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>Time: November 20, 2024</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>Number</td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td>John Doe</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">15</td><td></td><td></td><td></td><td>Total: 1000</td></tr>
</tbody>
</table>

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

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-red">{.name}</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-green xl-num">{.number}</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>{.name}</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td>Time: {date}</td><td></td><td></td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td><td class="xl-chrome">I</td><td class="xl-chrome">J</td><td class="xl-chrome">K</td><td class="xl-chrome">L</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td>Time: 2024-12-04 20:03:48</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>

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
        writer.fill(new FillWrapper("data1", data()), writeSheet);
        writer.fill(new FillWrapper("data2", data()), writeSheet);
        writer.fill(new FillWrapper("data3", data()), writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        writer.fill(map, writeSheet);
    }
}
```

### Template

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-red">{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-green">{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td>{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>Time: {date}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>Name</td><td>Number</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">{data2.name}</td><td class="xl-green">{data2.number}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td></td><td></td><td></td><td>Name</td><td>Number</td></tr>
<tr><td class="xl-chrome">11</td><td></td><td></td><td></td><td class="xl-red">{data3.name}</td><td class="xl-green">{data3.number}</td></tr>
</tbody>
</table>

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">Statistics</td><td>Name</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-red">John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">2</td><td>Number</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">3</td><td>Name</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td>John Doe</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">4</td><td>Number</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>Time: 2024-12-04 20:04:59</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>Name</td><td>Number</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td>Name</td><td>Number</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td class="xl-red">John Doe</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td></td><td></td></tr>
</tbody>
</table>

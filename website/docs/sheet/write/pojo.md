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

### Code Examples

Ignore specified columns

```java
@Test
public void excludeOrIncludeWrite() {
    String fileName = "excludeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> excludeColumns = Set.of("date");
    FesodSheet.write(fileName, DemoData.class)
        .excludeColumnFieldNames(excludeColumns)
        .sheet()
        .doWrite(data());
}
```

Export only specified columns

```java
@Test
public void excludeOrIncludeWrite() {
    String fileName = "includeColumnFiledWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> includeColumns = Set.of("date");
    FesodSheet.write(fileName, DemoData.class)
        .includeColumnFiledNames(includeColumns)
        .sheet()
        .doWrite(data());
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
</tbody>
</table>

---

## Specify Column Order for Writing

### Overview

Specify column order using the `index` attribute of the `@ExcelProperty` annotation.

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

```java
@Test
public void indexWrite() {
    String fileName = "indexWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, IndexData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td></td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td></td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

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

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Number Title</td><td class="xl-head">Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
</tbody>
</table>

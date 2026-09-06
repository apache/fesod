---
id: 'sheet'
title: 'Sheet'
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

# Sheet

This chapter introduces how to configure Sheets to write data.

> **Note:** Microsoft Excel limits sheet names to 31 characters. Longer names are automatically trimmed with a warning logged.

## Writing to the Same Sheet

### Overview

Write data in batches to the same Sheet.

### Code Example

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(pathname, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        for (int i = 0; i < 5; i++) {
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">51</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Sheet1</td></tr></tbody></table>
</div>

---

## Writing to Multiple Sheets

### Overview

Write data in batches to multiple Sheets, enabling paginated writing for large data volumes.

### Code Example

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(pathname, DemoData.class).build()) {
        for (int i = 0; i < 5; i++) {
            WriteSheet writeSheet = FesodSheet.writerSheet(i, "Sheet" + i).build();
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Sheet0</td><td>Sheet1</td><td>Sheet2</td><td>Sheet3</td><td>Sheet4</td></tr></tbody></table>
</div>

---

## Writing Using Tables

### Overview

Supports using multiple Tables within a single Sheet for segmented writing.

### Code Example

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(pathname).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Table Example").build();
        WriteTable table1 = FesodSheet.writerTable(0).head(DemoData.class).build();
        WriteTable table2 = FesodSheet.writerTable(1).head(DemoData.class).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">13</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">14</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">15</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">22</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Table Example</td></tr></tbody></table>
</div>

:::note
Each `WriteTable` writes its own header, so the header repeats once per table - rows 1 and 12 - with the two batches
of 10 rows at 2-11 and 13-22.

The head is declared on each `WriteTable` rather than on the writer, which is also what lets the tables carry
different data types. Building the writer with `FesodSheet.write(fileName, DemoData.class)` instead gives the sheet a
head of its own, and it writes an extra header row above the first table's.
:::

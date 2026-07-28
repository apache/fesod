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

@Test
public void writeSingleSheet() {
    String fileName = "repeatedWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        for (int i = 0; i < 5; i++) {
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

---

## Writing to Multiple Sheets

### Overview

Write data in batches to multiple Sheets, enabling paginated writing for large data volumes.

### Code Example

```java

@Test
public void writeMultiSheet() {
    String fileName = "repeatedWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        for (int i = 0; i < 5; i++) {
            WriteSheet writeSheet = FesodSheet.writerSheet(i, "Sheet" + i).build();
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

---

## Writing Using Tables

### Overview

Supports using multiple Tables within a single Sheet for segmented writing.

### Code Example

```java

@Test
public void tableWrite() {
    String fileName = "tableWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Table Example").build();
        WriteTable table1 = FesodSheet.writerTable(0).build();
        WriteTable table2 = FesodSheet.writerTable(1).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">13</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">14</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">15</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">16</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">17</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">18</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">19</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">20</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">21</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">22</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

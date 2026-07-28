---
id: 'sheet'
title: 'Sheet 页'
---

# Sheet 页

本章节将介绍设置 Sheet 来写入数据的使用。

> **注意：** 微软 Excel 中 Sheet 名称最多为 31 个字符。超过此长度的名称将被自动截断，并记录警告日志。

## 写入同一个 Sheet

### 概述

分批写入数据到同一个 Sheet。

### 代码示例

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

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>字符串3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>字符串4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>字符串5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>字符串6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>字符串7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>字符串8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>

---

## 写入多个 Sheet

### 概述

分批写入数据到多个 Sheet，可实现大数据量的分页写入。

### 代码示例

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

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>字符串3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>字符串4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>字符串5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>字符串6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>字符串7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>字符串8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

---

## 使用 Table 写入

### 概述

支持在一个 Sheet 中使用多个 Table 分块写入。

### 代码示例

```java

@Test
public void tableWrite() {
    String fileName = "tableWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Table示例").build();
        WriteTable table1 = FesodSheet.writerTable(0).build();
        WriteTable table2 = FesodSheet.writerTable(1).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>字符串2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>字符串3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>字符串4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>字符串5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>字符串6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>字符串7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td>字符串9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">14</td><td>字符串0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">15</td><td>字符串1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">16</td><td>字符串2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">17</td><td>字符串3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">18</td><td>字符串4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">19</td><td>字符串5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">20</td><td>字符串6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">21</td><td>字符串7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">22</td><td>字符串8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">23</td><td>字符串9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

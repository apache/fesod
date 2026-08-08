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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">51</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Sheet1</td></tr></tbody></table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Sheet0</td><td>Sheet1</td><td>Sheet2</td><td>Sheet3</td><td>Sheet4</td></tr></tbody></table>
</div>

---

## 使用 Table 写入

### 概述

支持在一个 Sheet 中使用多个 Table 分块写入。

### 代码示例

```java

@Test
public void tableWrite() {
    String fileName = "tableWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Table示例").build();
        WriteTable table1 = FesodSheet.writerTable(0).head(DemoData.class).build();
        WriteTable table2 = FesodSheet.writerTable(1).head(DemoData.class).build();

        excelWriter.write(data(), writeSheet, table1);
        excelWriter.write(data(), writeSheet, table2);
    }
}
```

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">13</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">14</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">15</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">22</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
<table class="xl-sheet-tabs"><tbody><tr><td class="xl-tab-active">Table示例</td></tr></tbody></table>
</div>

:::note
每个 `WriteTable` 各写入一行自己的表头，因此表头分别出现在第 1 行和第 12 行，两段各 10 行数据依次位于
2-11 行和 13-22 行。

这里把表头声明在每个 `WriteTable` 上，而不是在 Writer 上；当各个 Table 的数据类型不同时也正需要这种写法。
如果改用 `FesodSheet.write(fileName, DemoData.class)` 构建 Writer，Sheet 自身也会有表头，
会在第一个 Table 的表头之上多写一行。
:::

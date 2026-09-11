---
id: 'simple'
title: '简单写入'
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

# 简单写入

本章节介绍如何使用 Fesod 完成简单电子表格写入

## 概述

使用 Fesod 进行简单的电子表格数据写入，可以快速地将实体对象写入电子表格文件，是最基本、最常用的写入方式。

## 代码示例

### POJO 类

与电子表格结构对应的 POJO 类 `DemoData`

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    @ExcelIgnore
    private String ignore; // 忽略的字段
}
```

### 数据列表

```java
private List<DemoData> data() {
    List<DemoData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("字符串" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}
```

### 写入方式

Fesod 提供了多种写入方式，包括 `Lambda` 表达式、数据列表、`ExcelWriter` 对象等。

#### `Lambda` 表达式

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, DemoData.class)
            .sheet("Sheet1")
            .doWrite(() -> data());
}
```

#### 数据列表

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, DemoData.class)
            .sheet("Sheet1")
            .doWrite(data());
}
```

#### `ExcelWriter` 对象

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(pathname, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        excelWriter.write(data(), writeSheet);
    }
}
```

### 结果

三种方式生成的文件完全相同。
第 1 行是取自 `@ExcelProperty` 的表头标题，`data()` 返回的多个数据对象依次写入第 2 至 11 行。
`ignore` 字段因为标注了 `@ExcelIgnore` 而不会出现。

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
</div>

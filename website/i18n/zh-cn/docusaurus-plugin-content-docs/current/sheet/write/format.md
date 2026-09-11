---
id: 'format'
title: '格式化'
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

# 格式化

本章节将介绍写入数据时的数据格式化。

## 自定义格式写入

### 概述

支持日期、数字或其他自定义格式，通过注解实现。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    @ExcelProperty("日期标题")
    private Date date;

    @NumberFormat("#.##%")
    @ExcelProperty("数字标题")
    private Double doubleData;
}
```

### 代码示例

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, ConverterData.class)
        .sheet()
        .doWrite(data());
}
```

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>自定义：字符串0</td><td>2024年12月03日20时50分23秒</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">3</td><td>自定义：字符串1</td><td>2024年12月03日20时50分23秒</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">4</td><td>自定义：字符串2</td><td>2024年12月03日20时50分23秒</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>自定义：字符串9</td><td>2024年12月03日20时50分23秒</td><td class="xl-num">56.%</td></tr>
</tbody>
</table>
</div>

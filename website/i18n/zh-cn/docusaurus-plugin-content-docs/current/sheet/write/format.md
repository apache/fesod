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
@Test
public void converterWrite() {
    String fileName = "converterWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ConverterData.class)
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

## 默认日期格式

### 概述

当日期类型字段没有 `@DateTimeFormat` 格式时，Fesod 按三层级联解析其格式：

| 优先级 | 来源 | 作用域 |
| --- | --- | --- |
| 1 | `@DateTimeFormat("...")` | 单个字段 |
| 2 | `.defaultDateFormat(...)` 构建器参数 | 单次读/写调用 |
| 3 | `DateUtils.setDefaultDateFormat(...)` | 进程级 |
| — | 内置常量 | `yyyy-MM-dd HH:mm:ss` |

三个构建器参数分别对应不同日期类型：

| 构建器参数 | 适用类型 | 内置常量 |
| --- | --- | --- |
| `defaultDateFormat` | `Date`、`LocalDateTime`、`OffsetDateTime` | `yyyy-MM-dd HH:mm:ss` |
| `defaultLocalDateFormat` | `LocalDate` | `yyyy-MM-dd` |
| `defaultYearFormat` | `Year` | `yyyy` |

### 按调用设置默认值

```java
FesodSheet.write(fileName, OrderData.class)
    .defaultDateFormat("yyyy/MM/dd HH:mm:ss")
    .defaultLocalDateFormat("yyyy/MM/dd")
    .sheet()
    .doWrite(data());
```

该参数在读侧（`FesodSheet.read(...)`）同样可用，主要影响未标注格式时 `Year` 的解析。

### 进程级默认值

希望全局统一默认格式的应用，可以在启动时设置一次，而不必逐字段加注解或逐调用传参数：

```java
DateUtils.setDefaultDateFormat("yyyy/MM/dd HH:mm:ss");
DateUtils.setDefaultLocalDateFormat("yyyy/MM/dd");
DateUtils.setDefaultYearFormat("yyyy");
```

setter 会拒绝空白格式，`DateUtils.resetDefaultDateFormats()` 可恢复内置值。

### 快照语义

每个读/写上下文在创建时对进程级默认值做一次快照，因此同一个工作簿的所有单元格使用一致的格式。运行中途修改进程级默认值只影响之后新建的上下文，绝不会让正在写入的工作簿出现新旧格式混杂。

:::note 从 `DateUtils.defaultDateFormat` 字段赋值迁移
继承自 EasyExcel 血统、从未写入文档的公共静态字段 `DateUtils.defaultDateFormat` 与 `DateUtils.defaultLocalDateFormat` 已标记废弃，推荐改用上述 setter；字段仍然有效且现在是 `volatile` 的，但直接写字段会让进行中的工作簿不确定地混杂新旧值。`DateUtils.defaultYearFormat` 尚未随任何版本发布，已直接改为 setter。字段将在下一个大版本移除。
:::

:::info 转换器贡献者须知
需要默认格式的新转换器应当使用 `GlobalConfiguration` 字段配合 `DateUtils.resolve*Format(contentProperty, globalConfiguration)`，而不是在 `DateUtils` 上新增公共静态可变状态。字符串形式有规范表示的类型（例如基于 ISO 的格式）可以保留该表示、不接入级联。
:::

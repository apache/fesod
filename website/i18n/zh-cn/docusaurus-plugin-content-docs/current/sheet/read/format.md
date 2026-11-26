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

本章节介绍读取数据时的格式化处理。

## 自定义格式读取

### 概述

读取 Excel 文件时，日期和数字单元格可以通过注解自动转换为格式化的 `String` 值。当您需要精确控制日期和数字的文本表示时，此功能非常实用。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("日期标题")
    private String date;

    @NumberFormat("#.##%")
    @ExcelProperty("数字标题")
    private String doubleData;
}
```

### 转换行为

| Excel 单元格值 | 注解 | Java 字段值 |
|:---|:---|:---|
| `Hello` | `@ExcelProperty(converter = CustomStringStringConverter.class)` | `"自定义：Hello"` |
| `2025-01-01 12:30:00` | `@DateTimeFormat("yyyy-MM-dd HH:mm:ss")` | `"2025-01-01 12:30:00"` |
| `0.56` | `@NumberFormat("#.##%")` | `"56%"` |

:::tip
POJO 中所有字段均为 `String` 类型。Fesod 会在设置字段值之前，应用配置的转换器/格式对原始 Excel 单元格值进行转换。
:::

### 代码示例

```java
@Test
public void converterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ConverterData.class, new PageReadListener<ConverterData>(dataList -> {
        for (ConverterData data : dataList) {
            log.info("读取到一行数据：{}", JSON.toJSONString(data));
        }
    })).sheet().doRead();
}
```

---

## 默认日期字符串解析

当读取字符串单元格并转换为日期时间字段、且未声明 `@DateTimeFormat` 时，Fesod 会按固定规则回退解析。
对于 `java.util.Date`、`LocalDate`、`LocalDateTime` 和 `java.sql.Timestamp` 字段，根据字符串长度选择格式：

| 字符串长度 | 格式 |
|:---|:---|
| 19 | `yyyy-MM-dd HH:mm:ss` / `yyyy/MM/dd HH:mm:ss` |
| 17 | `yyyyMMdd HH:mm:ss` |
| 16 | `yyyy-MM-dd HH:mm` / `yyyy/MM/dd HH:mm` |
| 14 | `yyyyMMddHHmmss` |
| 10 | `yyyy-MM-dd` / `yyyy/MM/dd` |

字符串不含 `-` 时使用斜杠分隔的格式。`LocalDate` 同时接受日期和日期时间字符串（忽略时间部分），
`LocalDateTime` 则必须包含时间部分。

其他日期时间类型有各自的默认格式：

| Java 类型 | 默认格式 |
|:---|:---|
| `LocalTime`、`java.sql.Time` | `HH:mm:ss` 或 `HH:mm` |
| `OffsetDateTime` | 带显式偏移量的 ISO-8601，例如 `2024-01-02T03:04:05+08:00` |
| `Instant` | 带 UTC 偏移量的 ISO-8601，例如 `2024-01-02T03:04:05Z`；其他偏移量会被拒绝 |
| `Year` | `yyyy` |

`Instant` 只接受带 UTC 偏移量的 ISO-8601 字符串，且不能用 `@DateTimeFormat` 自定义解析格式。

不符合上述格式的值会直接抛出异常导致读取失败：字符串长度不在表中时，长度查找即抛出异常；
长度匹配但内容不符合所选格式时，解析器抛出异常。例如使用 `T` 分隔符或带毫秒的 ISO-8601 值、
未补零的值（如 `2024/1/2`）都会被拒绝。输入使用其他格式时，请显式声明格式：

```java
@DateTimeFormat("yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime time;
```

:::tip
默认格式只是对常见格式的兜底，且按字符串长度选择。需要保证解析行为稳定时，请显式声明 `@DateTimeFormat`。
:::

---

## 注解参考

### `@DateTimeFormat`

当字段类型为 `String` 时，将日期单元格转换为格式化字符串。

| 参数 | 默认值 | 说明 |
|:---|:---|:---|
| `value` | 空 | 日期格式模式，参考 `java.text.SimpleDateFormat` |
| `use1904windowing` | 自动 | 如果 Excel 文件使用 1904 日期系统，设置为 `true` |

### `@NumberFormat`

当字段类型为 `String` 时，将数字单元格转换为格式化字符串。

| 参数 | 默认值 | 说明 |
|:---|:---|:---|
| `value` | 空 | 数字格式模式，参考 `java.text.DecimalFormat` |
| `roundingMode` | `HALF_UP` | 格式化时的舍入模式 |

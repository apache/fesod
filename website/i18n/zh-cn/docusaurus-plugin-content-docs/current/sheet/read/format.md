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

---

## 读侧的默认日期格式

读取通常不需要默认格式：`String` 单元格按长度嗅探模式解析，带日期格式的数字单元格使用文件中存储的格式。默认格式在读侧只影响两个边缘场景：

- `Year` 字段：`String` 单元格按解析出的 `Year` 格式解析（`@DateTimeFormat` 优先，其次是按调用的 `defaultYearFormat` 参数，最后是进程级默认值 `yyyy`）。
- 带空 `@DateTimeFormat` 的 `String` 字段读取数字日期单元格：按解析出的日期格式渲染值。

完整的级联规则、构建器参数与进程级 setter 见[写侧文档 - 默认日期格式](../write/format.md#默认日期格式)；每个读上下文在创建时对进程级默认值做快照。

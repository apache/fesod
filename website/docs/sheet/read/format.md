---
id: 'format'
title: 'Format'
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

# Formatting

This chapter introduces data formatting when reading data.

## Custom Format Reading

### Overview

When reading Excel files, date and number cells can be automatically converted to formatted `String` values using annotations. This is useful when you want to control the exact text representation of dates and numbers.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "String Title", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("Date Title")
    private String date;

    @NumberFormat("#.##%")
    @ExcelProperty("Number Title")
    private String doubleData;
}
```

### Conversion Behavior

| Excel Cell Value | Annotation | Java Field Value |
|:---|:---|:---|
| `Hello` | `@ExcelProperty(converter = CustomStringStringConverter.class)` | `"Custom: Hello"` |
| `2025-01-01 12:30:00` | `@DateTimeFormat("yyyy-MM-dd HH:mm:ss")` | `"2025-01-01 12:30:00"` |
| `0.56` | `@NumberFormat("#.##%")` | `"56%"` |

:::tip
All fields in the POJO are `String` type. Fesod applies the configured converter/format to transform the raw Excel cell value before setting the field.
:::

### Code Example

```java
@Test
public void converterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ConverterData.class, new PageReadListener<ConverterData>(dataList -> {
        for (ConverterData data : dataList) {
            log.info("Read a row of data: {}", JSON.toJSONString(data));
        }
    })).sheet().doRead();
}
```

---

## Default Date String Parsing

When a string cell is read into a date/time field and no `@DateTimeFormat` is declared, Fesod falls back to a
fixed set of patterns. For `java.util.Date`, `LocalDate`, `LocalDateTime` and `java.sql.Timestamp` fields, the
pattern is selected by the length of the string:

| String length | Patterns |
|:---|:---|
| 19 | `yyyy-MM-dd HH:mm:ss` / `yyyy/MM/dd HH:mm:ss` |
| 17 | `yyyyMMdd HH:mm:ss` |
| 16 | `yyyy-MM-dd HH:mm` / `yyyy/MM/dd HH:mm` |
| 14 | `yyyyMMddHHmmss` |
| 10 | `yyyy-MM-dd` / `yyyy/MM/dd` |

The slash-separated pattern is used when the string contains no `-`. `LocalDate` accepts both date and
date-time strings and ignores the time part, while `LocalDateTime` requires the time part.

Other date/time types have their own defaults:

| Java type | Default patterns |
|:---|:---|
| `LocalTime`, `java.sql.Time` | `HH:mm:ss` or `HH:mm` |
| `OffsetDateTime` | ISO-8601 with an explicit offset, e.g. `2024-01-02T03:04:05+08:00` |
| `Instant` | ISO-8601 with a UTC offset, e.g. `2024-01-02T03:04:05Z`; other offsets are rejected |
| `Year` | `yyyy` |

`Instant` only accepts ISO-8601 strings with a UTC offset, and this rule cannot be customized with
`@DateTimeFormat`.

Values that do not match these patterns fail to parse with an exception: the length-based lookup throws
when the string length is not listed, and the parser throws when the content does not match the selected
pattern. For example, ISO-8601 values with a `T` separator or milliseconds, and non-zero-padded values
such as `2024/1/2` are rejected. When the input uses another format, declare the pattern explicitly:

```java
@DateTimeFormat("yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime time;
```

:::tip
The default patterns are a fallback for common formats and are selected by string length. Declare
`@DateTimeFormat` explicitly when the format of the input must be guaranteed.
:::

---

## Annotation Reference

### `@DateTimeFormat`

Converts date cells to formatted strings when the field type is `String`.

| Parameter | Default | Description |
|:---|:---|:---|
| `value` | Empty | Date format pattern, refer to `java.text.SimpleDateFormat` |
| `use1904windowing` | Auto | Set to `true` if the Excel file uses the 1904 date system |

### `@NumberFormat`

Converts number cells to formatted strings when the field type is `String`.

| Parameter | Default | Description |
|:---|:---|:---|
| `value` | Empty | Number format pattern, refer to `java.text.DecimalFormat` |
| `roundingMode` | `HALF_UP` | Rounding mode when formatting |

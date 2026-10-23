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

---

## Default Date Formats on Read

Reading rarely needs a default pattern: `String` cells are parsed by length-based pattern detection, and date-formatted numeric cells use the format stored in the file. The default patterns matter on read in two edge cases:

- `Year` fields: a `String` cell is parsed with the resolved `Year` pattern (`@DateTimeFormat` over the per-call `defaultYearFormat` parameter over the process-wide default, `yyyy`).
- `String` fields with an empty `@DateTimeFormat` reading a numeric date cell: the resolved date pattern renders the value.

The full cascade, the builder parameters and the process-wide setters are documented in [write format - Default Date Formats](../write/format.md#default-date-formats); each read context snapshots the process-wide defaults at creation.

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

This chapter introduces data formatting when writing data.

## Custom Format Writing

### Overview

Supports date, number, or other custom formats through annotations.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "String Title", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy/MM/dd HH:mm:ss")
    @ExcelProperty("Date Title")
    private Date date;

    @NumberFormat("#.##%")
    @ExcelProperty("Number Title")
    private Double doubleData;
}
```

### Code Example

```java
@Test
public void converterWrite() {
    String fileName = "converterWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ConverterData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>Custom: String0</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">3</td><td>Custom: String1</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">4</td><td>Custom: String2</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>Custom: String9</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td></tr>
</tbody>
</table>
</div>

## Default Date Formats

### Overview

When a date-typed field carries no `@DateTimeFormat` pattern, Fesod resolves its pattern through a three-tier cascade:

| Priority | Source | Scope |
| --- | --- | --- |
| 1 | `@DateTimeFormat("...")` | per field |
| 2 | `.defaultDateFormat(...)` builder parameter | per read/write call |
| 3 | `DateUtils.setDefaultDateFormat(...)` | process-wide |
| — | built-in constant | `yyyy-MM-dd HH:mm:ss` |

Three builder parameters cover the date types:

| Builder parameter | Applies to | Built-in constant |
| --- | --- | --- |
| `defaultDateFormat` | `Date`, `LocalDateTime`, `OffsetDateTime` | `yyyy-MM-dd HH:mm:ss` |
| `defaultLocalDateFormat` | `LocalDate` | `yyyy-MM-dd` |
| `defaultYearFormat` | `Year` | `yyyy` |

### Per-Call Default

```java
FesodSheet.write(fileName, OrderData.class)
    .defaultDateFormat("yyyy/MM/dd HH:mm:ss")
    .defaultLocalDateFormat("yyyy/MM/dd")
    .sheet()
    .doWrite(data());
```

The parameters are also available on the read side (`FesodSheet.read(...)`), which mainly affects `Year` parsing when no pattern is annotated.

### Process-Wide Default

Applications that want one default everywhere can set it once at startup instead of annotating every field or repeating the builder parameter:

```java
DateUtils.setDefaultDateFormat("yyyy/MM/dd HH:mm:ss");
DateUtils.setDefaultLocalDateFormat("yyyy/MM/dd");
DateUtils.setDefaultYearFormat("yyyy");
```

The setters reject blank patterns and `DateUtils.resetDefaultDateFormats()` restores the built-in values.

### Snapshot Semantics

Each read/write context snapshots the process-wide default when it is created, so every cell of one workbook is written with one consistent pattern. Changing the process-wide default mid-run only affects contexts created afterwards — it never leaves an in-flight workbook half-formatted with the old value and half with the new one.

:::note Migration from `DateUtils.defaultDateFormat` field writes
The public static fields `DateUtils.defaultDateFormat` and `DateUtils.defaultLocalDateFormat` (inherited from the EasyExcel lineage, never documented) are deprecated in favor of the setters above; they still work and are now `volatile`, but direct field writes make an in-flight workbook nondeterministically mixed. `DateUtils.defaultYearFormat` never shipped in a release and was replaced by the setter outright. The fields will be removed in the next major version.
:::

:::info For converter contributors
New converters that need a default pattern should take a `GlobalConfiguration` field plus a `DateUtils.resolve*Format(contentProperty, globalConfiguration)` call instead of introducing new public static mutable state on `DateUtils`. Types whose string form has a canonical representation (for example ISO-based formats) may keep that representation instead of joining the cascade.
:::

---
id: 'custom-converter'
title: 'Custom Converter'
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

# Custom Converter

This chapter introduces how to create and register custom converters for both read and write operations.

## Overview

Fesod splits conversion into two directions, each with its own interface:

- `ReadConverter<T>` — converts spreadsheet cell data into Java objects when reading.
- `WriteConverter<T>` — converts Java objects into spreadsheet cell data when writing.
- `Converter<T>` — combines both interfaces for two-way conversion.

Converters can be registered per-field (via annotation), globally (via builder), or bound to specific columns (via `ColumnBinding`).

## Creating a Custom Converter

### Read & Write: `Converter<T>`

Implement `Converter<T>` when the same converter handles both reading and writing.

```java
public class CustomStringStringConverter implements Converter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        return "Custom: " + context.getReadCellData().getStringValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>("Custom: " + context.getValue());
    }
}
```

### Read-Only: `ReadConverter<T>`

Implement `ReadConverter<T>` when only reading requires customization.

```java
public class CustomStringReadConverter implements ReadConverter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        return "Custom: " + context.getReadCellData().getStringValue();
    }
}
```

### Write-Only: `WriteConverter<T>`

Implement `WriteConverter<T>` when only writing requires customization.

```java
public class CustomStringWriteConverter implements WriteConverter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>("Custom: " + context.getValue());
    }
}
```

---

## Column-Bound Converters (`ColumnBinding`)

By default, a globally registered converter applies to all eligible columns across the entire sheet. Implement `ColumnBinding` to restrict a converter (read or write) to specific column indices (0-based).

This is especially useful in **no-bean** scenarios where you cannot use field annotations:

```java
public class ColumnZeroStringWriteConverter implements WriteConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // Binds exclusively to Column 0
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>("Col0: " + context.getValue());
    }
}
```

- Override `columnIndex()` for a single column.
- Override `columnIndexes()` if you need to match multiple columns (e.g. `Set.of(1, 3)`).

:::warning
If a converter implements `ColumnBinding` but returns `null` or an empty set, it will **match no columns at all** and will **not** fall back as a global converter. Do not implement `ColumnBinding` if you intend to create a global converter.
:::

## Registration Scope & Precedence

| Approach         | Scope              | How to Register                                                                               |
|:-----------------|:-------------------|:----------------------------------------------------------------------------------------------|
| **Per-Field**    | Single field       | `@ExcelProperty(converter = MyConverter.class)`                                               |
| **Column-Bound** | Target column(s)   | Implement `ColumnBinding` + register on builder                                               |
| **Global**       | All matching types | `.registerReadConverter(...)`, `.registerWriteConverter(...)`,<br/> `.registerConverter(...)` |

### Converter Resolution Priority

1. **Field-level** (`@ExcelProperty(converter = ...)`)
2. **Column-bound custom converter** (`ColumnBinding` matching target column index)
3. **Global custom converter** (if multiple match the same type, **the last one registered wins**)
4. **Built-in default converter**

*Scope Rule: Registrations at the sheet-level take precedence over workbook-level registrations.*

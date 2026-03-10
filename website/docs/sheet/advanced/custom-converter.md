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

Fesod provides a `Converter` interface that allows you to define custom data transformation logic. Converters can be registered per-field (via annotation) or globally (via builder), and work for both reading and writing.

## Creating a Custom Converter

### Converter Implementation

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

---

## Per-Field vs. Global Registration

| Approach | Scope | How |
|:---|:---|:---|
| Per-field | Single field only | `@ExcelProperty(converter = MyConverter.class)` |
| Global | All fields matching Java type + Excel type | `.registerConverter(new MyConverter())` on the builder |

### Converter Resolution Priority

1. Field-level converter (`@ExcelProperty(converter = ...)`) — highest priority
2. Builder-level converter (`.registerConverter(...)`)
3. Built-in default converter — lowest priority

---

## Global Registration Example

### Write with Global Converter

```java
@Test
public void customConverterWrite() {
    String fileName = "customConverterWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .registerConverter(new CustomStringStringConverter())
        .sheet()
        .doWrite(data());
}
```

### Read with Global Converter

```java
@Test
public void customConverterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
        .registerConverter(new CustomStringStringConverter())
        .sheet()
        .doRead();
}
```

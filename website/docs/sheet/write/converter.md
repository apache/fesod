---
id: 'converter'
title: 'Converter'
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

# Converter

This chapter introduces how to use custom converters when writing data.

## Overview

Fesod supports custom data converters for write operations. You can transform Java field values before they are written to Excel cells, enabling custom formatting, encryption, or any data transformation logic.

## Per-Field Converter

### Overview

Apply a converter to a specific field using the `@ExcelProperty(converter = ...)` annotation.

### Converter

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

---

## Global Converter Registration

### Overview

Register a converter at the builder level to apply it to ALL fields matching the Java type and Excel type. This is useful when you want the same transformation applied globally without annotating each field.

### Code Example

```java
@Test
public void globalConverterWrite() {
    String fileName = "globalConverterWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, DemoData.class)
        .registerConverter(new CustomStringStringConverter())
        .sheet()
        .doWrite(data());
}
```

---

## Converter Resolution Priority

When multiple converters could apply to a field, Fesod resolves them in this order:

1. Field-level converter (`@ExcelProperty(converter = ...)`) — highest priority
2. Builder-level converter (`.registerConverter(...)`)
3. Built-in default converter — lowest priority

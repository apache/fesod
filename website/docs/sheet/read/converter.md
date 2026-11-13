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

# Format Conversion

Fesod supports date, number, and custom format conversions.

## Overview

During usage, we may need to convert read or written data into specific formats. Fesod provides a flexible converter
mechanism that allows users to define custom data conversion rules to meet various business requirements.

## Example

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyyMMddHHmmss")
    private String date;

    @NumberFormat("#.##%")
    private String doubleData;
}
```

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
        return new WriteCellData<>(context.getValue());
    }
}
```

### Code Example

```java
@Test
public void converterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ConverterData.class, new DemoDataListener())
            .registerConverter(new CustomStringStringConverter()) // Register custom converter
            .sheet()
            .doRead();
}
```

## Converter Resolution Priority

### Converter resolution across read and write

Registered converters are looked up by the key `(JavaType, CellDataType)`, but the two directions build that key differently, and both give priority to explicit registrations over wildcard ones.

**Write.** The target cell type is `null` for xlsx/xls (the converter decides the cell type it produces) and forced to `STRING` for CSV. A converter registered with `supportExcelTypeKey() == null` (the wildcard key `(JavaType, null)`) therefore wins on xlsx/xls writes directly, and is additionally registered under `(JavaType, STRING)` so CSV honors it too. That extra registration only takes over the `STRING` slot when it is still held by the built-in default, so an explicitly registered converter is never displaced, regardless of registration order.

**Read.** Cells carry a concrete type from the file, so every format resolves `(JavaType, cellData.getType())` - e.g. `(Boolean, STRING)` for a text cell. A wildcard read registration is expanded under every concrete cell-type key (overwriting built-in defaults, skipping `EMPTY`), while keys already held by an explicitly registered converter are left untouched - again, explicit wins.

**In short:** one wildcard registration covers every format and every cell type, an explicit registration always beats the wildcard, and user registrations always beat built-ins.

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

# Write Data Conversion

Fesod supports format conversion during data writing, allowing users to define custom data conversion rules to meet various business requirements.

## Per-Field Converter

Apply a converter to a specific field using the `@ExcelProperty(converter = ...)` annotation. The annotation requires the combined `Converter` interface due to backward compatibility.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
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

### Code Example

```java
@Test
public void converterWrite() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .sheet()
        .doWrite(data());
}
```

---

## Global Converter Registration

Register a `WriteConverter<T>` on the builder to apply it globally to all fields of matching Java type.

For write-only, implementing `WriteConverter<T>` is recommended.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("String Title")
    private String string;
}
```

### Converter

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

### Code Example

```java
@Test
public void globalConverterWrite() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .registerWriteConverter(new CustomStringWriteConverter())
        .sheet()
        .doWrite(data());
}
```

---

## Column-Bound Converter

In no-bean scenarios where annotations cannot be used, implement `ColumnBinding` to restrict a write converter to specific columns (0-based):

### Converter

```java
public class ColumnZeroStringWriteConverter implements WriteConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // Only applies to column 0
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

### Code Example

```java
@Test
public void columnBoundWrite() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.write(fileName)
            .head(HeadBuilder.forSimple("Custom String Title", "String Title"))
            .registerWriteConverter(new ColumnZeroStringWriteConverter())
            .sheet()
            .doWrite(data());
}
```

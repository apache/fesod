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

# Read Data Conversion

Fesod supports format conversion during data reading, allowing users to define custom data conversion rules to meet various business requirements.

## Per-Field Converter

Apply a converter to a specific field using the `@ExcelProperty(converter = ...)` annotation. The annotation requires the combined `Converter` interface due to backward compatibility.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
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
        return new WriteCellData<>("Custom: " + context.getValue());
    }
}
```

### Code Example

```java
@Test
public void converterRead() {
    String fileName = "path/from/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## Global Converter Registration

Register a converter on the builder to apply it across all fields matching the Java type and Spreadsheet cell type.

For read-only, implementing `ReadConverter<T>` is recommended.

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

### Code Example

```java
@Test
public void globalConverterRead() {
    String fileName = "path/from/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .registerReadConverter(new CustomStringReadConverter())
            .sheet()
            .doRead();
}
```

---

## Column-Bound Converter

In no-bean scenarios where annotations cannot be used, implement `ColumnBinding` to restrict a read converter to specific columns (0-based):

### Converter

```java
public class ColumnZeroStringReadConverter implements ReadConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // Only applies to column 0
    }

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
        return "Col0: " + context.getReadCellData().getStringValue();
    }
}
```

### Code Example

```java
@Test
public void columnBoundRead() {
    String fileName = "path/from/demo.xlsx";

    FesodSheet.read(fileName, new NoModelDataListener())
            .head(HeadBuilder.forSimple("Custom String Title", "String Title"))
            .registerReadConverter(new ColumnZeroStringReadConverter())
            .sheet()
            .doRead();
}
```

---
id: 'converter'
title: 'Converter'
---

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

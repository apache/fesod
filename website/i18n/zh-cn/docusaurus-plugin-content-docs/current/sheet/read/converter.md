---
id: 'converter'
title: '格式转换'
---

# 格式转换

Fesod 支持日期、数字、自定义格式转换。

## 概述

在使用过程中，我们可能需要对读取或写入的数据进行特定格式的转换。Fesod 提供了灵活的转换器机制，允许用户自定义数据转换规则，以满足各种业务需求。

## 示例

### POJO 类

```java

@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private String date;

    @NumberFormat("#.##%")
    private String doubleData;
}
```

### 转换器

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
        return "自定义：" + context.getReadCellData().getStringValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>(context.getValue());
    }
}
```

### 代码示例

```java

@Test
public void converterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ConverterData.class, new DemoDataListener())
            .registerConverter(new CustomStringStringConverter()) // 注册自定义转换器
            .sheet()
            .doRead();
}
```

---
id: 'converter'
title: '转换器'
---

# 转换器

本章节介绍写入数据时如何使用自定义转换器。

## 概述

Fesod 支持写入操作的自定义数据转换器。您可以在 Java 字段值写入 Excel 单元格之前进行转换，实现自定义格式化、加密或任何数据转换逻辑。

## 字段级转换器

### 概述

通过 `@ExcelProperty(converter = ...)` 注解对特定字段应用转换器。

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
        return new WriteCellData<>("自定义：" + context.getValue());
    }
}
```

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    @ExcelProperty("日期标题")
    private Date date;

    @NumberFormat("#.##%")
    @ExcelProperty("数字标题")
    private Double doubleData;
}
```

### 代码示例

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

## 全局转换器注册

### 概述

在构建器级别注册转换器，将其应用于所有匹配 Java 类型和 Excel 类型的字段。当您希望对所有字段统一应用相同的转换逻辑时非常有用，无需逐一注解。

### 代码示例

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

## 转换器解析优先级

当多个转换器可能应用于某个字段时，Fesod 按以下顺序进行解析：

1. 字段级转换器（`@ExcelProperty(converter = ...)`）— 最高优先级
2. 构建器级转换器（`.registerConverter(...)`）
3. 内置默认转换器 — 最低优先级

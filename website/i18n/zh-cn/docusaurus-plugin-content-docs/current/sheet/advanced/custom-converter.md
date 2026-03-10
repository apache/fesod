---
id: 'custom-converter'
title: '自定义转换器'
---

# 自定义转换器

本章节介绍如何创建和注册自定义转换器，同时支持读取和写入操作。

## 概述

Fesod 提供了 `Converter` 接口，允许您定义自定义数据转换逻辑。转换器可以按字段注册（通过注解）或全局注册（通过构建器），同时适用于读取和写入。

## 创建自定义转换器

### 转换器实现

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

---

## 字段级与全局注册

| 方式 | 作用范围 | 使用方法 |
|:---|:---|:---|
| 字段级 | 仅单个字段 | `@ExcelProperty(converter = MyConverter.class)` |
| 全局 | 所有匹配 Java 类型 + Excel 类型的字段 | 在构建器上使用 `.registerConverter(new MyConverter())` |

### 转换器解析优先级

1. 字段级转换器（`@ExcelProperty(converter = ...)`）— 最高优先级
2. 构建器级转换器（`.registerConverter(...)`）
3. 内置默认转换器 — 最低优先级

---

## 全局注册示例

### 使用全局转换器写入

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

### 使用全局转换器读取

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

---
id: 'format'
title: '格式化'
---

# 格式化

本章节介绍读取数据时的格式化处理。

## 自定义格式读取

### 概述

读取 Excel 文件时，日期和数字单元格可以通过注解自动转换为格式化的 `String` 值。当您需要精确控制日期和数字的文本表示时，此功能非常实用。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("日期标题")
    private String date;

    @NumberFormat("#.##%")
    @ExcelProperty("数字标题")
    private String doubleData;
}
```

### 转换行为

| Excel 单元格值 | 注解 | Java 字段值 |
|:---|:---|:---|
| `Hello` | `@ExcelProperty(converter = CustomStringStringConverter.class)` | `"自定义：Hello"` |
| `2025-01-01 12:30:00` | `@DateTimeFormat("yyyy-MM-dd HH:mm:ss")` | `"2025-01-01 12:30:00"` |
| `0.56` | `@NumberFormat("#.##%")` | `"56%"` |

:::tip
POJO 中所有字段均为 `String` 类型。Fesod 会在设置字段值之前，应用配置的转换器/格式对原始 Excel 单元格值进行转换。
:::

### 代码示例

```java
@Test
public void converterRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ConverterData.class, new PageReadListener<ConverterData>(dataList -> {
        for (ConverterData data : dataList) {
            log.info("读取到一行数据：{}", JSON.toJSONString(data));
        }
    })).sheet().doRead();
}
```

---

## 注解参考

### `@DateTimeFormat`

当字段类型为 `String` 时，将日期单元格转换为格式化字符串。

| 参数 | 默认值 | 说明 |
|:---|:---|:---|
| `value` | 空 | 日期格式模式，参考 `java.text.SimpleDateFormat` |
| `use1904windowing` | 自动 | 如果 Excel 文件使用 1904 日期系统，设置为 `true` |

### `@NumberFormat`

当字段类型为 `String` 时，将数字单元格转换为格式化字符串。

| 参数 | 默认值 | 说明 |
|:---|:---|:---|
| `value` | 空 | 数字格式模式，参考 `java.text.DecimalFormat` |
| `roundingMode` | `HALF_UP` | 格式化时的舍入模式 |

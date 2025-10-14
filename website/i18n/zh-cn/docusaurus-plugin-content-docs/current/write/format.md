---
id: 'format'
title: '格式化'
---

# 格式化

本章节将介绍写入数据时的数据格式化。

## 自定义格式写入

### 概述

支持日期、数字或其他自定义格式，通过注解实现。

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
    FastExcel.write(fileName, ConverterData.class)
        .sheet()
        .doWrite(data());
}
```

### 结果

![img](/img/docs/write/converterWrite.png)

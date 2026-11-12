---
id: 'converter'
title: '写入数据转换'
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

# 写入数据转换

Fesod 支持写入数据过程中的格式转换，允许用户自定义数据转换规则，以满足各种业务需求。

## 字段级转换器

通过 `@ExcelProperty(converter = ...)` 注解对特定字段应用转换器。出于向后兼容的考虑，该注解要求实现组合的 `Converter` 接口。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("日期标题")
    private Date date;

    @NumberFormat("#.##%")
    @ExcelProperty("数字标题")
    private Double doubleData;
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
        return new WriteCellData<>("自定义：" + context.getValue());
    }
}
```

### 代码示例

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

## 全局转换器注册

在构建器上注册 `WriteConverter<T>`，使其全局应用于所有匹配 Java 类型的字段。

仅写入场景推荐实现 `WriteConverter<T>`。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
}
```

### 转换器

```java
public class CustomStringWriteConverter implements WriteConverter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>("自定义：" + context.getValue());
    }
}
```

### 代码示例

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

## 列绑定转换器

在无法使用字段注解的无实体类场景下，实现 `ColumnBinding` 可以将写入转换器限定到特定列（从 0 开始）：

### 转换器

```java
public class ColumnZeroStringWriteConverter implements WriteConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // 仅应用于第 0 列
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>("第0列：" + context.getValue());
    }
}
```

### 代码示例

```java
@Test
public void columnBoundWrite() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.write(fileName)
            .head(HeadBuilder.forSimple("自定义字符串标题", "字符串标题"))
            .registerWriteConverter(new ColumnZeroStringWriteConverter())
            .sheet()
            .doWrite(data());
}
```

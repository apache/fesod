---
id: 'converter'
title: '读取数据转换'
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

# 读取数据转换

Fesod 支持读取数据过程中的格式转换，允许用户自定义数据转换规则，以满足各种业务需求。

## 字段级转换器

通过 `@ExcelProperty(converter = ...)` 注解对特定字段应用转换器。出于向后兼容的考虑，该注解要求实现组合的 `Converter` 接口。

### POJO 类

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
public void converterRead() {
    String fileName = "path/from/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## 全局转换器注册

在构建器上注册转换器，使其应用于所有匹配 Java 类型和 Spreadsheet 单元格类型的字段。

仅读取场景推荐实现 `ReadConverter<T>`。

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
        return "自定义：" + context.getReadCellData().getStringValue();
    }
}
```

### 代码示例

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

## 列绑定转换器

在无法使用字段注解的无实体类场景下，实现 `ColumnBinding` 可以将读取转换器限定到特定列（从 0 开始）：

### 转换器

```java
public class ColumnZeroStringReadConverter implements ReadConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // 仅应用于第 0 列
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
        return "第0列：" + context.getReadCellData().getStringValue();
    }
}
```

### 代码示例

```java
@Test
public void columnBoundRead() {
    String fileName = "path/from/demo.xlsx";

    FesodSheet.read(fileName, new NoModelDataListener())
            .head(HeadBuilder.forSimple("自定义字符串标题", "字符串标题"))
            .registerReadConverter(new ColumnZeroStringReadConverter())
            .sheet()
            .doRead();
}
```

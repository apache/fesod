---
id: 'converter'
title: '格式转换'
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

## 转换器解析优先级

### 跨读取与写入的转换器解析

注册的转换器通过 `(JavaType, CellDataType)` 键进行查找，但读取与写入两个方向构建该键的方式不同，且二者都优先匹配显式注册、其次才是通配注册。

**写入。** 对于 xlsx/xls，目标单元格类型为 `null`（由转换器自行决定产出的单元格类型）；对于 CSV 则强制为 `STRING`。因此，以 `supportExcelTypeKey() == null`（通配键 `(JavaType, null)`）注册的转换器会直接在 xlsx/xls 写入时生效，并且会额外注册到 `(JavaType, STRING)` 键下，使 CSV 写入同样生效。这一额外注册只会在 `STRING` 槽位仍由内置默认转换器持有时才接管该槽位，因此无论注册顺序如何，显式注册的转换器都不会被顶掉。

**读取。** 单元格本身带有来自文件的具体类型，因此所有格式都会解析 `(JavaType, cellData.getType())` — 例如文本单元格对应 `(Boolean, STRING)`。通配的读取注册会展开到每一个具体单元格类型键下（覆盖内置默认值、跳过 `EMPTY`），而已经被显式注册的转换器占用的键则保持不变 — 同样是显式注册优先。

**一句话总结：** 一次通配注册即可覆盖所有格式与所有单元格类型；显式注册永远优先于通配注册；用户注册永远优先于内置转换器。

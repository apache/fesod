---
id: 'custom-converter'
title: '自定义转换器'
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

# 自定义转换器

本章节介绍如何创建和注册自定义转换器，同时支持读取和写入操作。

## 概述

Fesod 将转换拆分为两个方向，各自拥有独立的接口：

- `ReadConverter<T>` —— 读取时将电子表格单元格数据转换为 Java 对象。
- `WriteConverter<T>` —— 写入时将 Java 对象转换为电子表格单元格数据。
- `Converter<T>` —— 组合以上两个接口，实现双向转换。

转换器可以按字段注册（通过注解）、全局注册（通过构建器），或绑定到特定列（通过 `ColumnBinding`）。

## 创建自定义转换器

### 读写双向：`Converter<T>`

当同一个转换器需要同时处理读取和写入时，实现 `Converter<T>`。

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

### 仅读取：`ReadConverter<T>`

当只需要自定义读取逻辑时，实现 `ReadConverter<T>`。

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

### 仅写入：`WriteConverter<T>`

当只需要自定义写入逻辑时，实现 `WriteConverter<T>`。

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

---

## 列绑定转换器（`ColumnBinding`）

默认情况下，全局注册的转换器会作用于整个工作表中所有符合条件的列。实现 `ColumnBinding` 可以将转换器（读取或写入）限定到特定的列索引（从 0 开始）。

在无法使用字段注解的**无实体类**场景下尤其有用：

```java
public class ColumnZeroStringWriteConverter implements WriteConverter<String>, ColumnBinding {
    @Override
    public Integer columnIndex() {
        return 0; // 仅绑定到第 0 列
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

- 绑定单个列时覆写 `columnIndex()`。
- 需要匹配多个列时覆写 `columnIndexes()`（例如 `Set.of(1, 3)`）。

:::warning
如果转换器实现了 `ColumnBinding` 但返回 `null` 或空集合，它将**匹配不到任何列**，也**不会**退化为全局转换器。如果要创建全局转换器，请不要实现 `ColumnBinding`。
:::

## 注册范围与优先级

| 方式       | 作用范围       | 注册方法                                                                                       |
|:-----------|:---------------|:-----------------------------------------------------------------------------------------------|
| **字段级** | 单个字段       | `@ExcelProperty(converter = MyConverter.class)`                                                |
| **列绑定** | 目标列         | 实现 `ColumnBinding` 并在构建器上注册                                                          |
| **全局**   | 所有匹配的类型 | `.registerReadConverter(...)`、`.registerWriteConverter(...)`、<br/> `.registerConverter(...)` |

### 转换器解析优先级

1. **字段级**（`@ExcelProperty(converter = ...)`）
2. **列绑定自定义转换器**（匹配目标列索引的 `ColumnBinding`）
3. **全局自定义转换器**（同一类型匹配多个转换器时，**后注册的优先**）
4. **内置默认转换器**

*范围规则：工作表（sheet）级注册优先于工作簿（workbook）级注册。*

---
id: 'pojo'
title: '实体类'
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

# POJO

本章节将介绍通过设置 POJO 来读取数据的使用

## 按列名或列下标读取

### 概述

您可以通过指定**列名**或**列下标**来读取电子表格数据。这使得与动态生成的电子表格文件交互更加灵活。

### 示例代码

#### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class IndexOrNameData {
    @ExcelProperty(index = 2)
    private Double doubleData;

    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;
}
```

#### 代码示例

```java
@Test
public void indexOrNameRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, IndexOrNameData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

使用 includeColumnIndexes 时的注意事项：
如果在读取器上配置了 includeColumnIndexes（例如选择原始列 [0, 2, 4]），解析后的列会被重新映射为连续的索引（0, 1, 2）。请确保 @ExcelProperty(index = ...) 中的索引对应的是重新映射后的列索引，而非原始文件中的列索引。

---

## 单元格对象

### 概述

使用 `CellData` 类型接收单元格数据以支持公式和多种单元格格式。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class CellDataReadDemoData {
    private CellData<String> string;
    private CellData<Date> date;
    private CellData<Double> doubleData;
    private CellData<String> formulaValue;
}
```

### 代码示例

```java
@Test
public void cellDataRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, CellDataReadDemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## 无 POJO 类与监听器

### 概述

Fesod 支持不定义 POJO 类直接读取电子表格文件，通过 `Map<Integer, String>` 直接读取数据。

### 数据监听器

```java
@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("读取到一条数据:{}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据读取完成！");
    }
}
```

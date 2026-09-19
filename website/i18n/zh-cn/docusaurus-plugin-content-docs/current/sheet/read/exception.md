---
id: 'exception'
title: '异常处理'
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

# 异常处理

本章节将介绍如何处理异常。

## 概述

通过重写监听器的 `onException` 方法处理数据转换或其他读取异常。

## 数据监听器

```java
@Slf4j
public class DemoExceptionListener extends AnalysisEventListener<ExceptionDemoData> {
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("失败: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("第 {} 行, 第 {} 列异常, 数据: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(ExceptionDemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
```

## 代码示例

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.read(pathname, ExceptionDemoData.class, new DemoExceptionListener())
            .sheet()
            .doRead();
}
```

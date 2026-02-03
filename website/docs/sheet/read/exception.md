---
id: 'exception'
title: 'Exception'
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

# Exception Handling

This chapter introduces how to handle exceptions.

## Overview

Handle data conversion or other reading exceptions by overriding the `onException` method in the listener.

## Data Listener

```java
@Slf4j
public class DemoExceptionListener extends AnalysisEventListener<ExceptionDemoData> {
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Failed: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("Row {}, Column {} exception, data: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(ExceptionDemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}
```

## Code Example

```java
@Test
public void exceptionRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ExceptionDemoData.class, new DemoExceptionListener())
            .sheet()
            .doRead();
}
```

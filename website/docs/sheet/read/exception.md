---
id: 'exception'
title: 'Exception'
---

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

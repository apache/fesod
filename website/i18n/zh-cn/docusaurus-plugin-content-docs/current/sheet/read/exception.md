---
id: 'exception'
title: '异常处理'
---

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

@Test
public void exceptionRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, ExceptionDemoData.class, new DemoExceptionListener())
            .sheet()
            .doRead();
}
```

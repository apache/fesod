---
id: 'head'
title: 'Head'
---

# Headers

This chapter introduces how to read header data from the spreadsheet files.

## Reading Header Data

### Overview

You can obtain header information by overriding the `invokeHead` method in the listener.

### Data Listener

```java

@Slf4j
public class DemoHeadDataListener extends AnalysisEventListener<DemoData> {
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Parsed header data: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
```

### Code Example

```java

@Test
public void headerRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoHeadDataListener())
            .sheet()
            .doRead();
}
```

---

## Multi-Row Header Reading

### Overview

Parse multi-row headers by setting the `headRowNumber` parameter or automatically based on header annotations in entity
classes.

### Code Example

```java

@Test
public void complexHeaderRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            // Set the number of header rows, default is 1
            .headRowNumber(2)
            .doRead();
}
```

---

## Header POJO

### Overview

Set header POJO using the `head()` method.

### Code Example

```java

@Test
public void headerPojoRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, new DemoDataListener())
            .head(DemoData.class)
            .sheet()
            .doRead();
}
```

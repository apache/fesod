---
id: 'simple'
title: 'Simple'
---

# Simple Reading

This chapter introduces how to use this project to perform simple spreadsheet reading operations.

## Data Listeners

### Overview

Fesod provides a listener mechanism for processing each row of data while reading spreadsheet files.

### Usage

Data listeners need to be instantiated and support various usage patterns.

#### Instantiation

Listeners cannot be managed by Spring and must be re-instantiated each time a spreadsheet file is read.

#### `Lambda` Expressions

```java

@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new PageReadListener<>(dataList -> {
        for (DemoData demoData : dataList) {
            log.info("Read one record: {}", JSON.toJSONString(demoData));
        }
    })).sheet().doRead();
}
```

#### Anonymous Inner Classes

```java

@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new ReadListener<DemoData>() {
        @Override
        public void invoke(DemoData data, AnalysisContext context) {
            log.info("Read one record: {}", JSON.toJSONString(data));
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("All data reading completed!");
        }
    }).sheet().doRead();
}
```

#### Data Listeners

```java

@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## POJO Classes and Listeners

### Overview

Fesod provides a simple way to read spreadsheet files. Users only need to define a POJO class to represent the data
structure, then read data through Fesod's listener mechanism.

### POJO Class

The `DemoData` POJO class corresponding to the spreadsheet structure:

```java

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### Data Listener

`DemoDataListener` is a custom listener used to process data read from spreadsheet.

```java

@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Read one record: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data reading completed!");
    }
}
```

### Code Example

```java

@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

---

## Without POJO Classes and Listeners

### Overview

Fesod supports reading spreadsheet files directly without defining POJO classes, using `Map<Integer, String>` to read
data directly, where the key is the **column index** and the value is the **cell data**.

### Data Listener

```java

@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("Read one record: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data reading completed!");
    }
}
```

---

## Synchronous Reading

### Overview

Use the `doReadSync` method to directly read spreadsheet data into a list **in memory**. This method is recommended for
scenarios with **small data volumes**. The read data can be either a list of POJO objects or a list of Maps.

### POJO Class

The `DemoData` POJO class corresponding to the spreadsheet structure:

```java

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### Code Examples

#### Reading as POJO Object List

```java

@Test
public void synchronousReadToObjectList() {
    String fileName = "path/to/demo.xlsx";

    // POJO Object List
    List<DemoData> list = FesodSheet.read(fileName)
            .head(DemoData.class)
            .sheet()
            .doReadSync();

    for (DemoData data : list) {
        log.info("Read data: {}", JSON.toJSONString(data));
    }
}
```

#### Reading as Map List

When not using POJOs, each row can be read as a Map, where the key is the column index and the value is the cell
content.

```java

@Test
public void synchronousReadToMapList() {
    String fileName = "path/to/demo.xlsx";

    // Map List
    List<Map<Integer, String>> list = FesodSheet.read(fileName)
            .sheet()
            .doReadSync();

    for (Map<Integer, String> data : list) {
        log.info("Read data: {}", JSON.toJSONString(data));
    }
}
```

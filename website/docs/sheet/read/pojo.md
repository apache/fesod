---
id: 'pojo'
title: 'POJO'
---

# POJO

This chapter introduces how to read data by configuring POJO classes.

## Reading by Column Name or Column Index

### Overview

You can read spreadsheet data by specifying **column names** or **column indexes**. This makes interaction with
dynamically generated spreadsheet files more flexible.

### Example Code

#### POJO Class

```java

@Getter
@Setter
@EqualsAndHashCode
public class IndexOrNameData {
    @ExcelProperty(index = 2)
    private Double doubleData;

    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
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

---

## Cell Objects

### Overview

Use the `CellData` type to receive cell data to support formulas and various cell formats.

### POJO Class

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

### Code Example

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

## Without POJO Classes and Listeners

### Overview

Fesod supports reading spreadsheet files directly without defining POJO classes, using `Map<Integer, String>` to read
data
directly.

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

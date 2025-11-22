---
id: 'simple'
title: 'Simple'
---

# Simple Writing

This chapter introduces how to use Fesod to perform simple spreadsheet writing operations.

## Overview

Use Fesod for simple spreadsheet data writing to quickly write entity objects to spreadsheet files.
This is the most basic and commonly used writing approach.

## Code Examples

### POJO Class

The `DemoData` POJO class corresponding to the spreadsheet structure:

```java

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    @ExcelIgnore
    private String ignore; // Ignored field
}
```

### Data List

```java
private List<DemoData> data() {
    List<DemoData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("String" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}
```

### Writing Methods

Fesod provides multiple writing methods, including `Lambda` expressions, data lists, `ExcelWriter` objects, etc.

#### `Lambda` Expression

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(() -> data());
}
```

#### Data List

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(data());
}
```

#### `ExcelWriter` Object

```java

@Test
public void simpleWrite() {
    String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Sheet1").build();
        excelWriter.write(data(), writeSheet);
    }
}
```

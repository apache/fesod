---
id: 'fill'
title: 'Fill'
---

# Fill

This section explains how to use Fesod to fill data into files.

## Simple Fill

### Overview

Fill data into spreadsheet based on a template file using objects or Map.

### POJO Class

```java

@Getter
@Setter
@EqualsAndHashCode
public class FillData {
    private String name;
    private double number;
    private Date date;
}
```

### Code Example

```java

@Test
public void simpleFill() {
    String templateFileName = "path/to/simple.xlsx";

    // Approach 1: Fill based on object
    FillData fillData = new FillData();
    fillData.setName("张三");
    fillData.setNumber(5.2);
    FesodSheet.write("simpleFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(fillData);

    // Approach 2: Fill based on Map
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三");
    map.put("number", 5.2);
    FesodSheet.write("simpleFillMap.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(map);
}
```

### Template

![img](/img/docs/fill/simpleFill_file.png)

### Result

![img](/img/docs/fill/simpleFill_result.png)

---

## Fill List

### Overview

Fill multiple data items into a template list, supporting in-memory batch operations and file cache batch filling.

### Code Example

```java

@Test
public void listFill() {
    String templateFileName = "path/to/list.xlsx";

    // Approach 1: Fill all data at once
    FesodSheet.write("listFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data());

    // Approach 2: Batch filling
    try (ExcelWriter writer = FesodSheet.write("listFillBatch.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();
        writer.fill(data(), writeSheet);
        writer.fill(data(), writeSheet);
    }
}
```

### Template

![img](/img/docs/fill/listFill_file.png)

### Result

![img](/img/docs/fill/listFill_result.png)

---

## Complex Fill

### Overview

Fill various data types in a template, including lists and regular variables.

### Code Example

```java

@Test
public void complexFill() {
    String templateFileName = "path/to/complex.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Fill list data, with forceNewRow enabled
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        writer.fill(data(), config, writeSheet);

        // Fill regular variables
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        map.put("total", 1000);
        writer.fill(map, writeSheet);
    }
}
```

### Template

![img](/img/docs/fill/complexFill_file.png)

### Result

![img](/img/docs/fill/complexFill_result.png)

---

## Complex Fill with Large Data

### Overview

Optimize performance for filling large data, ensuring the template list is at the last row, and subsequent data is
filled using `WriteTable`.

### Code Example

```java

@Test
public void complexFillWithTable() {
    String templateFileName = "path/to/complexFillWithTable.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFillWithTable.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Fill list data
        writer.fill(data(), writeSheet);

        // Fill list data
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);

        // Fill statistical information
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(Arrays.asList(null, null, null, "统计: 1000"));
        writer.write(totalList, writeSheet);
    }
}
```

### Template

![img](/img/docs/fill/complexFillWithTable_file.png)

### Result

![img](/img/docs/fill/complexFillWithTable_result.png)

---

## Horizontal Fill

### Overview

Fill list data horizontally, suitable for scenarios with dynamic column numbers.

### Code Example

```java

@Test
public void horizontalFill() {
    String templateFileName = "path/to/horizontal.xlsx";

    try (ExcelWriter writer = FesodSheet.write("horizontalFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        FillConfig config = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        writer.fill(data(), config, writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);
    }
}
```

### Template

![img](/img/docs/fill/horizontalFill_file.png)

### Result

![img](/img/docs/fill/horizontalFill_result.png)

---

## Fill Multiple Lists Together

### Overview

Support filling multiple lists simultaneously, with prefixes to differentiate between lists.

### Code Example

```java

@Test
public void compositeFill() {
    String templateFileName = "path/to/composite.xlsx";

    try (ExcelWriter writer = FesodSheet.write("compositeFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // Use FillWrapper for filling multiple lists
        writer.fill(new FillWrapper("data1", data()), writeSheet);
        writer.fill(new FillWrapper("data2", data()), writeSheet);
        writer.fill(new FillWrapper("data3", data()), writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        writer.fill(map, writeSheet);
    }
}
```

### Template

![img](/img/docs/fill/compositeFill_file.png)

### Result

![img](/img/docs/fill/compositeFill_result.png)

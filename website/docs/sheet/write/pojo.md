---
id: 'pojo'
title: 'POJO'
---

# POJO

This chapter introduces how to write data by configuring POJO classes.

## Export Only Specified Columns Based on Parameters

### Overview

Dynamically select columns to export by setting a collection of column names, supporting ignoring columns or exporting
only specific columns.

### Code Examples

Ignore specified columns

```java
@Test
public void excludeOrIncludeWrite() {
    String fileName = "excludeColumnFieldWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> excludeColumns = Set.of("date");
    FesodSheet.write(fileName, DemoData.class)
        .excludeColumnFieldNames(excludeColumns)
        .sheet()
        .doWrite(data());
}
```

Export only specified columns

```java
@Test
public void excludeOrIncludeWrite() {
    String fileName = "includeColumnFiledWrite" + System.currentTimeMillis() + ".xlsx";

    Set<String> includeColumns = Set.of("date");
    FesodSheet.write(fileName, DemoData.class)
        .includeColumnFiledNames(includeColumns)
        .sheet()
        .doWrite(data());
}
```

### Result

![img](/img/docs/write/excludeOrIncludeWrite.png)

---

## Specify Column Order for Writing

### Overview

Specify column order using the `index` attribute of the `@ExcelProperty` annotation.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class IndexData {
    @ExcelProperty(value = "字符串标题", index = 0)
    private String string;
    @ExcelProperty(value = "日期标题", index = 1)
    private Date date;
    @ExcelProperty(value = "数字标题", index = 3)
    private Double doubleData;
}
```

### Code Example

```java
@Test
public void indexWrite() {
    String fileName = "indexWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, IndexData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

![img](/img/docs/write/indexWrite.png)

---

## Writing Without Creating Objects

### Overview

Write data directly using `List<List<String>>` to define headers and data without creating entity classes.

### Code Example

```java
@Test
public void noModelWrite() {
    String fileName = "noModelWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName)
        .head(head()) // Dynamic headers
        .sheet("无对象写入")
        .doWrite(dataList());
}

private List<List<String>> head() {
    return Arrays.asList(
        Collections.singletonList("字符串标题"),
        Collections.singletonList("数字标题"),
        Collections.singletonList("日期标题"));
}

private List<List<Object>> dataList() {
    List<List<Object>> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(Arrays.asList("字符串" + i, 0.56, new Date()));
    }
    return list;
}
```

### Result

![img](/img/docs/write/noModelWrite.png)

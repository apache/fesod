---
id: 'head'
title: 'Head'
---

# Headers

This chapter introduces how to write header data in spreadsheet.

## Complex Header Writing

### Overview

Supports setting multi-level headers by specifying main titles and subtitles through the `@ExcelProperty` annotation.

### POJO Class

```java

@Getter
@Setter
@EqualsAndHashCode
public class ComplexHeadData {
    @ExcelProperty({"主标题", "字符串标题"})
    private String string;
    @ExcelProperty({"主标题", "日期标题"})
    private Date date;
    @ExcelProperty({"主标题", "数字标题"})
    private Double doubleData;
}
```

### Code Example

```java

@Test
public void complexHeadWrite() {
    String fileName = "complexHeadWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ComplexHeadData.class)
            .sheet()
            .doWrite(data());
}
```

### Result

![img](/img/docs/write/complexHeadWrite.png)

---

## Dynamic Header Writing

### Overview

Generate dynamic headers in real-time, suitable for scenarios where header content changes dynamically.

### Code Example

```java

@Test
public void dynamicHeadWrite() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
            Collections.singletonList("动态字符串标题"),
            Collections.singletonList("动态数字标题"),
            Collections.singletonList("动态日期标题"));

    FesodSheet.write(fileName)
            .head(head)
            .sheet()
            .doWrite(data());
}
```

### Result

![img](/img/docs/write/dynamicHeadWrite.png)

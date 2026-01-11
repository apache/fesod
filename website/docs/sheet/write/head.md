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

---

## Header Merge Strategy

### Overview

By default, Fesod automatically merges header cells with the same name. However, you can control the merge behavior using the `headerMergeStrategy` parameter.

### Merge Strategies

- **NONE**: No automatic merging is performed.
- **HORIZONTAL_ONLY**: Only merges cells horizontally (same row).
- **VERTICAL_ONLY**: Only merges cells vertically (same column).
- **FULL_RECTANGLE**: Only merges complete rectangular regions where all cells have the same name.
- **AUTO**: Automatic merging (default).

### Code Example

```java
@Test
public void dynamicHeadWriteWithStrategy() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
        Collections.singletonList("动态字符串标题"),
        Collections.singletonList("动态数字标题"),
        Collections.singletonList("动态日期标题"));

    FesodSheet.write(fileName)
        .head(head)
        .headerMergeStrategy(HeaderMergeStrategy.FULL_RECTANGLE)
        .sheet()
        .doWrite(data());
}
```

### Common Use Cases

**Disable merging**: Use `NONE` to completely disable automatic merging:

```java
FesodSheet.write(fileName)
    .head(head)
    .headerMergeStrategy(HeaderMergeStrategy.NONE)
    .sheet()
    .doWrite(data());
```

**Note**: The old `automaticMergeHead` parameter is still supported for backward compatibility. When `headerMergeStrategy` is not set, the behavior is determined by `automaticMergeHead`.

---
id: 'extra'
title: 'Extra'
---

# Extra Information

This chapter introduces how to read extra information such as comments, hyperlinks, merged cells, etc.

## Comments

### Overview

Read extra comment information.

### Data Listener

```java
@Slf4j
public class DemoCommentExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("Read extra information: {}", JSON.toJSONString(extra));
        if(CellExtraTypeEnum.COMMENT == extra.getType()) {
            log.info("Comment information: {}", extra.getText());
        }
    }
}
```

### Code Example

```java
@Test
public void extraRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoCommentExtraListener())
            .extraRead(CellExtraTypeEnum.COMMENT)
            .sheet()
            .doRead();
}
```

---

## Hyperlinks

### Overview

Read extra hyperlink information.

### Data Listener

```java
@Slf4j
public class DemoHyperLinkExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("Read extra information: {}", JSON.toJSONString(extra));
        if(CellExtraTypeEnum.HYPERLINK == extra.getType()) {
            log.info("Hyperlink information: {}", extra.getText());
        }
    }
}
```

### Code Example

```java
@Test
public void extraRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoHyperLinkExtraListener())
            .extraRead(CellExtraTypeEnum.HYPERLINK)
            .sheet()
            .doRead();
}
```

---

## Merged Cells

### Overview

Read merged cell range information.

### Data Listener

```java
@Slf4j
public class DemoMergeExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("Read extra information: {}", JSON.toJSONString(extra));
        if(CellExtraTypeEnum.MERGE == extra.getType()) {
            log.info("Merged cell range: {} - {}", extra.getFirstRowIndex(), extra.getLastRowIndex());
        }
    }
}
```

### Code Example

```java
@Test
public void extraRead() {
    String fileName = "path/to/demo.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoMergeExtraListener())
            .extraRead(CellExtraTypeEnum.MERGE)
            .sheet()
            .doRead();
}
```

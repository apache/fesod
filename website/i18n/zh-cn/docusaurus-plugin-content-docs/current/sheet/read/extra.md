---
id: 'extra'
title: '额外信息'
---

# 额外信息

本章节将介绍如何读取额外的信息，如批注、超链接、合并单元格等。

## 批注

### 概述

读取额外的批注信息。

### 数据监听器

```java

@Slf4j
public class DemoCommentExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("读取到额外信息: {}", JSON.toJSONString(extra));
        if (CellExtraTypeEnum.COMMENT == extra.getType()) {
            log.info("批注信息: {}", extra.getText());
        }
    }
}
```

### 代码示例

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

## 超链接

### 概述

读取额外的超链接信息。

### 数据监听器

```java

@Slf4j
public class DemoHyperLinkExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("读取到额外信息: {}", JSON.toJSONString(extra));
        if (CellExtraTypeEnum.HYPERLINK == extra.getType()) {
            log.info("超链接信息: {}", extra.getText());
        }
    }
}
```

### 代码示例

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

## 合并单元格

### 概述

读取合并单元格范围信息。

### 数据监听器

```java

@Slf4j
public class DemoMergeExtraListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("读取到额外信息: {}", JSON.toJSONString(extra));
        if (CellExtraTypeEnum.MERGE == extra.getType()) {
            log.info("合并单元格范围: {} - {}", extra.getFirstRowIndex(), extra.getLastRowIndex());
        }
    }
}
```

### 代码示例

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

---
id: 'merge'
title: 'Merge'
---

<!--
- Licensed to the Apache Software Foundation (ASF) under one or more
- contributor license agreements.  See the NOTICE file distributed with
- this work for additional information regarding copyright ownership.
- The ASF licenses this file to You under the Apache License, Version 2.0
- (the "License"); you may not use this file except in compliance with
- the License.  You may obtain a copy of the License at
-
-   http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-->

# Cell Merging

This chapter introduces cell merging strategies when writing Excel files.

## Overview

When generating Excel reports, you may need to merge cells — for example, merging repeated category names in a column, or creating summary rows. Fesod supports two approaches: annotation-based and strategy-based merging.

## Annotation-Based Merging

### Overview

Annotate a field with `@ContentLoopMerge(eachRow = N)` to automatically merge every N rows in that column.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoMergeData {
    @ContentLoopMerge(eachRow = 2)
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ExcelProperty("Number Title")
    private Double doubleData;
}
```

### Code Example

```java
@Test
public void annotationMergeWrite() {
    String fileName = "annotationMergeWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, DemoMergeData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

```text
| String Title | Date Title          | Number Title |
|--------------|---------------------|--------------|
| String0      | 2025-01-01 00:00:00 | 0.56         |
| (merged)     | 2025-01-01 00:00:00 | 0.56         |
| String1      | 2025-01-01 00:00:00 | 0.56         |
| (merged)     | 2025-01-01 00:00:00 | 0.56         |
```

---

## Strategy-Based Merging

### Overview

Register a `LoopMergeStrategy(eachRow, columnIndex)` as a write handler. More flexible — can be configured at runtime and applied to any column.

### Code Example

```java
@Test
public void strategyMergeWrite() {
    String fileName = "strategyMergeWrite" + System.currentTimeMillis() + ".xlsx";

    // Merge every 2 rows in column 0
    LoopMergeStrategy strategy = new LoopMergeStrategy(2, 0);
    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(strategy)
        .sheet()
        .doWrite(data());
}
```

---

## Custom Merge Strategy

### Overview

For complex merging logic (e.g., merge based on data content), implement the `AbstractMergeStrategy` class.

### Code Example

```java
public class CustomMergeStrategy extends AbstractMergeStrategy {
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (relativeRowIndex != null && relativeRowIndex % 2 == 0 && head.getColumnIndex() == 0) {
            // Note: +1 assumes a single header row. For multi-row headers, use cell.getRowIndex() instead.
            int startRow = relativeRowIndex + 1;
            int endRow = startRow + 1;
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
        }
    }
}
```

Usage:

```java
@Test
public void customMergeWrite() {
    String fileName = "customMergeWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomMergeStrategy())
        .sheet()
        .doWrite(data());
}
```

---

## When to Use Which

| Approach | Best For |
|:---|:---|
| `@ContentLoopMerge` | Simple, fixed merge patterns baked into the data class |
| `LoopMergeStrategy` | Dynamic merging, runtime configuration |
| `AbstractMergeStrategy` | Complex merging based on data content or custom rules |

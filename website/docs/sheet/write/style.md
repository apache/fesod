---
id: 'style'
title: 'Style'
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

# Style

This chapter introduces style settings when writing data.

## Annotations

### Overview

Set cell styles through annotations in entity classes, including font, background color, row height, etc.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
// Set header background to red
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
// Set header font size to 20
@HeadFontStyle(fontHeightInPoints = 20)
// Set content background to green
@ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 17)
// Set content font size to 20
@ContentFontStyle(fontHeightInPoints = 20)
public class DemoStyleData {
    // Individually set header and content styles for a specific column
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 14)
    @HeadFontStyle(fontHeightInPoints = 30)
    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    @ContentFontStyle(fontHeightInPoints = 30)
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
public void annotationStyleWrite() {
    String fileName = "annotationStyleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoStyleData.class)
            .sheet()
            .doWrite(data());
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head xl-fill-magenta xl-fs-30">String Title</td><td class="xl-head xl-fill-red xl-fs-20">Date Title</td><td class="xl-head xl-fill-red xl-fs-20">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-fill-sky xl-fs-30">String0</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-fill-sky xl-fs-30">String1</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fill-sky xl-fs-30">String2</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-fill-sky xl-fs-30 xl-muted">…</td><td class="xl-fill-green xl-fs-20 xl-muted">…</td><td class="xl-fill-green xl-fs-20 xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fill-sky xl-fs-30">String9</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
</tbody>
</table>
</div>

---

## Built-in Interceptors

### Overview

Use `HorizontalCellStyleStrategy` to set styles for headers and content separately.

### Code Example

```java
@Test
public void handlerStyleWrite() {
    String fileName = "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";

    // Define header style
    WriteCellStyle headStyle = new WriteCellStyle();
    headStyle.setFillForegroundColor(IndexedColors.RED.getIndex()); // Red background
    WriteFont headFont = new WriteFont();
    headFont.setFontHeightInPoints((short) 20); // Font size 20
    headStyle.setWriteFont(headFont);

    // Define content style
    WriteCellStyle contentStyle = new WriteCellStyle();
    contentStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex()); // Green background
    contentStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
    WriteFont contentFont = new WriteFont();
    contentFont.setFontHeightInPoints((short) 20);
    contentStyle.setWriteFont(contentFont);

    // Use strategy to set styles
    HorizontalCellStyleStrategy styleStrategy =
        new HorizontalCellStyleStrategy(headStyle, contentStyle);

    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(styleStrategy)
        .sheet("Style Template")
        .doWrite(data());
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head xl-fill-red xl-fs-20">String Title</td><td class="xl-head xl-fill-red xl-fs-20">Date Title</td><td class="xl-head xl-fill-red xl-fs-20">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-fill-green xl-fs-20">String0</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-fill-green xl-fs-20">String1</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fill-green xl-fs-20">String2</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-fill-green xl-fs-20 xl-muted">…</td><td class="xl-fill-green xl-fs-20 xl-muted">…</td><td class="xl-fill-green xl-fs-20 xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fill-green xl-fs-20">String9</td><td class="xl-num xl-fill-green xl-fs-20">2026-07-31 20:50:23</td><td class="xl-num xl-fill-green xl-fs-20">0.56</td></tr>
</tbody>
</table>
</div>

---

## Custom Interceptors

### Overview

If existing strategies cannot meet requirements, you can implement the `CellWriteHandler` interface for complete custom
control over styling.

### Code Example

Custom interceptor

```java
@Slf4j
public class CustomCellStyleWriteHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // Only set styles for content cells
        if (BooleanUtils.isNotTrue(context.getHead())) {
            WriteCellData<?> cellData = context.getFirstCellData();
            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();

            // Set background color to yellow
            writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

            // Set font to blue
            WriteFont writeFont = new WriteFont();
            writeFont.setColor(IndexedColors.BLUE.getIndex());
            writeFont.setFontHeightInPoints((short) 14); // Font size 14
            writeCellStyle.setWriteFont(writeFont);

            log.info("Custom cell style applied: row {}, column {}", context.getRowIndex(), context.getColumnIndex());
        }
    }
}
```

Usage

```java
@Test
public void customCellStyleWrite() {
    String fileName = "customCellStyleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomCellStyleWriteHandler())
        .sheet("Custom Style")
        .doWrite(data());
}
```

---

## Custom POI Styles

### Overview

Directly manipulate POI's `CellStyle`, suitable for precise style control.

### Code Example

```java
@Test
public void poiStyleWrite() {
    String fileName = "poiStyleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                if (BooleanUtils.isNotTrue(context.getHead())) {
                    Cell cell = context.getCell();
                    Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();

                    // Create and set style
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                }
            }
        })
        .sheet("POI Style")
        .doWrite(data());
}
```

---

## Column Width and Row Height

### Overview

Control column width and row height through annotations, suitable for scenarios with specific table format requirements.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(20)
@HeadRowHeight(30)
@ColumnWidth(25) // Default column width
public class WidthAndHeightData {
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ColumnWidth(50) // Individually set column width
    @ExcelProperty("Number Title")
    private Double doubleData;
}
```

### Code Example

```java
@Test
public void widthAndHeightWrite() {
    String fileName = "widthAndHeightWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, WidthAndHeightData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td><td class="xl-chrome xl-cw-25">B</td><td class="xl-chrome xl-cw-50">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head xl-rh-30">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-rh-20">String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-rh-20">String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-rh-20">String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-rh-20 xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-rh-20">String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

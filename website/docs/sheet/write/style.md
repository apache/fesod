---
id: 'style'
title: 'Style'
---

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
    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ExcelProperty("数字标题")
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

![img](/img/docs/write/annotationStyleWrite.png)

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
        .sheet("样式模板")
        .doWrite(data());
}
```

### Result

![img](/img/docs/write/handlerStyleWrite.png)

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

            log.info("已自定义单元格样式: 行 {}, 列 {}", context.getRowIndex(), context.getColumnIndex());
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
        .sheet("自定义样式")
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
        .sheet("POI样式")
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
    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ColumnWidth(50) // Individually set column width
    @ExcelProperty("数字标题")
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

![img](/img/docs/write/widthAndHeightWrite.png)

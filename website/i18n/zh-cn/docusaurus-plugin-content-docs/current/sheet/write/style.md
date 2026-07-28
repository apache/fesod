---
id: 'style'
title: '样式'
---

# 样式

本章节将介绍写入数据时的样式设置。

## 注解

### 概述

通过实体类中的注解设置单元格样式，包括字体、背景颜色、行高等。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
// 设置头部背景为红色
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
// 设置头部字体大小为20
@HeadFontStyle(fontHeightInPoints = 20)
// 设置内容背景为绿色
@ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 17)
// 设置内容字体大小为20
@ContentFontStyle(fontHeightInPoints = 20)
public class DemoStyleData {
    // 单独设置某列的头部和内容样式
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

### 代码示例

```java

@Test
public void annotationStyleWrite() {
    String fileName = "annotationStyleWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoStyleData.class)
            .sheet()
            .doWrite(data());
}
```

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head xl-fill-14 xl-pt30">字符串标题</td><td class="xl-head xl-fill-10 xl-pt20">日期标题</td><td class="xl-head xl-fill-10 xl-pt20">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-fill-40 xl-pt30">字符串0</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-fill-40 xl-pt30">字符串1</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fill-40 xl-pt30">字符串2</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fill-40 xl-pt30">字符串3</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fill-40 xl-pt30">字符串4</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-fill-40 xl-pt30">字符串5</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-fill-40 xl-pt30">字符串6</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fill-40 xl-pt30">字符串7</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-fill-40 xl-pt30">字符串8</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fill-40 xl-pt30">字符串9</td><td class="xl-num xl-fill-17 xl-pt20">2024-12-03 20:50:23</td><td class="xl-num xl-fill-17 xl-pt20">0.56</td></tr>
</tbody>
</table>

---

## 内置拦截器

### 概述

通过 `HorizontalCellStyleStrategy` 为表头和内容分别设置样式。

### 代码示例

```java

@Test
public void handlerStyleWrite() {
    String fileName = "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";

    // 定义表头样式
    WriteCellStyle headStyle = new WriteCellStyle();
    headStyle.setFillForegroundColor(IndexedColors.RED.getIndex()); // 红色背景
    WriteFont headFont = new WriteFont();
    headFont.setFontHeightInPoints((short) 20); // 字体大小为20
    headStyle.setWriteFont(headFont);

    // 定义内容样式
    WriteCellStyle contentStyle = new WriteCellStyle();
    contentStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex()); // 绿色背景
    contentStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
    WriteFont contentFont = new WriteFont();
    contentFont.setFontHeightInPoints((short) 20);
    contentStyle.setWriteFont(contentFont);

    // 使用策略设置样式
    HorizontalCellStyleStrategy styleStrategy =
            new HorizontalCellStyleStrategy(headStyle, contentStyle);

    FesodSheet.write(fileName, DemoData.class)
            .registerWriteHandler(styleStrategy)
            .sheet("样式模板")
            .doWrite(data());
}
```

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-fill-10">字符串0</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-fill-10">字符串1</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-fill-10">字符串2</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-fill-10">字符串3</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-fill-10">字符串4</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-fill-10">字符串5</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-fill-10">字符串6</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-fill-10">字符串7</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-fill-10">字符串8</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-fill-10">字符串9</td><td class="xl-num xl-fill-10">45629.90044</td><td class="xl-num xl-fill-10">0.56</td></tr>
</tbody>
</table>

---

## 自定义拦截器

### 概述

如果已有策略无法满足需求，可以实现 `CellWriteHandler` 接口对样式进行完全自定义控制。

### 代码示例

自定义拦截器

```java
@Slf4j
public class CustomCellStyleWriteHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 仅设置内容单元格的样式
        if (BooleanUtils.isNotTrue(context.getHead())) {
            WriteCellData<?> cellData = context.getFirstCellData();
            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();

            // 设置背景颜色为黄色
            writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

            // 设置字体为蓝色
            WriteFont writeFont = new WriteFont();
            writeFont.setColor(IndexedColors.BLUE.getIndex());
            writeFont.setFontHeightInPoints((short) 14); // 字体大小为14
            writeCellStyle.setWriteFont(writeFont);

            log.info("已自定义单元格样式: 行 {}, 列 {}", context.getRowIndex(), context.getColumnIndex());
        }
    }
}
```

使用

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

## 自定义 POI 样式

### 概述

直接操作 POI 的 `CellStyle`，适合对样式精确控制。

### 代码示例

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

                        // 创建并设置样式
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

## 列宽和行高

### 概述

通过注解控制列宽、行高，适用于对表格格式有特定要求的场景。

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(20)
@HeadRowHeight(30)
@ColumnWidth(25) // 默认列宽
public class WidthAndHeightData {
    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ColumnWidth(50) // 单独设置列宽
    @ExcelProperty("数字标题")
    private Double doubleData;
}
```

### 代码示例

```java

@Test
public void widthAndHeightWrite() {
    String fileName = "widthAndHeightWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, WidthAndHeightData.class)
            .sheet()
            .doWrite(data());
}
```

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td><td class="xl-chrome xl-cw-25">B</td><td class="xl-chrome xl-cw-50">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head xl-rh-30">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-rh-20">字符串0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-rh-20">字符串1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-rh-20">字符串2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-rh-20">字符串3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-rh-20">字符串4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-rh-20">字符串5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-rh-20">字符串6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-rh-20">字符串7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-rh-20">字符串8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-rh-20">字符串9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

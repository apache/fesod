---
id: 'extra'
title: '额外信息'
---

# 额外信息

本章节将介绍如何写入额外的信息，如批注、超链接、公式、合并单元格等。

## 批注

### 概述

通过拦截器在特定单元格添加批注，适用于标注说明或特殊提示。

### 代码示例

自定义拦截器

```java
@Slf4j
public class CommentWriteHandler implements RowWriteHandler {

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (BooleanUtils.isTrue(context.getHead())) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // 在第一行第二列创建批注
            Comment comment = drawingPatriarch.createCellComment(
                new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
            comment.setString(new XSSFRichTextString("批注内容"));
            sheet.getRow(0).getCell(1).setCellComment(comment);
        }
    }
}
```

使用

```java
@Test
public void commentWrite() {
    String fileName = "commentWrite" + System.currentTimeMillis() + ".xlsx";

    FastExcel.write(fileName, DemoData.class)
        .inMemory(Boolean.TRUE) // 批注必须启用内存模式
        .registerWriteHandler(new CommentWriteHandler())
        .sheet("批注示例")
        .doWrite(data());
}
```

### 结果

![img](/img/docs/write/commentWrite.png)

---

## 超链接

写入额外的超链接信息

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class WriteCellDemoData {
    private WriteCellData<String> hyperlink;
}
```

### 代码示例

```java
@Test
public void writeHyperlinkDataWrite() {
    String fileName = "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellDemoData data = new WriteCellDemoData();
    // 设置超链接
    data.setHyperlink(new WriteCellData<>("点击访问").hyperlink("https://example.com"));

    FastExcel.write(fileName, WriteCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### 结果

![img](/img/docs/write/writeCellDataWrite.png)

---

## 公式

写入额外的公式信息

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class WriteCellDemoData {
    private WriteCellData<String> formulaData;
}
```

### 代码示例

```java
@Test
public void writeFormulaDataWrite() {
    String fileName = "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellDemoData data = new WriteCellDemoData();
    // 设置公式
    data.setFormulaData(new WriteCellData<>("=SUM(A1:A10)"));

    FastExcel.write(fileName, WriteCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### 结果

![img](/img/docs/write/writeCellDataWrite.png)

---

## 根据模板写入

### 概述

支持使用已有的模板文件，在模板上填充数据，适用于规范化输出。

### 代码示例

```java
@Test
public void templateWrite() {
    String templateFileName = "path/to/template.xlsx";
    String fileName = "templateWrite" + System.currentTimeMillis() + ".xlsx";

    FastExcel.write(fileName, DemoData.class)
        .withTemplate(templateFileName)
        .sheet()
        .doWrite(data());
}
```

---

## 合并单元格

### 概述

支持通过注解或自定义合并策略实现合并单元格。

### 代码示例

注解方式

```java
@Getter
@Setter
@EqualsAndHashCode
public class DemoMergeData {
    @ContentLoopMerge(eachRow = 2) // 每隔 2 行合并一次
    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ExcelProperty("数字标题")
    private Double doubleData;
}
```

自定义合并策略

```java
public class CustomMergeStrategy extends AbstractMergeStrategy {
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // merge方法会为每个单元格都调用一次，确保相同单元格只执行一次合并
        if (relativeRowIndex != null && relativeRowIndex % 2 == 0 && head.getColumnIndex() == 0) {
            int startRow = relativeRowIndex + 1; // 第0行是表头，数据从第1行开始
            int endRow = startRow + 1; // 合并当前行和下一行
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
        }
    }
}
```

使用

```java
@Test
public void mergeWrite() {
    String fileName = "mergeWrite" + System.currentTimeMillis() + ".xlsx";

    // 注解方式
    FastExcel.write(fileName, DemoMergeData.class)
        .sheet("合并示例")
        .doWrite(data());

    // 自定义合并策略
    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomMergeStrategy())
        .sheet("自定义合并")
        .doWrite(data());
}
```

### 结果

![img](/img/docs/write/mergeWrite.png)

---

## 自定义拦截器

### 概述

实现自定义逻辑（如添加下拉框等）需要通过拦截器操作。

### 代码示例

设置下拉框

```java
public class DropdownWriteHandler implements SheetWriteHandler {
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        CellRangeAddressList range = new CellRangeAddressList(1, 10, 0, 0); // 下拉框区域
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[] {"选项1", "选项2"});
        DataValidation validation = helper.createValidation(constraint, range);
        context.getWriteSheetHolder().getSheet().addValidationData(validation);
    }
}
```

### 结果

![img](/img/docs/write/customHandlerWrite.png)

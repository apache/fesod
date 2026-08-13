---
id: 'extra'
title: '额外信息'
---

# 额外信息

本章节将介绍如何写入额外的信息，如批注、超链接、公式等。

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
            comment.setString(new XSSFRichTextString("批注1"));
            sheet.getRow(0).getCell(1).setCellComment(comment);
        }
    }
}
```

使用

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, DemoData.class)
        .inMemory(Boolean.TRUE) // 批注必须启用内存模式
        .registerWriteHandler(new CommentWriteHandler())
        .sheet()
        .doWrite(data());
}
```

### 结果

在第一行第二列单元格上出现批注信息。

<div class="xl-sheet-container">
<table class="xl-sheet xl-sheet--overlay">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head xl-comment-anchor">日期标题<b class="xl-comment">批注1</b></td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td>字符串0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

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
void main() {
    String pathname = "path/to/demo.xlsx";

    WriteCellDemoData data = new WriteCellDemoData();
    // 设置超链接
    WriteCellData cellData = new WriteCellData<>("点击访问");
    HyperlinkData hyperlinkData = new HyperlinkData();
    hyperlinkData.setAddress("https://example.com");
    hyperlinkData.setHyperlinkType(HyperlinkData.HyperlinkType.URL);
    cellData.setHyperlinkData(hyperlinkData);
    data.setHyperlink(cellData);

    FesodSheet.write(pathname, WriteCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">hyperlink</td></tr>
<tr><td class="xl-chrome">2</td><td><a href="https://example.com">点击访问</a></td></tr>
</tbody>
</table>
</div>

---

## 公式

写入额外的公式信息

### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
public class WriteCellDemoData {
    private Integer num1;
    private Integer num2;
    private WriteCellData<String> formulaData;
}
```

### 代码示例

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    WriteCellDemoData data = new WriteCellDemoData();
    data.setNum1(10);
    data.setNum2(20);
    // 设置公式
    WriteCellData<String> cellData = new WriteCellData<>();
    FormulaData formulaData = new FormulaData();
    formulaData.setFormulaValue("SUM(A2:B2)");
    // 或
    // formulaData.setFormulaValue("=SUM(A2:B2)");
    cellData.setFormulaData(formulaData);
    data.setFormulaData(cellData);

    FesodSheet.write(pathname, WriteCellDemoData.class)
            .sheet()
            .doWrite(Collections.singletonList(data));
}
```

### 结果

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">num1</td><td class="xl-head">num2</td><td class="xl-head">formulaData</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-num">10</td><td class="xl-num">20</td><td class="xl-num">30</td></tr>
</tbody>
</table>
</div>

---

## 根据模板写入

### 概述

支持使用已有的模板文件，在模板上填充数据，适用于规范化输出。

### 代码示例

```java
void main() {
    String template = "path/from/demo.xlsx";
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, DemoData.class)
        .withTemplate(template)
        .sheet()
        .doWrite(data());
}
```

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

使用

```java
void main() {
    String pathname = "path/to/demo.xlsx";

    FesodSheet.write(pathname, DemoData.class)
        .registerWriteHandler(new DropdownWriteHandler())
        .sheet("下拉框示例")
        .doWrite(data());
}
```

### 结果

校验范围是 `A2:A11`，该区域内的每个单元格都可以选择下拉框中的值。

<div class="xl-sheet-container">
<table class="xl-sheet xl-sheet--overlay">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">字符串标题</td><td class="xl-head">日期标题</td><td class="xl-head">数字标题</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-dropdown">字符串0<b class="xl-dropdown-btn">▾</b><b class="xl-dropdown-list"><b>选项1</b><b>选项2</b></b></td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>字符串1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>字符串2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>字符串9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

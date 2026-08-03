---
id: 'extra'
title: 'Extra'
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

# Extra Information

This chapter introduces how to write extra information such as comments, hyperlinks, formulas, etc.

## Comments

### Overview

Add comments to specific cells through interceptors, suitable for annotations or special reminders.

### Code Example

Custom interceptor

```java
@Slf4j
public class CommentWriteHandler implements RowWriteHandler {

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (BooleanUtils.isTrue(context.getHead())) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // Create comment in first row, second column
            Comment comment = drawingPatriarch.createCellComment(
                new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
            comment.setString(new XSSFRichTextString("Comments"));
            sheet.getRow(0).getCell(1).setCellComment(comment);
        }
    }
}
```

Usage

```java
@Test
public void commentWrite() {
    String fileName = "commentWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .inMemory(Boolean.TRUE) // Comments must enable in-memory mode
        .registerWriteHandler(new CommentWriteHandler())
        .sheet()
        .doWrite(data());
}
```

### Result

The comment is attached to `B1` and is only shown when that cell is hovered.

<div class="xl-sheet-container">
<table class="xl-sheet xl-sheet--overlay">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head xl-comment-anchor">Date Title<b class="xl-comment">Comments</b></td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

---

## Hyperlinks

Write extra hyperlink information

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class WriteCellDemoData {
    private WriteCellData<String> hyperlink;
}
```

### Code Example

```java
@Test
public void writeHyperlinkDataWrite() {
    String fileName = "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellDemoData data = new WriteCellDemoData();
    // Set hyperlink
    WriteCellData cellData = new WriteCellData<>("Click to visit");
    HyperlinkData hyperlinkData = new HyperlinkData();
    hyperlinkData.setAddress("https://example.com");
    hyperlinkData.setHyperlinkType(HyperlinkData.HyperlinkType.URL);
    cellData.setHyperlinkData(hyperlinkData);
    data.setHyperlink(cellData);

    FesodSheet.write(fileName, WriteCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">hyperlink</td></tr>
<tr><td class="xl-chrome">2</td><td><a href="https://example.com">Click to visit</a></td></tr>
</tbody>
</table>
</div>

---

## Formulas

Write extra formula information

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class WriteCellDemoData {
    private WriteCellData<String> formulaData;
}
```

### Code Example

```java
@Test
public void writeFormulaDataWrite() {
    String fileName = "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellDemoData data = new WriteCellDemoData();
    // Set formula
    WriteCellData<String> cellData = new WriteCellData<>();
    FormulaData formulaData = new FormulaData();
    formulaData.setFormulaValue("SUM(A1:A10)");
    // Or
    // formulaData.setFormulaValue("=SUM(A1:A10)");
    cellData.setFormulaData(formulaData);
    data.setFormulaData(cellData);

    FesodSheet.write(fileName, WriteCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### Result

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">formulaData</td></tr>
<tr><td class="xl-chrome">2</td><td>=SUM(A1:A10)</td></tr>
</tbody>
</table>
</div>

---

## Template-based Writing

### Overview

Supports using existing template files and filling data into templates, suitable for standardized output.

### Code Example

```java
@Test
public void templateWrite() {
    String templateFileName = "path/to/template.xlsx";
    String fileName = "templateWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .withTemplate(templateFileName)
        .sheet()
        .doWrite(data());
}
```

---

## Custom Interceptors

### Overview

Implement custom logic (such as adding dropdowns) through interceptor operations.

### Code Example

Setting dropdowns

```java
public class DropdownWriteHandler implements SheetWriteHandler {
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        CellRangeAddressList range = new CellRangeAddressList(1, 10, 0, 0); // Dropdown area
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[] {"Option1", "Option2"});
        DataValidation validation = helper.createValidation(constraint, range);
        context.getWriteSheetHolder().getSheet().addValidationData(validation);
    }
}
```

Usage

```java
@Test
public void dropdownWrite() {
    String fileName = "dropdownWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName, DemoData.class)
        .registerWriteHandler(new DropdownWriteHandler())
        .sheet("Dropdown Example")
        .doWrite(data());
}
```

### Result

The validation covers `A2:A11`, so every cell in that range offers the list. Selecting one shows the
dropdown button and its options - drawn open here on `A2`.

<div class="xl-sheet-container">
<table class="xl-sheet xl-sheet--overlay">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-dropdown">String0<b class="xl-dropdown-btn">▾</b><b class="xl-dropdown-list"><b>Option1</b><b>Option2</b></b></td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

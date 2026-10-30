---
id: 'annotation'
title: 'Annotation'
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

# Annotation

This section provides an overview of the core annotations available in FesodSheet, including their configuration options, usage, and support for composed meta-annotations.

## Entity Class Annotations

Entity classes are the foundation of read and write operations. FesodSheet provides various annotations to help
developers easily define fields and formats.

### `@ExcelProperty`

Defines the column name in spreadsheet and the field name to map. Specific parameters are as follows:

| Name      | Default Value          | Description                                                                                                                                                                                                                                                                                           |
|-----------|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Empty                  | Used to match the header in spreadsheet, must be fully matched. If there are multiple header rows, it will match the last row header.                                                                                                                                                                 |
| order     | Integer.MAX_VALUE      | Higher priority than `value`, will match the order of entities and data in spreadsheet according to the order of `order`.                                                                                                                                                                             |
| index     | -1                     | Higher priority than `value` and `order`, will directly specify which column in spreadsheet to match based on `index`.                                                                                                                                                                                |
| converter | Automatically selected | Specifies which converter the current field uses. By default, it will be automatically selected. <br> For reading, as long as the `org.apache.fesod.sheet.converters.Converter#convertToJavaData(org.apache.fesod.sheet.converters.ReadConverterContext<?>)` method is implemented, it is sufficient. |

### `@ExcelIgnore`

By default, all fields will match spreadsheet. Adding this annotation will ignore the field.

### `@ExcelIgnoreUnannotated`

By default, all properties without the `@ExcelProperty` annotation are involved in read/write operations. Properties
with this annotation are not involved in read/write operations.

### `@DateTimeFormat`

Date conversion: When using `String` to receive data in spreadsheet date format, this annotation will be called. The
parameters are as follows:

| Name             | Default Value          | Description                                                                                                                                                                                              |
|------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| value            | Empty                  | Refer to `java.text.SimpleDateFormat` .                                                                                                                                                                  |
| use1904windowing | Automatically selected | In spreadsheet, time is stored as a double-precision floating-point number starting from 1900, but sometimes the default start date is 1904, so set this value to change the default start date to 1904. |

### `@NumberFormat`

Number conversion, using `String` to receive data in spreadsheet number format will trigger this annotation.

| Name         | Default Value        | Description                           |
|--------------|----------------------|---------------------------------------|
| value        | Empty                | Refer to `java.text.DecimalFormat`.   |
| roundingMode | RoundingMode.HALF_UP | Set the rounding mode when formatting |

### `@ColumnWidth`

Specifies the column width.

### `@HeadRowHeight` & `@ContentRowHeight`

Specifies the height of the header rows (`@HeadRowHeight`) or the content rows (`@ContentRowHeight`).  The
parameters are as follows:

| Name  | Default Value | Description                                              |
|-------|---------------|----------------------------------------------------------|
| value | -1            | Set the height. `-1` indicates automatic height setting. |

### `@HeadFontStyle` & `@ContentFontStyle`

Customizes the font style for headers (`@HeadFontStyle`) or content data (`@ContentFontStyle`).  The
parameters are as follows:

| Name               | Default Value       | Description                                                                                                                                          |
|--------------------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| fontName           | Empty               | The name for the font (e.g., "Arial").                                                                                                               |
| fontHeightInPoints | -1                  | Set font height.                                                                                                                                     |
| italic             | BooleanEnum.DEFAULT | Whether to use italics.                                                                                                                              |
| strikeout          | BooleanEnum.DEFAULT | Whether to use a strikeout horizontal line through the text.                                                                                         |
| color              | -1                  | The color for the font. (See `org.apache.poi.ss.usermodel.IndexedColors` or `org.apache.poi.ss.usermodel.Font`, e.g., `Font.COLOR_NORMAL`).          |
| typeOffset         | -1                  | Set the font type offset to normal, super, or subscript (See `org.apache.poi.ss.usermodel.Font`, e.g., `Font.SS_NONE`).                              |
| underline          | -1                  | Set type of text underlining (See `org.apache.poi.ss.usermodel.Font`, e.g., `Font.U_SINGLE`).                                                        |
| charset            | -1                  | Set character-set to use (See `org.apache.poi.common.usermodel.fonts.FontCharset` or `org.apache.poi.ss.usermodel.Font`, e.g., `Font.ANSI_CHARSET`). |
| bold               | BooleanEnum.DEFAULT | Whether to apply bold style.                                                                                                                         |

### `@HeadStyle` & `@ContentStyle`

Customizes the cell style (borders, alignment, colors...) for header data (`@HeadStyle`) or content data (`@ContentStyle`).
The parameters are as follows:

| Name                | Default Value                   | Description                                                                                           |
|---------------------|---------------------------------|-------------------------------------------------------------------------------------------------------|
| dataFormat          | -1                              | Set the data format (must be a valid format defined at `org.apache.poi.ss.usermodel.BuiltinFormats`). |
| hidden              | BooleanEnum.DEFAULT             | set the cell to be hidden. **Note: This only takes effect if the sheet is protected.**                |
| locked              | BooleanEnum.DEFAULT             | Set the cell to be locked. **Note: This only takes effect if the sheet is protected.**                |
| quotePrefix         | BooleanEnum.DEFAULT             | Turn on/off "Quote Prefix" (treats numeric/formula as text).                                          |
| horizontalAlignment | HorizontalAlignmentEnum.DEFAULT | Set the horizontal alignment.                                                                         |
| wrapped             | BooleanEnum.DEFAULT             | Set whether the text should be wrapped within the cell.                                               |
| verticalAlignment   | VerticalAlignmentEnum.DEFAULT   | Set the vertical alignment.                                                                           |
| rotation            | -1                              | Set the degree of rotation for the text.                                                              |
| indent              | -1                              | Set the number of spaces to indent the text.                                                          |
| borderLeft          | BorderStyleEnum.DEFAULT         | Set the border style for the left border.                                                             |
| borderRight         | BorderStyleEnum.DEFAULT         | Set the border style for the right border.                                                            |
| borderTop           | BorderStyleEnum.DEFAULT         | Set the border style for the top border.                                                              |
| borderBottom        | BorderStyleEnum.DEFAULT         | Set the border style for the bottom border.                                                           |
| leftBorderColor     | -1                              | Set the color for the left border (See `org.apache.poi.ss.usermodel.IndexedColors`).                  |
| rightBorderColor    | -1                              | Set the color for the right border (See `org.apache.poi.ss.usermodel.IndexedColors`).                 |
| topBorderColor      | -1                              | Set the color for the top border (See `org.apache.poi.ss.usermodel.IndexedColors`).                   |
| bottomBorderColor   | -1                              | Set the color for the bottom border (See `org.apache.poi.ss.usermodel.IndexedColors`).                |
| fillPatternType     | FillPatternTypeEnum.DEFAULT     | Set the fill pattern                                                                                  |
| fillBackgroundColor | -1                              | Set the background fill color.                                                                        |
| fillForegroundColor | -1                              | Set the foreground fill color. **Note: Ensure Foreground color is set prior to background color.**    |
| shrinkToFit         | BooleanEnum.DEFAULT             | Controls if the Cell should be auto-sized to shrink to fit if text is too long.                       |

### `@ContentLoopMerge`

Defines a loop merge strategy for content cells. The parameters are as follows:

| Name         | Default Value | Description                                       |
|--------------|---------------|---------------------------------------------------|
| eachRow      | 1             | The number of rows to include in each merge loop. |
| columnExtend | 1             | The number of columns to extend the merge.        |

### `@OnceAbsoluteMerge`

Defines a one-time absolute merge region. The parameters are as follows:

| Name             | Default Value | Description                                  |
|------------------|---------------|----------------------------------------------|
| firstRowIndex    | -1            | The index of the first row to merge.         |
| lastRowIndex     | -1            | The index of the last row to merge.          |
| firstColumnIndex | -1            | The index of the first column to merge.      |
| lastColumnIndex  | -1            | The index of the last column to merge.       |

### `@FreezePane`

Define a freeze pane for an Excel sheet. The parameters are as follows:

| Name           | Default Value | Description                                                              |
|----------------|---------------|--------------------------------------------------------------------------|
| colSplit       | 0             | Horizontal position of freeze pane.                                      |
| rowSplit       | 0             | Vertical position of freeze pane.                                        |
| leftmostColumn | -1            | Left column visible in right pane. By default, it's equal to `colSplit`. |
| topRow         | -1            | Top row visible in bottom pane. By default, it's equal to `rowSplit`.    |

---

## Composing Annotation Configurations

To improve configuration reusability and semantic clarity, FesodSheet introduces a meta-annotation mechanism: simply annotate a custom annotation with `@FesodMarked` to package multiple annotations¹ into a single, reusable business annotation.

> Annotation¹: Includes FesodSheet's built-in annotations (except `@ExcelIgnore` and `@ExcelIgnoreUnannotated`), as well as third-party annotations (retrievable only via `AnnotatedElementUtils` APIs; they do not participate in FesodSheet's internal read/write operations).

### Definition Patterns

**1. Preset Template Pattern**

Best suited for fixed configurations that do not require dynamic parameter passing.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
// highlight-start
@FesodMarked
@ColumnWidth(25)
@NumberFormat("#,##0.00")
@ContentFontStyle(bold = BooleanEnum.TRUE)
// highlight-end
public @interface AmountColumn {
}
```

**2. Alias Mapping Pattern**

When a custom annotation needs to accept parameters dynamically and forward/override them to target annotations, use `@FesodMarked.AliasFor` to establish attribute mappings.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
// highlight-start
@FesodMarked
@ExcelProperty
@ColumnWidth
// highlight-end
public @interface CustomHeader {
    
    // highlight-next-line
    @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
    String title() default "";

    // highlight-next-line
    @FesodMarked.AliasFor(annotation = ExcelProperty.class)
    int index() default -1;
    
    // highlight-next-line
    @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
    int width() default 20;
}
```

> Type Adaptation: If the target annotation attribute expects an array type (e.g., `ExcelProperty#value()` expects `String[]`), declaring a single scalar type (e.g., `String`) in your custom annotation will be automatically wrapped into a one-dimensional array by FesodSheet at runtime.

**Alias Constraints:**

- The target annotation of an alias must be declared on the composed annotation (e.g., `@ExcelProperty` and `@ColumnWidth` in the example above); otherwise, an `IllegalStateException` will be thrown during scanning.
- The `attribute` must be an existing attribute on the target annotation with a matching type (or eligible for scalar-to-array adaptation).
- When `attribute` is omitted, it defaults to **same-name mapping** (e.g., `index()` maps to `ExcelProperty#index()`).
- _Composed annotations can further compose other composed annotations (nested composition). When the same annotation type is declared multiple times across nested layers, the first declared instance takes precedence, and the rest are ignored entirely. (NOT RECOMMENDED)_

### Precedence and Override Rules

When multiple layers of annotations or attributes with the same name coexist on an entity class field, FesodSheet follows the parsing principles below:

- **Annotation Level:** Directly declared target annotation **>** Composed annotation. The directly declared target annotation wins entirely, and any matching target annotation inside composed annotations is completely ignored.
- **Composed Annotation Attribute Level:** Within an active composed annotation, `@FesodMarked.AliasFor` values (including defaults) **>** Static preset values.

:::warning
Alias overriding with `@FesodMarked.AliasFor` is **unconditional**: even if an alias attribute is not explicitly assigned at the usage site (remaining at its default value), its default value will still override any static presets defined inside the composed annotation.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty(value = {"Preset NAME"})
public @interface CustomHeader {

    @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
    String title() default "Aliased NAME";
}
```

```java
// Header will be {"Aliased NAME"} instead of the preset {"Preset NAME"}
@CustomHeader
private String name;
```

Therefore, in practice, alias attributes should either: Have no default value (forcing explicit assignment at the usage site), or use a default value identical to the preset value.
:::

#### Directly Declared Target Annotation

```java
// Header is {"NAME"}
@ExcelProperty(value = {"NAME"})
private String name;
```

#### Composed Annotation via @FesodMarked.AliasFor (Explicit or Default Values)

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty
public @interface CustomHeader {

    @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
    String title();
}
```

```java
// Header is {"Aliased NAME"}
@CustomHeader(title = "Aliased NAME")
private String name;
```

> In the example above, `title()` has no default value, enforcing explicit parameter passing at the usage site and naturally preventing default values from unintentionally overriding static presets.

#### Composed Annotation with Statically Preset Values

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty(value = {"Preset NAME"})
public @interface CustomHeader {
}
```

```java
// Header is {"Preset NAME"}
@CustomHeader
private String name;
```

#### Mixed Usage: Direct and Composed Annotations _(NOT RECOMMENDED)_

When a field directly declares the target annotation, the direct declaration wins completely: all declarations on composed annotations (both static presets and alias values) no longer participate. Attributes left unassigned in the direct declaration will not be backfilled by values from the composed annotation.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty(value = {"Preset NAME"}, index = 0)
public @interface CustomHeader {
}
```

```java
// Direct declaration wins completely: index takes the explicitly assigned 2; value remains at its default value.
// Result: index = 2, value = {""}
@ExcelProperty(index = 2)
@CustomHeader
private String name;
```

> Similarly, when multiple composed annotations declare the same target annotation, the **first declared one takes precedence**, and the others are ignored entirely.

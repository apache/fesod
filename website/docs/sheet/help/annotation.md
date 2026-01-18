---
id: 'annotation'
title: 'Annotation'
---

# Annotation

This section describes how to read annotations provided in the project.

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

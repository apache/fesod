---
id: 'annotation'
title: 'Annotation'
---

# Annotation

This section describes how to read annotations provided in the project.

## Entity Class Annotations

Entity classes are the foundation of read and write operations. Fesod provides various annotations to help
developers easily define fields and formats.

### `@ExcelProperty`

Defines the column name in Excel and the field name to map. Specific parameters are as follows:

| Name      | Default Value          | Description                                                                                                                                                                                                                                                                                           |
|-----------|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Empty                  | Used to match the header in Excel, must be fully matched. If there are multiple header rows, it will match the last row header.                                                                                                                                                                       |
| order     | Integer.MAX_VALUE      | Higher priority than `value`, will match the order of entities and data in Excel according to the order of `order`.                                                                                                                                                                                   |
| index     | -1                     | Higher priority than `value` and `order`, will directly specify which column in Excel to match based on `index`.                                                                                                                                                                                      |
| converter | Automatically selected | Specifies which converter the current field uses. By default, it will be automatically selected. <br> For reading, as long as the `org.apache.fesod.sheet.converters.Converter#convertToJavaData(org.apache.fesod.sheet.converters.ReadConverterContext<?>)` method is implemented, it is sufficient. |

### `@ExcelIgnore`

By default, all fields will match Excel. Adding this annotation will ignore the field.

### `@ExcelIgnoreUnannotated`

By default, all properties without the `@ExcelProperty` annotation are involved in read/write operations. Properties
with this annotation are not involved in read/write operations.

### `@DateTimeFormat`

Date conversion: When using `String` to receive data in Excel date format, this annotation will be called. The
parameters are as follows:

| Name             | Default Value          | Description                                                                                                                                                                                        |
|------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| value            | Empty                  | Refer to `java.text.SimpleDateFormat` .                                                                                                                                                            |
| use1904windowing | Automatically selected | In Excel, time is stored as a double-precision floating-point number starting from 1900, but sometimes the default start date is 1904, so set this value to change the default start date to 1904. |

### `@NumberFormat`

Number conversion, using `String` to receive data in Excel number format will trigger this annotation.

| Name         | Default Value        | Description                           |
|--------------|----------------------|---------------------------------------|
| value        | Empty                | Refer to `java.text.DecimalFormat`.   |
| roundingMode | RoundingMode.HALF_UP | Set the rounding mode when formatting |

### `@ColumnWidth`

Specifies the column width.

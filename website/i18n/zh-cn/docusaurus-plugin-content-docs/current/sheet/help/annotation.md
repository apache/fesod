---
id: 'annotation'
title: '注解'
---

# 注解

本章节介绍读取 FesodSheet 中提供的注解。

## 实体类注解

实体类是读写操作的基础。FesodSheet 提供了多种注解，帮助开发者轻松定义字段和格式。

### **`@ExcelProperty`**

定义电子表格列名和映射的字段名。 具体参数如下：

| 名称        | 默认值               | 描述                                                                                                                                                             |
|-----------|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| value     | 空                 | 用于匹配电子表格中的头，必须全匹配，如果有多行头，会匹配最后一行头                                                                                                                              |
| order     | Integer.MAX_VALUE | 优先级高于 `value`，会根据 `order` 的顺序来匹配实体和电子表格中数据的顺序                                                                                                                  |
| index     | &#45;1            | 优先级高于 `value` 和 `order`，会根据 `index` 直接指定到电子表格中具体的哪一列                                                                                                           |
| converter | 自动选择              | 指定当前字段用什么转换器，默认会自动选择。读的情况下只要实现 `org.apache.fesod.sheet.converters.Converter#convertToJavaData(org.apache.fesod.sheet.converters.ReadConverterContext<?>)` 方法即可 |

### `@ExcelIgnore`

默认所有字段都会和电子表格去匹配，加了这个注解会忽略该字段。

### `@ExcelIgnoreUnannotated`

默认不加 `@ExcelProperty` 的注解的都会参与读写，加了不会参与读写。

### **`@DateTimeFormat`**

日期转换，用 `String` 去接收电子表格日期格式的数据会调用这个注解，参数如下：

| 名称               | 默认值  | 描述                                                                 |
|------------------|------|--------------------------------------------------------------------|
| value            | 空    | 参照 `java.text.SimpleDateFormat` 书写即可                               |
| use1904windowing | 自动选择 | 电子表格中时间是存储 1900 年起的一个双精度浮点数，但是有时候默认开始日期是 1904，所以设置这个值改成默认 1904 年开始 |

### **`@NumberFormat`**

数字转换，用 `String` 去接收电子表格数字格式的数据会调用这个注解。

| 名称           | 默认值                  | 描述                                |
|--------------|----------------------|-----------------------------------|
| value        | 空                    | 参照 `java.text.DecimalFormat` 书写即可 |
| roundingMode | RoundingMode.HALF_UP | 格式化的时候设置舍入模式                      |

### **`@ColumnWidth`**

指定列宽。

### `@HeadRowHeight` & `@ContentRowHeight`

指定表头行（`@HeadRowHeight`）或内容行（`@ContentRowHeight`）的高度。具体参数如下：

| 名称    | 默认值 | 描述                 |
|-------|-----|--------------------|
| value | -1  | 设置高度。`-1` 表示自动设置高度 |

### `@HeadFontStyle` & `@ContentFontStyle`

自定义表头（`@HeadFontStyle`）或内容数据（`@ContentFontStyle`）的字体样式。具体参数如下：

| 名称                 | 默认值                 | 描述                                                                                                                        |
|--------------------|---------------------|---------------------------------------------------------------------------------------------------------------------------|
| fontName           | 空                   | 字体名称（例如 "Arial"）                                                                                                          |
| fontHeightInPoints | -1                  | 设置字体高度                                                                                                                    |
| italic             | BooleanEnum.DEFAULT | 是否使用斜体                                                                                                                    |
| strikeout          | BooleanEnum.DEFAULT | 文本是否显示水平删除线                                                                                                               |
| color              | -1                  | 设置字体颜色（参照 `org.apache.poi.ss.usermodel.IndexedColors` 或 `org.apache.poi.ss.usermodel.Font`，例如 `Font.COLOR_NORMAL`）        |
| typeOffset         | -1                  | 设置字体的类型偏移，用于控制文本显示为正常、上标或下标（参照 `org.apache.poi.ss.usermodel.Font`，例如 `Font.SS_NONE`）                                      |
| underline          | -1                  | 设置下划线类型（参照 `org.apache.poi.ss.usermodel.Font`，例如 `Font.U_SINGLE`）                                                         |
| charset            | -1                  | 设置字符集（参照 `org.apache.poi.common.usermodel.fonts.FontCharset` 或 `org.apache.poi.ss.usermodel.Font`，例如 `Font.ANSI_CHARSET`） |
| bold               | BooleanEnum.DEFAULT | 是否加粗                                                                                                                      |

### `@HeadStyle` & `@ContentStyle`

自定义表头数据（`@HeadStyle`）或内容数据（`@ContentStyle`）的单元格样式（边框、对齐、颜色等）。具体参数如下：

| 名称                  | 默认值                             | 描述                                                                |
|---------------------|---------------------------------|-------------------------------------------------------------------|
| dataFormat          | -1                              | 设置数据格式（必须是 `org.apache.poi.ss.usermodel.BuiltinFormats` 中定义的有效格式） |
| hidden              | BooleanEnum.DEFAULT             | 设置单元格为隐藏，**注意：此选项仅在工作表受保护时生效**                                    |
| locked              | BooleanEnum.DEFAULT             | 设置单元格为锁定，**注意：此选项仅在工作表受保护时生效。**                                   |
| quotePrefix         | BooleanEnum.DEFAULT             | 开启/关闭前缀引号（将看起来像数字或公式的内容视为文本处理）                                    |
| horizontalAlignment | HorizontalAlignmentEnum.DEFAULT | 设置水平对齐方式                                                          |
| wrapped             | BooleanEnum.DEFAULT             | 设置文本是否在单元格内自动换行                                                   |
| verticalAlignment   | VerticalAlignmentEnum.DEFAULT   | 设置垂直对齐方式                                                          |
| rotation            | -1                              | 设置文本的旋转角度                                                         |
| indent              | -1                              | 设置文本缩进的空格数                                                        |
| borderLeft          | BorderStyleEnum.DEFAULT         | 设置左边框的样式                                                          |
| borderRight         | BorderStyleEnum.DEFAULT         | 设置右边框的样式                                                          |
| borderTop           | BorderStyleEnum.DEFAULT         | 设置上边框的样式                                                          |
| borderBottom        | BorderStyleEnum.DEFAULT         | 设置下边框的样式                                                          |
| leftBorderColor     | -1                              | 设置左边框的颜色（参照 `org.apache.poi.ss.usermodel.IndexedColors`）          |
| rightBorderColor    | -1                              | 设置右边框的颜色（参照 `org.apache.poi.ss.usermodel.IndexedColors`）          |
| topBorderColor      | -1                              | 设置上边框的颜色（参照 `org.apache.poi.ss.usermodel.IndexedColors`）          |
| bottomBorderColor   | -1                              | 设置下边框的颜色（参照 `org.apache.poi.ss.usermodel.IndexedColors`）          |
| fillPatternType     | FillPatternTypeEnum.DEFAULT     | 设置填充图案类型                                                          |
| fillBackgroundColor | -1                              | 设置背景填充颜色                                                          |
| fillForegroundColor | -1                              | 设置前景填充颜色，**注意：请确保在设置背景色之前设置前景色**                                  |
| shrinkToFit         | BooleanEnum.DEFAULT             | 控制单元格是否自动缩小以适应过长的文本                                               |

### `@ContentLoopMerge`

定义内容单元格的循环合并策略。具体参数如下：

| 名称           | 默认值 | 描述          |
|--------------|-----|-------------|
| eachRow      | 1   | 每次合并循环包含的行数 |
| columnExtend | 1   | 合并需要延伸的列数   |

### `@OnceAbsoluteMerge`

定义一次性的单元格合并区域。具体参数如下：

| 名称               | 默认值 | 描述          |
|------------------|-----|-------------|
| firstRowIndex    | -1  | 合并区域的第一行索引  |
| lastRowIndex     | -1  | 合并区域的最后一行索引 |
| firstColumnIndex | -1  | 合并区域的第一列索引  |
| lastColumnIndex  | -1  | 合并区域的最后一列索引 |

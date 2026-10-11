---
id: 'annotation'
title: '注解'
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

# 注解

本节概述了 FesodSheet 中提供的核心注解，包括其配置项、使用以及组合元注解支持。

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

### `@FreezePane`

为 Excel 工作表定义冻结窗格。具体参数如下：

| 名称             | 默认值 | 描述                                 |
|----------------|-----|------------------------------------|
| colSplit       | 0   | 冻结窗格的水平位置（即需要冻结的列数）                |
| rowSplit       | 0   | 冻结窗格的垂直位置（即需要冻结的行数）                |
| leftmostColumn | -1  | 右侧窗格中可见的最左侧列。默认情况下，该值等于 `colSplit` |
| topRow         | -1  | 底部窗格中可见的最顶部行。默认情况下，该值等于 `rowSplit` |

---

## 组合注解配置

为了提升配置的复用性与语义化表达，FesodSheet 引入了元注解机制：只需在自定义注解上标注 `@FesodMarked`，即可将多个注解¹打包组合成一个可复用的业务注解。

> 注解¹：包括 FesodSheet 提供的内部注解（`@ExcelIgnore` 与 `@ExcelIgnoreUnannotated` 除外）；也包括第三方注解（仅可通过 `AnnotatedElementUtils` 的 API 获取，不会参与 FesodSheet 自身的读写行为）。

### 定义模式

**1. 预设模版模式**

适合固定格式无需动态传参的场景。

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

**2. 别名映射模式**

当自定义注解需要动态接收参数并传递覆盖到目标注解时，可使用 `@FesodMarked.AliasFor` 建立属性映射。

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

> 类型自适应：若目标注解属性要求数组类型（如 `ExcelProperty#value()` 为 `String[]`），在自定义注解中声明单个标量类型（如 `String`）时，FesodSheet 会在运行时自动包装为一维数组。

**别名约束**：

- 别名的目标注解必须声明在组合注解上（如上例中的 `@ExcelProperty`、`@ColumnWidth`），否则扫描时抛出 `IllegalStateException`；
- `attribute` 必须是目标注解真实存在的属性，且类型一致（或为"标量对应目标数组分量类型"的适配场景）；
- `attribute` 留空时按**同名映射**处理（如上例 `index()` 即别名 `ExcelProperty#index()`）；
- _组合注解可以再组合其他组合注解（嵌套组合）。同一注解类型被重复声明时，以最先声明的一次为准，其余整体忽略。（不推荐）_

### 优先级与覆盖规则

当实体类字段上同时存在多层注解或同名属性时，FesodSheet 遵循以下解析原则：

- **注解级别：** 直接标注目标注解 **>** 标注组合注解。直接标注的目标注解整体胜出，组合注解内部对应的目标注解会被整体忽略。
- **组合注解属性级别：** 在生效的组合注解内部，`@FesodMarked.AliasFor` 赋值（含默认值） **>** 静态预设值。

:::warning
`@FesodMarked.AliasFor` 的别名覆盖是**无条件**的：即使别名属性未显式赋值（处于默认值），其默认值也会覆盖组合注解内部的静态预设值。

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
// 表头为 {"Aliased NAME"} 而非预设的 {"Preset NAME"}
@CustomHeader
private String name;
```

因此实践中别名属性应当：要么不声明默认值（强制使用处显式赋值），要么让默认值与预设值保持一致。
:::

#### 字段直接标注目标注解

```java
// 表头为 {"NAME"}
@ExcelProperty(value = {"NAME"})
private String name;
```

#### 字段标注组合注解（通过 `@FesodMarked.AliasFor` 显式赋值或默认值传递）

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
// 表头为 {"Aliased NAME"}
@CustomHeader(title = "Aliased NAME")
private String name;
```

> 上例中 `title()` 未声明默认值，可强制使用处显式传参，天然规避别名默认值覆盖静态预设值的问题。

#### 字段标注组合注解（通过内部静态预设值传递）

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty(value = {"Preset NAME"})
public @interface CustomHeader {
}
```

```java
// 表头为 {"Preset NAME"}
@CustomHeader
private String name;
```

#### 同字段混用：直接标注 + 组合注解 _（不推荐）_

字段直接标注目标注解时，**直接标注整体获胜**：组合注解对该注解的所有声明（静态预设、别名取值）均不再参与，包括直接标注中未显式赋值的属性，也不会被组合注解的取值补齐。

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FesodMarked
@ExcelProperty(value = {"Preset NAME"}, index = 0)
public @interface CustomHeader {
}
```

```java
// 直接标注整体生效：index 采用字段显式赋值的 2；未显式赋值的 value 保持默认；
// 结果：index = 2，value = {""}
@ExcelProperty(index = 2)
@CustomHeader
private String name;
```

> 同理，多个组合注解重复声明同一目标注解时，以**最先声明的一个**为准，其余整体忽略。

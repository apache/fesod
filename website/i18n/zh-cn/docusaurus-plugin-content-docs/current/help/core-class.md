---
id: 'core-class'
title: '核心类'
---

# 核心类

本章节介绍读取 FastExcel 中的核心类。

## 概述

如果使用 FastExcel 进行自定义读写操作，需要理解其重要的概念和类。

## 核心概念

### FastExcel

入口类，用于构建开始各种操作。

### 多种 Builder

对读和写操作分别有对应的 Builder 类：

- **`ExcelReaderBuilder` 和 `ExcelWriterBuilder`**：分别为构建出一个 ReadWorkbook 和 WriteWorkbook，可以理解成一个 excel 对象，一个 excel 只要构建一个。
- **`ExcelReaderSheetBuilder` 和 `ExcelWriterSheetBuilder`**：分别构建出一个 ReadSheet 和 WriteSheet 对象，可以理解成 excel 里面的一页,每一页都要构建一个。
- **`CsvReaderBuilder` 和 `CsvWriterBuilder`**：构建内部所需的 CsvFormat。

### ReadListener

在每一行读取完毕后都会调用 ReadListener 来处理数据。

### WriteHandler

在每一个操作包括创建单元格、创建表格等都会调用 WriteHandler 来处理数据。

所有配置都是继承的，Workbook 的配置会被 Sheet 继承，所以在用 FastExcel 设置参数的时候，在 FastExcel...sheet() 方法之前作用域是整个 sheet,在 FastExcel...csv() 方法之前作用域是整个 csv。

---

## WriteHandler

### 概述

`WriteHandler` 是 FastExcel 提供的接口，用于在写入 Excel 文件时拦截写入过程，允许开发者自定义操作，如设置单元格样式、合并单元格、添加超链接、插入批注等。
通过实现 `WriteHandler`，开发者可以深入控制写入流程，以满足复杂的业务需求。

### 分类

FastExcel 提供了以下几种 WriteHandler 接口，分别用于处理不同的写入场景：

| 接口名                   | 描述                                  |
|-----------------------|-------------------------------------|
| **CellWriteHandler**  | 单元格级别的拦截器，允许对单元格数据和样式进行自定义操作        |
| **RowWriteHandler**   | 行级别的拦截器，用于在行数据写入完成后执行额外操作           |
| **SheetWriteHandler** | 工作表级别的拦截器，可用于设置工作表级别的属性（如冻结窗格、下拉框等） |

### 使用

1. 实现对应的 `WriteHandler` 接口：
    - 选择适合需求的接口（`CellWriteHandler`、`RowWriteHandler` 或 `SheetWriteHandler`）。
    - 实现接口中的方法，在方法中定义自定义逻辑。

2. 注册 WriteHandler：
    - 在调用 `FastExcel.write()` 时通过 `.registerWriteHandler()` 注册自定义的 WriteHandler。

### 示例

#### 设置单元格样式

为所有内容单元格设置背景颜色为黄色，字体为蓝色。

自定义 `CellWriteHandler`

```java
@Slf4j
public class CustomCellStyleHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 确保只操作内容单元格（非表头）
        if (BooleanUtils.isNotTrue(context.getHead())) {
            WriteCellData<?> cellData = context.getFirstCellData();
            WriteCellStyle style = cellData.getOrCreateStyle();

            // 设置背景颜色为黄色
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

            // 设置字体为蓝色
            WriteFont font = new WriteFont();
            font.setColor(IndexedColors.BLUE.getIndex());
            style.setWriteFont(font);

            log.info("已设置样式: 行 {}, 列 {}", context.getRowIndex(), context.getColumnIndex());
        }
    }
}
```

注册并使用

```java
@Test
public void customCellStyleWrite() {
    String fileName = "customCellStyleWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomCellStyleHandler())
        .sheet("自定义样式")
        .doWrite(data());
}
```

#### 插入批注

为表头的第一行第二列插入批注。

自定义 `RowWriteHandler`

```java
@Slf4j
public class CommentRowWriteHandler implements RowWriteHandler {

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (BooleanUtils.isTrue(context.getHead())) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            // 创建批注
            Comment comment = drawing.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
            comment.setString(new XSSFRichTextString("这是一个批注"));
            sheet.getRow(0).getCell(1).setCellComment(comment);

            log.info("批注已插入到第一行第二列");
        }
    }
}
```

注册并使用

```java
@Test
public void commentWrite() {
    String fileName = "commentWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new CommentRowWriteHandler())
        .sheet("插入批注")
        .doWrite(data());
}
```

#### 添加下拉框

为第一列的前两行数据添加下拉框。

自定义 `SheetWriteHandler`

```java
@Slf4j
public class DropdownSheetWriteHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();

        // 创建下拉框区域
        CellRangeAddressList range = new CellRangeAddressList(1, 2, 0, 0);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"选项1", "选项2"});
        DataValidation validation = helper.createValidation(constraint, range);
        sheet.addValidationData(validation);

        log.info("下拉框已添加到第一列的前两行");
    }
}
```

注册并使用

```java
@Test
public void dropdownWrite() {
    String fileName = "dropdownWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new DropdownSheetWriteHandler())
        .sheet("添加下拉框")
        .doWrite(data());
}
```

---

## ReadListener

### 概述

`ReadListener` 是 FastExcel 提供的接口，用于在读取 Excel 文件时对每一行数据进行处理。
它是 FastExcel 核心组件之一，允许开发者实现自定义逻辑来处理数据行、处理表头，甚至在读取完成后执行特定操作。

### 方法

`ReadListener` 是一个泛型接口，泛型类型是要读取的对象类型（如 `DemoData`）。其核心方法如下：

| 方法                                                                                       | 描述                                                  |
|------------------------------------------------------------------------------------------|-----------------------------------------------------|
| `void invoke(T data, AnalysisContext context)`                                           | 当读取到一行数据时触发，`data` 是解析后的当前行对象，`context` 包含读取的上下文信息。 |
| `void doAfterAllAnalysed(AnalysisContext context)`                                       | 在所有数据解析完成后调用，可用于资源释放或统计数据处理。                        |
| `void onException(Exception exception, AnalysisContext context)` *(可选)*                  | 捕获读取过程中的异常，方便处理解析错误。                                |
| `void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context)` *(可选)* | 获取 Excel 表头信息，用于动态处理表头。                             |

### 使用场景

- **简化代码**：适用于简单的按行处理，减少对内存和处理逻辑的控制。
- **异常处理**：能够捕获和处理读取过程中的异常。

### 实现步骤

1. 实现 `ReadListener` 接口：
    - 使用实体类作为泛型类型（如 `ReadListener<DemoData>`）。
    - 实现核心方法，根据需要添加数据处理逻辑。

2. 在读取时注册自定义 `ReadListener`：
    - 调用 `FastExcel.read()` 方法时，传入自定义监听器实例。

### 示例

#### 处理行数据

自定义 `ReadListener`

```java
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    private static final int BATCH_COUNT = 100; // 批量处理阈值
    private List<DemoData> cachedDataList = new ArrayList<>(BATCH_COUNT);

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到一条数据: {}", JSON.toJSONString(data));
        cachedDataList.add(data);

        // 达到批量阈值，执行处理
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理剩余数据
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        log.info("存储 {} 条数据到数据库", cachedDataList.size());
        // 实现批量入库逻辑
    }
}
```

使用

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .sheet() // 默认读取第一个 Sheet
        .doRead();
}
```

#### 处理表头

自定义 `ReadListener`

```java
@Slf4j
public class HeadDataListener implements ReadListener<DemoData> {

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到表头: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到数据: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
}
```

使用

```java
@Test
public void readWithHead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new HeadDataListener())
        .sheet() // 默认读取第一个 Sheet
        .doRead();
}
```

#### 异常处理

自定义 `ReadListener`

```java
@Slf4j
public class ExceptionHandlingListener implements ReadListener<DemoData> {

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析异常: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("第 {} 行, 第 {} 列解析错误，数据: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}
```

使用

```java
@Test
public void readWithExceptionHandling() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new ExceptionHandlingListener())
        .sheet()
        .doRead();
}
```

#### 分页处理

```java
@Test
public void pageRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new PageReadListener<>(dataList -> {
        // 分页批量处理
        log.info("读取到一批数据: {}", JSON.toJSONString(dataList));
        // 实现数据处理逻辑
    }))
    .sheet()
    .doRead();
}
```

> **说明**：
>
> - `PageReadListener` 是 FastExcel 提供的便捷工具类，支持基于分页的批量处理。
> - 默认每页大小为 1，可通过构造器指定。

---

## AnalysisEventListener

### 概述

`AnalysisEventListener` 是 FastExcel 中用于处理读取 Excel 数据的核心监听器。它基于事件驱动机制，允许开发者在读取每一行数据时执行自定义操作，并在所有数据解析完成后进行相应处理。它通常用于流式读取大量数据，适合需要处理大数据量、进行批量操作（如批量插入数据库）的场景。

核心特性:

- **逐行读取**：`AnalysisEventListener` 按行读取 Excel 文件的数据，在读取每行数据时执行 `invoke` 方法，适合流式处理。
- **内存控制**：可以设置 `BATCH_COUNT` 来控制每次处理的数据量，避免内存溢出。
- **批量处理**：可以缓存一定数量的数据并批量处理，适用于大数据量场景。
- **事件驱动**：当读取每一行数据时，调用 `invoke` 方法；所有数据读取完毕后，调用 `doAfterAllAnalysed` 方法。

### 方法

`AnalysisEventListener` 主要包含以下方法：

| 方法名                                                                                 | 描述                                              |
|-------------------------------------------------------------------------------------|-------------------------------------------------|
| `invoke(T data, AnalysisContext context)`                                           | 当读取到一行数据时触发，`data` 为解析后的当前行数据，`context` 为读取上下文。 |
| `doAfterAllAnalysed(AnalysisContext context)`                                       | 在所有数据解析完成后调用，用于资源清理或批量操作后处理。                    |
| `onException(Exception exception, AnalysisContext context)` *(可选)*                  | 捕获并处理解析过程中抛出的异常，方便处理错误数据。                       |
| `invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context)` *(可选)* | 获取 Excel 表头数据，常用于动态表头处理。                        |

### 使用场景

- **流式数据处理**：比如读取大量数据时，可以边读取边处理，减少内存消耗。
- **批量插入数据库**：如批量处理 Excel 中的行数据并存储到数据库。

### 实现步骤

1. 继承 `AnalysisEventListener` 并实现其方法。
2. 在读取时传入自定义监听器，通过 `FastExcel.read()` 注册。

### 示例

#### 处理每行数据并批量入库

继承 `AnalysisEventListener`

```java
@Slf4j
public class DemoDataListener extends AnalysisEventListener<DemoData> {

    private static final int BATCH_COUNT = 100;  // 每批处理的数据量
    private List<DemoData> cachedDataList = new ArrayList<>(BATCH_COUNT);

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到一条数据: {}", JSON.toJSONString(data));
        cachedDataList.add(data);

        // 如果缓存的数据量达到 BATCH_COUNT，执行批量处理
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();  // 清空缓存
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理剩余的数据
        if (!cachedDataList.isEmpty()) {
            saveData();
        }
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        log.info("存储 {} 条数据到数据库", cachedDataList.size());
        // TODO: 批量保存到数据库
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析过程中发生异常: {}", exception.getMessage());
        // 可以记录异常数据，或者重新抛出异常
    }
}
```

使用

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .sheet()  // 默认读取第一个 Sheet
        .doRead();
}
```

#### 处理表头

可以使用 `invokeHead` 方法获取表头信息，用于处理动态表头场景，或者进行表头数据的自定义解析。

继承 `AnalysisEventListener`

```java
@Slf4j
public class DemoDataListenerWithHead extends AnalysisEventListener<DemoData> {

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到表头数据: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到数据: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("读取过程中发生异常: {}", exception.getMessage());
    }
}
```

使用

```java
@Test
public void readWithHead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListenerWithHead())
        .sheet()  // 默认读取第一个 Sheet
        .doRead();
}
```

#### 异常处理

提供了 `onException` 方法，开发者可以在读取过程中捕获异常，并进行处理（例如记录错误、跳过错误行等）。

继承 `AnalysisEventListener`

```java
@Slf4j
public class ExceptionHandlingListener extends AnalysisEventListener<DemoData> {

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析异常: {}", exception.getMessage());
        // 捕获解析异常后，进行自定义处理
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("第 {} 行, 第 {} 列解析异常, 数据: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        // 处理正常行数据
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
}
```

使用

```java
@Test
public void readWithExceptionHandling() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new ExceptionHandlingListener())
        .sheet()
        .doRead();
}
```

### 与 `ReadListener` 比较

`AnalysisEventListener` 和 `ReadListener` 都是 FastExcel 提供的接口，目的是为了让开发者在读取 Excel 时进行自定义处理，但它们在功能和使用场景上有一些关键的区别。

#### 区别

| 特性       | `AnalysisEventListener`                       | `ReadListener`                                |
|----------|-----------------------------------------------|-----------------------------------------------|
| **接口设计** | 基于事件驱动，处理每行数据并保存批量数据                          | 基于回调接口，简化了处理，适合简单用法                           |
| **内存控制** | 通过 `BATCH_COUNT` 控制内存使用，适合大数据量处理              | 无专门内存控制，通常用于简单的读取操作                           |
| **使用场景** | 复杂场景，流式处理、批量入库、分页处理等                          | 简单的按行数据处理和异常捕获                                |
| **方法**   | `invoke`, `doAfterAllAnalysed`, `onException` | `invoke`, `doAfterAllAnalysed`, `onException` |
| **易用性**  | 需要更多的内存管理和复杂的逻辑处理                             | 更加简洁易用，适合简单的读取操作                              |

#### 如何选择

- 使用 `AnalysisEventListener`：
  - 如果你需要控制内存消耗、批量处理数据或者需要处理复杂的读取逻辑（如分页读取、批量写入数据库）。
  - 适用于大数据量处理，提供更多的灵活性。

- 使用 `ReadListener`：
  - 如果你希望简化代码，并且没有复杂的内存控制需求，只需处理每一行数据的逻辑。
  - 适合简单的 Excel 数据读取和异常捕获场景。
      总的来说，`ReadListener` 是更为简化的接口，适用于较为简单的场景，而 `AnalysisEventListener` 提供了更强的控制力和扩展性，适合复杂的数据处理需求。开发者可以根据实际需求选择合适的监听器。

## Converter

### 概述

`Converter` 是 FastExcel 提供的接口，用于在处理 Excel 文件时对数据进行转换。允许开发者自定义操作，通过实现 `Converter` 接口，自定义数据转换逻辑。

### 方法

`Converter` 是一个泛型接口，泛型类型是需要被转换的对象类型（如 `Date`)。其核心方法如下：

| 方法名                                                                                                                                   | 描述                                      |
|---------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| `Class<?> supportJavaTypeKey()`*(可选)*                                                                                                 | 返回支持的 Java 对象类型                         |
| `CellDataTypeEnum supportExcelTypeKey()`*(可选)*                                                                                        | 返回支持的 Excel 单元格类型，枚举类为 CellDataTypeEnum |
| `T convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)` *(可选)* | 将 Excel 单元格数据转换为 Java 对象                |
| `WriteCellData<?> convertToExcelData(T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)` *(可选)*  | 将 Java  对象转换为 Excel 单元格数据对象             |
| `WriteCellData<?> convertToExcelData(WriteConverterContext<T> context)` *(可选)*                                                        | 将 Java  对象转换为 Excel 单元格数据对象             |

FastExcel 默认提供了很多常用类型的转换器， 并已默认在 `DefaultConverterLoader` 中注册。

您可以自定义转换器，但类型不能与默认的类型重复。类型注册时，使用的 `ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey())` 作为 key 值。

### 使用场景

- **数据转换**：对 Excel 数据进行转换，如将日期转换为字符串、将字符串转换为日期等。

### 实现步骤

1. 实现 `Converter` 接口并实现其方法。
2. 在读取或写入时传入自定义转换器。

### 示例

#### TimestampNumber 转换器

实现 `Converter`

```java
@Slf4j
public class TimestampNumberConverter implements Converter<Timestamp> {
    @Override
    public Class<Timestamp> supportJavaTypeKey() {
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Timestamp value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(
                    BigDecimal.valueOf(DateUtil.getExcelDate(value, globalConfiguration.getUse1904windowing())));
        } else {
            return new WriteCellData<>(BigDecimal.valueOf(DateUtil.getExcelDate(
                    value, contentProperty.getDateTimeFormatProperty().getUse1904windowing())));
        }
    }
}
```

使用

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    // 读取
    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .registerConverter(new TimestampNumberConverter())
        .sheet()
        .doRead();

    // 写入
    FastExcel.write(fileName)
         .registerConverter(new TimestampNumberConverter())
         .sheet()
         .doWrite(data());
}
```

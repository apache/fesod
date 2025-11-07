---
id: 'core-class'
title: 'Core Class'
---

# Core Classes

This section introduces the core classes in FastExcel.

## Overview

If you use FastExcel for custom read/write operations, you need to understand its important concepts and classes.

## Core Concepts

### FastExcel

Entry class used to start various operations

### Multiple Builders

There are corresponding Builder classes for read and write operations:

- **`ExcelReaderBuilder` and `ExcelWriterBuilder`**：Constructs a `ReadWorkbook` or `WriteWorkbook`, which can be understood as an Excel object; only one Excel needs to be constructed
- **`ExcelReaderSheetBuilder` and `ExcelWriterSheetBuilder`**：Constructs a `ReadSheet` or `WriteSheet` object, which can be understood as a page in Excel; each page needs to be constructed
- **`CsvReaderBuilder` and `CsvWriterBuilder`**：Construct the CsvFormat required internally.

### ReadListener

Called to handle data after each row is read

### WriteHandler

Called to handle data for each operation, including creating cells, creating tables, etc.

All configurations are inherited. The configuration of `Workbook` will be inherited by `Sheet`, so when setting parameters in FastExcel, the scope is the entire sheet before the `FastExcel...sheet()` method, and the scope is the entire csv before the `FastExcel...csv()` method.

---

## WriteHandler

### Overview

`WriteHandler` is an interface provided by FastExcel for intercepting the writing process when writing to an Excel file, allowing developers to customize operations such as setting cell styles, merging cells, adding hyperlinks, inserting comments, etc. By implementing `WriteHandler`, developers can have precise control over the writing process to meet complex business requirements.

### WriteHandler Interface Categories

FastExcel provides the following WriteHandler interfaces for handling different writing scenarios:

| Interface Name        | Description                                                                                                        |
|-----------------------|--------------------------------------------------------------------------------------------------------------------|
| **CellWriteHandler**  | Interceptor at the cell level, allows custom operations on cell data and styles.                                   |
| **RowWriteHandler**   | Row-level interceptor, used to perform additional operations after row data is written.                            |
| **SheetWriteHandler** | Worksheet-level interceptor, used to set worksheet-level properties (such as freeze panes, drop-down lists, etc.). |

### Use

1. Implement the corresponding `WriteHandler` interface：
    - Choose the appropriate interface (`CellWriteHandler`, `RowWriteHandler`, or `SheetWriteHandler`).
    - Implement the methods in the interface, defining custom logic in the methods.

2. Register the WriteHandler：
    - Register your custom WriteHandler when calling `FastExcel.write()` using `.registerWriteHandler()`.

### Example

#### Set Cell Style

Set the background color to yellow and font color to blue for all content cells.

Customise a `CellWriteHandler`

```java
@Slf4j
public class CustomCellStyleHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // Ensure only content cells (not headers) are operated on
        if (BooleanUtils.isNotTrue(context.getHead())) {
            WriteCellData<?> cellData = context.getFirstCellData();
            WriteCellStyle style = cellData.getOrCreateStyle();

            // Set background color to yellow
            style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

            // Set font color to blue
            WriteFont font = new WriteFont();
            font.setColor(IndexedColors.BLUE.getIndex());
            style.setWriteFont(font);

            log.info("Style set: Row {}, Column {}", context.getRowIndex(), context.getColumnIndex());
        }
    }
}
```

Register and Use

```java
@Test
public void customCellStyleWrite() {
    String fileName = "customCellStyleWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new CustomCellStyleHandler())
        .sheet("Custom Style")
        .doWrite(data());
}
```

#### Inserting Comments

Insert a comment for the first row, second column of the header.

Customise a `RowWriteHandler`

```java
@Slf4j
public class CommentRowWriteHandler implements RowWriteHandler {

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (BooleanUtils.isTrue(context.getHead())) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            // Create a comment
            Comment comment = drawing.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
            comment.setString(new XSSFRichTextString("This is a comment"));
            sheet.getRow(0).getCell(1).setCellComment(comment);

            log.info("Comment inserted at first row, second column");
        }
    }
}
```

Register and Use

```java
@Test
public void commentWrite() {
    String fileName = "commentWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new CommentRowWriteHandler())
        .sheet("Insert Comment")
        .doWrite(data());
}
```

#### Adding Dropdown Lists

Add a dropdown list for the first column of the first two rows.

Customise a  `SheetWriteHandler`

```java
@Slf4j
public class DropdownSheetWriteHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();

        // Create dropdown list range
        CellRangeAddressList range = new CellRangeAddressList(1, 2, 0, 0);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"Option 1", "Option 2"});
        DataValidation validation = helper.createValidation(constraint, range);
        sheet.addValidationData(validation);

        log.info("Dropdown list added to the first column of the first two rows");
    }
}
```

Register and Use

```java
@Test
public void dropdownWrite() {
    String fileName = "dropdownWrite.xlsx";

    FastExcel.write(fileName, DemoData.class)
        .registerWriteHandler(new DropdownSheetWriteHandler())
        .sheet("Add Dropdown")
        .doWrite(data());
}
```

---

## ReadListener

### Overview

`ReadListener` is an interface provided by FastExcel for processing each row of data when reading an Excel file. It is one of the core components of FastExcel, allowing developers to implement custom logic to handle data rows, process headers, and even perform specific operations after reading is complete.

### Methods

`ReadListener` is a generic interface, where the generic type is the type of object to be read (e.g., `DemoData`). Its core methods are as follows:

| Method Name                                                                                    | Description                                                                                                                           |
|------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `void invoke(T data, AnalysisContext context)`                                                 | Triggered when a line of data is read. `data` is the parsed current line object, and `context` contains the read context information. |
| `void doAfterAllAnalysed(AnalysisContext context)`                                             | Called after all data parsing is complete, it can be used for resource release or statistical data processing.                        |
| `void onException(Exception exception, AnalysisContext context)` *(Optional)*                  | Capture exceptions during the reading process to facilitate error handling and analysis.                                              |
| `void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context)` *(Optional)* | Get Excel header information for dynamic header processing.                                                                           |

### Use Cases

- **Simplify code**: Suitable for simple row-by-row processing, reducing control over memory and processing logic.
- **Exception handling**: Able to capture and handle exceptions during the reading process.

### Implementation Steps

1. Implement the `ReadListener` interface:
   - Use an entity class as the generic type (e.g., `ReadListener<DemoData>`).
   - Implement the core methods and add data processing logic as needed.

2. Register the custom `ReadListener` during reading:
   - When calling the `FastExcel.read()` method, pass in the custom listener instance.

### Example

#### Process Row Data

Customise a `ReadListener`

```java
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    private static final int BATCH_COUNT = 100; // Batch processing threshold
    private List<DemoData> cachedDataList = new ArrayList<>(BATCH_COUNT);

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Parsing a single piece of data: {}", JSON.toJSONString(data));
        cachedDataList.add(data);

        // When the batch threshold is reached, execute processing.
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Processing residual data
        saveData();
        log.info("All data analysis completed!");
    }

    private void saveData() {
        log.info("Store {} pieces of data to the database", cachedDataList.size());
        // Implement batch entry logic
    }
}
```

Use

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .sheet() // Read the first sheet by default
        .doRead();
}
```

#### Processing Table Headers

Customise a `ReadListener`

```java
@Slf4j
public class HeadDataListener implements ReadListener<DemoData> {

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Analyse to table header: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Analyse the data: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data analysis completed!");
    }
}
```

Use

```java
@Test
public void readWithHead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new HeadDataListener())
        .sheet() // Read the first sheet by default
        .doRead();
}
```

#### Handling Exceptions

Customise a `ReadListener`

```java
@Slf4j
public class ExceptionHandlingListener implements ReadListener<DemoData> {

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Analysing exception: {}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("Row {} column {} parsing error, data: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}
```

Use

```java
@Test
public void readWithExceptionHandling() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new ExceptionHandlingListener())
        .sheet()
        .doRead();
}
```

#### Pagination

```java
@Test
public void pageRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new PageReadListener<>(dataList -> {
        // Pagination batch processing
        log.info("Read a batch of data: {}", JSON.toJSONString(dataList));
        // Implement data processing logic
    }))
    .sheet()
    .doRead();
}
```

> **Note**：
>
> - `PageReadListener` is a convenient utility class provided by FastExcel that supports batch processing based on pagination.
> - The default page size is 1, which can be specified using the constructor.

---

## AnalysisEventListener

### Overview

`AnalysisEventListener` is the core listener used in FastExcel for processing Excel data. It is based on an event-driven mechanism, allowing developers to perform custom operations when reading each row of data and to perform corresponding processing after all data has been parsed. It is typically used for streaming large amounts of data and is suitable for scenarios that require processing large data volumes and performing batch operations (such as batch insertion into a database).

Core Features:

- **Line-by-line reading**: `AnalysisEventListener` reads data from Excel files line by line, executing the `invoke` method when reading each line of data, making it suitable for streaming processing.
- **Memory control**: You can set `BATCH_COUNT` to control the amount of data processed each time, preventing memory overflow.
- **Batch Processing**: You can cache a certain amount of data and process it in batches, suitable for scenarios with large data volumes.
- **Event-Driven**: The `invoke` method is called when reading each row of data; after all data has been read, the `doAfterAllAnalysed` method is called.

### Methods

| Method Name                                                                              | Description                                                                                                                           |
|------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `invoke(T data, AnalysisContext context)`                                                | Triggered when a line of data is read. `data` is the parsed current line object, and `context` contains the read context information. |
| `doAfterAllAnalysed(AnalysisContext context)`                                            | Called after all data parsing is complete, used for resource cleanup or post-processing of batch operations.                          |
| `onException(Exception exception, AnalysisContext context)` *(Optional)*                 | Capture and handle exceptions thrown during parsing to facilitate error data handling.                                                |
| `invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context)` *(Optional)* | Retrieve Excel header data, commonly used for dynamic header processing.                                                              |

### Use Cases

- **Streaming Data Processing**: For example, when reading large amounts of data, you can process the data as you read it, reducing memory consumption.
- **Batch insertion into a database**: For example, batch processing row data from Excel and storing it in a database.

### Implementation Steps

1. Inherit from `AnalysisEventListener` and implement its methods.
2. Pass in a custom listener during reading and register it using `FastExcel.read()`.

### Example

#### Process each row of data and batch import into the database

Inherit `AnalysisEventListener`

```java
@Slf4j
public class DemoDataListener extends AnalysisEventListener<DemoData> {

    private static final int BATCH_COUNT = 100;  // The amount of data processed in each batch
    private List<DemoData> cachedDataList = new ArrayList<>(BATCH_COUNT);

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Parse to a single piece of data: {}", JSON.toJSONString(data));
        cachedDataList.add(data);

        // If the amount of cached data reaches BATCH_COUNT, perform batch processing.
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();  // Clear the cache
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Process the remaining data
        if (!cachedDataList.isEmpty()) {
            saveData();
        }
        log.info("All data analysis completed!");
    }

    private void saveData() {
        log.info("Store {} pieces of data to the database", cachedDataList.size());
        // Implement data processing logic
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("An error occurred during the analysis process.: {}", exception.getMessage());
        // Can record abnormal data or re-throw exceptions.
    }
}
```

Use

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .sheet()  // Read the first sheet by default
        .doRead();
}
```

#### Processing Table Headers

You can use the `invokeHead` method to obtain header information for handling dynamic header scenarios or for customising header data parsing.

Inherit `AnalysisEventListener`

```java
@Slf4j
public class DemoDataListenerWithHead extends AnalysisEventListener<DemoData> {

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Parse header data: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Analyse the data: {}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data analysis completed!");
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("An error occurred during reading: {}", exception.getMessage());
    }
}
```

Use

```java
@Test
public void readWithHead() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new DemoDataListenerWithHead())
        .sheet()  // Read the first sheet by default
        .doRead();
}
```

#### Handling Exceptions

The `onException` method is provided so that developers can catch exceptions during reading and handle them (e.g., log errors, skip error lines, etc.).

Inherit `AnalysisEventListener`

```java
@Slf4j
public class ExceptionHandlingListener extends AnalysisEventListener<DemoData> {

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Analysing anomalies: {}", exception.getMessage());
        // After capturing and analysing the exception, perform customised processing.
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            log.error("Row {}, column {} parsing exception, data: {}", ex.getRowIndex(), ex.getColumnIndex(), ex.getCellData());
        }
    }

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        // Processing normal row data
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data analysis completed!");
    }
}
```

Use

```java
@Test
public void readWithExceptionHandling() {
    String fileName = "path/to/demo.xlsx";

    FastExcel.read(fileName, DemoData.class, new ExceptionHandlingListener())
        .sheet()
        .doRead();
}
```

### Compared to ReadListener

`AnalysisEventListener` and `ReadListener` are both interfaces provided by FastExcel, designed to allow developers to perform customised processing when reading Excel files. However, they have some key differences in terms of functionality and use cases.

#### Different

| Features             | `AnalysisEventListener`                                                                       | `ReadListener`                                                         |
|----------------------|-----------------------------------------------------------------------------------------------|------------------------------------------------------------------------|
| **Interface Design** | Event-driven, processes each row of data and saves batch data                                 | Callback interface, simplifies processing, suitable for simple use     |
| **Memory Control**   | Controls memory usage via `BATCH_COUNT`, suitable for large data volume processing            | No dedicated memory control, typically used for simple read operations |
| **Use Cases**        | Complex scenarios, such as stream processing, batch data ingestion, and pagination processing | Simple row-based data processing and exception handling                |
| **Methods**          | `invoke`, `doAfterAllAnalysed`, `onException`                                                 | `invoke`, `doAfterAllAnalysed`, `onException`                          |
| **Usability**        | Requires more memory management and complex logic processing                                  | More concise and user-friendly, suitable for simple read operations    |

#### How to choose

Use `AnalysisEventListener`:

- If you need to control memory consumption, batch process data, or handle complex read logic (such as paginated reading or batch writing to a database).
- Suitable for processing large datasets, offering greater flexibility.

Use `ReadListener`:

- If you want to simplify your code and do not have complex memory control requirements, and only need to handle the logic for each row of data.
- Suitable for simple Excel data reading and exception handling scenarios.

In summary, `ReadListener` is a more simplified interface suitable for simpler scenarios, while `AnalysisEventListener` offers greater control and scalability, making it suitable for complex data processing requirements. Developers can choose the appropriate listener based on their actual needs.

## Converter

### Overview

`Converter` is an interface provided by FastExcel for converting data when processing Excel files. It allows developers to customise operations by implementing the `Converter` interface to define custom data conversion logic.

### Methods

`Converter` is a generic interface, and the generic type is the object type to be converted (such as `Date`). Its core methods are as follows:

| Method Name                                                                                                                                 | Description                                                             |
|---------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `Class<?> supportJavaTypeKey()`*(Optional)*                                                                                                 | Returns the supported Java object types.                                |
| `CellDataTypeEnum supportExcelTypeKey()`*(Optional)*                                                                                        | Returns the supported Excel cell types, enumerated as CellDataTypeEnum. |
| `T convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)` *(Optional)* | Convert Excel cell data to Java objects                                 |
| `WriteCellData<?> convertToExcelData(T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)` *(Optional)*  | Convert Java objects to Excel cell data objects                         |
| `WriteCellData<?> convertToExcelData(WriteConverterContext<T> context)` *(Optional)*                                                        | Convert Java objects to Excel cell data objects                         |

FastExcel provides many commonly used type converters by default, which are already registered in `DefaultConverterLoader`.

You can customise converters, but the types must not overlap with the default types. When registering types, use `ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey())` as the key value.

### Use Cases

- **Data Conversion**: Convert Excel data, such as converting dates to strings, converting strings to dates, etc.

### Implementation Steps

1. Implement the `Converter` interface and its methods.
2. Pass in a custom converter when reading or writing.

### Example

#### TimestampNumber Converter

Implement `Converter`

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

Use

```java
@Test
public void simpleRead() {
    String fileName = "path/to/demo.xlsx";

    // Read
    FastExcel.read(fileName, DemoData.class, new DemoDataListener())
        .registerConverter(new TimestampNumberConverter())
        .sheet()
        .doRead();

    // Write
    FastExcel.write(fileName)
         .registerConverter(new TimestampNumberConverter())
         .sheet()
         .doWrite(data());
}
```

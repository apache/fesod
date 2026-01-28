---
id: 'faq'
title: 'FAQ'
---

# FAQ

This section describes common issues that may arise when using this project.

## Feature Limitations

- **Q:** What functions does FesodSheet support? What functions are not supported?
- **A:** FesodSheet supports efficient reading and writing operations for spreadsheet files, including support for CSV
  format.
  Unsupported functions include concurrent writing to a single file, reading and writing image macros, etc.

## Choosing Write Operations

- **Q:** When writing to spreadsheet, when should I choose the fill mode and when should I choose direct writing?
- **A:** For complex export content, it is recommended to use template filling; for simple format scenarios, direct
  writing is more efficient.

## Lombok Annotations

- **Q:** What is the role of Lombok annotations in using FesodSheet?
- **A:** Commonly used Lombok annotations in entity classes such as `@Getter`, `@Setter`, `@EqualsAndHashCode` are used
  to automatically generate getter, setter methods, equals, and hashCode methods. If you do not want to use these
  auto-generated methods, you can implement them yourself.

## Field Matching

- **Q:** How to solve the problem of some fields not being read or written correctly?
- **A:** Ensure that the entity class fields follow the camel case naming convention, avoid using
  `@Accessors(chain = true)`, and recommend using `@Builder` instead. Also, ensure that the entity class uses the
  `@ExcelProperty` annotation to mark the fields participating in reading and writing.

## Compatibility Issues

- **Q:** What should I do if I encounter compatibility issues when using FesodSheet?
- **A:** Common compatibility issues include `NoSuchMethodException`, `ClassNotFoundException`, `NoClassDefFoundError`,
  etc., usually caused by jar conflicts. It is recommended to check and clean up dependencies in the project to ensure
  that the version of FesodSheet used is compatible with other libraries in the project.

## Online Deployment

- **Q:** Why are there issues in the online environment when it runs fine locally?
- **A:** In most cases, this is due to the lack of necessary font libraries in the online environment. You can solve
  this problem by installing font libraries (such as `dejavu-sans-fonts` and `fontconfig`) or enabling memory processing
  mode.

## Concurrent Reading

- **Q:** Why shouldn't the Listener be managed by Spring?
- **A:** Listeners should not be managed by Spring because this would cause the Listener to become a singleton, which
  may lead to data confusion when reading files concurrently. A new Listener instance should be created each time a file
  is read.

## Performance Optimization

- **Q:** For large files over 10M, what reading strategies does FesodSheet provide?
- **A:** FesodSheet supports default large file processing strategies, as well as customizable high-speed modes and
  optimization settings for high concurrency and super large files.

## Writing and Format Setting

- **Q:** How to set cell formats?
- **A:** You can set cell formats by using annotations such as `@ContentStyle` on entity class properties, for example,
  numeric formats, date formats, etc.

## Export Issues

- **Q:** How to resolve the issue of spreadsheet files exported cannot be opened or prompt for repair?
- **A:** This is usually caused by frontend frameworks or backend interceptors modifying the file stream. It is
  recommended to test local exports first, ensure the backend logic is correct, and then investigate frontend and
  network-related issues.

## Large File Reading Optimization

- **Q:** How does FesodSheet optimize memory usage when reading large files?
- **A:** FesodSheet automatically determines the processing method for large files. For files with shared strings
  exceeding 5MB, a file storage strategy is used to reduce memory usage. You can enable ultra-fast mode by setting the
  `readCache` parameter, but this will increase memory consumption.

## Concurrent Processing

- **Q:** How to efficiently read spreadsheet files in a high-concurrency environment?
- **A:** In a high-concurrency environment, you can optimize reading performance using `SimpleReadCacheSelector`. By
  setting the `maxUseMapCacheSize` and `maxCacheActivateBatchCount` parameters, you can control the cache strategy for
  shared strings, improve hit rates, and reduce file read delays.

## Field Mapping

- **Q:** How to handle cases where entity class fields do not match spreadsheet column names?
- **A:** You can use the `@ExcelProperty` annotation to specify the correspondence between entity class fields and
  spreadsheet column names. For example:

  ```java
  @ExcelProperty("Name")
  private String name;
  ```

## Data Validation

- **Q:** How to validate data when reading spreadsheet data?
- **A:** Data validation logic can be implemented in the `ReadListener`. For example:

  ```java
  public class DataValidatorListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getName() == null || data.getName().isEmpty()) {
              throw new RuntimeException("Name cannot be empty");
          }
          // Process data
      }
  }
  ```

## Custom Styles

- **Q:** How to customize cell styles?
- **A:** You can customize cell styles by implementing the `WriteHandler` interface. For example:

  ```java
  public class CustomCellStyleWriteHandler extends AbstractCellStyleStrategy {
    
      @Override
      public int order() {
          // Customize the execution order
          return OrderConstant.SHEET_ORDER;
      }

      @Override
      protected void setHeadCellStyle(CellWriteHandlerContext context) {
          // Customize the head cell style
      }
  
      @Override
      protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
          CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
          style.setFillForegroundColor(IndexedColors.RED.getIndex());
          style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          cell.setCellStyle(style);
      }
  }
  ```

## Fill Mode

- **Q:** How to solve the issue of fields not being replaced in fill mode?
- **A:** Using the `inMemory(true)` parameter can ensure correct field replacement. For example:

  ```java
  FesodSheet.write(fileName, DemoData.class).inMemory(true).sheet("Template").doWrite(fillData());
  ```

## Error Handling

- **Q:** How to handle exceptions thrown during the reading process?
- **A:** Exceptions can be caught and handled in the `ReadListener`. For example:

  ```java
  public class ErrorHandlingListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          try {
              // Process data
          } catch (Exception e) {
              // Handle exception
          }
      }
  }
  ```

## Dependency Conflict

- **Q:** How to resolve dependency conflict issues?
- **A:** Common dependency conflicts include POI, ehcache, commons-io, etc. It is recommended to check the dependency
  tree in the project, ensure that the versions used are compatible with FesodSheet. You can use the Maven
  `dependency:tree` command to view the dependency tree.

## Performance Monitoring

- **Q:** How to monitor the performance of FesodSheet?
- **A:** You can monitor the performance of FesodSheet by enabling debug logging. For example:

  ```java
  LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
  ch.qos.logback.classic.Logger logger = lc.getLogger("org.apache.fesod");
  logger.setLevel(Level.DEBUG);
  ```

## Multi-Sheet Reading

- **Q:** How to monitor the performance of FesodSheet?
- **A:** You can monitor the performance of FesodSheet by enabling debug logging. For example:

  ```java
  FesodSheet.read(file, MultipleSheetsData.class, new MultipleSheetsListener()).doReadAll();
  ```

  Alternatively, you can get information on all sheets before reading:

  ```java
  ExcelReader excelReader = FesodSheet.read(file, MultipleSheetsData.class, multipleSheetsListener).build();
  List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
  for (ReadSheet readSheet : sheets) {
      excelReader.read(readSheet);
  }
  excelReader.finish();
  ```

## Get Total Rows

- **Q:** How to get the total number of rows in a spreadsheet file?
- **A:** You can use `analysisContext.readSheetHolder().getApproximateTotalRowNumber()` method in the listener to get an
  approximate number of rows. For example:

  ```java
  @Override
  public void doAfterAllAnalysed(AnalysisContext context) {
      int totalRows = context.readSheetHolder().getApproximateTotalRowNumber();
      System.out.println("Total rows: " + totalRows);
  }
  ```

## Memory Mode

- **Q:** How to use memory mode to process spreadsheet files?
- **A:** Memory mode is suitable for processing smaller files and can significantly improve processing speed. For
  example:

  ```java
  FesodSheet.write(fileName, DemoData.class)
      .inMemory(Boolean.TRUE)
      .sheet("Template")
      .doWrite(data());
  ```

## Read CSV Files

- **Q:** How to read CSV files and modify the delimiter?
- **A:** You can modify the delimiter of CSV files by setting `CsvFormat`. For example:

  ```java
  FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv().delimiter(CsvConstant.UNICODE_EMPTY).doReadSync();
  ```

## Custom Read Listener

- **Q:** How to customize the read listener?
- **A:** You can inherit the `AnalysisEventListener` class and implement your own logic. For example:

  ```java
  public class CustomReadListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          // Process data
      }

      @Override
      public void doAfterAllAnalysed(AnalysisContext context) {
          // Operations after all data is read
      }
  }
  ```

## Ignore Unannotated Fields During Reading

- **Q:** How to ignore fields not annotated with `@ExcelProperty` during reading?
- **A:** Add the `@ExcelIgnoreUnannotated` annotation at the top of the class. For example:

  ```java
  @Data
  @ExcelIgnoreUnannotated
  public class DemoData {
      @ExcelProperty("Name")
      private String name;
  }
  ```

## Set Header Style During Export

- **Q:** How to set header style during export?
- **A:** You can customize the header style by implementing the `WriteHandler` interface. For example:

  ```java
  public class CustomHeadStyleWriteHandler extends AbstractCellStyleStrategy {
  
      @Override
      public int order() {
          // Customize the execution order
          return OrderConstant.SHEET_ORDER;
      }
  
      @Override
      protected void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
          CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
          style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
          style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          cell.setCellStyle(style);
      }
  
      @Override
      protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
          // Customize the content cell style
      }
  }
  ```

## Set Cell Data Format During Export

- **Q:** How to set the data format of cells during export?
- **A:** You can set the data format by using the `@ContentStyle` annotation on entity class properties. For example:

  ```java
  @ExcelProperty("Amount")
  @ContentStyle(dataFormat = 4) // 4 corresponds to currency format
  private Double amount;
  ```

## Merge Cells During Export

- **Q:** How to merge cells during export?
- **A:** You can customize the logic for merging cells by implementing the `WriteHandler` interface. For example:

  ```java
  public class MergeCellWriteHandler implements WriteHandler {
      @Override
      public void sheet(Sheet sheet, Map<Integer, List<CellRangeAddress>> mergedRegions, AnalysisContext context) {
          sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2)); // Merge from row 1, column 1 to column 3
      }
  }
  ```

## Set Font During Export

- **Q:** How to set the font of cells during export?
- **A:** You can set the font by creating a `Font` object and applying it to the `CellStyle`. For example:

  ```java
  public class CustomFontWriteHandler extends AbstractCellStyleStrategy {
  
      @Override
      public int order() {
          // Customize the execution order
          return OrderConstant.SHEET_ORDER;
      }

      @Override
      protected void setHeadCellStyle(CellWriteHandlerContext context) {
          // Customize the head cell style
      }
  
      @Override
      protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
          Workbook workbook = cell.getSheet().getWorkbook();
          Font font = workbook.createFont();
          font.setFontName("Arial");
          font.setBold(true);
          CellStyle style = workbook.createCellStyle();
          style.setFont(font);
          cell.setCellStyle(style);
      }
  }
  ```

## Handle Null Values During Reading

- **Q:** How to handle null values during reading?
- **A:** You can handle null values in the `ReadListener`. For example:

  ```java
  public class NullValueHandlerListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getName() == null) {
              data.setName("Default Value");
          }
          // Process data
      }
  }
  ```

## Data Filtering During Reading

- **Q:** How to filter data during reading?
- **A:** You can implement filtering logic in the `ReadListener`. For example:

  ```java
  public class DataFilterListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getAmount() > 1000) {
              // Save or process data
          }
      }
  }
  ```

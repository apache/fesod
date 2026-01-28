---
id: 'faq'
title: '常见问题'
---

# 常见问题

本章节介绍使用 FesodSheet 时可能遇到的常见问题。

## 功能限制

- **Q:** FesodSheet 支持哪些功能？有哪些不支持的功能？
- **A:** FesodSheet 支持电子表格文件的高效读写操作，包括CSV格式的支持。不支持的功能包括单个文件的并发写入、读取图片宏等。

## 写操作的选择

- **Q:** 在写电子表格时，何时选择填充方式，何时选择直接写入？
- **A:** 对于格式复杂的导出内容，推荐使用模板填充；对于格式简单的场景，直接写入更为高效。

## Lombok注解

- **Q:** 使用 FesodSheet 时，实体类中的Lombok注解有何作用？
- **A:** 实体类中常用的 Lombok 注解如`@Getter`、`@Setter`、`@EqualsAndHashCode`
  用于自动生成 getter、setter 方法及 equals 和 hashCode 方法。如果不想使用这些自动生成的方法，可以自行实现。

## 字段匹配

- **Q:** 如何解决部分字段无法正确读取或写入的问题？
- **A:** 确保实体类字段遵循驼峰命名规则，避免使用`@Accessors(chain = true)`，推荐使用`@Builder`替代。另外，确保实体类中使用了
  `@ExcelProperty`注解标记参与读写的字段。

## 兼容性问题

- **Q:** 使用 FesodSheet 时遇到兼容性问题怎么办？
- **A:** 常见的兼容性问题包括`NoSuchMethodException`、`ClassNotFoundException`、`NoClassDefFoundError`
  等，通常是由jar包冲突引起。建议检查并清理项目中的依赖，确保使用的 FesodSheet 版本与项目中的其他库兼容。

## 线上部署

- **Q:** 本地运行正常，为何线上环境出现问题？
- **A:** 大多数情况下是由于线上环境缺少必要的字体库导致。可以通过安装字体库（如`dejavu-sans-fonts`和`fontconfig`
  ）或启用内存处理模式来解决问题。

## 并发读取

- **Q:** 为何不能将 Listener 交给 Spring 管理？
- **A:** Listener 不应被 Spring 管理，因为这会导致 Listener 变成单例模式，在并发读取文件时可能引发数据混淆问题。每次读取文件时应新建一个
  Listener 实例。

## 性能优化

- **Q:** 对于10M以上的大型文件，FesodSheet 提供了哪些读取策略？
- **A:** FesodSheet 支持默认的大文件处理策略，以及可以自定义的高速模式和针对高并发、超大文件的优化设置。

## 写入与格式设置

- **Q:** 如何设置单元格格式？
- **A:** 可以通过在实体类属性上使用`@ContentStyle`等注解来设置单元格的格式，例如数字格式、日期格式等。

## 导出问题

- **Q:** 导出的电子表格文件无法打开或提示需要修复，如何解决？
- **A:** 这通常是由于前端框架或后端拦截器修改了文件流导致。建议先测试本地导出，确保后端逻辑无误后再排查前端和网络相关的问题。

## 大文件读取优化

- **Q:** FesodSheet 在读取大文件时如何优化内存使用？
- **A:** FesodSheet 默认会自动判断大文件的处理方式。对于共享字符串超过 5MB 的文件，会使用文件存储策略，减少内存占用。可以通过设置
  `readCache`参数来开启极速模式，但这会增加内存消耗。

## 并发处理

- **Q:** 如何在高并发环境下高效读取电子表格文件？
- **A:** 在高并发环境下，可以使用`SimpleReadCacheSelector`来优化读取性能。通过设置`maxUseMapCacheSize`和
  `maxCacheActivateBatchCount`参数，可以控制共享字符串的缓存策略，提高命中率，减少文件读取的延迟。

## 字段映射

- **Q:** 如何处理实体类字段与电子表格列名不一致的情况？
- **A:** 可以使用`@ExcelProperty`注解来指定实体类字段与电子表格列名的对应关系。例如：

  ```java
  @ExcelProperty("姓名")
  private String name;
  ```

## 数据校验

- **Q:** 如何在读取电子表格数据时进行校验？
- **A:** 可以在`ReadListener`中实现数据校验逻辑。例如：

  ```java
  public class DataValidatorListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getName() == null || data.getName().isEmpty()) {
              throw new RuntimeException("姓名不能为空");
          }
          // 处理数据
      }
  }
  ```

## 自定义样式

- **Q:** 如何自定义单元格样式？
- **A:** 可以通过实现`WriteHandler`接口来自定义单元格样式。例如：

  ```java
  public class CustomCellStyleWriteHandler extends AbstractCellStyleStrategy {
  
      @Override
      public int order() {
          // 自定义执行顺序
          return OrderConstant.SHEET_ORDER;
      }

      @Override
      protected void setHeadCellStyle(CellWriteHandlerContext context) {
          // 自定义表头单元格样式
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

## 填充模式

- **Q:** 如何在填充模式下解决字段未替换的问题？
- **A:** 使用`inMemory(true)`参数可以确保字段正确替换。例如：

  ```java
  FesodSheet.write(fileName, DemoData.class).inMemory(true).sheet("模板").doWrite(fillData());
  ```

## 错误处理

- **Q:** 如何处理读取过程中抛出的异常？
- **A:** 可以在`ReadListener`中捕获并处理异常。例如：

  ```java
  public class ErrorHandlingListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          try {
              // 处理数据
          } catch (Exception e) {
              // 处理异常
          }
      }
  }
  ```

## 依赖冲突

- **Q:** 如何解决依赖冲突问题？
- **A:** 常见的依赖冲突包括 POI、ehcache 和 commons-io 等。建议检查项目中的依赖树，确保使用的版本与 FesodSheet 兼容。可以通过
  Maven 的
  `dependency:tree`命令查看依赖树。

## 性能监控

- **Q:** 如何监控 FesodSheet 的性能？
- **A:** 可以通过开启调试日志来监控 FesodSheet 的性能。例如：

  ```java
  LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
  ch.qos.logback.classic.Logger logger = lc.getLogger("org.apache.fesod");
  logger.setLevel(Level.DEBUG);
  ```

## 多Sheet读取

- **Q:** 如何读取包含多个 Sheet 的电子表格文件？
- **A:** 可以使用`MultipleSheetsListener`来处理多 Sheet 的读取。例如：

  ```java
  FesodSheet.read(file, MultipleSheetsData.class, new MultipleSheetsListener()).doReadAll();
  ```

  或者，可以在读取前获取所有Sheet的信息：

  ```java
  ExcelReader excelReader = FesodSheet.read(file, MultipleSheetsData.class, multipleSheetsListener).build();
  List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
  for (ReadSheet readSheet : sheets) {
      excelReader.read(readSheet);
  }
  excelReader.finish();
  ```

## 获取总行数

- **Q:** 如何获取电子表格文件的总行数？
- **A:** 可以在监听器中使用`analysisContext.readSheetHolder().getApproximateTotalRowNumber()`方法获取大致的行数。例如：

  ```java
  @Override
  public void doAfterAllAnalysed(AnalysisContext context) {
      int totalRows = context.readSheetHolder().getApproximateTotalRowNumber();
      System.out.println("总行数: " + totalRows);
  }
  ```

## 内存模式

- **Q:** 如何使用内存模式处理电子表格文件？
- **A:** 内存模式适合处理较小的文件，可以显著提高处理速度。例如：

  ```java
  FesodSheet.write(fileName, DemoData.class)
      .inMemory(Boolean.TRUE)
      .sheet("模板")
      .doWrite(data());
  ```

## 读取CSV文件

- **Q:** 如何读取 CSV 文件并修改分隔符？
- **A:** 可以通过设置`csv()`中的分隔符来读取 CSV 文件。例如：

  ```java
  FesodSheet.read(csvFile, DemoData.class, new DemoDataListener())
            .csv().delimiter(CsvConstant.UNICODE_EMPTY).doReadSync();
  ```

## 自定义读取监听器

- **Q:** 如何自定义读取监听器？
- **A:** 可以继承`AnalysisEventListener`类并实现自己的逻辑。例如：

  ```java
  public class CustomReadListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          // 处理数据
      }

      @Override
      public void doAfterAllAnalysed(AnalysisContext context) {
          // 所有数据读取完成后执行的操作
      }
  }
  ```

## 读取时忽略未标注的字段

- **Q:** 如何在读取时忽略未标注`@ExcelProperty`的字段？
- **A:** 在类的最上面加入`@ExcelIgnoreUnannotated`注解。例如：

  ```java
  @Data
  @ExcelIgnoreUnannotated
  public class DemoData {
      @ExcelProperty("姓名")
      private String name;
  }
  ```

## 导出时设置表头样式

- **Q:** 如何在导出时设置表头样式？
- **A:** 可以通过实现`WriteHandler`接口来自定义表头样式。例如：

  ```java
  public class CustomHeadStyleWriteHandler extends AbstractCellStyleStrategy {
  
       @Override
      public int order() {
          // 自定义执行顺序
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
          // 自定义内容单元格样式
      }
  }
  ```

## 导出时设置单元格数据格式

- **Q:** 如何在导出时设置单元格的数据格式？
- **A:** 可以在实体类属性上使用`@ContentStyle`注解来设置数据格式。例如：

  ```java
  @ExcelProperty("金额")
  @ContentStyle(dataFormat = 4) // 4对应货币格式
  private Double amount;
  ```

## 导出时合并单元格

- **Q:** 如何在导出时合并单元格？
- **A:** 可以通过实现`WriteHandler`接口来自定义合并单元格的逻辑。例如：

  ```java
  public class MergeCellWriteHandler implements WriteHandler {
      @Override
      public void sheet(Sheet sheet, Map<Integer, List<CellRangeAddress>> mergedRegions, AnalysisContext context) {
          sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2)); // 合并第1行第1列到第3列
      }
  }
  ```

## 导出时设置字体

- **Q:** 如何在导出时设置单元格的字体？
- **A:** 可以通过创建`Font`对象并应用到`CellStyle`中来设置字体。例如：

  ```java
  public class CustomFontWriteHandler extends AbstractCellStyleStrategy {
  
      @Override
      public int order() {
          // 自定义执行顺序
          return OrderConstant.SHEET_ORDER;
      }

      @Override
      protected void setHeadCellStyle(CellWriteHandlerContext context) {
          // 自定义表头单元格样式
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

## 读取时处理空值

- **Q:** 如何在读取时处理空值？
- **A:** 可以在`ReadListener`中处理空值。例如：

  ```java
  public class NullValueHandlerListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getName() == null) {
              data.setName("默认值");
          }
          // 处理数据
      }
  }
  ```

## 读取时过滤数据

- **Q:** 如何在读取时过滤数据？
- **A:** 可以在`ReadListener`中实现过滤逻辑。例如：

  ```java
  public class DataFilterListener extends AnalysisEventListener<DemoData> {
      @Override
      public void invoke(DemoData data, AnalysisContext context) {
          if (data.getAmount() > 1000) {
              // 保存或处理数据
          }
      }
  }
  ```

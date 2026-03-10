---
id: 'ods'
title: 'ODS'
---

# ODS 文件格式支持

本章介绍如何使用 Fesod 读写 ODS（OpenDocument Spreadsheet）文件。

## 概述

ODS（OpenDocument Spreadsheet）是由 OASIS 定义的开放标准电子表格文件格式，广泛应用于：

- LibreOffice Calc
- Apache OpenOffice Calc
- Google 表格（导出格式）
- 许多其他开源办公套件

Fesod 提供了对 ODS 文件的完整读写支持，使用与其他格式（XLSX、XLS、CSV）相同的 API。

## 读取 ODS 文件

### 基本读取

读取 ODS 文件遵循与读取其他电子表格格式相同的模式：

```java
@Test
public void readOds() {
    String fileName = "path/to/demo.ods";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet()
            .doRead();
}
```

### 显式指定类型

当从没有文件扩展名的 InputStream 读取时，可以显式指定文件类型：

```java
@Test
public void readOdsFromStream() {
    InputStream inputStream = getOdsInputStream();

    FesodSheet.read(inputStream, DemoData.class, new DemoDataListener())
            .excelType(ExcelTypeEnum.ODS)
            .sheet()
            .doRead();
}
```

### 多工作表

ODS 文件支持多个工作表，读取方式与其他格式相同：

```java
@Test
public void readMultipleSheets() {
    String fileName = "path/to/demo.ods";

    // 读取所有工作表
    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .doReadAll();

    // 或按索引读取特定工作表
    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
            .sheet(0)  // 第一个工作表
            .doRead();
}
```

## 写入 ODS 文件

### 基本写入

写入 ODS 文件非常简单：

```java
@Test
public void writeOds() {
    String fileName = "path/to/output.ods";
    List<DemoData> dataList = generateData();

    FesodSheet.write(fileName, DemoData.class)
            .sheet("Sheet1")
            .doWrite(dataList);
}
```

### 显式指定类型

可以显式指定输出类型：

```java
@Test
public void writeOdsExplicit() {
    String fileName = "path/to/output.ods";
    List<DemoData> dataList = generateData();

    FesodSheet.write(fileName, DemoData.class)
            .excelType(ExcelTypeEnum.ODS)
            .sheet("MySheet")
            .doWrite(dataList);
}
```

### 写入到 OutputStream

当写入到 OutputStream 时，显式指定文件类型：

```java
@Test
public void writeOdsToStream() throws IOException {
    OutputStream outputStream = new FileOutputStream("output.ods");
    List<DemoData> dataList = generateData();

    FesodSheet.write(outputStream, DemoData.class)
            .excelType(ExcelTypeEnum.ODS)
            .sheet("Sheet1")
            .doWrite(dataList);
}
```

## 支持的功能

| 功能 | 支持状态 |
|------|---------|
| 基本读写 | ✅ 完全支持 |
| 多工作表 | ✅ 完全支持 |
| 字符串数据 | ✅ 完全支持 |
| 数值数据 | ✅ 完全支持 |
| 日期/时间数据 | ✅ 完全支持 |
| 布尔数据 | ✅ 完全支持 |
| 公式 | ⚠️ 基本支持 |
| 样式 | ⚠️ 基本支持 |
| 图片 | ⚠️ 有限支持 |
| 批注 | ⚠️ 有限支持 |
| 加密 | ❌ 不支持 |

## 依赖

ODS 支持通过 Apache ODF Toolkit 提供。当您使用 Fesod 时，该依赖会自动包含：

```xml
<dependency>
    <groupId>org.odftoolkit</groupId>
    <artifactId>odfdom-java</artifactId>
</dependency>
```

## 注意事项

1. **文件检测**：ODS 文件通过 `.ods` 扩展名自动检测。当从流读取时，使用 `excelType(ExcelTypeEnum.ODS)` 显式指定格式。

2. **性能**：对于典型用例，ODS 读写性能与 XLSX 相当。

3. **兼容性**：Fesod 创建的文件与 LibreOffice、OpenOffice 和其他支持 ODF 标准的应用程序兼容。

4. **公式语法**：ODS 使用与 Excel 不同的公式语法。跨格式的公式转换不会自动执行。

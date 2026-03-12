---
sidebar_position: 1
title: 从 FastExcel 迁移
description: 从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 的完整指南
keywords: [fesod, 迁移, fastexcel, apache, excel, 升级]
---

# 迁移指南:从 FastExcel 迁移到 Apache Fesod (Incubating)

## 概述

本指南为从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 提供全面的迁移路线。Apache Fesod (Incubating) 是该项目的演进版本，现已成为 Apache 软件基金会(孵化中)的一部分，提供同样的高性能 Excel 处理能力，并具有更强的社区支持和长期可持续性。

### 为什么要迁移?

- **Apache 软件基金会支持**：Apache Fesod (Incubating) 现已成为 Apache 软件基金会的一部分，确保长期维护和社区驱动的开发
- **无缝过渡**：API 几乎完全相同，只需最少的代码更改
- **统一品牌**：在 Apache Fesod (Incubating) 下统一的命名规范
- **持续创新**：在 Apache 治理下获得未来的增强和功能
- **向后兼容**：暂时保留已废弃的别名（FastExcel）以便逐步迁移

### 迁移范围

本次迁移主要涉及:

1. 更新 Maven/Gradle 依赖
2. 将已废弃的类名替换为 FesodSheet
3. 更新包导入
4. 通过全面测试验证功能

核心 API、注解和处理逻辑保持不变，确保低风险的迁移路径。

---

## 迁移步骤

### 步骤 1：更新依赖

将现有依赖替换为 Apache Fesod (Incubating)：

| 来源                              | GroupId          | ArtifactId  | 版本                |
|---------------------------------|------------------|-------------|-------------------|
| **cn.idev FastExcel**           | cn.idev.excel    | fastexcel   | 1.3.0             |
| **Apache Fesod (Incubating)** ✅ | org.apache.fesod | fesod-sheet | 2.0.1-incubating+ |

**Maven 配置：**

修改前：

```xml
<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>1.3.0</version>
</dependency>
```

修改后：

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.0.1-incubating</version>
</dependency>
```

**Gradle 配置：**

修改前：

```groovy
implementation 'cn.idev.excel:fastexcel:1.3.0'
```

修改后：

```gradle
implementation 'org.apache.fesod:fesod-sheet:2.0.1-incubating'
```

> **注意**: `fesod-sheet` 模块是 Excel/CSV 处理的核心模块。它会自动引入必要的依赖（`fesod-common` 和 `fesod-shaded`）。

### 步骤 2：更新包导入

更新所有 import 语句以使用新的 Apache Fesod (Incubating) 包结构。

**核心入口类替换：**

| 修改前                                      | 修改后                                               |
|------------------------------------------|---------------------------------------------------|
| `import cn.idev.excel.FastExcel;`        | `import org.apache.fesod.sheet.FastExcel;`        |
| `import cn.idev.excel.FastExcelFactory;` | `import org.apache.fesod.sheet.FastExcelFactory;` |
| `import cn.idev.excel.ExcelWriter;`      | `import org.apache.fesod.sheet.ExcelWriter;`      |
| `import cn.idev.excel.ExcelReader;`      | `import org.apache.fesod.sheet.ExcelReader;`      |

> **注意**: 在此阶段之后，`FastExcel.read(...)` 和 `FastExcelFactory.writerSheet(...)`
> 依然可以编译通过，它们会解析到 Fesod 中带有 `@Deprecated` 标记的桥接类。
> 在 “步骤 3” 中我们会将它们彻底替换为规范的 FesodSheet 类。

**读取 API 替换：**

| 修改前                                                          | 修改后                                                                   |
|--------------------------------------------------------------|-----------------------------------------------------------------------|
| `import cn.idev.excel.read.listener.ReadListener;`           | `import org.apache.fesod.sheet.read.listener.ReadListener;`           |
| `import cn.idev.excel.read.listener.PageReadListener;`       | `import org.apache.fesod.sheet.read.listener.PageReadListener;`       |
| `import cn.idev.excel.read.metadata.ReadSheet;`              | `import org.apache.fesod.sheet.read.metadata.ReadSheet;`              |
| `import cn.idev.excel.read.metadata.ReadWorkbook;`           | `import org.apache.fesod.sheet.read.metadata.ReadWorkbook;`           |
| `import cn.idev.excel.read.metadata.ReadBasicParameter;`     | `import org.apache.fesod.sheet.read.metadata.ReadBasicParameter;`     |
| `import cn.idev.excel.read.builder.ExcelReaderBuilder;`      | `import org.apache.fesod.sheet.read.builder.ExcelReaderBuilder;`      |
| `import cn.idev.excel.read.builder.ExcelReaderSheetBuilder;` | `import org.apache.fesod.sheet.read.builder.ExcelReaderSheetBuilder;` |
| `import cn.idev.excel.context.AnalysisContext;`              | `import org.apache.fesod.sheet.context.AnalysisContext;`              |
| `import cn.idev.excel.event.SyncReadListener;`               | `import org.apache.fesod.sheet.event.SyncReadListener;`               |

**写入 API 替换：**

| 修改前                                                           | 修改后                                                                    |
|---------------------------------------------------------------|------------------------------------------------------------------------|
| `import cn.idev.excel.write.metadata.WriteSheet;`             | `import org.apache.fesod.sheet.write.metadata.WriteSheet;`             |
| `import cn.idev.excel.write.metadata.WriteWorkbook;`          | `import org.apache.fesod.sheet.write.metadata.WriteWorkbook;`          |
| `import cn.idev.excel.write.metadata.WriteTable;`             | `import org.apache.fesod.sheet.write.metadata.WriteTable;`             |
| `import cn.idev.excel.write.metadata.WriteBasicParameter;`    | `import org.apache.fesod.sheet.write.metadata.WriteBasicParameter;`    |
| `import cn.idev.excel.write.builder.ExcelWriterBuilder;`      | `import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;`      |
| `import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;` |
| `import cn.idev.excel.write.builder.ExcelWriterTableBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterTableBuilder;` |

**写入拦截器替换：**

| 修改前                                                                   | 修改后                                                                            |
|-----------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `import cn.idev.excel.write.handler.WriteHandler;`                    | `import org.apache.fesod.sheet.write.handler.WriteHandler;`                    |
| `import cn.idev.excel.write.handler.SheetWriteHandler;`               | `import org.apache.fesod.sheet.write.handler.SheetWriteHandler;`               |
| `import cn.idev.excel.write.handler.CellWriteHandler;`                | `import org.apache.fesod.sheet.write.handler.CellWriteHandler;`                |
| `import cn.idev.excel.write.handler.RowWriteHandler;`                 | `import org.apache.fesod.sheet.write.handler.RowWriteHandler;`                 |
| `import cn.idev.excel.write.handler.WorkbookWriteHandler;`            | `import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;`            |
| `import cn.idev.excel.write.handler.context.CellWriteHandlerContext;` | `import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;` |

**注解替换：**

| 修改前                                                              | 修改后                                                                       |
|------------------------------------------------------------------|---------------------------------------------------------------------------|
| `import cn.idev.excel.annotation.ExcelProperty;`                 | `import org.apache.fesod.sheet.annotation.ExcelProperty;`                 |
| `import cn.idev.excel.annotation.ExcelIgnore;`                   | `import org.apache.fesod.sheet.annotation.ExcelIgnore;`                   |
| `import cn.idev.excel.annotation.ExcelIgnoreUnannotated;`        | `import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;`        |
| `import cn.idev.excel.annotation.format.DateTimeFormat;`         | `import org.apache.fesod.sheet.annotation.format.DateTimeFormat;`         |
| `import cn.idev.excel.annotation.format.NumberFormat;`           | `import org.apache.fesod.sheet.annotation.format.NumberFormat;`           |
| `import cn.idev.excel.annotation.write.style.ColumnWidth;`       | `import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;`       |
| `import cn.idev.excel.annotation.write.style.HeadStyle;`         | `import org.apache.fesod.sheet.annotation.write.style.HeadStyle;`         |
| `import cn.idev.excel.annotation.write.style.ContentStyle;`      | `import org.apache.fesod.sheet.annotation.write.style.ContentStyle;`      |
| `import cn.idev.excel.annotation.write.style.HeadFontStyle;`     | `import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;`     |
| `import cn.idev.excel.annotation.write.style.ContentFontStyle;`  | `import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;`  |
| `import cn.idev.excel.annotation.write.style.HeadRowHeight;`     | `import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;`     |
| `import cn.idev.excel.annotation.write.style.ContentRowHeight;`  | `import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;`  |
| `import cn.idev.excel.annotation.write.style.OnceAbsoluteMerge;` | `import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;` |
| `import cn.idev.excel.annotation.write.style.ContentLoopMerge;`  | `import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;`  |

**转换器替换：**

| 修改前                                                      | 修改后                                                               |
|----------------------------------------------------------|-------------------------------------------------------------------|
| `import cn.idev.excel.converters.Converter;`             | `import org.apache.fesod.sheet.converters.Converter;`             |
| `import cn.idev.excel.converters.AutoConverter;`         | `import org.apache.fesod.sheet.converters.AutoConverter;`         |
| `import cn.idev.excel.converters.ReadConverterContext;`  | `import org.apache.fesod.sheet.converters.ReadConverterContext;`  |
| `import cn.idev.excel.converters.WriteConverterContext;` | `import org.apache.fesod.sheet.converters.WriteConverterContext;` |

**枚举替换：**

| 修改前                                                       | 修改后                                                                |
|-----------------------------------------------------------|--------------------------------------------------------------------|
| `import cn.idev.excel.enums.CellDataTypeEnum;`            | `import org.apache.fesod.sheet.enums.CellDataTypeEnum;`            |
| `import cn.idev.excel.enums.CellExtraTypeEnum;`           | `import org.apache.fesod.sheet.enums.CellExtraTypeEnum;`           |
| `import cn.idev.excel.enums.WriteDirectionEnum;`          | `import org.apache.fesod.sheet.enums.WriteDirectionEnum;`          |
| `import cn.idev.excel.enums.poi.HorizontalAlignmentEnum;` | `import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;` |
| `import cn.idev.excel.enums.poi.BorderStyleEnum;`         | `import org.apache.fesod.sheet.enums.poi.BorderStyleEnum;`         |
| `import cn.idev.excel.enums.poi.FillPatternTypeEnum;`     | `import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;`     |

**异常与元信息替换：**

| 修改前                                                            | 修改后                                                                     |
|----------------------------------------------------------------|-------------------------------------------------------------------------|
| `import cn.idev.excel.exception.ExcelAnalysisException;`       | `import org.apache.fesod.sheet.exception.ExcelAnalysisException;`       |
| `import cn.idev.excel.exception.ExcelAnalysisStopException;`   | `import org.apache.fesod.sheet.exception.ExcelAnalysisStopException;`   |
| `import cn.idev.excel.exception.ExcelCommonException;`         | `import org.apache.fesod.sheet.exception.ExcelCommonException;`         |
| `import cn.idev.excel.exception.ExcelGenerateException;`       | `import org.apache.fesod.sheet.exception.ExcelGenerateException;`       |
| `import cn.idev.excel.metadata.data.WriteCellData;`            | `import org.apache.fesod.sheet.metadata.data.WriteCellData;`            |
| `import cn.idev.excel.metadata.data.ReadCellData;`             | `import org.apache.fesod.sheet.metadata.data.ReadCellData;`             |
| `import cn.idev.excel.metadata.CellExtra;`                     | `import org.apache.fesod.sheet.metadata.CellExtra;`                     |
| `import cn.idev.excel.metadata.Head;`                          | `import org.apache.fesod.sheet.metadata.Head;`                          |
| `import cn.idev.excel.metadata.property.ExcelContentProperty;` | `import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;` |

**通配符兜底替换**

在完成上述所有具体替换后，扫描任何剩余的通配符包导入：

| 修改前                              | 修改后                              |
|----------------------------------|----------------------------------|
| `import cn.idev.excel.`          | `import org.apache.fesod.sheet.` |
| `import org.apache.fesod.excel.` | `import org.apache.fesod.sheet.` |

### 步骤 3：重命名入口类（强烈建议）

`FastExcel` 和 `FastExcelFactory` 在 Fesod 中仍然可以编译，但已被标记为 `@Deprecated` 弃用，
并且**将在未来的版本中被移除**。请将所有调用点替换为 `FesodSheet`。

**导入替换：**

| 修改前                                               | 修改后                                         |
|---------------------------------------------------|---------------------------------------------|
| `import org.apache.fesod.sheet.FastExcel;`        | `import org.apache.fesod.sheet.FesodSheet;` |
| `import org.apache.fesod.sheet.FastExcelFactory;` | `import org.apache.fesod.sheet.FesodSheet;` |

**调用入口类替换 - FastExcel**

| 修改前                       | 修改后                        |
|---------------------------|----------------------------|
| `FastExcel.read()`        | `FesodSheet.read()`        |
| `FastExcel.write()`       | `FesodSheet.write()`       |
| `FastExcel.writerSheet()` | `FesodSheet.writerSheet()` |
| `FastExcel.readSheet()`   | `FesodSheet.readSheet()`   |
| `FastExcel.writerTable()` | `FesodSheet.writerTable()` |

**调用入口类替换 — FastExcelFactory**

FastExcel 1.3 曾提供了 `FastExcelFactory` 作为第二个入口类，其 API 表面完全相同。它的所有静态方法都可以直接映射到 FesodSheet：

| 修改前                              | 修改后                        |
|----------------------------------|----------------------------|
| `FastExcelFactory.read()`        | `FesodSheet.read()`        |
| `FastExcelFactory.write()`       | `FesodSheet.write()`       |
| `FastExcelFactory.writerSheet()` | `FesodSheet.writerSheet()` |
| `FastExcelFactory.readSheet()`   | `FesodSheet.readSheet()`   |
| `FastExcelFactory.writerTable()` | `FesodSheet.writerTable()` |

**类型引用重命名**

如果 `FastExcel` 或 `FastExcelFactory` 作为类型名称（而不是调用入口）出现，也请重命名它们：

- 变量类型：`FastExcel x = ...` → `FesodSheet x = ...`
- 类字面量：`FastExcel.class` → `FesodSheet.class`

`ExcelWriter` 和 `ExcelReader` 无需重命名，保持相同的类名即可。

**CGLIB 类名替换（按需执行）**

此阶段仅适用于项目中包含**在运行时检查或断言生成的 CGLIB 类名**的代码，例如在单元测试或特定的序列化逻辑中。 搜索所有 `.java` 文件中的字符串 `ByFastExcelCGLIB`。
如果找到，请替换为 `ByFesodCGLIB`。 在 Fesod 中，命名策略定义在 `org.apache.fesod.sheet.util.BeanMapUtils.FesodSheetNamingPolicy` 中，其 `getTag()`
返回 `ByFesodCGLIB`。 如果没有文件引用 `ByFastExcelCGLIB`，请直接跳过此步骤。

---

## 迁移策略

### 渐进式迁移（推荐）

利用已废弃的别名类进行分阶段迁移。

**阶段 1：仅更新依赖**

- 将 Maven/Gradle 依赖更新为 Apache Fesod (Incubating)
- 继续使用 FastExcel 类(现为已废弃的别名)
- 仅更新包导入
- 运行全面测试以验证兼容性

**阶段 2：类名迁移**

- 逐步将已废弃的类替换为 FesodSheet
- 使用 IDE 重构工具进行批量重命名
- 逐个模块或逐个功能进行迁移
- 在整个过程中保持全面的测试覆盖

**阶段 3：清理**

- 删除所有对已废弃类的引用
- 解决废弃警告
- 更新文档和代码注释

**优势：**

- 通过增量更改降低风险
- 如果出现问题更容易回滚
- 对正在进行的开发影响最小
- 允许在每个阶段进行充分测试的时间

---

## 总结

由于高度的 API 兼容性和向后兼容的已废弃别名，从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 是一个直接的过程。主要工作涉及更新依赖声明和包导入，几乎不需要或不需要逻辑更改。

渐进式迁移策略得到临时已废弃别名（FastExcel、FastExcelFactory）的支持，允许团队按自己的节奏迁移，同时保持完整功能。

遵循本指南，组织可以无缝过渡到 Apache Fesod (Incubating)，并从 Apache 软件基金会生态系统的长期可持续性和社区支持中受益。

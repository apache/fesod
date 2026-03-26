---
sidebar_position: 1
title: From FastExcel
description: Complete migration guide for transitioning from cn.idev FastExcel to Apache Fesod (Incubating)
keywords: [fesod, migration, fastexcel, apache, excel, upgrade]
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

# Migration Guide: FastExcel to Apache Fesod (Incubating)

## Overview

This guide provides a comprehensive roadmap for migrating applications from cn.idev FastExcel library to Apache Fesod (Incubating). Apache Fesod (Incubating) is the evolution of this project, now under the Apache Software Foundation (Incubating), offering the same high-performance Excel processing capabilities with enhanced community support and long-term sustainability.

### Why Migrate?

- **Apache Software Foundation Support**: Apache Fesod (Incubating) is now part of the Apache Software Foundation, ensuring long-term maintenance and community-driven development
- **Seamless Transition**: The API remains virtually identical, requiring minimal code changes
- **Enhanced Branding**: Unified naming conventions under the Apache Fesod (Incubating) umbrella
- **Continued Innovation**: Access to future enhancements and features under active Apache governance
- **Backward Compatibility**: Deprecated aliases (FastExcel) are temporarily maintained for gradual migration

### Migration Scope

This migration primarily involves:

1. Updating Maven/Gradle dependencies
2. Replacing deprecated class names with FesodSheet
3. Updating package imports
4. Verifying functionality through comprehensive testing

The core API, annotations, and processing logic remain unchanged, ensuring a low-risk migration path.

---

## Migration Steps

### Step 1: Update Dependencies

Replace your existing dependency with Apache Fesod (Incubating):

| Source                          | GroupId          | ArtifactId  | Version           |
|---------------------------------|------------------|-------------|-------------------|
| **cn.idev FastExcel**           | cn.idev.excel    | fastexcel   | 1.3.0             |
| **Apache Fesod (Incubating)** ✅ | org.apache.fesod | fesod-sheet | 2.0.1-incubating+ |

**Maven:**

Before:

```xml
<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>1.3.0</version>
</dependency>
```

After:

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.0.1-incubating</version>
</dependency>
```

**Gradle:**

Before:

```groovy
implementation 'cn.idev.excel:fastexcel:1.3.0'
```

After:

```gradle
implementation 'org.apache.fesod:fesod-sheet:2.0.1-incubating'
```

> **Note**: The `fesod-sheet` module is the core module for Excel/CSV processing. It automatically includes the necessary dependencies (`fesod-common` and `fesod-shaded`).

### Step 2: Package Import Updates

Update all import statements to use the new Apache Fesod (Incubating) package structure.

**Core Entry Classes Replacements:**

| Before                                   | After                                             |
|------------------------------------------|---------------------------------------------------|
| `import cn.idev.excel.FastExcel;`        | `import org.apache.fesod.sheet.FastExcel;`        |
| `import cn.idev.excel.FastExcelFactory;` | `import org.apache.fesod.sheet.FastExcelFactory;` |
| `import cn.idev.excel.ExcelWriter;`      | `import org.apache.fesod.sheet.ExcelWriter;`      |
| `import cn.idev.excel.ExcelReader;`      | `import org.apache.fesod.sheet.ExcelReader;`      |

> **Note**: After this phase, `FastExcel.read(...)` and `FastExcelFactory.writerSheet(...)`
> still compile — they resolve to the `@Deprecated` bridge classes in Fesod.
> Step 3 replaces them with the canonical `FesodSheet` class.

**Read API Replacements:**

| Before                                                       | After                                                                 |
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

**Write API Replacements:**

| Before                                                        | After                                                                  |
|---------------------------------------------------------------|------------------------------------------------------------------------|
| `import cn.idev.excel.write.metadata.WriteSheet;`             | `import org.apache.fesod.sheet.write.metadata.WriteSheet;`             |
| `import cn.idev.excel.write.metadata.WriteWorkbook;`          | `import org.apache.fesod.sheet.write.metadata.WriteWorkbook;`          |
| `import cn.idev.excel.write.metadata.WriteTable;`             | `import org.apache.fesod.sheet.write.metadata.WriteTable;`             |
| `import cn.idev.excel.write.metadata.WriteBasicParameter;`    | `import org.apache.fesod.sheet.write.metadata.WriteBasicParameter;`    |
| `import cn.idev.excel.write.builder.ExcelWriterBuilder;`      | `import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;`      |
| `import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;` |
| `import cn.idev.excel.write.builder.ExcelWriterTableBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterTableBuilder;` |

**Write Handlers Replacements:**

| Before                                                                | After                                                                          |
|-----------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `import cn.idev.excel.write.handler.WriteHandler;`                    | `import org.apache.fesod.sheet.write.handler.WriteHandler;`                    |
| `import cn.idev.excel.write.handler.SheetWriteHandler;`               | `import org.apache.fesod.sheet.write.handler.SheetWriteHandler;`               |
| `import cn.idev.excel.write.handler.CellWriteHandler;`                | `import org.apache.fesod.sheet.write.handler.CellWriteHandler;`                |
| `import cn.idev.excel.write.handler.RowWriteHandler;`                 | `import org.apache.fesod.sheet.write.handler.RowWriteHandler;`                 |
| `import cn.idev.excel.write.handler.WorkbookWriteHandler;`            | `import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;`            |
| `import cn.idev.excel.write.handler.context.CellWriteHandlerContext;` | `import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;` |

**Annotations Replacements:**

| Before                                                           | After                                                                     |
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

**Converters Replacements:**

| Before                                                   | After                                                             |
|----------------------------------------------------------|-------------------------------------------------------------------|
| `import cn.idev.excel.converters.Converter;`             | `import org.apache.fesod.sheet.converters.Converter;`             |
| `import cn.idev.excel.converters.AutoConverter;`         | `import org.apache.fesod.sheet.converters.AutoConverter;`         |
| `import cn.idev.excel.converters.ReadConverterContext;`  | `import org.apache.fesod.sheet.converters.ReadConverterContext;`  |
| `import cn.idev.excel.converters.WriteConverterContext;` | `import org.apache.fesod.sheet.converters.WriteConverterContext;` |

**Enums Replacements:**

| Before                                                    | After                                                              |
|-----------------------------------------------------------|--------------------------------------------------------------------|
| `import cn.idev.excel.enums.CellDataTypeEnum;`            | `import org.apache.fesod.sheet.enums.CellDataTypeEnum;`            |
| `import cn.idev.excel.enums.CellExtraTypeEnum;`           | `import org.apache.fesod.sheet.enums.CellExtraTypeEnum;`           |
| `import cn.idev.excel.enums.WriteDirectionEnum;`          | `import org.apache.fesod.sheet.enums.WriteDirectionEnum;`          |
| `import cn.idev.excel.enums.poi.HorizontalAlignmentEnum;` | `import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;` |
| `import cn.idev.excel.enums.poi.BorderStyleEnum;`         | `import org.apache.fesod.sheet.enums.poi.BorderStyleEnum;`         |
| `import cn.idev.excel.enums.poi.FillPatternTypeEnum;`     | `import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;`     |

**Exceptions and Metadata Replacements:**

| Before                                                         | After                                                                   |
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

**Wildcard catch-all**

After all specific replacements above, scan for any remaining wildcard imports:

| Before                           | After                            |
|----------------------------------|----------------------------------|
| `import cn.idev.excel.`          | `import org.apache.fesod.sheet.` |
| `import org.apache.fesod.excel.` | `import org.apache.fesod.sheet.` |

### Step 3: Entry class rename (STRONGLY RECOMMENDED)

`FastExcel` and `FastExcelFactory` compile in Fesod but are `@Deprecated` and
**will be removed in a future release**. Replace all call sites with `FesodSheet`.

**Import Replacements:**

| Before                                            | After                                       |
|---------------------------------------------------|---------------------------------------------|
| `import org.apache.fesod.sheet.FastExcel;`        | `import org.apache.fesod.sheet.FesodSheet;` |
| `import org.apache.fesod.sheet.FastExcelFactory;` | `import org.apache.fesod.sheet.FesodSheet;` |

**Call Site Replacements - FastExcel**

| Before                    | After                      |
|---------------------------|----------------------------|
| `FastExcel.read()`        | `FesodSheet.read()`        |
| `FastExcel.write()`       | `FesodSheet.write()`       |
| `FastExcel.writerSheet()` | `FesodSheet.writerSheet()` |
| `FastExcel.readSheet()`   | `FesodSheet.readSheet()`   |
| `FastExcel.writerTable()` | `FesodSheet.writerTable()` |

**Call Site Replacements — FastExcelFactory**

FastExcel 1.3 shipped `FastExcelFactory` as a second entry class with an
identical API surface. All of its static methods map directly to `FesodSheet`:

| Before                           | After                      |
|----------------------------------|----------------------------|
| `FastExcelFactory.read()`        | `FesodSheet.read()`        |
| `FastExcelFactory.write()`       | `FesodSheet.write()`       |
| `FastExcelFactory.writerSheet()` | `FesodSheet.writerSheet()` |
| `FastExcelFactory.readSheet()`   | `FesodSheet.readSheet()`   |
| `FastExcelFactory.writerTable()` | `FesodSheet.writerTable()` |

**Type Reference Rename**

If `FastExcel` or `FastExcelFactory` appear as a **type name** (not a call site),
rename those too:

- Variable type: `FastExcel x = ...` → `FesodSheet x = ...`
- Class literal: `FastExcel.class` → `FesodSheet.class`

`ExcelWriter` and `ExcelReader` are **not renamed**, they keep the same class name.

**CGLIB class name (conditional)**

This phase only applies if the project contains code that **inspects or asserts
on generated CGLIB class names at runtime**, for example in tests or serialization logic.

Search all `.java` files for the string `ByFastExcelCGLIB`.
If found, replace with `ByFesodCGLIB`.

In Fesod, the naming policy is defined in
`org.apache.fesod.sheet.util.BeanMapUtils.FesodSheetNamingPolicy` and its
`getTag()` returns `ByFesodCGLIB`.

If no file references `ByFastExcelCGLIB`, skip this phase entirely.

---

## Migration Strategies

### Gradual Migration (Recommended)

Utilize the deprecated alias classes for a phased migration approach.

**Phase 1: Dependency Update Only**

- Update Maven/Gradle dependency to Apache Fesod (Incubating)
- Keep using FastExcel classes (now deprecated aliases)
- Update package imports only
- Run comprehensive tests to verify compatibility

**Phase 2: Class Name Migration**

- Progressively replace deprecated classes with FesodSheet
- Use IDE refactoring tools for bulk renaming
- Migrate module by module or feature by feature
- Maintain thorough test coverage throughout

**Phase 3: Cleanup**

- Remove all references to deprecated classes
- Resolve deprecation warnings
- Update documentation and code comments

**Benefits:**

- Lower risk through incremental changes
- Easier rollback if issues arise
- Minimal disruption to ongoing development
- Allows time for thorough testing at each phase

---

## Conclusion

Migrating from cn.idev FastExcel to Apache Fesod (Incubating) is a straightforward process due to the high degree of API compatibility and backward-compatible deprecated aliases. The primary effort involves updating dependency declarations and package imports, with minimal to no logic changes required.

The gradual migration strategy, supported by the temporary deprecated aliases (FastExcel, FastExcelFactory), allows teams to migrate at their own pace while maintaining full functionality.

By following this guide, organizations can seamlessly transition to Apache Fesod (Incubating) and benefit from the long-term sustainability and community support of the Apache Software Foundation ecosystem.

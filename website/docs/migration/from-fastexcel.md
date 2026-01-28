---
sidebar_position: 1
title: From FastExcel
description: Complete migration guide for transitioning from cn.idev FastExcel to Apache Fesod (Incubating)
keywords: [fesod, migration, fastexcel, apache, excel, upgrade]
---

# Migration Guide: FastExcel to Apache Fesod (Incubating)

## Overview

This guide provides a comprehensive roadmap for migrating applications from cn.idev FastExcel library to Apache Fesod (Incubating). Apache Fesod (Incubating) is the evolution of this project, now under the Apache Software Foundation (Incubating), offering the same high-performance Excel processing capabilities with enhanced community support and long-term sustainability.

### Why Migrate?

- **Apache Foundation Support**: Apache Fesod (Incubating) is now part of the Apache Software Foundation, ensuring long-term maintenance and community-driven development
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

| Source | GroupId | ArtifactId | Version |
|--------|---------|------------|----------|
| **cn.idev FastExcel** | cn.idev.excel | fastexcel | 1.3.0 |
| **Apache Fesod (Incubating)** ✅ | org.apache.fesod | fesod-sheet | 2.0.0-incubating+ |

**Maven:**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.0.0-incubating</version>
</dependency>
```

**Gradle:**

```gradle
implementation 'org.apache.fesod:fesod-sheet:2.0.0-incubating'
```

> **Note**: The `fesod-sheet` module is the core module for Excel/CSV processing. It automatically includes the necessary dependencies (`fesod-common` and `fesod-shaded`).

### Step 2: Package Import Updates

Update all import statements to use the new Apache Fesod (Incubating) package structure.

| Old Package (Deprecated) | New Package (Required)   |
|--------------------------|--------------------------|
| cn.idev.excel.*          | org.apache.fesod.sheet.* |

**Common Import Replacements:**

| Before                                           | After                                                     |
|--------------------------------------------------|-----------------------------------------------------------|
| import cn.idev.excel.FastExcel;                  | import org.apache.fesod.sheet.FesodSheet;                 |
| import cn.idev.excel.FastExcelFactory;           | import org.apache.fesod.sheet.FesodSheet;                 |
| import cn.idev.excel.ExcelReader;                | import org.apache.fesod.sheet.ExcelReader;                |
| import cn.idev.excel.ExcelWriter;                | import org.apache.fesod.sheet.ExcelWriter;                |
| import cn.idev.excel.annotation.ExcelProperty;   | import org.apache.fesod.sheet.annotation.ExcelProperty;   |
| import cn.idev.excel.context.AnalysisContext;    | import org.apache.fesod.sheet.context.AnalysisContext;    |
| import cn.idev.excel.read.listener.ReadListener; | import org.apache.fesod.sheet.read.listener.ReadListener; |

### Step 3: Class Name Migration

Replace deprecated entry point classes with FesodSheet.

#### Migration from FastExcel/FastExcelFactory

| Operation             | Before (FastExcel)                         | After (FesodSheet)                          |
|-----------------------|--------------------------------------------|---------------------------------------------|
| **Simple Read**       | FastExcel.read(file, Data.class, listener) | FesodSheet.read(file, Data.class, listener) |
| **Simple Write**      | FastExcel.write(file, Data.class)          | FesodSheet.write(file, Data.class)          |
| **Read Sheet**        | FastExcelFactory.readSheet(0)              | FesodSheet.readSheet(0)                     |
| **Write Sheet**       | FastExcelFactory.writerSheet("Sheet1")     | FesodSheet.writerSheet("Sheet1")            |
| **Read with Stream**  | FastExcel.read(inputStream)                | FesodSheet.read(inputStream)                |
| **Write with Stream** | FastExcel.write(outputStream)              | FesodSheet.write(outputStream)              |

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

---
name: fastexcel-to-fesod
description: >
  Migrates a Java project from FastExcel 1.3 (cn.idev.excel:fastexcel:1.3.0)
  to Apache Fesod (org.apache.fesod:fesod-sheet:2.0.1-incubating).
  Invoke this skill when asked to "migrate FastExcel to Fesod",
  "upgrade to Apache Fesod", or "replace cn.idev.excel".
  FastExcel 1.3 is the direct predecessor of Apache Fesod.
  Supports both legacy namespaces seen in real projects:
  cn.idev.excel.* and org.apache.fesod.excel.*.
  The entry classes FastExcel and FastExcelFactory are kept as @Deprecated
  aliases in Fesod, so call-site renames are strongly recommended but NOT
  required for compilation. The only breaking change is the Java package prefix.
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

# FastExcel 1.3 → Apache Fesod Migration Skill

## Background

FastExcel (`cn.idev.excel:fastexcel:1.3.0`) was donated to the Apache Software
Foundation and reborn as **Apache Fesod (Incubating)**.
The latest stable release is `2.0.1-incubating`.

Key facts that affect how this migration works:

- `cn.idev.excel.FastExcel` and `cn.idev.excel.FastExcelFactory` are renamed,
  but Fesod ships **`@Deprecated` bridge subclasses** at
  `org.apache.fesod.sheet.FastExcel` and `org.apache.fesod.sheet.FastExcelFactory`
  so existing call sites compile after a package-only swap.
- The **only hard breaking change** is the Java package prefix:
  any `*.excel.*` imports must move to `*.sheet.*`.
  In practice, both of these are common and must be handled:
    - `cn.idev.excel.*` → `org.apache.fesod.sheet.*`
    - `org.apache.fesod.excel.*` → `org.apache.fesod.sheet.*`
- The CGLIB naming tag changed from `ByFastExcelCGLIB` → `ByFesodCGLIB`.
  This only matters if the project inspects generated class names at runtime.
- All annotation names, method signatures, and processing logic are identical.

The official Fesod migration doc recommends a **three-phase gradual approach**;
this skill implements all three phases in one pass, but each phase is
clearly labelled so the developer can stop after Phase 1 or Phase 2 if needed.

---

## Phase 1 — Dependency swap (REQUIRED, zero code changes)

This phase alone is sufficient to make the project compile and run on Fesod
with deprecation warnings. It is always safe to stop here first and run tests.

### 1a. Maven — single-module project

In `pom.xml`, find the FastExcel dependency block:

```xml

<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>...</version>
</dependency>
```

Replace it with:

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.0.1-incubating</version>
</dependency>
```

Also remove the FastExcel version property if it exists, e.g.:

```xml

<fastexcel.version>1.3.0</fastexcel.version>
```

### 1b. Gradle

In `build.gradle` (or `build.gradle.kts`), replace:

```groovy
// Groovy DSL
implementation 'cn.idev.excel:fastexcel:1.3.0'
```

with:

```groovy
implementation 'org.apache.fesod:fesod-sheet:2.0.1-incubating'
```

Kotlin DSL:

```kotlin
implementation("org.apache.fesod:fesod-sheet:2.0.1-incubating")
```

---

## Phase 2 — Package import rename (REQUIRED for compilation)

After swapping the dependency, any import under `*.excel.*` can be unresolved.
Apply the following substitutions to every `.java` file in the project.

Process the table **top-to-bottom** (most specific first) to avoid partial replacements.

### 2x. Scope guardrails (IMPORTANT)

To avoid accidental regressions, apply replacements only in migration-relevant code.

- ✅ Safe targets:
    - `import ...` lines under `cn.idev.excel.*` / `org.apache.fesod.excel.*`
    - static call sites such as `FastExcel.read(` / `FastExcel.write(` / `FastExcelFactory.*(`
- ❌ Do **not** bulk-replace whole files or whole repositories for `FastExcel` text.
- ❌ Do **not** modify business literals unless explicitly requested:
    - `@ExcelProperty("...")` labels
    - sheet names / file names / i18n text
    - controller response strings
- ❌ Do **not** rewrite comments/Javadoc as part of mechanical migration.

If you use scripted replacement, constrain the pattern to imports and call expressions,
then review diffs before running tests.

### 2a0. Pre-migrated namespace cleanup (very common)

Some repositories are already partially migrated and use `org.apache.fesod.excel.*`
while still depending on older artifacts. After moving to `fesod-sheet`, these
imports must be rewritten to `org.apache.fesod.sheet.*`.

Apply this explicit replacement before the detailed mapping table:

| Find                             | Replace with                     |
|----------------------------------|----------------------------------|
| `import org.apache.fesod.excel.` | `import org.apache.fesod.sheet.` |

Then continue with the specific import mappings below.

### 2a. Core entry classes

| Find (exact import string)               | Replace with                                      |
|------------------------------------------|---------------------------------------------------|
| `import cn.idev.excel.FastExcel;`        | `import org.apache.fesod.sheet.FastExcel;`        |
| `import cn.idev.excel.FastExcelFactory;` | `import org.apache.fesod.sheet.FastExcelFactory;` |
| `import cn.idev.excel.ExcelWriter;`      | `import org.apache.fesod.sheet.ExcelWriter;`      |
| `import cn.idev.excel.ExcelReader;`      | `import org.apache.fesod.sheet.ExcelReader;`      |

> **Note**: After this phase, `FastExcel.read(...)` and `FastExcelFactory.writerSheet(...)`
> still compile — they resolve to the `@Deprecated` bridge classes in Fesod.
> Phase 3 replaces them with the canonical `FesodSheet` class.

### 2b. Read API

| Find                                                         | Replace with                                                          |
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

### 2c. Write API

| Find                                                          | Replace with                                                           |
|---------------------------------------------------------------|------------------------------------------------------------------------|
| `import cn.idev.excel.write.metadata.WriteSheet;`             | `import org.apache.fesod.sheet.write.metadata.WriteSheet;`             |
| `import cn.idev.excel.write.metadata.WriteWorkbook;`          | `import org.apache.fesod.sheet.write.metadata.WriteWorkbook;`          |
| `import cn.idev.excel.write.metadata.WriteTable;`             | `import org.apache.fesod.sheet.write.metadata.WriteTable;`             |
| `import cn.idev.excel.write.metadata.WriteBasicParameter;`    | `import org.apache.fesod.sheet.write.metadata.WriteBasicParameter;`    |
| `import cn.idev.excel.write.builder.ExcelWriterBuilder;`      | `import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;`      |
| `import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;` |
| `import cn.idev.excel.write.builder.ExcelWriterTableBuilder;` | `import org.apache.fesod.sheet.write.builder.ExcelWriterTableBuilder;` |

### 2d. Write handlers

| Find                                                                  | Replace with                                                                   |
|-----------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `import cn.idev.excel.write.handler.WriteHandler;`                    | `import org.apache.fesod.sheet.write.handler.WriteHandler;`                    |
| `import cn.idev.excel.write.handler.SheetWriteHandler;`               | `import org.apache.fesod.sheet.write.handler.SheetWriteHandler;`               |
| `import cn.idev.excel.write.handler.CellWriteHandler;`                | `import org.apache.fesod.sheet.write.handler.CellWriteHandler;`                |
| `import cn.idev.excel.write.handler.RowWriteHandler;`                 | `import org.apache.fesod.sheet.write.handler.RowWriteHandler;`                 |
| `import cn.idev.excel.write.handler.WorkbookWriteHandler;`            | `import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;`            |
| `import cn.idev.excel.write.handler.context.CellWriteHandlerContext;` | `import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;` |

### 2e. Annotations

| Find                                                             | Replace with                                                              |
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

### 2f. Converters

| Find                                                     | Replace with                                                      |
|----------------------------------------------------------|-------------------------------------------------------------------|
| `import cn.idev.excel.converters.Converter;`             | `import org.apache.fesod.sheet.converters.Converter;`             |
| `import cn.idev.excel.converters.AutoConverter;`         | `import org.apache.fesod.sheet.converters.AutoConverter;`         |
| `import cn.idev.excel.converters.ReadConverterContext;`  | `import org.apache.fesod.sheet.converters.ReadConverterContext;`  |
| `import cn.idev.excel.converters.WriteConverterContext;` | `import org.apache.fesod.sheet.converters.WriteConverterContext;` |

### 2g. Enums

| Find                                                      | Replace with                                                       |
|-----------------------------------------------------------|--------------------------------------------------------------------|
| `import cn.idev.excel.enums.CellDataTypeEnum;`            | `import org.apache.fesod.sheet.enums.CellDataTypeEnum;`            |
| `import cn.idev.excel.enums.CellExtraTypeEnum;`           | `import org.apache.fesod.sheet.enums.CellExtraTypeEnum;`           |
| `import cn.idev.excel.enums.WriteDirectionEnum;`          | `import org.apache.fesod.sheet.enums.WriteDirectionEnum;`          |
| `import cn.idev.excel.enums.poi.HorizontalAlignmentEnum;` | `import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;` |
| `import cn.idev.excel.enums.poi.BorderStyleEnum;`         | `import org.apache.fesod.sheet.enums.poi.BorderStyleEnum;`         |
| `import cn.idev.excel.enums.poi.FillPatternTypeEnum;`     | `import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;`     |

### 2h. Exceptions and metadata

| Find                                                           | Replace with                                                            |
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

### 2i. Wildcard catch-all (apply last)

After all specific replacements above, scan for any remaining wildcard imports:

| Find                             | Replace with                     |
|----------------------------------|----------------------------------|
| `import cn.idev.excel.`          | `import org.apache.fesod.sheet.` |
| `import org.apache.fesod.excel.` | `import org.apache.fesod.sheet.` |

Apply this only after all the specific rules above, as a safety net.

---

## Phase 3 — Entry class rename (STRONGLY RECOMMENDED)

`FastExcel` and `FastExcelFactory` compile in Fesod but are `@Deprecated` and
**will be removed in a future release**. Replace all call sites with `FesodSheet`.

### 3a. Import replacement

| Find                                              | Replace with                                |
|---------------------------------------------------|---------------------------------------------|
| `import org.apache.fesod.sheet.FastExcel;`        | `import org.apache.fesod.sheet.FesodSheet;` |
| `import org.apache.fesod.sheet.FastExcelFactory;` | `import org.apache.fesod.sheet.FesodSheet;` |

### 3b. Call site replacement — FastExcel

| Find                     | Replace with              |
|--------------------------|---------------------------|
| `FastExcel.read(`        | `FesodSheet.read(`        |
| `FastExcel.write(`       | `FesodSheet.write(`       |
| `FastExcel.writerSheet(` | `FesodSheet.writerSheet(` |
| `FastExcel.readSheet(`   | `FesodSheet.readSheet(`   |
| `FastExcel.writerTable(` | `FesodSheet.writerTable(` |

### 3c. Call site replacement — FastExcelFactory

FastExcel 1.3 shipped `FastExcelFactory` as a second entry class with an
identical API surface. All of its static methods map directly to `FesodSheet`:

| Find                            | Replace with              |
|---------------------------------|---------------------------|
| `FastExcelFactory.read(`        | `FesodSheet.read(`        |
| `FastExcelFactory.write(`       | `FesodSheet.write(`       |
| `FastExcelFactory.writerSheet(` | `FesodSheet.writerSheet(` |
| `FastExcelFactory.readSheet(`   | `FesodSheet.readSheet(`   |
| `FastExcelFactory.writerTable(` | `FesodSheet.writerTable(` |

### 3d. Type reference rename

If `FastExcel` or `FastExcelFactory` appear as a **type name** (not a call site),
rename those too:

- Variable type: `FastExcel x = ...` → `FesodSheet x = ...`
- Class literal: `FastExcel.class` → `FesodSheet.class`

`ExcelWriter` and `ExcelReader` are **not renamed** — they keep the same class name.

---

## Phase 4 — CGLIB class name (conditional)

This phase only applies if the project contains code that **inspects or asserts
on generated CGLIB class names at runtime**, for example in tests or serialisation logic.

Search all `.java` files for the string `ByFastExcelCGLIB`.
If found, replace with `ByFesodCGLIB`.

In Fesod, the naming policy is defined in
`org.apache.fesod.sheet.util.BeanMapUtils.FesodSheetNamingPolicy` and its
`getTag()` returns `"ByFesodCGLIB"`.

If no file references `ByFastExcelCGLIB`, skip this phase entirely.

---

## Phase 5 — Encoding and text safety (RECOMMENDED)

This phase prevents encoding-related breakage on Windows locales (for example GBK terminals).

- Keep source files in UTF-8; do not run global "remove non-ASCII" replacements.
- If compilation reports unmappable characters, fix only the specific broken lines.
- Prefer preserving existing business-language strings (Chinese/English/etc.) unless the task
  explicitly asks to normalize or translate them.
- If a replacement tool introduces malformed characters (e.g. `�?`), revert those hunks and
  re-apply scoped replacements.

---

## Verification

After completing the desired phases, confirm the following:

**Must be true after Phase 2:**

- No `.java` file contains `import cn.idev.excel.`
- No `.java` file contains `import org.apache.fesod.excel.`
- No `pom.xml` or `build.gradle` references `cn.idev.excel`

**Must be true after Phase 3 (additional):**

- No `.java` file calls `FastExcel.` or `FastExcelFactory.` as a static call site
- No `.java` file imports `org.apache.fesod.sheet.FastExcel` or
  `org.apache.fesod.sheet.FastExcelFactory`

**Must be true after Phase 4 (if applicable):**

- No `.java` file contains the string `ByFastExcelCGLIB`

**Must be true after Phase 5 (recommended):**

- No malformed replacement artifacts such as `�?` remain in source files
- No unintended changes in business literals (headers/sheet names/messages) unless requested
- No accidental rewrites in comments/Javadoc caused by mechanical replace

Run the project's existing test suite to confirm functional equivalence:

```
# Maven
mvn clean test

# Gradle
./gradlew clean test
```

If tests fail with runtime messages like `Unresolved compilation problem`, rerun
with a clean build first (`mvn clean test` / `./gradlew clean test`) to avoid
stale compiled classes masking the real source-level error.

---

## Migration summary output

After applying all changes, produce a table in this format:

```
## Migration Summary

| Phase | File | Changes |
|---|---|---|
| 1 | pom.xml | Replaced cn.idev.excel:fastexcel:1.3.0 with org.apache.fesod:fesod-sheet:2.0.1-incubating |
| 2 | src/.../Foo.java | N import statements updated (cn.idev.excel → org.apache.fesod.sheet) |
| 3 | src/.../Bar.java | M FastExcel./FastExcelFactory. call sites renamed to FesodSheet. |
| 4 | src/.../Baz.java | ByFastExcelCGLIB → ByFesodCGLIB (if applicable) |
| 5 | src/.../Qux.java | Encoding/literal safety check passed (no malformed artifacts / no unintended literal rewrites) |

Remaining cn.idev.excel references : 0 ✅
Remaining FastExcel. call sites    : 0 ✅  (or N ⚠️  if Phase 3 was skipped)
Remaining ByFastExcelCGLIB refs    : 0 ✅  (or skipped — not present)
```

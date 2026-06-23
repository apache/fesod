---
title: "Automated Migration from FastExcel 1.3.0 to Apache Fesod (Incubating) with OpenRewrite"
description: Migrate FastExcel 1.3 to Apache Fesod (Incubating) 2.0.1-incubating
authors: [bengbengbalabalabeng]
tags: [migration, fastexcel, fesod]
---

> This article demonstrates how to write a declarative OpenRewrite recipe that migrates your project from `cn.idev.excel:fastexcel:1.3.0` to `org.apache.fesod:fesod-sheet:2.0.1-incubating`, covering both Maven and Gradle builds.

<!--truncate-->

## Background

FastExcel (`cn.idev.excel`) has been donated to the Apache Software Foundation and is now incubating as **Apache Fesod (Incubating)**. The core change is a **Java package path replacement** — the API, annotations, and processing logic remain identical, making this a low-risk, mechanical refactoring.

For the official migration strategy, see: [https://fesod.apache.org/docs/migration/from-fastexcel/](https://fesod.apache.org/docs/migration/from-fastexcel/)

Manually replacing imports file by file is tedious and error-prone. OpenRewrite provides AST-level code transformation capabilities to perform this kind of bulk migration precisely and safely.

## Migration Scope

1. Update Maven/Gradle dependencies
2. Replace deprecated class names with `FesodSheet`
3. Update package imports

## Writing the OpenRewrite Recipe

Create a `rewrite.yml` file in your project root:

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: org.apache.fesod.MigrateFastExcelToFesod
displayName: Migrate FastExcel 1.3 to Apache Fesod (Incubating) 2.0.1-incubating
recipeList:

  # Step 1: Update Dependencies (Maven & Gradle)
  - org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId:
      oldGroupId: cn.idev.excel
      oldArtifactId: fastexcel
      newGroupId: org.apache.fesod
      newArtifactId: fesod-sheet
      newVersion: 2.0.1-incubating
  - org.openrewrite.gradle.ChangeDependency:
      oldGroupId: cn.idev.excel
      oldArtifactId: fastexcel
      newGroupId: org.apache.fesod
      newArtifactId: fesod-sheet
      newVersion: 2.0.1-incubating

  # Step 2: Package Import Updates
  - org.openrewrite.java.ChangePackage:
      oldPackageName: cn.idev.excel
      newPackageName: org.apache.fesod.sheet
      recursive: true
  - org.openrewrite.java.ChangePackage:
      oldPackageName: org.apache.fesod.excel
      newPackageName: org.apache.fesod.sheet
      recursive: true

  # Step 3: Entry Class Rename
  - org.openrewrite.java.ChangeType:
      oldFullyQualifiedTypeName: org.apache.fesod.sheet.FastExcel
      newFullyQualifiedTypeName: org.apache.fesod.sheet.FesodSheet
  - org.openrewrite.java.ChangeType:
      oldFullyQualifiedTypeName: org.apache.fesod.sheet.FastExcelFactory
      newFullyQualifiedTypeName: org.apache.fesod.sheet.FesodSheet

  - org.openrewrite.text.FindAndReplace:
      find: ByFastExcelCGLIB
      replace: ByFesodCGLIB
      filePattern: "**/*.java"
```

### How It Works

The recipe executes steps sequentially in declaration order:

**Step 1 — Update Dependencies**: The first rule updates `pom.xml` for Maven projects; the second rule updates `build.gradle` / `build.gradle.kts` for Gradle projects. If your project uses only one build tool, the other rule is silently skipped.

**Step 2 — Package Import Updates**: `ChangePackage(recursive=true)` covers all sub-packages under `cn.idev.excel` (annotation, read, write, converters, enums, etc.) in a single rule — no need to enumerate each one individually. The second rule handles legacy code that was already partially migrated to `org.apache.fesod.excel`. After this step, imports like `cn.idev.excel.FastExcel` become `org.apache.fesod.sheet.FastExcel` (the `@Deprecated` bridge class in Fesod).

**Step 3 — Entry Class Rename**: Once Step 2 completes, all `FastExcel` and `FastExcelFactory` imports are unified under `org.apache.fesod.sheet`. Only two `ChangeType` rules are needed to rename them to the canonical `FesodSheet` class, automatically updating all call sites (`FastExcel.read()`, `FastExcel.write()`, etc.) and type references (variable declarations, class literals).

**Step 4 — CGLIB String Replacement**: Only affects code that references CGLIB-generated class names at runtime. Most projects don't need this step — the rule is silently skipped if there's no match.

## Adding OpenRewrite to Your Project

### Maven

Add the plugin to `pom.xml`:

```xml
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>6.38.0</version>
    <configuration>
        <activeRecipes>
            <recipe>org.apache.fesod.MigrateFastExcelToFesod</recipe>
        </activeRecipes>
    </configuration>
</plugin>
```

### Gradle (Groovy)

Add to `build.gradle`:

```groovy
plugins {
    id 'java'
    id 'maven-publish'
    id 'org.openrewrite.rewrite' version '7.32.2'
}

rewrite {
    activeRecipe(
            'org.apache.fesod.MigrateFastExcelToFesod',
    )
}
```

### Gradle (Kotlin)

Add to `build.gradle.kts`:

```kotlin
plugins {
    `java-library`
    `maven-publish`
    id("org.openrewrite.rewrite") version "7.32.2"
}

rewrite {
    activeRecipe(
        "org.apache.fesod.MigrateFastExcelToFesod",
    )
}
```

## Running the Migration

### Preview Changes

View what the recipe would change without modifying files:

```bash
# Maven
# Output: target/site/rewrite/rewrite.patch
mvn rewrite:dryRun

# Gradle
# Output: build/reports/rewrite/rewrite.patch
gradle rewriteDryRun
```

### Apply Changes

Once satisfied with the preview, apply the changes:

```bash
# Maven
mvn rewrite:run

# Gradle
gradle rewriteRun
```

### Verify

```bash
# Compile check
mvn compile
# or
gradle compileJava

# Run tests
mvn test
# or
gradle test
```

## Before and After

Before migration:

```java
import cn.idev.excel.FastExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.read.listener.ReadListener;

FastExcel.write(outputStream, BookData.class)
        .sheet("Sheet1")
        .doWrite(data());
```

After migration:

```java
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.read.listener.ReadListener;

FesodSheet.write(outputStream, BookData.class)
        .sheet("Sheet1")
        .doWrite(data());
```

## References

- Apache Fesod (Incubating) Official Migration Guide: [https://fesod.apache.org/docs/migration/from-fastexcel/](https://fesod.apache.org/docs/migration/from-fastexcel/)
- OpenRewrite Documentation: [https://docs.openrewrite.org/](https://docs.openrewrite.org/)
- OpenRewrite Recipe Reference: [https://docs.openrewrite.org/recipes](https://docs.openrewrite.org/recipes)

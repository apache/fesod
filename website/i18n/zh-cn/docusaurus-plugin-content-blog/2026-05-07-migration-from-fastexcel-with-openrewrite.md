---
title: "使用 OpenRewrite 从 FastExcel 1.3.0 自动迁移到 Apache Fesod (Incubating)"
description: 将 FastExcel 1.3 迁移至 Apache Fesod (Incubating) 2.0.1-incubating
authors: [bengbengbalabalabeng]
tags: [migration, fastexcel, fesod]
---

> 本文介绍如何利用 OpenRewrite 编写声明式迁移配置，将项目从 `cn.idev.excel:fastexcel:1.3.0` 迁移至 `org.apache.fesod:fesod-sheet:2.0.1-incubating`，适用于 Maven 与 Gradle 项目。

<!--truncate-->

## 背景

FastExcel（`cn.idev.excel`）已捐赠给 Apache 软件基金会，孵化为 **Apache Fesod (Incubating)**。迁移的核心变化是 **Java 包路径替换**——API、注解和处理逻辑完全一致，属于低风险的机械重构。

官方迁移策略参考：[https://fesod.apache.org/docs/migration/from-fastexcel/](https://fesod.apache.org/docs/migration/from-fastexcel/)

手动逐文件替换 import 既耗时又容易遗漏。OpenRewrite 提供了 AST 级别的代码变换能力，能精确、安全地完成这类批量迁移。

## 迁移范围

1. 更新 Maven/Gradle 依赖
2. 将已废弃的类名替换为 `FesodSheet`
3. 更新包导入

## 编写 OpenRewrite Recipe

在项目根目录创建文件 `rewrite.yml`：

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: org.apache.fesod.MigrateFastExcelToFesod
displayName: Migrate FastExcel 1.3 to Apache Fesod (Incubating) 2.0.1-incubating
recipeList:

  # 步骤 1：更新依赖 (Maven & Gradle)
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

  # 步骤 2：更新包导入
  - org.openrewrite.java.ChangePackage:
      oldPackageName: cn.idev.excel
      newPackageName: org.apache.fesod.sheet
      recursive: true
  - org.openrewrite.java.ChangePackage:
      oldPackageName: org.apache.fesod.excel
      newPackageName: org.apache.fesod.sheet
      recursive: true

  # 步骤 3：重命名入口类
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

### 执行逻辑解析

Recipe 按声明顺序依次执行，分为四个步骤：

**Step 1 — 更新依赖**：Maven 项目触发第一条规则修改 `pom.xml`；Gradle 项目触发第二条规则修改 `build.gradle` / `build.gradle.kts`。如果项目只使用其中一种构建工具，另一条规则自动跳过，不会报错。

**Step 2 — 更新包导入**：`ChangePackage(recursive=true)` 一条规则覆盖 `cn.idev.excel` 下所有子包（annotation、read、write、converters、enums 等），无需逐个枚举。第二条规则处理已部分迁移到 `org.apache.fesod.excel` 的遗留代码。这一步会将 `cn.idev.excel.FastExcel` 的 import 改为 `org.apache.fesod.sheet.FastExcel`（Fesod 中的 `@Deprecated` 桥接类）。

**Step 3 — 重命名入口类**：Step 2 完成后，所有 `FastExcel` 和 `FastExcelFactory` 的 import 已统一指向 `org.apache.fesod.sheet` 包。此时只需两条 `ChangeType` 规则，将它们改为正式的 `FesodSheet` 类，同时自动处理所有调用点（`FastExcel.read()`、`FastExcel.write()` 等）和类型引用（变量声明、类字面量）。

**Step 4 — CGLIB 字符串替换**：仅对引用了 CGLIB 生成类名的代码生效，多数项目不需要此步骤，不匹配则自动跳过。

## 在项目中引入 OpenRewrite

### Maven 项目

在 `pom.xml` 中添加插件：

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

在 `build.gradle` 中引入：

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

## 执行迁移

### 预览变更

先查看 Recipe 会产生哪些修改，不实际改动文件：

```bash
# Maven
# 默认输出路径：target/site/rewrite/rewrite.patch
mvn rewrite:dryRun

# Gradle
# 默认输出路径：build/reports/rewrite/rewrite.patch
gradle rewriteDryRun
```

输出会列出所有即将修改的文件和具体变更内容。

### 执行迁移

确认无误后，执行实际修改：

```bash
# Maven
mvn rewrite:run

# Gradle
gradle rewriteRun
```

### 验证

```bash
# 编译检查
mvn compile
# 或
gradle compileJava

# 运行测试
mvn test
# 或
gradle test
```

## 迁移效果示例

迁移前：

```java
import cn.idev.excel.FastExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.read.listener.ReadListener;

FastExcel.write(outputStream, BookData.class)
        .sheet("Sheet1")
        .doWrite(data());
```

迁移后：

```java
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.read.listener.ReadListener;

FesodSheet.write(outputStream, BookData.class)
        .sheet("Sheet1")
        .doWrite(data());
```

## 参考

- Apache Fesod (Incubating) 官方迁移指南：[https://fesod.apache.org/docs/migration/from-fastexcel/](https://fesod.apache.org/docs/migration/from-fastexcel/)
- OpenRewrite 官方文档：[https://docs.openrewrite.org/](https://docs.openrewrite.org/)
- OpenRewrite Recipe 参考：[https://docs.openrewrite.org/recipes](https://docs.openrewrite.org/recipes)

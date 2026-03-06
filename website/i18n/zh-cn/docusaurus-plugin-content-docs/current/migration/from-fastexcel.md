---
sidebar_position: 1
title: 从 FastExcel 迁移
description: 从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 的完整指南
keywords: [fesod, 迁移, fastexcel, apache, excel, 升级]
---

# 迁移指南:从 FastExcel 迁移到 Apache Fesod (Incubating)

## 概述

本指南为从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 提供全面的迁移路线。Apache Fesod (Incubating) 是该项目的演进版本,现已成为 Apache 软件基金会(孵化中)的一部分,提供同样的高性能 Excel 处理能力,并具有更强的社区支持和长期可持续性。

### 为什么要迁移?

- **Apache 基金会支持**: Apache Fesod (Incubating) 现已成为 Apache 软件基金会的一部分,确保长期维护和社区驱动的开发
- **无缝过渡**: API 几乎完全相同,只需最少的代码更改
- **统一品牌**: 在 Apache Fesod (Incubating) 下统一的命名规范
- **持续创新**: 在 Apache 治理下获得未来的增强和功能
- **向后兼容**: 暂时保留已废弃的别名(FastExcel)以便逐步迁移

### 迁移范围

本次迁移主要涉及:

1. 更新 Maven/Gradle 依赖
2. 将已废弃的类名替换为 FesodSheet
3. 更新包导入
4. 通过全面测试验证功能

核心 API、注解和处理逻辑保持不变,确保低风险的迁移路径。

---

## 迁移步骤

### 步骤 1: 更新依赖

将现有依赖替换为 Apache Fesod (Incubating):

| 来源                    | GroupId          | ArtifactId | 版本     |
|-----------------------|------------------|------------|--------|
| **cn.idev FastExcel** | cn.idev.excel    | fastexcel  | 1.3.0  |
| **Apache Fesod (Incubating)** ✅    | org.apache.fesod | fesod-sheet      | 2.0.0-incubating+ |

**Maven 配置:**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.0.0-incubating</version>
</dependency>
```

**Gradle 配置:**

```gradle
implementation 'org.apache.fesod:fesod-sheet:2.0.0-incubating'
```

> **注意**: `fesod-sheet` 模块是 Excel/CSV 处理的核心模块。它会自动引入必要的依赖（`fesod-common` 和 `fesod-shaded`）。

### 步骤 2: 更新包导入

更新所有 import 语句以使用新的 Apache Fesod (Incubating) 包结构。

| 旧包名(已废弃)        | 新包名(必需)                  |
|-----------------|--------------------------|
| cn.idev.excel.* | org.apache.fesod.sheet.* |

**常见导入替换:**

| 修改前                                              | 修改后                                                       |
|--------------------------------------------------|-----------------------------------------------------------|
| import cn.idev.excel.FastExcel;                  | import org.apache.fesod.sheet.FesodSheet;                 |
| import cn.idev.excel.FastExcelFactory;           | import org.apache.fesod.sheet.FesodSheet;                 |
| import cn.idev.excel.ExcelReader;                | import org.apache.fesod.sheet.ExcelReader;                |
| import cn.idev.excel.ExcelWriter;                | import org.apache.fesod.sheet.ExcelWriter;                |
| import cn.idev.excel.annotation.ExcelProperty;   | import org.apache.fesod.sheet.annotation.ExcelProperty;   |
| import cn.idev.excel.context.AnalysisContext;    | import org.apache.fesod.sheet.context.AnalysisContext;    |
| import cn.idev.excel.read.listener.ReadListener; | import org.apache.fesod.sheet.read.listener.ReadListener; |

### 步骤 3: 类名迁移

将已废弃的入口类替换为 FesodSheet。

#### 从 FastExcel/FastExcelFactory 迁移

| 操作           | 修改前 (FastExcel)                            | 修改后 (FesodSheet)                            |
|--------------|--------------------------------------------|---------------------------------------------|
| **简单读取**     | FastExcel.read(file, Data.class, listener) | FesodSheet.read(file, Data.class, listener) |
| **简单写入**     | FastExcel.write(file, Data.class)          | FesodSheet.write(file, Data.class)          |
| **读取 Sheet** | FastExcelFactory.readSheet(0)              | FesodSheet.readSheet(0)                     |
| **写入 Sheet** | FastExcelFactory.writerSheet("Sheet1")     | FesodSheet.writerSheet("Sheet1")            |
| **流式读取**     | FastExcel.read(inputStream)                | FesodSheet.read(inputStream)                |
| **流式写入**     | FastExcel.write(outputStream)              | FesodSheet.write(outputStream)              |

---

## 迁移策略

### 渐进式迁移(推荐)

利用已废弃的别名类进行分阶段迁移。

**阶段 1: 仅更新依赖**

- 将 Maven/Gradle 依赖更新为 Apache Fesod (Incubating)
- 继续使用 FastExcel 类(现为已废弃的别名)
- 仅更新包导入
- 运行全面测试以验证兼容性

**阶段 2: 类名迁移**

- 逐步将已废弃的类替换为 FesodSheet
- 使用 IDE 重构工具进行批量重命名
- 逐个模块或逐个功能进行迁移
- 在整个过程中保持全面的测试覆盖

**阶段 3: 清理**

- 删除所有对已废弃类的引用
- 解决废弃警告
- 更新文档和代码注释

**优势:**

- 通过增量更改降低风险
- 如果出现问题更容易回滚
- 对正在进行的开发影响最小
- 允许在每个阶段进行充分测试的时间

---

## 总结

由于高度的 API 兼容性和向后兼容的已废弃别名,从 cn.idev FastExcel 迁移到 Apache Fesod (Incubating) 是一个直接的过程。主要工作涉及更新依赖声明和包导入,几乎不需要或不需要逻辑更改。

渐进式迁移策略得到临时已废弃别名(FastExcel、FastExcelFactory)的支持,允许团队按自己的节奏迁移,同时保持完整功能。

遵循本指南,组织可以无缝过渡到 Apache Fesod (Incubating),并从 Apache 软件基金会生态系统的长期可持续性和社区支持中受益。

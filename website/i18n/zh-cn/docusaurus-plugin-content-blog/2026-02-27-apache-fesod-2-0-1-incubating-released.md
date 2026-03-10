---
title: "Apache Fesod (Incubating) 2.0.1-incubating 正式发布"
description: "Apache Fesod (Incubating) 社区欣然宣布，Apache Fesod (Incubating) 2.0.1-incubating 版本正式发布！"
authors: [psxjoy]
tags: [announcement, release]
date: 2026-02-27T00:00:00.000Z
---

**2026年2月** —— Apache Fesod (Incubating) 社区欣然宣布，**Apache Fesod (Incubating) 2.0.1-incubating 版本正式发布！**

这不仅是一个技术版本的迭代，更是 Fesod 项目自加入 **Apache 软件基金会 (ASF)** 孵化器以来的**首个正式发布版本**。在过去的几个月中，社区成员共同完成了大量的合规性改造、架构重构以及功能增强，标志着 Fesod 已经完全准备好在 ASF 规范下开启高性能 Excel 处理的新篇章。

<!-- truncate -->

## 里程碑意义：首个正式的 ASF Release

作为进入孵化器后的“处子秀”，2.0.1 版本在 **Apache Way（Apache 之道）** 方面取得了决定性进展：

* **全面更名与合规：** 我们完成了从 `FastExcel` 到 `Fesod` 的全面重命名（包括包名、类名及文档），确保品牌完全受 ASF 保护。
* **合规性补全：** 引入了 `DISCLAIMER`、`NOTICE` 以及 `support` 模块，并通过 Spotless 实现了自动化的 License Header 管理，完全符合 ASF 的政策要求。
* **社区治理：** 所有的代码合并与版本发布均经过了社区投票与邮件列表讨论，体现了公开、透明的治理原则。

## 版本核心亮点

### 1. 架构深度重构

为了支持更长远的发展，我们对项目结构进行了大规模优化：

* **多模块化（Multi-module）：** 引入了多模块架构，使项目层次更清晰，依赖管理更精细。
* **分发增强：** 新增 `fesod-distribution` 模块，规范了分发包的描述与构建。
* **JDK 25 支持：** 紧跟 Java 生态，正式增加了对 **JDK 25** 的兼容与支持。

### 2. 功能特性 (Features)

本次发布引入了多项实用新功能，进一步增强了 Excel 处理的灵活性：

* **Excel 处理增强：** 在 `SheetWriteHandler` 中新增了 `afterSheetDispose` 生命周期方法，并引入了全新的**表头合并策略（Header Merge Strategy）**。
* **老版本兼容：** 优雅地处理了极老版本的 Excel BIFF 格式，避免运行崩溃。
* **灵活控制：** 新增 `autoStrip` 参数，并在 `Workbook` 常量中引入了最大 Sheet 名称长度校验。
* **网站体验：** 官方文档新增了本地搜索功能及 Matomo 统计插件，并优化了移动端展示。

### 3. 稳定性与健壮性 (Stability)

我们投入了大量精力在质量保障上：

* **模糊测试 (Fuzz Testing)：** 为 Excel 读取增加了每日模糊测试任务，能更早发现处理异常文件时的潜在崩溃。
* **依赖安全：** 升级了 Spring-core、Logback、Fastjson2 以及 POI（升级至 5.5.1）等核心依赖，修复了多项已知漏洞。
* **CI/CD 优化：** 引入了 Netlify 预览部署和自动关闭过期 Issue 的工作流，显著提升了社区协作效率。

## 关键变更概览

### **新功能 (Feature)**

* `SheetWriteHandler` 增加 `afterSheetDispose` 方法。
* 增加 Excel 写入的表头合并策略。
* 增加 GitHub Actions 的 Netlify 预览部署支持。
* 增加 `docusaurus-search-local` 插件以支持官网搜索。

### **修复 (Bugfix)**

* 修复 `WriteSheetWorkbookWriteHandler` 类中的 NPE 异常。
* 修复文档中错误的示例代码及过期的链接。
* 升级依赖以修复漏洞告警，并更新 import 语句以使用 shaded 后的 cglib 报名。

### **重构 (Refactor)**

* 将项目重命名为 Fesod/FesodSheet，并更新包结构以符合 Apache 命名规范。
* 引入 Spotless 自动管理 License Header。
* 移除过期且不正确的 `@since` 标签，精简核心类。

> 详细的变更列表请参考：[GitHub Release Notes](https://github.com/apache/fesod/releases/tag/2.0.1-incubating)

## 致谢

“社区优于代码”是 Apache 的核心理念。感谢所有为此版本做出贡献的开发者、Mentors 以及社区成员。

### 新贡献者 (New Contributors)

我们要特别欢迎并感谢在该版本中做出首次贡献的 **14 位新成员**：

> @X-qinghai, @ngocnhan-tran1996, @YIminta, @jounghu, @wlgusqkr, @gaushon, @GOODBOY008, @ongdisheng, @harshasiddartha, @pjfanning, @liugddx, @hezhangjian, @bengbengbalabalabeng

特别感谢 **@delei, @psxjoy, @alaahong, @ongdisheng, @GOODBOY008** 以及所有在 GitHub 上提交 PR 和建议的朋友们。正是由于你们在重命名、合规性检查、CI 优化以及核心功能改进上的不懈努力，才促成了 Fesod 在 Apache 的完美首秀。

## 如何获取

你可以通过以下渠道下载并体验全新的 Apache Fesod (Incubating) ：

* **官方网站：** [https://fesod.apache.org/](https://fesod.apache.org/)
* **源码仓库：** [https://github.com/apache/fesod](https://github.com/apache/fesod)
* **Maven 坐标：**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>2.0.1-incubating</version>
</dependency>
```

**欢迎加入我们！**
Apache Fesod (Incubating) 社区始终对开发者保持开放。你可以通过订阅邮件列表 `dev@fesod.apache.org` 或在 GitHub 上提交 Issue 与我们交流。

让我们共同期待 Fesod 在 Apache 孵化器中茁壮成长！

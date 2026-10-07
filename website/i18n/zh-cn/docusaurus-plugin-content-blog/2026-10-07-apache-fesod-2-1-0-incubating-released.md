---
title: "Apache Fesod (Incubating) 2.1.0-incubating 正式发布"
description: "Apache Fesod (Incubating) 社区欣然宣布，Apache Fesod (Incubating) 2.1.0-incubating 版本正式发布！"
authors: [alaahong]
tags: [announcement, release]
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

**2026年10月** —— Apache Fesod (Incubating) 社区欣然宣布，**Apache Fesod (Incubating) 2.1.0-incubating 版本正式发布！**

这是 Fesod 在 **Apache 软件基金会 (ASF)** 孵化器治理下持续推进的一次版本迭代。本版本共合并 **110 个 Pull Request**，汇聚了来自全球各地贡献者的智慧，带来了按列读写等重磅新能力、更完善的 Excel 格式兼容性、性能优化，并通过完全可复现的构建进一步强化了供应链安全。

<!-- truncate -->

## 里程碑意义：从合规走向能力

在 2.0.x 系列打牢合规与治理基础之后，2.1.0 标志着 Fesod 从"整理内务"转向**规模化交付新功能**：

* **110 个 PR 合并：** 涵盖新功能、缺陷修复、重构、文档、测试与构建自动化等各个方面。
* **10 位新贡献者，来自四个国家：** 中国、爱尔兰、拉脱维亚与韩国——社区持续成长，国际化程度日益加深。
* **可复现构建：** 通过固定的 `project.build.outputTimestamp` 实现字节级一致的可复现构件，这是供应链安全的关键改进。
* **安全加固：** 远程 URL 图片访问必须显式加入白名单，同时修复了多项依赖漏洞（含 XXE、CVE）。

## 版本核心亮点

### 1. 新特性：更灵活的 Excel 处理

* **冻结窗格（Freeze Pane）：** 写入时可通过 `@FreezePane` 注解或自定义 `WriteHandler` 冻结行与列。
* **XLS 与 CSV 按列读取：** 在原有 XLSX 按列读取的基础上，2.1.0 将列级读取控制扩展至老旧的 **XLS** 与 **CSV** 文件，可按需只读取所需列。
* **无 Bean 模式流式表头 API：** 不使用注解 Bean 时，可通过流式 API 以编程方式构建表头。
* **`java.time.LocalTime` 转换器：** 读写链路原生支持 `LocalTime` 值的转换。
* **图片转换器精细化：** `StringImageConverter` 拆分为专用的 `Pathname` 与 `Base64` 两个转换器，图片处理更清晰、更安全。
* **`FesodDoc` 初始结构：** 新模块为未来支持 Word 文档（`.docx`）打下基础。
* **性能提升：** 日期与数字格式化器现在会被缓存，减少大规模读取时的重复解析开销。

### 2. 安全与合规

* **远程 URL 图片白名单：** `read` 要求远程 URL 图片必须显式加入白名单，规避 SSRF 类风险。
* **可复现构建：** 固定时间戳使构件在多次构建间完全一致，便于社区验证已发布二进制产物。
* **孵化器合规：** `DISCLAIMER` 文件现已随二进制与源码 JAR 一起打包至 `META-INF` 目录。
* **依赖加固：** 升级 `assertj-core`（修复 XXE）、`fastjson2`、`slf4j` 及 npm 依赖，修复已知漏洞。

### 3. 稳定性与健壮性

* **老旧格式修复：** XLS BIFF8 加密现在可以正确生效（此前因密码过早清除而失效）。
* **日期处理修正：** 修复 CSV 单元格中 `java.sql.Date/Time` 的处理，以及 `@DateTimeFormat` 默认值下的 1904 日期系统回退。
* **Windows 模板填充：** 修复 Windows 平台上的填充模板复制失败问题。
* **分批读取完整性：** 修复 `PageReadListener` 在切换 Sheet 时产生的重复行，并将 Sheet 级读取参数复制到 Sheet holder。

## 全球化的开源社区

"社区优于代码"在本版本的贡献者版图上得到了生动体现。2.1.0-incubating 的 **16 位具名贡献者** 遍布四个国家：

* **中国（13 位）：** 苏州 3 位、杭州 1 位、天津 1 位、北京 1 位，以及其他城市 7 位。
* **爱尔兰（1 位）：** Kilkenny。
* **拉脱维亚（1 位）。**
* **韩国（1 位）。**

从按列读取到冻结窗格，这些新特性都是在跨时区的国际协作中——代码评审、讨论与测试——打磨成型的。正是这种多样性，驱动着 Fesod 在 ASF 孵化器治理下稳步前行。

## 关键变更概览

### **新功能 (Feature)**

* 通过 `@FreezePane` 或 `WriteHandler` 支持冻结窗格。
* 支持 XLS 与 CSV 文件的按列读取。
* 新增无 Bean 模式的流式表头 API。
* `StringImageConverter` 拆分为 `Pathname` 与 `Base64` 转换器。
* 新增 `java.time.LocalTime` 转换器。
* `ReadSheet` 支持列索引上限。
* 新增 `FesodDoc` 类的初始结构。
* 默认注册 `EscapeHexCellWriteHandler` 以支持 XLSX。

### **修复 (Bugfix)**

* 修复 XLS BIFF8 加密未生效的问题。
* 修复 `CsvCell` 处理 `java.sql.Date/Time` 时崩溃的问题。
* 修复 `PageReadListener` 在 Sheet 间产生重复行的问题。
* 修复 Windows 平台填充模板复制失败的问题。
* 修复 `@DateTimeFormat` 默认值下的 1904 日期系统回退。
* 修复内联字符串单元格中 `_xHHHH_` 转义的解码问题。

### **重构 (Refactor)**

* 统一并重载 `readSheet` 方法，使用更灵活。
* 使用 `ColumnIndexResolver` 取代基于列表的列查找。
* 使用 Lombok 生成简单访问器。
* 移除 `fesod-examples` 模块。

> 详细的变更列表请参考：[GitHub Release Notes](https://github.com/apache/fesod/releases/tag/2.1.0-incubating) 以及 [完整变更日志](https://github.com/apache/fesod/compare/2.0.2-incubating...2.1.0-incubating)。

## 致谢

"社区优于代码"是 Apache 的核心理念。感谢所有为此版本做出贡献的开发者、Mentors 以及社区成员。

### 新贡献者 (New Contributors)

我们要特别欢迎并感谢在该版本中做出首次贡献的 **10 位新成员**：

> @32154678925, @skytin1004, @sapienza88, @Duansg, @nkuprins, @Aias00, @xleoken, @leehaut, @codeAnqiang-ma, @Mikkey-f

特别感谢 **@alaahong, @delei, @psxjoy, @bengbengbalabalabeng, @nkuprins, @sapienza88, @GOODBOY008, @pjfanning** 以及所有在 GitHub 上提交 PR 和建议的朋友们。正是你们在功能开发、缺陷修复、测试与发布管理上的付出，才促成了本次版本的成功发布。

## 如何获取

你可以通过以下渠道下载并体验全新的 Apache Fesod (Incubating) ：

* **官方网站：** [https://fesod.apache.org/](https://fesod.apache.org/)
* **源码仓库：** [https://github.com/apache/fesod](https://github.com/apache/fesod)
* **Maven 坐标：**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.1.0-incubating</version>
</dependency>
```

**欢迎加入我们！**
Apache Fesod (Incubating) 社区始终对开发者保持开放。你可以通过订阅邮件列表 `dev@fesod.apache.org` 或在 GitHub 上提交 Issue 与我们交流。

让我们共同期待 Fesod 在 Apache 孵化器中茁壮成长！

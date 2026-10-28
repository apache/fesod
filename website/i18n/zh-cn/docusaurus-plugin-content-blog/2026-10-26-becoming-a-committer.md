---
title: "成为 Committer"
description: 记录加入 Apache Fesod (Incubating) 成为 Committer 的心路历程、学到的经验，以及接下来的计划
authors: [nkuprins]
tags: [opensource, apache, committer, fesod]
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

![Apache Fesod (Incubating) New Committer](/img/blog/apache-fesod-new-committer.svg)

> 很高兴与大家分享，我已经成为 Apache Fesod (Incubating) 的 Committer，期待继续为项目和社区做出更多贡献。

<!--truncate-->

## 致谢

首先要衷心感谢 **@delei** 和 **@bengbengbalabalabeng**。

@delei 评审了我的第一个 PR，带我熟悉了 Apache 的工作流程。@bengbengbalabalabeng 耐心评审了我后续的大部分贡献，提出的反馈让我的实现变得更加简洁、健壮。

也感谢 PPMC 的其他成员以及更广泛的社区对我的信任。

## 一切是怎么开始的

我的背景是软件性能与能效方向 —— 读书期间，我研究过不同的设计选择如何影响系统的能耗。正是这种对软件资源浪费的好奇心，最终把我带到了 Apache Fesod (Incubating)。我的第一个贡献只是一次很小的清理：去掉了某个工具类中一次多余的缓冲区拷贝。

修复本身只有几行代码，但整个过程让我清楚地看到了项目的运作方式——先在 Issue 中讨论清楚问题，确认好方案，再提交一个范围明确、测试完善的 PR。第一次提交被合并之后，我就彻底上瘾了。

## 我做过的工作

过去几个月里，我主要专注在正确性、测试和开发者体验上：

* **正确性：** 修复了读取路径上一系列隐蔽的数据完整性问题——重复行、过期的缓存样式，以及丢失或损坏的单元格内容。
* **测试：** 在改动容易出问题的解析逻辑之前，补充了回归测试来固定现有行为。
* **文档与官网：** 完善了开发者文档，并更新了项目官网的部分内容。
* **重构：** 精简了样板代码，收紧了封装边界，并把逻辑拆分到了专门的辅助方法中以提升可读性。
* **代码评审：** 评审了其他贡献者提交的 PR，参与塑造了新增的转换器等功能的实现方式。

## 心得体会

在 Apache Fesod (Incubating) 上的工作，也在潜移默化地影响着我对软件设计的整体看法：

1. **小改动比大改动更快赢得信任。** 我的第一个 PR 只是一次很小的清理，而不是什么亮眼的新功能——但正是它为后面的一切打开了大门。
2. **评审是一项双向的技能。** 当我开始评审其他贡献者的 PR 之后，我才从“另一侧”理解了什么样的改动容易评审、什么样的改动很难评审。
3. **一致性比个人喜好更重要。** 一份风格统一的代码，对下一个阅读它的人来说，远比按个人喜好“改得更好”的代码更友好。

## 展望未来

成为 Committer 并不是终点，而只是责任的一次转变。

接下来，除了继续为 Apache Fesod (Incubating) 的代码库做贡献之外，我也打算把更多时间投入到评审 PR、处理已报告的 Issue，以及帮助新贡献者让自己的第一个 PR 顺利合并。

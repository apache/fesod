---
id: 'contribution'
title: '概述'
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

欢迎社区中的每一位用户和开发者成为贡献者。无论是报告问题、改进文档、提交代码，还是提供技术支持，您的参与都将有助于使其变得更好。

## 贡献方向

对 Apache Fesod (Incubating) 贡献，贡献方向有很多:

- 修复错别字
- 修复 Bug
- 删除冗余代码
- 添加测试用例
- 增强功能
- 添加注释以提升代码可读性
- 优化代码结构
- 改进或完善文档

### 原则

- 任何有助于项目改进的 PR 都值得鼓励！
- 贡献一个新的功能，请先在 `issue` 或 `discussion` 中提出和讨论。我们不会合并**未经讨论和确认的**的功能

## 贡献流程

### 查找任务

找到您感兴趣的问题！在我们的 GitHub 仓库问题列表中，我们经常发布带有`good first issue`或`help wanted`标签的问题。这些问题欢迎贡献者的帮助。其中，`good first issues`通常门槛较低，适合初学者参与。

当然，如果您有好的想法，也可以直接在 GitHub 讨论区提出，或联系社区成员。

### 讨论

请参考[问题反馈](./feedback.md)来创建一个问题或讨论，并努力达成共识。

**注意**：如果申请分配这个问题时，请注意这并非简单的“请分配给我”，您需要阐述对问题的理解、设计方案，并在可能的情况下提供您的概念验证代码。

### 实现

根据问题中商定的方案实施变更。

- 仅在方案达成共识时才开始实施工作（例如：您已被指派处理该工单）
- 若您是新成员，可参考[代码贡献指南](./contribution/contribute-code.md)来搭建开发环境。

### 审核

创建拉取请求并与审核者协作。

- 确保不包含无关或不必要的格式调整。
- 在提交 PR 之后会触发 CI，请确保通过 CI 检查。
- 请确保测试通过。
- 请勿解决讨论。

### 合并

社区 committer 需要根据[Code Review指南](./contribution/code-review-guide.md)中的指南，负责检查贡献是否满足要求，并将代码合并到代码库中。

## 贡献代码

所有改进均可通过 Pull Request (PR) 实现。在提交 Pull Request 前，请参考[代码贡献指南](./contribution/contribute-code.md)了解如何贡献代码。

## 测试用例贡献

任何测试用例的贡献都值得鼓励，尤其是单元测试。建议在对应模块的 `test` 目录中创建 `XXXTest.java` 文件，推荐使用 `JUnit5` 框架。

## 贡献文档

文档是 Apache Fesod (Incubating) 官方网站的重要构建内容，是项目与社区之间的重要桥梁。请参考[文档贡献指南](./contribution/contribute-doc.md)了解如何贡献文档。

## AI 辅助贡献指南

所有借助 AI 辅助的贡献都必须严格遵守我们的 [AI 辅助贡献指南](./contribution/ai-policy.md)。

## 非活跃 PR 接手指南

为避免长期停滞的 PR 阻塞项目演进，同时充分尊重原作者的劳动成果，社区支持对停滞 PR 进行接手与认领。

### 适用条件

当**同时满足**以下所有情况时，适用 PR 接手机制：

- **审查正常推进**：Reviewers 已积极跟进审查并给出反馈，PR 停滞并非因社区缺乏 Review 资源所致。
- **作者端长期未响应**：原作者超过 **14 天** 未处理必要事项（如：CI 失败未修复、Review 意见未回复、分支严重冲突未解决，或公开表示无暇推进）。
- **已有接管意向**：已有其他贡献者在原 PR 评论区主动留言，明确表达了接手意向。

### 接手流程

1. **礼貌询问**：接管人在原 PR 留言确认接手意向后，维护者可以为原 PR 添加 `PR: waiting for feedback` 标签。
2. **等待缓冲期**：给予原作者 **7 天缓冲期**。
    - **恢复推进**：若原作者在期内取得实质性进展（如回复 Review 疑问、提交修改代码，或提供明确的排期计划），接管流程终止，优先留给原作者完成；
    - **提前交接**：若原作者明确表示无暇继续推进，新贡献者可**立即接手**，无需等满 7 天；
    - **超时处理**：若 7 天期满仍未回复或无实质性进展，新贡献者可正式开展工作并提交新 PR。
3. **关联与追踪**：新 PR 描述开头请注明 `Related #<原PR编号>`，并添加 `PR: adopted` 标签。新 PR 合并或确认替代方案有效后，维护者将关闭原 PR 并致谢。

### 署名与致敬

- **代码复用**：新 PR **必须保留原作者的贡献**。优先保留原 Commits；并在提交信息末尾附上：`Co-authored-by: 原作者名 <原作者邮箱>`。
- **完全重写**：无需保留原 Commit，但**必须在 PR 描述中链接原 PR**，致敬前期的探索。

### 防抢跑与公平原则

- **抢跑行为**：若有人在缓冲期满前擅自开启新 PR 抢跑，维护者将**暂缓审查该新 PR**，直至等待期结束。
- **恶意竞争**：若原作者仍在积极跟进，第三方仍执意开启新 PR 竞争的，维护者**有权直接关闭该新 PR**。

## 其他参与方式

除了直接贡献代码，以下方式同样是对 Apache Fesod (Incubating) 的宝贵支持：

- 回答其他用户的问题。
- 帮助审阅他人的 PR。
- 提出改进建议。
- 撰写技术博客，宣传 Apache Fesod (Incubating)。
- 在社区中分享项目相关知识。

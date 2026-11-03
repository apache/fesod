---
id: 'ai-policy'
title: 'AI 辅助贡献指南'
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

# AI 辅助贡献指南

我们欢迎贡献者使用 AI 作为辅助编码与学习的工具。然而，**工具的使用不能替代人类的思考与责任**。每一位贡献者都必须对自己提交的代码与言论承担最终责任。

开源社区的核心价值之一是**人与人之间的协作、知识共享与信任**。维护者的审查资源非常有限，我们无法接受维护者沦为“替贡献者操作/调试 AI”的代理人。为了保障项目质量与社区协作效率，请遵守以下规范：

## Pull Request

- 提交 PR 前，作者必须亲自通读全部代码并**端到端理解其核心设计与实现思路**，在审查过程中有能力解释并阐明自己的修改方案。“AI Dump”是指作者未经消化理解便将大量 AI 生成的代码和描述直接提交，即使代码逻辑能够跑通，这种做法也将理解与核实的负担完全转嫁给了 Reviewers，作者本人也无法从中获得任何成长。
- 若未能完全吃透 AI 生成代码的某一处细节是完全正常的。请在 PR 描述中主动标明这些未知点与前提假设，以便 Reviewers 能够与你共同探讨并排查隐患。
- **严禁使用全自动或自主 Agent 批量或自动创建 PR**。我们强制要求“人工参与闭环”；任何缺乏人类深度参与的自动化贡献都将被直接关闭。

## 沟通与讨论

代码审查不仅是为了合并代码，更是为了与**你本人**（而非你的 AI 工具）交流，帮助你融入社区并共同成长。无论是创建 Issue 描述问题、撰写 PR 说明，还是在审查过程中回复提问，都请务必使用你自己的语言表达。**在与 Reviewers 沟通时，严禁使用自动化 AI 生成评论或直接复制粘贴 AI 的回复。** Reviewers 可能会直接折叠或隐藏疑似纯 AI 生成的评论。

未经消化的 AI 回复不仅将甄别与理解的负担完全推给了 Reviewers，也让你失去了交流与成长的机会。这类无效回复的典型特征包括：机械地复述代码变更却对核心提问答非所问、罗列 CI 早已自动覆盖的本地命令与检查结果，或者用泛泛的代码修改描述来回避具体的设计疑问。

我们非常理解非英语母语者使用 AI 辅助阅读、润色或翻译评论的需求。只要最终生成的内容能够真实反映你个人的思考与想法，使用 AI 协助语言表达是完全被接受的。

## AI 辅助参与审查

使用 AI 协助审查他人代码时同样适用上述标准：你必须亲自通读并完全理解自己发出的每一条意见。未经提炼的 AI 原始审查意见往往冗长且充斥着琐碎细节，会耗费他人大量精力去分辨核心诉求。如果你借助 AI 辅助 Review，请务必先过滤掉无关或自己不理解的内容，并用自己的语言重新组织，确保每条评论都能给出清晰、具体且可落地的改进建议。

## 更好的贡献方式

与其草率地抛出一个未经消化的 AI 代码补丁，我们更鼓励你提交一份**高质量的 Issue**：用清晰的语言描述问题，并提供聚焦于终端用户可见行为的**最小可复现用例（MRE）**。这不仅能极大减轻维护者的排查负担，也为后续高效的社区协作打下坚实的基础。

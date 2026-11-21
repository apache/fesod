---
id: 'security'
title: '安全'
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

Apache Software Foundation 在消除其软件项目中的安全问题方面采取了严格的立场。Apache Fesod 也十分关注与其特性和功能相关的安全问题。

请参阅[安全模型](./security-model.md)，了解 Apache Fesod 的信任边界、密码保护范围，以及应用与部署方的责任。

如果您对 Apache Fesod 的安全性感到担忧，或者您发现了漏洞或潜在的威胁，请不要犹豫与 [Apache 安全团队](https://www.apache.org/security/) 联系，发送邮件至 `security@apache.org`。 在邮件中请指明项目名称为 Apache Fesod，并提供相关问题或潜在威胁的描述。同时推荐重现和复制安全问题的方法。在评估和分析调查结果后，Apache 安全团队和 Apache Fesod PMC 将直接与您回复。

**请注意**：请先在安全邮件中报告该问题，在此之前不要在公开渠道披露。

## 依赖项安全公告

许多组织使用安全扫描工具来识别带有已知安全公告的组件。虽然我们强烈推荐这些工具，因为它们可以提醒用户注意潜在风险，但它们经常会产生误报。这是因为，如果以不可利用的方式使用，易受攻击的依赖项不一定会影响 Apache Fesod。

因此，与 Apache Fesod 依赖项相关的安全公告不会自动被视为关键公告。但是，如果您进一步分析表明 Apache Fesod 可能受到依赖项漏洞的影响，请将您的发现私下报告至 `security@apache.org`。

如果发现依赖项安全公告：

1. 确认我们的 DependencyCheck 抑制列表是否包含相关说明。
2. 查看我们的 Issue 列表是否已有关于该公告的讨论。
3. 分析以确定 Apache Fesod 是否受到影响
   - 如果受到影响，请将您的发现报告至 `security@apache.org`。
   - 如果没有受到影响，请通过更新 DependencyCheck 贡献，并清楚地记录为什么 Apache Fesod 不受影响。

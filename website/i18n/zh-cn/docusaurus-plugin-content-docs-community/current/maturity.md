---
id: 'maturity'
title: '成熟度'
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

# 成熟度

成熟度模型旨在以简洁、概括的方式描述 Apache 项目的运作方式，并为项目提供一个可选用的自我评估基本框架。

更多信息请参阅 [Apache 项目成熟度模型](https://community.apache.org/apache-way/apache-project-maturity-model.html)。

## 评估说明

本自评估在 Apache Fesod (Incubating) 孵化期间编写。

- **最近核验：** 2026-11-12
- **模型版本：** [Apache 项目成熟度模型](https://community.apache.org/apache-way/apache-project-maturity-model.html) v1.4
- **范围：** 项目的社区实践、截至 2.1.0-incubating 的已发布版本，以及核验当日 [apache/fesod](https://github.com/apache/fesod) 的 `main` 分支。凡备注中描述的是 `main` 分支上的变更，均会明确说明该变更是否尚未进入已发布版本。

本文是用于指导社区的自评估，不是评分，也不是毕业结论。

## 成熟度模型评估

下表依据 [Apache 成熟度模型](https://community.apache.org/apache-way/apache-project-maturity-model.html) 填写。欢迎导师和社区成员评论和修改。

### 代码

| **编号** | **描述** | **状态** | **备注** |
|:--------:|----------|:--------:|----------|
| **CD10** | 项目开发开源软件，免费向公众分发。 | **是** :white_check_mark: | 项目源代码采用 `Apache License 2.0` 许可证。 |
| **CD20** | 任何人都可以方便地找到并获取项目代码。 | **是** :white_check_mark: | [官网](https://fesod.apache.org/zh-cn/) 的导航栏提供了项目 [GitHub 仓库](https://github.com/apache/fesod) 的链接。 |
| **CD30** | 任何人都可以使用标准、广泛可用的工具，以可复现的方式构建代码。 | **是** :white_check_mark: | [代码贡献指南](https://fesod.apache.org/zh-cn/community/contribution/contribute-code) 列出了所需的 Maven 和 JDK 版本，要求源码保持 Java 8 兼容，并说明了如何使用 Maven 构建 Apache Fesod (Incubating)。 |
| **CD40** | 项目代码的完整历史保存在源代码管理系统中，任何人都可以据此重建任一已发布版本。 | **是** :white_check_mark: | 项目使用 Git 管理代码，完整历史公开在 [GitHub](https://github.com/apache/fesod) 上。每个版本都打了标签，例如 [2.1.0-incubating](https://github.com/apache/fesod/releases/tag/2.1.0-incubating)，因此可以重建任一已发布版本。 |
| **CD50** | 源代码管理系统基于对提交者的强身份认证，可靠地确定每一行代码的来源。第三方贡献代码时，提交信息会提供关于代码来源的可靠信息。 | **是** :white_check_mark: | 项目仓库托管于 [GitHub](https://github.com/apache/fesod)，仓库访问权限通过 ASF 基础设施管理。Git 历史和 Pull Request 记录了作者和评审信息，第三方贡献遵循贡献指南。 |

### 许可证与版权

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **LC10** | 发布的代码采用 Apache License 2.0。 | **是** :white_check_mark: | 项目采用 [Apache License 2.0](https://github.com/apache/fesod/blob/main/LICENSE)。随项目分发的第三方代码保留其适用的许可证和署名声明。 |
| **LC20** | 项目代码的必需依赖库不会带来比 Apache 许可证更多的限制。 | **是** :white_check_mark: | 必需依赖在各模块的 POM 中声明，例如 [fesod-sheet/pom.xml](https://github.com/apache/fesod/blob/main/fesod-sheet/pom.xml)。按核验当日的 `main` 分支，`fesod-sheet` 解析出的运行时依赖中，Apache POI、Apache Commons、Apache XMLBeans、Log4j API、Ehcache 和 SparseBitSet 采用 Apache License 2.0，SLF4J API 采用 MIT 许可证，curvesapi 采用 BSD 3-Clause 许可证，均属于 [ASF A 类许可证](https://www.apache.org/legal/resolved.html#category-a)。`fesod-shaded` 中重定位的 Spring Framework 类（包括源自 ASM 和 CGLIB 的代码）记录在该模块的 [LICENSE](https://github.com/apache/fesod/blob/main/fesod-shaded/src/main/resources/META-INF/LICENSE) 和 [NOTICE](https://github.com/apache/fesod/blob/main/fesod-shaded/src/main/resources/META-INF/NOTICE) 中。 |
| **LC30** | LC20 中提到的依赖库均为开源软件。 | **是** :white_check_mark: | LC20 列出的必需依赖均为开源软件，其许可证属于 [ASF A 类许可证](https://www.apache.org/legal/resolved.html#category-a)。[发布验证指南](https://fesod.apache.org/zh-cn/community/release/verify-release/) 要求评审者检查每个候选版本的 LICENSE 和 NOTICE 文件。 |
| **LC40** | 提交者受个人贡献者许可协议（"Apache iCLA"）约束，该协议规定了他们可以提交哪些代码，以及如何标识非本人编写的代码。 | **是** :white_check_mark: | 每位 committer 都已签署并提交 Apache iCLA，这是开通 ASF committer 账号的前提条件。 |
| **LC50** | 项目明确界定并记录其所有产出物的版权归属。 | **是** :white_check_mark: | 版权和第三方署名记录在 [NOTICE](https://github.com/apache/fesod/blob/main/NOTICE) 和许可证声明中。在对源代码来源和版权署名进行审查后，`main` 分支已修正署名，并恢复了 Apache POI `DataFormatter` 携带的第三方声明（[#1323](https://github.com/apache/fesod/pull/1323)、[#1330](https://github.com/apache/fesod/pull/1330)、[#1331](https://github.com/apache/fesod/pull/1331)，跟踪于 [#1322](https://github.com/apache/fesod/issues/1322)）。这些变更尚未包含在已发布版本中，最终确认仍待 [LEGAL-737](https://issues.apache.org/jira/browse/LEGAL-737) 给出。 |

### 发布

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **RE10** | 发布物由源代码组成，采用预期可长期读取的标准、开放归档格式分发。 | **是** :white_check_mark: | 源码发布包以 `.tar.gz` 格式通过 [dist.apache.org](https://dist.apache.org/repos/dist/release/incubator/fesod/) 分发，并在 [下载页](https://fesod.apache.org/zh-cn/docs/download/) 提供链接。下载页记录了孵化期间的 4 个版本，从 2.0.0-incubating（2026-01-21）到 2.1.0-incubating（2026-10-07）。 |
| **RE20** | 项目 PMC（项目管理委员会，见 CS10）批准每一次软件发布，使其成为基金会的行为。 | **是** :white_check_mark: | 发布先在 <dev@fesod.apache.org> 投票，再由 IPMC 在 <general@incubator.apache.org> 批准。每个阶段都需要至少三票有约束力的 +1，且有约束力的 +1 多于 -1；只有 IPMC 的投票对 ASF 发布批准具有约束力。参见 [2.1.0 RC7 的 IPMC 投票](https://lists.apache.org/thread/qnyco8wbx32dlmtb0ls2scbkd4j9tsow)。 |
| **RE30** | 发布物经过签名，和/或附带任何人都能可靠地用来校验下载包的摘要。 | **是** :white_check_mark: | 所有源码发布包都经过签名，并附带 `.asc` 签名文件和 `.sha512` 校验文件。[KEYS](https://downloads.apache.org/incubator/fesod/KEYS) 文件可供获取，[下载页](https://fesod.apache.org/zh-cn/docs/download/) 说明了如何校验签名和校验和。 |
| **RE40** | 项目可以在源代码之外分发便利二进制包，但它们不是 Apache Release，不提供任何保证。 | **是** :white_check_mark: | 只有经过投票、通过 [dist.apache.org](https://dist.apache.org/repos/dist/release/incubator/fesod/) 和 [ASF 归档站](https://archive.apache.org/dist/incubator/fesod/) 分发的源码包才是 Apache Release。Maven Central 上对应的二进制构件（如 `org.apache.fesod:fesod-sheet`）是便利二进制包，不属于 Apache Release。 |
| **RE50** | 项目记录了可重复的发布流程，使新加入项目的人也能独立生成发布所需的全部产物。 | **是** :white_check_mark: | [发布指南](https://fesod.apache.org/zh-cn/community/release/release-version/) 说明了如何准备、投票和发布 Apache Fesod (Incubating) 版本。 |

### 质量

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **QU10** | 项目对其代码质量保持开放和坦诚。只要清楚说明，不同模块处于不同的质量和成熟度水平是自然且可以接受的。 | **是** :white_check_mark: | [安全模型](https://fesod.apache.org/zh-cn/community/security-model/) 说明了本库的已知限制，包括解析过程不是沙箱、不可信文档可能消耗大量 CPU、内存或磁盘，以及临时文件、缓存和受密码保护文件的处理方式。已知问题在 [GitHub issues](https://github.com/apache/fesod/issues) 中公开跟踪，不兼容变更列在 [发布说明](https://github.com/apache/fesod/releases) 中。 |
| **QU20** | 项目高度重视开发安全的软件。 | **是** :white_check_mark: | [安全模型](https://fesod.apache.org/zh-cn/community/security-model/) 界定了信任边界、部署方责任和受支持的版本；安全修复只在最新的发布线上提供。安全报告按照 [安全策略](https://fesod.apache.org/zh-cn/community/security/) 私下处理，修复和公告通过 ASF 安全流程协调。 |
| **QU30** | 项目提供有完善文档、安全且私密的安全问题报告渠道，并记录了响应方式。 | **是** :white_check_mark: | [安全页面](https://fesod.apache.org/zh-cn/community/security/) 引导报告者私下发送邮件至 <security@apache.org>，[安全模型](https://fesod.apache.org/zh-cn/community/security-model/) 说明了报告的处理、修复和披露方式。过往的安全公告，如 [CVE-2026-49328](https://fesod.apache.org/zh-cn/blog/2026/06/11/cve-2026-49328) 和 [CVE-2026-107460](https://fesod.apache.org/zh-cn/blog/2026/10/14/cve-2026-107460)，均发布在项目博客上。 |
| **QU40** | 项目高度重视向后兼容，力求记录所有不兼容变更，并提供工具和文档帮助用户过渡到新功能。 | **是** :white_check_mark: | 项目重视向后兼容，并在 [发布说明](https://github.com/apache/fesod/releases) 和 [迁移指南](https://fesod.apache.org/zh-cn/docs/migration/from-fastexcel/) 中记录不兼容变更。例如 [CVE-2026-107460 公告](https://fesod.apache.org/zh-cn/blog/2026/10/14/cve-2026-107460) 说明了 2.1.0-incubating 作为安全修复，默认禁用远程 URL 图片获取。从 FastExcel 迁移时，可以使用 [OpenRewrite 方案](https://fesod.apache.org/zh-cn/blog/2026/05/07/migration-from-fastexcel-with-openrewrite) 自动更新依赖、类名和包导入。 |
| **QU50** | 项目力求及时响应已记录的缺陷报告。 | **是** :white_check_mark: | 社区会响应缺陷报告并跟进修复。例如 [issue #828](https://github.com/apache/fesod/issues/828) 在提交当天就得到了复现回复，之后又更新了 2.0.1-incubating 中的修复情况。 |

### 社区

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **CO10** | 项目有一个知名的主页，提供按照本成熟度模型运作所需的全部信息。 | **是** :white_check_mark: | [官网](https://fesod.apache.org/zh-cn/) 提供了本成熟度模型所需的信息入口，包括下载、文档、社区与贡献指南、安全信息以及本评估。 |
| **CO20** | 社区欢迎任何以善意、尊重的方式行事并为项目带来价值的人参与贡献。 | **是** :white_check_mark: | [通用贡献指南](https://github.com/apache/fesod/blob/main/CONTRIBUTING.md) 以及官网上的 [代码](https://fesod.apache.org/zh-cn/community/contribution/contribute-code) 和 [文档](https://fesod.apache.org/zh-cn/community/contribution/contribute-doc) 贡献指南说明了任何人如何参与。 |
| **CO30** | 贡献包括源代码、文档、有建设性的缺陷报告、有建设性的讨论、市场推广，以及一切能为项目带来价值的事情。 | **是** :white_check_mark: | 代码和非代码贡献都受欢迎，例如文档、翻译、缺陷报告、讨论和博客文章。[文档贡献指南](https://fesod.apache.org/zh-cn/community/contribution/contribute-doc) 介绍了非代码贡献的方式。 |
| **CO40** | 社区力求任人唯贤，随着时间推移，为持续贡献价值的贡献者赋予更多权利和责任。 | **是** :white_check_mark: | 根据截至 2026-11-12 的 [孵化状态页](https://incubator.apache.org/projects/fesod.html)，孵化期间社区新增了 **3** 名 committer，其中 **1** 人随后加入了 PPMC。 |
| **CO50** | 项目记录了贡献者如何获得更多权利（如提交权限或决策权），并始终如一地执行这些原则。 | **是** :white_check_mark: | 项目文档说明了 [成为 committer](https://fesod.apache.org/zh-cn/community/committer/become-committer) 和 [成为 PPMC 成员](https://fesod.apache.org/zh-cn/community/pmc/become-pmc-member) 的要求，以及 PPMC 成员遵循的 [提名流程](https://fesod.apache.org/zh-cn/community/pmc/nominate-committer)。 |
| **CO60** | 社区基于拥有决策权的成员（见 CS10）的共识运作。Apache 项目不欢迎独裁者，无论是否仁慈。 | **是** :white_check_mark: | 决策通过社区讨论和共识做出，必要时进行正式投票。[CI 审批讨论](https://lists.apache.org/thread/d0s8kkshdvdy65fd2jrdf438oxo1c97l) 和 [issue #1009](https://github.com/apache/fesod/issues/1009) 展示了讨论、达成一致和后续落实的过程。 |
| **CO70** | 项目力求及时回答用户问题。 | **是** :white_check_mark: | 社区通过 <dev@fesod.apache.org>、[GitHub issues](https://github.com/apache/fesod/issues) 和 [discussions](https://github.com/apache/fesod/discussions) 回答问题。例如 [issue #828](https://github.com/apache/fesod/issues/828#issuecomment-3794189027) 在几分钟内就得到了首次回复，之后也有持续更新。 |

### 共识建立

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **CS10** | 项目公开维护拥有决策权的贡献者名单。项目的 PMC（项目管理委员会）由这些贡献者组成。 | **是** :white_check_mark: | [团队页面](https://fesod.apache.org/zh-cn/community/team) 列出了全部 PPMC 成员和 committer，并链接到 [ASF committer 权限组名单](https://people.apache.org/committers-by-project.html#fesod)。 |
| **CS20** | 决策需要 PMC 成员达成共识，并记录在项目的主要沟通渠道上。PMC 会考虑社区意见，但拥有最终决定权。 | **是** :white_check_mark: | PPMC 在考虑社区意见的基础上达成共识，并在邮件列表中记录决策。参见 [CI 审批讨论及结论](https://lists.apache.org/thread/d0s8kkshdvdy65fd2jrdf438oxo1c97l)。投票要求取决于决策类型。 |
| **CS30** | 当讨论不足以达成共识时，项目使用有文档记录的投票规则。 | **是** :white_check_mark: | 项目遵循 [ASF 投票规则](https://www.apache.org/foundation/voting.html) 及其文档化的发布流程。[2.0.0 RC3 投票结果](https://www.mail-archive.com/dev@fesod.apache.org/msg00091.html) 分别记录了有约束力和无约束力的投票。 |
| **CS40** | 在 Apache 项目中，否决票仅对代码提交有效。按照 CS30 中的 Apache 投票规则，行使否决权的人必须给出技术理由。 | **是** :white_check_mark: | 根据 [ASF 投票规则](https://www.apache.org/foundation/voting.html)，对代码提交的否决必须给出技术理由，且不能被多数票推翻。发布投票不适用否决。 |
| **CS50** | 所有"重要"讨论都以书面形式在项目的主要沟通渠道上异步进行。影响项目的线下、面对面或私下讨论也会记录在该渠道上。 | **是** :white_check_mark: | 重要讨论及结论异步记录在 <dev@fesod.apache.org> 上。例如 [issue #1009](https://github.com/apache/fesod/issues/1009#issuecomment-5274616763) 将拟议的 CI 策略变更引导到邮件列表，先达成共识再实施。人事事项和未公开的安全问题使用相应的私有渠道。 |

### 独立性

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **IN10** | 项目独立于任何公司或组织的影响。 | **是** :white_check_mark: | Apache Fesod (Incubating) 由社区治理。项目决策通过社区共识做出，没有任何公司拥有单方面的决策权。 |
| **IN20** | 贡献者以个人身份行事，而不是作为公司或组织的代表。 | **是** :white_check_mark: | 贡献者出于自身意愿参与，不代表任何公司或组织。 |

### 商标与品牌

| **编号** | **描述** | **状态** | **备注** |
|----------|----------|:--------:|----------|
| **TB10** | 项目始终使用 "Apache Fesod™" 作为项目和软件产品名称，并带有适当的商标声明。 | **进行中** :construction: | 项目在官网和发布物中一致使用 "Apache Fesod (Incubating)" 这一名称。官网页脚带有通用的 ASF 商标声明，但首页首次显著出现 "Apache Fesod" 的位置和项目 logo 都没有 ™ 标记，页脚也尚未单独声明 Apache Fesod 名称及其 logo 为 ASF 商标。[ASF 项目品牌政策](https://www.apache.org/foundation/marks/pmcs) 要求孵化项目在毕业前满足全部项目品牌要求。 |
| **TB20** | 项目主页位于 `projectname.apache.org`。如因特殊情况使用其他域名，所有非 `apache.org` 域名均归 ASF 所有。 | **是** :white_check_mark: | 项目主页位于 [fesod.apache.org](https://fesod.apache.org/zh-cn/)。 |
| **TB30** | ASF 拥有项目名称、logo 及其他主要品牌元素的商标权（包括任何注册）。 | **是** :white_check_mark: | 名称检索 [PODLINGNAMESEARCH-246](https://issues.apache.org/jira/browse/PODLINGNAMESEARCH-246) 已确认 "Apache Fesod" 是合适的名称，项目名称和 logo 作为 ASF 商标管理。官方 logo 发布在 [品牌页](https://fesod.apache.org/zh-cn/community/brand/)。 |
| **TB40** | 项目监测他人对项目品牌的重大滥用，并向品牌管理团队报告任何潜在的滥用行为。 | **是** :white_check_mark: | PPMC 监测项目品牌可能遭到的滥用，并遵循 [ASF 报告流程](https://www.apache.org/foundation/marks/reporting.html)。 |

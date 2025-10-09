---
id: 'security'
title: '安全'
---

Apache Software Foundation 在消除其软件项目中的安全问题方面采取了严格的立场。Apache Fesod (Incubating) 也十分关注与其特性和功能相关的安全问题。

如果您对 Apache Fesod (Incubating) 的安全性感到担忧，或者您发现了漏洞或潜在的威胁，请不要犹豫与 [Apache 安全团队](http://www.apache.org/security/) 联系，发送邮件至 `security@apache.org`。 在邮件中请指明项目名称为 Apache Fesod (Incubating)，并提供相关问题或潜在威胁的描述。同时推荐重现和复制安全问题的方法。在评估和分析调查结果后，Apache 安全团队和 Apache Fesod (Incubating) 社区将直接与您回复。

请注意 在提交安全邮件之前，请勿在公共领域披露安全电子邮件报告的安全问题。

## 依赖项建议

许多组织使用安全扫描工具来识别已知安全警告的组件。虽然我们强烈推荐这些工具，因为它们可以提醒用户注意潜在风险，但它们经常会产生误报。这是因为，如果以不可利用的方式使用，易受攻击的依赖项不一定会影响 Apache Fesod (Incubating)。

因此，有关 Apache Fesod (Incubating) 依赖项的建议不会自动被视为关键建议。但是，如果您进一步分析表明 Apache Fesod (Incubating) 可能受到依赖项漏洞的影响，请将您的发现报告至 `security@apache.org` 。

如果发现依赖性建议：

1. 验证我们的 DependencyCheck 抑制是否包含相关详细信息。
2. 查看我们的 Issue 页面以了解有关此建议的讨论
3. 分析以确定 Apache Fesod (Incubating) 是否受到影响
   - 如果受到影响，请将您的发现报告至 `security@apache.org`。
   - 如果没有受到影响，请通过更新 DependencyCheck 贡献，并清楚地记录为什么 Apache Fesod (Incubating) 不受影响。

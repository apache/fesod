---
id: 'commit-format'
title: 'Commit 格式规范'
---

所有改进措施均可通过“拉取请求”（PR）来实现。在提交拉取请求之前，请先熟悉以下指南：

### Commit 规范

#### Commit 消息格式

请确保提交消息清晰且具有描述性，务必使用**英文**，且不超过 100 个字符。

允许提交信息的类型且需遵循以下格式：

- **docs**: 更新文档，例如 `docs: update README.md`
- **feature/feat**: 新功能，例如 `feature: support for xxx`
- **bugfix/fix**: 修复 Bug，例如 `fix: fix NPE in the A class`
- **refactor**: 代码重构（不涉及功能变动），例如 `refactor: optimise data processing logic`
- **style**: 代码格式，例如 `style: update code style`
- **test**: 增加或改进测试，例如 `test: add new test cases`
- **chore**: 构建过程或辅助工具的变动，例如 `chore: improve issue template`
- **dependency**: 第三方依赖库的修改，例如 `dependency: upgrade poi version to 5.4.1`

不建议使用模糊的提交信息，如：

- ~~fixed issue~~
- ~~update code~~

如果需要帮助，请参考 [如何编写 Git 提交消息](http://chris.beams.io/posts/git-commit/)。

#### Commit 内容

一次提交应包含完整且可审查的更改，确保：

- 避免提交过于庞大的改动。
- 每次提交内容独立且可通过 CI 测试。

另外，请确保提交时配置正确的 Git 用户信息：

```bash
git config --get user.name
git config --get user.email
```

### PR 描述信息

为了帮助审阅者快速了解 PR 的内容和目的，请使用 [PR 模板](https://github.com/apache/fesod/blob/main/.github/pull_request_template.md)。详细的描述将极大提高代码审阅效率。

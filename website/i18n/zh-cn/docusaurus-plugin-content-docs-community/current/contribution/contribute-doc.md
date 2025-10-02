---
id: 'contribute-doc'
title: '文档贡献指南'
---

本文档贡献指南主要说明如何修改文档并进行贡献。

官方网站采用[Docusaurus](https://docusaurus.io/)构建，文档维护于[website](https://github.com/apache/fesod/tree/main/website)目录。请注意，无论是历史版本还是最新版本的文档，所有修改均应通过向`apache/fesod`仓库提交拉取请求实现。

提交拉取请求的具体方法请参阅：

- [代码贡献指南](./contribute-code.md)
- [Commit格式规范](./commit-format.md)

## 环境要求

- [Node.js](https://nodejs.org/en/download/) 版本 20.0 或更高版本（可通过运行 `node -v` 命令进行检查）。您可使用 [nvm](https://github.com/nvm-sh/nvm) 在同一台机器上管理多个 Node 版本。
- 安装 Node.js 时，建议勾选所有与依赖项相关的复选框。

## 目录结构说明

Docusaurus 支持国际化，主要需要维护的文档目录结构如下:

```bash
.
├── community      # 社区(英文)
├── docs           # 文档(英文)
└── i18n           # 国际化
    └── zh-cn
        ├── docusaurus-plugin-content-docs
        │   └── current   # 文档(简体中文)
        └── docusaurus-plugin-content-docs-community
            └── current   # 社区(简体中文)
```

单语言的文档目录结构如下:

```bash
.
├── quickstart     # 1. 快速开始
├── read           # 2. 读取文件
├── write          # 3. 写入
├── fill           # 4. 填充
├── community      # 5. 社区
└── help           # 6. FAQ
```

## 文档编写指南

- 使用带有 `.md` 扩展名的文件路径

```markdown
[Example](docs/quickstart/example.md)
```

- 使用相对于 `docs/` 目录的路径

```markdown
[Example](docs/quickstart/example.md)
```

- 图片文件需要存储在 `static/img` 目录，并使用相对目录的形式引用.

```markdown
[img](/img/docs/fill/listFill_file.png)
```

## 预览和生成静态文件

进入 `website` 目录，并执行以下命令

### 安装

```bash
pnpm install
```

### 本地预览和运行

```bash
# 英文版
pnpm start

# 简体中文版
pnpm start --locale zh-cn
```

此命令会启动一个本地开发服务器，并打开一个浏览器窗口。大多数更改都无需重启服务器即可实时预览。

> 每次只能运行一种语言版本

## 生成静态文件(可选)

```bash
pnpm build
```

此命令将生成静态内容到`build`目录中，并且可以使用任何静态内容托管服务。

## 文档格式校验

使用 [markdownlint-cli2](https://github.com/DavidAnson/markdownlint-cli2) 来检查文档格式。您在编写了相关 Markdown 文章后，可以在本地执行以下命令，来预先检查 Markdown 的格式内容是否符合要求:

```bash
cd website

pnpm md-lint

# 如果文档错误，可以使用以下命令来尝试自动修复。
pnpm md-lint-fix
```

- Markdown 文章的相关格式规则可以参考 [Markdown-lint-rules](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md)
- 项目中的 Markdown 格式配置文件位于: [.markdownlint-cli2.jsonc](https://github.com/apache/fesod/blob/main/website/.markdownlint-cli2.jsonc)

---

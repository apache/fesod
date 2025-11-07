---
id: 'contribute-doc'
title: 'Docs Contribution'
---

This document contribution guide primarily explains how to modify and contribute to the documents.

The official website is built using [Docusaurus](https://docusaurus.io/), and the documentation is maintained in the [website](https://github.com/apache/fesod/tree/main/website) directory.Please note that whether it is for historical versions or the latest version of the docs, all modifications should be submitted as pull requests on the `apache/fesod` repository.

For how to submit pull requests, please refer to

- [Code Contribution](./contribute-code.md)
- [Commit Format Specification](./commit-format.md)

## Requirements

- [Node.js](https://nodejs.org/en/download/) version 20.0 or above (which can be checked by running `node -v`). You can use [nvm](https://github.com/nvm-sh/nvm) for managing multiple Node versions on a single machine installed.
- When installing Node.js, you are recommended to check all checkboxes related to dependencies.

## Directory Structure Description

Docusaurus supports I18n. The main documentation directory structure that needs to be maintained is as follows:

```bash
.
├── community      # Community(English)
├── docs           # Documentation(English)
└── i18n           # I18n
    └── zh-cn
        ├── docusaurus-plugin-content-docs
        │   └── current   # Documentation(Simplified Chinese)
        └── docusaurus-plugin-content-docs-community
            └── current   # Community(Simplified Chinese)
```

The directory structure for single-language documents is as follows:

```bash
.
├── quickstart     # 1. Quick Start
├── read           # 2. Read
├── write          # 3. Write
├── fill           # 4. Fill
├── community      # 5. Community
└── help           # 6. FAQ
```

## Writing Guidelines

- Use file paths with the `.md` extension

```markdown
[Example](docs/quickstart/example.md)
```

- Use paths relative to the docs/ directory

```markdown
[Example](docs/quickstart/example.md)
```

- Image files must be stored in the `static/img` directory and referenced using relative directory paths.

```markdown
[img](/img/docs/fill/listFill_file.png)
```

## Preview and generate static files

Enter the `website` directory and execute the command

### Installation

```bash
pnpm install
```

### Local Development

```bash
# English
pnpm start

# Simplified Chinese
pnpm start --locale zh-cn
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

> Only one language version can be run at a time.

## Build(Optional)

```bash
pnpm build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service.

## Document Format Inspection

We uses [markdownlint-cli2](https://github.com/DavidAnson/markdownlint-cli2) to check document formatting. After writing the relevant Markdown articles, you can run the following command locally to pre-check whether the Markdown formatting meets the requirements:

```bash
cd website

pnpm md-lint

# If the documentation is wrong, you can use the following command to attempt an automatic repair.
pnpm md-lint-fix
```

- For formatting rules for Markdown articles you can refer to: [Markdown-lint-rules](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md)
- Markdown format configuration file in the project: [.markdownlint-cli2.jsonc](https://github.com/apache/fesod/blob/main/website/.markdownlint-cli2.jsonc)

---

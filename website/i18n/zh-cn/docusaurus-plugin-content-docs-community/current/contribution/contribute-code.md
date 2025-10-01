---
id: 'contribute-code'
title: '代码贡献指南'
---

本节将介绍如何从源代码构建项目，同时也会说明如何贡献代码。

## 工作区准备

### 开发环境

需要 **3.8.1及以上版本的Maven** 和 **17及以上版本的JDK (Java Development Kit)** 。目前，开发环境推荐 **3.9.0** 及以上版本的Maven和 **21** 及以上的版本Java，但在编译过程中必须使用 **Java 1.8** 兼容的语言特性，保证能在 Java 1.8 及以上版本环境中运行。

> 您可以使用 [SDKMAN](https://sdkman.io/) 等工具配置多版本的 Java 工具链。

## IDEA 设置

以下指南针对使用 `IntelliJ IDEA` 开发，其他版本可能存在一些细节差异。请务必遵循所有步骤。

### 工程项目设置

设置项目 [Language Level](https://www.jetbrains.com/help/idea/project-settings-and-structure.html#language-level) 为 `8` 以确保后向兼容。

## Git 使用

确保您已注册 `GitHub` 账号，并按照以下步骤完成本地开发环境配置：

### Fork 仓库

在仓库的 [GitHub 页面](https://github.com/apache/fesod) 点击 `Fork` 按钮，将项目复制到您的 GitHub 账户下，

```bash
https://github.com/<your-username>/fesod
```

### 克隆代码库

运行以下命令将 Fork 的项目克隆到本地：

```bash
git clone git@github.com:<your-username>/fesod.git
```

### 设置上游仓库

将官方仓库设置为 `upstream`，方便同步更新：

```bash
git remote add upstream git@github.com:apache/fesod.git
git remote set-url --push upstream no-pushing
```

运行 `git remote -v` 可检查配置是否正确。

### 新建分支修改

创建一个新的分支以进行开发

```bash
git checkout -b <your_branch_name>
```

**注意事项**:

- 应该创建新分支来开发功能：`git checkout -b feature-xxx`。不建议直接在`main`分支上进行开发。
- `<your_branch_name>` 为您自定义的分支名字。

### 提交代码到远程分支

在本地修改完代码后，将其提交到自己的代码库中。

```bash
git commit -a -m "<you_commit_message>"
git push origin <your_branch_name>
```

## Pull request

### 新建 PR

在浏览器切换到自己的 GitHub 页面，切换分支到提交的分支 `<your_branch_name>`，点击 "Compare & pull request" 按钮进行创建。

### 填写 Commit Message

请参考 [Commit 格式规范](./commit-format.md) 来填写评论摘要和详细内容，然后点击创建拉取请求来创建它。

### 准备审核

成功创建后，社区提交者将进行代码审查，随后会与你讨论具体细节（设计、实现、性能等），之后你可直接根据建议在此分支更新代码（无需创建新PR）。当此PR获得批准时，提交内容将合并至主分支。

## 分支定义

所有贡献应基于 `main` 开发分支。此外，还有以下分支类型：

- **release 分支**：用于版本发布（如 `1.1.0`, `1.1.1`）。
- **feature 分支**：用于开发较大的功能。
- **hotfix 分支**：用于修复重要 Bug。

提交 PR 时，请确保变更基于 `main` 分支。

## 编译

运行以下命令编译

```bash
mvn clean install -DskipTests
```

为了加快构建速度，您可以：

- 使用`-DskipTests`跳过单元测试
- 使用 Maven 的并行构建功能，例如，`mvn clean install -DskipTests -T 1C`

## 代码格式化

使用 [Spotless](https://github.com/diffplug/spotless) 检查并修复代码风格和格式问题。 您可以执行如下的命令，`Spotless` 将会自动检查并修复代码风格和格式问题。

```bash
mvn spotless:apply
```

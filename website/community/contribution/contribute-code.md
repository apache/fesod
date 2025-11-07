---
id: 'contribute-code'
title: 'Code Contribution'
---

This section explains how to build project from source code and also describe how to contribute code.

## Workspace Preparation

To develop this project, you need **Maven 3.9 or above** and **JDK (Java Development Kit) 17 or above**. However, you must use **Java 1.8** compatible language features during compilation to ensure this project can run in environments with Java 1.8 or above.

> You can use tools such as [SDKMAN](https://sdkman.io/) to configure multiple versions of the Java toolchain.

## IDEA Settings

The following guidelines are for development using `IntelliJ IDEA`. Other versions may have some minor differences. Please follow all steps carefully.

### Project Settings

Set the project [Language Level](https://www.jetbrains.com/help/idea/project-settings-and-structure.html#language-level) to `8` to ensure backward compatibility.

## Git Usage

Ensure that you have registered a GitHub account and follow the steps below to configure your local development environment:

### Fork the repository

Click the `Fork` button on the Fesod [GitHub page](https://github.com/apache/fesod) to copy the project to your GitHub account.

```bash
https://github.com/<your-username>/fesod
```

### Clone Repository

Run the following command to clone the forked project to your local machine:

```bash
git clone git@github.com:<your-username>/fesod.git
```

### Set Upstream Repository

Set the official repository as `upstream` for easy synchronization of updates:

```bash
git remote add upstream git@github.com:apache/fesod.git
git remote set-url --push upstream no-pushing
```

Running `git remote -v` can verify if the configuration is correct.

### New branches to modify

Create a new branch to develop

```bash
git checkout -b <your_branch_name>
```

**Note**:

- You must create a new branch to develop features `git checkout -b feature-xxx`. It is not recommended to use the `main` branch for direct development
- `<your_branch_name>` name is customized for you.

### Submit code to remote branch

After modifying the code locally, submit it to your own repository

```bash
git commit -a -m "<you_commit_message>"
git push origin <your_branch_name>
```

## Pull request

### New pull request

Submit changes to the remote repository, you can see a green button "Compare & pull request" on your repository page, click it.

### Fill Commit Message

Please refer to [Commit Format Specification](./commit-format.md) to fill in the summary and details of the comment, and then click Create pull request to create it.

### Ready for review

After successful creation, Then the community Committers will do CodeReview, and then he will discuss some details (design, implementation, performance, etc.) with you, afterward you can directly update the code in this branch according to the suggestions (no need to create a new PR). When this pr is approved, the commit will be merged into the main branch.

## Branch definition

All contributions should be based on the `main` development branch. Additionally, there are the following branch types:

- **release branches**: Used for version releases (e.g., `1.1.0`, `1.1.1`).
- **feature branches**: Used for developing major features.
- **hotfix branches**: Used for fixing critical bugs.

When submitting a PR, please ensure that the changes are based on the `main` branch.

## Compile

Run the following command to compile

```bash
mvn clean install -DskipTests
```

To speed up the build process, you can:

- Use `-DskipTests` to skip unit tests
- Use Maven's parallel build feature, e.g., `mvn clean install -DskipTests -T 1C`

## Code Style

This project uses [Spotless](https://github.com/diffplug/spotless) as its code formatting tool. Please ensure you run the following command to automatically format your code before submitting:

```bash
mvn spotless:apply
```

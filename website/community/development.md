---
id: 'development'
title: 'Run or Build'
---

# Run or Build

This section explains how to build FastExcel from source code.

## Development environment

To develop FastExcel, you need **Maven 3.8.1 or above** and **JDK (Java Development Kit) 17 or above**. Currently, it is recommended to use **Maven 3.9.0 or above** and **Java 21 or above** for the development environment. However, you must use **Java 1.8** compatible language features during compilation to ensure FastExcel can run in environments with Java 1.8 or above.

> You can use tools such as [SDKMAN](https://sdkman.io/) to configure multiple versions of the Java toolchain.

## IDEA Settings

The following guidelines are for development using `IntelliJ IDEA`. Other versions may have some minor differences. Please follow all steps carefully.

### Project Settings

Set the project [Language Level](https://www.jetbrains.com/help/idea/project-settings-and-structure.html#language-level) to `8` to ensure backward compatibility.

## Git Usage

Ensure that you have registered a GitHub account and follow the steps below to configure your local development environment:

**Fork the repository**: Click the `Fork` button on the FastExcel [GitHub page](https://github.com/fast-excel/fastexcel) to copy the project to your GitHub account.

```bash
https://github.com/<your-username>/fastexcel
```

**Clone Repository**: Run the following command to clone the forked project to your local machine:

```bash
git clone git@github.com:<your-username>/fastexcel.git
```

**Set Upstream Repository**: Set the official repository as `upstream` for easy synchronization of updates:

```bash
git remote add upstream git@github.com:fast-excel/fastexcel.git
git remote set-url --push upstream no-pushing
```

Running `git remote -v` can verify if the configuration is correct.

## Compilation

Run the following command to compile

```bash
mvn clean install -DskipTests
```

To speed up the build process, you can:

- Use `-DskipTests` to skip unit tests
- Use Maven's parallel build feature, e.g., `mvn clean install -DskipTests -T 1C`

## Code Style

FastExcel uses [Spotless](https://github.com/diffplug/spotless) as its code formatting tool. Please ensure you run the following command to automatically format your code before submitting:

```bash
mvn spotless:apply
```

## Maintaining the Java Module Descriptor

Fesod ships an optional Java Platform Module System (JPMS) descriptor that is compiled when the build runs on JDK 9 or newer. When you add a new public package or rely on additional reflective access, remember to:

- Update `fesod/src/main/java9/module-info.java` and export any new public packages that should be reachable by module consumers.
- Extend the Java 9+ Surefire arguments (`surefire.jdk9plus.args` property in `fesod/pom.xml`) if new reflective code requires `--add-opens` access to JDK or third-party modules.
- Run `ModuleDefinitionTest` to confirm the module stays open and reflective access continues to work. Maven will execute it automatically via `./mvnw test -pl fesod` on JDK 11+.
- Keep Java 8 compatibility: all new code must still compile with the Java 8 toolchain even after updating the module descriptor.

---
id: 'guide'
title: '指南'
---

# 安装

## 兼容说明

下表列出了各版本 Apache Fesod(Incubating) 基础库对 Java 语言版本最低要求的情况：

| 版本               | jdk版本支持范围    | 备注          |
|------------------|--------------|-------------|
| 2.0.0-incubating | jdk8 - jdk25 | NA(无效)      |
| 1.3.x            | jdk8 - jdk25 | 非 Apache 版本 |
| 1.2.x            | jdk8 - jdk21 | 非 Apache 版本 |
| 1.1.x            | jdk8 - jdk21 | 非 Apache 版本 |
| 1.0.x            | jdk8 - jdk21 | 非 Apache 版本 |

我们强烈建议您使用最新版本的 Apache Fesod(Incubating)，因为最新版本中的性能优化、BUG 修复和新功能都会让您的使用更加方便。

### 依赖说明

Apache Fesod(Incubating) 使用了以下核心依赖：

- **Apache POI 5.5.1** - 用于 Excel 文件处理
- **Apache Commons CSV 1.14.1** - 用于 CSV 文件支持
- **Ehcache 3.9.11** - 用于缓存功能

> 如果您的项目中已经有 POI 相关组件，可能需要手动排除 POI 的相关 jar 包以避免版本冲突。

## 版本更新

您可以在 [版本发布](https://github.com/apache/fesod/releases)
中查询到具体的版本更新细节。您也可以在 [Maven 中心仓库](https://mvnrepository.com/artifact/org.apache.fesod/fesod)
中查询到所有的版本。

## Maven

如果您使用 Maven 进行项目构建，请在 `pom.xml` 文件中引入以下配置：

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>版本号</version>
</dependency>
```

### 可选：使用 BOM 管理版本

为了更好地管理依赖版本，您可以使用 Apache Fesod(Incubating) BOM：

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.fesod</groupId>
            <artifactId>fesod-bom</artifactId>
            <version>版本号</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.apache.fesod</groupId>
        <artifactId>fesod-sheet</artifactId>
        <!-- 版本号由 BOM 管理 -->
    </dependency>
</dependencies>
```

## Gradle

如果您使用 Gradle 进行项目构建，请在 `build.gradle` 文件中引入以下配置：

```gradle
dependencies {
    implementation 'org.apache.fesod:fesod-sheet:版本号'
}
```

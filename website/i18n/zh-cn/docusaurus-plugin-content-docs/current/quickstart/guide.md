---
id: 'guide'
title: '指南'
---

# 安装

## 兼容说明

下表列出了各版本 FastExcel 基础库对 Java 语言版本最低要求的情况：

| 版本    | jdk版本支持范围    | 备注 |
|-------|--------------|----|
| 1.3.x | jdk8 - jdk25 |    |
| 1.2.x | jdk8 - jdk21 |    |
| 1.1.x | jdk8 - jdk21 |    |
| 1.0.x | jdk8 - jdk21 |    |

我们强烈建议您使用最新版本的 FastExcel，因为最新版本中的性能优化、BUG 修复和新功能都会让您的使用更加方便。

> 当前 FastExcel 底层使用 poi 作为基础包，如果您的项目中已经有 poi 相关组件，需要您手动排除 poi 的相关 jar 包。

## 版本更新

您可以在 [版本升级详情](https://github.com/fast-excel/fastexcel/blob/main/CHANGELOG.md)
中查询到具体的版本更新细节。您也可以在 [Maven 中心仓库](https://mvnrepository.com/artifact/cn.idev.excel/fastexcel)
中查询到所有的版本。

## Maven

如果您使用 Maven 进行项目构建，请在 `pom.xml` 文件中引入以下配置：

```xml

<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>版本号</version>
</dependency>
```

## Gradle

如果您使用 Gradle 进行项目构建，请在 `build.gradle` 文件中引入以下配置：

```gradle
dependencies {
    implementation 'cn.idev.excel:fastexcel:版本号'
}
```

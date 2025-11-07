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

<p align="center">
  <a href="https://fesod.apache.org">
     <img alt="fesod" src="logo.svg" width="260">
  </a>
</p>

<p align="center">
<b>Readme</b>:
<b><a href="README.md">English</a></b> | <a href="README_CN.md">中文</a> 
</p>

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/apache/fesod/ci.yml?style=flat-square&logo=github)](https://github.com/apache/fesod/actions/workflows/ci.yml)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/apache/fesod/nightly.yml?style=flat-square&logo=github&label=nightly)](https://github.com/apache/fesod/actions/workflows/nightly.yml)
[![GitHub License](https://img.shields.io/github/license/apache/fesod?logo=apache&style=flat-square)](https://github.com/apache/fesod/blob/main/LICENSE)
![Maven Central Version](https://img.shields.io/maven-central/v/org.apache.fesod/fesod?logo=apachemaven&style=flat-square)
[![Document](https://img.shields.io/github/actions/workflow/status/apache/fesod/ci.yml?style=flat-square&logo=read-the-docs&label=Document)](https://fesod.apache.org/)
[![DeepWiki](https://img.shields.io/badge/DeepWiki-apache%2Ffesod-blue.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACwAAAAyCAYAAAAnWDnqAAAAAXNSR0IArs4c6QAAA05JREFUaEPtmUtyEzEQhtWTQyQLHNak2AB7ZnyXZMEjXMGeK/AIi+QuHrMnbChYY7MIh8g01fJoopFb0uhhEqqcbWTp06/uv1saEDv4O3n3dV60RfP947Mm9/SQc0ICFQgzfc4CYZoTPAswgSJCCUJUnAAoRHOAUOcATwbmVLWdGoH//PB8mnKqScAhsD0kYP3j/Yt5LPQe2KvcXmGvRHcDnpxfL2zOYJ1mFwrryWTz0advv1Ut4CJgf5uhDuDj5eUcAUoahrdY/56ebRWeraTjMt/00Sh3UDtjgHtQNHwcRGOC98BJEAEymycmYcWwOprTgcB6VZ5JK5TAJ+fXGLBm3FDAmn6oPPjR4rKCAoJCal2eAiQp2x0vxTPB3ALO2CRkwmDy5WohzBDwSEFKRwPbknEggCPB/imwrycgxX2NzoMCHhPkDwqYMr9tRcP5qNrMZHkVnOjRMWwLCcr8ohBVb1OMjxLwGCvjTikrsBOiA6fNyCrm8V1rP93iVPpwaE+gO0SsWmPiXB+jikdf6SizrT5qKasx5j8ABbHpFTx+vFXp9EnYQmLx02h1QTTrl6eDqxLnGjporxl3NL3agEvXdT0WmEost648sQOYAeJS9Q7bfUVoMGnjo4AZdUMQku50McDcMWcBPvr0SzbTAFDfvJqwLzgxwATnCgnp4wDl6Aa+Ax283gghmj+vj7feE2KBBRMW3FzOpLOADl0Isb5587h/U4gGvkt5v60Z1VLG8BhYjbzRwyQZemwAd6cCR5/XFWLYZRIMpX39AR0tjaGGiGzLVyhse5C9RKC6ai42ppWPKiBagOvaYk8lO7DajerabOZP46Lby5wKjw1HCRx7p9sVMOWGzb/vA1hwiWc6jm3MvQDTogQkiqIhJV0nBQBTU+3okKCFDy9WwferkHjtxib7t3xIUQtHxnIwtx4mpg26/HfwVNVDb4oI9RHmx5WGelRVlrtiw43zboCLaxv46AZeB3IlTkwouebTr1y2NjSpHz68WNFjHvupy3q8TFn3Hos2IAk4Ju5dCo8B3wP7VPr/FGaKiG+T+v+TQqIrOqMTL1VdWV1DdmcbO8KXBz6esmYWYKPwDL5b5FA1a0hwapHiom0r/cKaoqr+27/XcrS5UwSMbQAAAABJRU5ErkJggg==)](https://deepwiki.com/apache/fesod)

**官网: [fesod.apache.org](https://fesod.apache.org)**    
**邮件: <a href="mailto:dev-subscribe@fesod.apache.org">发送至 ```dev-subscribe@fesod.apache.org```</a>** 订阅邮件列表

## 简介

**Apache Fesod (Incubating)** 是一个高性能、内存高效的 Java 库，用于读写 Excel 文件，旨在简化开发并确保可靠性。

Apache Fesod (Incubating) 可以为开发者和企业提供极大的自由度和灵活性。我们计划在未来引入更多新功能，以持续提升用户体验和工具可用性。Apache Fesod (Incubating) 致力于成为您处理 Excel 文件的最佳选择。

名称 fesod（发音为 `/ˈfɛsɒd/`），是 "fast easy spreadsheet and other documents"（快速简单的电子表格和其他文档）的首字母缩写，表达了项目的起源、背景和愿景。

### 特性

- **高性能读写**：Apache Fesod (Incubating) 专注于性能优化，能够高效处理大规模 Excel 数据。与一些传统的 Excel 处理库相比，它可以显著减少内存消耗。
- **简单易用**：该库提供了简单直观的 API，使开发者能够轻松将其集成到项目中，无论是简单的 Excel 操作还是复杂的数据处理。
- **流式操作**：Apache Fesod (Incubating) 支持流式读取，最大程度地减少一次性加载大量数据的问题。这一设计在处理数十万甚至数百万行数据时尤其重要。

## 安装

Apache Fesod (Incubating) 需要 **Java 1.8** 或更高版本。建议使用最新的 LTS 版本的 Java。我们强烈建议使用最新版本的 Apache Fesod (Incubating)，因为最新版本中的性能优化、错误修复和新功能将提升您的使用体验。

> 目前，Apache Fesod (Incubating) 使用 POI 作为底层包。如果您的项目已包含 POI 相关组件，您需要手动排除 POI 相关的 jar 文件。

> 我们目前正在准备 Apache 孵化器下的第一个版本。当前的版本都是非 Apache 版本。您可以通过此链接查看之前的版本：https://fesod.apache.org/docs/quickstart/guide/

### Maven

如果使用 Maven 进行项目构建，请在 `pom.xml` 文件中添加以下配置：

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>version</version>
</dependency>
```

### Gradle

如果使用 Gradle 进行项目构建，请在 build.gradle 文件中添加以下配置：

```gradle
dependencies {
    implementation 'org.apache.fesod:fesod:version'
}
```

## 快速开始

### 读取

以下是读取 Excel 文档的示例：

```java
// 实现 ReadListener 接口以设置读取数据的操作
public class DemoDataListener implements ReadListener<DemoData> {

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("解析了一条数据" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 读取 Excel 文件
    Fesod.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### 写入

以下是创建 Excel 文档的简单示例：

```java
// 示例数据类
public class DemoData {

    @ExcelProperty("字符串标题")
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ExcelProperty("数字标题")
    private Double doubleData;

    @ExcelIgnore
    private String ignore;
}

// 准备要写入的数据
private static List<DemoData> data() {
    List<DemoData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("String" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 创建一个名为 "Template" 的工作表并写入数据
    Fesod.write(fileName, DemoData.class).sheet("Template").doWrite(data());
}
```

## 社区

### 贡献者

欢迎贡献者加入 Apache Fesod (Incubating)。请查看[贡献指南](./CONTRIBUTING.md)了解如何为该项目做出贡献。

感谢所有已经为 Apache Fesod (Incubating) 做出贡献的人们！

<a href="https://github.com/apache/fesod/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=apache/fesod"/>
</a>

> 注意：由于 GitHub 图片大小限制，仅显示前 100 名贡献者

### 订阅邮件列表

邮件列表是 Apache 社区中最受认可的交流形式。请通过以下邮件列表联系我们。

| 名称                                                | 邮件列表                                                                                                  |
|:----------------------------------------------------|:--------------------------------------------------------------------------------------------------------------|
| [dev@fesod.apache.org](mailto:dev@fesod.apache.org) | [订阅](mailto:dev-subscribe@fesod.apache.org)  ｜  [取消订阅](mailto:dev-unsubscribe@fesod.apache.org) |

### Star 历史

[![Star History Chart](https://api.star-history.com/svg?repos=apache/fesod&type=Date)](https://www.star-history.com/#apache/fesod&Date)

## 许可证

Apache Fesod (Incubating) 项目采用 [Apache License 2.0](LICENSE) 许可证。


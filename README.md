<p align="center">
    <img src="img/readme/logo.svg"/>
</p>

<p align="center">
    <a href="README.md">中文</a> | <a href="README_EN.md">English</a> | <a href="README_JP.md">日本語</a>
</p>

<p align="center">
    <a href="https://github.com/fast-excel/fastexcel/actions/workflows/ci.yml"><img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/fast-excel/fastexcel/ci.yml?style=flat-square&logo=github"></a>
    <a href="https://github.com/fast-excel/fastexcel/blob/main/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/fast-excel/fastexcel?logo=apache&style=flat-square"></a>
    <a href="https://mvnrepository.com/artifact/cn.idev.excel/fastexcel"><img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/cn.idev.excel/fastexcel?logo=apachemaven&style=flat-square"></a>
</p>

## 什么是 FastExcel

FastExcel 是由原 EasyExcel 作者创建的新项目。2023 年我已从阿里离职，近期阿里宣布停止更新 EasyExcel，我决定继续维护和升级这个项目。在重新开始时，我选择为它起名为 FastExcel，以突出这个框架在处理 Excel 文件时的高性能表现，而不仅仅是简单易用。

FastExcel 将始终坚持免费开源，并采用商业友好的 Apache 协议，使其适用于任何商业化场景。这为开发者和企业提供了极大的自由度和灵活性。其一些显著特点包括：

- 完全兼容原 EasyExcel 的所有功能和特性，这使得用户可以无缝过渡。
- 从 EasyExcel 迁移到 FastExcel 只需简单地更换包名和 Maven 依赖即可完成升级。
- 在功能上，比 EasyExcel 提供更多新的特性和改进。

我们计划在未来推出更多新特性，以不断提升用户体验和工具实用性。欢迎大家持续关注 FastExcel 的发展，FastExcel 致力于成为您处理 Excel 文件的最佳选择。

## 主要特性

- **高性能读写**：FastExcel 专注于性能优化，能够高效处理大规模的 Excel 数据。相比一些传统的 Excel 处理库，它能显著降低内存占用。
- **简单易用**：该库提供了简洁直观的 API，使得开发者可以轻松集成到项目中，无论是简单的 Excel 操作还是复杂的数据处理都能快速上手。
- **流式操作**：FastExcel 支持流式读取，将一次性加载大量数据的问题降到最低。这种设计方式在处理数十万甚至上百万行的数据时尤为重要。

## 安装

下表列出了各版本 FastExcel 基础库对 Java 语言版本最低要求的情况：

| 版本    |  jdk版本支持范围   | 备注            |
|-------|:------------:|---------------|
| 1.2.x | jdk8 - jdk21 | 完全兼容easyexcel |
| 1.1.x | jdk8 - jdk21 | 完全兼容easyexcel |
| 1.0.x | jdk8 - jdk21 | 完全兼容easyexcel |

我们强烈建议您使用最新版本的 FastExcel，因为最新版本中的性能优化、BUG修复和新功能都会让您的使用更加方便。

> 当前 FastExcel 底层使用 poi 作为基础包，如果您的项目中已经有 poi 相关组件，需要您手动排除 poi 的相关 jar 包。

### 版本更新

您可以在 [版本升级详情](./CHANGELOG.md) 中查询到具体的版本更新细节。您也可以在[Maven 中心仓库](https://mvnrepository.com/artifact/cn.idev.excel/fastexcel)中查询到所有的版本。

### Maven

如果您使用 Maven 进行项目构建，请在 `pom.xml` 文件中引入以下配置：

```xml
<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>版本号</version>
</dependency>
```

### Gradle

如果您使用 Gradle 进行项目构建，请在 `build.gradle` 文件中引入以下配置：

```gradle
dependencies {
    implementation 'cn.idev.excel:fastexcel:版本号'
}
```

## 示例

### 读取 Excel 文件

下面是读取 Excel 文档的例子：

```java
// 实现 ReadListener 接口，设置读取数据的操作
public class DemoDataListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("解析到一条数据" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 读取 Excel 文件
    FastExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### 创建 Excel 文件

下面是一个创建 Excel 文档的简单例子：

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

// 填充要写入的数据
private static List<DemoData> data() {
    List<DemoData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("字符串" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 创建一个名为“模板”的 sheet 页，并写入数据
    FastExcel.write(fileName, DemoData.class).sheet("模板").doWrite(data());
}
```

## EasyExcel 与 FastExcel

### 区别

- FastExcel 支持所有 EasyExcel 的功能，但是 FastExcel 的性能更好，更稳定。
- FastExcel 与 EasyExcel 的 API 完全一致，可以无缝切换。
- FastExcel 会持续的更新，修复 bug，优化性能，增加新功能。

### 如何升级到 FastExcel

#### 替换依赖

将 EasyExcel 的依赖替换为 FastExcel 的依赖，如下：

```xml
<!-- easyexcel 依赖 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>版本号</version>
</dependency>
```

的依赖替换为

```xml

<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>版本号</version>
</dependency>
```

#### 替换包名

将 EasyExcel 的包名替换为 FastExcel 的包名，如下：

```java
// 将 easyexcel 的包名替换为 FastExcel 的包名

import com.alibaba.excel.*;
```

替换为

```java
import cn.idev.excel.*;
```

### 直接依赖 FastExcel

如果由于种种原因您不想修改代码，可以直接在 `pom.xml` 文件中直接依赖 FastExcel。EasyExcel 与 FastExcel 可以共存，但是长期建议替换为 FastExcel。

### 使用建议

为了兼容性考虑保留了 EasyExcel 类，但是建议以后使用 FastExcel 类，FastExcel 类是 FastExcel 的入口类，功能包含了 EasyExcel 类的所有功能，以后新特性仅在 FastExcel 类中添加。

## 参与贡献

欢迎社区的每一位用户和开发者成为贡献者。无论是报告问题、改进文档、提交代码，还是提供技术支持，您的参与都将帮助 FastExcel 变得更好。请查阅[贡献指南](./CONTRIBUTING.md)来参与贡献。

感谢所有的贡献者们!

<a href="https://github.com/fast-excel/fastexcel/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=fast-excel/fastexcel" />
</a>

> 备注: 由于图片大小的限制，我们默认暂只显示前100名贡献者

## 关注我们

### 公众号

关注作者“程序员小懒“的公众号加入技术交流群，获取更多技术干货和最新动态。

<a><img src="https://github.com/user-attachments/assets/b40aebe8-0552-4fb2-b184-4cb64a5b1229" width="30%"/></a>

### Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fast-excel/fastexcel&type=Date)](https://www.star-history.com/#fast-excel/fastexcel&Date)



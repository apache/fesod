---
id: 'download'
title: '下载'
---

# 下载 Apache Fesod (Incubating)

这是 Apache Fesod (Incubating) 的官方下载页面。提供可从 ASF 发布站点下载的源码发布。二进制构件可通过 Maven
中央仓库获取。

## 最新版本

|        版本        |    发布日期    | 下载                                                                                                                                                                                                                                                                                                                                                                                                        |                                 版本说明                                  |
|:----------------:|:----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------:|
| 2.0.2-incubating | 2026-05-30 | [apache-fesod-2.0.2-incubating-src.tar.gz](https://www.apache.org/dyn/closer.lua/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz ) ([asc](https://downloads.apache.org/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz.asc), [sha512](https://downloads.apache.org/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz.sha512)) | [版本说明](https://github.com/apache/fesod/releases/tag/2.0.2-incubating) |

## 归档版本

|     版本      |    发布日期    | 下载                                                                                                                                                                                                                                                                                                                                                                                                  |                                 版本说明                                  |
|:----------------:|:----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------:|
| 2.0.1-incubating | 2026-02-11 | [apache-fesod-2.0.1-incubating-src.tar.gz](https://www.apache.org/dyn/closer.lua/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz ) ([asc](https://downloads.apache.org/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz.asc), [sha512](https://downloads.apache.org/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz.sha512)) | [版本说明](https://github.com/apache/fesod/releases/tag/2.0.1-incubating) |
| 2.0.0-incubating | 2026-01-24 | NA(Not Available)                                                                                                                                                                                                                                                                                                                                                                                         |                               NA(Not Available)                                |

在这里查看所有历史已归档版本: [archive](https://archive.apache.org/dist/incubator/fesod/).

在这里查看所有 non-Apache 版本: [non-apache releases](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/).

## 验证 Apache 发布版本

在使用前，请参考这份[官方指南](https://www.apache.org/info/verification.html)验证所有 Apache 发布版本，包括验证源码发布的完整性和真实性。

### 下载验证文件

下载包含用于签署发布版本的公钥的 [KEYS](https://downloads.apache.org/incubator/fesod/KEYS) 文件。

### 验证签名

1. 将 KEYS 文件导入您的 GPG 密钥环：

    ```bash
    gpg --import KEYS
    ```

2. 下载源码发布包、.asc 签名文件和 .sha512 校验和文件。

3. 验证 GPG 签名：

   ```bash
   gpg --verify apache-fesod-X.X.X-incubating-src.tar.gz.asc apache-fesod-X.X.X-incubating-src.tar.gz
   ```

### 验证校验和

验证 SHA-512 校验和：

```bash
shasum -a 512 -c apache-fesod-X.X.X-incubating-src.tar.gz.sha512
```

或在 Linux 上：

```bash
sha512sum -c apache-fesod-X.X.X-incubating-src.tar.gz.sha512
```

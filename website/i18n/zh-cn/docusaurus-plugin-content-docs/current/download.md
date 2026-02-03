---
id: 'download'
title: '下载'
---

这是 Apache Fesod (Incubating) 的官方下载页面。Apache Fesod 提供可从 ASF 发布站点下载的源码发布。二进制构件可通过 Maven
中央仓库获取。

# 如何使用 Apache Fesod (Incubating)

## 可用模块

- **fesod-sheet** - Excel/CSV 处理的核心模块（推荐大多数用户使用）
- **fesod-bom** - 用于依赖管理的 BOM（Bill of Materials）
- **fesod-common** - 公共工具类（会随 fesod-sheet 自动引入）
- **fesod-shaded** - 隔离的依赖以避免冲突（会随 fesod-sheet 自动引入）

# Apache 源码发布

## 最新版本

|        版本        |    发布日期    |  源码下载  |  版本说明  |
|:----------------:|:----------:|:------:|:------:|
| 2.0.0-incubating | 2026-01-24 | NA(无效) | NA(无效) |

# 发布版本（非 Apache 版本）

## 历史版本

|  版本   |    发布日期    |                                                                                                                                                  下载地址                                                                                                                                                  |                            版本说明                             |
|:-----:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
| 1.3.0 | 2025-08-23 | [fastexcel-1.3.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.3.0) |

|  版本   |    发布日期    |                                                                                                                                                  下载地址                                                                                                                                                  |                            版本说明                             |
|:-----:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
| 1.2.0 | 2025-04-14 | [fastexcel-1.2.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.2.0) |
| 1.1.0 | 2025-01-14 | [fastexcel-1.1.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.1.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.1.0) |
| 1.0.0 | 2024-12-05 | [fastexcel-1.0.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.0.0) |

# 验证 Apache 发布版本

在使用前必须验证所有 Apache 发布版本。请按以下步骤验证源码发布的完整性和真实性：

## 下载验证文件

下载包含用于签署发布版本的公钥的 [KEYS](https://downloads.apache.org/incubator/fesod/KEYS) 文件。

## 验证签名

1. 将 KEYS 文件导入您的 GPG 密钥环：

    ```bash
    gpg --import KEYS
    ```

2. 下载源码发布包、.asc 签名文件和 .sha512 校验和文件。

3. 验证 GPG 签名：

```bash
gpg --verify apache-fesod-2.0.0-incubating-src.tar.gz.asc apache-fesod-2.0.0-incubating-src.tar.gz
```

## 验证校验和

验证 SHA-512 校验和：

```bash
shasum -a 512 -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

或在 Linux 上：

```bash
sha512sum -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

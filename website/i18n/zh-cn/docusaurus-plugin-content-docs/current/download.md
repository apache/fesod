---
id: 'download'
title: '下载'
---

这是 Apache Fesod (Incubating)的官方下载页面。请从以下表格中选择要下载的版本。建议使用最新版本。

:::tip

我们目前正准备进行在 Apache 孵化器下的首次发布。之前的版本并非基于 Apache 。

:::

## 发布版本（非 Apache 版本）

### 最新版本

|  版本   |    发布日期    |                                                                                                                                                  下载地址                                                                                                                                                  |                            版本说明                             |
|:-----:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
| 1.3.0 | 2025-08-23 | [fastexcel-1.3.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.3.0) |

### 历史版本

|  版本   |    发布日期    |                                                                                                                                                  下载地址                                                                                                                                                  |                            版本说明                             |
|:-----:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
| 1.2.0 | 2025-04-14 | [fastexcel-1.2.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.2.0) |
| 1.1.0 | 2025-01-14 | [fastexcel-1.1.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.1.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.1.0) |
| 1.0.0 | 2024-12-05 | [fastexcel-1.0.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.0.0) |

## 说明

* 在下载版本时，请验证其符合 OpenPGP 标准的签名（如果无法验证，则需检查 SHA-512 签名）；这些文件应从 Apache 主站点获取。
* KEYS 文件包含了用于签署发布版本的公钥。建议（在可能的情况下）采用信任网络的方式来确认这些公钥的身份。

### 验证签名

需要同时下载发布工件及其对应的 .asc 签名文件。然后通过以下步骤验证签名：

* 下载相关发布版本的 KEYS 文件和 .asc 签名文件。
* 将 KEYS 文件导入您的 GPG 密钥环：

```bash
gpg --import KEYS
```

* 使用以下命令验证发布版本的签名:

```bash
gpg --verify <artifact>.asc <artifact>
```

### 验证发布文件的校验码

您需要下载该发布包以及该包的 .sha512 校验码文件。然后通过以下方式验证校验码：

```bash
shasum -a 512 -c <artifact>.sha512
```

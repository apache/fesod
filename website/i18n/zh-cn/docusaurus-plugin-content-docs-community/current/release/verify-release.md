---
id: 'verify-release'
title: '如何验证版本'
---

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

本检查清单所依据的 ASF 要求，请参阅 [Apache Release Policy](https://www.apache.org/legal/release-policy.html)
和 [Release Creation Process](https://infra.apache.org/release-publishing.html)。

## 1. 下载要发布的候选版本

> 验证环节需依赖 GPG 工具，建议预先安装 `gpg` 或 `gpg2`。

:::warning
请确保网络环境畅通，下载耗时取决于网络状况。
:::

首先，设置环境变量以便于后续命令执行（请替换为实际的版本号）：

```shell
# 例如：export RELEASE_VERSION=0.1.0
# 例如：export RC_VERSION=rc1
export RELEASE_VERSION={发布版本号}
export RC_VERSION={RC版本号}

```

下载物料：

```shell
# 方式一：如果本地有 SVN，直接 checkout (推荐)
svn co https://dist.apache.org/repos/dist/dev/fesod/${RELEASE_VERSION}-${RC_VERSION}/ fesod-dist-dev

# 方式二：使用 wget 直接下载特定文件
wget https://dist.apache.org/repos/dist/dev/fesod/${RELEASE_VERSION}-${RC_VERSION}/apache-fesod-${RELEASE_VERSION}-src.tar.gz

```

## 2. 验证上传的版本是否合规

### 2.1 检查发布包完整性

上传到 dist 的包必须包含：

1. **源码包** (Source Package, 必须)
2. **签名文件** (.asc, 必须)
3. **哈希文件** (.sha512, 必须)

### 2.2 检查 GPG 签名

首先导入发布人的公钥。

**2.2.1 导入 KEYS**

```shell
# 下载 KEYS（KEYS 在 release 目录维护，单一来源）
curl https://downloads.apache.org/fesod/KEYS > KEYS

# 导入 KEYS 到本地
gpg --import KEYS

```

**2.2.2 验证签名密钥指纹**

```shell
# 显示项目 KEYS 文件中的密钥指纹
gpg --show-keys --with-fingerprint KEYS

```

将投票邮件中指定密钥的完整指纹与 `KEYS` 中的指纹进行比对；如果发布经理的 Apache 个人
资料中提供了指纹，也应一并核对。导入密钥并不等同于建立信任，请勿仅为消除 GPG 警告而
设置 ownertrust。

**2.2.3 验证签名**

```shell
# 验证源码包
gpg --verify apache-fesod-${RELEASE_VERSION}-src.tar.gz.asc apache-fesod-${RELEASE_VERSION}-src.tar.gz
```

> **检查结果：** 输出中必须出现 **`Good signature`**，且签名密钥指纹必须与上述核验结果
> 一致。仅有 `Good signature` 只能证明内容完整，不能单独证明签名者身份。

### 2.3 检查 SHA512 哈希

**Mac OS / Linux:**

```shell
# 验证源码包
shasum -a 512 --check apache-fesod-${RELEASE_VERSION}-src.tar.gz.sha512

# 或者手动对比
shasum -a 512 apache-fesod-${RELEASE_VERSION}-src.tar.gz
# 查看 .sha512 文件内容进行肉眼比对
cat apache-fesod-${RELEASE_VERSION}-src.tar.gz.sha512

```

**Windows:**

```shell
certUtil -hashfile apache-fesod-${RELEASE_VERSION}-src.tar.gz SHA512

```

## 3. 检查源码包内容 (核心合规项)

解压源码包：

```shell
tar -xvf apache-fesod-${RELEASE_VERSION}-src.tar.gz
cd apache-fesod-${RELEASE_VERSION}-src

```

### 3.1 ASF License Header (RAT 检查)

使用 Maven 插件进行 License 头检查。

```shell
# 运行 RAT 检查
./mvnw apache-rat:check
# 或者如果未配置 wrapper
mvn apache-rat:check

```

**检查结果分析：**
查看生成的报告文件（通常在 `target/rat.txt` 或控制台输出）：

* **Unapproved Licenses:** 必须为 0。
* **Binaries:** 应当为 0 (源码包中不应包含编译后的二进制 jar/class 文件)。

```shell
# 快速查看异常文件 (Mac/Linux)
find . -name rat.txt -print0 | xargs -0 -I file cat file | grep "Unapproved Licenses"

```

### 3.2 源码编译验证

仅使用文档列出的前置条件构建并测试源码包。PMC 成员在投出具有约束力的 `+1` 票之前，
必须在自己控制的硬件上编译并测试候选版本。

```shell
# 首次编译可能需要下载依赖，耗时视网络而定
./mvnw clean install

```

**检查项：**

* [ ] 构建和测试成功。
* [ ] 源码包中**不包含**任何非必要的二进制文件 (如 `.jar`, `.zip`, `.class`)。

### 3.3 许可证合规性检查

进入解压后的目录，人工检查：

* [ ] **LICENSE 文件：** 存在且内容标准 (Apache License 2.0)。
* [ ] **NOTICE 文件：**
* * 存在
* * 年份正确。
* * 如果引入了其他必须在 NOTICE 中声明的依赖，需确认已包含。

## 4. 邮件回复示例

验证完成后，请在开发者邮件列表 (`dev@fesod.apache.org`) 回复投票邮件。

:::tip 特别提示
你是 **Apache Fesod PMC 成员**，你的投票是 **Binding（有约束力）** 的。请务必带上
`(binding)` 后缀。
:::

**回复模板（PMC 成员）：**

```text
+1 (binding)

[X] Download links are valid.
[X] Checksums and signatures are valid.
[X] LICENSE and NOTICE files are correct.
[X] No unexpected binary files are present.
[X] All source files have ASF headers.
[X] The source package builds and tests pass.

My Environment:
- OS: MacOS <版本号> / Linux
- JDK: <JDK版本>
- Maven: <Maven版本>

```

**回复模板（非 PMC 的 Committer 和贡献者）：**

```text
+1 (non-binding)

I have checked:
... (同上)

```

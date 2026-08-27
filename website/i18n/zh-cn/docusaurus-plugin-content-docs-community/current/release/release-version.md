---
id: 'release-version'
title: '如何发布版本'
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

## 1. 准备

### 1.1 版本发布指南

参考以下链接，了解 ASF 版本发布相关指南：

- [Apache Release Guide](http://www.apache.org/dev/release-publishing)
- [Apache Release Policy](http://www.apache.org/dev/release.html)
- [Apache Incubator Release](https://incubator.apache.org/guides/releasemanagement.html)
- [Maven Release Info](http://www.apache.org/dev/publishing-maven-artifacts.html)

### 1.2 PGP签名

如果您是**第一次**作为发布者，请参考 [Apache Releases Signing documentation](https://infra.apache.org/release-signing)，[Cryptography with OpenPGP](http://www.apache.org/dev/openpgp.html) 指南生成一个  `PGP` 密钥, `PGP` 密钥将用于版本签名。

#### 1.2.1 安装

请根据操作系统安装好对应的 `GnuPG 2.x`，推荐安装最新版本。例如

```bash
apt-get install gnupg2
```

#### 1.2.2 生成密钥

请使用您的 **\<your Apache ID>@apache.org** 作为密钥用户标识(USER-ID)，生成一个新的 `gpg` 密钥

```bash
gpg --full-gen-key
```

示例

```bash
$ gpg --full-gen-key
gpg (GnuPG) 2.4.8; Copyright (C) 2025 g10 Code GmbH
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

请选择您要使用的密钥类型：
   (1) RSA 和 RSA
   (2) DSA 和 Elgamal
   (3) DSA（仅用于签名）
   (4) RSA（仅用于签名）
   (9) ECC（签名和加密） *默认*
  (10) ECC（仅用于签名）
 （14）卡中现有密钥
您的选择是？ 1

RSA 密钥的长度应在 1024 位与 4096 位之间。
您想要使用的密钥长度？(3072) 4096
请求的密钥长度是 4096 位
请设定这个密钥的有效期限。
         0 = 密钥永不过期
      <n>  = 密钥在 n 天后过期
      <n>w = 密钥在 n 周后过期
      <n>m = 密钥在 n 月后过期
      <n>y = 密钥在 n 年后过期
密钥的有效期限是？(0) 0
密钥永远不会过期
这些内容正确吗？ (y/N) y

真实姓名： (使用apache id)
电子邮件地址： (使用apache邮箱)
注释： （填写注释）
您选定了此用户标识：
    "用户名 (注释) <邮件地址>"
    
 更改姓名（N）、注释（C）、电子邮件地址（E）或确定（O）/退出（Q）？ o
```

注意事项:

- 密钥类型：根据`gpg`版本，使用安全的密钥类型，推荐至少是 `RSA 和 RSA`
- 密钥长度：建议设置 4096 位
- 有效期：可设置永不过期，也可根据自己需求设置一定的过期时间
- 真实姓名：使用 Apache ID
- 邮箱地址：使用 Apache 邮箱
- 注释：注释信息

确定后输入密码

```bash
┌─────────────────────────────────────────────────────────────┐
│ Please enter the passphrase to                              │
│ protect your new key                                        │
│                                                             │
│ Passphrases match.                                          │
│                                                             │
│ Passphrase: _______________________________________________ │
│                                                             │
│ Repeat: ___________________________________________________ │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │                                                         │ │
│ └─────────────────────────────────────────────────────────┘ │
│        <OK>                                   <Cancel>      │
└─────────────────────────────────────────────────────────────┘
```

#### 1.2.3 查询密钥

查看密钥列表：

```bash
gpg --list-signatures --keyid-format LONG
```

执行后输出本机上所有的密钥列表，通过**\<your Apache ID>@apache.org** 来找到所需的密钥

#### 1.2.4 上传公钥

查询密钥信息

示例：

```bash
$ gpg --list-signatures --keyid-format LONG
[keyboxd]
---------
pub   rsa4096/85E2C3CA72D5936C 2025-12-15 [SC]
      D71C9B1CA898A2408D55EDC785E2C3CA72D5936C
uid                   [ 绝对 ] XXXXX <xxx@apache.org>
sig 3        85E2C3CA72D5936C 2025-12-15  [自签名]
sub   rsa4096/DF02F738216EBD6D 2025-12-15 [E]
sig          85E2C3CA72D5936C 2025-12-15  [自签名]
```

示例中的[ **85E2C3CA72D5936C** ] 便是需要上传的`<key-id>`，将**公钥**上传至 GPG 密钥公网服务器。

```bash
gpg --keyserver keys.openpgp.org --send-key <key-id> 

# 示例
# gpg --keyserver keys.openpgp.org --send-key 85E2C3CA72D5936C
```

`keys.openpgp.org` 是一个随机选择的密钥服务器，也可以使用 `keyserver.ubuntu.com` 或任何其他功能完备的密钥服务器。如果无法执行命令，也可通过访问[OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) ，在线手动上传公钥信息。

上传大约需要一分钟之后，可以检查密钥是否创建成功。如果不成功，您可以多次上传并重试。

```bash
# 检查密钥是否创建成功
gpg --keyserver keyserver.ubuntu.com --recv-keys <key-id> 
```

示例

```bash
$ gpg --keyserver keyserver.ubuntu.com --recv-keys 85E2C3CA72D5936C
gpg: 密钥 85E2C3CA72D5936C：“XXXXXX <xxxxxx@apache.org>” 未改变
gpg: 处理的总数：1
gpg:              未改变：1
```

也可以访问[OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) 网址，输入`key-id`，然后点击`Search key` 按钮，查看是否有对应名称的密钥。

#### 1.2.5 上传 Github

建议将 GPG 公钥上传到 Apache ID 绑定的 GitHub 帐户。

- 输入 `https://github.com/settings/keys` 以添加您的 GPG 密钥。
- 如果添加后发现“未验证”字样，请将 GPG 密钥中使用的电子邮件地址绑定到您的 [GitHub 帐户](https://github.com/settings/emails)。

## 2. 版本发布讨论

向 DEV 邮件列表发送电子邮件来发起下一个版本的讨论

发送至：

```mail
dev@fesod.apache.org
```

主题：

```text
[DISCUSS] Release Apache Fesod(Incubating) ${RELEASE_VERSION}
```

内容：

```text
Hi Community,

This is a call for a discussion to release Apache Fesod(Incubating) ${RELEASE_VERSION}.

The change lists about this release:
https://github.com/apache/fesod/compare/2.0.0-incubating...2.0.1-incubating-rc1

Please leave your comments here about this release plan. We will bump the version in repo and start the release process after the discussion.

Best regards,
${RELEASE_MANAGER}
```

- ${RELEASE_VERSION}：替换为下一个要发布的版本号
- ${RELEASE_MANAGER}：一般为这个版本的版本经理

## 3 物料准备

### 3.1 KEYS 文件

如果您是**第一次**作为发布者或原来的密钥已过期，请将**公钥**追加到 Apache SVN 项目 **release 仓库**的 **KEYS** 文件中。KEYS 文件在单独权威位置（release 目录）维护，以保证一致性，并可在 downloads.apache.org 上用于签名验证：

- Release 仓库：<https://dist.apache.org/repos/dist/release/incubator/fesod>

操作步骤：

```bash
# 检出到本地 fesod-release 目录
svn co https://dist.apache.org/repos/dist/release/incubator/fesod/ fesod-release

# 进入本地目录
cd fesod-release

# 导出并追加到 KEYS 文件
(gpg --list-sigs xxx@apache.org && gpg --export --armor  xxx@apache.org) >> KEYS

# 提交
svn ci -m "add gpg key for xxx"
```

注意事项：

- 请不要直接覆盖仓库中 `KEYS`文件，只能**追加**
- `KEYS` 文件只在 **release** 目录维护（单一权威来源），**不要**在 dev 目录再保留一份，否则两份内容容易不同步。
- SVN 仓库需要 PPMC 权限，可由 PPMC 成员协助您上传。

### 3.2 POM 配置

配置本地 POM 文件，以便将版本推送到 ASF Nexus 仓库。

#### 3.2.1 项目工程

确保项目工程 `pom.xml` 文件设置

```xml
<parent>
    <groupId>org.apache</groupId>
    <artifactId>apache</artifactId>
    <version>31</version>
</parent>
```

#### 3.2.1 Maven设置

在本地 Apache Maven 配置文件 `settings.xml` 中设置 Apache ID 和 GPG 密钥信息

```xml
<settings>
    <profiles>
        <profile>
            <id>apache-release</id>
            <properties>
                <mavenExecutorId>forked-path</mavenExecutorId>
                <!-- gpg 密钥的 key-id -->
                <gpg.keyname>yourKeyName</gpg.keyname>
                <deploy.url>https://dist.apache.org/repos/dist/dev/incubator/fesod/</deploy.url>
            </properties>
        </profile>
    </profiles>
    <servers>
        <!-- Apache ID 和密码 -->
        <server>
            <id>apache.snapshots.https</id>
            <username>yourApacheID</username>
            <password>yourApachePassword</password>
        </server>
        <server>
            <id>apache.releases.https</id>
            <username>yourApacheID</username>
            <password>yourApachePassword</password>
        </server>
      
        <!-- gpg 密码 -->
        <server>
            <id>gpg.passphrase</id>
            <passphrase>yourKeyPassword</passphrase>
        </server>
    </servers>
</settings>
```

建议加密配置，可参考如下指南：

- [Publishing Maven Releases to Maven Central Repository](https://infra.apache.org/publishing-maven-artifacts.html)
- [Maven's password encryption capabilities](http://maven.apache.org/guides/mini/guide-encryption.html)

### 3.3 版本物料

#### 3.3.1 预发布版本

例如要发布 `2.0.0-incubating` 版本，需要进行如下的操作：

- 创建一个新分支`2.0.0-incubating`作为发布分支
- 修改 `pom.xml` 中的版本号为 `2.0.0-incubating`
- 推送 RC(Release Candidates) 版本标签

```bash
# 切换到发布分支
git checkout -b 2.0.0-incubating

# 创建 RC1 版本Tag
git tag -s 2.0.0-incubating-rc1 -m "release: release for 2.0.0-incubating RC1"

# 推送 Tag 到远程仓库
git push origin 2.0.0-incubating-rc1
```

#### 3.3.2 推送二进制包

编译预发布RC版本分支源码，并推送二进制包到 [预发仓库](https://repository.apache.org/#stagingRepositories)

```bash
mvn clean deploy -Papache-release -DskipTests -Dgpg.skip=false
```

使用 Apache ID 登录 ASF Nexus 仓库，找到发布的版本并点击 **Close**。

> 如果 close 失败很可能是因为签名的秘钥对应的公钥在`keys.openpgp.org`中无法获取到，请自行通过[OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) 检查。

#### 3.3.3 打包源代码

:::caution 注意
请勿在日常工作目录中打包！
:::

> 诸如 `node_modules`、IDE 配置文件（例如 `.idea`、`.vscode`）或重构后残留的空目录等本地文件，可能意外被打包到 `source-release.zip` 中。这将导致合规性问题（例如分发未经授权的二进制文件），并引发投票失败。

您**必须**在**全新克隆的Git仓库**中执行发布流程，以确保构建产物可复现且干净。

**注意**：请勿用 IDE（如IntelliJ或VS Code）打开源码目录，否则可能生成配置文件或编译缓存。请先在终端直接运行Maven发布命令。

使用 `git archive` 确保源码包的纯净（不包含 .git 目录或其他忽略文件）。

```bash
git archive --format=tar.gz \
  --prefix=apache-fesod-2.0.0-incubating-src/ \
  -o apache-fesod-2.0.0-incubating-src.tar.gz \
  e7546d1138d4d3a638df10193a4c29c50a7e55d8
```

> **注意**：这里的 hash `e7546d1138d4d3a638df10193a4c29c50a7e55d8` 对应 tag `2.0.0-incubating-rc1` 的 commit hash。

#### 3.3.4 签名

对生成的源码包进行 GPG 签名和 SHA512 计算。

```bash
# 1. 指定 GPG key-id 对源码包签名 (.asc)
# 请将 <key-id> 替换成 GPG 的 key-id
for i in *.tar.gz; do echo "Signing $i"; gpg -u <key-id> --armor --output $i.asc --detach-sig $i ; done

# 2. 生成 SHA512 校验和 (.sha512)
for i in *.tar.gz; do echo "Hashing $i"; sha512sum $i > $i.sha512 ; done
```

签名完成后可进行本地验证

```bash
# 验证签名
for i in *.tar.gz; do echo $i; gpg --verify $i.asc $i ; done

# 验证 SHA512
for i in *.tar.gz; do echo $i; sha512sum --check $i.sha512; done
```

#### 3.3.5 上传至 SVN

将签好名的源码包上传到 SVN 仓库

```bash
# 检出 SVN dev 仓库
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/ fesod-dev
cd fesod-dev

# 创建版本目录
mkdir 2.0.0-incubating-rc1
cd 2.0.0-incubating-rc1

# 复制源代码包文件 (假设文件在上一级目录)
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.asc .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.sha512 .

# 提交到 SVN
cd ..
svn add 2.0.0-incubating-rc1
svn commit -m "Add 2.0.0-incubating-rc1 source release"
```

使用 SVN 仓库需要先设置好用户，也可以在每个命令行中指定用户名和密码，例如

```bash
svn co --username <apache-id> --password <password>
```

如果某些文件是意外出现或者发生某些错误，则需要删除相关内容并执行 `svn delete`，然后重复上述上传过程。

## 4.社区投票

每个版本可经历多轮次投票，其中 RC N 和 Round N 代表 N 次数，该版本的第几次投票

### 4.1 发起投票

发送至：

```mail
dev@fesod.apache.org
```

标题：

```text
[VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

正文：

```text
Hi Community,

This is a call for vote to release Apache Fesod(Incubating) ${RELEASE_VERSION}.

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/2.0.0-incubating-rc1

The staging repo:
https://repository.apache.org/content/repositories/orgapachefesod-1016

Git tag for the release:
https://github.com/apache/fesod/releases/tag/2.0.0-incubating-rc1

Hash for the release tag:
e7546d1138d4d3a638df10193a4c29c50a7e55d8

Release Notes:
https://github.com/apache/fesod/releases/tag/2.0.0--rc1

The artifacts have been signed with Key [ key-id ], corresponding
to
[ xxx@apache.org ]
which can be found in the keys file:
https://downloads.apache.org/incubator/fesod/KEYS

Build Environment: JDK 8+, Apache Maven 3.6.0+.
./mvnw clean package -DskipTests

The vote will be open for at least 72 hours.

Please vote accordingly:

[ ] +1 approve
[ ] +0 no opinion
[ ] -1 disapprove with the reason

Checklist for reference:

[ ] Download links are valid.
[ ] Checksums and signatures.
[ ] LICENSE/NOTICE files exist
[ ] No unexpected binary files
[ ] All source files have ASF headers
[ ] Can compile from source

To learn more about Apache Fesod , please see https://fesod.apache.org/
```

将正文中所需内容更正为实际的`${RELEASE_VERSION}`的`RCN`轮次的内容

### 4.2 完成投票

**投票持续至少 72 小时并至少获得 3个 +1 的 binding 票** 之后，发布投票结果：

发送至：

```mail
dev@fesod.apache.org
```

标题：

```text
[RESULT][VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

正文：

```text
Hi Community,

The vote to release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN-RCN has
PASSED and closed now.

The result is as follows:

3 binding +1 Votes:
- XXX
- XXX
- XXX

2 non-binding +1 Votes:
- XXX
- XXX

no further 0 or -1 votes.

The vote thread:
https://lists.apache.org/thread/3b8vz8891cjtjblthvnx95l44sh2fmqz

Thank you for reviewing and voting for our release candidate.

We will now bring this vote to the general@incubator mailing list for
IPMC approval.
```

如果投票失败，先解决提出的问题，然后按照版本发布流程，准备新一轮`RCN` 的物料和发起新一轮投票。

## 5 孵化器投票

社区投票通过并完成后，需要向孵化器邮件列表发起投票。

### 5.1 发起投票

与社区发起投票类似，但是需要增加社区投票通过相关的邮件链接，以证明已在社区内达成一致。

发送至：

```mail
general@incubator.apache.org
```

标题：

```text
[VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

正文：

```text
Hi all,

This is a call for vote to release Apache Fesod(Incubating) ${RELEASE_VERSION}-RCN.

The Apache Fesod community has voted and approved the release of Apache
Fesod(Incubating) ${RELEASE_VERSION}. We now kindly request the IPMC members
review and vote for this release.

Project description: Apache Fesod (Incubating) is a high-performance,
easy-to-use Java library for processing spreadsheet files.

The vote thread:
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

The vote result thread:
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x/

The staging repository:
https://repository.apache.org/content/repositories/${STAGING.RELEASE}/

Git tag for the release:
https://github.com/apache/fesod/releases/tag/vx.x.x

Hash for the release tag:
ea6687ff9da3d3389c51a39852d84f3a209708c4

Release Notes:
https://github.com/apache/fesod/releases/tag/vx.x.x

The artifacts have been signed with Key [ key-id ], corresponding
to [ xxxx@apache.org ] which can be found in the keys file:
https://downloads.apache.org/incubator/fesod/KEYS

Build Environment: JDK 8+, Apache Maven 3.6.0+.
./mvnw clean package -DskipTests

The vote will be open for at least 72 hours.

Please vote accordingly:

[ ] +1 approve
[ ] +0 no opinion
[ ] -1 disapprove with the reason

Checklist for reference:

[ ] Download links are valid.
[ ] Checksums and signatures.
[ ] LICENSE/NOTICE files exist
[ ] No unexpected binary files
[ ] All source files have ASF headers
[ ] Can compile from source

To learn more about Apache Fesod , please see https://fesod.apache.org/
```

### 5.2 公示孵化器投票结果

**投票持续至少 72 小时并至少获得 3个 +1 的 binding 票** 之后，发布投票结果：

发送至：

```mail
general@incubator.apache.org
```

标题：

```text
[RESULT][VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

正文：

```text
Hi Incubator PMC,

The vote to release Apache Fesod(Incubating) ${RELEASE_VERSION}-RCN has passed with
3 +1 binding and 1 +1 non-binding votes, no +0 or -1 votes.

Binding votes：

- XXX
- XXX
- XXX

Non-Binding votes:

- XXX

Vote thread:
https://lists.apache.org/thread/o7vwdvtolclcv1y4j4ozshj923ppwlnl

Thanks for reviewing and voting for our release candidate. We will
proceed with publishing the approved artifacts and sending out the
announcement soon.
```

# 6.完成发布

## 6.1 迁移源代码和二进制包

登录 Apache Nexus 仓库, 选择之前进行 close 过的的 **orgapachefesod-XXX** 点击 `Release` 图标发布

1. 将 dev下的签名文件、src、bin移动到release路径下，参考如下命令：

   ```bash
   svn mv https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x-RCN https://dist.apache.org/repos/dist/release/incubator/fesod/x.x.x -m "Release X.X.X"
   ```

2. 将之前release note设置为Set as the latest release并提交

3. 将x.x.x的文档更新至fesod官网中，并补充对应binary和source的下载链接

## 6.2 版本公示

发送至：

```mail
announce@apache.org,dev@fesod.apache.org
```

标题：

```text
[ANNOUNCE] Apache Fesod(Incubating) ${RELEASE_VERSION} released
```

正文：

```text
Hello everyone,

The Apache Fesod(Incubating) ${RELEASE_VERSION} has been released!

Apache Fesod is an easy-to-use, high-performance, open source distributed transaction solution.

Download Links:
https://fesod.apache.org/download/fesod/

Release Notes:
https://github.com/apache/fesod/releases/tag/x.x.x/

Website:
https://fesod.apache.org/

Resources:
- Issue: https://github.com/apache/fesod/issues
- Mailing list: dev@fesod.apache.org

We would like to thank all the contributors who made this release possible, and to
our Incubator mentors for their great guidance.

${RELEASE_MANAGER}
On behalf of Apache Fesod (Incubating) PPMC
```

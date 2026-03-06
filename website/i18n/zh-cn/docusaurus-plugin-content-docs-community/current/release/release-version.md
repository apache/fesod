---
id: 'release-version'
title: '如何发布版本'
---

# 1. 前言

## 1.1 Apache 版本发布文档

参考以下链接，了解 ASF 版本发布流程：

- [Apache Release Guide](http://www.apache.org/dev/release-publishing)
- [Apache Release Policy](http://www.apache.org/dev/release.html)
- [Maven Release Info](http://www.apache.org/dev/publishing-maven-artifacts.html)

## 1.2 PGP 签名

遵循 Apache 版本发布指南，对发布版本签名，用户也可据此判断下载的版本是否被篡改。

创建 `pgp` 密钥用于版本签名，使用 **\<your Apache ID>@apache.org** 作为密钥 USER-ID

详情可参考 [Apache Releases Signing documentation](https://infra.apache.org/release-signing)，[Cryptography with OpenPGP](http://www.apache.org/dev/openpgp.html)

生成密钥的简要流程：

- 通过`gpg --full-gen-key` 生成一个新的 `gpg` 密钥, 设置密钥长度为 4096

  注：可设置永不过期，也可根据自己需求设置一定的过期时间，但需要在过期后更新的公钥到[DEV KEYS file](https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS) 和 [RELEASE KEYS file](https://dist.apache.org/repos/dist/release/incubator/fesod/KEYS)

- 通过 `gpg --keyserver keys.openpgp.org --send-key <your key id>` 上传密钥到公钥服务器

  注：如若访问不通，可通过[OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) 在线上传公钥

  ```bash
  使用该命令可查到keyid如：gpg --list-signatures --keyid-format LONG
  pub   rsa4096/XXXXXXXX 2025-12-15 [SC] [有效至：2027-12-15]
        F2D3A28A392129B927C7FB42XXXXXXXX
  uid                   [ 绝对 ] xxxx <xxxx@apache.org>
  sig 3        XXXXXXXX 2025-12-15  [自签名]
  sub   rsa4096/XXXXX 2025-12-15 [E] [有效至：2027-12-15]
  sig          XXXXXXXX 2025-12-15  [自签名]
  ```

- 通过 `gpg --armor --output ./public-key.txt --export XXXXXXXX` 导出公钥到文本文件

- 将生成的密钥追加到[DEV KEYS file](https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS) 和 [RELEASE KEYS file](https://dist.apache.org/repos/dist/release/incubator/fesod/KEYS)

注意：

DEV SVN 仓库可以由 Release Manager 自行添加，Release SVN 仓库需要 PMC 权限，可以由 PMC 协助将 KEY 进行上传。

**Tips:** 需要设置默认公钥, 若有多个公钥，请修改 `~/.gnupg/gpg.conf`

参考示例：

```text
gpg (GnuPG) 2.2.4; Copyright (C) 2017 Free Software Foundation, Inc.
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Please select what kind of key you want:
  (1) RSA and RSA (default)
  (2) DSA and Elgamal
  (3) DSA (sign only)
  (4) RSA (sign only)
Your selection? 1
RSA keys may be between 1024 and 4096 bits long.
What keysize do you want? (2048) 4096
Requested keysize is 4096 bits
Please specify how long the key should be valid.
        0 = key does not expire
     <n>  = key expires in n days
     <n>w = key expires in n weeks
     <n>m = key expires in n months
     <n>y = key expires in n years
Key is valid for? (0)
Key does not expire at all
Is this correct? (y/N) y

GnuPG needs to construct a user ID to identify your key.

Real name: （设置用户名）(使用apache id)
Email address: （设置邮件地址）(使用apache邮箱)
Comment: （填写注释）
You selected this USER-ID:
   "用户名 (注释) <邮件地址>"

Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? O
You need a Passphrase to protect your secret key. （设置密码）
```

将生成的公钥和私钥转化为 ASCII 形式：

```bash
gpg --armor --output ./public-key.txt --export XXXXXXXX
gpg --armor --output ./private-key.txt --export-secret-keys XXXXXXXX
```

查看密钥列表：

```text
[root@localhost ~]# gpg --list-signatures --keyid-format LONG
[keyboxd]
---------
pub   rsa4096/XXXXXXXX 2025-12-15 [SC] [有效至：2027-12-15]
      D71C9B1CA898A2408D55EDCXXXXXXXX
uid                   [ 绝对 ] xxxx <xxxx@apache.org>
sig 3        XXXXXXXX 2025-12-15  [自签名]
sub   rsa4096/XXXXX 2025-12-15 [E] [有效至：2027-12-15]
sig          XXXXXXXX 2025-12-15  [自签名]
```

上传公钥到公钥服务器

```bash
[root@localhost gpgtest]# gpg --keyserver keys.openpgp.org --send-key XXXXXXXX
gpg: sending key XXXXXXXX to hkp server keys.openpgp.org
```

## 1.3 POM 配置

配置 POM 文件，以便将版本部署到 ASF Nexus 仓库。

① 添加 Apache POM 继承默认设置

```xml
<parent>
    <groupId>org.apache</groupId>
    <artifactId>apache</artifactId>
    <version>31</version>
</parent>
```

② Maven 配置文件 `settings.xml` 中添加密钥信息

```xml
<settings>
    <profiles>
        <profile>
            <id>signed_release</id>
            <properties>
                <mavenExecutorId>forked-path</mavenExecutorId>
                <gpg.keyname>yourKeyName</gpg.keyname>
          <deploy.url>https://dist.apache.org/repos/dist/dev/incubator/fesod/</deploy.url>
            </properties>
        </profile>
    </profiles>
    <servers>
        <!-- To publish a snapshot of some part of Maven -->
        <server>
            <id>apache.snapshots.https</id>
            <username>yourApacheID</username>
            <!-- Use the password encryption by maven -->
            <password>yourApachePassword</password>
        </server>
        <!-- To stage a release of some part of Maven -->
        <server>
            <id>apache.releases.https</id>
            <username>yourApacheID</username>
            <password>yourApachePassword</password>
        </server>
        <server>
            <id>gpg.passphrase</id>
            <passphrase>yourKeyPassword</passphrase>
        </server>
    </servers>
</settings>
```

**Tips:** 推荐使用 [Maven's password encryption capabilities](http://maven.apache.org/guides/mini/guide-encryption.html) 加密 `gpg.passphrase`

# 2.发布流程

## 2.1 准备分支

从主干分支拉取新分支作为发布分支，如现在要发布 `${release_version}` 版本，则从开发分支拉出新分支 `${release_version}`，此后`${release_version}` Release Candidates 涉及的修改及打标签等都在`${release_version}`分支进行，并保证该分支的github actions ci全部通过，最终发布完成后合入主干分支。

例：如 Java SDK 需要发布 `2.2.0-incubating` 版本，从 `main` 分支拉出新分支 `release-2.2.0-incubating`，并在此分支提交从 Snapshot版本号 替换为 `2.2.0-incubating` 版本号的 commit。

### 2.2 预发布二进制包和源码

### 2.2.1 SDK根据 [publishing maven artifacts](https://infra.apache.org/publishing-maven-artifacts.html) [4] 的说明准备发布

```bash
mvn clean deploy -Papache-release -DskipTests -Dgpg.skip=false
```

此时，fesod sdk被发布到 [预发仓库](https://repository.apache.org/#stagingRepositories) （需要apache账号密码登录），找到发布的版本，即 `${STAGING.RELEASE}`， 并点击 Close。

注：如果close失败很可能是因为签名的秘钥对应的公钥在keys.openpgp.org中无法获取到，请自行通过[OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) 检查

### 2.2.2 打包源代码 (Package Source)

:::caution 注意
请勿在日常工作目录中运行发布流程！
:::

> 诸如 `node_modules`、IDE 配置文件（例如 `.idea`、`.vscode`）或重构后残留的空目录等本地文件，可能意外被打包到 `source-release.zip` 中。这将导致合规性问题（例如分发未经授权的二进制文件），并引发投票失败。

您**必须**在**全新克隆的Git仓库**中执行发布流程，以确保构建产物可复现且干净。

**注意**：请勿立即用IDE（如IntelliJ或VS Code）打开此目录，否则可能生成配置文件或编译缓存。请先在终端直接运行Maven发布命令。

首先，确认当前代码库处于准备发布的状态。

```bash
# 1. 切换到主分支并更新
git checkout main
git pull

# 2. 创建 GPG 签名的 Tag
# 注意：请确保 -m 中的内容准确
git tag -s 2.0.0-incubating-rc1 -m "release: release for 2.0.0-incubating RC1"

# 3. 推送 Tag 到远程仓库
git push git@github.com:apache/fesod.git 2.0.0-incubating-rc1
```

使用 `git archive` 确保源码包的纯净（不包含 .git 目录或其他忽略文件）。

```bash
# 1. 导出源码包
git archive --format=tar.gz \
  --prefix=apache-fesod-2.0.0-incubating-src/ \
  -o apache-fesod-2.0.0-incubating-src.tar.gz \
  e7546d1138d4d3a638df10193a4c29c50a7e55d8
```

> **注意**：这里的 hash `e7546d11...` 对应 tag `2.0.0-incubating-rc1` 的 commit hash。

### 2.2.3 签名与校验 (Sign and Hash)

对生成的源码包进行 GPG 签名和 SHA512 计算。

```bash
# 1. GPG 签名 (.asc)
for i in *.tar.gz; do 
  echo "Signing $i"; 
  gpg --armor --output $i.asc --detach-sig $i ; 
done

# 2. 生成 SHA512 校验和 (.sha512)
for i in *.tar.gz; do 
  echo "Hashing $i"; 
  sha512sum $i > $i.sha512 ; 
done

# 3. 验证 (可选)
gpg --verify apache-fesod-2.0.0-incubating-src.tar.gz.asc apache-fesod-2.0.0-incubating-src.tar.gz
sha512sum -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

### 2.2.4 上传源码包至 SVN (Upload to Dist)

将签好名的源码包上传到 Apache 开发分发区 (`dist/dev`)。

```bash
# 1. 检出 SVN dev 仓库
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/ fesod-dev
cd fesod-dev

# 2. 创建版本目录
mkdir 2.0.0-incubating-rc1
cd 2.0.0-incubating-rc1

# 3. 复制文件 (假设文件在上一级目录)
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.asc .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.sha512 .

# 4. 提交到 SVN
cd ..
svn add 2.0.0-incubating-rc1
svn commit -m "Add 2.0.0-incubating-rc1 source release"
```

---

# 3.投票阶段

## 3.1 社区内部投票

**投票持续至少 72 小时并获得 3 个+1 binding票**

发送至：

```mail
dev@fesod.apache.org
```

标题：

`[VOTE]Release Apache Fesod (Incubating) x.x.x-RCN (RoundN)`

RC N和Round N的N代表次数，该版本的第几次投票

正文：

```text
Hi Fesod Community,

This is a call for vote to release Apache Fesod(incubating) 2.0.0-incubating.

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/2.0.0-incubating-rc1

The staging repo:
https://repository.apache.org/content/repositories/orgapachefesod-1016

Git tag for the release:
https://github.com/apache/fesod/releases/tag/2.0.0-incubating-rc1

Hash for the release tag:
e7546d1138d4d3a638df10193a4c29c50a7e55d8

Release Notes:
https://github.com/apache/fesod/releases/tag/2.0.0-incubating-rc1

The artifacts have been signed with Key [ 72D5936C ], corresponding
to
[ psxjoy@apache.org ]
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

### 3.1.2 完成投票

发布投票通过邮件

```text
Hi Community,


The vote to release Apache Fesod (Incubating) vx.x.x-RCN has passed
with 3 +1 binding votes, and no +0 or -1 votes.

3 (+1 binding)

- XXX

- XXX

- XXX

no further 0 or -1 votes.


The vote thread:
所对应投票邮件的thread链接，如:
https://lists.apache.org/thread/rwco6lms9qo10whjj8gg1dr8j7drl2gf

Thank you for reviewing and voting for our release candidate.

We will soon launch the second stage of voting.
```

## 3.2 孵化器投票

### 3.2.1 孵化器中投票

与社区投票类似，但是需要增加社区投票相关的thread链接，以证明已在社区内达成一致

发送邮件至 `general@incubator.apache.org`

标题：

`[VOTE]Release Apache Fesod (Incubating) x.x.x-RCN`

**投票持续至少 72 小时并获得 3 个+1 binding票**

```text
Hello everyone,

This is a call for vote to release Apache Fesod(incubating) vx.x.x

The Apache Fesod community has voted and approved the release of Apache
Fesod(incubating) vx.x.x. We now kindly request the IPMC members
review and vote for this release.


The vote thread:
社区中投票的thread链接, 如：
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

Vote Result:
社区中投票通过的result thread链接，如：
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x/

The staging repo:
https://repository.apache.org/content/repositories/${STAGING.RELEASE}/

Git tag for the release:
https://github.com/apache/fesod/releases/tag/vx.x.x

Hash for the release tag:
tag分支最后一条commit的id

Release Notes:
https://github.com/apache/fesod/releases/tag/vx.x.x

The artifacts have been signed with Key [ key-id ], corresponding
to
[ 邮箱如xxxx@apache.org ]
which can be found in the keys file:
https://downloads.apache.org/incubator/fesod/KEYS

Build Environment: JDK 8+, Apache Maven 3.6.0+.
/mvnw clean package -DskipTests=true

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

### 3.2.2 公示孵化器投票结果

72 小时后，若至少有 3 票通过而没有反对票，则参考如下邮件进行发送结果

发送邮件至 `general@incubator.apache.org`

标题：`[RESULT][VOTE] Release Apache Fesod (incubating) x.x.x-RCN`

```text
Hi Incubator PMC,

The vote to release Apache Fesod(incubating) X.X.X-RCN has passed with
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

# 4.完成发布

## 4.1 release 版本

1. 从Apache Nexus 仓库, 选择之前进行close过的的 **orgapachefesod-XXX** 点击 `Release` 图标发布

2. 将dev下的签名文件、src、bin移动到release路径下，参考如下命令：

   `svn mv https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x-RCN https://dist.apache.org/repos/dist/release/incubator/fesod/x.x.x -m "Release Fesod X.X.X"`

3. 将之前release note设置为Set as the latest release并提交

4. 将x.x.x的文档更新至fesod官网中，并补充对应binary和source的下载链接

## 4.2 版本公示

发送邮件至 `general@incubator.apache.org`

标题 `[ANNOUNCE] Apache Fesod(Incubating) vx.x.x available`

```text
Hi All,

The Apache Fesod(Incubating) vx.x.x has been released!

Apache Fesod is an easy-to-use, high-performance, open source distributed transaction solution.

Download Links: https://fesod.apache.org/download/fesod/

Release Notes:
https://github.com/apache/fesod/releases/tag/vx.x.x/

Website: https://fesod.apache.org/

Resources:
- Issue: https://github.com/apache/fesod/issues
- Mailing list: dev@fesod.apache.org
```

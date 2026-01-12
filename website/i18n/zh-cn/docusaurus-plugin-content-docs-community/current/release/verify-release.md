---
id: 'verify-release'
title: '如何验证版本'
---

详细检查列表请参考官方的 [Incubator Release Checklist](https://cwiki.apache.org/confluence/display/INCUBATOR/Incubator+Release+Checklist)。

### 1. 下载要发布的候选版本

> 验证环节需依赖 GPG 工具，建议预先安装 `gpg` 或 `gpg2`。

:::caution 注意
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
# 方式一：如果本地有 SVN，直接 checkout (推荐，包含了 KEYS 文件)
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/${RELEASE_VERSION}-${RC_VERSION}/ fesod-dist-dev

# 方式二：使用 wget 直接下载特定文件
wget https://dist.apache.org/repos/dist/dev/incubator/fesod/${RELEASE_VERSION}-${RC_VERSION}/apache-fesod-${RELEASE_VERSION}-src.tar.gz

```

### 2. 验证上传的版本是否合规

#### 2.1 检查发布包完整性

上传到 dist 的包必须包含：

1. **源码包** (Source Package, 必须)
2. **签名文件** (.asc, 必须)
3. **哈希文件** (.sha512, 必须)

#### 2.2 检查 GPG 签名

首先导入发布人的公钥。

**2.2.1 导入 KEYS**

```shell
# 从 SVN 仓库下载 KEYS (通常在版本目录或根目录)
curl https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS > KEYS

# 导入 KEYS 到本地
gpg --import KEYS

```

**2.2.2 信任公钥 (可选，但推荐)**

```shell
# 查找本次发版人的 Key ID，并进行信任设置
gpg --edit-key <KEY_ID>

# 输入 trust，选择 5 (ultimate)，确认 y，最后 quit

```

**2.2.3 验证签名**

```shell
# 验证源码包
gpg --verify apache-fesod-${RELEASE_VERSION}-src.tar.gz.asc apache-fesod-${RELEASE_VERSION}-src.tar.gz
```

> **检查结果：** 必须出现 **`Good signature`** 字样。

#### 2.3 检查 SHA512 哈希

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

### 3. 检查源码包内容 (核心合规项)

解压源码包：

```shell
tar -xvf apache-fesod-${RELEASE_VERSION}-src.tar.gz
cd apache-fesod-${RELEASE_VERSION}-src

```

#### 3.1 孵化器特有检查 (Incubator Check)

作为孵化项目，必须检查根目录下是否存在 `DISCLAIMER` (或 `DISCLAIMER-WIP`) 文件。

* **检查项：** 确认存在 `DISCLAIMER` 文件，且内容声明了这是一个处于孵化阶段的项目。

#### 3.2 ASF License Header (RAT 检查)

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

#### 3.3 源码编译验证

确保源码可以被正确编译打包。

```shell
# 首次编译可能需要下载依赖，耗时视网络而定
./mvnw clean install -DskipTests

```

**检查项：**

* [ ] Build Success (编译成功)
* [ ] 源码包中**不包含**任何非必要的二进制文件 (如 `.jar`, `.zip`, `.class`)。

#### 3.4 许可证合规性检查

进入解压后的目录，人工检查：

* [ ] **LICENSE 文件：** 存在且内容标准 (Apache License 2.0)。
* [ ] **NOTICE 文件：**
* 存在。
* 年份正确 (例如包含 2025/2026)。
* 如果引入了其他必须在 NOTICE 中声明的依赖，需确认已包含。

* [ ] **DISCLAIMER 文件：** 存在（孵化项目必须）。

### 4. 邮件回复示例

验证完成后，请在开发者邮件列表 (`dev@fesod.apache.org`) 回复投票邮件。

:::tip 特别提示
你是 **PPMC 成员**，你的投票是 **Binding (有约束力)** 的。请务必带上 `(binding)` 后缀。
:::

**回复模板 (PPMC 成员):**

```text
+1 (binding)

[X] Download links are valid.
[X] Checksums and signatures.
[X] LICENSE/NOTICE files exist
[X] No unexpected binary files
[X] All source files have ASF headers
[X] Can compile from source

My Environment:
- OS: MacOS <版本号> / Linux
- JDK: <JDK版本>
- Maven: <Maven版本>

```

**回复模板 (非 PPMC 成员/贡献者):**

```text
+1 (non-binding)

I have checked:
... (同上)

```

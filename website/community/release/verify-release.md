---
id: 'verify-release'
title: 'How to Verify Release'
---
For a detailed checklist, please refer to the official [Incubator Release Checklist](https://cwiki.apache.org/confluence/display/INCUBATOR/Incubator+Release+Checklist).

### 1. Download the Release Candidate

> **Prerequisite:** Ensure you have `gpg` or `gpg2` installed.

:::caution Note
Downloading may take some time depending on your network connection.
:::

Set environment variables for convenience (replace with actual versions):

```shell
# Example: export RELEASE_VERSION=0.1.0
# Example: export RC_VERSION=rc1
export RELEASE_VERSION={release_version}
export RC_VERSION={rc_version}

```

Download the artifacts:

```shell
# Option 1: SVN checkout (Recommended, includes KEYS file)
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/${RELEASE_VERSION}-${RC_VERSION}/ fesod-dist-dev

# Option 2: Wget individual files
wget https://dist.apache.org/repos/dist/dev/incubator/fesod/${RELEASE_VERSION}-${RC_VERSION}/apache-fesod-${RELEASE_VERSION}-src.tar.gz

```

### 2. Verify Compliance and Integrity

#### 2.1 Check Package Completeness

The uploaded artifacts must contain:

1. **Source Package** (Required)
2. **Signature file** (.asc, Required)
3. **Hash file** (.sha512, Required)

#### 2.2 Verify GPG Signature

**2.2.1 Import KEYS**

```shell
# Download KEYS
curl https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS > KEYS

# Import KEYS locally
gpg --import KEYS

```

**2.2.2 Trust the Public Key (Optional but Recommended)**

```shell
# Find the Key ID used for this release
gpg --edit-key <KEY_ID>

# Type 'trust', select '5' (ultimate), confirm with 'y', then type 'quit'

```

**2.2.3 Verify the Signature**

```shell
# Verify Source Package
gpg --verify apache-fesod-${RELEASE_VERSION}-src.tar.gz.asc apache-fesod-${RELEASE_VERSION}-src.tar.gz

```

> **Success Indicator:** The output must include **`Good signature`**.

#### 2.3 Verify SHA512 Checksum

**Mac OS / Linux:**

```shell
# Verify Source Package
shasum -a 512 --check apache-fesod-${RELEASE_VERSION}-src.tar.gz.sha512

# Or manually compare
shasum -a 512 apache-fesod-${RELEASE_VERSION}-src.tar.gz
cat apache-fesod-${RELEASE_VERSION}-src.tar.gz.sha512

```

**Windows:**

```shell
certUtil -hashfile apache-fesod-${RELEASE_VERSION}-src.tar.gz SHA512

```

### 3. Check Source Package Content (Crucial)

Extract the source package:

```shell
tar -xvf apache-fesod-${RELEASE_VERSION}-src.tar.gz
cd apache-fesod-${RELEASE_VERSION}-src

```

#### 3.1 Incubator Specific Checks

* [ ] **DISCLAIMER:** Ensure a `DISCLAIMER` (or `DISCLAIMER-WIP`) file exists in the root directory. This is mandatory for incubating projects.

#### 3.2 ASF License Header Check (RAT)

Run the Apache RAT (Release Audit Tool) check:

```shell
# Run RAT check
./mvnw apache-rat:check
# Or if wrapper is not configured
mvn apache-rat:check

```

**Check the report (`target/rat.txt`):**

* **Unapproved Licenses:** Must be **0**.
* **Binaries:** Should be **0** (Source packages should not contain compiled jars/classes).

#### 3.3 Compilation Verification

Ensure the source code compiles successfully.

```shell
# This may take time depending on network to download dependencies
./mvnw clean install -DskipTests

```

**Checklist:**

* [ ] Build Success.
* [ ] No unexpected binary files in the source tree.

#### 3.4 License and Notice

Manually check the following files in the root directory:

* [ ] **LICENSE:** Exists and contains the Apache License 2.0.
* [ ] **NOTICE:**
* * Exists.
* * Copyright year is current (e.g., includes 2025/2026).
* * Contains required attributions for bundled dependencies (if any).
* [ ] **DISCLAIMER：** Exists.

### 4. Email Reply Templates

After verification, reply to the vote thread on `dev@fesod.apache.org`.

:::tip
As a **PPMC member**, your vote is **binding**. Please include `(binding)` in your reply.
:::

**Template for PPMC Members:**

```text
+1 (binding)

[X] Download links are valid.
[X] Checksums and signatures.
[X] LICENSE/NOTICE files exist
[X] No unexpected binary files
[X] All source files have ASF headers
[X] Can compile from source

My Environment:
- OS: MacOS <Version> / Linux
- JDK: <JDK Version>
- Maven: <Maven Version>

```

**Template for Contributors (Non-PPMC):**

```text
+1 (non-binding)

I have checked:
... (Same as above)

```

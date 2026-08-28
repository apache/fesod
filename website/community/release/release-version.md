---
id: 'release-version'
title: 'How to Release'
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

## 1. Preparation

### 1.1 Release Guide

Refer to the following links to learn about ASF release guidelines:

- [Apache Release Guide](http://www.apache.org/dev/release-publishing)
- [Apache Release Policy](http://www.apache.org/dev/release.html)
- [Apache Incubator Release](https://incubator.apache.org/guides/releasemanagement.html)
- [Maven Release Info](http://www.apache.org/dev/publishing-maven-artifacts.html)

### 1.2 PGP Signature

If you are a **first-time** release manager, please refer to the [Apache Releases Signing documentation](https://infra.apache.org/release-signing) and the [Cryptography with OpenPGP](http://www.apache.org/dev/openpgp.html) guide to generate a `PGP` key, which will be used for release signing.

#### 1.2.1 Installation

Install the appropriate `GnuPG 2.x` for your operating system. The latest version is recommended. For example:

```bash
apt-get install gnupg2
```

#### 1.2.2 Key Generation

Use your **\<your Apache ID>@apache.org** as the user identifier (USER-ID) and generate a new `gpg` key.

```bash
gpg --full-gen-key
```

Example:

```bash
$ gpg --full-gen-key
gpg (GnuPG) 2.4.8; Copyright (C) 2025 g10 Code GmbH
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Please select what kind of key you want:
   (1) RSA and RSA
   (2) DSA and Elgamal
   (3) DSA (sign only)
   (4) RSA (sign only)
   (9) ECC (sign and encrypt) *default*
  (10) ECC (sign only)
  (14) Existing key from card
Your selection? 1

RSA keys may be between 1024 and 4096 bits long.
What keysize do you want? (3072) 4096
Requested keysize is 4096 bits
Please specify how long the key should be valid.
         0 = key does not expire
      <n>  = key expires in n days
      <n>w = key expires in n weeks
      <n>m = key expires in n months
      <n>y = key expires in n years
Key is valid for? (0) 0
Key does not expire at all
Is this correct? (y/N) y

Real name: (use your Apache ID)
Email address: (use your Apache email)
Comment: (enter a comment)
You selected this USER-ID:
    "Username (Comment) <email@address>"

Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? o
```

Notes:

- Key type: Depending on the `gpg` version, use a secure key type. At least `RSA and RSA` is recommended.
- Key length: 4096 bits is recommended.
- Validity period: You may set it to never expire or set an expiration period based on your needs.
- Real name: Use your Apache ID.
- Email address: Use your Apache email.
- Comment: Add comment information.

After confirming, enter the passphrase:

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

#### 1.2.3 View Key

View the key list:

```bash
gpg --list-signatures --keyid-format LONG
```

After execution, all keys on the local machine will be listed. Locate the desired key by **\<your Apache ID>@apache.org**.

#### 1.2.4 Upload Public Key

View key information.

Example:

```bash
$ gpg --list-signatures --keyid-format LONG
[keyboxd]
---------
pub   rsa4096/85E2C3CA72D5936C 2025-12-15 [SC]
      D71C9B1CA898A2408D55EDC785E2C3CA72D5936C
uid                   [ultimate] XXXXX <xxx@apache.org>
sig 3        85E2C3CA72D5936C 2025-12-15  [self-signature]
sub   rsa4096/DF02F738216EBD6D 2025-12-15 [E]
sig          85E2C3CA72D5936C 2025-12-15  [self-signature]
```

In the example above, [ **85E2C3CA72D5936C** ] is the `<key-id>` that needs to be uploaded. Upload the **public key** to a GPG key server.

```bash
gpg --keyserver keys.openpgp.org --send-key <key-id>

# Example
# gpg --keyserver keys.openpgp.org --send-key 85E2C3CA72D5936C
```

`keys.openpgp.org` is a randomly chosen key server. You may also use `keyserver.ubuntu.com` or any other fully functional key server. If the command fails, you can also manually upload the public key online via [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/).

After the upload (which takes about a minute), check whether the key was created successfully. If unsuccessful, you can retry uploading multiple times.

```bash
# Check if the key was created successfully
gpg --keyserver keyserver.ubuntu.com --recv-keys <key-id>
```

Example:

```bash
$ gpg --keyserver keyserver.ubuntu.com --recv-keys 85E2C3CA72D5936C
gpg: key 85E2C3CA72D5936C: "XXXXXX <xxxxxx@apache.org>" not changed
gpg: Total number processed: 1
gpg:              unchanged: 1
```

You may also visit [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/), enter the `key-id`, and click the `Search key` button to check whether a key with the corresponding name exists.

#### 1.2.5 Upload to GitHub

It is recommended to upload the GPG public key to the GitHub account linked to your Apache ID.

- Visit `https://github.com/settings/keys` to add your GPG key.
- If you see "Unverified" after adding the key, bind the email address used in the GPG key to your [GitHub account](https://github.com/settings/emails).

## 2. Discussion

Send an email to the DEV mailing list to initiate a discussion for the next release.

To:

```mail
dev@fesod.apache.org
```

Subject:

```text
[DISCUSS] Release Apache Fesod(Incubating) ${RELEASE_VERSION}
```

Body:

```text
Hi Community,

This is a call for a discussion to release Apache Fesod(Incubating) ${RELEASE_VERSION}.

The change lists about this release:
https://github.com/apache/fesod/compare/2.0.0-incubating...2.0.1-incubating-rc1

Please leave your comments here about this release plan. We will bump the version in repo and start the release process after the discussion.

Best regards,
${RELEASE_MANAGER}
```

- ${RELEASE_VERSION}: Replace with the next version number to be released.
- ${RELEASE_MANAGER}: Typically the release manager for this version.

## 3. Material Preparation

### 3.1 KEYS File

If you are a **first-time** release manager or your original key has expired, please **append** the **public key** to the **KEYS** file in the Apache SVN project **release** repository. The KEYS file is maintained in a single authoritative location (the release directory) so that it stays consistent and is available on downloads.apache.org for signature verification:

- Release repository: <https://dist.apache.org/repos/dist/release/incubator/fesod>

Steps:

```bash
# Check out to the local fesod-release directory
svn co https://dist.apache.org/repos/dist/release/incubator/fesod/ fesod-release

# Enter the local directory
cd fesod-release

# Export and append to the KEYS file
(gpg --list-sigs xxx@apache.org && gpg --export --armor  xxx@apache.org) >> KEYS

# Commit
svn ci -m "add gpg key for xxx"
```

Notes:

- Do not directly overwrite the `KEYS` file in the repository. Only **append** to it.
- The `KEYS` file is maintained only in the **release** directory (single source of truth). Do **not** create a copy in the dev directory, as two copies tend to drift out of sync.
- SVN repositories require PPMC permissions. A PPMC member can assist you with the upload.

### 3.2 POM Configuration

Configure the local POM files to push the release to the ASF Nexus repository.

#### 3.2.1 Project Setup

Ensure the project's `pom.xml` file is configured as follows:

```xml
<parent>
    <groupId>org.apache</groupId>
    <artifactId>apache</artifactId>
    <version>31</version>
</parent>
```

#### 3.2.2 Maven Settings

Configure your Apache ID and GPG key information in the local Apache Maven configuration file `settings.xml`.

```xml
<settings>
    <profiles>
        <profile>
            <id>apache-release</id>
            <properties>
                <mavenExecutorId>forked-path</mavenExecutorId>
                <!-- gpg key-id -->
                <gpg.keyname>yourKeyName</gpg.keyname>
                <deploy.url>https://dist.apache.org/repos/dist/dev/incubator/fesod/</deploy.url>
            </properties>
        </profile>
    </profiles>
    <servers>
        <!-- Apache ID and password -->
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

        <!-- gpg passphrase -->
        <server>
            <id>gpg.passphrase</id>
            <passphrase>yourKeyPassword</passphrase>
        </server>
    </servers>
</settings>
```

Encryption of the configuration is recommended. Refer to the following guides:

- [Publishing Maven Releases to Maven Central Repository](https://infra.apache.org/publishing-maven-artifacts.html)
- [Maven's password encryption capabilities](http://maven.apache.org/guides/mini/guide-encryption.html)

### 3.3 Release Artifacts

#### 3.3.1 Pre-Release Version

For example, to release version `2.0.0-incubating`, follow these steps:

- Create a new branch `2.0.0-incubating` as the release branch.
- Update the version number in `pom.xml` to `2.0.0-incubating`.
- Push the RC (Release Candidate) version tag.

```bash
# Switch to the release branch
git checkout -b 2.0.0-incubating

# Create the RC1 version tag
git tag -s 2.0.0-incubating-rc1 -m "release: release for 2.0.0-incubating RC1"

# Push the tag to the remote repository
git push origin 2.0.0-incubating-rc1
```

#### 3.3.2 Push Binary Packages

Compile the source code of the pre-release RC branch and push the binary packages to the [staging repository](https://repository.apache.org/#stagingRepositories).

```bash
mvn clean deploy -Papache-release -DskipTests -Dgpg.skip=false
```

Log in to the ASF Nexus repository with your Apache ID, locate the released version, and click **Close**.

> If the close operation fails, it is likely because the public key corresponding to the signing key cannot be retrieved from `keys.openpgp.org`. Please check via [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) yourself.

#### 3.3.3 Package Source Code

:::caution Note
Do not package in your daily working directory!
:::

> Local files such as `node_modules`, IDE configuration files (e.g., `.idea`, `.vscode`), or empty directories left behind after refactoring may accidentally be included in the `source-release.zip`. This can cause compliance issues (e.g., distributing unauthorized binaries) and lead to a failed vote.

You **must** perform the release process in a **freshly cloned Git repository** to ensure the build artifacts are reproducible and clean.

**Note**: Do not open the source directory with an IDE (such as IntelliJ or VS Code), as it may generate configuration files or compilation caches. Run the Maven release commands directly in the terminal first.

Use `git archive` to ensure the source package is clean (excluding the .git directory and other ignored files).

```bash
git archive --format=tar.gz \
  --prefix=apache-fesod-2.0.0-incubating-src/ \
  -o apache-fesod-2.0.0-incubating-src.tar.gz \
  e7546d1138d4d3a638df10193a4c29c50a7e55d8
```

> **Note**: The hash `e7546d1138d4d3a638df10193a4c29c50a7e55d8` here corresponds to the commit hash of tag `2.0.0-incubating-rc1`.

#### 3.3.4 Signing

Perform GPG signing and SHA512 checksum calculation on the generated source package.

```bash
# 1. Sign the source packages with the specified GPG key-id (.asc)
# Replace <key-id> with your GPG key-id
for i in *.tar.gz; do echo "Signing $i"; gpg -u <key-id> --armor --output $i.asc --detach-sig $i ; done

# 2. Generate SHA512 checksums (.sha512)
for i in *.tar.gz; do echo "Hashing $i"; sha512sum $i > $i.sha512 ; done
```

After signing, you can verify locally:

```bash
# Verify signatures
for i in *.tar.gz; do echo $i; gpg --verify $i.asc $i ; done

# Verify SHA512
for i in *.tar.gz; do echo $i; sha512sum --check $i.sha512; done
```

#### 3.3.5 Upload to SVN

Upload the signed source packages to the SVN repository.

```bash
# Check out the SVN dev repository
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/ fesod-dev
cd fesod-dev

# Create the version directory
mkdir 2.0.0-incubating-rc1
cd 2.0.0-incubating-rc1

# Copy the source package files (assuming they are in the parent directory)
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.asc .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.sha512 .

# Commit to SVN
cd ..
svn add 2.0.0-incubating-rc1
svn commit -m "Add 2.0.0-incubating-rc1 source release"
```

You need to configure your SVN user first. Alternatively, you can specify the username and password in each command line, for example:

```bash
svn co --username <apache-id> --password <password>
```

If certain files appear unexpectedly or errors occur, delete the relevant content, execute `svn delete`, and repeat the upload process above.

## 4. Community Vote

Each release may go through multiple rounds of voting, where RC N and Round N represent the Nth round, i.e., the Nth vote for that version.

### 4.1 Initiating a Vote

To:

```mail
dev@fesod.apache.org
```

Subject:

```text
[VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

Body:

```text
Hi Community,

This is a call for vote to release Apache Fesod(Incubating) ${RELEASE_VERSION}.

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/${RELEASE_VERSION}-RCN

The staging repo:
https://repository.apache.org/content/repositories/orgapachefesod-1016

Git tag for the release:
https://github.com/apache/fesod/releases/tag/${RELEASE_VERSION}-RCN

Hash for the release tag:
e7546d1138d4d3a638df10193a4c29c50a7e55d8

Release Notes:
https://github.com/apache/fesod/releases/tag/${RELEASE_VERSION}-RCN

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

Replace the required content in the body with the actual `${RELEASE_VERSION}` and `RCN` round information.

### 4.2 Closing the Vote

After **the vote has been open for at least 72 hours and has received at least 3 binding +1 votes**, announce the voting results:

To:

```mail
dev@fesod.apache.org
```

Subject:

```text
[RESULT][VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

Body:

```text
Hi Community,

The vote to release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN has
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

If the vote fails, first resolve the issues raised, then prepare the materials for a new round `RCN` and initiate a new round of voting following the release process.

## 5. Incubator Vote

After the community vote has passed and concluded, a vote must be initiated on the incubator mailing list.

### 5.1 Initiating a Vote

Similar to initiating a community vote, but with the addition of links to the community vote threads to demonstrate that consensus has been reached within the community.

To:

```mail
general@incubator.apache.org
```

Subject:

```text
[VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

Body:

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

### 5.2 Announcing Incubator Vote Results

After **the vote has been open for at least 72 hours and has received at least 3 binding +1 votes**, announce the voting results:

To:

```mail
general@incubator.apache.org
```

Subject:

```text
[RESULT][VOTE] Release Apache Fesod (Incubating) ${RELEASE_VERSION}-RCN
```

Body:

```text
Hi Incubator PMC,

The vote to release Apache Fesod(Incubating) ${RELEASE_VERSION}-RCN has passed with
3 +1 binding and 1 +1 non-binding votes, no +0 or -1 votes.

Binding votes:

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

## 6. Completing the Release

### 6.1 Migrating Source and Binary Packages

Log in to the Apache Nexus repository, select the previously closed **orgapachefesod-XXX**, and click the `Release` icon to publish.

1. Move the signature files, src, and bin from dev to the release path. Refer to the following command:

   ```bash
   svn mv https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x-RCN https://dist.apache.org/repos/dist/release/incubator/fesod/x.x.x -m "Release X.X.X"
   ```

2. Set the previous release note as the latest release (Set as the latest release) and submit it.

3. Update the documentation for version x.x.x on the Fesod website and add the corresponding binary and source download links.

### 6.2 Release Announcement

To:

```mail
announce@apache.org,dev@fesod.apache.org
```

Subject:

```text
[ANNOUNCE] Apache Fesod(Incubating) ${RELEASE_VERSION} released
```

Body:

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

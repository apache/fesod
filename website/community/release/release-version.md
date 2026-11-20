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

- [Apache Release Guide](https://infra.apache.org/release-publishing.html)
- [Apache Release Policy](https://www.apache.org/legal/release-policy.html)
- [Apache Release Distribution Policy](https://infra.apache.org/release-distribution)
- [Maven Release Info](https://infra.apache.org/publishing-maven-artifacts.html)

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
[DISCUSS] Release Apache Fesod ${RELEASE_VERSION}
```

Body:

```text
Hi Community,

This is a call for a discussion to release Apache Fesod ${RELEASE_VERSION}.

Changes since Apache Fesod ${PREVIOUS_RELEASE_TAG}:
https://github.com/apache/fesod/compare/${PREVIOUS_RELEASE_TAG}...main

Please leave your comments here about this release plan. We will bump the version in repo and start the release process after the discussion.

Best regards,
${RELEASE_MANAGER}
```

- ${RELEASE_VERSION}: Replace with the next version number to be released.
- ${PREVIOUS_RELEASE_TAG}: Replace with the most recent final release tag.
- ${RELEASE_MANAGER}: Typically the release manager for this version.

## 3. Material Preparation

### 3.1 KEYS File

If you are a **first-time** release manager or your original key has expired, please **append** the **public key** to the **KEYS** file in the Apache SVN project **release** repository. The KEYS file is maintained in a single authoritative location (the release directory) so that it stays consistent and is available on downloads.apache.org for signature verification:

- Release repository: <https://dist.apache.org/repos/dist/release/fesod>

Steps:

```bash
# Check out to the local fesod-release directory
svn co https://dist.apache.org/repos/dist/release/fesod/ fesod-release

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
- By default, only PMC members can update `dist/release/fesod`. If you are not a PMC member, ask a
  PMC member to append your public key.

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

For example, to release version `X.Y.Z`, follow these steps:

- Create a new branch `X.Y.Z` as the release branch.
- Update the version number in `pom.xml` to `X.Y.Z`.
- Set a fixed `project.build.outputTimestamp` in `pom.xml` to ensure [reproducible builds](https://maven.apache.org/guides/mini/guide-reproducible-builds.html).
- Commit and push the release preparation changes.
- Create the signed RC (Release Candidate) tag from that exact commit, then push the tag.

```bash
export RELEASE_VERSION=X.Y.Z
export RC_VERSION=rc1
export BUILD_TIMESTAMP=YYYY-MM-DDTHH:MM:SSZ
export RELEASE_CANDIDATE_TAG="${RELEASE_VERSION}-${RC_VERSION}"

# Create and switch to the release branch
git checkout -b "${RELEASE_VERSION}"

# Set the release version
./mvnw versions:set-property \
  -Dproperty=revision \
  -DnewVersion="${RELEASE_VERSION}"

# Set a fixed build timestamp so every build produces identical artifacts
./mvnw versions:set-property \
  -Dproperty=project.build.outputTimestamp \
  -DnewVersion="${BUILD_TIMESTAMP}"
# Or edit pom.xml directly:
#   <project.build.outputTimestamp>YYYY-MM-DDTHH:MM:SSZ</project.build.outputTimestamp>

# Review, commit, and push the release preparation changes
git diff -- pom.xml
git add pom.xml
git commit -m "chore: prepare Apache Fesod ${RELEASE_VERSION} ${RC_VERSION}"
git push -u origin "${RELEASE_VERSION}"

# Create the signed RC tag from the committed release preparation
git tag -s "${RELEASE_CANDIDATE_TAG}" \
  -m "release: Apache Fesod ${RELEASE_VERSION} ${RC_VERSION}"
git push origin "${RELEASE_CANDIDATE_TAG}"

# Record this commit hash for the vote email
git rev-parse "${RELEASE_CANDIDATE_TAG}^{commit}"
```

#### 3.3.2 Push Binary Packages

Check out the exact RC tag and deploy the Maven convenience binaries to the [staging repository](https://repository.apache.org/#stagingRepositories).

```bash
git checkout "${RELEASE_CANDIDATE_TAG}"
./mvnw clean deploy -Papache-release -Dgpg.skip=false
```

Log in to the ASF Nexus repository with your Apache ID, locate the released version, and click **Close**.

> If the close operation fails, it is likely because the public key corresponding to the signing key cannot be retrieved from `keys.openpgp.org`. Please check via [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/) yourself.

#### 3.3.3 Package Source Code

:::warning
Do not package in your daily working directory!
:::

> Local files such as `node_modules`, IDE configuration files (e.g., `.idea`, `.vscode`), or empty directories left behind after refactoring may accidentally be included in a source archive. This can cause compliance issues (e.g., distributing unauthorized binaries) and lead to a failed vote.

You **must** perform the release process in a **freshly cloned Git repository** to ensure the build artifacts are reproducible and clean.

**Note**: Do not open the source directory with an IDE (such as IntelliJ or VS Code), as it may generate configuration files or compilation caches. Run the Maven release commands directly in the terminal first.

Use `git archive` to ensure the source package is clean (excluding the .git directory and other ignored files).

```bash
git archive --format=tar.gz \
  --prefix="apache-fesod-${RELEASE_VERSION}-src/" \
  -o "apache-fesod-${RELEASE_VERSION}-src.tar.gz" \
  "${RELEASE_CANDIDATE_TAG}"
```

> **Note**: Build the source package from the exact signed tag proposed in the vote. Record the tag's
> commit hash in the vote email so voters can compare the archive with that revision.

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
svn co https://dist.apache.org/repos/dist/dev/fesod/ fesod-dev
cd fesod-dev

# Create the version directory
mkdir "${RELEASE_VERSION}-${RC_VERSION}"
cd "${RELEASE_VERSION}-${RC_VERSION}"

# Copy the source package files (assuming they are in the parent directory)
cp "../../apache-fesod-${RELEASE_VERSION}-src.tar.gz" .
cp "../../apache-fesod-${RELEASE_VERSION}-src.tar.gz.asc" .
cp "../../apache-fesod-${RELEASE_VERSION}-src.tar.gz.sha512" .

# Commit to SVN
cd ..
svn add "${RELEASE_VERSION}-${RC_VERSION}"
svn commit -m "Add Apache Fesod ${RELEASE_VERSION} ${RC_VERSION} candidate"
```

You need to configure your SVN user first. Alternatively, you can specify the username and password in each command line, for example:

```bash
svn co --username <apache-id> --password <password>
```

If certain files appear unexpectedly or errors occur, delete the relevant content, execute `svn delete`, and repeat the upload process above.

## 4. PMC Release Vote

The release vote is held publicly on `dev@fesod.apache.org`. Only Apache Fesod PMC votes are binding,
but reviews and non-binding votes from committers, contributors, and users are strongly encouraged.

Each new candidate uses a new RC number. Never reuse a tag or staging directory that has already been
voted on.

### 4.1 Initiating a Vote

To:

```mail
dev@fesod.apache.org
```

Subject:

```text
[VOTE] Release Apache Fesod ${RELEASE_VERSION} (${RC_VERSION})
```

Body:

```text
Hi Community,

This is a call for a vote to release Apache Fesod ${RELEASE_VERSION}.

The release candidate:
https://dist.apache.org/repos/dist/dev/fesod/${RELEASE_VERSION}-${RC_VERSION}/

The staging repo:
https://repository.apache.org/content/repositories/${STAGING_REPOSITORY}/

Git tag for the release:
https://github.com/apache/fesod/tree/${RELEASE_VERSION}-${RC_VERSION}

Commit hash for the release tag:
${RELEASE_TAG_COMMIT_HASH}

Release Notes:
${RELEASE_NOTES_URL}

The artifacts were signed with the key belonging to ${RELEASE_MANAGER_APACHE_EMAIL}.
The full signing-key fingerprint is ${SIGNING_KEY_FINGERPRINT}.
The public key can be found in the KEYS file:
https://downloads.apache.org/fesod/KEYS

Build environment: JDK 8 or later and the Maven wrapper included in the source package.
./mvnw clean package

The vote will be open for at least 72 hours.

Please vote accordingly:

[ ] +1 approve
[ ] +0 no opinion
[ ] -1 disapprove with the reason

Only Apache Fesod PMC votes are binding. Other votes are welcome and will be recorded as non-binding.

Checklist for reference:

[ ] Download links are valid.
[ ] Checksums and signatures are valid.
[ ] LICENSE and NOTICE files are correct.
[ ] No unexpected binary files are present.
[ ] All source files have ASF headers.
[ ] The source package builds and tests pass.

To learn more about Apache Fesod, please see https://fesod.apache.org/.
```

Replace all placeholders with the values for the candidate before sending the email.

### 4.2 Closing the Vote

Release votes should be open for at least 72 hours. A release vote passes with at least three
binding `+1` votes and more binding `+1` votes than binding `-1` votes. Releases cannot be vetoed.

Announce the result on the same mailing list:

To:

```mail
dev@fesod.apache.org
```

Subject:

```text
[RESULT][VOTE] Release Apache Fesod ${RELEASE_VERSION} (${RC_VERSION})
```

Body:

```text
Hi Community,

The vote to release Apache Fesod ${RELEASE_VERSION} (${RC_VERSION}) has passed.

The vote tally is:

Binding +1 votes: <count>
- <name>

Non-binding +1 votes: <count>
- <name or none>

+0 votes: <count>
- <name or none>

-1 votes: <count>
- <name and reason, or none>

Vote thread:
${VOTE_THREAD_URL}

Thank you for reviewing and voting for our release candidate.

I will now publish the approved artifacts and send the release announcement.
```

Replace every count and voter list with the actual result. Include the reasons given for any `-1` votes.

If the vote does not pass, resolve the issues raised, prepare a new candidate with a new RC number,
and start a new vote. Do not overwrite or reuse the rejected candidate's tag or staging directory.

## 5. Completing the Release

### 5.1 Creating the Final Git Tag

After the vote passes, create the final release tag from the exact commit of the approved RC tag.
Do not rebuild from or tag a different commit. Set `RELEASE_TAG_COMMIT_HASH` to the commit hash
recorded in the vote email before running the commands below.

```bash
if git fetch origin --tags &&
   rc_commit="$(git rev-parse --verify "refs/tags/${RELEASE_VERSION}-${RC_VERSION}^{commit}")" &&
   test "$rc_commit" = "${RELEASE_TAG_COMMIT_HASH:-}"; then
  git tag -s "${RELEASE_VERSION}" -m "release: Apache Fesod ${RELEASE_VERSION}" "$rc_commit" &&
    git push origin "refs/tags/${RELEASE_VERSION}"
else
  printf '%s\n' 'Could not verify the RC tag against the commit hash from the vote email.' >&2
  false
fi
```

### 5.2 Publishing Source and Convenience Binary Packages

1. Log in to the Apache Nexus repository, select the closed **orgapachefesod-XXX** staging repository,
   and click `Release` to publish the Maven convenience binaries.

2. Move the approved source package, signature, and checksum from the dev directory to the release
   directory:

   ```bash
   svn mv \
     "https://dist.apache.org/repos/dist/dev/fesod/${RELEASE_VERSION}-${RC_VERSION}" \
     "https://dist.apache.org/repos/dist/release/fesod/${RELEASE_VERSION}" \
     -m "Release Apache Fesod ${RELEASE_VERSION}"
   ```

3. Wait for the release artifacts to appear on `downloads.apache.org` and for the Maven artifacts to
   synchronize to Maven Central before publishing download links or announcing the release.

4. Create the GitHub release from the final `${RELEASE_VERSION}` tag, mark it as the latest release,
   and publish the approved release notes. The final tag must point to the approved RC commit.

5. Update the Fesod website for `${RELEASE_VERSION}` and add the source and convenience binary download
   links. The source download must use the ASF mirror redirector.

### 5.3 Release Announcement

To:

```mail
announce@apache.org,dev@fesod.apache.org
```

Subject:

```text
[ANNOUNCE] Apache Fesod ${RELEASE_VERSION} released
```

Body:

```text
Hello everyone,

The Apache Fesod ${RELEASE_VERSION} has been released!

Apache Fesod is a high-performance and memory-efficient Java library for reading and writing spreadsheet files, designed to simplify development and ensure reliability.

Download Links:
https://fesod.apache.org/docs/download

Release Notes:
https://github.com/apache/fesod/releases/tag/${RELEASE_VERSION}

Website:
https://fesod.apache.org/

Resources:
- Issue: https://github.com/apache/fesod/issues
- Mailing list: dev@fesod.apache.org

We would like to thank all the contributors who made this release possible.

${RELEASE_MANAGER}
On behalf of the Apache Fesod PMC
```

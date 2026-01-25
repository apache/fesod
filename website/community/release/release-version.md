---
id: 'release-version'
title: 'How to Release'
---

# 1. Preface

## 1.1 Apache Release Documentation

Refer to the following links to understand the ASF release process:

- [Apache Release Guide](http://www.apache.org/dev/release-publishing)
- [Apache Release Policy](http://www.apache.org/dev/release.html)
- [Maven Release Info](http://www.apache.org/dev/publishing-maven-artifacts.html)

## 1.2 PGP Signing

Follow the Apache Release Guide to sign the release version. Users can also use this signature to verify that the downloaded version has not been tampered with.

Create a `pgp` key for release signing, using **\<your Apache ID>@apache.org** as the key USER-ID.

For details, refer to [Apache Releases Signing documentation](https://infra.apache.org/release-signing) and [Cryptography with OpenPGP](http://www.apache.org/dev/openpgp.html).

Brief process for generating a key:

- Generate a new `gpg` key using `gpg --full-gen-key`, setting the key length to 4096.

  Note: You can set it to never expire, or set an expiration date based on your needs. However, if it expires, you must update the public key in the [DEV KEYS file](https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS) and [RELEASE KEYS file](https://dist.apache.org/repos/dist/release/incubator/fesod/KEYS).

- Upload the key to the public key server using `gpg --keyserver keys.openpgp.org --send-key <your key id>`.

Note: If the server is inaccessible, you can upload the public key online via [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/).

  ```bash
  # Use this command to find the keyid, e.g.: gpg --list-signatures --keyid-format LONG
  pub   rsa4096/XXXXXXXX 2025-12-15 [SC] [Expires: 2027-12-15]
        F2D3A28A392129B927C7FB42XXXXXXXX
  uid                   [ Absolute ] xxxx <xxxx@apache.org>
  sig 3        XXXXXXXX 2025-12-15  [Self-signature]
  sub   rsa4096/XXXXX 2025-12-15 [E] [Expires: 2027-12-15]
  sig          XXXXXXXX 2025-12-15  [Self-signature]

- Export the public key to a text file using `gpg --armor --output ./public-key.txt --export XXXXXXXX`.

- Append the generated key to the [DEV KEYS file](https://dist.apache.org/repos/dist/dev/incubator/fesod/KEYS) and [RELEASE KEYS file](https://dist.apache.org/repos/dist/release/incubator/fesod/KEYS).

**Note:**

The DEV SVN repository can be updated by the Release Manager directly. The Release SVN repository requires PMC privileges, so you may need PMC assistance to upload the KEY.

**Tips:** You need to set a default public key. If you have multiple public keys, please modify `~/.gnupg/gpg.conf`.

Reference example:

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

Real name: (Set username) (Use Apache ID)
Email address: (Set email address) (Use Apache email)
Comment: (Fill in comments)
You selected this USER-ID:
   "Username (Comment) <Email Address>"

Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? O
You need a Passphrase to protect your secret key. (Set password)

```

Convert the generated public and private keys to ASCII format:

```bash
gpg --armor --output ./public-key.txt --export XXXXXXXX
gpg --armor --output ./private-key.txt --export-secret-keys XXXXXXXX
```

View the key list:

```bash
[root@localhost ~]# gpg --list-signatures --keyid-format LONG
[keyboxd]
---------
pub   rsa4096/XXXXXXXX 2025-12-15 [SC] [Expires: 2027-12-15]
      D71C9B1CA898A2408D55EDCXXXXXXXX
uid                   [ Absolute ] xxxx <xxxx@apache.org>
sig 3        XXXXXXXX 2025-12-15  [Self-signature]
sub   rsa4096/XXXXX 2025-12-15 [E] [Expires: 2027-12-15]
sig          XXXXXXXX 2025-12-15  [Self-signature]

```

Upload the public key to the public key server:

```bash
[root@localhost gpgtest]# gpg --keyserver keys.openpgp.org --send-key XXXXXXXX
gpg: sending key XXXXXXXX to hkp server keys.openpgp.org
```

## 1.3 POM Configuration

Configure the POM file to deploy the version to the ASF Nexus repository.

① Add Apache POM inheritance default settings:

```xml
<parent>
    <groupId>org.apache</groupId>
    <artifactId>apache</artifactId>
    <version>31</version>
</parent>
```

② Add key information to the Maven configuration file `settings.xml`:

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
        <server>
            <id>gpg.passphrase</id>
            <passphrase>yourKeyPassword</passphrase>
        </server>
    </servers>
</settings>
```

**Tips:** It is recommended to use [Maven's password encryption capabilities](http://maven.apache.org/guides/mini/guide-encryption.html) to encrypt `gpg.passphrase`.

# 2. Release Process

## 2.1 Prepare Branch

Create a new release branch from the main branch. For example, if you want to release version `${release_version}`, create a new branch `${release_version}` from the development branch. All modifications involving Release Candidates and tagging for `${release_version}` will be done on this branch. Ensure that all GitHub Actions CI checks pass on this branch. Finally, merge it back into the main branch after the release is complete.

Example: If the Java SDK needs to release version `2.2.0-incubating`, create a new branch `release-2.2.0-incubating` from the `main` branch, and submit a commit on this branch replacing the Snapshot version number with the `2.2.0-incubating` version number.

## 2.2 Prepare Binary and Source Packages

### 2.2.1 Prepare the SDK release according to [publishing maven artifacts](https://infra.apache.org/publishing-maven-artifacts.html) [4] instructions

```bash
mvn clean deploy -Papache-release -DskipTests -Dgpg.skip=false
```

At this point, the Fesod SDK is published to the [Staging Repository](https://repository.apache.org/#stagingRepositories) (requires Apache account login). Find the released version (i.e., `${STAGING.RELEASE}`) and click **Close**.

Note: If the close operation fails, it is likely because the public key corresponding to the signing key cannot be found on keys.openpgp.org. Please check via [OpenPGP Keyserver (ubuntu.com)](https://keyserver.ubuntu.com/).

### 2.2.2 Package Source

:::caution Note
Do NOT run the release process in your daily working directory!
:::

> Local files such as `node_modules`, IDE configurations (e.g., `.idea`, `.vscode`), or leftover empty directories from refactoring can accidentally be packaged into the `source-release.zip`. This will cause compliance issues (e.g., distributing unauthorized binaries) and lead to vote failures.

You **MUST** perform the release process in a **fresh git clone** to ensure the artifacts are reproducible and clean.

**Note**: Do not open this directory with an IDE (like IntelliJ or VS Code) immediately, as it may generate configuration files or compilation caches. Run the Maven release commands directly from the terminal first.

First, confirm that the current codebase is ready for release.

```bash
# 1. Switch to the main branch and update
git checkout main
git pull

# 2. Create a GPG signed Tag
# Note: Ensure the content in -m is accurate
git tag -s 2.0.0-incubating-rc1 -m "release: release for 2.0.0-incubating RC1"

# 3. Push the Tag to the remote repository
git push git@github.com:apache/fesod.git 2.0.0-incubating-rc1
```

Use `git archive` to ensure the source package is clean (excludes the .git directory or other ignored files).

```bash
# 1. Export the source package
git archive --format=tar.gz \
  --prefix=apache-fesod-2.0.0-incubating-src/ \
  -o apache-fesod-2.0.0-incubating-src.tar.gz \
  e7546d1138d4d3a638df10193a4c29c50a7e55d8
```

> **Note**: The hash `e7546d11...` here corresponds to the commit hash of tag `2.0.0-incubating-rc1`.

### 2.2.3 Sign and Hash

Perform GPG signing and SHA512 calculation on the generated source package.

```bash
# 1. GPG Signature (.asc)
for i in *.tar.gz; do 
  echo "Signing $i"; 
  gpg --armor --output $i.asc --detach-sig $i ; 
done

# 2. Generate SHA512 Checksum (.sha512)
for i in *.tar.gz; do 
  echo "Hashing $i"; 
  sha512sum $i > $i.sha512 ; 
done

# 3. Verify (Optional)
gpg --verify apache-fesod-2.0.0-incubating-src.tar.gz.asc apache-fesod-2.0.0-incubating-src.tar.gz
sha512sum -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

### 2.2.4 Upload Source Package to SVN (Upload to Dist)

Upload the signed source package to the Apache development distribution area (`dist/dev`).

```bash
# 1. Checkout SVN dev repository
svn co https://dist.apache.org/repos/dist/dev/incubator/fesod/
cd fesod-dev

# 2. Create version directory
mkdir 2.0.0-incubating-rc1
cd 2.0.0-incubating-rc1

# 3. Copy files (assuming files are in the parent directory)
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.asc .
cp ../../apache-fesod-2.0.0-incubating-src.tar.gz.sha512 .

# 4. Commit to SVN
cd ..
svn add 2.0.0-incubating-rc1
svn commit -m "Add 2.0.0-incubating-rc1 source release"
```

---

# 3. Voting Phase

## 3.1 Internal Community Vote

**The vote lasts for at least 72 hours and requires 3 +1 binding votes.**

Send to:

```mail
dev@fesod.apache.org

```

Subject:

`[VOTE]Release Apache Fesod (Incubating) x.x.x-RCN (RoundN)`

RC N and Round N: N represents the count, i.e., which round of voting for this version.

Body:

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

### 3.1.2 Complete Vote

Publish the vote pass email.

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
Link to the corresponding voting email thread, e.g.:
https://lists.apache.org/thread/rwco6lms9qo10whjj8gg1dr8j7drl2gf

Thank you for reviewing and voting for our release candidate.

We will soon launch the second stage of voting.
```

## 3.2 Vote in Incubator

### 3.2.1 Vote in Incubator

Similar to the community vote, but requires adding the community vote thread link to prove that consensus has been reached within the community.

Send email to `general@incubator.apache.org`

Subject:

`[VOTE]Release Apache Fesod (Incubating) x.x.x-RCN`

**The vote lasts for at least 72 hours and requires 3 +1 binding votes.**

```text
Hello everyone,

This is a call for vote to release Apache Fesod(incubating) vx.x.x

The Apache Fesod community has voted and approved the release of Apache
Fesod(incubating) vx.x.x. We now kindly request the IPMC members
review and vote for this release.


The vote thread:
Link to the voting thread in the community, e.g.:
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

Vote Result:
Link to the vote result thread in the community, e.g.:
https://lists.apache.org/thread/r6hsbb9tmsqmn9s7q9qptv3z287lkcbf

The release candidates:
https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x/

The staging repo:
https://repository.apache.org/content/repositories/$

Git tag for the release:
https://github.com/apache/fesod/releases/tag/vx.x.x

Hash for the release tag:
The commit ID of the last commit on the tag branch

Release Notes:
https://github.com/apache/fesod/releases/tag/vx.x.x

The artifacts have been signed with Key [ key-id ], corresponding
to
[ Email e.g. xxxx@apache.org ]
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

### 3.2.2 Announce Incubator Vote Result

After 72 hours, if there are at least 3 passing votes and no opposing votes, send the result email referencing the following template.

Send email to `general@incubator.apache.org`

Subject: `[RESULT][VOTE] Release Apache Fesod (incubating) x.x.x-RCN`

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

# 4. Finalize Release

## 4.1 Release Version

1. From the Apache Nexus repository, select the previously closed **orgapachefesod-XXX** and click the `Release` icon.
2. Move the signature files, src, and bin from the dev path to the release path, referring to the following command:
   `svn mv https://dist.apache.org/repos/dist/dev/incubator/fesod/x.x.x-RCN https://dist.apache.org/repos/dist/release/incubator/fesod/x.x.x -m "Release Fesod X.X.X"`
3. Set the previous release note to "Set as the latest release" and submit.
4. Update the x.x.x documentation on the Fesod official website, and add the corresponding download links for binary and source.

## 4.2 Announcement

Send email to `general@incubator.apache.org`

Subject: `[ANNOUNCE] Apache Fesod(Incubating) vx.x.x available`

```text
Hi All,

The Apache Fesod(Incubating) vx.x.x has been released!

Apache Fesod is an easy-to-use, high-performance, open source distributed transaction solution.

Download Links: [https://fesod.apache.org/download/fesod/

Release Notes:
https://github.com/apache/fesod/releases/tag/vx.x.x/

Website: https://fesod.apache.org/

Resources:
- Issue: https://github.com/apache/fesod/issues
- Mailing list: dev@fesod.apache.org
```

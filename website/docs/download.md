---
id: 'download'
title: 'Download'
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

# Download Apache Fesod (Incubating)

Here is the Apache Fesod (Incubating) official download page. Apache Fesod provides source releases that can be downloaded from the ASF distribution site. Binary artifacts are available through Maven Central.

## The Latest Stable Release

|     Version      |    Date    | Download                                                                                                                                                                                                                                                                                                                                                                                                  |                                 Release Notes                                  |
|:----------------:|:----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------:|
| 2.0.2-incubating | 2026-05-30 | [apache-fesod-2.0.2-incubating-src.tar.gz](https://www.apache.org/dyn/closer.lua/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz ) ([asc](https://downloads.apache.org/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz.asc), [sha512](https://downloads.apache.org/incubator/fesod/2.0.2-incubating/apache-fesod-2.0.2-incubating-src.tar.gz.sha512)) | [Release Notes](https://github.com/apache/fesod/releases/tag/2.0.2-incubating) |

## All Archived Releases

|     Version      |    Date    | Download                                                                                                                                                                                                                                                                                                                                                                                                  |                                 Release Notes                                  |
|:----------------:|:----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------:|
| 2.0.1-incubating | 2026-02-11 | [apache-fesod-2.0.1-incubating-src.tar.gz](https://www.apache.org/dyn/closer.lua/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz ) ([asc](https://downloads.apache.org/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz.asc), [sha512](https://downloads.apache.org/incubator/fesod/2.0.1-incubating/apache-fesod-2.0.1-incubating-src.tar.gz.sha512)) | [Release Notes](https://github.com/apache/fesod/releases/tag/2.0.1-incubating) |
| 2.0.0-incubating | 2026-01-24 | NA(Not Available)                                                                                                                                                                                                                                                                                                                                                                                         |                               NA(Not Available)                                |

For older releases, please check the [archive](https://archive.apache.org/dist/incubator/fesod/).

For non-Apache releases, please check the [non-apache releases](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/).

## Verifying Apache Releases

Before use, please refer to this [official guide](https://www.apache.org/info/verification.html) to verify all Apache release versions, including the integrity and authenticity of source code releases.

### Download Verification Files

Download the [project release KEYS](https://downloads.apache.org/incubator/fesod/KEYS) file containing the public keys used for signing releases.

### Verify Signature

1. Import the KEYS file to your GPG keyring:

    ```bash
    gpg --import KEYS
    ```

2. Download the source release, .asc signature file, and .sha512 checksum file.

3. Verify the GPG signature:

    ```bash
    gpg --verify apache-fesod-X.X.X-incubating-src.tar.gz.asc apache-fesod-X.X.X-incubating-src.tar.gz
    ```

### Verify Checksum

Verify the `SHA-512` checksum:

```bash
shasum -a 512 -c apache-fesod-X.X.X-incubating-src.tar.gz.sha512
```

Or on Linux:

```bash
sha512sum -c apache-fesod-X.X.X-incubating-src.tar.gz.sha512
```

---
id: 'download'
title: 'Download'
---

Here is the Apache Fesod (Incubating) official download page. Apache Fesod provides source releases that can be
downloaded from the ASF distribution site. Binary artifacts are available through Maven Central.

## Available Modules

- **fesod-sheet** - Core module for Excel/CSV processing (recommended for most users)
- **fesod-bom** - Bill of Materials for dependency management
- **fesod-common** - Common utilities (automatically included with fesod-sheet)
- **fesod-shaded** - Shaded dependencies to avoid conflicts (automatically included with fesod-sheet)

# Apache Source Releases

## The Latest Release

|     Version      |    Date    |  Source Download  |   Release Notes   |
|:----------------:|:----------:|:-----------------:|:-----------------:|
| 2.0.0-incubating | 2026-01-24 | NA(Not Available) | NA(Not Available) |

# Previous Releases (Non-Apache)

## All archived releases

| Version |    Date    |                                                                                                                                                Download                                                                                                                                                |                        Release notes                        |
|:-------:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
|  1.3.0  | 2025-08-23 | [fastexcel-1.3.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.3.0) |

| Version |    Date    |                                                                                                                                                Download                                                                                                                                                |                        Release notes                        |
|:-------:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
|  1.2.0  | 2025-04-14 | [fastexcel-1.2.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.2.0) |
|  1.1.0  | 2025-01-14 | [fastexcel-1.1.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.1.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.1.0) |
|  1.0.0  | 2024-12-05 | [fastexcel-1.0.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.0.0) |

# Verifying Apache Releases

All Apache releases must be verified before use. Follow these steps to verify the integrity and authenticity of the
source release:

## Download Verification Files

Download the [KEYS](https://downloads.apache.org/incubator/fesod/KEYS) file containing the public keys used for signing
releases.

## Verify Signature

1. Import the KEYS file to your GPG keyring:

    ```bash
    gpg --import KEYS
    ```

2. Download the source release, .asc signature file, and .sha512 checksum file.

3. Verify the GPG signature:

```bash
gpg --verify apache-fesod-2.0.0-incubating-src.tar.gz.asc apache-fesod-2.0.0-incubating-src.tar.gz
```

## Verify Checksum

Verify the SHA-512 checksum:

```bash
shasum -a 512 -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

Or on Linux:

```bash
sha512sum -c apache-fesod-2.0.0-incubating-src.tar.gz.sha512
```

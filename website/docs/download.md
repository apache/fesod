---
id: 'download'
title: 'Download'
---

Here is the Apache Fesod (Incubating) official download page. Please choose version to download from the following tables. It is recommended use the latest.

:::tip

We are currently preparing for the first release under the Apache Incubator. Previous releases were non-Apache releases.

:::

## Previous Releases (Non-Apache)

### The latest release

| Version |    Date    |                                                                                                                                                Download                                                                                                                                                |                        Release notes                        |
|:-------:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
|  1.3.0  | 2025-08-23 | [fastexcel-1.3.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.3.0/fastexcel-1.3.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.3.0) |

### All archived releases

| Version |    Date    |                                                                                                                                                Download                                                                                                                                                |                        Release notes                        |
|:-------:|:----------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------:|
|  1.2.0  | 2025-04-14 | [fastexcel-1.2.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.2.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.2.0) |
|  1.1.0  | 2025-01-14 | [fastexcel-1.1.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.1.0/fastexcel-1.1.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.2.0/fastexcel-1.1.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.1.0) |
|  1.0.0  | 2024-12-05 | [fastexcel-1.0.0.jar](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar) ( [asc](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.asc) \| [sha](https://repo1.maven.org/maven2/cn/idev/excel/fastexcel/1.0.0/fastexcel-1.0.0.jar.sha1)) | [notes](https://github.com/apache/fesod/releases/tag/1.0.0) |

## Notes

* When downloading a release, please verify the OpenPGP compatible signature (or failing that, check the SHA-512); these should be fetched from the main Apache site.
* The KEYS file contains the public keys used for signing release. It is recommended that (when possible) a web of trust is used to confirm the identity of these keys.

### To verify the signature of the release artifact

You will need to download both the release artifact and the .asc signature file for that artifact. Then verify the signature by:

* Download the KEYS file and the .asc signature files for the relevant release artifacts.
* Import the KEYS file to your GPG keyring:

```bash
gpg --import KEYS
```

* Verify the signature of the release artifact using the following command:

```bash
gpg --verify <artifact>.asc <artifact>
```

### To verify the checksum of the release artifact

You will need to download both the release artifact and the .sha512 checksum file for that artifact. Then verify the checksum by:

```bash
shasum -a 512 -c <artifact>.sha512
```

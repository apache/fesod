---
id: 'password'
title: 'Password Protection'
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

# Password Protection

This chapter introduces how to read and write password-protected Excel files.

## Overview

Apache Fesod (Incubating) supports Excel's built-in password protection for both reading and writing. Use `.password("xxx")` on the builder to configure the password. Successfully completed `.xlsx` output with a non-empty password uses Apache POI's encryption (AES-based), which can be memory-intensive for large workbooks. Legacy `.xls` protection differs from `.xlsx` encryption and must not be assumed to provide equivalent confidentiality.

## Writing with Password

### Code Example

```java
@Test
public void passwordWrite() {
    String fileName = "passwordWrite" + System.currentTimeMillis() + ".xlsx";

    FesodSheet.write(fileName)
        .password("your_password")
        .head(DemoData.class)
        .sheet("PasswordSheet")
        .doWrite(data());
}
```

---

## Reading with Password

### Code Example

```java
@Test
public void passwordRead() {
    String fileName = "path/to/encrypted.xlsx";

    FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
        .password("your_password")
        .sheet()
        .doRead();
}
```

---

## Security Notes

- XLSX password protection applies to successfully completed output; it does not encrypt all temporary files, caches, or JVM memory used during processing.
- Protect temporary and output directories from untrusted access. File output may contain plaintext during processing; publish results only after the entire write succeeds.
- Treat output from a failed write or encryption operation as incomplete and sensitive, and do not deliver it.
- Without the correct password, reading will throw an `EncryptedDocumentException`.
- In production, avoid hardcoding passwords — use configuration files or secrets management.

See the [Security Model](/community/security-model#password-protection) for encryption boundaries, [temporary-file requirements](/community/security-model#temporary-files-and-caches), and [Apache POI's role and limitations](/community/security-model#apache-poi).

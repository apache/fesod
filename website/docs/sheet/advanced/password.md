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

Fesod supports Excel's built-in password protection for both reading and writing. Use `.password("xxx")` on the builder to encrypt/decrypt files. For `.xlsx` output, Fesod uses Apache POI's encryption (AES-based) which can be memory-intensive for large workbooks. For legacy `.xls` files, password protection behaves as write-protection rather than full content encryption, so behavior and security guarantees differ from `.xlsx`.

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

- Excel password protection encrypts the file content, making it unreadable without the password.
- Without the correct password, reading will throw an `EncryptedDocumentException`.
- In production, avoid hardcoding passwords — use configuration files or secrets management.

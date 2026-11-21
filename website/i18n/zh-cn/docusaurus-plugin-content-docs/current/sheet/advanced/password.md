---
id: 'password'
title: '密码保护'
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

# 密码保护

本章节介绍如何读取和写入受密码保护的 Excel 文件。

## 概述

Apache Fesod (Incubating) 支持 Excel 内置的密码保护功能，适用于读取和写入操作。在构建器上使用 `.password("xxx")` 配置密码。对于 `.xlsx` 输出，设置非空密码并成功完成写入后，Apache Fesod (Incubating) 使用 Apache POI 的加密实现（基于 AES），大文件加密时内存开销较高。旧版 `.xls` 的保护行为与 `.xlsx` 加密不同，不应假定其具有相同的保密性。

## 带密码写入

### 代码示例

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

## 带密码读取

### 代码示例

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

## 安全说明

- XLSX 密码保护适用于成功完成的输出，不代表处理期间的临时文件、缓存或 JVM 内存也已加密。
- 保护临时目录和输出目录，禁止不可信主体访问。文件输出在处理期间可能包含明文，必须在整个写入流程成功后再交付。
- 写入或加密失败后的结果应按未完成的敏感数据处理，不得交付。
- 如果密码不正确，读取时会抛出 `EncryptedDocumentException` 异常。
- 在生产环境中，请避免硬编码密码 — 建议使用配置文件或密钥管理服务。

请参阅[安全模型](/community/security-model#password-protection)，了解加密边界、[临时文件保护要求](/community/security-model#temporary-files-and-caches)，以及 [Apache POI 的作用与限制](/community/security-model#apache-poi)。

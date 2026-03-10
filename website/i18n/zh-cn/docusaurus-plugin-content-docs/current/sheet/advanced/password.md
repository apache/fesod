---
id: 'password'
title: '密码保护'
---

# 密码保护

本章节介绍如何读取和写入受密码保护的 Excel 文件。

## 概述

Fesod 支持 Excel 内置的密码加密功能，适用于读取和写入操作。在构建器上使用 `.password("xxx")` 即可加密输出文件或解密输入文件。加密使用 Excel 原生的 AES 加密方式（适用于 `.xlsx` 文件），兼容所有 Excel 版本。

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

- Excel 密码保护会加密文件内容，没有密码将无法读取。
- 如果密码不正确，读取时会抛出 `EncryptedDocumentException` 异常。
- 在生产环境中，请避免硬编码密码 — 建议使用配置文件或密钥管理服务。

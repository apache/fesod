---
title: "Apache Fesod (Incubating) 2.1.0-incubating Officially Released"
description: "The Apache Fesod community is pleased to announce the official release of Apache Fesod (Incubating) 2.1.0-incubating."
authors: [alaahong]
tags: [announcement, release]
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

**October 2026** — The Apache Fesod (Incubating) community is pleased to announce the official release of **Apache Fesod (Incubating) 2.1.0-incubating**.

This release continues Fesod's steady evolution under the Apache Software Foundation (ASF) Incubator. With **110 pull requests** merged by contributors from around the world, it delivers significant new capabilities for column-based reading and writing, enhanced Excel format compatibility, performance optimizations, and strengthened supply-chain security through fully reproducible builds.

<!-- truncate -->

## Milestone Significance: From Compliance to Capability

After laying a solid compliance and governance foundation in the 2.0.x releases, version 2.1.0 marks Fesod's transition from "getting the house in order" to **shipping meaningful new features at scale**:

* **110 PRs Merged:** Covering features, bug fixes, refactoring, documentation, testing, and build automation.
* **10 New Contributors from Four Countries:** Hailing from **China, Ireland, Latvia, and South Korea**, this release's contributors reflect Fesod's growing international reach.
* **Reproducible Builds:** Fixed `project.build.outputTimestamp` guarantees byte-for-byte reproducible artifacts — a key supply-chain security improvement.
* **Security Hardening:** Remote URL image access now requires an explicit allowlist, and multiple dependency vulnerabilities (XXE, CVE fixes) were resolved.

## Key Highlights

### 1. New Features for Flexible Excel Processing

* **Freeze Pane Support:** Freeze rows and columns when writing via the `@FreezePane` annotation or a custom `WriteHandler`.
* **Column-Based Reading for XLS and CSV:** In addition to existing column-based reading for XLSX, `2.1.0` extends column-level read control to legacy **XLS** and **CSV** files, letting you read only the columns you need.
* **Fluent Header API for No-Bean Mode:** Build headers programmatically with a fluent API when you are not using annotated beans.
* **`java.time.LocalTime` Converters:** Native conversion support for `LocalTime` values in read and write paths.
* **Image Converter Refinement:** `StringImageConverter` was split into dedicated `Pathname` and `Base64` converters for clearer, safer image handling.
* **`FesodDoc` Initial Structure:** A new module lays the groundwork for future Word document (`.docx`) support.
* **Performance:** Date and number formatters are now cached to reduce repeated parsing overhead during large reads.

### 2. Security & Compliance

* **Remote URL Image Allowlist:** `read` now requires an explicit allowlist for remote URL images, mitigating SSRF-style risks.
* **Reproducible Builds:** Fixed timestamps produce identical artifacts across builds, enabling community verification of published binaries.
* **Incubating Compliance:** `DISCLAIMER` files are now bundled into both binary and sources JARs under `META-INF`.
* **Dependency Hardening:** Upgraded `assertj-core` (XXE fix), `fastjson2`, `slf4j`, and npm dependencies to resolve known vulnerabilities.

### 3. Stability & Robustness

* **Legacy Format Fixes:** XLS BIFF8 encryption is now correctly applied (previously broken by premature password clearing).
* **Correct Date Handling:** Fixed `java.sql.Date/Time` handling in CSV cells and `1904` windowing fallback for `@DateTimeFormat` defaults.
* **Template Fill on Windows:** Resolved fill-template copy failures on Windows platforms.
* **Batch Read Integrity:** Fixed `PageReadListener` duplicate rows when switching sheets, and copied sheet-level read parameters to sheet holders.

## A Global Community

"Community Over Code" comes alive in the geography of this release. The **16 named contributors** behind 2.1.0-incubating span four countries:

* **China (13):** Suzhou (3), Hangzhou (1), Tianjin (1), Beijing (1), and other cities (7).
* **Ireland (1):** Kilkenny.
* **Latvia (1).**
* **South Korea (1).**

From column-based reading to freeze panes, these features were shaped through international collaboration — code reviews, discussions, and testing carried out across time zones. This diversity is the engine driving Fesod's steady progress under the ASF Incubator.

## Key Changes at a Glance

### **Features**

* Added freeze pane support via `@FreezePane` or `WriteHandler`.
* Added column-based reading for XLS and CSV files.
* Added fluent header API for no-bean mode.
* Split `StringImageConverter` into `Pathname` and `Base64` converters.
* Added `java.time.LocalTime` converters.
* Added column index limit support in `ReadSheet`.
* Added initial `FesodDoc` class structure.
* Registered `EscapeHexCellWriteHandler` by default for XLSX.

### **Bugfixes**

* Fixed XLS BIFF8 encryption not being applied.
* Fixed `CsvCell` crashes on `java.sql.Date/Time` values.
* Fixed `PageReadListener` duplicate rows between sheets.
* Fixed Windows fill-template copy failures.
* Fixed `1904` windowing fallback for `@DateTimeFormat` defaults.
* Fixed `_xHHHH_` escape decoding in inline string cells.

### **Refactoring**

* Unified and overloaded `readSheet` methods for flexible usage.
* Replaced list-based column lookups with `ColumnIndexResolver`.
* Generated trivial accessors with Lombok.
* Removed the `fesod-examples` module.

> For a detailed list of changes, please refer to the [GitHub Release Notes](https://github.com/apache/fesod/releases/tag/2.1.0-incubating) and the [full changelog](https://github.com/apache/fesod/compare/2.0.2-incubating...2.1.0-incubating).

## Acknowledgments

"Community Over Code" is the core philosophy of the Apache Software Foundation. We would like to thank all the developers, mentors, and community members who contributed to this release.

### New Contributors

We would like to extend a warm welcome and a special thank you to the **10 new members** who made their first contribution in this release:

> @32154678925, @skytin1004, @sapienza88, @Duansg, @nkuprins, @Aias00, @xleoken, @leehaut, @codeAnqiang-ma, @Mikkey-f

Special thanks to **@alaahong, @delei, @psxjoy, @bengbengbalabalabeng, @nkuprins, @sapienza88, @GOODBOY008, @pjfanning**, and everyone who submitted PRs and suggestions on GitHub. Your dedication to features, bug fixing, testing, and release management made this release possible.

## How to Get Involved

You can download and experience the new Apache Fesod (Incubating) through the following channels:

* **Official Website:** [https://fesod.apache.org/](https://fesod.apache.org/)
* **Source Code:** [https://github.com/apache/fesod](https://github.com/apache/fesod)
* **Maven Central:**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>2.1.0-incubating</version>
</dependency>
```

**Join Us!**
The Apache Fesod (Incubating) community is always open to new contributors. You can reach out to us by subscribing to the mailing list at `dev@fesod.apache.org` or by submitting issues on GitHub.

We look forward to growing together within the Apache Incubator!

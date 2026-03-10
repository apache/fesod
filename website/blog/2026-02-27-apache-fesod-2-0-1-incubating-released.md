---
title: "Apache Fesod (Incubating) 2.0.1-incubating Officially Released"
description: "The Apache Fesod community is pleased to announce the official release of Apache Fesod (Incubating) 2.0.1-incubating."
authors: [psxjoy]
tags: [announcement, release]
date: 2026-02-27T00:00:00.000Z
---

**February 2026** — The Apache Fesod (Incubating) community is pleased to announce the official release of **Apache Fesod (Incubating) 2.0.1-incubating**.

This is not only a technical iteration but also a significant milestone: **the first official release of Fesod since joining the Apache Software Foundation (ASF) Incubator.** Over the past few months, the community has collaboratively completed extensive compliance improvements, architectural refactoring, and functional enhancements. This release marks Fesod’s readiness to embark on a new chapter of high-performance Excel processing under the ASF governance.

<!-- truncate -->

## Milestone Significance: The First Official ASF Release

As our "debut" in the incubator, version 2.0.1 represents a decisive step forward in embracing **"The Apache Way"**:

* **Comprehensive Renaming & Compliance:** We have completed the full transition from `FastExcel` to `Fesod` (including package names, class names, and documentation) to ensure the brand is fully protected under the ASF.
* **Compliance Completion:** We have introduced the `DISCLAIMER`, `NOTICE`, and `support` modules, and implemented automated License Header management via Spotless, fully aligning with ASF policies.
* **Community Governance:** All code merges and release processes were conducted through public discussion and voting on the mailing lists, reflecting the principles of openness and transparency.

## Key Highlights

### 1. Deep Architectural Refactoring

To support long-term sustainability, we have significantly optimized the project structure:

* **Multi-module Architecture:** Introduced a multi-module structure for clearer hierarchy and finer dependency management.
* **Distribution Enhancement:** Added the `fesod-distribution` module to standardize the description and building of distribution packages.
* **JDK 25 Support:** Keeping pace with the Java ecosystem, we have officially added compatibility and support for **JDK 25**.

### 2. New Features

This release introduces several practical features to enhance the flexibility of Excel processing:

* **Enhanced Excel Processing:** Added the `afterSheetDispose` lifecycle method in `SheetWriteHandler` and introduced a brand-new **Header Merge Strategy**.
* **Legacy Format Compatibility:** Gracefully handles very old Excel BIFF formats to prevent runtime crashes.
* **Flexible Control:** Added the `autoStrip` parameter and introduced validation for maximum Sheet name length in `Workbook` constants.
* **Website Experience:** Added local search functionality and Matomo analytics to the official documentation, with optimized display for mobile devices.

### 3. Stability & Robustness

We have invested heavily in quality assurance:

* **Fuzz Testing:** Added daily fuzz testing for Excel reading to proactively identify potential crashes when processing malformed files.
* **Dependency Security:** Upgraded core dependencies including Spring-core, Logback, Fastjson2, and POI (to 5.5.1) to resolve known vulnerabilities.
* **CI/CD Optimization:** Introduced Netlify preview deployments and automated workflows for closing stale issues, significantly improving community collaboration efficiency.

## Key Changes at a Glance

### **Features**

* Added `afterSheetDispose` method to `SheetWriteHandler`.
* Introduced Header Merge Strategy for Excel writing.
* Added GitHub Actions for Netlify preview deployments.
* Integrated `docusaurus-search-local` for official website search.

### **Bugfixes**

* Fixed NPE in the `WriteSheetWorkbookWriteHandler` class.
* Fixed broken links and incorrect code examples in the documentation.
* Updated import statements to use the shaded `cglib` package.

### **Refactoring**

* Renamed project to Fesod/FesodSheet and updated package structures to align with Apache naming conventions.
* Implemented Spotless for automated License Header management.
* Removed outdated and incorrect `@since` tags.

> For a detailed list of changes, please refer to the [GitHub Release Notes](https://github.com/apache/fesod/releases/tag/2.0.1-incubating).

## Acknowledgments

"Community Over Code" is the core philosophy of the Apache Software Foundation. We would like to thank all the developers, mentors, and community members who contributed to this release.

### New Contributors

We would like to extend a warm welcome and a special thank you to the **14 new members** who made their first contribution in this release:

> @X-qinghai, @ngocnhan-tran1996, @YIminta, @jounghu, @wlgusqkr, @gaushon, @GOODBOY008, @ongdisheng, @harshasiddartha, @pjfanning, @liugddx, @hezhangjian, @bengbengbalabalabeng

Special thanks to **@delei, @psxjoy, @alaahong, @ongdisheng, @GOODBOY008**, and everyone who submitted PRs and suggestions on GitHub. Your tireless efforts in renaming, compliance checking, CI optimization, and feature improvements have made Fesod's ASF debut a success.

## How to Get Involved

You can download and experience the new Apache Fesod (Incubating) through the following channels:

* **Official Website:** [https://fesod.apache.org/](https://fesod.apache.org/)
* **Source Code:** [https://github.com/apache/fesod](https://github.com/apache/fesod)
* **Maven Central:**

```xml
<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>2.0.1-incubating</version>
</dependency>
```

**Join Us!**
The Apache Fesod (Incubating) community is always open to new contributors. You can reach out to us by subscribing to the mailing list at `dev@fesod.apache.org` or by submitting issues on GitHub.

We look forward to growing together within the Apache Incubator!

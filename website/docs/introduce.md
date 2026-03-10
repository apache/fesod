---
id: 'introduce'
title: 'Introduction'
slug: /
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

# Apache Fesod (Incubating)

## Introduction

**Apache Fesod (Incubating)** is a high-performance and memory-efficient Java library for reading and writing
spreadsheet
files, designed to simplify development and ensure reliability.

Apache Fesod (Incubating) can provide developers and enterprises with great freedom and flexibility. We plan to
introduce more new features in the future to continually enhance user experience and tool usability. Apache Fesod (
Incubating) is committed to being your best choice for handling spreadsheet files.

The name fesod(pronounced `/ˈfɛsɒd/`), an acronym for "fast easy spreadsheet and other documents" expresses the
project's origin, background and vision.

### Features

- **High-performance Reading and Writing**: Apache Fesod (Incubating) focuses on performance optimization, capable of
  efficiently handling large-scale spreadsheet data. Compared to some traditional spreadsheet processing libraries, it
  can
  significantly reduce memory consumption.
- **Simplicity and Ease of Use**: The library offers a simple and intuitive API, allowing developers to easily integrate
  it into projects, whether for simple spreadsheet operations or complex data processing.
- **Stream Operations**: Apache Fesod (Incubating) supports stream reading, minimizing the problem of loading large
  amounts of data at once. This design is especially important when dealing with hundreds of thousands or even millions
  of rows of data.

## Quick Example

### Read

```java
FesodSheet.read("demo.xlsx", DemoData.class, new DemoDataListener()).sheet().doRead();
```

### Write

```java
FesodSheet.write("demo.xlsx", DemoData.class).sheet("Template").doWrite(data());
```

:::tip
For complete examples with POJO definitions, listeners, and data preparation, see the [Quick Start](/docs/quickstart/simple-example) guide.
:::

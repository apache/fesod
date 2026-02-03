---
id: 'spring'
title: 'Spring'
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

# Spring Integration Guide

This chapter introduces how to integrate and use Fesod in the Spring framework to generate spreadsheet files.

## Spring Controller Example

### Overview

In Spring Boot projects, you can generate spreadsheet files and provide download functionality through HTTP interfaces,
making it convenient to use Fesod in web environments.

### Code Example

```java

@GetMapping("download")
public void download(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setCharacterEncoding("utf-8");
    String fileName = URLEncoder.encode("demo", "UTF-8").replaceAll("\\+", "%20");
    response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

    FesodSheet.write(response.getOutputStream(), DemoData.class)
            .sheet("Sheet1")
            .doWrite(data());
}
```

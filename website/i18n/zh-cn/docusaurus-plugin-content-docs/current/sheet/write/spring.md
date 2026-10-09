---
id: 'spring'
title: '与 Spring 集成'
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

# 与 Spring 集成指南

本章节介绍如何在 Spring 框架中集成和使用 Fesod 来生成电子表格文件。

## Spring 控制器示例

### 概述

Spring Boot 项目中可以通过 HTTP 接口生成电子表格文件并提供下载功能，便于在 Web 环境下使用 Fesod。

### 代码示例

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

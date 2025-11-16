---
id: 'spring'
title: '与 Spring 集成'
---

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

---
id: 'spring'
title: 'Spring'
---

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

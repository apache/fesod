---
id: 'spring'
title: 'Spring'
---

# Spring Integration Guide
This chapter introduces how to integrate and use FastExcel in the Spring framework to generate Excel files.

## Spring Controller Example

### Overview
In Spring Boot projects, you can generate Excel files and provide download functionality through HTTP interfaces, making it convenient to use FastExcel in web environments.

### Code Example
```java
@GetMapping("download")
public void download(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setCharacterEncoding("utf-8");
    String fileName = URLEncoder.encode("demo", "UTF-8").replaceAll("\\+", "%20");
    response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

    FastExcel.write(response.getOutputStream(), DemoData.class)
        .sheet("Sheet1")
        .doWrite(data());
}
```

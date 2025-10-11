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



## Spring Boot Starter

For Spring Boot 2.x or 3.x applications you can depend on the starter to get auto-configuration, a preconfigured `FesodTemplate` bean, and property binding out of the box. The starter keeps its Spring Boot dependencies in `provided` scope, so your application controls the exact Boot version it runs with.

```xml
<dependency>
        <groupId>org.apache.fesod</groupId>
        <artifactId>fesod-spring-boot-starter</artifactId>
        <version>version</version>
</dependency>
```

Inject the template anywhere in your application to obtain writer builders:

```java
@Service
public class ReportService {

    private final FesodTemplate fesodTemplate;

    public ReportService(FesodTemplate fesodTemplate) {
        this.fesodTemplate = fesodTemplate;
        }

        public void exportReport(OutputStream out, Class<?> head, Collection<?> rows) {
        fesodTemplate.writer(out, head)
                    .sheet("report")
                    .doWrite(rows);
        }
}
```

> ℹ️ When using Spring Boot 3.x, make sure your project targets Java 17 or newer, as required by Spring Boot 3.

Starter behaviour can be tuned through standard Spring configuration properties (`application.yml` shown, `application.properties` also supported):

```yaml
fesod:
    global:
        auto-trim: true
        locale: zh-CN
    reader:
        ignore-empty-row: true
        auto-close-stream: true
    writer:
        in-memory: false
        write-excel-on-exception: true
```

By default (without explicit configuration) the starter keeps the same defaults as core Fesod:

| Property | Default | Description |
| --- | --- | --- |
| `fesod.global.auto-trim` | `true` | Trim leading/trailing whitespace for sheet names and cell values |
| `fesod.global.auto-strip` | `false` | Strip non-printable characters only when enabled |
| `fesod.global.use1904-windowing` | `false` | Use the 1900 Excel date system |
| `fesod.global.use-scientific-format` | `false` | Reader-specific toggle for scientific number formatting |
| `fesod.global.locale` | JVM default locale | Drives number/date formatting |
| `fesod.global.filed-cache-location` | `THREAD_LOCAL` | Metadata cache strategy |
| `fesod.reader.ignore-empty-row` | `true` | Skip blank rows during reads |
| `fesod.reader.auto-close-stream` | `true` | Close input streams when finished |
| `fesod.reader.mandatory-use-input-stream` | `false` | If `false`, streams may be spooled to disk for performance |
| `fesod.writer.auto-close-stream` | `true` | Close output streams automatically |
| `fesod.writer.with-bom` | `true` | Emit BOM for CSV output |
| `fesod.writer.in-memory` | `false` | Write through temporary files before finalizing |
| `fesod.writer.write-excel-on-exception` | `false` | Do not keep partially written files on failure |

For advanced scenarios you can also contribute `FesodReaderBuilderCustomizer` or `FesodWriterBuilderCustomizer` beans to tweak the underlying builders before every operation.

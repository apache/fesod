---
id: 'spring'
title: 'Spring'
---

# Spring Integration Guide

This chapter introduces how to integrate and use FastExcel in the Spring framework to handle Excel files uploaded by users.

## Overview

By creating RESTful API endpoints, users can upload Excel files using HTTP requests, and the server uses FastExcel to parse the data.

## Environment Dependencies

### Maven

Ensure the necessary dependencies are included in your pom.xml file:

```xml
<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>{version.number}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

## Creating Upload Endpoints

### POJO Class

First, define a POJO class for mapping Excel data:

```java
@Getter
@Setter
@ToString
public class UploadData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### Data Listener

Create a listener to handle each row of data:

```java
@Slf4j
public class UploadDataListener extends AnalysisEventListener<UploadData> {
    private final List<UploadData> list = new ArrayList<>();

    @Override
    public void invoke(UploadData data, AnalysisContext context) {
        log.info("Read one record: {}", JSON.toJSONString(data));
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data reading completed!");
        // Data storage operations can be performed here, such as saving to database
    }
}
```

### Spring Controller

Create a controller to handle file upload requests:

```java
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload!");
        }

        try {
            FastExcel.read(file.getInputStream(), UploadData.class, new UploadDataListener())
                    .sheet()
                    .doRead();
            return ResponseEntity.ok("File uploaded and processed successfully!");
        } catch (IOException e) {
            log.error("File processing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing failed");
        }
    }
}
```

## Complex Scenarios

### Multi-Template Parsing

By defining multiple different model classes and processing methods within the same listener, you can extend support for multi-template parsing as needed.

### Exception Handling

To improve user experience and ensure program robustness, exception handling logic needs to be added during data processing.
You can override the `onException` method in custom listeners for detailed exception handling.

### Practical Applications

In real-world scenarios, parsed data may be stored in a database.
Database interaction logic can be implemented in the `doAfterAllAnalysed` method to ensure data persistence.


## Spring Boot Starter

For Spring Boot 2.x or 3.x applications you can depend on the starter to get auto-configuration, a preconfigured `FesodTemplate` bean, and property binding out of the box. The starter keeps its Spring Boot dependencies in `provided` scope, so your application controls the exact Boot version it runs with.

```xml
<dependency>
        <groupId>org.apache.fesod</groupId>
        <artifactId>fesod-spring-boot-starter</artifactId>
        <version>version</version>
</dependency>
```

Inject the template anywhere in your application to obtain reader builders:

```java
@Service
@RequiredArgsConstructor
public class SampleService {
    private final FesodTemplate fesodTemplate;

    @Data
    public static class Sample {
        private String data;
    }

    public List<Sample> read(String filePath) throws IOException {
        List<Sample> result = new LinkedList<>();
        fesodTemplate.reader(filePath, Sample.class, new ReadListener<Sample>() {
            @Override
            public void invoke(Sample data, AnalysisContext context) {
                result.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).doReadAll();
        return result;
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

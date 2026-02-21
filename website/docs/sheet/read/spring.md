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

This chapter introduces how to integrate and use Fesod in the Spring framework to handle spreadsheet files uploaded by
users.

## Overview

By creating RESTful API endpoints, users can upload spreadsheet files using HTTP requests, and the server uses Fesod to
parse the data.

## Environment Dependencies

### Maven

Ensure the necessary dependencies are included in your pom.xml file:

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
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

First, define a POJO class for mapping spreadsheet data:

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
            FesodSheet.read(file.getInputStream(), UploadData.class, new UploadDataListener())
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

By defining multiple different model classes and processing methods within the same listener, you can extend support for
multi-template parsing as needed.

### Exception Handling

To improve user experience and ensure program robustness, exception handling logic needs to be added during data
processing.
You can override the `onException` method in custom listeners for detailed exception handling.

### Practical Applications

In real-world scenarios, parsed data may be stored in a database.
Database interaction logic can be implemented in the `doAfterAllAnalysed` method to ensure data persistence.

---
id: 'spring'
title: '与 Spring 集成'
---

# 与 Spring 集成指南

本章节介绍如何在 Spring 框架中集成和使用 Fesod 来处理用户上传的电子表格文件。

## 概述

通过创建 RESTful API 接口，用户可以使用 HTTP 请求上传电子表格文件，服务器端使用 Fesod 解析数据。

## 环境依赖

### Maven

确保在 pom.xml 文件中包括必要的依赖项：

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>版本号</version>
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

## 创建上传接口

### POJO类

首先，定义一个用于映射电子表格数据的 POJO 类：

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

### 数据监听器

创建一个监听器来处理每一行数据：

```java

@Slf4j
public class UploadDataListener extends AnalysisEventListener<UploadData> {
    private final List<UploadData> list = new ArrayList<>();

    @Override
    public void invoke(UploadData data, AnalysisContext context) {
        log.info("读取到一条数据: {}", JSON.toJSONString(data));
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据读取完成！");
        // 在此处可以进行数据的存储操作，如保存到数据库
    }
}
```

### Spring 控制器

创建一个控制器来处理文件上传请求：

```java

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择一个文件上传！");
        }

        try {
            FesodSheet.read(file.getInputStream(), UploadData.class, new UploadDataListener())
                    .sheet()
                    .doRead();
            return ResponseEntity.ok("文件上传并处理成功！");
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件处理失败！");
        }
    }
}
```

## 复杂场景

### 多模板解析

通过在同一个监听器中定义多个不同的模型类和处理方法，可以根据需要扩展支持多模板解析。

### 异常处理

为了改善用户体验并保证程序健壮性，需要在数据处理过程中加入异常处理逻辑，可以在自定义监听器中重写 `onException`
方法进行详细的异常处理。

### 实际应用

在实际场景中，解析的数据可能需要存储到数据库中。可以在 `doAfterAllAnalysed` 方法中实现数据库交互逻辑，确保数据的持久化。

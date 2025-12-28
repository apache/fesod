# **1Apache Fesod CLI Tool - Shell 脚本完整设计方案**

## **📋 目录**

1. [项目概述](#1-项目概述)
2. [完整项目结构](#2-完整项目结构)
3. [核心代码实现](#3-核心代码实现)
4. [启动脚本](#4-启动脚本)
5. [Maven 配置](#5-maven-配置)
6. [构建与部署](#6-构建与部署)
7. [使用文档](#7-使用文档)
8. [测试方案](#8-测试方案)

---

## **1. 项目概述**

### **1.1 设计目标**

- ✅ **完全兼容 JDK 8+**
- ✅ **简单易用**:  `fesod-cli read data.xlsx --format json`
- ✅ **跨平台支持**: Linux、macOS、Windows
- ✅ **零配置**: 解压即用
- ✅ **轻量级**: 打包后 ~30MB
- ✅ **模块化**:  支持未来扩展（响应 @delei 的 `--module` 建议）

### **1.2 技术栈**

| 组件 | 技术选型 | 版本 |
|------|---------|------|
| JDK | Java SE | 8+ |
| CLI 框架 | Picocli | 4.7.5 |
| JSON 处理 | Fastjson2 | 2.0.60 |
| YAML 解析 | SnakeYAML | 2.2 |
| CSV 处理 | Apache Commons CSV | 1.11. 0 |
| 日志 | SLF4J + Logback | 1.7.36 / 1.5.23 |
| 构建工具 | Maven | 3.6+ |

---

## **2. 完整项目结构**

```
fesod-cli/
├── pom.xml                                          # Maven 配置
├── README.md                                        # 用户文档
├── BUILDING.md                                      # 构建指南
├── LICENSE                                          # Apache 2.0 许可证
├── NOTICE                                           # 版权声明
│
├── src/main/java/org/apache/fesod/cli/
│   ├── FesodCli.java                                # CLI 主入口
│   │
│   ├── core/                                        # 核心服务层
│   │   ├── DocumentProcessor.java                  # 文档处理器接口
│   │   ├── ModuleRegistry.java                     # 模块注册器
│   │   └── sheet/                                  # Sheet 模块
│   │       ├── SheetProcessor.java                 # Sheet 处理器
│   │       ├── SheetReader.java                    # Sheet 读取器
│   │       ├── SheetWriter.java                    # Sheet 写入器
│   │       └── SheetConverter.java                 # Sheet 转换器
│   │
│   ├── commands/                                    # CLI 命令
│   │   ├── BaseCommand.java                        # 命令基类
│   │   ├── ReadCommand.java                        # read 命令
│   │   ├── WriteCommand.java                       # write 命令
│   │   ├── ConvertCommand.java                     # convert 命令
│   │   ├── InfoCommand.java                        # info 命令
│   │   └── VersionCommand.java                     # version 命令
│   │
│   ├── formatters/                                  # 格式化器
│   │   ├── OutputFormatter.java                    # 格式化器接口
│   │   ├── FormatterFactory.java                   # 格式化器工厂
│   │   ├── JsonFormatter.java                      # JSON 格式化器
│   │   ├── CsvFormatter.java                       # CSV 格式化器
│   │   └── XmlFormatter.java                       # XML 格式化器
│   │
│   ├── config/                                      # 配置管理
│   │   ├── CliConfig.java                          # 配置类
│   │   ├── ConfigLoader.java                       # 配置加载器
│   │   └── ConfigValidator.java                    # 配置验证器
│   │
│   ├── exception/                                   # 异常定义
│   │   ├── CliException.java                       # CLI 基础异常
│   │   ├── FileProcessException.java               # 文件处理异常
│   │   └── ConfigurationException.java             # 配置异常
│   │
│   └── utils/                                       # 工具类
│       ├── FileUtils.java                          # 文件工具
│       └── LogUtils.java                           # 日志工具
│
├── src/main/scripts/
│   ├── fesod-cli                                    # Unix/Linux/macOS 启动脚本
│   └── fesod-cli.bat                                # Windows 启动脚本
│
├── src/main/resources/
│   ├── application.properties                       # 应用配置
│   ├── logback.xml                                  # 日志配置
│   └── default-config.yaml                          # 默认配置模板
│
├── src/assembly/
│   └── bin. xml                                      # 分发打包描述
│
└── src/test/java/org/apache/fesod/cli/
    ├── commands/                                    # 命令测试
    │   ├── ReadCommandTest.java
    │   ├── ConvertCommandTest.java
    │   └── InfoCommandTest.java
    ├── core/sheet/                                  # 核心服务测试
    │   └── SheetProcessorTest.java
    └── integration/                                 # 集成测试
        └── CliIntegrationTest.java
```

---

## **3. 核心代码实现**

### **3.1 主入口 - FesodCli.java**

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fesod.cli;

import org.apache.fesod.cli.commands.*;
import org.apache.fesod.cli.exception.CliException;
import picocli.CommandLine;
import picocli.CommandLine. Command;
import picocli. CommandLine.Option;

/**
 * Apache Fesod CLI - Main Entry Point
 * 
 * @author Apache Fesod Team
 * @since 2.0.0
 */
@Command(
    name = "fesod-cli",
    mixinStandardHelpOptions = true,
    version = {
        "Apache Fesod CLI 2.0.0",
        "Java Runtime:  ${java.version}",
        "OS: ${os.name} ${os.arch}"
    },
    description = "Fast and Easy spreadsheet processing from the command line",
    subcommands = {
        ReadCommand.class,
        WriteCommand. class,
        ConvertCommand. class,
        InfoCommand.class,
        VersionCommand.class,
        CommandLine.HelpCommand.class
    },
    usageHelpAutoWidth = true,
    footer = {
        "",
        "Examples:",
        "  fesod-cli read data.xlsx --format json",
        "  fesod-cli convert input.xls output.xlsx",
        "  fesod-cli info data.xlsx",
        "",
        "Documentation:  https://fesod.apache.org/docs/cli",
        "Report bugs:  https://github.com/apache/fesod/issues"
    }
)
public class FesodCli implements Runnable {

    @Option(
        names = {"--verbose", "-v"},
        description = "Enable verbose logging"
    )
    private boolean verbose;

    @Override
    public void run() {
        // Default:  show help when no command specified
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FesodCli())
            .setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                cmd.getErr().println(cmd.getColorScheme().errorText("Error: " + ex.getMessage()));
                
                if (ex instanceof CliException) {
                    CliException cliEx = (CliException) ex;
                    if (cliEx.getCause() != null && parseResult.hasMatchedOption("--verbose")) {
                        cliEx.printStackTrace(cmd.getErr());
                    }
                    return cliEx.getExitCode();
                } else {
                    if (parseResult.hasMatchedOption("--verbose")) {
                        ex.printStackTrace(cmd.getErr());
                    }
                    return 1;
                }
            })
            .execute(args);
        
        System.exit(exitCode);
    }
}
```

### **3.2 核心服务层**

#### **DocumentProcessor. java**

```java
package org.apache.fesod. cli.core;

import java.nio.file.Path;
import java.util.Map;

/**
 * Document processor abstraction for different document types
 */
public interface DocumentProcessor {
    
    /**
     * Read document and return data
     */
    Map<String, Object> read(Path inputPath, Map<String, Object> options);
    
    /**
     * Write data to document
     */
    void write(Map<String, Object> data, Path outputPath, Map<String, Object> options);
    
    /**
     * Convert document format
     */
    void convert(Path inputPath, Path outputPath, Map<String, Object> options);
    
    /**
     * Get document information
     */
    Map<String, Object> getInfo(Path inputPath);
    
    /**
     * Get supported module name
     */
    String getModuleName();
}
```

#### **ModuleRegistry.java**

```java
package org.apache.fesod.cli.core;

import org.apache.fesod. cli.core.sheet.SheetProcessor;
import org.apache.fesod.cli.exception.CliException;

import java.util.HashMap;
import java.util.Map;

/**
 * Module registry for managing document processors
 */
public class ModuleRegistry {
    
    private static final Map<String, DocumentProcessor> PROCESSORS = new HashMap<String, DocumentProcessor>();
    
    static {
        // Register sheet processor
        registerProcessor(new SheetProcessor());
    }
    
    public static void registerProcessor(DocumentProcessor processor) {
        PROCESSORS.put(processor.getModuleName(), processor);
    }
    
    public static DocumentProcessor getProcessor(String moduleName) {
        DocumentProcessor processor = PROCESSORS.get(moduleName);
        if (processor == null) {
            throw new CliException(
                "Unsupported module: " + moduleName + 
                ". Available modules: " + String.join(", ", PROCESSORS. keySet())
            );
        }
        return processor;
    }
    
    public static String[] getModuleNames() {
        return PROCESSORS.keySet().toArray(new String[0]);
    }
}
```

#### **SheetProcessor.java**

```java
package org.apache.fesod.cli.core.sheet;

import org.apache.fesod.cli.core.DocumentProcessor;
import org.apache.fesod.cli.exception.FileProcessException;
import org.apache.fesod.sheet.FesodSheet;
import org. apache.fesod.sheet. read.metadata.ReadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Sheet document processor implementation
 */
public class SheetProcessor implements DocumentProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(SheetProcessor.class);
    
    private final SheetReader reader;
    private final SheetWriter writer;
    private final SheetConverter converter;
    
    public SheetProcessor() {
        this.reader = new SheetReader();
        this.writer = new SheetWriter();
        this.converter = new SheetConverter();
    }
    
    @Override
    public Map<String, Object> read(Path inputPath, Map<String, Object> options) {
        log.info("Reading spreadsheet from: {}", inputPath);
        
        try {
            Integer sheetIndex = (Integer) options.get("sheetIndex");
            String sheetName = (String) options.get("sheetName");
            Boolean readAll = (Boolean) options.get("readAll");
            if (readAll == null) {
                readAll = false;
            }
            
            return reader.read(inputPath, sheetIndex, sheetName, readAll);
            
        } catch (Exception e) {
            throw new FileProcessException("Failed to read spreadsheet:  " + e.getMessage(), e);
        }
    }
    
    @Override
    public void write(Map<String, Object> data, Path outputPath, Map<String, Object> options) {
        log.info("Writing spreadsheet to: {}", outputPath);
        
        try {
            String sheetName = (String) options.get("sheetName");
            if (sheetName == null) {
                sheetName = "Sheet1";
            }
            writer.write(data, outputPath, sheetName, options);
            
        } catch (Exception e) {
            throw new FileProcessException("Failed to write spreadsheet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void convert(Path inputPath, Path outputPath, Map<String, Object> options) {
        log.info("Converting {} to {}", inputPath, outputPath);
        
        try {
            converter.convert(inputPath, outputPath, options);
            
        } catch (Exception e) {
            throw new FileProcessException("Failed to convert spreadsheet: " + e. getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> getInfo(Path inputPath) {
        log.info("Getting info for: {}", inputPath);
        
        try {
            Map<String, Object> info = new LinkedHashMap<String, Object>();
            List<ReadSheet> sheets = FesodSheet.read(inputPath. toFile()).getSheets();
            
            info.put("file", inputPath.toString());
            info.put("fileSize", inputPath.toFile().length());
            info.put("sheetCount", sheets.size());
            
            List<Map<String, Object>> sheetInfoList = new ArrayList<Map<String, Object>>();
            for (ReadSheet sheet : sheets) {
                Map<String, Object> sheetInfo = new LinkedHashMap<String, Object>();
                sheetInfo.put("index", sheet.getSheetNo());
                sheetInfo.put("name", sheet.getSheetName());
                sheetInfo.put("hidden", sheet.getHidden());
                sheetInfo.put("rowCount", sheet.getNumRows());
                sheetInfoList.add(sheetInfo);
            }
            
            info. put("sheets", sheetInfoList);
            return info;
            
        } catch (Exception e) {
            throw new FileProcessException("Failed to get spreadsheet info: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getModuleName() {
        return "sheet";
    }
}
```

#### **SheetReader.java**

```java
package org.apache.fesod.cli.core.sheet;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba. fastjson2.JSONObject;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org. apache.fesod.sheet. context. AnalysisContext;

import java.nio.file.Path;
import java.util.*;

/**
 * Sheet reader implementation
 */
public class SheetReader {
    
    public Map<String, Object> read(Path inputPath, Integer sheetIndex, String sheetName, Boolean readAll) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        if (readAll) {
            result.put("sheets", readAllSheets(inputPath));
        } else if (sheetName != null) {
            result. put("data", readSheetByName(inputPath, sheetName));
        } else {
            int index = sheetIndex != null ? sheetIndex : 0;
            result.put("data", readSheetByIndex(inputPath, index));
        }
        
        return result;
    }
    
    private List<Map<String, Object>> readAllSheets(Path inputPath) {
        List<Map<String, Object>> allSheets = new ArrayList<Map<String, Object>>();
        List<org.apache.fesod.sheet.read.metadata.ReadSheet> sheets = 
            FesodSheet.read(inputPath.toFile()).getSheets();
        
        for (org.apache.fesod.sheet.read.metadata.ReadSheet sheet : sheets) {
            Map<String, Object> sheetData = new LinkedHashMap<String, Object>();
            sheetData.put("name", sheet.getSheetName());
            sheetData.put("index", sheet.getSheetNo());
            sheetData.put("rows", readSheetByIndex(inputPath, sheet.getSheetNo()));
            allSheets.add(sheetData);
        }
        
        return allSheets;
    }
    
    private JSONArray readSheetByIndex(Path inputPath, int sheetIndex) {
        DataCollector collector = new DataCollector();
        FesodSheet. read(inputPath.toFile(), collector)
            .sheet(sheetIndex)
            .doRead();
        return collector.getData();
    }
    
    private JSONArray readSheetByName(Path inputPath, String sheetName) {
        DataCollector collector = new DataCollector();
        FesodSheet.read(inputPath.toFile(), collector)
            .sheet(sheetName)
            .doRead();
        return collector.getData();
    }
    
    /**
     * Data collector listener
     */
    private static class DataCollector implements ReadListener<Map<Integer, String>> {
        private final JSONArray data = new JSONArray();
        
        @Override
        public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
            JSONObject row = new JSONObject(new LinkedHashMap<String, Object>());
            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                row.put("col_" + entry.getKey(), entry.getValue());
            }
            data.add(row);
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Processing complete
        }
        
        public JSONArray getData() {
            return data;
        }
    }
}
```

#### **SheetWriter.java**

```java
package org.apache.fesod.cli.core.sheet;

import com.alibaba.fastjson2.JSON;
import com.alibaba. fastjson2.JSONArray;
import com.alibaba. fastjson2.JSONObject;
import org.apache.fesod.sheet.FesodSheet;

import java.nio.file.Path;
import java.util.*;

/**
 * Sheet writer implementation
 */
public class SheetWriter {
    
    public void write(Map<String, Object> data, Path outputPath, String sheetName, Map<String, Object> options) {
        Object dataObj = data.get("data");
        
        if (dataObj instanceof String) {
            dataObj = JSON.parse((String) dataObj);
        }
        
        List<List<String>> rows = new ArrayList<List<String>>();
        
        if (dataObj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) dataObj;
            
            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                
                if (item instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) item;
                    List<String> row = new ArrayList<String>();
                    for (Object val : jsonObj.values()) {
                        row.add(val != null ? val.toString() : "");
                    }
                    rows.add(row);
                } else if (item instanceof List) {
                    List<? > list = (List<?>) item;
                    List<String> row = new ArrayList<String>();
                    for (Object val : list) {
                        row.add(val != null ? val.toString() : "");
                    }
                    rows.add(row);
                }
            }
        }
        
        FesodSheet.write(outputPath. toFile())
            .sheet(sheetName)
            .doWrite(rows);
    }
}
```

#### **SheetConverter.java**

```java
package org.apache.fesod.cli. core.sheet;

import org. apache.fesod.sheet. FesodSheet;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org. apache.fesod.sheet. context.AnalysisContext;

import java.nio.file.Path;
import java.util.*;

/**
 * Sheet format converter
 */
public class SheetConverter {
    
    public void convert(Path inputPath, Path outputPath, Map<String, Object> options) {
        final List<List<String>> allData = new ArrayList<List<String>>();
        
        FesodSheet.read(inputPath.toFile(), new ReadListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                List<String> row = new ArrayList<String>();
                for (String value : data.values()) {
                    row.add(value);
                }
                allData. add(row);
            }
            
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // Complete
            }
        }).sheet(0).doRead();
        
        FesodSheet.write(outputPath.toFile())
            .sheet("Sheet1")
            .doWrite(allData);
    }
}
```

### **3.3 命令层**

#### **BaseCommand.java**

```java
package org.apache.fesod.cli. commands;

import org.apache. fesod.cli.config.CliConfig;
import org.apache.fesod.cli.config.ConfigLoader;
import org.apache. fesod.cli.core.DocumentProcessor;
import org.apache. fesod.cli.core. ModuleRegistry;
import picocli.CommandLine. Option;

import java.nio.file. Paths;

/**
 * Base class for all CLI commands
 */
public abstract class BaseCommand implements Runnable {
    
    @Option(
        names = {"--config", "-c"},
        description = "Configuration file path (default: ~/. fesod/config.yaml)",
        paramLabel = "<file>"
    )
    protected String configFile;
    
    @Option(
        names = {"--module", "-m"},
        description = "Document module:  sheet (default: sheet)",
        defaultValue = "sheet"
    )
    protected String module;
    
    protected CliConfig config;
    protected DocumentProcessor processor;
    
    protected void initialize() {
        ConfigLoader loader = new ConfigLoader();
        if (configFile != null) {
            config = loader.loadFromFile(Paths.get(configFile));
        } else {
            config = loader.loadDefault();
        }
        
        processor = ModuleRegistry.getProcessor(module);
    }
    
    @Override
    public void run() {
        initialize();
        execute();
    }
    
    protected abstract void execute();
}
```

#### **ReadCommand.java**

```java
package org.apache.fesod.cli. commands;

import org.apache. fesod.cli.formatters.FormatterFactory;
import org.apache.fesod.cli.formatters.OutputFormatter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine. Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Read command implementation
 */
@Command(
    name = "read",
    description = "Read spreadsheet data and output in specified format",
    mixinStandardHelpOptions = true
)
public class ReadCommand extends BaseCommand {
    
    @Parameters(
        index = "0",
        description = "Input file path",
        paramLabel = "<file>"
    )
    private String inputFile;
    
    @Option(
        names = {"--format", "-f"},
        description = "Output format: json, csv (default: json)",
        defaultValue = "json"
    )
    private String format;
    
    @Option(
        names = {"--sheet", "-s"},
        description = "Sheet name or index (default: 0)",
        paramLabel = "<name|index>"
    )
    private String sheet;
    
    @Option(
        names = {"--output", "-o"},
        description = "Output file path (default: stdout)",
        paramLabel = "<file>"
    )
    private String outputFile;
    
    @Option(
        names = {"--all"},
        description = "Read all sheets"
    )
    private boolean readAll;
    
    @Override
    protected void execute() {
        Path input = Paths.get(inputFile);
        
        Map<String, Object> options = new HashMap<String, Object>();
        
        if (sheet != null) {
            try {
                int sheetIndex = Integer.parseInt(sheet);
                options.put("sheetIndex", sheetIndex);
            } catch (NumberFormatException e) {
                options.put("sheetName", sheet);
            }
        }
        
        options.put("readAll", readAll);
        
        Map<String, Object> data = processor.read(input, options);
        
        OutputFormatter formatter = FormatterFactory.getFormatter(format);
        String output = formatter.format(data);
        
        if (outputFile != null) {
            formatter.writeToFile(output, Paths.get(outputFile));
            System.out.println("✓ Output written to: " + outputFile);
        } else {
            System.out.println(output);
        }
    }
}
```

#### **ConvertCommand.java**

```java
package org.apache.fesod.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert command implementation
 */
@Command(
    name = "convert",
    description = "Convert spreadsheet between different formats",
    mixinStandardHelpOptions = true
)
public class ConvertCommand extends BaseCommand {
    
    @Parameters(
        index = "0",
        description = "Input file path",
        paramLabel = "<input>"
    )
    private String inputFile;
    
    @Parameters(
        index = "1",
        description = "Output file path",
        paramLabel = "<output>"
    )
    private String outputFile;
    
    @Override
    protected void execute() {
        Path input = Paths.get(inputFile);
        Path output = Paths.get(outputFile);
        
        Map<String, Object> options = new HashMap<String, Object>();
        
        processor.convert(input, output, options);
        
        System.out.println("✓ Conversion completed:  " + inputFile + " → " + outputFile);
    }
}
```

#### **WriteCommand.java**

```java
package org.apache.fesod.cli.commands;

import com.alibaba.fastjson2.JSON;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java. nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Write command implementation
 */
@Command(
    name = "write",
    description = "Write data from JSON/CSV to spreadsheet",
    mixinStandardHelpOptions = true
)
public class WriteCommand extends BaseCommand {
    
    @Parameters(
        index = "0",
        description = "Input data file (JSON/CSV)",
        paramLabel = "<input>"
    )
    private String inputFile;
    
    @Parameters(
        index = "1",
        description = "Output spreadsheet file",
        paramLabel = "<output>"
    )
    private String outputFile;
    
    @Option(
        names = {"--input-format"},
        description = "Input data format: json, csv (default:  json)",
        defaultValue = "json"
    )
    private String inputFormat;
    
    @Option(
        names = {"--sheet-name"},
        description = "Sheet name (default: Sheet1)",
        defaultValue = "Sheet1"
    )
    private String sheetName;
    
    @Override
    protected void execute() {
        try {
            Path input = Paths.get(inputFile);
            Path output = Paths.get(outputFile);
            
            String content = new String(Files.readAllBytes(input), "UTF-8");
            Map<String, Object> data = new HashMap<String, Object>();
            
            if ("json".equalsIgnoreCase(inputFormat)) {
                data.put("data", JSON.parse(content));
            } else {
                throw new UnsupportedOperationException("CSV input format not yet implemented");
            }
            
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("sheetName", sheetName);
            
            processor.write(data, output, options);
            
            System. out.println("✓ Data written to: " + outputFile);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to write data:  " + e.getMessage(), e);
        }
    }
}
```

#### **InfoCommand.java**

```java
package org.apache.fesod.cli.commands;

import org.apache.fesod.cli.formatters.FormatterFactory;
import org.apache.fesod. cli.formatters.OutputFormatter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Info command implementation
 */
@Command(
    name = "info",
    description = "Display spreadsheet file information",
    mixinStandardHelpOptions = true
)
public class InfoCommand extends BaseCommand {
    
    @Parameters(
        index = "0",
        description = "Input file path",
        paramLabel = "<file>"
    )
    private String inputFile;
    
    @Override
    protected void execute() {
        Path input = Paths.get(inputFile);
        
        Map<String, Object> info = processor.getInfo(input);
        
        OutputFormatter formatter = FormatterFactory.getFormatter("json");
        String output = formatter.format(info);
        
        System.out.println(output);
    }
}
```

#### **VersionCommand.java**

```java
package org.apache.fesod.cli.commands;

import picocli.CommandLine.Command;

/**
 * Version command implementation
 */
@Command(
    name = "version",
    description = "Display version information",
    mixinStandardHelpOptions = true
)
public class VersionCommand implements Runnable {
    
    @Override
    public void run() {
        System.out.println("Apache Fesod CLI");
        System.out.println("Version: 2.0.0");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS:  " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println();
        System.out.println("Copyright © 2025 The Apache Software Foundation");
        System.out.println("Licensed under the Apache License 2.0");
    }
}
```

### **3.4 格式化器**

#### **OutputFormatter.java**

```java
package org.apache.fesod.cli.formatters;

import java.nio.file.Path;
import java.util.Map;

/**
 * Output formatter interface
 */
public interface OutputFormatter {
    String format(Map<String, Object> data);
    void writeToFile(String content, Path outputPath);
    String getFormatType();
}
```

#### **FormatterFactory.java**

```java
package org.apache.fesod.cli.formatters;

import org.apache.fesod.cli.exception.CliException;

import java.util.HashMap;
import java.util.Map;

/**
 * Formatter factory
 */
public class FormatterFactory {
    
    private static final Map<String, OutputFormatter> FORMATTERS = new HashMap<String, OutputFormatter>();
    
    static {
        registerFormatter(new JsonFormatter());
        registerFormatter(new CsvFormatter());
    }
    
    public static void registerFormatter(OutputFormatter formatter) {
        FORMATTERS.put(formatter.getFormatType().toLowerCase(), formatter);
    }
    
    public static OutputFormatter getFormatter(String formatType) {
        OutputFormatter formatter = FORMATTERS.get(formatType.toLowerCase());
        if (formatter == null) {
            throw new CliException("Unsupported format:  " + formatType);
        }
        return formatter;
    }
}
```

#### **JsonFormatter.java**

```java
package org.apache.fesod.cli.formatters;

import com.alibaba.fastjson2.JSON;
import com. alibaba.fastjson2.JSONWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * JSON formatter implementation
 */
public class JsonFormatter implements OutputFormatter {
    
    @Override
    public String format(Map<String, Object> data) {
        return JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
    }
    
    @Override
    public void writeToFile(String content, Path outputPath) {
        try {
            Files. write(outputPath, content.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON to file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getFormatType() {
        return "json";
    }
}
```

#### **CsvFormatter.java**

```java
package org.apache.fesod.cli.formatters;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java. nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * CSV formatter implementation
 */
public class CsvFormatter implements OutputFormatter {
    
    @Override
    public String format(Map<String, Object> data) {
        try {
            StringWriter out = new StringWriter();
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
            
            Object dataObj = data.get("data");
            
            if (dataObj instanceof JSONArray) {
                JSONArray array = (JSONArray) dataObj;
                
                for (int i = 0; i < array.size(); i++) {
                    Object item = array.get(i);
                    
                    if (item instanceof JSONObject) {
                        JSONObject obj = (JSONObject) item;
                        printer.printRecord(obj.values());
                    }
                }
            }
            
            printer.close();
            return out. toString();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to format as CSV: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void writeToFile(String content, Path outputPath) {
        try {
            Files.write(outputPath, content.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV to file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getFormatType() {
        return "csv";
    }
}
```

### **3.5 配置管理**

#### **CliConfig.java**

```java
package org.apache.fesod. cli.config;

/**
 * CLI configuration
 */
public class CliConfig {
    
    private DefaultsConfig defaults = new DefaultsConfig();
    private ReadConfig read = new ReadConfig();
    private WriteConfig write = new WriteConfig();
    
    public DefaultsConfig getDefaults() {
        return defaults;
    }
    
    public void setDefaults(DefaultsConfig defaults) {
        this.defaults = defaults;
    }
    
    public ReadConfig getRead() {
        return read;
    }
    
    public void setRead(ReadConfig read) {
        this.read = read;
    }
    
    public WriteConfig getWrite() {
        return write;
    }
    
    public void setWrite(WriteConfig write) {
        this.write = write;
    }
    
    public static class DefaultsConfig {
        private String outputFormat = "json";
        private String encoding = "UTF-8";
        
        public String getOutputFormat() {
            return outputFormat;
        }
        
        public void setOutputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
        }
        
        public String getEncoding() {
            return encoding;
        }
        
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }
    
    public static class ReadConfig {
        private boolean autoTrim = true;
        private boolean ignoreEmptyRows = true;
        
        public boolean isAutoTrim() {
            return autoTrim;
        }
        
        public void setAutoTrim(boolean autoTrim) {
            this. autoTrim = autoTrim;
        }
        
        public boolean isIgnoreEmptyRows() {
            return ignoreEmptyRows;
        }
        
        public void setIgnoreEmptyRows(boolean ignoreEmptyRows) {
            this.ignoreEmptyRows = ignoreEmptyRows;
        }
    }
    
    public static class WriteConfig {
        private boolean autoCreateDirectories = true;
        
        public boolean isAutoCreateDirectories() {
            return autoCreateDirectories;
        }
        
        public void setAutoCreateDirectories(boolean autoCreateDirectories) {
            this.autoCreateDirectories = autoCreateDirectories;
        }
    }
}
```

#### **ConfigLoader.java**

```java
package org.apache.fesod.cli.config;

import org.yaml.snakeyaml. Yaml;

import java.io. InputStream;
import java.nio. file.Files;
import java. nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration loader
 */
public class ConfigLoader {
    
    private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") + "/.fesod/config.yaml";
    
    public CliConfig loadDefault() {
        Path defaultPath = Paths.get(DEFAULT_CONFIG_PATH);
        
        if (Files.exists(defaultPath)) {
            return loadFromFile(defaultPath);
        }
        
        return createDefaultConfig();
    }
    
    public CliConfig loadFromFile(Path configPath) {
        try {
            InputStream is = Files.newInputStream(configPath);
            Yaml yaml = new Yaml();
            CliConfig config = yaml.loadAs(is, CliConfig.class);
            is.close();
            return config != null ? config : createDefaultConfig();
        } catch (Exception e) {
            System.err.println("Warning: Failed to load config from " + configPath + ", using defaults");
            return createDefaultConfig();
        }
    }
    
    private CliConfig createDefaultConfig() {
        return new CliConfig();
    }
}
```

### **3.6 异常处理**

#### **CliException.java**

```java
package org.apache.fesod.cli.exception;

/**
 * Base CLI exception
 */
public class CliException extends RuntimeException {
    
    private final int exitCode;
    
    public CliException(String message) {
        this(message, 1);
    }
    
    public CliException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }
    
    public CliException(String message, Throwable cause) {
        this(message, cause, 1);
    }
    
    public CliException(String message, Throwable cause, int exitCode) {
        super(message, cause);
        this.exitCode = exitCode;
    }
    
    public int getExitCode() {
        return exitCode;
    }
}
```

#### **FileProcessException.java**

```java
package org.apache.fesod. cli.exception;

/**
 * File processing exception
 */
public class FileProcessException extends CliException {
    
    public FileProcessException(String message) {
        super(message, 2);
    }
    
    public FileProcessException(String message, Throwable cause) {
        super(message, cause, 2);
    }
}
```

#### **ConfigurationException.java**

```java
package org.apache.fesod.cli.exception;

/**
 * Configuration exception
 */
public class ConfigurationException extends CliException {
    
    public ConfigurationException(String message) {
        super(message, 3);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause, 3);
    }
}
```

---

## **4. 启动脚本**

### **4.1 fesod-cli (Unix/Linux/macOS)**

```bash
#!/usr/bin/env bash

##############################################################################
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.   See the License for the
# specific language governing permissions and limitations
# under the License.
##############################################################################

##############################################################################
# Apache Fesod CLI Launcher Script
# Supports:  JDK 8+
# Platforms: Linux, macOS, Unix
##############################################################################

set -e

# 脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Fesod CLI 主目录
FESOD_HOME="${FESOD_HOME:-$(dirname "$SCRIPT_DIR")}"

# JAR 文件
FESOD_JAR="$FESOD_HOME/lib/fesod-cli-2.0.0.jar"

# 检查 JAR 是否存在
if [ ! -f "$FESOD_JAR" ]; then
    echo "Error: Cannot find fesod-cli JAR at $FESOD_JAR"
    exit 1
fi

# 查找 Java
find_java() {
    # 1. 检查 JAVA_HOME
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
        echo "$JAVA_HOME/bin/java"
        return 0
    fi
    
    # 2. 检查 PATH
    if command -v java &> /dev/null; then
        echo "java"
        return 0
    fi
    
    # 3. macOS 特定查找
    if [ "$(uname)" = "Darwin" ]; then
        if [ -x "/usr/libexec/java_home" ]; then
            JAVA_HOME_CANDIDATE=$(/usr/libexec/java_home 2>/dev/null)
            if [ -n "$JAVA_HOME_CANDIDATE" ] && [ -x "$JAVA_HOME_CANDIDATE/bin/java" ]; then
                echo "$JAVA_HOME_CANDIDATE/bin/java"
                return 0
            fi
        fi
    fi
    
    # 4. Linux 常见路径
    for candidate in \
        /usr/lib/jvm/java-8-openjdk-amd64/bin/java \
        /usr/lib/jvm/java-11-openjdk-amd64/bin/java \
        /usr/lib/jvm/default-java/bin/java
    do
        if [ -x "$candidate" ]; then
            echo "$candidate"
            return 0
        fi
    done
    
    return 1
}

JAVA_CMD=$(find_java)

# 验证 Java 可用性
if [ -z "$JAVA_CMD" ]; then
    echo "Error: Java is not installed or not in PATH"
    echo ""
    echo "Please install Java 8 or higher:"
    echo "  - Ubuntu/Debian: sudo apt-get install openjdk-8-jdk"
    echo "  - CentOS/RHEL:    sudo yum install java-1.8.0-openjdk"
    echo "  - macOS:         brew install openjdk@8"
    echo "  - Or download:    https://adoptium.net/"
    echo ""
    echo "Or set JAVA_HOME environment variable:"
    echo "  export JAVA_HOME=/path/to/jdk"
    exit 1
fi

# 检查 Java 版本
check_java_version() {
    local java_cmd=$1
    local version_output=$("$java_cmd" -version 2>&1)
    local version=$(echo "$version_output" | head -n 1 | awk -F '"' '{print $2}')
    
    # 提取主版本号
    local major_version=$(echo "$version" | awk -F.  '{print $1}')
    if [ "$major_version" -eq 1 ]; then
        major_version=$(echo "$version" | awk -F. '{print $2}')
    fi
    
    if [ "$major_version" -lt 8 ]; then
        echo "Error: Java 8 or higher is required"
        echo "Current Java version: $version"
        echo "Java command: $java_cmd"
        exit 1
    fi
}

check_java_version "$JAVA_CMD"

# JVM 参数
JAVA_OPTS="${FESOD_JAVA_OPTS: --Xms128m -Xmx1g}"

# 日志配置
if [ -f "$FESOD_HOME/conf/logback.xml" ]; then
    JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=$FESOD_HOME/conf/logback.xml"
fi

# 字符编码
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# 执行命令
exec "$JAVA_CMD" $JAVA_OPTS -jar "$FESOD_JAR" "$@"
```

### **4.2 fesod-cli. bat (Windows)**

```batch
@echo off
setlocal enabledelayedexpansion

REM ============================================================================
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.
REM ============================================================================

REM ============================================================================
REM Apache Fesod CLI Launcher Script for Windows
REM Supports: JDK 8+
REM ============================================================================

REM 脚本所在目录
set SCRIPT_DIR=%~dp0
set FESOD_HOME=%SCRIPT_DIR%.. 

REM JAR 文件
set FESOD_JAR=%FESOD_HOME%\lib\fesod-cli-2.0.0.jar

REM 检查 JAR 是否存在
if not exist "%FESOD_JAR%" (
    echo Error:  Cannot find fesod-cli JAR at %FESOD_JAR%
    exit /b 1
)

REM 查找 Java
set JAVA_CMD=

REM 1. 检查 JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_CMD=%JAVA_HOME%\bin\java.exe
        goto :java_found
    )
)

REM 2. 检查 PATH
where java >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set JAVA_CMD=java
    goto :java_found
)

REM 3. 检查常见安装路径
for %%d in (
    "C:\Program Files\Java\jdk-8"
    "C:\Program Files\Java\jdk1.8.0_*"
    "C:\Program Files\Java\jdk-11"
    "C:\Program Files\OpenJDK\jdk-8"
    "C:\Program Files\Eclipse Adoptium\jdk-8*"
) do (
    if exist "%%~d\bin\java.exe" (
        set JAVA_CMD=%%~d\bin\java.exe
        goto :java_found
    )
)

:java_not_found
echo Error: Java is not installed or not in PATH
echo. 
echo Please install Java 8 or higher:
echo   - Download from: https://adoptium.net/
echo   - Or install via Chocolatey: choco install openjdk8
echo.
echo Or set JAVA_HOME environment variable:
echo   set JAVA_HOME=C:\Path\To\JDK
exit /b 1

:java_found

REM 检查 Java 版本
for /f "tokens=3" %%g in ('"%JAVA_CMD%" -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=%JAVA_VERSION:"=%

REM 提取主版本号
for /f "delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a
if "%JAVA_MAJOR%" equ "1" (
    for /f "tokens=2 delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a
)

if %JAVA_MAJOR% lss 8 (
    echo Error: Java 8 or higher is required
    echo Current Java version: %JAVA_VERSION%
    echo Java command: %JAVA_CMD%
    exit /b 1
)

REM JVM 参数
if not defined FESOD_JAVA_OPTS (
    set JAVA_OPTS=-Xms128m -Xmx1g
) else (
    set JAVA_OPTS=%FESOD_JAVA_OPTS%
)

REM 日志配置
if exist "%FESOD_HOME%\conf\logback.xml" (
    set JAVA_OPTS=%JAVA_OPTS% -Dlogback.configurationFile=%FESOD_HOME%\conf\logback.xml
)

REM 字符编码
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

REM 执行命令
"%JAVA_CMD%" %JAVA_OPTS% -jar "%FESOD_JAR%" %*
exit /b %ERRORLEVEL%
```

---

## **5. Maven 配置**

### **5.1 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.apache.fesod</groupId>
        <artifactId>fesod-parent</artifactId>
        <version>${revision}</version>
    </parent>

    <artifactId>fesod-cli</artifactId>
    <packaging>jar</packaging>
    <name>Apache Fesod CLI Tool</name>
    <description>Command-line interface for Apache Fesod spreadsheet processing</description>

    <properties>
        <!-- JDK 8 兼容性 -->
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler. target>1.8</maven. compiler.target>
        
        <picocli.version>4.7.5</picocli.version>
        <snakeyaml.version>2.2</snakeyaml.version>
        <mainClass>org.apache.fesod.cli.FesodCli</mainClass>
    </properties>

    <dependencies>
        <!-- Fesod Core Dependencies -->
        <dependency>
            <groupId>org.apache. fesod</groupId>
            <artifactId>fesod-sheet</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Picocli for CLI -->
        <dependency>
            <groupId>info.picocli</groupId>
            <artifactId>picocli</artifactId>
            <version>${picocli. version}</version>
        </dependency>

        <!-- JSON Processing -->
        <dependency>
            <groupId>com.alibaba. fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
        </dependency>

        <!-- YAML Configuration -->
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>${snakeyaml.version}</version>
        </dependency>

        <!-- CSV Support -->
        <dependency>
            <groupId>org.apache. commons</groupId>
            <artifactId>commons-csv</artifactId>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>info.picocli</groupId>
                            <artifactId>picocli-codegen</artifactId>
                            <version>${picocli.version}</version>
                        </path>
                    </annotationProcessorPaths>
                    <compilerArgs>
                        <arg>-Aproject=${project.groupId}/${project. artifactId}</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <!-- Maven Shade Plugin - FAT JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <finalName>fesod-cli-${project.version}</finalName>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>${mainClass}</mainClass>
                                    <manifestEntries>
                                        <Implementation-Title>${project.name}</Implementation-Title>
                                        <Implementation-Version>${project.version}</Implementation-Version>
                                        <Implementation-Vendor>Apache Software Foundation</Implementation-Vendor>
                                        <Multi-Release>true</Multi-Release>
                                    </manifestEntries>
                                </transformer>
                                <transformer implementation="org.apache. maven.plugins.shade.resource. ServicesResourceTransformer"/>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ApacheLicenseResourceTransformer"/>
                                <transformer implementation="org.apache. maven.plugins.shade.resource. ApacheNoticeResourceTransformer"/>
                            </transformers>
                            <filters>
                                <filter>
                                    <artifact>*: *</artifact>
                                    <excludes>
                                        <exclude>META-INF/*. SF</exclude>
                                        <exclude>META-INF/*. DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                        <exclude>META-INF/MANIFEST.MF</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Maven Assembly Plugin - 分发包 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.6.0</version>
                <configuration>
                    <descriptors>
                        <descriptor>src/assembly/bin.xml</descriptor>
                    </descriptors>
                    <tarLongFileMode>posix</tarLongFileMode>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Maven Surefire Plugin - 测试 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package org.apache.fesod.sheet.benchmark.operations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.fesod.sheet.EasyExcel;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.benchmark.core.AbstractBenchmark;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.apache.fesod.sheet.benchmark.utils.BenchmarkFileUtil;
import org.apache.fesod.sheet.benchmark.utils.DataGenerator;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.fill.FillWrapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Comprehensive benchmarks for FastExcel fill operations
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Fork(
        value = 1,
        jvmArgs = {"-Xms2g", "-Xmx2g"})
public class FillBenchmark extends AbstractBenchmark {

    // Template files for different scenarios
    private String simpleTemplateFile;
    private String complexTemplateFile;
    private String horizontalTemplateFile;
    private String verticalTemplateFile;
    private String multiListTemplateFile;

    // Test data for different sizes
    private List<BenchmarkData> smallData;
    private List<BenchmarkData> mediumData;
    private List<BenchmarkData> largeData;

    // Single objects for simple fills
    private BenchmarkData singleData;
    private Map<String, Object> simpleMap;
    private Map<String, Object> complexMap;

    // Fill configurations
    private FillConfig verticalConfig;
    private FillConfig horizontalConfig;
    private FillConfig forceNewRowConfig;

    // Data generator
    private DataGenerator dataGenerator;

    @Override
    protected void setupBenchmark() throws Exception {
        logger.info("Setting up fill benchmark templates and data...");

        dataGenerator = new DataGenerator();

        // Generate test data
        generateTestData();

        // Create template files
        createTemplateFiles();

        // Setup fill configurations
        setupFillConfigurations();

        logger.info("Fill benchmark setup completed");
    }

    @Override
    protected void tearDownBenchmark() throws Exception {
        // Clean up temporary files
        BenchmarkFileUtil.cleanupTempFiles();
        logger.info("Fill benchmark cleanup completed");
    }

    private void generateTestData() {
        // Generate data for different sizes
        smallData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.SMALL);
        mediumData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.MEDIUM);
        largeData = dataGenerator.generateData(BenchmarkConfiguration.DatasetSize.LARGE);

        // Single object for simple fills
        singleData = smallData.get(0);

        // Simple map for template variable filling
        simpleMap = new HashMap<>();
        simpleMap.put("title", "Benchmark Report");
        simpleMap.put("date", LocalDate.now().toString());
        simpleMap.put("dateTime", LocalDateTime.now().toString());
        simpleMap.put("total", 12345.67);
        simpleMap.put("count", 1000);
        simpleMap.put("author", "FastExcel Benchmark");

        // Complex map with nested data
        complexMap = new HashMap<>();
        complexMap.put("reportTitle", "Performance Analysis Report");
        complexMap.put("generatedDate", LocalDate.now());
        complexMap.put("generatedTime", LocalDateTime.now());
        complexMap.put("totalRecords", largeData.size());
        complexMap.put("avgProcessingTime", 123.45);
        complexMap.put("maxMemoryUsage", "256MB");
        complexMap.put(
                "summary", "This is a comprehensive performance analysis report generated by FastExcel benchmarks.");

        logger.debug(
                "Generated test data - Small: {}, Medium: {}, Large: {} rows",
                smallData.size(),
                mediumData.size(),
                largeData.size());
    }

    private void createTemplateFiles() {
        // Create simple template with basic placeholders
        simpleTemplateFile = createSimpleTemplate();

        // Create complex template with multiple data types
        complexTemplateFile = createComplexTemplate();

        // Create horizontal fill template
        horizontalTemplateFile = createHorizontalTemplate();

        // Create vertical fill template
        verticalTemplateFile = createVerticalTemplate();

        // Create multi-list template
        multiListTemplateFile = createMultiListTemplate();
    }

    private String createSimpleTemplate() {
        String templatePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, "SimpleTemplate");

        // Create a simple template with placeholder rows
        Map<String, Object> row1 = new HashMap<>();
        row1.put("name", "Simple Fill Test");
        row1.put("date", "2023-01-01");
        row1.put("version", "1.0");

        Map<String, Object> row2 = new HashMap<>();
        row2.put("description", "This is a simple fill test");
        row2.put("author", "Test Author");
        row2.put("status", "Active");

        List<Map<String, Object>> templateData = new ArrayList<>();
        templateData.add(row1);
        templateData.add(row2);

        try {
            // Write template structure
            EasyExcel.write(templatePath).sheet("Template").doWrite(templateData);

            logger.debug("Created simple template: {}", templatePath);
            return templatePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create simple template", e);
        }
    }

    private String createComplexTemplate() {
        String templatePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "ComplexTemplate");

        // Create a more complex template with data list placeholders
        List<Map<String, Object>> templateData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("A", "{reportTitle}");
        row1.put("B", "");
        row1.put("C", "");
        templateData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("A", "Generated on: {generatedDate}");
        row2.put("B", "Time: {generatedTime}");
        row2.put("C", "");
        templateData.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("A", "Total Records: {totalRecords}");
        row3.put("B", "Avg Time: {avgProcessingTime}ms");
        row3.put("C", "Max Memory: {maxMemoryUsage}");
        templateData.add(row3);

        Map<String, Object> row4 = new HashMap<>();
        row4.put("A", "");
        row4.put("B", "");
        row4.put("C", "");
        templateData.add(row4);

        Map<String, Object> row5 = new HashMap<>();
        row5.put("A", "Summary: {summary}");
        row5.put("B", "");
        row5.put("C", "");
        templateData.add(row5);

        Map<String, Object> row6 = new HashMap<>();
        row6.put("A", "");
        row6.put("B", "");
        row6.put("C", "");
        templateData.add(row6);

        Map<String, Object> row7 = new HashMap<>();
        row7.put("A", "ID");
        row7.put("B", "String Data");
        row7.put("C", "Value");
        templateData.add(row7);

        Map<String, Object> row8 = new HashMap<>();
        row8.put("A", "{.id}");
        row8.put("B", "{.stringData}");
        row8.put("C", "{.intValue}");
        templateData.add(row8);

        try {
            EasyExcel.write(templatePath).sheet("ComplexTemplate").doWrite(templateData);

            logger.debug("Created complex template: {}", templatePath);
            return templatePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create complex template", e);
        }
    }

    private String createHorizontalTemplate() {
        String templatePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.MEDIUM,
                "HorizontalTemplate");

        // Create horizontal fill template
        Map<String, Object> row1 = new HashMap<>();
        row1.put("A", "Horizontal Fill Demo");
        row1.put("B", "");
        row1.put("C", "");

        Map<String, Object> row2 = new HashMap<>();
        row2.put("A", "{.id}");
        row2.put("B", "{.stringData}");
        row2.put("C", "{.intValue}");

        List<Map<String, Object>> templateData = new ArrayList<>();
        templateData.add(row1);
        templateData.add(row2);

        try {
            EasyExcel.write(templatePath).sheet("HorizontalTemplate").doWrite(templateData);

            logger.debug("Created horizontal template: {}", templatePath);
            return templatePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create horizontal template", e);
        }
    }

    private String createVerticalTemplate() {
        String templatePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "VerticalTemplate");

        // Create vertical fill template
        Map<String, Object> row1 = new HashMap<>();
        row1.put("A", "Dynamic Fill Test");
        row1.put("B", "Status");
        row1.put("C", "Priority");

        Map<String, Object> row2 = new HashMap<>();
        row2.put("A", "{.id}");
        row2.put("B", "{.status}");
        row2.put("C", "{.priority}");

        List<Map<String, Object>> templateData = new ArrayList<>();
        templateData.add(row1);
        templateData.add(row2);

        try {
            EasyExcel.write(templatePath).sheet("VerticalTemplate").doWrite(templateData);

            logger.debug("Created vertical template: {}", templatePath);
            return templatePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create vertical template", e);
        }
    }

    private String createMultiListTemplate() {
        String templatePath = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, "MultiListTemplate");

        // Create multi-list template
        Map<String, Object> row1 = new HashMap<>();
        row1.put("Report", "Performance Report");
        row1.put("Date", "{date}");
        row1.put("Version", "{version}");

        Map<String, Object> row2 = new HashMap<>();
        row2.put("Metric", "Value");
        row2.put("Status", "Threshold");
        row2.put("Notes", "Comments");

        List<Map<String, Object>> templateData = new ArrayList<>();
        templateData.add(row1);
        templateData.add(row2);

        try {
            EasyExcel.write(templatePath).sheet("MultiListTemplate").doWrite(templateData);

            logger.debug("Created multi-list template: {}", templatePath);
            return templatePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create multi-list template", e);
        }
    }

    private void setupFillConfigurations() {
        verticalConfig =
                FillConfig.builder().direction(WriteDirectionEnum.VERTICAL).build();

        horizontalConfig =
                FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();

        forceNewRowConfig = FillConfig.builder()
                .direction(WriteDirectionEnum.VERTICAL)
                .forceNewRow(true)
                .build();
    }

    // Simple fill benchmarks
    @Benchmark
    public void fillSimpleMap(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, "FillSimpleMap");

        EasyExcel.write(outputFile).withTemplate(simpleTemplateFile).sheet().doFill(simpleMap);

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void fillSingleObject(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, "FillSingleObject");

        EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(simpleTemplateFile)
                .sheet()
                .doFill(singleData);

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // List fill benchmarks - different sizes
    @Benchmark
    public void fillSmallList(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.SMALL, "FillSmallList");

        EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(complexTemplateFile)
                .sheet()
                .doFill(smallData);

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void fillMediumList(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "FillMediumList");

        EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(complexTemplateFile)
                .sheet()
                .doFill(mediumData);

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void fillLargeList(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, "FillLargeList");

        EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(complexTemplateFile)
                .sheet()
                .doFill(largeData);

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // Directional fill benchmarks
    @Benchmark
    public void fillHorizontal(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "FillHorizontal");

        try (ExcelWriter excelWriter = EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(horizontalTemplateFile)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(mediumData, horizontalConfig, writeSheet);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void fillVertical(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "FillVertical");

        try (ExcelWriter excelWriter = EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(verticalTemplateFile)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(mediumData, verticalConfig, writeSheet);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    @Benchmark
    public void fillForceNewRow(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "FillForceNewRow");

        try (ExcelWriter excelWriter = EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(verticalTemplateFile)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(mediumData, forceNewRowConfig, writeSheet);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // Multi-list fill benchmarks
    @Benchmark
    public void fillMultipleLists(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.MEDIUM, "FillMultipleLists");

        try (ExcelWriter excelWriter =
                EasyExcel.write(outputFile).withTemplate(multiListTemplateFile).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            // Fill multiple lists with different prefixes
            excelWriter.fill(new FillWrapper("data1", smallData), writeSheet);
            excelWriter.fill(new FillWrapper("data2", mediumData), writeSheet);

            // Fill summary data
            Map<String, Object> summary = new HashMap<>();
            summary.put("total", smallData.size() + mediumData.size());
            summary.put("date", LocalDate.now().toString());
            excelWriter.fill(summary, writeSheet);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // Complex fill scenarios
    @Benchmark
    public void fillComplexMixed(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, "FillComplexMixed");

        try (ExcelWriter excelWriter =
                EasyExcel.write(outputFile).withTemplate(complexTemplateFile).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            // Fill header variables
            excelWriter.fill(complexMap, writeSheet);

            // Fill data list
            excelWriter.fill(largeData, forceNewRowConfig, writeSheet);
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // Streaming fill benchmark
    @Benchmark
    public void fillStreaming(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX, BenchmarkConfiguration.DatasetSize.LARGE, "FillStreaming");

        try (ExcelWriter excelWriter = EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(complexTemplateFile)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            // Fill in batches to test streaming behavior
            int batchSize = 1000;
            for (int i = 0; i < largeData.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, largeData.size());
                List<BenchmarkData> batch = largeData.subList(i, endIndex);
                excelWriter.fill(batch, verticalConfig, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }

    // Memory efficient fill benchmark
    @Benchmark
    public void fillMemoryEfficient(Blackhole blackhole) throws Exception {
        String outputFile = BenchmarkFileUtil.getTempFilePath(
                BenchmarkConfiguration.FileFormat.XLSX,
                BenchmarkConfiguration.DatasetSize.LARGE,
                "FillMemoryEfficient");

        try (ExcelWriter excelWriter = EasyExcel.write(outputFile, BenchmarkData.class)
                .withTemplate(complexTemplateFile)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            // Generate and fill data on-the-fly to test memory efficiency
            DataGenerator.DataStream dataStream =
                    dataGenerator.generateStreamingData(BenchmarkConfiguration.DatasetSize.LARGE.getRowCount());

            List<BenchmarkData> batch = new ArrayList<>();
            int batchSize = 500;

            for (BenchmarkData data : dataStream) {
                batch.add(data);

                if (batch.size() >= batchSize) {
                    excelWriter.fill(batch, verticalConfig, writeSheet);
                    batch.clear();
                }
            }

            // Fill remaining data
            if (!batch.isEmpty()) {
                excelWriter.fill(batch, verticalConfig, writeSheet);
            }
        }

        long fileSize = BenchmarkFileUtil.getFileSize(outputFile);
        consumeData(fileSize, blackhole);
    }
}

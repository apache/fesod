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

package org.apache.fesod.excel.demo.read;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.annotation.format.DateTimeFormat;
import org.apache.fesod.excel.annotation.format.NumberFormat;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.converters.DefaultConverterLoader;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.fesod.excel.read.listener.PageReadListener;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.csv.CsvReadWorkbookHolder;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Common approaches for reading Excel files
 *
 *
 */
@Slf4j
public class ReadTest {

    /**
     * Simplest way to read
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure. Refer to {@link DemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link DemoDataListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void simpleRead() {
        // Approach 1: JDK8+, no need to create a separate DemoDataListener
        // since: 3.0.0-beta1
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read the data, then read the first sheet. The file stream will be automatically closed.
        // By default, it reads 100 rows at a time. You can process the data directly.
        // The number of rows to read can be set in the constructor of `PageReadListener`.
        FastExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
                    for (DemoData demoData : dataList) {
                        log.info("Reading a row of data: {}", JSON.toJSONString(demoData));
                    }
                }))
                .numRows(2)
                .sheet()
                .doRead();

        // Approach 2:
        // Anonymous inner class, no need to create a separate DemoDataListener
        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read the data, then read the first sheet. The file stream will be automatically closed.
        FastExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {
                    /**
                     * Batch size for caching data
                     */
                    public static final int BATCH_COUNT = 100;
                    /**
                     * Temporary storage
                     */
                    private List<DemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

                    @Override
                    public void invoke(DemoData data, AnalysisContext context) {
                        cachedDataList.add(data);
                        if (cachedDataList.size() >= BATCH_COUNT) {
                            saveData();
                            // Clear the list after saving
                            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        saveData();
                    }

                    /**
                     * Simulate saving data to the database
                     */
                    private void saveData() {
                        log.info("Saving {} rows of data to the database!", cachedDataList.size());
                        log.info("Data saved successfully!");
                    }
                })
                .sheet()
                .doRead();

        // Important note: DemoDataListener should not be managed by Spring. It needs to be instantiated every time you
        // read an Excel file.
        // Approach 3:
        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read the data, then read the first sheet. The file stream will be automatically closed.
        FastExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();

        // Approach 4
        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // One reader per file
        try (ExcelReader excelReader =
                FastExcel.read(fileName, DemoData.class, new DemoDataListener()).build()) {
            // Build a sheet. You can specify the name or index.
            ReadSheet readSheet = FastExcel.readSheet(0).build();
            readSheet.setNumRows(2);
            // Read a single sheet
            excelReader.read(readSheet);
        }
    }

    @Test
    public void genericHeaderTypeRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "generic-demo.xlsx";
        // Simulate obtaining the Excel header's Class<?> object through any possible means
        Class<?> excelHeaderClass = DemoDataAnother.class;
        FastExcel.read(fileName, excelHeaderClass, GenericHeaderTypeDataListener.build(excelHeaderClass))
                .sheet()
                .doRead();
    }

    /**
     * Specify column indexes or names
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure and use the {@link ExcelProperty} annotation. Refer to {@link IndexOrNameData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link IndexOrNameDataListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void indexOrNameRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // By default, read the first sheet
        FastExcel.read(fileName, IndexOrNameData.class, new IndexOrNameDataListener())
                .numRows(1)
                .sheet()
                .doRead();
    }

    /**
     * Read multiple or all sheets. Note that a sheet cannot be read multiple times; multiple reads require re-reading the file.
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure. Refer to {@link DemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link DemoDataListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void repeatedRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Read all sheets
        // Note: The `doAfterAllAnalysed` method of DemoDataListener will be called once after each sheet is read.
        // All sheets will write to the same DemoDataListener.
        FastExcel.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();

        // Read some sheets
        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // Method 1
        try (ExcelReader excelReader = FastExcel.read(fileName).build()) {
            // For simplicity, the same head and Listener are registered here.
            // In actual use, different Listeners must be used.
            ReadSheet readSheet1 = FastExcel.readSheet(0)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();
            ReadSheet readSheet2 = FastExcel.readSheet(1)
                    .head(DemoData.class)
                    .registerReadListener(new DemoDataListener())
                    .build();
            // Note: All sheets (sheet1 and sheet2) must be passed together.
            // Otherwise, for Excel 2003 files, the same sheet may be read multiple times, wasting performance.
            excelReader.read(readSheet1, readSheet2);
        }
    }

    /**
     * Date, number, or custom format conversion
     * <p>
     * Default converter: {@link DefaultConverterLoader#loadDefaultReadConverter()}
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure. Refer to {@link ConverterData}.
     * Annotations such as {@link DateTimeFormat}, {@link NumberFormat}, or custom annotations can be used.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link ConverterDataListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void converterRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, ConverterData.class, new ConverterDataListener())
                // Note: We can also register a custom converter using `registerConverter`.
                // However, this converter will be global, and all fields with Java type `String` and Excel type
                // `String` will use this converter.
                // If you want to use it for a single field, specify the converter using `@ExcelProperty`.
                // .registerConverter(new CustomStringStringConverter())
                // Read the sheet
                .sheet()
                .doRead();
    }

    /**
     * Multi-row header
     *
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure. Refer to {@link DemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link DemoDataListener}.
     * <p>
     * 3. Set the `headRowNumber` parameter, then read. Note that if `headRowNumber` is not specified,
     * the number of rows will be determined by the number of headers in the `@ExcelProperty#value()` of the class you provide.
     * If no class is provided, the default is 1. Of course, if you specify `headRowNumber`, it will be used regardless of whether a class is provided.
     */
    @Test
    public void complexHeaderRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, DemoData.class, new DemoDataListener())
                .sheet()
                // Set to 1 here because the header is one row. For multi-row headers, set to other values.
                // You can also omit this, as the default behavior will parse based on DemoData, which does not specify
                // a header, meaning the default is 1 row.
                .headRowNumber(1)
                .doRead();
    }

    /**
     * Method to read Excel files with headers that support compatibility, such as case sensitivity or simultaneous
     * support for Chinese and English headers.
     *
     * <p>
     * 1. Create an entity object corresponding to the Excel data structure. Refer to {@link DemoCompatibleHeaderData}
     * for implementation details.
     * </p>
     *
     * <p>
     * 2. Since FastExcel reads the Excel file row by row by default, you need to create a listener that handles each
     * row's data accordingly. Refer to {@link DemoCompatibleHeaderDataListener} for implementation details. In this
     * listener, you should override the `invokeHead` method to transform the uploaded headers as needed.
     * </p>
     *
     * <p>
     * 3. Simply proceed to read the file.
     * </p>
     */
    @Test
    public void compatibleHeaderRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class used for reading and choose to read the first sheet.
        FastExcel.read(fileName, DemoCompatibleHeaderData.class, new DemoCompatibleHeaderDataListener())
                .sheet()
                .doRead();
    }

    /**
     * Read header data
     *
     * <p>
     * 1. Create an entity object corresponding to the Excel data structure. Refer to {@link DemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row, you need to create a callback listener for each row. Refer to {@link DemoHeadDataListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void headerRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, DemoData.class, new DemoHeadDataListener())
                .sheet()
                .doRead();
    }

    /**
     * Additional information (comments, hyperlinks, merged cell information)
     * <p>
     * Since it is stream-based reading, it is not possible to directly read additional information when reading cell data.
     * Therefore, only notifications of which cells contain additional information can be provided at the end.
     *
     * <p>
     * 1. Create an entity object corresponding to the Excel data structure. Refer to {@link DemoExtraData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row by default, you need to create a callback listener for each row. Refer to {@link DemoExtraListener}.
     * <p>
     * 3. Directly read the file.
     *
     * @since 2.2.0-beat1
     */
    @Test
    public void extraRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "extra.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, DemoExtraData.class, new DemoExtraListener())
                // Read comments (default is not to read)
                .extraRead(CellExtraTypeEnum.COMMENT)
                // Read hyperlinks (default is not to read)
                .extraRead(CellExtraTypeEnum.HYPERLINK)
                // Read merged cell information (default is not to read)
                .extraRead(CellExtraTypeEnum.MERGE)
                .sheet()
                .doRead();
    }

    /**
     * Read formulas and cell types
     *
     * <p>
     * 1. Create an entity object corresponding to the Excel data structure. Refer to {@link CellDataReadDemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row by default, you need to create a callback listener for each row. Refer to {@link CellDataDemoHeadDataListener}.
     * <p>
     * 3. Directly read the file.
     *
     * @since 2.2.0-beat1
     */
    @Test
    public void cellDataRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "cellDataDemo.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, CellDataReadDemoData.class, new CellDataDemoHeadDataListener())
                .sheet()
                .doRead();
    }

    /**
     * Exception handling for data conversion, etc.
     *
     * <p>
     * 1. Create an entity object corresponding to the Excel data structure. Refer to {@link ExceptionDemoData}.
     * <p>
     * 2. Since FastExcel reads Excel files row by row by default, you need to create a callback listener for each row. Refer to {@link DemoExceptionListener}.
     * <p>
     * 3. Directly read the file.
     */
    @Test
    public void exceptionRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read, then read the first sheet
        FastExcel.read(fileName, ExceptionDemoData.class, new DemoExceptionListener())
                .sheet()
                .doRead();
    }

    /**
     * Synchronous return is not recommended, as it will store data in memory if the data volume is large.
     */
    @Test
    public void synchronousRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Specify the class to read, then read the first sheet. Synchronous reading will automatically finish.
        List<DemoData> list =
                FastExcel.read(fileName).head(DemoData.class).sheet().doReadSync();
        for (DemoData data : list) {
            log.info("Read data:{}", JSON.toJSONString(data));
        }

        // Alternatively, you can read without specifying a class, returning a list, then read the first sheet.
        // Synchronous reading will automatically finish.
        List<Map<Integer, String>> listMap = FastExcel.read(fileName).sheet().doReadSync();
        for (Map<Integer, String> data : listMap) {
            // Return key-value pairs for each data item, representing the column index and its value.
            log.info("Read data:{}", JSON.toJSONString(data));
        }
    }

    /**
     * Reading without creating objects
     */
    @Test
    public void noModelRead() {
        String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        // Simply read the first sheet. Synchronous reading will automatically finish.
        FastExcel.read(fileName, new NoModelDataListener()).sheet().doRead();
    }

    /**
     * Custom modification of CSV configuration
     */
    @Nested
    class ReadCsvFormat {

        @Test
        void asNormalJavaBean() {
            String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.csv";
            try (ExcelReader excelReader = FastExcel.read(fileName, DemoData.class, new DemoDataListener())
                    .build()) {
                // Check if it is a CSV file
                if (excelReader.analysisContext().readWorkbookHolder() instanceof CsvReadWorkbookHolder) {
                    CsvReadWorkbookHolder csvReadWorkbookHolder = (CsvReadWorkbookHolder)
                            excelReader.analysisContext().readWorkbookHolder();
                    // Set to comma-separated (default is also comma-separated)
                    // Note: `withDelimiter` will regenerate the format, so it needs to be set back.
                    csvReadWorkbookHolder.setCsvFormat(
                            csvReadWorkbookHolder.getCsvFormat().withDelimiter(','));
                }

                // Get all sheets
                List<ReadSheet> readSheetList = excelReader.excelExecutor().sheetList();
                // If you only want to read the first sheet, you can pass the parameter accordingly.
                // ReadSheet readSheet = FastExcel.readSheet(0).build();
                excelReader.read(readSheetList);
            }
        }

        @Test
        void asChainedAccessorsJavaBean() {
            String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.csv";
            try (ExcelReader excelReader = FastExcel.read(
                            fileName, DemoChainAccessorsData.class, new ReadListener<DemoChainAccessorsData>() {
                                @Override
                                public void invoke(DemoChainAccessorsData data, AnalysisContext context) {
                                    Assertions.assertNotNull(data.getString());
                                    Assertions.assertNotNull(data.getDate());
                                    Assertions.assertNotNull(data.getDoubleData());
                                }

                                @Override
                                public void doAfterAllAnalysed(AnalysisContext context) {}
                            })
                    .build()) {
                excelReader.readAll();
            }
        }
    }
}

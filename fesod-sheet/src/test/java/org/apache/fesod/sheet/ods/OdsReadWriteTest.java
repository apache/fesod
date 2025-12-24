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

package org.apache.fesod.sheet.ods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for ODS (OpenDocument Spreadsheet) read and write functionality.
 */
public class OdsReadWriteTest {

    private File tempDir;

    @BeforeEach
    public void setUp() {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "fesod-ods-test");
        tempDir.mkdirs();
    }

    @AfterEach
    public void tearDown() {
        if (tempDir != null && tempDir.exists()) {
            FileUtils.delete(tempDir);
        }
    }

    /**
     * Test writing ODS file.
     */
    @Test
    public void testWriteOds() {
        String fileName = new File(tempDir, "test-write.ods").getAbsolutePath();

        List<OdsTestData> dataList = generateTestData(10);

        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("TestSheet")
                .doWrite(dataList);

        File outputFile = new File(fileName);
        assertTrue(outputFile.exists(), "ODS file should be created");
        assertTrue(outputFile.length() > 0, "ODS file should not be empty");
    }

    /**
     * Test reading ODS file.
     */
    @Test
    public void testReadOds() {
        // First write the file
        String fileName = new File(tempDir, "test-read.ods").getAbsolutePath();
        List<OdsTestData> writeData = generateTestData(5);

        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("TestSheet")
                .doWrite(writeData);

        // Then read it back
        List<OdsTestData> readData = new ArrayList<>();
        FesodSheet.read(fileName, OdsTestData.class, new ReadListener<OdsTestData>() {
                    @Override
                    public void invoke(OdsTestData data, AnalysisContext context) {
                        readData.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        // Reading complete
                    }
                })
                .sheet()
                .doRead();

        // Verify
        assertNotNull(readData, "Read data should not be null");
        assertEquals(5, readData.size(), "Should read 5 rows");

        // Verify first row data
        OdsTestData firstRow = readData.get(0);
        assertNotNull(firstRow.getString(), "String value should not be null");
        assertTrue(firstRow.getString().startsWith("String"), "String value should start with 'String'");
    }

    /**
     * Test write and read round-trip.
     */
    @Test
    public void testWriteReadRoundTrip() {
        String fileName = new File(tempDir, "test-roundtrip.ods").getAbsolutePath();

        // Prepare test data
        List<OdsTestData> originalData = generateTestData(3);

        // Write
        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("RoundTrip")
                .doWrite(originalData);

        // Read
        List<OdsTestData> readBackData = new ArrayList<>();
        FesodSheet.read(fileName, OdsTestData.class, new ReadListener<OdsTestData>() {
                    @Override
                    public void invoke(OdsTestData data, AnalysisContext context) {
                        readBackData.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();

        // Verify count
        assertEquals(originalData.size(), readBackData.size(), "Data count should match");

        // Verify values
        for (int i = 0; i < originalData.size(); i++) {
            OdsTestData original = originalData.get(i);
            OdsTestData readBack = readBackData.get(i);
            assertEquals(original.getString(), readBack.getString(), "String values should match at row " + i);
            assertEquals(original.getDoubleData(), readBack.getDoubleData(), "Double values should match at row " + i);
        }
    }

    /**
     * Test reading ODS file from InputStream (verifies magic number conflict fix).
     */
    @Test
    public void testReadOdsFromInputStream() {
        // First write the file
        String fileName = new File(tempDir, "test-inputstream.ods").getAbsolutePath();
        List<OdsTestData> writeData = generateTestData(3);

        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("TestSheet")
                .doWrite(writeData);

        // Read from InputStream without explicit type - should auto-detect as ODS
        List<OdsTestData> readData = new ArrayList<>();
        try (InputStream inputStream = new FileInputStream(fileName)) {
            FesodSheet.read(inputStream, OdsTestData.class, new ReadListener<OdsTestData>() {
                        @Override
                        public void invoke(OdsTestData data, AnalysisContext context) {
                            readData.add(data);
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {}
                    })
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read ODS from InputStream", e);
        }

        // Verify
        assertNotNull(readData, "Read data should not be null");
        assertEquals(3, readData.size(), "Should read 3 rows");
    }

    /**
     * Test multiple sheets support.
     */
    @Test
    public void testMultipleSheets() {
        String fileName = new File(tempDir, "test-multi-sheet.ods").getAbsolutePath();
        List<OdsTestData> dataList1 = generateTestData(5);
        List<OdsTestData> dataList2 = generateTestData(3);

        // Write multiple sheets
        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("Sheet1")
                .doWrite(dataList1);

        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("Sheet2")
                .doWrite(dataList2);

        // Read first sheet
        List<OdsTestData> readData1 = new ArrayList<>();
        FesodSheet.read(fileName, OdsTestData.class, new ReadListener<OdsTestData>() {
                    @Override
                    public void invoke(OdsTestData data, AnalysisContext context) {
                        readData1.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet(0)
                .doRead();

        assertEquals(5, readData1.size(), "First sheet should have 5 rows");

        // Read second sheet
        List<OdsTestData> readData2 = new ArrayList<>();
        FesodSheet.read(fileName, OdsTestData.class, new ReadListener<OdsTestData>() {
                    @Override
                    public void invoke(OdsTestData data, AnalysisContext context) {
                        readData2.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet(1)
                .doRead();

        assertEquals(3, readData2.size(), "Second sheet should have 3 rows");
    }

    /**
     * Test various data types.
     */
    @Test
    public void testVariousDataTypes() {
        String fileName = new File(tempDir, "test-datatypes.ods").getAbsolutePath();
        List<OdsExtendedTestData> dataList = new ArrayList<>();

        OdsExtendedTestData data1 = new OdsExtendedTestData();
        data1.setString("Test String");
        data1.setInteger(42);
        data1.setLongValue(123456789L);
        data1.setBooleanValue(true);
        data1.setBigDecimal(new BigDecimal("123.456"));
        data1.setDoubleValue(3.14159);
        dataList.add(data1);

        OdsExtendedTestData data2 = new OdsExtendedTestData();
        data2.setString("Another String");
        data2.setInteger(100);
        data2.setLongValue(987654321L);
        data2.setBooleanValue(false);
        data2.setBigDecimal(new BigDecimal("999.999"));
        data2.setDoubleValue(2.71828);
        dataList.add(data2);

        // Write
        FesodSheet.write(fileName, OdsExtendedTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("DataTypes")
                .doWrite(dataList);

        // Read
        List<OdsExtendedTestData> readData = new ArrayList<>();
        FesodSheet.read(fileName, OdsExtendedTestData.class, new ReadListener<OdsExtendedTestData>() {
                    @Override
                    public void invoke(OdsExtendedTestData data, AnalysisContext context) {
                        readData.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();

        assertEquals(2, readData.size(), "Should read 2 rows");
        assertEquals("Test String", readData.get(0).getString());
        assertEquals(Integer.valueOf(42), readData.get(0).getInteger());
        assertEquals(Long.valueOf(123456789L), readData.get(0).getLongValue());
        assertEquals(Boolean.TRUE, readData.get(0).getBooleanValue());
    }

    /**
     * Test empty data list.
     */
    @Test
    public void testEmptyData() {
        String fileName = new File(tempDir, "test-empty.ods").getAbsolutePath();
        List<OdsTestData> emptyList = new ArrayList<>();

        FesodSheet.write(fileName, OdsTestData.class)
                .excelType(ExcelTypeEnum.ODS)
                .sheet("EmptySheet")
                .doWrite(emptyList);

        File outputFile = new File(fileName);
        assertTrue(outputFile.exists(), "ODS file should be created even with empty data");

        // Read empty file
        List<OdsTestData> readData = new ArrayList<>();
        FesodSheet.read(fileName, OdsTestData.class, new ReadListener<OdsTestData>() {
                    @Override
                    public void invoke(OdsTestData data, AnalysisContext context) {
                        readData.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();

        assertEquals(0, readData.size(), "Should read 0 rows from empty file");
    }

    /**
     * Test writing to OutputStream.
     */
    @Test
    public void testWriteToOutputStream() throws Exception {
        String fileName = new File(tempDir, "test-outputstream.ods").getAbsolutePath();
        List<OdsTestData> dataList = generateTestData(5);

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            FesodSheet.write(outputStream, OdsTestData.class)
                    .excelType(ExcelTypeEnum.ODS)
                    .sheet("OutputStream")
                    .doWrite(dataList);
        }

        File outputFile = new File(fileName);
        assertTrue(outputFile.exists(), "ODS file should be created");
        assertTrue(outputFile.length() > 0, "ODS file should not be empty");
    }

    /**
     * Generate test data.
     */
    private List<OdsTestData> generateTestData(int count) {
        List<OdsTestData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            OdsTestData data = new OdsTestData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56 + i);
            list.add(data);
        }
        return list;
    }

    /**
     * Test data model for ODS tests.
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class OdsTestData {
        @ExcelProperty("String Title")
        private String string;

        @ExcelProperty("Date Title")
        private Date date;

        @ExcelProperty("Number Title")
        private Double doubleData;
    }

    /**
     * Extended test data model with various data types.
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class OdsExtendedTestData {
        @ExcelProperty("String")
        private String string;

        @ExcelProperty("Integer")
        private Integer integer;

        @ExcelProperty("Long")
        private Long longValue;

        @ExcelProperty("Boolean")
        private Boolean booleanValue;

        @ExcelProperty("BigDecimal")
        private BigDecimal bigDecimal;

        @ExcelProperty("Double")
        private Double doubleValue;
    }
}

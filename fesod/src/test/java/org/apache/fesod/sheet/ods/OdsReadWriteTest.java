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
}

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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.readwrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 */
public class ExceptionDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @MethodSource("allFormats")
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(10));
        ExceptionDataListener listener = new ExceptionDataListener();
        FesodSheet.read(file, SimpleData.class, listener).sheet().doRead();
        assertEquals(8, listener.getList().size());
        assertEquals("Name0", listener.getList().get(0).getName());
        assertEquals(0, (int) listener.getSheetNo());
        assertEquals("Name", listener.getHeadName());
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void readAndWriteException(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(10));
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> FesodSheet.read(
                        file, SimpleData.class, new ExceptionThrowDataListener())
                .sheet()
                .doRead());
        assertEquals("/ by zero", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void readAndWriteExcelAnalysisStopSheetException(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        try (ExcelWriter excelWriter = FesodSheet.write(file, SimpleData.class).build()) {
            for (int i = 0; i < 5; i++) {
                String sheetName = "sheet" + i;
                WriteSheet writeSheet = FesodSheet.writerSheet(i, sheetName).build();
                List<SimpleData> data = TestDataBuilder.simpleData(10, sheetName + "-");
                excelWriter.write(data, writeSheet);
            }
        }

        ExcelAnalysisStopSheetExceptionDataListener listener = new ExcelAnalysisStopSheetExceptionDataListener();
        FesodSheet.read(file, SimpleData.class, listener).doReadAll();
        Map<Integer, List<String>> dataMap = listener.getDataMap();
        assertEquals(5, dataMap.size());
        for (int i = 0; i < 5; i++) {
            List<String> sheetDataList = dataMap.get(i);
            assertNotNull(sheetDataList);
            assertEquals(5, sheetDataList.size());
            String sheetName = "sheet" + i;
            for (String sheetData : sheetDataList) {
                assertTrue(sheetData.startsWith(sheetName));
            }
        }
    }
}

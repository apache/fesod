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

package org.apache.fesod.excel.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ExceptionDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File fileExcelAnalysisStopSheetException07;
    private static File fileExcelAnalysisStopSheetException03;
    private static File fileExcelAnalysisStopSheetExceptionCsv;
    private static File fileException07;
    private static File fileException03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("exception.xlsx");
        file03 = TestFileUtil.createNewFile("exception.xls");
        fileCsv = TestFileUtil.createNewFile("exception.csv");
        fileExcelAnalysisStopSheetException07 = TestFileUtil.createNewFile("excelAnalysisStopSheetException.xlsx");
        fileExcelAnalysisStopSheetException03 = TestFileUtil.createNewFile("excelAnalysisStopSheetException.xls");
        fileException07 = TestFileUtil.createNewFile("exceptionThrow.xlsx");
        fileException03 = TestFileUtil.createNewFile("exceptionThrow.xls");
    }

    @Test
    public void t01ReadAndWrite07() throws Exception {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() throws Exception {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() throws Exception {
        readAndWrite(fileCsv);
    }

    @Test
    public void t11ReadAndWrite07() throws Exception {
        readAndWriteException(fileException07);
    }

    @Test
    public void t12ReadAndWrite03() throws Exception {
        readAndWriteException(fileException03);
    }

    @Test
    public void t21ReadAndWrite07() throws Exception {
        readAndWriteExcelAnalysisStopSheetException(fileExcelAnalysisStopSheetException07);
    }

    @Test
    public void t22ReadAndWrite03() throws Exception {
        readAndWriteExcelAnalysisStopSheetException(fileExcelAnalysisStopSheetException03);
    }

    private void readAndWriteExcelAnalysisStopSheetException(File file) throws Exception {
        try (ExcelWriter excelWriter =
                FastExcel.write(file, ExceptionData.class).build()) {
            for (int i = 0; i < 5; i++) {
                String sheetName = "sheet" + i;
                WriteSheet writeSheet = FastExcel.writerSheet(i, sheetName).build();
                List<ExceptionData> data = data(sheetName);
                excelWriter.write(data, writeSheet);
            }
        }

        ExcelAnalysisStopSheetExceptionDataListener excelAnalysisStopSheetExceptionDataListener =
                new ExcelAnalysisStopSheetExceptionDataListener();
        FastExcel.read(file, ExceptionData.class, excelAnalysisStopSheetExceptionDataListener)
                .doReadAll();
        Map<Integer, List<String>> dataMap = excelAnalysisStopSheetExceptionDataListener.getDataMap();
        Assertions.assertEquals(5, dataMap.size());
        for (int i = 0; i < 5; i++) {
            List<String> sheetDataList = dataMap.get(i);
            Assertions.assertNotNull(sheetDataList);
            Assertions.assertEquals(5, sheetDataList.size());
            String sheetName = "sheet" + i;

            for (String sheetData : sheetDataList) {
                Assertions.assertTrue(sheetData.startsWith(sheetName));
            }
        }
    }

    private void readAndWriteException(File file) throws Exception {
        FastExcel.write(new FileOutputStream(file), ExceptionData.class).sheet().doWrite(data());
        ArithmeticException exception = Assertions.assertThrows(ArithmeticException.class, () -> FastExcel.read(
                        new FileInputStream(file), ExceptionData.class, new ExceptionThrowDataListener())
                .sheet()
                .doRead());
        Assertions.assertEquals("/ by zero", exception.getMessage());
    }

    private void readAndWrite(File file) throws Exception {
        FastExcel.write(new FileOutputStream(file), ExceptionData.class).sheet().doWrite(data());
        FastExcel.read(new FileInputStream(file), ExceptionData.class, new ExceptionDataListener())
                .sheet()
                .doRead();
    }

    private List<ExceptionData> data() {
        List<ExceptionData> list = new ArrayList<ExceptionData>();
        for (int i = 0; i < 10; i++) {
            ExceptionData simpleData = new ExceptionData();
            simpleData.setName("姓名" + i);
            list.add(simpleData);
        }
        return list;
    }

    private List<ExceptionData> data(String prefix) {
        List<ExceptionData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ExceptionData simpleData = new ExceptionData();
            simpleData.setName(prefix + "-姓名" + i);
            list.add(simpleData);
        }
        return list;
    }
}

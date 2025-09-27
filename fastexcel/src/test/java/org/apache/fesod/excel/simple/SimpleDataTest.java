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

package org.apache.fesod.excel.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.read.listener.PageReadListener;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test simple read/write for Excel formats:
 * <li>Excel 2007 format (.xlsx)</li>
 * <li>Excel 2003 format (.xls)</li>
 * <li>CSV format (.csv)</li>
 * Test methods are grouped by prefixes:
 * <li>t0x: Basic read/write tests</li>
 * <li>t1x: Synchronous reading tests</li>
 * <li>t2x: Specific feature tests (sheet name reading, pagination, etc.)</li>
 *
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class SimpleDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("simple07.xlsx");
        file03 = TestFileUtil.createNewFile("simple03.xls");
        fileCsv = TestFileUtil.createNewFile("simpleCsv.csv");
    }

    @Test
    public void t01ReadAndWrite07() {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() {
        readAndWrite(fileCsv);
    }

    /**
     * Test simple read/write with file
     *
     * @param file file
     */
    private void readAndWrite(File file) {
        FastExcel.write(file, SimpleData.class).sheet().doWrite(data());
        // use a SimpleDataListener object to handle and check result
        FastExcel.read(file, SimpleData.class, new SimpleDataListener()).sheet().doRead();
    }

    @Test
    public void t04ReadAndWrite07() throws Exception {
        readAndWriteInputStream(file07, ExcelTypeEnum.XLSX);
    }

    @Test
    public void t05ReadAndWrite03() throws Exception {
        readAndWriteInputStream(file03, ExcelTypeEnum.XLS);
    }

    @Test
    public void t06ReadAndWriteCsv() throws Exception {
        readAndWriteInputStream(fileCsv, ExcelTypeEnum.CSV);
    }

    /**
     * Test simple read/write with InputStream/OutputStream
     *
     * @param file          file used to generate InputStream/OutputStream
     * @param excelTypeEnum excel type enum
     * @throws Exception exception
     */
    private void readAndWriteInputStream(File file, ExcelTypeEnum excelTypeEnum) throws Exception {
        FastExcel.write(new FileOutputStream(file), SimpleData.class)
                .excelType(excelTypeEnum)
                .sheet()
                .doWrite(data());
        // use a SimpleDataListener object to handle and check result
        FastExcel.read(new FileInputStream(file), SimpleData.class, new SimpleDataListener())
                .sheet()
                .doRead();
    }

    /**
     * Test synchronous reading of Excel 2007 format
     */
    @Test
    public void t11SynchronousRead07() {
        synchronousRead(file07);
    }

    /**
     * Test synchronous reading of Excel 2003 format
     */
    @Test
    public void t12SynchronousRead03() {
        synchronousRead(file03);
    }

    /**
     * Test synchronous reading of CSV format
     */
    @Test
    public void t13SynchronousReadCsv() {
        synchronousRead(fileCsv);
    }

    /**
     * test read sheet in an Excel file with specified sheetName
     */
    @Test
    public void t21SheetNameRead07() {
        List<Map<Integer, Object>> list = FastExcel.read(
                        TestFileUtil.readFile("simple" + File.separator + "simple07.xlsx"))
                // set the sheet name to read
                .sheet("simple")
                .doReadSync();
        Assertions.assertEquals(1, list.size());
    }

    /**
     * test read sheet in an Excel file with specified sheetNo
     */
    @Test
    public void t22SheetNoRead07() {
        List<Map<Integer, Object>> list = FastExcel.read(
                        TestFileUtil.readFile("simple" + File.separator + "simple07.xlsx"))
                // sheetNo begin with 0
                .sheet(1)
                .doReadSync();
        Assertions.assertEquals(1, list.size());
    }

    /**
     * Test page reading with PageReadListener
     * <p>
     * PageReadListener processes Excel data in batches, triggering callbacks when reaching
     * specified batch size. {@link PageReadListener#invoke}
     * Useful for large files to prevent memory overflow
     * </p>
     */
    @Test
    public void t23PageReadListener07() {
        // Read the first 5 rows of an Excel file
        FastExcel.read(
                        file07,
                        SimpleData.class,
                        new PageReadListener<SimpleData>(
                                dataList -> {
                                    Assertions.assertEquals(5, dataList.size());
                                },
                                5))
                .sheet()
                .doRead();
    }

    /**
     * Synchronous reading of Excel files
     * <p>
     * Unlike asynchronous reading with listeners, synchronous reading loads all data into memory
     * and returns a complete data list. It may cause memory issues when processing large files.
     * </p>
     *
     * @param file file
     */
    private void synchronousRead(File file) {
        // Synchronous read file
        List<Object> list = FastExcel.read(file).head(SimpleData.class).sheet().doReadSync();
        Assertions.assertEquals(list.size(), 10);
        Assertions.assertTrue(list.get(0) instanceof SimpleData);
        Assertions.assertEquals(((SimpleData) list.get(0)).getName(), "姓名0");
    }

    /**
     * mock data
     *
     * @return {@link List }<{@link SimpleData }>
     */
    private List<SimpleData> data() {
        List<SimpleData> list = new ArrayList<SimpleData>();
        for (int i = 0; i < 10; i++) {
            SimpleData simpleData = new SimpleData();
            simpleData.setName("姓名" + i);
            list.add(simpleData);
        }
        return list;
    }
}

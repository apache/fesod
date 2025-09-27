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

package org.apache.fesod.excel.encrypt;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.read.builder.ExcelReaderBuilder;
import org.apache.fesod.excel.simple.SimpleData;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.builder.ExcelWriterBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class EncryptDataTest {
    private static final String PASSWORD = "123456";
    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File file07OutputStream;
    private static File file03OutputStream;
    private static File fileCsvOutputStream;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("encrypt07.xlsx");
        file03 = TestFileUtil.createNewFile("encrypt03.xls");
        fileCsv = TestFileUtil.createNewFile("encryptCsv.csv");
        file07OutputStream = TestFileUtil.createNewFile("encryptOutputStream07.xlsx");
        file03OutputStream = TestFileUtil.createNewFile("encryptOutputStream03.xls");
        fileCsvOutputStream = TestFileUtil.createNewFile("encryptOutputStreamCsv.csv");
    }

    @Test
    public void t01ReadAndWrite07() throws Exception {
        readAndWrite(file07, null, false, false);
        readAndWrite(file07, null, true, false);
        readAndWrite(file07, ExcelTypeEnum.XLSX, false, false);
        readAndWrite(file07, ExcelTypeEnum.XLSX, true, false);

        readAndWrite(file07OutputStream, null, false, true);
        readAndWrite(file07OutputStream, null, true, true);
        readAndWrite(file07OutputStream, ExcelTypeEnum.XLSX, false, true);
        readAndWrite(file07OutputStream, ExcelTypeEnum.XLSX, true, true);
    }

    @Test
    public void t02ReadAndWrite03() throws Exception {
        readAndWrite(file03, null, false, false);
        readAndWrite(file03, null, true, false);
        readAndWrite(file03, ExcelTypeEnum.XLS, false, false);
        readAndWrite(file03, ExcelTypeEnum.XLS, true, false);

        readAndWrite(file03OutputStream, null, false, true);
        readAndWrite(file03OutputStream, null, true, true);
        readAndWrite(file03OutputStream, ExcelTypeEnum.XLSX, false, true);
        readAndWrite(file03OutputStream, ExcelTypeEnum.XLSX, true, true);
    }

    @Test
    public void t03ReadAndWriteCSV() throws Exception {
        readAndWrite(fileCsv, null, false, false);
        readAndWrite(fileCsv, null, true, false);
        readAndWrite(fileCsv, ExcelTypeEnum.CSV, false, false);
        readAndWrite(fileCsv, ExcelTypeEnum.CSV, true, false);

        readAndWrite(fileCsvOutputStream, null, false, true);
        readAndWrite(fileCsvOutputStream, null, true, true);
        readAndWrite(fileCsvOutputStream, ExcelTypeEnum.CSV, false, true);
        readAndWrite(fileCsvOutputStream, ExcelTypeEnum.CSV, true, true);
    }

    private void readAndWrite(File file, ExcelTypeEnum excelType, boolean hasPassword, boolean isStream)
            throws Exception {
        log.info(
                "file:{}, isStream:{}, excelType:{}, hasPassword:{}", file.getName(), isStream, excelType, hasPassword);
        ExcelWriterBuilder excelWriterBuilder = isStream
                ? FastExcel.write(Files.newOutputStream(file.toPath()), EncryptData.class)
                : FastExcel.write(file, EncryptData.class);

        ExcelReaderBuilder readerBuilder = isStream
                ? FastExcel.read(Files.newInputStream(file.toPath()), EncryptData.class, new EncryptDataListener())
                : FastExcel.read(file, EncryptData.class, new EncryptDataListener());
        if (excelType != null) {
            excelWriterBuilder.excelType(excelType);
            readerBuilder.excelType(excelType);
        }
        if (hasPassword) {
            excelWriterBuilder.password(PASSWORD);
            readerBuilder.password(PASSWORD);
        }

        excelWriterBuilder.sheet().doWrite(data());
        List<EncryptData> dataList = readerBuilder.sheet().doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0).getName());
    }

    private List<SimpleData> data() {
        List<SimpleData> list = new ArrayList<SimpleData>();
        for (int i = 0; i < 10; i++) {
            SimpleData simpleData = new SimpleData();
            simpleData.setName("Name" + i);
            list.add(simpleData);
        }
        return list;
    }
}

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

package org.apache.fesod.excel.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class WriteHandlerTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("writeHandler07.xlsx");
        file03 = TestFileUtil.createNewFile("writeHandler03.xls");
        fileCsv = TestFileUtil.createNewFile("writeHandlerCsv.csv");
    }

    @Test
    public void t01WorkbookWrite07() throws Exception {
        workbookWrite(file07);
    }

    @Test
    public void t02WorkbookWrite03() throws Exception {
        workbookWrite(file03);
    }

    @Test
    public void t03WorkbookWriteCsv() throws Exception {
        workbookWrite(fileCsv);
    }

    @Test
    public void t11SheetWrite07() throws Exception {
        sheetWrite(file07);
    }

    @Test
    public void t12SheetWrite03() throws Exception {
        sheetWrite(file03);
    }

    @Test
    public void t13SheetWriteCsv() throws Exception {
        sheetWrite(fileCsv);
    }

    @Test
    public void t21TableWrite07() throws Exception {
        tableWrite(file07);
    }

    @Test
    public void t22TableWrite03() throws Exception {
        tableWrite(file03);
    }

    @Test
    public void t23TableWriteCsv() throws Exception {
        tableWrite(fileCsv);
    }

    private void workbookWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FastExcel.write(file)
                .head(WriteHandlerData.class)
                .registerWriteHandler(writeHandler)
                .sheet()
                .doWrite(data());
        writeHandler.afterAll();
    }

    private void sheetWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FastExcel.write(file)
                .head(WriteHandlerData.class)
                .sheet()
                .registerWriteHandler(writeHandler)
                .doWrite(data());
        writeHandler.afterAll();
    }

    private void tableWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FastExcel.write(file)
                .head(WriteHandlerData.class)
                .sheet()
                .table(0)
                .registerWriteHandler(writeHandler)
                .doWrite(data());
        writeHandler.afterAll();
    }

    private List<WriteHandlerData> data() {
        List<WriteHandlerData> list = new ArrayList<WriteHandlerData>();
        for (int i = 0; i < 1; i++) {
            WriteHandlerData data = new WriteHandlerData();
            data.setName("姓名" + i);
            list.add(data);
        }
        return list;
    }
}

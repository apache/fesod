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

package org.apache.fesod.sheet.handler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class WriteHandlerTest {

    private File file07;
    private File file03;
    private File fileCsv;

    private File fillTemplate07;
    private File fillTemplate03;
    private File fill07;
    private File fill03;

    @BeforeEach
    void init() throws Exception {
        file07 = TestFileUtil.createNewFile("writeHandler07.xlsx");
        file03 = TestFileUtil.createNewFile("writeHandler03.xls");
        fileCsv = TestFileUtil.createNewFile("writeHandlerCsv.csv");

        fillTemplate07 = loadTemplate("fillHandler07.xlsx");
        fillTemplate03 = loadTemplate("fillHandler03.xls");
        fill07 = TestFileUtil.createNewFile("fill07.xlsx");
        fill03 = TestFileUtil.createNewFile("fill03.xls");
    }

    private File loadTemplate(String filename) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("fill" + File.separator + filename);
        Assertions.assertNotNull(resource);
        return new File(resource.toURI());
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

    @Test
    public void t31SheetWrite07() throws Exception {
        writeSheetWithMultiWrites(file07);
    }

    @Test
    public void t32SheetWrite03() throws Exception {
        writeSheetWithMultiWrites(file03);
    }

    @Test
    public void t33SheetWriteCsv() throws Exception {
        writeSheetWithMultiWrites(fileCsv);
    }

    @Test
    public void t41TableWrite07() throws Exception {
        writeTableWithMultiWrites(file07);
    }

    @Test
    public void t42TableWrite03() throws Exception {
        writeTableWithMultiWrites(file03);
    }

    @Test
    public void t43TableWriteCsv() throws Exception {
        writeTableWithMultiWrites(fileCsv);
    }

    @Test
    public void t51SheetFill07() throws Exception {
        fillSheetWithMultiFills(fillTemplate07, fill07);
    }

    @Test
    public void t52SheetFill03() throws Exception {
        fillSheetWithMultiFills(fillTemplate03, fill03);
    }

    private void workbookWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(WriteHandlerData.class)
                .registerWriteHandler(writeHandler)
                .sheet()
                .doWrite(data());
        writeHandler.afterAll();
    }

    private void sheetWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(WriteHandlerData.class)
                .sheet()
                .registerWriteHandler(writeHandler)
                .doWrite(data());
        writeHandler.afterAll();
    }

    private void writeSheetWithMultiWrites(File file) {
        CountingWriteHandler writeHandler = new CountingWriteHandler(1L, 2L);

        try (ExcelWriter writer =
                FesodSheet.write(file).head(WriteHandlerData.class).build()) {

            WriteSheet writeSheet = FesodSheet.writerSheet()
                    .needHead(Boolean.TRUE)
                    .registerWriteHandler(writeHandler)
                    .build();

            writer.write(data(), writeSheet);
            writer.write(data(), writeSheet);
        }

        writeHandler.afterAll();
    }

    private void tableWrite(File file) {
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(WriteHandlerData.class)
                .sheet()
                .table(0)
                .registerWriteHandler(writeHandler)
                .doWrite(data());
        writeHandler.afterAll();
    }

    private void writeTableWithMultiWrites(File file) {
        CountingWriteHandler writeHandler = new CountingWriteHandler(2L, 2L);

        try (ExcelWriter writer =
                FesodSheet.write(file).head(WriteHandlerData.class).build()) {

            WriteSheet writeSheet = FesodSheet.writerSheet()
                    .needHead(Boolean.FALSE)
                    .registerWriteHandler(writeHandler)
                    .build();
            WriteTable table1 = FesodSheet.writerTable(0).needHead(Boolean.TRUE).build();
            WriteTable table2 = FesodSheet.writerTable(1).needHead(Boolean.TRUE).build();

            writer.write(data(), writeSheet, table1);
            writer.write(data(), writeSheet, table2);
        }

        writeHandler.afterAll();
    }

    private void fillSheetWithMultiFills(File template, File file) {
        CountingWriteHandler writeHandler = new CountingWriteHandler(0L, 4L);

        try (ExcelWriter writer = FesodSheet.write(file).withTemplate(template).build()) {

            WriteSheet writeSheet =
                    FesodSheet.writerSheet().registerWriteHandler(writeHandler).build();

            Map<String, String> data1 = new HashMap<>();
            data1.put("name", "Tom");

            Map<String, String> data2 = new HashMap<>();
            data2.put("code", "Custom Code");

            writer.fill(data1, writeSheet);
            writer.fill(data2, writeSheet);
        }

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

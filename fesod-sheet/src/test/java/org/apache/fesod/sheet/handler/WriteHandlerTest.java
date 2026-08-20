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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatCapability;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.WRITE)
public class WriteHandlerTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void workbookWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .registerWriteHandler(writeHandler)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(1));
        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource
    void sheetWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .sheet()
                .registerWriteHandler(writeHandler)
                .doWrite(TestDataBuilder.simpleData(1));
        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource
    void tableWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        WriteHandler writeHandler = new WriteHandler();
        FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .sheet()
                .table(0)
                .registerWriteHandler(writeHandler)
                .doWrite(TestDataBuilder.simpleData(1));
        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void sheetMultiWrites(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(1L)
                .withAfterCellCreate(1L)
                .withAfterCellDataConverted(2L)
                .withAfterCellDispose(1L)
                .withBeforeRowCreate(1L)
                .withAfterRowCreate(1L)
                .withAfterRowDispose(1L)
                .withBeforeSheetCreate(1L)
                .withAfterSheetCreate(1L)
                .withAfterSheetDispose(1L)
                .withAfterSheetDisposeSheetNos(Collections.singletonList(0))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .build()) {

            WriteSheet writeSheet = FesodSheet.writerSheet()
                    .needHead(Boolean.TRUE)
                    .registerWriteHandler(writeHandler)
                    .build();

            writer.write(TestDataBuilder.simpleData(1), writeSheet);
            writer.write(TestDataBuilder.simpleData(1), writeSheet);
        }

        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void multiTableWrites(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(2L)
                .withAfterCellCreate(2L)
                .withAfterCellDataConverted(2L)
                .withAfterCellDispose(2L)
                .withBeforeRowCreate(2L)
                .withAfterRowCreate(2L)
                .withAfterRowDispose(2L)
                .withBeforeSheetCreate(1L)
                .withAfterSheetCreate(1L)
                .withAfterSheetDispose(1L)
                .withAfterSheetDisposeSheetNos(Collections.singletonList(0))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .build()) {

            WriteSheet writeSheet = FesodSheet.writerSheet()
                    .needHead(Boolean.FALSE)
                    .registerWriteHandler(writeHandler)
                    .build();
            WriteTable table1 = FesodSheet.writerTable(0).needHead(Boolean.TRUE).build();
            WriteTable table2 = FesodSheet.writerTable(1).needHead(Boolean.TRUE).build();

            writer.write(TestDataBuilder.simpleData(1), writeSheet, table1);
            writer.write(TestDataBuilder.simpleData(1), writeSheet, table2);
        }

        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void complexMultiSheetAndMultiTableWrites(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(4L)
                .withAfterCellCreate(4L)
                .withAfterCellDataConverted(4L)
                .withAfterCellDispose(4L)
                .withBeforeRowCreate(4L)
                .withAfterRowCreate(4L)
                .withAfterRowDispose(4L)
                .withBeforeSheetCreate(2L)
                .withAfterSheetCreate(2L)
                .withAfterSheetDispose(2L)
                .withAfterSheetDisposeSheetNos(Arrays.asList(0, 1))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .registerWriteHandler(writeHandler)
                .build()) {

            WriteSheet writeSheet1 =
                    FesodSheet.writerSheet(0).needHead(Boolean.FALSE).build();

            writer.write(
                    TestDataBuilder.simpleData(1),
                    writeSheet1,
                    FesodSheet.writerTable(0).needHead(Boolean.TRUE).build());
            writer.write(
                    TestDataBuilder.simpleData(1),
                    writeSheet1,
                    FesodSheet.writerTable(1).needHead(Boolean.TRUE).build());

            WriteSheet writeSheet2 =
                    FesodSheet.writerSheet(1).needHead(Boolean.FALSE).build();

            writer.write(
                    TestDataBuilder.simpleData(1),
                    writeSheet2,
                    FesodSheet.writerTable(0).needHead(Boolean.TRUE).build());
            writer.write(
                    TestDataBuilder.simpleData(1),
                    writeSheet2,
                    FesodSheet.writerTable(1).needHead(Boolean.TRUE).build());
        }

        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY, requires = FormatCapability.TEMPLATES)
    void sheetFills(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        File template = readFile("fill" + File.separator + "fillHandler" + (format == ExcelFormat.XLSX ? "07" : "03")
                + format.getExtension());
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(0L)
                .withAfterCellCreate(0L)
                .withAfterCellDataConverted(4L)
                .withAfterCellDispose(0L)
                .withBeforeRowCreate(0L)
                .withAfterRowCreate(0L)
                .withAfterRowDispose(0L)
                .withBeforeSheetCreate(1L)
                .withAfterSheetCreate(1L)
                .withAfterSheetDispose(1L)
                .withAfterSheetDisposeSheetNos(Collections.singletonList(0))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

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

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void multiSheetWrites(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(2L)
                .withAfterCellCreate(2L)
                .withAfterCellDataConverted(2L)
                .withAfterCellDispose(2L)
                .withBeforeRowCreate(2L)
                .withAfterRowCreate(2L)
                .withAfterRowDispose(2L)
                .withBeforeSheetCreate(2L)
                .withAfterSheetCreate(2L)
                .withAfterSheetDispose(2L)
                .withAfterSheetDisposeSheetNos(Arrays.asList(0, 1))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .registerWriteHandler(writeHandler)
                .build()) {

            WriteSheet writeSheet1 =
                    FesodSheet.writerSheet(0).needHead(Boolean.TRUE).build();
            writer.write(TestDataBuilder.simpleData(1), writeSheet1);

            WriteSheet writeSheet2 =
                    FesodSheet.writerSheet(1).needHead(Boolean.TRUE).build();
            writer.write(TestDataBuilder.simpleData(1), writeSheet2);
        }

        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY, requires = FormatCapability.TEMPLATES)
    void multiSheetFills(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        File template = readFile("fill" + File.separator + "fillHandler" + (format == ExcelFormat.XLSX ? "07" : "03")
                + format.getExtension());
        CountingWriteHandler writeHandler = CountingWriteHandler.builder()
                .withBeforeCellCreate(0L)
                .withAfterCellCreate(0L)
                .withAfterCellDataConverted(8L)
                .withAfterCellDispose(0L)
                .withBeforeRowCreate(0L)
                .withAfterRowCreate(0L)
                .withAfterRowDispose(0L)
                .withBeforeSheetCreate(2L)
                .withAfterSheetCreate(2L)
                .withAfterSheetDispose(2L)
                .withAfterSheetDisposeSheetNos(Arrays.asList(0, 1))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .withTemplate(template)
                .registerWriteHandler(writeHandler)
                .build()) {

            WriteSheet writeSheet0 = FesodSheet.writerSheet(0).build();
            Map<String, String> data1 = new HashMap<>();
            data1.put("name", "Tom");
            Map<String, String> data2 = new HashMap<>();
            data2.put("code", "Code1");
            writer.fill(data1, writeSheet0);
            writer.fill(data2, writeSheet0);

            WriteSheet writeSheet1 = FesodSheet.writerSheet(1).build();
            Map<String, String> data3 = new HashMap<>();
            data3.put("name", "Jerry");
            Map<String, String> data4 = new HashMap<>();
            data4.put("code", "Code2");
            writer.fill(data3, writeSheet1);
            writer.fill(data4, writeSheet1);
        }

        writeHandler.afterAll();
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void multiSheetLevelHandlerWrites(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        CountingWriteHandler writeHandler1 = CountingWriteHandler.builder()
                .withBeforeCellCreate(1L)
                .withAfterCellCreate(1L)
                .withAfterCellDataConverted(1L)
                .withAfterCellDispose(1L)
                .withBeforeRowCreate(1L)
                .withAfterRowCreate(1L)
                .withAfterRowDispose(1L)
                .withBeforeSheetCreate(1L)
                .withAfterSheetCreate(1L)
                .withAfterSheetDispose(1L)
                .withAfterSheetDisposeSheetNos(Collections.singletonList(0))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                // The data has not been fully written yet
                .withAfterWorkbookDispose(0L)
                .build();

        CountingWriteHandler writeHandler2 = CountingWriteHandler.builder()
                .withBeforeCellCreate(1L)
                .withAfterCellCreate(1L)
                .withAfterCellDataConverted(1L)
                .withAfterCellDispose(1L)
                .withBeforeRowCreate(1L)
                .withAfterRowCreate(1L)
                .withAfterRowDispose(1L)
                .withBeforeSheetCreate(1L)
                .withAfterSheetCreate(1L)
                .withAfterSheetDispose(1L)
                .withAfterSheetDisposeSheetNos(Collections.singletonList(1))
                .withBeforeWorkbookCreate(1L)
                .withAfterWorkbookCreate(1L)
                .withAfterWorkbookDispose(1L)
                .build();

        try (ExcelWriter writer = FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .build()) {

            WriteSheet writeSheet1 = FesodSheet.writerSheet(0)
                    .needHead(Boolean.TRUE)
                    .registerWriteHandler(writeHandler1)
                    .build();
            writer.write(TestDataBuilder.simpleData(1), writeSheet1);

            WriteSheet writeSheet2 = FesodSheet.writerSheet(1)
                    .needHead(Boolean.TRUE)
                    .registerWriteHandler(writeHandler2)
                    .build();
            writer.write(TestDataBuilder.simpleData(1), writeSheet2);
        }

        writeHandler1.afterAll();
        writeHandler2.afterAll();
    }
}

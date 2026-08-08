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

package org.apache.fesod.sheet.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.listener.PageReadListener;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test simple read/write for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class SimpleDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        List<SimpleData> data = TestDataBuilder.simpleData(10);
        List<SimpleData> result = writeAndRead(format, SimpleData.class, data);

        Assertions.assertEquals(10, result.size());
        Assertions.assertEquals("Name0", result.get(0).getName());
    }

    @ParameterizedTest
    @ExcelFormatSource(withApiMode = true)
    void readAndWriteWithApiMode(ExcelFormat format, ApiMode mode) throws Exception {
        File file = createTempFile(format);
        List<SimpleData> data = TestDataBuilder.simpleData(10);
        List<SimpleData> result = RoundTripHelper.writeAndRead(file, format, mode, SimpleData.class, data);

        Assertions.assertEquals(10, result.size());
        Assertions.assertEquals("Name0", result.get(0).getName());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void synchronousRead(ExcelFormat format) throws Exception {
        List<SimpleData> data = TestDataBuilder.simpleData(10);
        List<SimpleData> result = RoundTripHelper.writeAndReadSync(createTempFile(format), SimpleData.class, data);

        Assertions.assertEquals(10, result.size());
        Assertions.assertInstanceOf(SimpleData.class, result.get(0));
        Assertions.assertEquals("Name0", result.get(0).getName());
    }

    @Test
    void sheetNameRead07() {
        List<Map<Integer, Object>> list = FesodSheet.read(readFile("simple" + File.separator + "simple07.xlsx"))
                .sheet("simple")
                .doReadSync();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void sheetNoRead07() {
        List<Map<Integer, Object>> list = FesodSheet.read(readFile("simple" + File.separator + "simple07.xlsx"))
                .sheet(1)
                .doReadSync();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void pageReadListener07() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        List<SimpleData> data = TestDataBuilder.simpleData(10);
        RoundTripHelper.write(file, SimpleData.class, data);

        FesodSheet.read(
                        file,
                        SimpleData.class,
                        new PageReadListener<SimpleData>(
                                dataList -> {
                                    Assertions.assertEquals(5, dataList.size());
                                },
                                5))
                .sheet()
                .doRead();
    }

    @Test
    void pageReadListenerMultipleSheets07() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        try (ExcelWriter excelWriter = FesodSheet.write(file, SimpleData.class).build()) {
            excelWriter.write(
                    TestDataBuilder.simpleData(3, "First"),
                    FesodSheet.writerSheet(0, "sheet0").build());
            excelWriter.write(
                    TestDataBuilder.simpleData(4, "Second"),
                    FesodSheet.writerSheet(1, "sheet1").build());
        }

        List<SimpleData> allData = new ArrayList<>();
        FesodSheet.read(file, SimpleData.class, new PageReadListener<SimpleData>(allData::addAll, 5))
                .doReadAll();

        // Each row must be delivered exactly once, even when a sheet ends with a partially filled batch
        Assertions.assertEquals(
                Arrays.asList(
                        "FirstName0",
                        "FirstName1",
                        "FirstName2",
                        "SecondName0",
                        "SecondName1",
                        "SecondName2",
                        "SecondName3"),
                allData.stream().map(SimpleData::getName).collect(Collectors.toList()));
    }
}

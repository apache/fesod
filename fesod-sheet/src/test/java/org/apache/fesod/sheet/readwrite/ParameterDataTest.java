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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.cache.MapCache;
import org.apache.fesod.sheet.converters.string.StringStringConverter;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class ParameterDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("parameter", format);
        ExcelTypeEnum type = format.toExcelTypeEnum();
        readAndWrite1(file, type);
        readAndWrite2(file, type);
        readAndWrite3(file, type);
        readAndWrite4(file, type);
        readAndWrite5(file, type);
        readAndWrite6(file, type);
        readAndWrite7(file, type);
    }

    private void readAndWrite1(File file, ExcelTypeEnum type) {
        FesodSheet.write(file.getPath()).head(SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList =
                FesodSheet.read(file.getPath()).head(SimpleData.class).sheet().doReadSync();
        assertParameterData(dataList);
    }

    private void readAndWrite2(File file, ExcelTypeEnum type) {
        FesodSheet.write(file.getPath(), SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList = FesodSheet.read(
                        file.getPath(), SimpleData.class, new CollectingReadListener<SimpleData>())
                .sheet()
                .doReadSync();
        assertParameterData(dataList);
    }

    private void readAndWrite3(File file, ExcelTypeEnum type) throws Exception {
        FesodSheet.write(new FileOutputStream(file))
                .excelType(type)
                .head(SimpleData.class)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList =
                FesodSheet.read(file.getPath()).head(SimpleData.class).sheet().doReadSync();
        assertParameterData(dataList);
    }

    private void readAndWrite4(File file, ExcelTypeEnum type) throws Exception {
        FesodSheet.write(new FileOutputStream(file), SimpleData.class)
                .excelType(type)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList =
                FesodSheet.read(file.getPath()).head(SimpleData.class).sheet().doReadSync();
        assertParameterData(dataList);
    }

    private void readAndWrite5(File file, ExcelTypeEnum type) throws Exception {
        ExcelWriter excelWriter = FesodSheet.write(new FileOutputStream(file))
                .excelType(type)
                .head(SimpleData.class)
                .relativeHeadRowIndex(0)
                .build();
        WriteSheet writeSheet = FesodSheet.writerSheet(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.FALSE)
                .build();
        WriteTable writeTable = FesodSheet.writerTable(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.TRUE)
                .build();
        excelWriter.write(TestDataBuilder.simpleData(10), writeSheet, writeTable);
        excelWriter.finish();

        CollectingReadListener<SimpleData> listener1 = new CollectingReadListener<SimpleData>();
        ExcelReader excelReader = FesodSheet.read(file.getPath(), listener1)
                .head(SimpleData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        ReadSheet readSheet = FesodSheet.readSheet()
                .head(SimpleData.class)
                .use1904windowing(Boolean.FALSE)
                .headRowNumber(1)
                .sheetNo(0)
                .sheetName("0")
                .build();
        excelReader.read(readSheet);
        excelReader.finish();
        assertParameterData(listener1.getRows());

        CollectingReadListener<SimpleData> listener2 = new CollectingReadListener<SimpleData>();
        excelReader = FesodSheet.read(file.getPath(), listener2)
                .head(SimpleData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        excelReader.readAll();
        excelReader.finish();
        assertParameterData(listener2.getRows());
    }

    private void readAndWrite6(File file, ExcelTypeEnum type) throws Exception {
        ExcelWriter excelWriter = FesodSheet.write(new FileOutputStream(file))
                .excelType(type)
                .head(SimpleData.class)
                .relativeHeadRowIndex(0)
                .build();
        WriteSheet writeSheet = FesodSheet.writerSheet(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.FALSE)
                .build();
        WriteTable writeTable = FesodSheet.writerTable(0)
                .registerConverter(new StringStringConverter())
                .relativeHeadRowIndex(0)
                .needHead(Boolean.TRUE)
                .build();
        excelWriter.write(TestDataBuilder.simpleData(10), writeSheet, writeTable);
        excelWriter.finish();

        CollectingReadListener<SimpleData> listener1 = new CollectingReadListener<SimpleData>();
        ExcelReader excelReader = FesodSheet.read(file.getPath(), listener1)
                .head(SimpleData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        ReadSheet readSheet = FesodSheet.readSheet("0")
                .head(SimpleData.class)
                .use1904windowing(Boolean.FALSE)
                .headRowNumber(1)
                .sheetNo(0)
                .build();
        excelReader.read(readSheet);
        excelReader.finish();
        assertParameterData(listener1.getRows());

        CollectingReadListener<SimpleData> listener2 = new CollectingReadListener<SimpleData>();
        excelReader = FesodSheet.read(file.getPath(), listener2)
                .head(SimpleData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        excelReader.readAll();
        excelReader.finish();
        assertParameterData(listener2.getRows());
    }

    private void readAndWrite7(File file, ExcelTypeEnum type) {
        FesodSheet.write(file, SimpleData.class)
                .registerConverter(new StringStringConverter())
                .sheet()
                .registerConverter(new StringStringConverter())
                .needHead(Boolean.FALSE)
                .table(0)
                .needHead(Boolean.TRUE)
                .doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList = FesodSheet.read(file.getPath())
                .head(SimpleData.class)
                .sheet()
                .registerConverter(new StringStringConverter())
                .doReadSync();
        assertParameterData(dataList);
    }

    private void assertParameterData(List<SimpleData> dataList) {
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertEquals("Name0", dataList.get(0).getName());
    }
}

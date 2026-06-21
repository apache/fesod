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

package org.apache.fesod.sheet.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for column include/exclude filtering during read and write.
 */
@Tag(Tags.ROUND_TRIP)
public class ExcludeOrIncludeDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void excludeIndex(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Set<Integer> excludeColumnIndexes = new HashSet<Integer>();
        excludeColumnIndexes.add(0);
        excludeColumnIndexes.add(3);
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .excludeColumnIndexes(excludeColumnIndexes)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(2, record.size());
        assertEquals("column2", record.get(0));
        assertEquals("column3", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void excludeFieldName(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Set<String> excludeColumnFieldNames = new HashSet<String>();
        excludeColumnFieldNames.add("column1");
        excludeColumnFieldNames.add("column3");
        excludeColumnFieldNames.add("column4");
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .excludeColumnFieldNames(excludeColumnFieldNames)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(1, record.size());
        assertEquals("column2", record.get(0));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeIndex(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Set<Integer> includeColumnIndexes = new HashSet<Integer>();
        includeColumnIndexes.add(1);
        includeColumnIndexes.add(2);
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .includeColumnIndexes(includeColumnIndexes)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(2, record.size());
        assertEquals("column2", record.get(0));
        assertEquals("column3", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeFieldName(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Set<String> includeColumnFieldNames = new HashSet<String>();
        includeColumnFieldNames.add("column2");
        includeColumnFieldNames.add("column3");
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .sheet()
                .includeColumnFieldNames(includeColumnFieldNames)
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(2, record.size());
        assertEquals("column2", record.get(0));
        assertEquals("column3", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeFieldNameOrderIndex(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<Integer> includeColumnIndexes = new ArrayList<>();
        includeColumnIndexes.add(3);
        includeColumnIndexes.add(1);
        includeColumnIndexes.add(2);
        includeColumnIndexes.add(0);
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .includeColumnIndexes(includeColumnIndexes)
                .orderByIncludeColumn(true)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(4, record.size());
        assertEquals("column4", record.get(0));
        assertEquals("column2", record.get(1));
        assertEquals("column3", record.get(2));
        assertEquals("column1", record.get(3));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeFieldNameOrder(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<String> includeColumnFieldNames = new ArrayList<>();
        includeColumnFieldNames.add("column4");
        includeColumnFieldNames.add("column2");
        includeColumnFieldNames.add("column3");
        FesodSheet.write(file, ExcludeOrIncludeData.class)
                .includeColumnFieldNames(includeColumnFieldNames)
                .orderByIncludeColumn(true)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        assertEquals(3, record.size());
        assertEquals("column4", record.get(0));
        assertEquals("column2", record.get(1));
        assertEquals("column3", record.get(2));
    }

    private List<ExcludeOrIncludeData> data() {
        return TestDataBuilder.excludeOrIncludeData(1);
    }
}

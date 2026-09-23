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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests column include/exclude for field names that start with a lower case letter followed by an
 * upper case letter, for example {@code xRealIp} or {@code pName}.
 *
 * <p>The name kept by the internal cglib bean map of such a field is not the Java field name, so
 * filtering by field name must not be based on that name.
 */
@Tag(Tags.ROUND_TRIP)
class CamelCaseFieldDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void writeAllCamelCaseFields(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class).sheet().doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(4, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("xRealIp1", record.get(1));
        Assertions.assertEquals("pName1", record.get(2));
        Assertions.assertEquals("attackType1", record.get(3));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void writeHeadOfCamelCaseFields(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class).sheet().doWrite(data());
        List<Map<Integer, String>> dataMap =
                FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();
        Assertions.assertEquals(2, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(4, record.size());
        Assertions.assertEquals("name", record.get(0));
        Assertions.assertEquals("xRealIp", record.get(1));
        Assertions.assertEquals("pName", record.get(2));
        Assertions.assertEquals("attackType", record.get(3));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeColumnFieldNames(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class)
                .includeColumnFieldNames(Arrays.asList("name", "xRealIp", "pName", "attackType"))
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(4, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("xRealIp1", record.get(1));
        Assertions.assertEquals("pName1", record.get(2));
        Assertions.assertEquals("attackType1", record.get(3));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includePartOfCamelCaseFieldNames(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class)
                .includeColumnFieldNames(Arrays.asList("name", "xRealIp"))
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(2, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("xRealIp1", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void excludeCamelCaseFieldNames(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class)
                .excludeColumnFieldNames(Arrays.asList("xRealIp", "attackType"))
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(2, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("pName1", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeUnknownColumnFieldName(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class)
                .includeColumnFieldNames(Arrays.asList("name", "notExist", "pName"))
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(2, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("pName1", record.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includeUnknownColumnFieldNameOrderByIncludeColumn(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CamelCaseFieldData.class)
                .includeColumnFieldNames(Arrays.asList("name", "notExist", "pName"))
                .orderByIncludeColumn(true)
                .sheet()
                .doWrite(data());
        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals(2, record.size());
        Assertions.assertEquals("name1", record.get(0));
        Assertions.assertEquals("pName1", record.get(1));
    }

    private List<CamelCaseFieldData> data() {
        List<CamelCaseFieldData> list = new ArrayList<CamelCaseFieldData>();
        CamelCaseFieldData data = new CamelCaseFieldData();
        data.setName("name1");
        data.setXRealIp("xRealIp1");
        data.setPName("pName1");
        data.setAttackType("attackType1");
        list.add(data);
        return list;
    }
}

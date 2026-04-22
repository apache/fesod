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

package org.apache.fesod.sheet.style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.fesod.sheet.write.merge.LoopMergeStrategy;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.fill.FillWrapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test fill operations for all Excel formats using parameterized tests.
 */
public class FillDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @MethodSource("allFormats")
    void simpleFill(ExcelFormat format) throws Exception {
        File file = createTempFile("fill", format);
        File template = TestFileUtil.readFile("fill" + File.separator + "simple" + format.getExtension());
        if (format == ExcelFormat.CSV) {
            ExcelGenerateException ex = assertThrows(ExcelGenerateException.class, () -> fill(file, template));
            assertEquals("csv cannot use template.", ex.getMessage());
        } else {
            fill(file, template);
        }
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void complexFill(ExcelFormat format) throws Exception {
        Assumptions.assumeTrue(format.supportsTemplates());
        File file = createTempFile("fillComplex", format);
        File template = TestFileUtil.readFile("fill" + File.separator + "complex" + format.getExtension());
        complexFillImpl(file, template);
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void horizontalFill(ExcelFormat format) throws Exception {
        Assumptions.assumeTrue(format.supportsTemplates());
        File file = createTempFile("fillHorizontal", format);
        File template = TestFileUtil.readFile("fill" + File.separator + "horizontal" + format.getExtension());
        horizontalFillImpl(file, template);
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void byNameFill(ExcelFormat format) throws Exception {
        Assumptions.assumeTrue(format.supportsTemplates());
        File file = createTempFile("byName", format);
        File template = TestFileUtil.readFile("fill" + File.separator + "byName" + format.getExtension());
        byNameFillImpl(file, template);
    }

    @ParameterizedTest
    @MethodSource("binaryFormats")
    void compositeFill(ExcelFormat format) throws Exception {
        Assumptions.assumeTrue(format.supportsTemplates());
        File file = createTempFile("composite", format);
        File template = TestFileUtil.readFile("fill" + File.separator + "composite" + format.getExtension());
        compositeFillImpl(file, template);
    }

    private void byNameFillImpl(File file, File template) {
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FesodSheet.write(file, FillData.class)
                .withTemplate(template)
                .sheet("Sheet2")
                .doFill(fillData);
    }

    @SuppressWarnings("unchecked")
    private void compositeFillImpl(File file, File template) {
        try (ExcelWriter excelWriter =
                FesodSheet.write(file).withTemplate(template).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet().build();

            FillConfig fillConfig = FillConfig.builder()
                    .direction(WriteDirectionEnum.HORIZONTAL)
                    .build();
            excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", "2019-10-09 13:28:28");
            excelWriter.fill(map, writeSheet);
        }

        List<Object> list = FesodSheet.read(file)
                .ignoreEmptyRow(false)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        Map<String, String> map0 = (Map<String, String>) list.get(0);
        assertEquals("Zhang San", map0.get(21));
        Map<String, String> map27 = (Map<String, String>) list.get(27);
        assertEquals("Zhang San", map27.get(0));
        Map<String, String> map29 = (Map<String, String>) list.get(29);
        assertEquals("Zhang San", map29.get(3));
    }

    @SuppressWarnings("unchecked")
    private void horizontalFillImpl(File file, File template) {
        try (ExcelWriter excelWriter =
                FesodSheet.write(file).withTemplate(template).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder()
                    .direction(WriteDirectionEnum.HORIZONTAL)
                    .build();
            excelWriter.fill(data(), fillConfig, writeSheet);
            excelWriter.fill(data(), fillConfig, writeSheet);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", "2019-10-09 13:28:28");
            excelWriter.fill(map, writeSheet);
            excelWriter.finish();
        }

        List<Object> list = FesodSheet.read(file).sheet().headRowNumber(0).doReadSync();
        assertEquals(5L, list.size());
        Map<String, String> map0 = (Map<String, String>) list.get(0);
        assertEquals("Zhang San", map0.get(2));
    }

    @SuppressWarnings("unchecked")
    private void complexFillImpl(File file, File template) {
        try (ExcelWriter excelWriter =
                FesodSheet.write(file).withTemplate(template).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet()
                    .registerWriteHandler(new LoopMergeStrategy(2, 0))
                    .build();
            FillConfig fillConfig =
                    FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(data(), fillConfig, writeSheet);
            excelWriter.fill(data(), fillConfig, writeSheet);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", "2019-10-09 13:28:28");
            map.put("total", 1000);
            excelWriter.fill(map, writeSheet);
        }
        List<Object> list = FesodSheet.read(file).sheet().headRowNumber(3).doReadSync();
        assertEquals(21L, list.size());
        Map<String, String> map19 = (Map<String, String>) list.get(19);
        assertEquals("Zhang San", map19.get(0));
    }

    private void fill(File file, File template) {
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FesodSheet.write(file, FillData.class).withTemplate(template).sheet().doFill(fillData);
    }

    private List<FillData> data() {
        return TestDataBuilder.fillData(10);
    }
}

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

package org.apache.fesod.sheet.head;

import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test complex head write/read for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class ComplexHeadDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("complexHead", format);
        FesodSheet.write(file, ComplexHeadData.class).sheet().doWrite(TestDataBuilder.complexHeadData(1));
        List<ComplexHeadData> result = FesodSheet.read(file)
                .head(ComplexHeadData.class)
                .xlsxSAXParserFactoryName("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl")
                .sheet()
                .doReadSync();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("String4", result.get(0).getString4());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteAutomaticMergeHead(ExcelFormat format) throws Exception {
        File file = createTempFile("complexHeadAutoMerge", format);
        FesodSheet.write(file, ComplexHeadData.class)
                .automaticMergeHead(Boolean.FALSE)
                .sheet()
                .doWrite(TestDataBuilder.complexHeadData(1));
        List<ComplexHeadData> result =
                FesodSheet.read(file).head(ComplexHeadData.class).sheet().doReadSync();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("String4", result.get(0).getString4());
    }
}

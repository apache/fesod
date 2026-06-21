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
import java.util.Collections;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
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
}

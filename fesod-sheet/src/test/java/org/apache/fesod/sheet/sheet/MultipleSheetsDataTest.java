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

package org.apache.fesod.sheet.sheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.TitleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class MultipleSheetsDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void read(ExcelFormat format) {
        File file = readFile("multiplesheets" + File.separator + "multiplesheets" + format.getExtension());
        CollectingReadListener<TitleData> listener = new CollectingReadListener<>();
        try (ExcelReader excelReader =
                FesodSheet.read(file, TitleData.class, listener).build()) {
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            int count = 1;
            for (ReadSheet readSheet : sheets) {
                excelReader.read(readSheet);
                assertEquals(count, listener.getRowCount());
                count++;
            }
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readAll(ExcelFormat format) {
        File file = readFile("multiplesheets" + File.separator + "multiplesheets" + format.getExtension());
        FesodSheet.read(file, TitleData.class, new CollectingReadListener<TitleData>())
                .doReadAll();
    }
}

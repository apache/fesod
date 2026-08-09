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
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
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
public class RepetitionDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<RepetitionData> data = TestDataBuilder.repetitionData(1);
        try (ExcelWriter excelWriter =
                FesodSheet.write(file, RepetitionData.class).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet(0).build();
            excelWriter.write(data, writeSheet).write(data, writeSheet);
        }

        CollectingReadListener<RepetitionData> listener = new CollectingReadListener<>();
        try (ExcelReader excelReader =
                FesodSheet.read(file, RepetitionData.class, listener).build()) {
            ReadSheet readSheet = FesodSheet.readSheet(0).build();
            excelReader.read(readSheet);
        }

        Assertions.assertEquals(2, listener.getRowCount());
        Assertions.assertEquals("String0", listener.getRows().get(0).getString());
        Assertions.assertEquals("String0", listener.getRows().get(1).getString());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteTable(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<RepetitionData> data = TestDataBuilder.repetitionData(1);
        try (ExcelWriter excelWriter =
                FesodSheet.write(file, RepetitionData.class).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet(0).build();
            WriteTable writeTable =
                    FesodSheet.writerTable(0).relativeHeadRowIndex(0).build();
            excelWriter.write(data, writeSheet, writeTable).write(data, writeSheet, writeTable);
        }

        CollectingReadListener<RepetitionData> listener = new CollectingReadListener<>();
        try (ExcelReader excelReader =
                FesodSheet.read(file, RepetitionData.class, listener).build()) {
            ReadSheet readSheet = FesodSheet.readSheet(0).headRowNumber(2).build();
            excelReader.read(readSheet);
        }

        Assertions.assertEquals(2, listener.getRowCount());
        Assertions.assertEquals("String0", listener.getRows().get(0).getString());
        Assertions.assertEquals("String0", listener.getRows().get(1).getString());
    }
}

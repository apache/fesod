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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.event.SyncReadListener;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class SkipDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        readAndWriteImpl(file);
    }

    @Test
    void readAndWriteCsvThrows() throws Exception {
        File file = createTempFile(ExcelFormat.CSV);
        assertThrows(ExcelGenerateException.class, () -> readAndWriteImpl(file));
    }

    private void readAndWriteImpl(File file) {
        try (ExcelWriter excelWriter = FesodSheet.write(file, SimpleData.class).build()) {
            WriteSheet writeSheet0 = FesodSheet.writerSheet(0, "Sheet1").build();
            WriteSheet writeSheet1 = FesodSheet.writerSheet(1, "Sheet2").build();
            WriteSheet writeSheet2 = FesodSheet.writerSheet(2, "Sheet3").build();
            WriteSheet writeSheet3 = FesodSheet.writerSheet(3, "Sheet4").build();
            excelWriter.write(data("name1"), writeSheet0);
            excelWriter.write(data("name2"), writeSheet1);
            excelWriter.write(data("name3"), writeSheet2);
            excelWriter.write(data("name4"), writeSheet3);
        }

        List<SimpleData> list =
                FesodSheet.read(file, SimpleData.class, null).sheet("Sheet2").doReadSync();
        assertEquals(1, list.size());
        assertEquals("name2", list.get(0).getName());

        SyncReadListener syncReadListener = new SyncReadListener();
        try (ExcelReader excelReader = FesodSheet.read(file, SimpleData.class, null)
                .registerReadListener(syncReadListener)
                .build()) {
            ReadSheet readSheet1 = FesodSheet.readSheet("Sheet2").build();
            ReadSheet readSheet3 = FesodSheet.readSheet("Sheet4").build();
            excelReader.read(readSheet1, readSheet3);
            List<Object> syncList = syncReadListener.getList();
            assertEquals(2, syncList.size());
            assertEquals("name2", ((SimpleData) syncList.get(0)).getName());
            assertEquals("name4", ((SimpleData) syncList.get(1)).getName());
        }
    }

    private List<SimpleData> data(String name) {
        List<SimpleData> list = TestDataBuilder.simpleData(1);
        list.get(0).setName(name);
        return list;
    }
}

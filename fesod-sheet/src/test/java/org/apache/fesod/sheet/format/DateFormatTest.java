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

package org.apache.fesod.sheet.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for date formatting patterns and round-trip date value preservation.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.FORMAT)
public class DateFormatTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readCn(ExcelFormat format) {
        File file = readFile("dataformat" + File.separator + "dataformat" + format.getExtension());
        List<DateFormatData> list = FesodSheet.read(file, DateFormatData.class, null)
                .locale(Locale.CHINA)
                .sheet()
                .doReadSync();
        for (DateFormatData data : list) {
            assertTrue(Objects.equals(data.getDateStringCn(), data.getDate())
                    || Objects.equals(data.getDateStringCn2(), data.getDate()));
            assertEquals(data.getNumberStringCn(), data.getNumber());
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readUs(ExcelFormat format) {
        File file = readFile("dataformat" + File.separator + "dataformat" + format.getExtension());
        List<DateFormatData> list = FesodSheet.read(file, DateFormatData.class, null)
                .locale(Locale.US)
                .sheet()
                .doReadSync();
        for (DateFormatData data : list) {
            assertEquals(data.getDateStringUs(), data.getDate());
            assertEquals(data.getNumberStringUs(), data.getNumber());
        }
    }

    @Test
    void readV2() {
        File file07V2 = readFile("dataformat" + File.separator + "dataformatv2.xlsx");
        List<Map<Integer, String>> dataMap =
                FesodSheet.read(file07V2).headRowNumber(0).doReadAllSync();
        assertEquals("15:00", dataMap.get(0).get(0));
        assertEquals("2023-1-01 00:00:00", dataMap.get(1).get(0));
        assertEquals("2023-1-01 00:00:00", dataMap.get(2).get(0));
        assertEquals("2023-1-01 00:00:01", dataMap.get(3).get(0));
        assertEquals("2023-1-01 00:00:00", dataMap.get(4).get(0));
        assertEquals("2023-1-01 00:00:00", dataMap.get(5).get(0));
        assertEquals("2023-1-01 00:00:01", dataMap.get(6).get(0));
    }
}

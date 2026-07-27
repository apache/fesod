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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.ReadDefaultReturnEnum;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class NoModelDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        boolean isCsv = (format == ExcelFormat.CSV);
        File file = createTempFile(format);
        File fileRepeat = createTempFile("repeat", format);

        FesodSheet.write(file).sheet().doWrite(TestDataBuilder.noModelData(10));
        List<Map<Integer, String>> result =
                FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();
        Assertions.assertEquals(10, result.size());
        Map<Integer, String> data10 = result.get(9);
        Assertions.assertEquals("string19", data10.get(0));
        Assertions.assertEquals("109", data10.get(1));
        Assertions.assertEquals("2020-01-01 01:01:01", data10.get(2));

        List<Map<Integer, Object>> actualDataList = FesodSheet.read(file)
                .headRowNumber(0)
                .readDefaultReturn(ReadDefaultReturnEnum.ACTUAL_DATA)
                .sheet()
                .doReadSync();
        Assertions.assertEquals(10, actualDataList.size());
        Map<Integer, Object> actualData10 = actualDataList.get(9);
        Assertions.assertEquals("string19", actualData10.get(0));
        if (isCsv) {
            Assertions.assertEquals("109", actualData10.get(1));
            Assertions.assertEquals("2020-01-01 01:01:01", actualData10.get(2));
        } else {
            Assertions.assertEquals(0, new BigDecimal("109").compareTo((BigDecimal) actualData10.get(1)));
            Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1, 1), actualData10.get(2));
        }

        List<Map<Integer, ReadCellData<?>>> readCellDataList = FesodSheet.read(file)
                .headRowNumber(0)
                .readDefaultReturn(ReadDefaultReturnEnum.READ_CELL_DATA)
                .sheet()
                .doReadSync();
        Assertions.assertEquals(10, readCellDataList.size());
        Map<Integer, ReadCellData<?>> readCellData10 = readCellDataList.get(9);
        Assertions.assertEquals("string19", readCellData10.get(0).getData());
        if (isCsv) {
            Assertions.assertEquals("109", readCellData10.get(1).getData());
            Assertions.assertEquals("2020-01-01 01:01:01", readCellData10.get(2).getData());
        } else {
            Assertions.assertEquals(0, new BigDecimal("109").compareTo((BigDecimal)
                            readCellData10.get(1).getData()));
            Assertions.assertEquals(
                    LocalDateTime.of(2020, 1, 1, 1, 1, 1), readCellData10.get(2).getData());
        }

        FesodSheet.write(fileRepeat).sheet().doWrite(result);
        result = FesodSheet.read(fileRepeat).headRowNumber(0).sheet().doReadSync();
        Assertions.assertEquals(10, result.size());
        data10 = result.get(9);
        Assertions.assertEquals("string19", data10.get(0));
        Assertions.assertEquals("109", data10.get(1));
        Assertions.assertEquals("2020-01-01 01:01:01", data10.get(2));
    }
}

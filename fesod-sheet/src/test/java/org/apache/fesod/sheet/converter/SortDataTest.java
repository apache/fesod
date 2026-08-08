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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for column sorting/reordering via @ExcelProperty index.
 */
@Tag(Tags.ROUND_TRIP)
public class SortDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, SortData.class).sheet().doWrite(data());

        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals("column1", record.get(0));
        Assertions.assertEquals("column2", record.get(1));
        Assertions.assertEquals("column3", record.get(2));
        Assertions.assertEquals("column4", record.get(3));
        Assertions.assertEquals("column5", record.get(4));
        Assertions.assertEquals("column6", record.get(5));

        CollectingReadListener<SortData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SortData.class, listener).sheet().doRead();
        Assertions.assertEquals(1, listener.getRowCount());
        SortData sortData = listener.getFirstRow();
        Assertions.assertEquals("column1", sortData.getColumn1());
        Assertions.assertEquals("column2", sortData.getColumn2());
        Assertions.assertEquals("column3", sortData.getColumn3());
        Assertions.assertEquals("column4", sortData.getColumn4());
        Assertions.assertEquals("column5", sortData.getColumn5());
        Assertions.assertEquals("column6", sortData.getColumn6());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteNoHead(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file).head(head()).sheet().doWrite(data());

        List<Map<Integer, String>> dataMap = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals("column1", record.get(0));
        Assertions.assertEquals("column2", record.get(1));
        Assertions.assertEquals("column3", record.get(2));
        Assertions.assertEquals("column4", record.get(3));
        Assertions.assertEquals("column5", record.get(4));
        Assertions.assertEquals("column6", record.get(5));

        CollectingReadListener<SortData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SortData.class, listener).sheet().doRead();
        Assertions.assertEquals(1, listener.getRowCount());
        SortData sortData = listener.getFirstRow();
        Assertions.assertEquals("column1", sortData.getColumn1());
        Assertions.assertEquals("column2", sortData.getColumn2());
        Assertions.assertEquals("column3", sortData.getColumn3());
        Assertions.assertEquals("column4", sortData.getColumn4());
        Assertions.assertEquals("column5", sortData.getColumn5());
        Assertions.assertEquals("column6", sortData.getColumn6());
    }

    private List<List<String>> head() {
        List<List<String>> head = new ArrayList<List<String>>();
        head.add(Collections.singletonList("column1"));
        head.add(Collections.singletonList("column2"));
        head.add(Collections.singletonList("column3"));
        head.add(Collections.singletonList("column4"));
        head.add(Collections.singletonList("column5"));
        head.add(Collections.singletonList("column6"));
        return head;
    }

    private List<SortData> data() {
        return TestDataBuilder.sortData(1);
    }
}

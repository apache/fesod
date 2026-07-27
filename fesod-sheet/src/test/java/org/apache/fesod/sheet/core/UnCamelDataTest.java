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
import java.util.ArrayList;
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
 *
 */
@Tag(Tags.ROUND_TRIP)
public class UnCamelDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, UnCamelData.class).sheet().doWrite(TestDataBuilder.unCamelData(10));

        CollectingReadListener<UnCamelData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, UnCamelData.class, listener).sheet().doRead();

        Assertions.assertEquals(10, listener.getRowCount());
        UnCamelData row = listener.getFirstRow();
        Assertions.assertEquals("string1", row.getString1());
        Assertions.assertEquals("string2", row.getString2());
        Assertions.assertEquals("string3", row.getSTring3());
        Assertions.assertEquals("string4", row.getSTring4());
        Assertions.assertEquals("string5", row.getSTRING5());
        Assertions.assertEquals("string6", row.getSTRing6());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteHeadMap(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, UnCamelData.class).sheet().doWrite(TestDataBuilder.unCamelData(10));

        List<Map<Integer, String>> headMaps = new ArrayList<>();
        FesodSheet.read(file, UnCamelData.class, new CollectingReadListener<UnCamelData>() {
                    @Override
                    public void invokeHeadMap(
                            Map<Integer, String> headMap, org.apache.fesod.sheet.context.AnalysisContext context) {
                        headMaps.add(headMap);
                    }
                })
                .sheet()
                .doRead();

        Assertions.assertEquals(1, headMaps.size());
        Map<Integer, String> headMap = headMaps.get(0);
        Assertions.assertEquals("string1", headMap.get(0));
        Assertions.assertEquals("string2", headMap.get(1));
        Assertions.assertEquals("STring3", headMap.get(2));
        Assertions.assertEquals("STring4", headMap.get(3));
        Assertions.assertEquals("STRING5", headMap.get(4));
        Assertions.assertEquals("STRing6", headMap.get(5));
    }
}

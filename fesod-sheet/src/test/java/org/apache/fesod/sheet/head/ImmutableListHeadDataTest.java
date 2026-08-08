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

package org.apache.fesod.sheet.head;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test immutable list head write/read for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class ImmutableListHeadDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("listHead", format);
        FesodSheet.write(file)
                .head(head())
                .registerWriteHandler(new ImmutableListHeadDataWriteHandler())
                .sheet()
                .doWrite(data());

        List<Map<Integer, String>> list =
                FesodSheet.read(file).head(head()).sheet().doReadSync();
        Assertions.assertEquals(1, list.size());
        Map<Integer, String> row = list.get(0);
        Assertions.assertEquals("stringData", row.get(0));
        Assertions.assertEquals("1", row.get(1));
        Assertions.assertEquals("2025-10-31 01:01:01", row.get(2));
        Assertions.assertEquals("extraData", row.get(3));
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = Arrays.asList("stringTitle");
        List<String> head1 = new ArrayList<String>();
        head1.add("numberTitle1");
        head1.add("numberTitle2");

        list.add(head0);
        list.add(Collections.unmodifiableList(head1));
        list.add(Collections.singletonList("datetimeTitle"));
        return list;
    }

    private List<List<Object>> data() throws ParseException {
        List<List<Object>> list = new ArrayList<List<Object>>();
        List<Object> data0 = new ArrayList<Object>();
        data0.add("stringData");
        data0.add(1);
        data0.add(DateUtils.parseDate("2025-10-31 01:01:01"));
        data0.add("extraData");
        list.add(data0);
        return list;
    }
}

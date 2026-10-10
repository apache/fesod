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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * No-model reads must return rows that are exactly as wide as the head that was supplied for them.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.READ)
public class NoModelHeadSizeTest extends AbstractExcelTest {

    @Test
    public void readWithExplicitHeadReturnsExactlyHeadWidth() throws Exception {
        File file = createTempFile("noModelHeadSize", ExcelFormat.XLSX);

        FesodSheet.write(file).head(head()).sheet().doWrite(fullRow());

        List<Map<Integer, String>> list =
                FesodSheet.read(file).head(head()).sheet().doReadSync();
        Assertions.assertEquals(1, list.size());
        Map<Integer, String> row = list.get(0);
        Assertions.assertEquals(4, row.size(), "row must be exactly as wide as the explicit head");
        Assertions.assertEquals("stringData", row.get(0));
        Assertions.assertEquals("1", row.get(1));
        Assertions.assertEquals("true", row.get(2));
        Assertions.assertEquals("2020-01-01 01:01:01", row.get(3));
        Assertions.assertFalse(row.containsKey(4), "no phantom column may follow the head width");
    }

    @Test
    public void readWithShortDataRowPadsToHeadWidthNotBeyond() throws Exception {
        File file = createTempFile("noModelHeadSizeShort", ExcelFormat.XLSX);

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("stringData", 1));
        FesodSheet.write(file).head(head()).sheet().doWrite(data);

        List<Map<Integer, String>> list =
                FesodSheet.read(file).head(head()).sheet().doReadSync();
        Assertions.assertEquals(1, list.size());
        Map<Integer, String> row = list.get(0);
        Assertions.assertEquals(4, row.size(), "a short row must be padded to the head width");
        Assertions.assertEquals("stringData", row.get(0));
        Assertions.assertEquals("1", row.get(1));
        Assertions.assertNull(row.get(2));
        Assertions.assertNull(row.get(3));
        Assertions.assertFalse(row.containsKey(4), "no phantom column may follow the head width");
    }

    @Test
    public void readWithoutExplicitHeadStillPadsToWidestHeadRow() throws Exception {
        File file = createTempFile("noModelHeadSizeImplicit", ExcelFormat.XLSX);

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("title0", "title1", "title2"));
        data.add(Arrays.asList("value0"));
        FesodSheet.write(file).sheet().doWrite(data);

        List<Map<Integer, String>> list = FesodSheet.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, list.size());
        Map<Integer, String> row = list.get(0);
        Assertions.assertEquals(3, row.size(), "headless rows keep being padded to the widest head row");
        Assertions.assertEquals("value0", row.get(0));
        Assertions.assertNull(row.get(1));
        Assertions.assertNull(row.get(2));
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<>();
        list.add(Collections.singletonList("stringTitle"));
        list.add(Collections.singletonList("numberTitle"));
        list.add(Collections.singletonList("booleanTitle"));
        list.add(Collections.singletonList("dateTitle"));
        return list;
    }

    private List<List<Object>> fullRow() {
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("stringData", 1, Boolean.TRUE, "2020-01-01 01:01:01"));
        return data;
    }
}

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
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * A head column with an empty name list is a legal head shape ({@code Head} stores it as an unnamed column, the same
 * shape a missing {@code @ExcelProperty} value produces), so writing must leave that column's header cells empty
 * instead of crashing.
 */
@Tag(Tags.WRITE)
public class EmptyHeadColumnWriteTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void writeWithEmptyHeadColumnNameList(ExcelFormat format) throws Exception {
        File file = createTempFile("emptyHeadCol", format);
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("first"));
        head.add(new ArrayList<>());
        head.add(Arrays.asList("third"));
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("v1", "v2", "v3"));

        FesodSheet.write(file).head(head).sheet().doWrite(data);

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).headRowNumber(1).sheet().doReadSync();
        Assertions.assertEquals(1, rows.size(), "the data row survives");
        Assertions.assertEquals("v1", rows.get(0).get(0), "data column 0 keeps its value");
        Assertions.assertEquals("v2", rows.get(0).get(1), "data column 1 keeps its value");
        Assertions.assertEquals("v3", rows.get(0).get(2), "data column 2 keeps its value");
    }
}

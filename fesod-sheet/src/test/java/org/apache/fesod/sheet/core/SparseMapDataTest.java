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

package org.apache.fesod.sheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(Tags.ROUND_TRIP)
class SparseMapDataTest extends AbstractExcelTest {

    @ParameterizedTest(name = "{0}, head={1}, inMemory={2}")
    @MethodSource("writeCases")
    void preservesSparseMapValues(ExcelFormat format, boolean withHead, boolean inMemory) throws Exception {
        List<Map<Integer, String>> rows = Arrays.asList(
                row(0, "FIRST", 2, "THIRD"),
                row(2, "THIRD"),
                row(4, "FIFTH", 1, "SECOND"),
                row(2, "THIRD", 0, "FIRST"),
                row(0, "FIRST", 1, null, 2, "THIRD"),
                row(0, "FIRST", 2, null),
                row(0, "FIRST", 1, "SECOND", 2, "THIRD"));
        File file = createTempFile(format);
        ExcelWriterBuilder writer =
                FesodSheet.write(file).charset(StandardCharsets.UTF_8).inMemory(inMemory);
        if (withHead) {
            writer.head(Arrays.asList(
                    Collections.singletonList("First"),
                    Collections.singletonList("Second"),
                    Collections.singletonList("Third")));
        }
        writer.sheet().doWrite(rows);

        List<Map<Integer, String>> actual = FesodSheet.read(file)
                .charset(StandardCharsets.UTF_8)
                .headRowNumber(withHead ? 1 : 0)
                .sheet()
                .doReadSync();
        assertEquals(rows.size(), actual.size());
        for (int i = 0; i < rows.size(); i++) {
            assertEquals(nonBlankValues(rows.get(i)), nonBlankValues(actual.get(i)), "Row " + i);
        }
    }

    @ParameterizedTest
    @ExcelFormatSource
    void preservesIndexedHeadMapping(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Map<Integer, String> sparse = row(0, "FIRST", 2, "THIRD");
        List<String> collection = Arrays.asList("FIRST", null, "THIRD");
        FesodSheet.write(file)
                .charset(StandardCharsets.UTF_8)
                .head(IndexedHead.class)
                .sheet()
                .doWrite(Arrays.asList(sparse, collection));

        List<Map<Integer, String>> actual = FesodSheet.read(file)
                .charset(StandardCharsets.UTF_8)
                .headRowNumber(1)
                .sheet()
                .doReadSync();
        Map<Integer, String> expected = row(1, "FIRST", 4, "THIRD");
        assertEquals(2, actual.size());
        assertEquals(expected, nonBlankValues(actual.get(0)));
        assertEquals(expected, nonBlankValues(actual.get(1)));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void preservesCollectionRows(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file)
                .charset(StandardCharsets.UTF_8)
                .sheet()
                .doWrite(Collections.singletonList(Arrays.asList("FIRST", null, "THIRD")));
        List<Map<Integer, String>> actual = FesodSheet.read(file)
                .charset(StandardCharsets.UTF_8)
                .headRowNumber(0)
                .sheet()
                .doReadSync();
        assertEquals(1, actual.size());
        assertEquals(row(0, "FIRST", 2, "THIRD"), nonBlankValues(actual.get(0)));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void includesValuesAddedByRowHandlers(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        Map<Integer, String> row = row(0, "FIRST");
        FesodSheet.write(file)
                .charset(StandardCharsets.UTF_8)
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowCreate(RowWriteHandlerContext context) {
                        row.put(2, "THIRD");
                    }
                })
                .sheet()
                .doWrite(Collections.singletonList(row));
        List<Map<Integer, String>> actual = FesodSheet.read(file)
                .charset(StandardCharsets.UTF_8)
                .headRowNumber(0)
                .sheet()
                .doReadSync();
        assertEquals(1, actual.size());
        assertEquals(row, nonBlankValues(actual.get(0)));
    }

    private Stream<Arguments> writeCases() {
        List<Arguments> cases = new ArrayList<>();
        for (ExcelFormat format : ExcelFormat.values()) {
            for (boolean withHead : new boolean[] {false, true}) {
                for (boolean inMemory : new boolean[] {false, true}) {
                    cases.add(Arguments.of(format, withHead, inMemory));
                }
            }
        }
        return cases.stream();
    }

    private static Map<Integer, String> row(Object... entries) {
        Map<Integer, String> row = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            row.put((Integer) entries[i], (String) entries[i + 1]);
        }
        return row;
    }

    private static Map<Integer, String> nonBlankValues(Map<Integer, String> row) {
        Map<Integer, String> values = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : row.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                values.put(entry.getKey(), entry.getValue());
            }
        }
        return values;
    }

    private static class IndexedHead {
        @ExcelProperty(value = "First", index = 1)
        private String first;

        @ExcelProperty(value = "Second", index = 3)
        private String second;
    }
}

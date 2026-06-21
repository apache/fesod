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

package org.apache.fesod.sheet.testkit.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Tag(Tags.UNIT)
class RoundTripHelperTest {

    @TempDir
    File tempDir;

    private static List<SimpleData> simpleData(int count) {
        List<SimpleData> list = new ArrayList<SimpleData>();
        for (int i = 0; i < count; i++) {
            SimpleData d = new SimpleData();
            d.setName("Name" + i);
            list.add(d);
        }
        return list;
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void writeAndReadPreservesRowCount(ExcelFormat format) throws IOException {
        File file = format.createTempFile("rth-test", tempDir);
        List<SimpleData> data = simpleData(10);

        List<SimpleData> result = RoundTripHelper.writeAndRead(file, SimpleData.class, data);

        assertEquals(10, result.size());
        assertEquals("Name0", result.get(0).getName());
        assertEquals("Name9", result.get(9).getName());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void writeAndReadViaStream(ExcelFormat format) throws Exception {
        File file = format.createTempFile("rth-stream", tempDir);
        List<SimpleData> data = simpleData(5);

        List<SimpleData> result = RoundTripHelper.writeAndReadViaStream(file, format, SimpleData.class, data);

        assertEquals(5, result.size());
        assertEquals("Name0", result.get(0).getName());
        assertEquals("Name4", result.get(4).getName());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void writeAndReadSync(ExcelFormat format) throws IOException {
        File file = format.createTempFile("rth-sync", tempDir);
        List<SimpleData> data = simpleData(3);

        List<SimpleData> result = RoundTripHelper.writeAndReadSync(file, SimpleData.class, data);

        assertEquals(3, result.size());
        assertEquals("Name0", result.get(0).getName());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void apiModeFileMatchesDirect(ExcelFormat format) throws Exception {
        File file = format.createTempFile("rth-mode", tempDir);
        List<SimpleData> data = simpleData(7);

        List<SimpleData> result = RoundTripHelper.writeAndRead(file, format, ApiMode.FILE, SimpleData.class, data);

        assertEquals(7, result.size());
        assertEquals("Name0", result.get(0).getName());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void apiModeStreamMatchesDirect(ExcelFormat format) throws Exception {
        File file = format.createTempFile("rth-smode", tempDir);
        List<SimpleData> data = simpleData(7);

        List<SimpleData> result = RoundTripHelper.writeAndRead(file, format, ApiMode.STREAM, SimpleData.class, data);

        assertEquals(7, result.size());
        assertEquals("Name0", result.get(0).getName());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void fileAndStreamApiProduceSameResults(ExcelFormat format) throws Exception {
        List<SimpleData> data = simpleData(5);

        File fileA = format.createTempFile("rth-fa", tempDir);
        List<SimpleData> fileResult = RoundTripHelper.writeAndRead(fileA, SimpleData.class, data);

        File fileB = format.createTempFile("rth-fb", tempDir);
        List<SimpleData> streamResult = RoundTripHelper.writeAndReadViaStream(fileB, format, SimpleData.class, data);

        assertEquals(fileResult.size(), streamResult.size());
        for (int i = 0; i < fileResult.size(); i++) {
            assertEquals(fileResult.get(i).getName(), streamResult.get(i).getName());
        }
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void writePrimitiveThenReadPrimitive(ExcelFormat format) throws IOException {
        File file = format.createTempFile("rth-prim", tempDir);
        List<SimpleData> data = simpleData(4);

        RoundTripHelper.write(file, SimpleData.class, data);
        List<SimpleData> result = RoundTripHelper.read(file, SimpleData.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(4, result.size());
    }
}

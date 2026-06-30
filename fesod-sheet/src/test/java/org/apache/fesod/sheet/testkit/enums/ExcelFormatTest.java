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

package org.apache.fesod.sheet.testkit.enums;

import java.io.File;
import java.io.IOException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Tag(Tags.UNIT)
class ExcelFormatTest {

    @TempDir
    File tempDir;

    // --- Capability flag tests ---

    @Test
    void xlsxCapabilities() {
        Assertions.assertTrue(ExcelFormat.XLSX.supportsTemplates());
        Assertions.assertTrue(ExcelFormat.XLSX.supportsImages());
        Assertions.assertTrue(ExcelFormat.XLSX.supportsEncryption());
        Assertions.assertTrue(ExcelFormat.XLSX.supportsStyles());
    }

    @Test
    void xlsCapabilities() {
        Assertions.assertTrue(ExcelFormat.XLS.supportsTemplates());
        Assertions.assertTrue(ExcelFormat.XLS.supportsImages());
        Assertions.assertFalse(ExcelFormat.XLS.supportsEncryption());
        Assertions.assertTrue(ExcelFormat.XLS.supportsStyles());
    }

    @Test
    void csvCapabilities() {
        Assertions.assertFalse(ExcelFormat.CSV.supportsTemplates());
        Assertions.assertFalse(ExcelFormat.CSV.supportsImages());
        Assertions.assertFalse(ExcelFormat.CSV.supportsEncryption());
        Assertions.assertFalse(ExcelFormat.CSV.supportsStyles());
    }

    // --- Extension tests ---

    @Test
    void extensions() {
        Assertions.assertEquals(".xlsx", ExcelFormat.XLSX.getExtension());
        Assertions.assertEquals(".xls", ExcelFormat.XLS.getExtension());
        Assertions.assertEquals(".csv", ExcelFormat.CSV.getExtension());
    }

    // --- Enum round-trip: toExcelTypeEnum / fromExcelTypeEnum ---

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void enumRoundTrip(ExcelFormat format) {
        ExcelTypeEnum type = format.toExcelTypeEnum();
        ExcelFormat roundTripped = ExcelFormat.fromExcelTypeEnum(type);
        Assertions.assertEquals(format, roundTripped);
    }

    @Test
    void toExcelTypeEnumMapping() {
        Assertions.assertEquals(ExcelTypeEnum.XLSX, ExcelFormat.XLSX.toExcelTypeEnum());
        Assertions.assertEquals(ExcelTypeEnum.XLS, ExcelFormat.XLS.toExcelTypeEnum());
        Assertions.assertEquals(ExcelTypeEnum.CSV, ExcelFormat.CSV.toExcelTypeEnum());
    }

    // --- Temp file tests ---

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void createTempFileHasCorrectExtension(ExcelFormat format) throws IOException {
        File file = format.createTempFile("test", tempDir);
        Assertions.assertTrue(
                file.getName().endsWith(format.getExtension()),
                "Expected file name to end with " + format.getExtension() + ", got: " + file.getName());
        Assertions.assertEquals(tempDir, file.getParentFile());
    }

    @ParameterizedTest
    @EnumSource(ExcelFormat.class)
    void createTempFileInSystemTempDir(ExcelFormat format) throws IOException {
        File file = format.createTempFile("test");
        try {
            Assertions.assertTrue(
                    file.getName().endsWith(format.getExtension()),
                    "Expected file name to end with " + format.getExtension() + ", got: " + file.getName());
            Assertions.assertTrue(file.exists());
        } finally {
            file.delete();
        }
    }

    // --- Edge cases ---

    @Test
    void fromExcelTypeEnumWithNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ExcelFormat.fromExcelTypeEnum(null));
    }
}

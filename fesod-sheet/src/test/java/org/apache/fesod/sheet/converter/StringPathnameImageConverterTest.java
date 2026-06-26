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

package org.apache.fesod.sheet.converter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.fesod.sheet.converters.string.StringPathnameImageConverter;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link StringPathnameImageConverter}.
 */
@Tag(Tags.UNIT)
class StringPathnameImageConverterTest {

    @TempDir
    Path tempDir;

    private final StringPathnameImageConverter converter = new StringPathnameImageConverter();
    private byte[] imageBytes;

    @BeforeEach
    void setup() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("converter/img.jpg")) {
            imageBytes = IOUtils.toByteArray(is);
        }
    }

    @Test
    void test_supportJavaTypeKey() {
        Assertions.assertEquals(String.class, converter.supportJavaTypeKey());
    }

    @Test
    void test_convertToExcelData() throws IOException {
        Path imageFile = tempDir.resolve("test.jpg");
        Files.write(imageFile, imageBytes);

        WriteCellData<?> cellData = converter.convertToExcelData(imageFile.toString(), null, new GlobalConfiguration());

        Assertions.assertArrayEquals(
                imageBytes, cellData.getImageDataList().get(0).getImage());
    }

    @Test
    void test_convertToExcelData_nonExistentFile() {
        String nonExistentPath = tempDir.resolve("nonexistent.jpg").toString();

        Assertions.assertThrows(
                IOException.class,
                () -> converter.convertToExcelData(nonExistentPath, null, new GlobalConfiguration()));
    }
}

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

package org.apache.fesod.sheet.readwrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.builder.ExcelReaderBuilder;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test encrypt read/write for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class EncryptDataTest extends AbstractExcelTest {
    private static final String PASSWORD = "123456";

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteAllCombinations(ExcelFormat format) throws Exception {
        ExcelTypeEnum excelType = format.toExcelTypeEnum();

        // File-based: no explicit type, no password
        readAndWrite(createTempFile("enc-f-np", format), null, false, false, excelType);
        // File-based: no explicit type, with password
        readAndWrite(createTempFile("enc-f-wp", format), null, true, false, excelType);
        // File-based: explicit type, no password
        readAndWrite(createTempFile("enc-f-tp", format), excelType, false, false, excelType);
        // File-based: explicit type, with password
        readAndWrite(createTempFile("enc-f-twp", format), excelType, true, false, excelType);

        // Stream-based: no explicit type, no password
        readAndWrite(createTempFile("enc-s-np", format), null, false, true, excelType);
        // Stream-based: no explicit type, with password
        readAndWrite(createTempFile("enc-s-wp", format), null, true, true, excelType);
        // Stream-based: explicit type, no password
        readAndWrite(createTempFile("enc-s-tp", format), excelType, false, true, excelType);
        // Stream-based: explicit type, with password
        readAndWrite(createTempFile("enc-s-twp", format), excelType, true, true, excelType);
    }

    private void readAndWrite(
            File file, ExcelTypeEnum excelType, boolean hasPassword, boolean isStream, ExcelTypeEnum streamType)
            throws Exception {
        ExcelWriterBuilder excelWriterBuilder = isStream
                ? FesodSheet.write(Files.newOutputStream(file.toPath()), SimpleData.class)
                : FesodSheet.write(file, SimpleData.class);

        ExcelReaderBuilder readerBuilder = isStream
                ? FesodSheet.read(
                        Files.newInputStream(file.toPath()), SimpleData.class, new CollectingReadListener<SimpleData>())
                : FesodSheet.read(file, SimpleData.class, new CollectingReadListener<SimpleData>());
        if (excelType != null) {
            excelWriterBuilder.excelType(excelType);
            readerBuilder.excelType(excelType);
        }
        if (isStream && excelType == null) {
            // Stream API needs type hint when not explicitly set
            excelWriterBuilder.excelType(streamType);
            readerBuilder.excelType(streamType);
        }
        if (hasPassword) {
            excelWriterBuilder.password(PASSWORD);
            readerBuilder.password(PASSWORD);
        }

        excelWriterBuilder.sheet().doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList = readerBuilder.sheet().doReadSync();
        assertEquals(10, dataList.size());
        assertNotNull(dataList.get(0).getName());
    }
}

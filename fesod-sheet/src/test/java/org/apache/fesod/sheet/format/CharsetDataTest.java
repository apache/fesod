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

package org.apache.fesod.sheet.format;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * charset
 *
 *
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.FORMAT)
@Slf4j
public class CharsetDataTest extends AbstractExcelTest {
    private static final Charset GBK = Charset.forName("GBK");

    @Test
    public void readAndWriteCsv() throws Exception {
        File fileCsvGbk = createTempFile("charsetGbk", ExcelFormat.CSV);
        File fileCsvUtf8 = createTempFile("charsetUtf8", ExcelFormat.CSV);
        readAndWrite(fileCsvGbk, GBK);
        readAndWrite(fileCsvUtf8, StandardCharsets.UTF_8);
    }

    @Test
    public void readAndWriteCsvError() throws Exception {
        File fileCsvError = createTempFile("charsetError", ExcelFormat.CSV);
        FesodSheet.write(fileCsvError, SimpleData.class)
                .charset(GBK)
                .sheet()
                .doWrite(TestDataBuilder.simpleDataWithAge(10));
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(fileCsvError, SimpleData.class, listener)
                .charset(StandardCharsets.UTF_8)
                .sheet()
                .doRead();
    }

    private void readAndWrite(File file, Charset charset) {
        FesodSheet.write(file, SimpleData.class)
                .charset(charset)
                .sheet()
                .doWrite(TestDataBuilder.simpleDataWithAge(10));
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SimpleData.class, listener)
                .charset(charset)
                .sheet()
                .doRead();
        Assertions.assertEquals(10, listener.getRowCount());
        Assertions.assertEquals("Name0", listener.getFirstRow().getName());
        Assertions.assertEquals(0, (int) listener.getFirstRow().getAge());
    }
}

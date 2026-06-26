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

package org.apache.fesod.sheet.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * bom test
 *
 *
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.FORMAT)
@Slf4j
public class BomDataTest extends AbstractExcelTest {

    @Test
    public void readCsv() {
        readCsvImpl(readFile("bom" + File.separator + "no_bom.csv"));
        readCsvImpl(readFile("bom" + File.separator + "office_bom.csv"));
    }

    @Test
    public void readAndWriteCsv() throws Exception {
        readAndWriteCsv(createTempFile("bom_default", ExcelFormat.CSV), null, null);
        readAndWriteCsv(createTempFile("bom_utf_8", ExcelFormat.CSV), "UTF-8", null);
        readAndWriteCsv(createTempFile("bom_utf_8_lower_case", ExcelFormat.CSV), "utf-8", null);
        readAndWriteCsv(createTempFile("bom_gbk", ExcelFormat.CSV), "GBK", null);
        readAndWriteCsv(createTempFile("bom_gbk_lower_case", ExcelFormat.CSV), "gbk", null);
        readAndWriteCsv(createTempFile("bom_utf_16be", ExcelFormat.CSV), "UTF-16BE", null);
        readAndWriteCsv(createTempFile("bom_utf_8_not_with_bom", ExcelFormat.CSV), "UTF-8", Boolean.FALSE);
    }

    private void readAndWriteCsv(File file, String charsetName, Boolean withBom) throws Exception {
        Charset charset = null;
        if (charsetName != null) {
            charset = Charset.forName(charsetName);
        }
        List<SimpleData> data = TestDataBuilder.simpleDataWithAge(10);
        FesodSheet.write(new FileOutputStream(file), SimpleData.class)
                .charset(charset)
                .withBom(withBom)
                .excelType(ExcelTypeEnum.CSV)
                .sheet()
                .doWrite(data);

        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SimpleData.class, listener)
                .charset(charset)
                .sheet()
                .doRead();
        assertEquals(10, listener.getRowCount());
        assertEquals("Name0", listener.getFirstRow().getName());
        assertEquals(0, (int) listener.getFirstRow().getAge());
    }

    private void readCsvImpl(File file) {
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SimpleData.class, listener).sheet().doRead();
        assertEquals(10, listener.getRowCount());
        assertEquals("姓名0", listener.getFirstRow().getName());
        assertEquals(20, (int) listener.getFirstRow().getAge());
    }
}

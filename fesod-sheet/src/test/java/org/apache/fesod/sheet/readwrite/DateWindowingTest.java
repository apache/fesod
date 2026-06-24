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

package org.apache.fesod.sheet.readwrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
public class DateWindowingTest extends AbstractExcelTest {

    @Test
    void readsXlsWindowingFlagsFromGeneratedAndFixtureFiles() throws Exception {
        File file1900 = createTempFile("datewindowing1900", ExcelFormat.XLS);
        File file1904 = createTempFile("datewindowing1904", ExcelFormat.XLS);
        writeDateWindowingFile(file1900, ExcelTypeEnum.XLS, Boolean.FALSE);
        writeDateWindowingFile(file1904, ExcelTypeEnum.XLS, Boolean.TRUE);

        assertActualWindowing(file1904, ExcelTypeEnum.XLS, null, Boolean.FALSE);
        assertActualWindowing(file1900, ExcelTypeEnum.XLS, Boolean.FALSE, Boolean.FALSE);
        assertActualWindowing(file1904, ExcelTypeEnum.XLS, Boolean.TRUE, Boolean.TRUE);
        assertActualWindowing(fixtureFile("1900DateWindowing.xls"), ExcelTypeEnum.XLS, null, Boolean.FALSE);
        assertActualWindowing(fixtureFile("1904DateWindowing.xls"), ExcelTypeEnum.XLS, null, Boolean.TRUE);
    }

    @Test
    void readsXlsxWindowingFlagsFromGeneratedFiles() throws Exception {
        File file1900 = createTempFile("datewindowing1900", ExcelFormat.XLSX);
        File file1904 = createTempFile("datewindowing1904", ExcelFormat.XLSX);
        writeDateWindowingFile(file1900, ExcelTypeEnum.XLSX, Boolean.FALSE);
        writeDateWindowingFile(file1904, ExcelTypeEnum.XLSX, Boolean.TRUE);

        assertActualWindowing(file1900, ExcelTypeEnum.XLSX, null, Boolean.FALSE);
        assertActualWindowing(file1904, ExcelTypeEnum.XLSX, null, Boolean.TRUE);
    }

    @Test
    void readsCsvWindowingFlagsFromReadConfiguration() throws Exception {
        File file1900 = createTempFile("datewindowing1900", ExcelFormat.CSV);
        File file1904 = createTempFile("datewindowing1904", ExcelFormat.CSV);
        writeDateWindowingFile(file1900, ExcelTypeEnum.CSV, Boolean.FALSE);
        writeDateWindowingFile(file1904, ExcelTypeEnum.CSV, Boolean.TRUE);

        assertActualWindowing(file1900, ExcelTypeEnum.CSV, Boolean.FALSE, Boolean.FALSE);
        assertActualWindowing(file1904, ExcelTypeEnum.CSV, null, Boolean.FALSE);
        assertActualWindowing(file1904, ExcelTypeEnum.CSV, Boolean.TRUE, Boolean.TRUE);
        assertActualWindowing(file1904, ExcelTypeEnum.CSV, null, Boolean.FALSE);
    }

    private void writeDateWindowingFile(File file, ExcelTypeEnum excelType, Boolean use1904windowing) {
        FesodSheet.write(file, SimpleData.class)
                .excelType(excelType)
                .use1904windowing(use1904windowing)
                .sheet()
                .doWrite(TestDataBuilder.simpleDataWithDate(10));
    }

    private void assertActualWindowing(
            File file,
            ExcelTypeEnum excelType,
            Boolean configuredUse1904windowing,
            Boolean expectedActualUse1904windowing) {
        DateWindowingListener listener = new DateWindowingListener(Boolean.FALSE);
        if (configuredUse1904windowing == null) {
            FesodSheet.read(file, listener)
                    .excelType(excelType)
                    .head(SimpleData.class)
                    .doReadAllSync();
        } else {
            FesodSheet.read(file, listener)
                    .excelType(excelType)
                    .use1904windowing(configuredUse1904windowing)
                    .head(SimpleData.class)
                    .doReadAllSync();
        }
        assertEquals(expectedActualUse1904windowing, listener.getActualUse1904windowing());
    }

    private File fixtureFile(String fileName) {
        return readFile("datewindowing/" + fileName);
    }
}

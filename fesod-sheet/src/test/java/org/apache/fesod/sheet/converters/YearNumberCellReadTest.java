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

package org.apache.fesod.sheet.converters;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Year;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * A {@code Year} field must read numeric cells produced outside fesod: a plain number is the year
 * itself, while a date-formatted cell is an Excel date serial whose year is taken.
 */
@Tag(Tags.READ)
public class YearNumberCellReadTest extends AbstractExcelTest {

    @Test
    void readsPlainNumericAndDateFormattedCells() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                FileOutputStream out = new FileOutputStream(file)) {
            XSSFSheet sheet = workbook.createSheet("s1");
            XSSFRow head = sheet.createRow(0);
            head.createCell(0).setCellValue("plainYear");
            head.createCell(1).setCellValue("dateYear");

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(
                    workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd"));

            XSSFRow row = sheet.createRow(1);
            row.createCell(0).setCellValue(2026.0);
            row.createCell(1).setCellValue(46023.0); // 2026-01-01
            row.getCell(1).setCellStyle(dateStyle);

            workbook.write(out);
        }

        List<YearData> result =
                FesodSheet.read(file).head(YearData.class).sheet().doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Year.of(2026), result.get(0).getPlainYear());
        Assertions.assertEquals(Year.of(2026), result.get(0).getDateYear());
    }

    public static class YearData {

        @ExcelProperty("plainYear")
        private Year plainYear;

        @ExcelProperty("dateYear")
        private Year dateYear;

        public Year getPlainYear() {
            return plainYear;
        }

        public void setPlainYear(Year plainYear) {
            this.plainYear = plainYear;
        }

        public Year getDateYear() {
            return dateYear;
        }

        public void setDateYear(Year dateYear) {
            this.dateYear = dateYear;
        }
    }
}

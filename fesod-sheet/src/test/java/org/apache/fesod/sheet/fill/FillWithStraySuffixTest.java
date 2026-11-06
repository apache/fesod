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

package org.apache.fesod.sheet.fill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
public class FillWithStraySuffixTest extends AbstractExcelTest {

    @Test
    void fillTemplateWithStraySuffixBeforePlaceholder() throws Exception {
        File template = createTempFile(ExcelFormat.XLSX);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet0");
            Row row = sheet.createRow(0);
            // The stray '}' before the '{name}' placeholder is literal text, not a placeholder suffix.
            row.createCell(0).setCellValue("a}b{name}");
            try (FileOutputStream out = new FileOutputStream(template)) {
                workbook.write(out);
            }
        }

        File outFile = createTempFile(ExcelFormat.XLSX);
        Map<String, String> data = new HashMap<>();
        data.put("name", "filled");

        FesodSheet.write(outFile).withTemplate(template).sheet().doFill(data);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(outFile))) {
            String value = workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            Assertions.assertEquals("a}bfilled", value);
        }
    }
}

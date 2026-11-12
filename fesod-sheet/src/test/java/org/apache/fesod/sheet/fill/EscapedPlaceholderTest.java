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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for issue #462: a template placeholder whose closing brace is escaped (for
 * example {@code {foo\}}) has no valid closing brace to parse, so the text must be kept as-is
 * instead of breaking the fill.
 */
@Tag(Tags.ROUND_TRIP)
public class EscapedPlaceholderTest extends AbstractExcelTest {

    private static final String TEMPLATE_SHEET_NAME = "Sheet1";
    private static final String VARIABLE = "foo";
    private static final String VALUE = "replaced_value";

    @Test
    void escapedClosingBraceIsKeptAsLiteralText() throws IOException {
        // {foo\} used to blow up with StringIndexOutOfBoundsException while parsing the suffix
        Assertions.assertEquals("{foo\\}", fillTemplateWith("{foo\\}"));
    }

    @Test
    void escapedOpeningBraceIsKeptAsLiteralText() throws IOException {
        Assertions.assertEquals("\\{foo}", fillTemplateWith("\\{foo}"));
    }

    @Test
    void escapedPlaceholderInsideTextIsKeptAsLiteralText() throws IOException {
        Assertions.assertEquals("prefix {foo\\} suffix", fillTemplateWith("prefix {foo\\} suffix"));
    }

    @Test
    void normalPlaceholderIsStillReplaced() throws IOException {
        Assertions.assertEquals(VALUE, fillTemplateWith("{foo}"));
    }

    @Test
    void escapedPlaceholderBeforeNormalPlaceholderIsHandledIndependently() throws IOException {
        Assertions.assertEquals("{foo\\} second_value", fillTemplateWith("{foo\\} {bar}"));
    }

    /**
     * Fills a single-cell template holding {@code cellText} and returns the filled value read back
     * from the output file.
     */
    private String fillTemplateWith(String cellText) throws IOException {
        File template = createTemplate(cellText);
        File output = createTempFile("escaped-placeholder-out", ExcelFormat.XLSX);
        Map<String, Object> data = new HashMap<>();
        data.put(VARIABLE, VALUE);
        data.put("bar", "second_value");
        FesodSheet.write(output).withTemplate(template).sheet().doFill(data);
        List<Map<Integer, String>> rows =
                FesodSheet.read(output).sheet().headRowNumber(0).doReadSync();
        return rows.get(0).get(0);
    }

    private File createTemplate(String cellText) throws IOException {
        File template = createTempFile("escaped-placeholder", ExcelFormat.XLSX);
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(template)) {
            Sheet sheet = workbook.createSheet(TEMPLATE_SHEET_NAME);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(cellText);
            workbook.write(fos);
        }
        return template;
    }
}

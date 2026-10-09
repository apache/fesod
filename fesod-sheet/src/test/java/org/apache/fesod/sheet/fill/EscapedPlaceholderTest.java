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
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for issue #462: ArrayIndexOutOfBoundsException when placeholder content is '{foo\}'
 *
 * When a placeholder has an escaped closing brace like {foo\}, the parser should handle it
 * gracefully without throwing an exception.
 */
public class EscapedPlaceholderTest {

    private static final String SUBDIR = "issue462";

    @BeforeAll
    public static void createTemplates() throws IOException {
        // Create template with escaped placeholder {foo\}
        createTemplate("escaped_suffix.xlsx", "{foo\\}");

        // Create template with escaped prefix \{foo}
        createTemplate("escaped_prefix.xlsx", "\\{foo}");

        // Create template with normal placeholder for comparison
        createTemplate("normal.xlsx", "{foo}");

        // Create template with mixed content
        createTemplate("mixed.xlsx", "prefix {foo\\} suffix");
    }

    private static void createTemplate(String fileName, String content) throws IOException {
        File templateFile = TestFileUtil.createNewFile(SUBDIR + File.separator + fileName);
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(templateFile)) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(content);
            workbook.write(fos);
        }
    }

    /**
     * Test that template with {foo\} (escaped closing brace) does not throw exception.
     * The escaped brace means there's no valid closing brace, so this should be treated as literal text.
     */
    @Test
    public void testEscapedSuffixPlaceholder() {
        File template = TestFileUtil.readFile(SUBDIR + File.separator + "escaped_suffix.xlsx");
        File output = TestFileUtil.createNewFile(SUBDIR + File.separator + "output_escaped_suffix.xlsx");

        Map<String, Object> data = new HashMap<>();
        data.put("foo", "replaced_value");

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(data));

        // Verify output file exists and can be read
        List<Object> result =
                FesodSheet.read(output).sheet().headRowNumber(0).doReadSync();
        Assertions.assertFalse(result.isEmpty());
    }

    /**
     * Test that template with \{foo} (escaped opening brace) is treated as literal text.
     */
    @Test
    public void testEscapedPrefixPlaceholder() {
        File template = TestFileUtil.readFile(SUBDIR + File.separator + "escaped_prefix.xlsx");
        File output = TestFileUtil.createNewFile(SUBDIR + File.separator + "output_escaped_prefix.xlsx");

        Map<String, Object> data = new HashMap<>();
        data.put("foo", "replaced_value");

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(data));

        // Verify the output
        List<Object> result =
                FesodSheet.read(output).sheet().headRowNumber(0).doReadSync();
        Assertions.assertFalse(result.isEmpty());
    }

    /**
     * Test that normal placeholder {foo} still works correctly.
     */
    @Test
    public void testNormalPlaceholder() {
        File template = TestFileUtil.readFile(SUBDIR + File.separator + "normal.xlsx");
        File output = TestFileUtil.createNewFile(SUBDIR + File.separator + "output_normal.xlsx");

        Map<String, Object> data = new HashMap<>();
        data.put("foo", "replaced_value");

        FesodSheet.write(output).withTemplate(template).sheet().doFill(data);

        // Verify the placeholder was replaced
        List<Object> result =
                FesodSheet.read(output).sheet().headRowNumber(0).doReadSync();
        Assertions.assertFalse(result.isEmpty());

        @SuppressWarnings("unchecked")
        Map<String, String> row = (Map<String, String>) result.get(0);
        Assertions.assertEquals("replaced_value", row.get(0));
    }

    /**
     * Test mixed content with escaped placeholder in the middle.
     */
    @Test
    public void testMixedContentWithEscapedPlaceholder() {
        File template = TestFileUtil.readFile(SUBDIR + File.separator + "mixed.xlsx");
        File output = TestFileUtil.createNewFile(SUBDIR + File.separator + "output_mixed.xlsx");

        Map<String, Object> data = new HashMap<>();
        data.put("foo", "replaced_value");

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(data));
    }
}

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag(Tags.READ)
@Tag(Tags.FORMAT)
class InlineStringReadTest extends AbstractExcelTest {
    private static final String NAMESPACE = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String PHONETIC_TEXT =
            "<rPh sb=\"0\" eb=\"1\"><t>とう</t></rPh><rPh sb=\"1\" eb=\"2\"><t>きょう</t></rPh>";

    @ParameterizedTest(name = "prefix={0}, chunked={1}")
    @CsvSource({"'', false", "'', true", "x, false", "x, true", "ns2, false", "ns2, true"})
    void readInlineStrings(String prefix, boolean chunked) throws IOException {
        File file = createWorkbook(prefix);
        List<Map<Integer, String>> rows;
        try (InputStream input = openInput(file, chunked)) {
            rows = FesodSheet.read(input)
                    .headRowNumber(0)
                    .autoTrim(false)
                    .sheet()
                    .doReadSync();
        }
        String[] expected = {"base", "actual", "actual", "start\rend", "東京", "東京", "東京", "_x0041_", "42"};
        Assertions.assertEquals(expected.length, rows.size());
        Assertions.assertAll(IntStream.range(0, expected.length)
                .mapToObj(index -> () ->
                        Assertions.assertEquals(expected[index], rows.get(index).get(0), "Cell A" + (index + 1))));
    }

    private File createWorkbook(String prefix) throws IOException {
        ByteArrayOutputStream template = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            workbook.createSheet().createRow(0).createCell(0).setCellValue("seed");
            workbook.write(template);
        }
        // Patch the XML to exercise representations that a workbook writer would normalize away.
        String[] cells = {
            "<c r=\"A1\" t=\"inlineStr\"><is><t>base</t></is></c>",
            "<c r=\"A2\" t=\"inlineStr\"><v>stale</v><is><t>actual</t></is></c>",
            "<c r=\"A3\" t=\"inlineStr\"><is><t>actual</t></is><v>stale</v></c>",
            "<c r=\"A4\" t=\"str\"><f>\"start\"</f><v>start_x000D_end</v></c>",
            "<c r=\"A5\" t=\"inlineStr\"><is><t>東京</t>" + PHONETIC_TEXT + "</is></c>",
            "<c r=\"A6\" t=\"inlineStr\"><is><r><t>東</t></r><r><t>京</t></r>" + PHONETIC_TEXT + "</is></c>",
            "<c r=\"A7\" t=\"s\"><v>0</v></c>",
            "<c r=\"A8\" t=\"inlineStr\"><is><t>_x005F_x0041_</t></is></c>",
            "<c r=\"A9\"><v>42</v></c>"
        };
        StringBuilder sheetData = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            sheetData
                    .append("<row r=\"")
                    .append(i + 1)
                    .append("\">")
                    .append(cells[i])
                    .append("</row>");
        }
        String sheet = "<worksheet xmlns=\"" + NAMESPACE + "\"><sheetData>" + sheetData + "</sheetData></worksheet>";
        String sharedStrings = "<sst xmlns=\"" + NAMESPACE + "\" count=\"1\" uniqueCount=\"1\"><si><t>東京</t>"
                + PHONETIC_TEXT + "</si></sst>";
        File file = createTempFile(ExcelFormat.XLSX);
        try (ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(template.toByteArray()));
                ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(file.toPath()))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                output.putNextEntry(new ZipEntry(entry.getName()));
                if ("xl/worksheets/sheet1.xml".equals(entry.getName())) {
                    output.write(withPrefix(sheet, prefix).getBytes(StandardCharsets.UTF_8));
                } else if ("xl/sharedStrings.xml".equals(entry.getName())) {
                    output.write(withPrefix(sharedStrings, prefix).getBytes(StandardCharsets.UTF_8));
                } else {
                    IOUtils.copy(input, output);
                }
                output.closeEntry();
            }
        }
        return file;
    }

    private String withPrefix(String xml, String prefix) {
        if (prefix.isEmpty()) {
            return xml;
        }
        return xml.replace("xmlns=", "xmlns:" + prefix + "=")
                .replaceAll("<(/?)([A-Za-z][A-Za-z0-9]*)", "<$1" + prefix + ":$2");
    }

    private InputStream openInput(File file, boolean chunked) throws IOException {
        InputStream input = Files.newInputStream(file.toPath());
        if (!chunked) {
            return input;
        }
        return new FilterInputStream(input) {
            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                return super.read(buffer, offset, Math.min(length, 7));
            }
        };
    }
}

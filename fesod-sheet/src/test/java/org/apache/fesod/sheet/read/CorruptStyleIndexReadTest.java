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

package org.apache.fesod.sheet.read;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Regression test for <a href="https://github.com/apache/fesod/issues/355">issue #355</a>.
 *
 * <p>An xlsx produced by third-party tools may carry a non-numeric {@code s} (style index)
 * attribute on a cell. Reading such a file used to abort with a {@code NumberFormatException}
 * at the very first corrupt cell, so nothing after it was read. The handler must fall back to
 * the default format and keep reading the remaining rows.
 *
 * <p>The file below is a minimal hand-built xlsx (no styles.xml on purpose): row 1 and row 3
 * carry valid style indexes, row 2 carries a corrupt one.
 */
@Tag(Tags.READ)
class CorruptStyleIndexReadTest {

    private static final String CONTENT_TYPES = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n"
            + "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
            + "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n"
            + "  <Override PartName=\"/xl/workbook.xml\""
            + " ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n"
            + "  <Override PartName=\"/xl/worksheets/sheet1.xml\""
            + " ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n"
            + "</Types>\n";

    private static final String ROOT_RELS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
            + "  <Relationship Id=\"rId1\""
            + " Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\""
            + " Target=\"xl/workbook.xml\"/>\n"
            + "</Relationships>\n";

    private static final String WORKBOOK = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\""
            + " xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n"
            + "  <sheets>\n"
            + "    <sheet name=\"Sheet1\" sheetId=\"1\" r:id=\"rId1\"/>\n"
            + "  </sheets>\n"
            + "</workbook>\n";

    private static final String WORKBOOK_RELS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n"
            + "  <Relationship Id=\"rId1\""
            + " Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\""
            + " Target=\"worksheets/sheet1.xml\"/>\n"
            + "</Relationships>\n";

    private static final String SHEET = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n"
            + "  <sheetData>\n"
            + "    <row r=\"1\"><c r=\"A1\" t=\"n\" s=\"0\"><v>1</v></c></row>\n"
            + "    <row r=\"2\"><c r=\"A2\" t=\"n\" s=\"abc\"><v>2</v></c></row>\n"
            + "    <row r=\"3\"><c r=\"A3\" t=\"n\" s=\"1\"><v>3</v></c></row>\n"
            + "  </sheetData>\n"
            + "</worksheet>\n";

    @Test
    void read_corruptStyleIndex_skipsStyleAndReadsAllRows() throws Exception {
        File file = buildMinimalXlsx();

        List<Map<Integer, String>> rows =
                FesodSheet.read(file).sheet().headRowNumber(0).doReadSync();

        Assertions.assertEquals(3, rows.size(), "all rows must be read, none aborted");
        Assertions.assertEquals("1", rows.get(0).get(0));
        Assertions.assertEquals("2", rows.get(1).get(0), "row with the corrupt style index must still be read");
        Assertions.assertEquals("3", rows.get(2).get(0), "rows after the corrupt one must still be read");
    }

    private File buildMinimalXlsx() throws Exception {
        File file = File.createTempFile("corrupt-style-index", ".xlsx");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            addEntry(zip, "[Content_Types].xml", CONTENT_TYPES);
            addEntry(zip, "_rels/.rels", ROOT_RELS);
            addEntry(zip, "xl/workbook.xml", WORKBOOK);
            addEntry(zip, "xl/_rels/workbook.xml.rels", WORKBOOK_RELS);
            addEntry(zip, "xl/worksheets/sheet1.xml", SHEET);
        }
        return file;
    }

    private void addEntry(ZipOutputStream zip, String name, String content) throws Exception {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}

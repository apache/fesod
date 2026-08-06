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

package org.apache.fesod.sheet.analysis.v07.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.write.handler.EscapeHexCellWriteHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Regression test for <a href="https://github.com/apache/fesod/issues/696">issue #696</a>
 *
 * <p>Characters that XML 1.0 forbids are stored in a cell as {@code _xHHHH_} escapes (ECMA-376,
 * section 22.4.2.4), which a reader has to undo. Fesod only did so for one of the two places a cell
 * can keep its text:
 *
 * <ul>
 *   <li>{@code sharedStrings.xml} - decoded, a contract pinned by
 *       {@code CompatibilityTest#readXlsxWithEscapeSequence}
 *   <li>inline string - not decoded, so the raw escape reached the caller
 * </ul>
 *
 * <p>Both poi read paths decode it: the SAX {@code XSSFSheetXMLHandler} routes inline strings
 * through {@code XSSFRichTextString#getString()}, and the DOM {@code XSSFCell} does the same for
 * {@code inlineStr} and {@code str} cells. Fesod's own writer emits inline strings, so the escape
 * survived a Fesod write/read round trip.
 *
 * <p>Each test asserts the decoded value twice: through the testkit assertions, which pin the value
 * the file actually holds, and through Fesod, which is the behaviour under test.
 */
@Tag(Tags.READ)
class InlineStringUtfDecodeTest extends AbstractExcelTest {

    /** The control character from the issue report, kept out of the source as a raw literal. */
    private static final String STX = String.valueOf((char) 0x02);

    @Test
    void readDecodesUtfEscapeInInlineStringCell() throws IOException {
        File file = createTempFile("inline-string-utf-escape", ExcelFormat.XLSX);
        writeName(file, "Product_x0002_Code", false);

        String expected = "Product" + STX + "Code";
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(1).cell(0).hasStringValue(expected);
        }
        Assertions.assertEquals(expected, readNameWithFesod(file));
    }

    @Test
    void escapeHexWriteHandlerRoundTripsLiteralEscape() throws IOException {
        File file = createTempFile("escape-hex-round-trip", ExcelFormat.XLSX);
        // The handler stores the literal "_xB9f0_" as "_x005F_xB9f0_" so it survives decoding.
        writeName(file, "Product_xB9f0_Code", true);

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(1).cell(0).hasStringValue("Product_xB9f0_Code");
        }
        Assertions.assertEquals("Product_xB9f0_Code", readNameWithFesod(file));
    }

    private void writeName(File file, String name, boolean escapeHex) {
        SimpleData data = new SimpleData();
        data.setName(name);
        if (escapeHex) {
            FesodSheet.write(file, SimpleData.class)
                    .registerWriteHandler(new EscapeHexCellWriteHandler())
                    .sheet()
                    .doWrite(Collections.singletonList(data));
        } else {
            FesodSheet.write(file, SimpleData.class).sheet().doWrite(Collections.singletonList(data));
        }
    }

    private String readNameWithFesod(File file) {
        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SimpleData.class, listener).sheet().doRead();
        List<SimpleData> rows = listener.getRows();
        Assertions.assertEquals(1, rows.size());
        return rows.get(0).getName();
    }
}

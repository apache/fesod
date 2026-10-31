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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.csv.CsvConstant;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link CsvConstant#NONE_QUOTE} is documented to be equivalent to a {@code null} quote character, so quoting must be
 * disabled instead of being redirected to the NUL character.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.FORMAT)
public class CsvNoneQuoteTest extends AbstractExcelTest {

    @Test
    public void writeWithNoneQuoteEmitsNoNulBytes() throws Exception {
        File file = createTempFile("csvNoneQuoteWrite", ExcelFormat.CSV);

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("name", "note"));
        data.add(Arrays.asList("plain", "has,comma"));
        FesodSheet.write(file).csv().quote(CsvConstant.NONE_QUOTE).doWrite(data);

        byte[] bytes = Files.readAllBytes(file.toPath());
        for (byte b : bytes) {
            Assertions.assertNotEquals(0, b, "NONE_QUOTE must not write NUL quote bytes");
        }
        String content = new String(bytes, StandardCharsets.UTF_8);
        Assertions.assertTrue(content.contains("plain,has,comma"), "values must be written unquoted");
    }

    @Test
    public void writeWithNoneQuoteKeepsPlainValuesUnchanged() throws Exception {
        File plainFile = createTempFile("csvNoneQuotePlain", ExcelFormat.CSV);
        File noneQuoteFile = createTempFile("csvNoneQuotePlainNq", ExcelFormat.CSV);

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("name", "note"));
        data.add(Arrays.asList("plain", "simple"));
        FesodSheet.write(plainFile).csv().doWrite(data);
        FesodSheet.write(noneQuoteFile).csv().quote(CsvConstant.NONE_QUOTE).doWrite(data);

        Assertions.assertEquals(
                Files.readAllLines(plainFile.toPath(), StandardCharsets.UTF_8),
                Files.readAllLines(noneQuoteFile.toPath(), StandardCharsets.UTF_8));
    }

    @Test
    public void readWithNoneQuoteSplitsNulLeadingField() throws Exception {
        File file = createTempFile("csvNoneQuoteRead", ExcelFormat.CSV);
        // A field that itself starts with the NUL character must be ordinary text: the row is kept and the field is
        // split on the delimiter instead of being parsed as an unterminated quoted field. The leading NUL is removed
        // by the default value trimming, which strips every character up to and including 0x20.
        Files.write(file.toPath(), "\u0000a,b\n".getBytes(StandardCharsets.UTF_8));

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .csv()
                .quote(CsvConstant.NONE_QUOTE)
                .headRowNumber(0)
                .doReadSync();

        Assertions.assertEquals(1, rows.size(), "the row must be read instead of being dropped as malformed");
        Assertions.assertEquals("a", rows.get(0).get(0));
        Assertions.assertEquals("b", rows.get(0).get(1));
    }

    @Test
    public void readWithNoneQuoteTreatsDoubleQuotesAsText() throws Exception {
        File file = createTempFile("csvNoneQuoteReadText", ExcelFormat.CSV);
        Files.write(file.toPath(), "\"abc,def\n".getBytes(StandardCharsets.UTF_8));

        List<Map<Integer, String>> rows = FesodSheet.read(file)
                .csv()
                .quote(CsvConstant.NONE_QUOTE)
                .headRowNumber(0)
                .doReadSync();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("\"abc", rows.get(0).get(0));
        Assertions.assertEquals("def", rows.get(0).get(1));
    }
}

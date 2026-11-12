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

package org.apache.fesod.sheet.i18n;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Integration tests for head-name localization.
 */
@Tag(Tags.ROUND_TRIP)
class LocalizedHeadDataTest extends AbstractExcelTest {

    private static final String NAME_CODE = "head.name";
    private static final String CODE_CODE = "head.code";
    private static final String ADDRESS_CODE = "head.address";
    private static final String CITY_CODE = "head.city";
    private static final String STREET_CODE = "head.street";

    private static StaticMessageSource headMessageSource() {
        StaticMessageSource source = new StaticMessageSource();
        source.addMessage(NAME_CODE, "Name", Locale.ENGLISH);
        source.addMessage(CODE_CODE, "Code", Locale.ENGLISH);
        source.addMessage(NAME_CODE, "姓名", Locale.SIMPLIFIED_CHINESE);
        source.addMessage(CODE_CODE, "编号", Locale.SIMPLIFIED_CHINESE);
        source.addMessage(ADDRESS_CODE, "Address", Locale.ENGLISH);
        source.addMessage(CITY_CODE, "City", Locale.ENGLISH);
        source.addMessage(STREET_CODE, "Street", Locale.ENGLISH);
        source.addMessage(ADDRESS_CODE, "地址", Locale.SIMPLIFIED_CHINESE);
        source.addMessage(CITY_CODE, "城市", Locale.SIMPLIFIED_CHINESE);
        source.addMessage(STREET_CODE, "街道", Locale.SIMPLIFIED_CHINESE);
        return source;
    }

    private static List<Map<Integer, String>> readRawRows(File file) {
        return FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();
    }

    private static List<Map<Integer, String>> readRawRows(File file, int sheetNo) {
        return FesodSheet.read(file).headRowNumber(0).sheet(sheetNo).doReadSync();
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldLocalizeHeadNamesForConfiguredLocale(ExcelFormat format) throws Exception {
        File englishFile = createTempFile("localizedHeadEn", format);
        FesodSheet.write(englishFile, LocalizedHeadData.class)
                .localizeHead(headMessageSource(), Locale.ENGLISH)
                .sheet()
                .doWrite(Collections.singletonList(new LocalizedHeadData("Alice", "A1")));
        Map<Integer, String> englishHeader = readRawRows(englishFile).get(0);
        Assertions.assertEquals("Name", englishHeader.get(0));
        Assertions.assertEquals("Code", englishHeader.get(1));

        File chineseFile = createTempFile("localizedHeadZh", format);
        FesodSheet.write(chineseFile, LocalizedHeadData.class)
                .localizeHead(headMessageSource(), Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(new LocalizedHeadData("Alice", "A1")));
        Map<Integer, String> chineseHeader = readRawRows(chineseFile).get(0);
        Assertions.assertEquals("姓名", chineseHeader.get(0));
        Assertions.assertEquals("编号", chineseHeader.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldKeepHeadTemplatesLiteralWithoutLocalization(ExcelFormat format) throws Exception {
        File file = createTempFile("literalHead", format);
        FesodSheet.write(file, LocalizedHeadData.class)
                .sheet()
                .doWrite(Collections.singletonList(new LocalizedHeadData("Alice", "A1")));

        Map<Integer, String> header = readRawRows(file).get(0);
        Assertions.assertEquals("{head.name}", header.get(0));
        Assertions.assertEquals("{head.code}", header.get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shouldPropagateWorkbookLocalizationToSheetWrites(ExcelFormat format) throws Exception {
        File file = createTempFile("localizedHeadSheets", format);
        try (ExcelWriter excelWriter = FesodSheet.write(file, LocalizedHeadData.class)
                .localizeHead(headMessageSource(), Locale.ENGLISH)
                .build()) {
            excelWriter.write(
                    Collections.singletonList(new LocalizedHeadData("Alice", "A1")),
                    FesodSheet.writerSheet(0, "sheet0").build());
            excelWriter.write(
                    Collections.singletonList(new LocalizedHeadData("Bob", "B1")),
                    FesodSheet.writerSheet(1, "sheet1").build());
        }

        Assertions.assertEquals("Name", readRawRows(file, 0).get(0).get(0));
        Assertions.assertEquals("Name", readRawRows(file, 1).get(0).get(0));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldRoundTripLocalizedHeadersWithMatchingLocale(ExcelFormat format) throws Exception {
        StaticMessageSource source = headMessageSource();
        File file = createTempFile("localizedHeadRoundTrip", format);
        FesodSheet.write(file, LocalizedHeadData.class)
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(new LocalizedHeadData("Alice", "A1")));

        List<LocalizedHeadData> result = FesodSheet.read(file)
                .head(LocalizedHeadData.class)
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Alice", result.get(0).getName());
        Assertions.assertEquals("A1", result.get(0).getCode());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldRoundTripLocalizedDynamicHeadersWithMatchingLocale(ExcelFormat format) throws Exception {
        StaticMessageSource source = headMessageSource();
        File file = createTempFile("localizedDynamicHeadRoundTrip", format);
        FesodSheet.write(file)
                .head(head -> head.column("{" + NAME_CODE + "}").column("{" + CODE_CODE + "}"))
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(Arrays.asList("Alice", "A1")));

        List<Map<Integer, String>> result = FesodSheet.read(file)
                .head(head -> head.column("{" + NAME_CODE + "}").column("{" + CODE_CODE + "}"))
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Alice", result.get(0).get(0));
        Assertions.assertEquals("A1", result.get(0).get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldLocalizeEveryLevelOfMultiLevelHeadNames(ExcelFormat format) throws Exception {
        File englishFile = createTempFile("localizedMultiHeadEn", format);
        FesodSheet.write(englishFile, MultiLevelHeadData.class)
                .localizeHead(headMessageSource(), Locale.ENGLISH)
                .sheet()
                .doWrite(Collections.singletonList(new MultiLevelHeadData("Berlin", "Main St")));
        List<Map<Integer, String>> englishRows = readRawRows(englishFile);
        Assertions.assertEquals("Address", englishRows.get(0).get(0));
        Assertions.assertEquals("City", englishRows.get(1).get(0));
        Assertions.assertEquals("Street", englishRows.get(1).get(1));

        File chineseFile = createTempFile("localizedMultiHeadZh", format);
        FesodSheet.write(chineseFile, MultiLevelHeadData.class)
                .localizeHead(headMessageSource(), Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(new MultiLevelHeadData("Berlin", "Main St")));
        List<Map<Integer, String>> chineseRows = readRawRows(chineseFile);
        Assertions.assertEquals("地址", chineseRows.get(0).get(0));
        Assertions.assertEquals("城市", chineseRows.get(1).get(0));
        Assertions.assertEquals("街道", chineseRows.get(1).get(1));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldRoundTripMultiLevelLocalizedHeadersWithMatchingLocale(ExcelFormat format) throws Exception {
        StaticMessageSource source = headMessageSource();
        File file = createTempFile("localizedMultiHeadRoundTrip", format);
        FesodSheet.write(file, MultiLevelHeadData.class)
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(new MultiLevelHeadData("Berlin", "Main St")));

        List<MultiLevelHeadData> result = FesodSheet.read(file)
                .head(MultiLevelHeadData.class)
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Berlin", result.get(0).getCity());
        Assertions.assertEquals("Main St", result.get(0).getStreet());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldRoundTripMultiLevelLocalizedDynamicHeadersWithMatchingLocale(ExcelFormat format) throws Exception {
        StaticMessageSource source = headMessageSource();
        File file = createTempFile("localizedMultiDynamicHeadRoundTrip", format);
        FesodSheet.write(file)
                .head(head -> head.column("{" + ADDRESS_CODE + "}", "{" + CITY_CODE + "}")
                        .column("{" + STREET_CODE + "}", "{" + STREET_CODE + "}"))
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doWrite(Collections.singletonList(Arrays.asList("Berlin", "Main St")));

        List<Map<Integer, String>> result = FesodSheet.read(file)
                .head(head -> head.column("{" + ADDRESS_CODE + "}", "{" + CITY_CODE + "}")
                        .column("{" + STREET_CODE + "}", "{" + STREET_CODE + "}"))
                .localizeHead(source, Locale.SIMPLIFIED_CHINESE)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Berlin", result.get(0).get(0));
        Assertions.assertEquals("Main St", result.get(0).get(1));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalizedHeadData {

        @ExcelProperty("{" + NAME_CODE + "}")
        private String name;

        @ExcelProperty("{" + CODE_CODE + "}")
        private String code;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiLevelHeadData {

        @ExcelProperty({"{" + ADDRESS_CODE + "}", "{" + CITY_CODE + "}"})
        private String city;

        @ExcelProperty({"{" + ADDRESS_CODE + "}", "{" + STREET_CODE + "}"})
        private String street;
    }
}

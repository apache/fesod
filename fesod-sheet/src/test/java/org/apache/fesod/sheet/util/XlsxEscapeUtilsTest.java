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

package org.apache.fesod.sheet.util;

import java.util.stream.Stream;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * {@link XlsxEscapeUtils#utfDecode(String)} is a copy of POI's {@code XSSFRichTextString#utfDecode}, so every
 * expectation below is what POI produces - see {@link #matchesPoiForEveryInput(String)}, which pins that rather than
 * leaving it to the copy staying in step by luck.
 */
@Tag(Tags.UNIT)
class XlsxEscapeUtilsTest {

    static Stream<Arguments> decodesEscapes() {
        return Stream.of(
                Arguments.of("_x0041_", "A"),
                Arguments.of("_x0041_tail", "Atail"),
                Arguments.of("head_x0041_", "headA"),
                Arguments.of("head_x0041_tail", "headAtail"),
                Arguments.of("_x0041__x0042_", "AB"),
                Arguments.of("__x0041_", "_A"),
                Arguments.of("_x000D_", "\r"),
                // the hex digits are case insensitive, the leading x is not - see leavesTextWithoutAnEscapeAlone
                Arguments.of("_x00e9_", "é"),
                Arguments.of("_x00E9_", "é"),
                // an escape whose own underscore is escaped decodes to the literal text, not to the character
                Arguments.of("_x005F_x0041_", "_x0041_"),
                // as written by Excel - see the sharedStrings.xml of compatibility/t09.xlsx
                Arguments.of("SH_x005f_x000D_Z002", "SH_x000D_Z002"),
                // a valid escape is decoded even when a malformed one sits next to it
                Arguments.of("_x0041_ _xGHIJ_", "A _xGHIJ_"),
                // uppercase X is not an escape even once a lowercase one has taken the input past the _x fast path
                Arguments.of("_x0041_ _X0042_", "A _X0042_"));
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @MethodSource
    void decodesEscapes(String input, String expected) {
        Assertions.assertEquals(expected, XlsxEscapeUtils.utfDecode(input));
    }

    static Stream<String> leavesTextWithoutAnEscapeAlone() {
        return Stream.of(
                "",
                "plain text",
                "_X0041_", // uppercase X
                "_x041_", // three hex digits
                "_x00041_", // five hex digits
                "_x0041", // no closing underscore
                "_x00G1_", // a non-hex digit
                "x0041_", // no leading underscore
                "_x"); // the marker alone
    }

    @ParameterizedTest(name = "[{index}] {0} is left alone")
    @MethodSource
    void leavesTextWithoutAnEscapeAlone(String input) {
        Assertions.assertEquals(input, XlsxEscapeUtils.utfDecode(input));
    }

    @Test
    void returnsNullForNull() {
        Assertions.assertNull(XlsxEscapeUtils.utfDecode(null));
    }

    /**
     * POI is the reference for this decoding, so it is also the oracle: any input where the two disagree is a defect
     * here, whatever the table above says.
     */
    @ParameterizedTest(name = "[{index}] {0} decodes as POI does")
    @MethodSource
    void matchesPoiForEveryInput(String input) {
        Assertions.assertEquals(new XSSFRichTextString(input).getString(), XlsxEscapeUtils.utfDecode(input));
    }

    static Stream<String> matchesPoiForEveryInput() {
        return Stream.concat(
                decodesEscapes().map(arguments -> (String) arguments.get()[0]), leavesTextWithoutAnEscapeAlone());
    }
}

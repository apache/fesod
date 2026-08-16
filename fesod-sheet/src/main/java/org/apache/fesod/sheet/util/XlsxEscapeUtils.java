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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code _xHHHH_} escapes that xlsx uses for characters XML 1.0 forbids, defined by section 3.18.9 of the Office
 * Open XML spec.
 * <p>
 * Every xlsx read path decodes them, whichever part carries the text:
 * {@link org.apache.fesod.sheet.analysis.v07.handlers.sax.SharedStringsTableHandler SharedStringsTableHandler} for
 * {@code sharedStrings.xml}, and
 * {@link org.apache.fesod.sheet.analysis.v07.handlers.CellTagHandler CellTagHandler} for an inline or direct string
 * held by the cell itself. POI decodes on both of its own read paths, the DOM {@code XSSFCell} and the streaming
 * {@code XSSFSheetXMLHandler}.
 * <p>
 * The convention escapes itself: text that is literally {@code _x0041_} is stored as {@code _x005F_x0041_}, since
 * {@code _x005F_} is the escape for the underscore. Decoding it yields the literal back, not {@code A}.
 */
public class XlsxEscapeUtils {

    private static final Pattern UTF_PATTERN = Pattern.compile("_x([0-9A-Fa-f]{4})_");

    private XlsxEscapeUtils() {}

    /**
     * from poi XSSFRichTextString
     *
     * @param value the string to decode
     * @return the decoded string or null if the input string is null
     * <p>
     * For all characters which cannot be represented in XML as defined by the XML 1.0 specification,
     * the characters are escaped using the Unicode numerical character representation escape character
     * format _xHHHH_, where H represents a hexadecimal character in the character's value.
     * <p>
     * Example: The Unicode character 0D is invalid in an XML 1.0 document,
     * so it shall be escaped as <code>_x000D_</code>.
     * </p>
     * See section 3.18.9 in the OOXML spec.
     * @see org.apache.poi.xssf.usermodel.XSSFRichTextString#utfDecode(String)
     */
    public static String utfDecode(String value) {
        if (value == null || !value.contains("_x")) {
            return value;
        }

        StringBuilder buf = new StringBuilder();
        Matcher m = UTF_PATTERN.matcher(value);
        int idx = 0;
        while (m.find()) {
            int pos = m.start();
            if (pos > idx) {
                buf.append(value, idx, pos);
            }

            String code = m.group(1);
            int icode = Integer.decode("0x" + code);
            buf.append((char) icode);

            idx = m.end();
        }

        // small optimization: don't go via StringBuilder if not necessary,
        // the encodings are very rare, so we should almost always go via this shortcut.
        if (idx == 0) {
            return value;
        }

        buf.append(value.substring(idx));
        return buf.toString();
    }
}

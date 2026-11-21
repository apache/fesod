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

package org.apache.fesod.common.util;

/**
 * Utility methods for {@link CharSequence} and {@link String} handling.
 *
 * <p>All methods are null-safe and treat a {@code null} input the same way as documented
 * per method. This class is not intended to be instantiated.
 */
public class StringUtils {

    /**
     * Utility classes must never be instantiated.
     */
    private StringUtils() {}

    /**
     * A single space character, {@code " "}.
     */
    public static final String SPACE = " ";

    /**
     * The empty string {@code ""}.
     */
    public static final String EMPTY = "";

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be {@code null}
     * @return {@code true} if the CharSequence is {@code null} or empty, {@code false} otherwise
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be {@code null}
     * @return {@code true} if the CharSequence is {@code null}, empty or whitespace only,
     *         {@code false} otherwise
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be {@code null}
     * @return {@code true} if the CharSequence is not {@code null}, not empty and not whitespace
     *         only, {@code false} otherwise
     * @see #isBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Compares two {@link CharSequence}s for equality, returning {@code true} if they represent
     * the same sequence of characters.
     *
     * <p>{@code null} values are handled without throwing; two {@code null} references are
     * considered equal. The comparison is case-sensitive.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if both CharSequences are {@code null}, or are case-sensitively equal,
     *         {@code false} otherwise
     * @see Object#equals(Object)
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }

    /**
     * Compares a region of one {@link CharSequence} with a region of another, mirroring the
     * behavior of {@link String#regionMatches(boolean, int, String, int, int)} for
     * implementations that are not {@link String}.
     *
     * @param cs         the CharSequence being searched, must not be {@code null}
     * @param ignoreCase {@code true} to compare while ignoring case, {@code false} otherwise
     * @param thisStart  the start index within {@code cs}
     * @param substring  the CharSequence to look for, must not be {@code null}
     * @param start      the start index within {@code substring}
     * @param length     the number of characters to compare
     * @return {@code true} if the specified regions match, {@code false} otherwise
     */
    public static boolean regionMatches(
            final CharSequence cs,
            final boolean ignoreCase,
            final int thisStart,
            final CharSequence substring,
            final int start,
            final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode digits.
     * A decimal point is not a Unicode digit and returns false.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <p>Note that the method does not allow for a leading sign, either positive or negative.
     * Also, if a String passes the numeric test, it may still generate a NumberFormatException
     * when parsed by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range
     * for int or long respectively.</p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("\u0967\u0968\u0969")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * StringUtils.isNumeric("-123") = false
     * StringUtils.isNumeric("+123") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be {@code null}
     * @return {@code true} if the CharSequence is non-empty and contains only digits,
     *         {@code false} otherwise
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Strips whitespace from the start and end of a String.
     *
     * <p>This is similar to {@link String#trim()} but removes whitespace.
     * Whitespace is defined by {@link #isBlankChar(char)}.</p>
     *
     * <p>A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to strip, may be {@code null}
     * @return the stripped String, or {@code null} if the input is {@code null}
     */
    public static String strip(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int beginIndex = 0;
        int endIndex = str.length() - 1;

        while (beginIndex <= endIndex && isBlankChar(str.charAt(beginIndex))) {
            beginIndex++;
        }

        while (endIndex > beginIndex && isBlankChar(str.charAt(endIndex))) {
            endIndex--;
        }

        return str.substring(beginIndex, endIndex + 1);
    }

    /**
     * Checks whether a character should be treated as blank.
     *
     * <p>A character is considered blank if it is a Java whitespace character
     * ({@link Character#isWhitespace(char)}), a Unicode space character
     * ({@link Character#isSpaceChar(char)}), or one of the invisible/formatting characters
     * commonly found in spreadsheets ({@code \u202a}, {@code \ufeff}, {@code \u3164},
     * {@code \u2800}, {@code \u200c}, {@code \u180e}).</p>
     *
     * @param ch the character to check
     * @return {@code true} if the character is blank, {@code false} otherwise
     * @see Character#isWhitespace(char)
     * @see Character#isSpaceChar(char)
     */
    public static boolean isBlankChar(final char ch) {
        return Character.isSpaceChar(ch)
                || Character.isWhitespace(ch)
                || ch == '\u202a'
                || ch == '\ufeff'
                || ch == '\u3164'
                || ch == '\u2800'
                || ch == '\u200c'
                || ch == '\u180e';
    }
}

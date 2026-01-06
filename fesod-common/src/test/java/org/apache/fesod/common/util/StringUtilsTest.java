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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link StringUtils}
 */
class StringUtilsTest {

    private abstract static class RunTest {

        abstract boolean invoke();

        void run(final TestData data, final String id) {
            if (data.throwable != null) {
                Assertions.assertThrows(data.throwable, this::invoke, id + " Expected " + data.throwable);
            } else {
                final boolean stringCheck = invoke();
                Assertions.assertEquals(data.expected, stringCheck, id + " Failed test " + data);
            }
        }
    }

    private static class TestData {
        final String source;
        final boolean ignoreCase;
        final int toffset;
        final String other;
        final int ooffset;
        final int len;
        final boolean expected;
        final Class<? extends Throwable> throwable;

        TestData(
                final String source,
                final boolean ignoreCase,
                final int toffset,
                final String other,
                final int ooffset,
                final int len,
                final boolean expected) {
            this.source = source;
            this.ignoreCase = ignoreCase;
            this.toffset = toffset;
            this.other = other;
            this.ooffset = ooffset;
            this.len = len;
            this.expected = expected;
            this.throwable = null;
        }

        TestData(
                final String source,
                final boolean ignoreCase,
                final int toffset,
                final String other,
                final int ooffset,
                final int len,
                final Class<? extends Throwable> throwable) {
            this.source = source;
            this.ignoreCase = ignoreCase;
            this.toffset = toffset;
            this.other = other;
            this.ooffset = ooffset;
            this.len = len;
            this.expected = false;
            this.throwable = throwable;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(source).append("[").append(toffset).append("]");
            sb.append(ignoreCase ? " caseblind " : " samecase ");
            sb.append(other).append("[").append(ooffset).append("]");
            sb.append(" ").append(len).append(" => ");
            if (throwable != null) {
                sb.append(throwable);
            } else {
                sb.append(expected);
            }
            return sb.toString();
        }
    }

    private static final TestData[] TEST_DATA = {
        // @formatter:off
        //           Source  IgnoreCase Offset Other  Offset Length Result
        new TestData("", true, -1, "", -1, -1, false),
        new TestData("", true, 0, "", 0, 1, false),
        new TestData("a", true, 0, "abc", 0, 0, true),
        new TestData("a", true, 0, "abc", 0, 1, true),
        new TestData("a", true, 0, null, 0, 0, NullPointerException.class),
        new TestData(null, true, 0, null, 0, 0, NullPointerException.class),
        new TestData(null, true, 0, "", 0, 0, NullPointerException.class),
        new TestData("Abc", true, 0, "abc", 0, 3, true),
        new TestData("Abc", false, 0, "abc", 0, 3, false),
        new TestData("Abc", true, 1, "abc", 1, 2, true),
        new TestData("Abc", false, 1, "abc", 1, 2, true),
        new TestData("Abcd", true, 1, "abcD", 1, 2, true),
        new TestData("Abcd", false, 1, "abcD", 1, 2, true),
        // @formatter:on
    };

    @Test
    void test_isEmpty() {
        Assertions.assertTrue(StringUtils.isEmpty(null));
        Assertions.assertTrue(StringUtils.isEmpty(""));
        Assertions.assertFalse(StringUtils.isEmpty(" "));
        Assertions.assertFalse(StringUtils.isEmpty("bob"));
        Assertions.assertFalse(StringUtils.isEmpty("  bob  "));
    }

    @Test
    void test_isBlank() {
        Assertions.assertTrue(StringUtils.isBlank(null));
        Assertions.assertTrue(StringUtils.isBlank(""));
        Assertions.assertTrue(StringUtils.isBlank(" "));
        Assertions.assertFalse(StringUtils.isBlank("bob"));
        Assertions.assertFalse(StringUtils.isBlank("  bob  "));
    }

    @Test
    void test_isNotBlank() {
        Assertions.assertFalse(StringUtils.isNotBlank(null));
        Assertions.assertFalse(StringUtils.isNotBlank(""));
        Assertions.assertFalse(StringUtils.isNotBlank(" "));
        Assertions.assertTrue(StringUtils.isNotBlank("bob"));
        Assertions.assertTrue(StringUtils.isNotBlank("  bob  "));
    }

    @Test
    void test_equals() {
        Assertions.assertTrue(StringUtils.equals(null, null));
        Assertions.assertFalse(StringUtils.equals(null, "abc"));
        Assertions.assertFalse(StringUtils.equals("abc", null));
        Assertions.assertTrue(StringUtils.equals("abc", "abc"));
        Assertions.assertFalse(StringUtils.equals("abc", "ABC"));
    }

    @Test
    void test_isNumeric() {
        Assertions.assertFalse(StringUtils.isNumeric(null));
        Assertions.assertFalse(StringUtils.isNumeric(""));
        Assertions.assertFalse(StringUtils.isNumeric(" "));
        Assertions.assertFalse(StringUtils.isNumeric("a"));
        Assertions.assertFalse(StringUtils.isNumeric("A"));
        Assertions.assertFalse(StringUtils.isNumeric("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertFalse(StringUtils.isNumeric("ham kso"));
        Assertions.assertTrue(StringUtils.isNumeric("1"));
        Assertions.assertTrue(StringUtils.isNumeric("1000"));
        Assertions.assertTrue(StringUtils.isNumeric("\u0967\u0968\u0969"));
        Assertions.assertFalse(StringUtils.isNumeric("\u0967\u0968 \u0969"));
        Assertions.assertFalse(StringUtils.isNumeric("2.3"));
        Assertions.assertFalse(StringUtils.isNumeric("10 00"));
        Assertions.assertFalse(StringUtils.isNumeric("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isNumeric("_"));
        Assertions.assertFalse(StringUtils.isNumeric("hkHKHik*khbkuh"));
        Assertions.assertFalse(StringUtils.isNumeric("+123"));
        Assertions.assertFalse(StringUtils.isNumeric("-123"));
    }

    @Test
    void test_regionMatches() {
        for (final TestData data : TEST_DATA) {
            new RunTest() {
                @Override
                boolean invoke() {
                    return data.source.regionMatches(data.ignoreCase, data.toffset, data.other, data.ooffset, data.len);
                }
            }.run(data, "String");
            new RunTest() {
                @Override
                boolean invoke() {
                    return StringUtils.regionMatches(
                            data.source, data.ignoreCase, data.toffset, data.other, data.ooffset, data.len);
                }
            }.run(data, "CSString");
            new RunTest() {
                @Override
                boolean invoke() {
                    return StringUtils.regionMatches(
                            new StringBuilder(data.source),
                            data.ignoreCase,
                            data.toffset,
                            data.other,
                            data.ooffset,
                            data.len);
                }
            }.run(data, "CSNonString");
        }
    }

    @Test
    void stripTest() {
        Assertions.assertNull(StringUtils.strip(null));
        Assertions.assertEquals("", StringUtils.strip(""));
        Assertions.assertEquals("", StringUtils.strip("   "));
        Assertions.assertEquals("abc", StringUtils.strip("abc"));
        Assertions.assertEquals("abc", StringUtils.strip("  abc"));
        Assertions.assertEquals("abc", StringUtils.strip("abc  "));
        Assertions.assertEquals("abc", StringUtils.strip(" abc "));
        Assertions.assertEquals("abc", StringUtils.strip("　abc　"));
        Assertions.assertEquals("abc", StringUtils.strip(" abc　"));
        Assertions.assertEquals("ab　c", StringUtils.strip(" ab　c　"));
        Assertions.assertEquals("ab c", StringUtils.strip(" ab c "));
    }

    @Test
    void isBlankCharTest() {
        Assertions.assertTrue(StringUtils.isBlankChar(' '));
        Assertions.assertTrue(StringUtils.isBlankChar('　'));
        Assertions.assertTrue(StringUtils.isBlankChar('\ufeff'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u202a'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u3164'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u2800'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u200c'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u180e'));
    }
}

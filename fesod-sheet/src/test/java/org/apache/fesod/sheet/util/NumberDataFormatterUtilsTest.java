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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Locale;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link NumberDataFormatterUtils}
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class NumberDataFormatterUtilsTest {

    @Mock
    private GlobalConfiguration globalConfiguration;

    @AfterEach
    void tearDown() {
        NumberDataFormatterUtils.removeThreadLocalCache();
    }

    @Test
    void test_format_withConfig_Locale() {
        // Setup
        Mockito.when(globalConfiguration.getLocale()).thenReturn(Locale.GERMANY);
        Mockito.when(globalConfiguration.getUse1904windowing()).thenReturn(false);
        Mockito.when(globalConfiguration.getUseScientificFormat()).thenReturn(false);

        BigDecimal data = new BigDecimal("1234.56");
        String formatString = "0.00";

        // Execute
        String result = NumberDataFormatterUtils.format(data, null, formatString, globalConfiguration);

        // Verify
        Assertions.assertEquals("1234,56", result);
    }

    @Test
    void test_format_nullConfig() {
        BigDecimal data = new BigDecimal("1234.56");
        String formatString = "0.00";

        String result = NumberDataFormatterUtils.format(data, null, formatString, null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("1234"));
    }

    @Test
    void test_format_scientific() {
        // 1.23E4 -> 12300
        BigDecimal data = new BigDecimal("1.23E+4");
        String formatString = "0";

        String result = NumberDataFormatterUtils.format(data, null, formatString, false, Locale.US, false);

        Assertions.assertEquals("12300", result);
    }

    @Test
    void test_format_replacesCachedFormatterWhenLocaleChanges() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.KOREA);
            String korean = NumberDataFormatterUtils.format(
                    new BigDecimal("0.75"), (short) 18, "h:mm AM/PM", false, null, false);
            String chinese = NumberDataFormatterUtils.format(
                    new BigDecimal("0.75"), (short) 18, "h:mm AM/PM", false, Locale.CHINA, false);

            Assertions.assertTrue(korean.endsWith("오후"));
            Assertions.assertTrue(chinese.endsWith("下午"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    void test_format_replacesCachedFormatterWhenUse1904WindowingChanges() {
        String date1900 = NumberDataFormatterUtils.format(
                BigDecimal.ONE, (short) 14, "yyyy-mm-dd", false, Locale.US, false);
        String date1904 = NumberDataFormatterUtils.format(
                BigDecimal.ONE, (short) 14, "yyyy-mm-dd", true, Locale.US, false);

        Assertions.assertEquals("1900-01-01", date1900);
        Assertions.assertEquals("1904-01-02", date1904);
    }

    @Test
    void test_format_replacesCachedFormatterWhenScientificFormatChanges() {
        BigDecimal data = new BigDecimal("100000000000");
        String plain = NumberDataFormatterUtils.format(data, null, "General", false, Locale.US, false);
        String scientific = NumberDataFormatterUtils.format(data, null, "General", false, Locale.US, true);

        Assertions.assertEquals("100000000000", plain);
        Assertions.assertEquals("1E+11", scientific);
    }

    @Test
    void test_ThreadLocal_Cache_And_Remove() throws NoSuchFieldException, IllegalAccessException {
        Field field = NumberDataFormatterUtils.class.getDeclaredField("DATA_FORMATTER_THREAD_LOCAL");
        field.setAccessible(true);

        ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);

        Assertions.assertNull(threadLocal.get());

        NumberDataFormatterUtils.format(new BigDecimal("1"), null, "0", false, Locale.US, false);

        Object cachedFormatter = threadLocal.get();
        Assertions.assertNotNull(cachedFormatter);

        NumberDataFormatterUtils.format(new BigDecimal("2"), null, "0", false, Locale.US, false);
        Assertions.assertSame(cachedFormatter, threadLocal.get());

        NumberDataFormatterUtils.removeThreadLocalCache();

        Assertions.assertNull(threadLocal.get());
    }
}

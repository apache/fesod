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
import org.apache.fesod.sheet.metadata.format.DataFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link NumberDataFormatterUtils}
 */
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
    void test_ThreadLocal_Cache_And_Remove() throws NoSuchFieldException, IllegalAccessException {
        Field field = NumberDataFormatterUtils.class.getDeclaredField("DATA_FORMATTER_THREAD_LOCAL");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        ThreadLocal<DataFormatter> threadLocal = (ThreadLocal<DataFormatter>) field.get(null);

        Assertions.assertNull(threadLocal.get());

        NumberDataFormatterUtils.format(new BigDecimal("1"), null, "0", false, Locale.US, false);

        DataFormatter cachedFormatter = threadLocal.get();
        Assertions.assertNotNull(cachedFormatter);

        NumberDataFormatterUtils.format(new BigDecimal("2"), null, "0", false, Locale.US, false);
        Assertions.assertSame(cachedFormatter, threadLocal.get());

        NumberDataFormatterUtils.removeThreadLocalCache();

        Assertions.assertNull(threadLocal.get());
    }
}

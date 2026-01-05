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
 * Tests {@link ValidateUtils}
 */
class ValidateUtilsTest {

    @Test
    void test_isTrue_long() {
        Assertions.assertDoesNotThrow(() -> ValidateUtils.isTrue(true, "Error count: %d", 100L));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ValidateUtils.isTrue(false, "Error count: %d", 50L);
        });
        Assertions.assertEquals("Error count: 50", exception.getMessage());
    }

    @Test
    void test_isTrue_double() {
        Assertions.assertDoesNotThrow(() -> ValidateUtils.isTrue(true, "Value: %.1f", 1.5));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ValidateUtils.isTrue(false, "Value: %.2f", 3.1415);
        });
        Assertions.assertEquals("Value: 3.14", exception.getMessage());
    }

    @Test
    void test_isTrue_objects() {
        Assertions.assertDoesNotThrow(() -> ValidateUtils.isTrue(true, "Failed for %s and %s", "A", "B"));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ValidateUtils.isTrue(false, "User %s has id %d", "Admin", 123);
        });
        Assertions.assertEquals("User Admin has id 123", exception.getMessage());
    }

    @Test
    void test_isTrue() {
        Assertions.assertDoesNotThrow(() -> ValidateUtils.isTrue(1 + 1 == 2));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ValidateUtils.isTrue(false);
        });
        Assertions.assertEquals("The validated expression is false", exception.getMessage());
    }

    @Test
    void test_notNull() {
        String testObj = "NotNull";

        String result = ValidateUtils.notNull(testObj);
        Assertions.assertEquals(testObj, result);

        NullPointerException exception = Assertions.assertThrows(NullPointerException.class, () -> {
            ValidateUtils.notNull(null);
        });
        Assertions.assertEquals("The validated object is null", exception.getMessage());
    }

    @Test
    void test_notNull_withMessage() {
        String testObj = "NotNull";

        String result = ValidateUtils.notNull(testObj, "Must not be null");
        Assertions.assertEquals(testObj, result);

        NullPointerException exception = Assertions.assertThrows(NullPointerException.class, () -> {
            ValidateUtils.notNull(null, "The field %s cannot be null", "username");
        });
        Assertions.assertEquals("The field username cannot be null", exception.getMessage());
    }
}

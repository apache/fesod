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
 * Tests {@link BooleanUtils}
 */
class BooleanUtilsTest {

    @Test
    void test_valueOf() {
        Assertions.assertTrue(BooleanUtils.valueOf("1"));
        Assertions.assertFalse(BooleanUtils.valueOf(""));
        Assertions.assertFalse(BooleanUtils.valueOf(null));
    }

    @Test
    void test_isTrue() {
        Assertions.assertTrue(BooleanUtils.isTrue(Boolean.TRUE));
        Assertions.assertFalse(BooleanUtils.isTrue(Boolean.FALSE));
        Assertions.assertFalse(BooleanUtils.isTrue(null));
    }

    @Test
    void test_isNotTrue() {
        Assertions.assertFalse(BooleanUtils.isNotTrue(Boolean.TRUE));
        Assertions.assertTrue(BooleanUtils.isNotTrue(Boolean.FALSE));
        Assertions.assertTrue(BooleanUtils.isNotTrue(null));
    }

    @Test
    void test_isFalse() {
        Assertions.assertFalse(BooleanUtils.isFalse(Boolean.TRUE));
        Assertions.assertTrue(BooleanUtils.isFalse(Boolean.FALSE));
        Assertions.assertFalse(BooleanUtils.isFalse(null));
    }

    @Test
    void test_isNotFalse() {
        Assertions.assertTrue(BooleanUtils.isNotFalse(Boolean.TRUE));
        Assertions.assertFalse(BooleanUtils.isNotFalse(Boolean.FALSE));
        Assertions.assertTrue(BooleanUtils.isNotFalse(null));
    }
}

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
 * Tests {@link PositionUtils}
 */
class PositionUtilsTest {

    @Test
    void test_getRowByRowTag() {
        Assertions.assertEquals(0, PositionUtils.getRowByRowTag("1", null));
        Assertions.assertEquals(9, PositionUtils.getRowByRowTag("10", 5));
        Assertions.assertEquals(6, PositionUtils.getRowByRowTag(null, 5));
        Assertions.assertEquals(0, PositionUtils.getRowByRowTag(null, null));
    }

    @Test
    void test_getRow() {
        Assertions.assertEquals(0, PositionUtils.getRow("A1"));
        Assertions.assertEquals(9, PositionUtils.getRow("B10"));
        Assertions.assertEquals(99, PositionUtils.getRow("AA100"));

        Assertions.assertEquals(4, PositionUtils.getRow("$A$5"));
        Assertions.assertEquals(19, PositionUtils.getRow("C$20"));

        Assertions.assertEquals(-1, PositionUtils.getRow(null));
    }

    @Test
    void test_getCol() {
        // A -> 0
        Assertions.assertEquals(0, PositionUtils.getCol("A1", null));
        // B -> 1
        Assertions.assertEquals(1, PositionUtils.getCol("B10", null));
        // Z -> 25
        Assertions.assertEquals(25, PositionUtils.getCol("Z1", null));

        // AA -> 26
        Assertions.assertEquals(26, PositionUtils.getCol("AA1", null));
        // AZ -> 51
        Assertions.assertEquals(51, PositionUtils.getCol("AZ1", null));

        Assertions.assertEquals(0, PositionUtils.getCol("$A1", null));
        Assertions.assertEquals(1, PositionUtils.getCol("$B$2", null));

        Assertions.assertEquals(5, PositionUtils.getCol(null, 4));
        Assertions.assertEquals(0, PositionUtils.getCol(null, null)); // -1 + 1
    }

    @Test
    void test_getRow_invalid_format() {
        Assertions.assertThrows(NumberFormatException.class, () -> {
            PositionUtils.getRow("ABC");
        });
    }

    @Test
    void test_getCol_case_insensitivity() {
        Assertions.assertEquals(0, PositionUtils.getCol("a1", null));
        Assertions.assertEquals(27, PositionUtils.getCol("ab1", null));
    }
}

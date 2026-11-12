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

package org.apache.fesod.sheet.write.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class MapRowDataTest {

    @Test
    void emptyMapHasNoPositions() {
        MapRowData row = new MapRowData(Collections.emptyMap());
        assertEquals(0, row.size());
        assertTrue(row.isEmpty());
        assertNull(row.get(0));
    }

    @Test
    void sparseMapSizeIncludesGapsRegardlessOfIterationOrder() {
        Map<Integer, String> values = new LinkedHashMap<>();
        values.put(4, "FIFTH");
        values.put(1, "SECOND");
        MapRowData row = new MapRowData(values);
        assertEquals(5, row.size());
        assertFalse(row.isEmpty());
        assertNull(row.get(0));
        assertEquals("SECOND", row.get(1));
        assertNull(row.get(2));
        assertEquals("FIFTH", row.get(4));
    }

    @Test
    void trailingNullValueStillHasAPosition() {
        MapRowData row = new MapRowData(Collections.singletonMap(3, null));
        assertEquals(4, row.size());
        assertFalse(row.isEmpty());
        assertNull(row.get(3));
    }

    @Test
    void largestRepresentableSpanDoesNotOverflow() {
        MapRowData row = new MapRowData(Collections.singletonMap(Integer.MAX_VALUE - 1, "LAST"));
        assertEquals(Integer.MAX_VALUE, row.size());
        assertEquals("LAST", row.get(Integer.MAX_VALUE - 1));
    }

    @Test
    void rejectsAnIndexWhoseSpanCannotBeRepresented() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MapRowData(Collections.singletonMap(Integer.MAX_VALUE, "LAST")));
    }
}

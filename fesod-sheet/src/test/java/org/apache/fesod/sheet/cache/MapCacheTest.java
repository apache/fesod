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

package org.apache.fesod.sheet.cache;

import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MapCache}.
 */
@Tag(Tags.UNIT)
class MapCacheTest {

    @Test
    void storesValuesByInsertionOrder() {
        MapCache cache = new MapCache();
        cache.init(null);
        cache.put("alpha");
        cache.put("beta");
        cache.putFinished();

        Assertions.assertEquals("alpha", cache.get(0));
        Assertions.assertEquals("beta", cache.get(1));
        Assertions.assertDoesNotThrow(cache::destroy);
    }

    @Test
    void storesNullAndEmptyString() {
        MapCache cache = new MapCache();
        cache.put(null);
        cache.put("");

        Assertions.assertNull(cache.get(0));
        Assertions.assertEquals("", cache.get(1));
    }

    @Test
    void getReturnsNullForNullOrNegativeKey() {
        MapCache cache = new MapCache();
        cache.put("alpha");

        Assertions.assertNull(cache.get(null));
        Assertions.assertNull(cache.get(-1));
    }

    @Test
    void getThrowsWhenKeyIsOutOfRange() {
        MapCache cache = new MapCache();

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> cache.get(0));

        cache.put("alpha");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> cache.get(1));
    }
}

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Ehcache}.
 */
@Tag(Tags.UNIT)
class EhcacheTest {

    private Ehcache cache;

    @AfterEach
    void tearDown() {
        if (cache != null) {
            cache.destroy();
            cache = null;
        }
    }

    @Test
    void storesRemainderAfterPutFinished() {
        cache = newCache();
        cache.put("alpha");
        cache.put("beta");
        cache.putFinished();

        Assertions.assertEquals("alpha", cache.get(0));
        Assertions.assertEquals("beta", cache.get(1));
    }

    @Test
    void storesNullAndEmptyString() {
        cache = newCache();
        cache.put(null);
        cache.put("");
        cache.putFinished();

        Assertions.assertNull(cache.get(0));
        Assertions.assertEquals("", cache.get(1));
    }

    @Test
    void getReturnsNullForNullOrNegativeKey() {
        cache = newCache();
        cache.put("alpha");
        cache.putFinished();

        Assertions.assertNull(cache.get(null));
        Assertions.assertNull(cache.get(-1));
    }

    @Test
    void flushesAutomaticallyAtBatchBoundary() {
        cache = newCache();
        for (int i = 0; i < Ehcache.BATCH_COUNT; i++) {
            cache.put("v" + i);
        }

        Assertions.assertEquals("v0", cache.get(0));
        Assertions.assertEquals("v99", cache.get(Ehcache.BATCH_COUNT - 1));
        Assertions.assertDoesNotThrow(() -> cache.putFinished());
    }

    @Test
    void readsAcrossBatchesAfterCacheMiss() {
        cache = new Ehcache(null, 5);
        cache.init(null);
        int total = Ehcache.BATCH_COUNT + 25;
        for (int i = 0; i < total; i++) {
            cache.put("v" + i);
        }
        cache.putFinished();

        Assertions.assertEquals("v0", cache.get(0));
        Assertions.assertEquals("v100", cache.get(Ehcache.BATCH_COUNT));
        Assertions.assertEquals("v124", cache.get(total - 1));
    }

    @Test
    void deprecatedSizeConstructorStillWorks() {
        cache = new Ehcache(1, 1);
        cache.init(null);
        cache.put("alpha");
        cache.putFinished();

        Assertions.assertEquals("alpha", cache.get(0));
    }

    @Test
    void instancesDoNotShareEntries() {
        cache = newCache();
        Ehcache second = newCache();
        try {
            cache.put("first");
            cache.putFinished();
            second.put("second");
            second.putFinished();

            Assertions.assertEquals("first", cache.get(0));
            Assertions.assertEquals("second", second.get(0));
        } finally {
            second.destroy();
        }
    }

    private Ehcache newCache() {
        Ehcache created = new Ehcache(null, 20);
        created.init(null);
        return created;
    }
}

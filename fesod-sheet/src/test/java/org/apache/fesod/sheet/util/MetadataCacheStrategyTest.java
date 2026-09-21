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

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MetadataCacheStrategy}
 */
@Tag(Tags.UNIT)
class MetadataCacheStrategyTest {

    @Test
    void test_ThreadLocalCache_clear_detachesTheMapFromTheThread() {
        AtomicInteger createdMaps = new AtomicInteger();
        MetadataCacheStrategy.ThreadLocalCache<String, String> cache =
                new MetadataCacheStrategy.ThreadLocalCache<>(() -> {
                    createdMaps.incrementAndGet();
                    return new HashMap<>();
                });

        cache.get("key", key -> "value");
        cache.get("key", key -> "value");
        Assertions.assertEquals(1, createdMaps.get());

        cache.clear();
        cache.get("key", key -> "value");
        Assertions.assertEquals(2, createdMaps.get());
    }
}

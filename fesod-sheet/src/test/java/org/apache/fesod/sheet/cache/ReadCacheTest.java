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
 * Unit tests for {@link ReadCache} default methods.
 */
@Tag(Tags.UNIT)
class ReadCacheTest {

    @Test
    void defaultLifecycleMethodsAreNoOps() {
        ReadCache cache = key -> "value-" + key;

        Assertions.assertDoesNotThrow(() -> cache.init(null));
        Assertions.assertDoesNotThrow(() -> cache.put("ignored"));
        Assertions.assertDoesNotThrow(cache::putFinished);
        Assertions.assertDoesNotThrow(cache::destroy);
        Assertions.assertEquals("value-0", cache.get(0));
    }

    @Test
    void getIsTheOnlyRequiredMethod() {
        ReadCache cache = key -> null;

        Assertions.assertNull(cache.get(1));
        Assertions.assertDoesNotThrow(() -> cache.init(null));
        Assertions.assertDoesNotThrow(() -> cache.put(null));
        Assertions.assertDoesNotThrow(cache::putFinished);
        Assertions.assertDoesNotThrow(cache::destroy);
    }
}

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

package org.apache.fesod.sheet.converters;

import java.util.Map;
import org.apache.fesod.sheet.converters.ConverterKeyBuild.ConverterKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultConverterLoaderTest {

    @Test
    void loadDefaultWriteConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultWriteConverter(),
                DefaultConverterLoader.copyDefaultWriteConverter());
    }

    @Test
    void loadDefaultReadConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultReadConverter(),
                DefaultConverterLoader.copyDefaultReadConverter());
    }

    @Test
    void loadAllConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadAllConverter(), DefaultConverterLoader.copyAllConverter());
    }

    private static void assertLoadIsImmutableAndCopyIsMutable(
            Map<ConverterKey, Converter<?>> loaded, Map<ConverterKey, Converter<?>> copy) {
        Map.Entry<ConverterKey, Converter<?>> entry = loaded.entrySet().iterator().next();

        Assertions.assertThrows(
                UnsupportedOperationException.class, () -> loaded.put(entry.getKey(), entry.getValue()));

        copy.remove(entry.getKey());
        Assertions.assertFalse(copy.containsKey(entry.getKey()));
        Assertions.assertTrue(loaded.containsKey(entry.getKey()));
    }
}

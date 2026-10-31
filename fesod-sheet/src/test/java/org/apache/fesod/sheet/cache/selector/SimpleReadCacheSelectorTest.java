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

package org.apache.fesod.sheet.cache.selector;

import java.io.IOException;
import java.io.InputStream;
import org.apache.fesod.sheet.cache.Ehcache;
import org.apache.fesod.sheet.cache.MapCache;
import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

@Tag(Tags.UNIT)
class SimpleReadCacheSelectorTest {

    @ParameterizedTest
    @CsvSource({"0, false", "4999999, false", "5000000, true", "5000001, true", "2147483648, true"})
    void selects_cache_using_default_threshold_without_opening_stream(long size, boolean useEhcache)
            throws IOException {
        PackagePart part = Mockito.mock(PackagePart.class);
        Mockito.when(part.getSize()).thenReturn(size);

        ReadCache cache = new SimpleReadCacheSelector().readCache(part);

        Assertions.assertEquals(useEhcache ? Ehcache.class : MapCache.class, cache.getClass());
        Mockito.verify(part, Mockito.never()).getInputStream();
    }

    @ParameterizedTest
    @CsvSource({"999999, false", "1000000, true", "1000001, true"})
    void selects_cache_using_custom_threshold_without_opening_stream(long size, boolean useEhcache) throws IOException {
        SimpleReadCacheSelector selector = new SimpleReadCacheSelector();
        selector.setMaxUseMapCacheSize(1L);
        PackagePart part = Mockito.mock(PackagePart.class);
        Mockito.when(part.getSize()).thenReturn(size);

        ReadCache cache = selector.readCache(part);

        Assertions.assertEquals(useEhcache ? Ehcache.class : MapCache.class, cache.getClass());
        Mockito.verify(part, Mockito.never()).getInputStream();
    }

    @ParameterizedTest
    @CsvSource({"0, false", "4999999, false", "5000000, true", "5000001, true"})
    void selects_cache_using_available_bytes_when_size_is_unknown(int available, boolean useEhcache)
            throws IOException {
        PackagePart part = Mockito.mock(PackagePart.class);
        Mockito.when(part.getSize()).thenReturn(-1L);
        InputStream stream = Mockito.mock(InputStream.class);
        Mockito.when(part.getInputStream()).thenReturn(stream);
        Mockito.when(stream.available()).thenReturn(available);

        ReadCache cache = new SimpleReadCacheSelector().readCache(part);

        Assertions.assertEquals(useEhcache ? Ehcache.class : MapCache.class, cache.getClass());
        Mockito.verify(part).getInputStream();
        Mockito.verify(stream).available();
    }

    @Test
    void falls_back_to_map_cache_when_opening_stream_throws() throws IOException {
        PackagePart part = Mockito.mock(PackagePart.class);
        Mockito.when(part.getSize()).thenReturn(-1L);
        Mockito.when(part.getInputStream()).thenThrow(new IOException("Cannot open shared strings"));

        ReadCache cache = new SimpleReadCacheSelector().readCache(part);

        Assertions.assertInstanceOf(MapCache.class, cache);
        Mockito.verify(part).getInputStream();
    }

    @Test
    void falls_back_to_map_cache_when_available_throws() throws IOException {
        PackagePart part = Mockito.mock(PackagePart.class);
        Mockito.when(part.getSize()).thenReturn(-1L);
        InputStream stream = Mockito.mock(InputStream.class);
        Mockito.when(part.getInputStream()).thenReturn(stream);
        Mockito.when(stream.available()).thenThrow(new IOException("Cannot determine shared strings size"));

        ReadCache cache = new SimpleReadCacheSelector().readCache(part);

        Assertions.assertInstanceOf(MapCache.class, cache);
        Mockito.verify(part).getInputStream();
        Mockito.verify(stream).available();
    }
}

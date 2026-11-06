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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

/**
 * Unit tests for {@link SimpleReadCacheSelector}.
 */
@Tag(Tags.UNIT)
class SimpleReadCacheSelectorTest {

    private static final long DEFAULT_THRESHOLD_BYTES = 5_000_000L;

    @Test
    void selectsMapCacheBelowDefaultThreshold() throws Exception {
        PackagePart packagePart = packagePartWithSize(DEFAULT_THRESHOLD_BYTES - 1);

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Assertions.assertInstanceOf(MapCache.class, cache);
        Mockito.verify(packagePart, Mockito.never()).getInputStream();
    }

    @ParameterizedTest
    @ValueSource(longs = {DEFAULT_THRESHOLD_BYTES, DEFAULT_THRESHOLD_BYTES + 1})
    void selectsEhcacheAtAndAboveDefaultThreshold(long size) throws Exception {
        PackagePart packagePart = packagePartWithSize(size);

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Assertions.assertInstanceOf(Ehcache.class, cache);
        Mockito.verify(packagePart, Mockito.never()).getInputStream();
    }

    @Test
    void appliesCustomThreshold() throws Exception {
        SimpleReadCacheSelector selector = new SimpleReadCacheSelector();
        selector.setMaxUseMapCacheSize(1L);
        PackagePart packagePart = packagePartWithSize(1_000_000L);

        ReadCache cache = selector.readCache(packagePart);

        Assertions.assertInstanceOf(Ehcache.class, cache);
        Mockito.verify(packagePart, Mockito.never()).getInputStream();
    }

    @Test
    void usesInputStreamSizeWhenPackagePartSizeIsUnknown() throws Exception {
        PackagePart packagePart = packagePartWithSize(-1);
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(packagePart.getInputStream()).thenReturn(inputStream);
        Mockito.when(inputStream.available()).thenReturn((int) DEFAULT_THRESHOLD_BYTES);

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Assertions.assertInstanceOf(Ehcache.class, cache);
        Mockito.verify(packagePart).getInputStream();
        Mockito.verify(inputStream).available();
    }

    @Test
    void fallsBackToMapCacheWhenInputStreamSizeCannotBeRead() throws Exception {
        PackagePart packagePart = packagePartWithSize(-1);
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(packagePart.getInputStream()).thenReturn(inputStream);
        Mockito.when(inputStream.available()).thenThrow(new IOException("unavailable"));

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Assertions.assertInstanceOf(MapCache.class, cache);
        Mockito.verify(packagePart).getInputStream();
        Mockito.verify(inputStream).available();
    }

    private PackagePart packagePartWithSize(long size) {
        PackagePart packagePart = Mockito.mock(PackagePart.class);
        Mockito.when(packagePart.getSize()).thenReturn(size);
        return packagePart;
    }
}

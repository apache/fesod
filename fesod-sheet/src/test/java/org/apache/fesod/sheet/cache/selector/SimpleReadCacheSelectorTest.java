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

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fesod.sheet.cache.MapCache;
import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link SimpleReadCacheSelector}.
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class SimpleReadCacheSelectorTest {

    @Test
    void readCache_closesInputStream_whenPackagePartSizeUnknown() throws Exception {
        PackagePart packagePart = Mockito.mock(PackagePart.class);
        CloseTrackingInputStream inputStream =
                new CloseTrackingInputStream(new ByteArrayInputStream(new byte[] {1, 2, 3, 4}));
        Mockito.when(packagePart.getSize()).thenReturn(-1L);
        Mockito.when(packagePart.getInputStream()).thenReturn(inputStream);

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Assertions.assertTrue(inputStream.isClosed());
        Assertions.assertInstanceOf(MapCache.class, cache);
    }

    @Test
    void readCache_doesNotOpenInputStream_whenPackagePartSizeIsKnown() throws Exception {
        PackagePart packagePart = Mockito.mock(PackagePart.class);
        Mockito.when(packagePart.getSize()).thenReturn(1024L);

        ReadCache cache = new SimpleReadCacheSelector().readCache(packagePart);

        Mockito.verify(packagePart, Mockito.never()).getInputStream();
        Assertions.assertInstanceOf(MapCache.class, cache);
    }

    private static final class CloseTrackingInputStream extends FilterInputStream {
        private boolean closed;

        private CloseTrackingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        private boolean isClosed() {
            return closed;
        }
    }
}

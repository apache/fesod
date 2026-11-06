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

package org.apache.fesod.sheet.analysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests the failure recording of {@link ExcelAnalyserImpl#finish()}.
 */
@Tag(Tags.UNIT)
class ExcelAnalyserImplTest {

    /**
     * When two cleanup steps fail (CSV parser close and stream close), the first failure is the
     * cause and the later one is attached as suppressed, instead of overwriting it.
     */
    @Test
    void finish_multipleCleanupFailures_keepsFirstAsCause() {
        AtomicInteger closeCalls = new AtomicInteger();
        InputStream stream = new ByteArrayInputStream("name\nvalue\n".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public void close() throws IOException {
                throw new IOException("close failed #" + closeCalls.incrementAndGet());
            }
        };

        ExcelAnalysisException exception = Assertions.assertThrows(
                ExcelAnalysisException.class,
                () -> FesodSheet.read(stream).csv().doReadSync());

        Assertions.assertEquals("close failed #1", exception.getCause().getMessage());
        Assertions.assertEquals(1, exception.getCause().getSuppressed().length);
        Assertions.assertEquals(
                "close failed #2", exception.getCause().getSuppressed()[0].getMessage());
    }
}

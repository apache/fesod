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
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.READ)
class ExcelAnalyserImplTest {

    private static final byte[] CSV = "Name,Age\nAlice,30\nBob,31\n".getBytes(StandardCharsets.UTF_8);

    /**
     * An Error thrown by a listener still releases the resources, then reaches the caller unwrapped.
     */
    @Test
    void analysis_listenerThrowsError_runsCleanupAndRethrowsTheSameError() {
        TrackingInputStream stream = new TrackingInputStream(false);
        AssertionError failure = new AssertionError("listener failed");

        AssertionError thrown = Assertions.assertThrows(
                AssertionError.class,
                () -> read(stream, () -> {
                    throw failure;
                }));

        Assertions.assertSame(failure, thrown);
        Assertions.assertTrue(stream.closed, "the input stream should be closed on the error path");
        Assertions.assertEquals(0, thrown.getSuppressed().length);
    }

    /**
     * When cleanup fails after an Error, the Error is still what the caller gets; the cleanup failure rides
     * along as a suppressed exception instead of replacing it.
     */
    @Test
    void analysis_listenerThrowsErrorAndCloseFails_attachesCleanupFailureAsSuppressed() {
        TrackingInputStream stream = new TrackingInputStream(true);
        AssertionError failure = new AssertionError("listener failed");

        AssertionError thrown = Assertions.assertThrows(
                AssertionError.class,
                () -> read(stream, () -> {
                    throw failure;
                }));

        Assertions.assertSame(failure, thrown);
        Assertions.assertTrue(stream.closed);
        Assertions.assertEquals(1, thrown.getSuppressed().length);
        Throwable cleanupFailure = thrown.getSuppressed()[0];
        Assertions.assertInstanceOf(ExcelAnalysisException.class, cleanupFailure);
        Assertions.assertEquals("close failed", cleanupFailure.getCause().getMessage());
    }

    private static void read(InputStream stream, Runnable onRow) {
        ReadListener<Map<Integer, String>> listener = new ReadListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                onRow.run();
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        };
        FesodSheet.read(stream, listener).excelType(ExcelTypeEnum.CSV).sheet().doRead();
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {

        private final boolean failOnClose;
        private boolean closed;

        private TrackingInputStream(boolean failOnClose) {
            super(CSV);
            this.failOnClose = failOnClose;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }
}

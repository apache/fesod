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

package org.apache.fesod.sheet.write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.WRITE)
class ExcelBuilderImplTest {

    /**
     * An Error thrown while writing still releases the resources, then reaches the caller unwrapped.
     */
    @Test
    void addContent_handlerThrowsError_runsCleanupAndRethrowsTheSameError() {
        TrackingOutputStream stream = new TrackingOutputStream(false);
        AssertionError failure = new AssertionError("handler failed");

        AssertionError thrown = Assertions.assertThrows(
                AssertionError.class,
                () -> write(stream, () -> {
                    throw failure;
                }));

        Assertions.assertSame(failure, thrown);
        Assertions.assertTrue(stream.closed, "the output stream should be closed on the error path");
        Assertions.assertEquals(0, thrown.getSuppressed().length);
    }

    /**
     * When cleanup fails after an Error, the Error is still what the caller gets; the cleanup failure rides
     * along as a suppressed exception instead of replacing it.
     */
    @Test
    void addContent_handlerThrowsErrorAndCloseFails_attachesCleanupFailureAsSuppressed() {
        TrackingOutputStream stream = new TrackingOutputStream(true);
        AssertionError failure = new AssertionError("handler failed");

        AssertionError thrown = Assertions.assertThrows(
                AssertionError.class,
                () -> write(stream, () -> {
                    throw failure;
                }));

        Assertions.assertSame(failure, thrown);
        Assertions.assertTrue(stream.closed);
        assertSuppressedCleanupFailure(thrown);
    }

    /**
     * The same holds for runtime exceptions: a failing close no longer hides the original failure.
     */
    @Test
    void addContent_handlerThrowsRuntimeExceptionAndCloseFails_attachesCleanupFailureAsSuppressed() {
        TrackingOutputStream stream = new TrackingOutputStream(true);
        IllegalStateException failure = new IllegalStateException("handler failed");

        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> write(stream, () -> {
                    throw failure;
                }));

        Assertions.assertSame(failure, thrown);
        Assertions.assertTrue(stream.closed);
        assertSuppressedCleanupFailure(thrown);
    }

    private static void assertSuppressedCleanupFailure(Throwable thrown) {
        Assertions.assertEquals(1, thrown.getSuppressed().length);
        Throwable cleanupFailure = thrown.getSuppressed()[0];
        Assertions.assertInstanceOf(ExcelGenerateException.class, cleanupFailure);
        Assertions.assertEquals("close failed", cleanupFailure.getCause().getMessage());
    }

    private static void write(OutputStream stream, Runnable afterRowDispose) {
        RowWriteHandler handler = new RowWriteHandler() {
            @Override
            public void afterRowDispose(RowWriteHandlerContext context) {
                afterRowDispose.run();
            }
        };
        FesodSheet.write(stream, SimpleData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .registerWriteHandler(handler)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(3));
    }

    private static final class TrackingOutputStream extends ByteArrayOutputStream {

        private final boolean failOnClose;
        private boolean closed;

        private TrackingOutputStream(boolean failOnClose) {
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

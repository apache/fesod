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

package org.apache.fesod.sheet.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.WriteHandler;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

@Tag(Tags.WRITE)
class WriteContextFinishCleanupTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void workbookCallbackFailureStillClosesResources(boolean inMemory) throws Exception {
        RuntimeException failure = new IllegalStateException("workbook callback failed");
        verifyCallbackCleanup(
                new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                        throw failure;
                    }
                },
                failure,
                inMemory);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void sheetCallbackFailureStillClosesResources(boolean inMemory) throws Exception {
        RuntimeException failure = new IllegalStateException("sheet callback failed");
        verifyCallbackCleanup(
                new SheetWriteHandler() {
                    @Override
                    public void afterSheetDispose(SheetWriteHandlerContext context) {
                        throw failure;
                    }
                },
                failure,
                inMemory);
    }

    private void verifyCallbackCleanup(WriteHandler handler, RuntimeException failure, boolean inMemory)
            throws Exception {
        TrackingStream output = new TrackingStream(false);
        ExcelWriter writer = FesodSheet.write(output)
                .inMemory(inMemory)
                .registerWriteHandler(handler)
                .build();
        Workbook workbook =
                Mockito.spy(writer.writeContext().writeWorkbookHolder().getWorkbook());
        writer.writeContext().writeWorkbookHolder().setWorkbook(workbook);
        try {
            writer.write(
                    Collections.singletonList(Collections.singletonList("value")),
                    FesodSheet.writerSheet().build());
            Assertions.assertSame(failure, Assertions.assertThrows(RuntimeException.class, writer::finish));
            writer.finish(); // Finishing twice must not close resources twice.
            Assertions.assertEquals(0, output.bytes.size());
            Assertions.assertAll(
                    () -> Assertions.assertTrue(
                            output.closed, "owned output stream must close even after callback failure"),
                    () -> Mockito.verify(workbook).close());
        } finally {
            workbook.close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            output.close();
        }
    }

    @Test
    void successfulFinishClosesResources() throws Exception {
        TrackingStream output = new TrackingStream(false);
        ExcelWriter writer = FesodSheet.write(output).inMemory(true).build();
        Workbook workbook =
                Mockito.spy(writer.writeContext().writeWorkbookHolder().getWorkbook());
        writer.writeContext().writeWorkbookHolder().setWorkbook(workbook);
        try {
            writer.write(
                    Collections.singletonList(Collections.singletonList("value")),
                    FesodSheet.writerSheet().build());
            writer.finish();
            Assertions.assertAll(() -> Assertions.assertTrue(output.closed), () -> Mockito.verify(workbook)
                    .close());
        } finally {
            workbook.close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            output.close();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void outputWriteFailureStillClosesWorkbook(boolean inMemory) throws Exception {
        TrackingStream output = new TrackingStream(true);
        ExcelWriter writer = FesodSheet.write(output).inMemory(inMemory).build();
        Workbook workbook =
                Mockito.spy(writer.writeContext().writeWorkbookHolder().getWorkbook());
        writer.writeContext().writeWorkbookHolder().setWorkbook(workbook);
        try {
            writer.write(
                    Collections.singletonList(Collections.singletonList("value")),
                    FesodSheet.writerSheet().build());
            Assertions.assertThrows(ExcelGenerateException.class, writer::finish);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(output.closed, "owned output stream must close after write failure"),
                    () -> Mockito.verify(workbook).close());
        } finally {
            workbook.close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            output.close();
        }
    }

    @Test
    void callbackFailurePreservesOriginalExceptionWhenStreamCloseFails() throws Exception {
        RuntimeException failure = new IllegalStateException("callback failed");
        IOException closeFailure = new IOException("close failed");
        OutputStream output = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                throw closeFailure;
            }
        };
        ExcelWriter writer = FesodSheet.write(output)
                .inMemory(true)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                        throw failure;
                    }
                })
                .build();
        try {
            Assertions.assertSame(failure, Assertions.assertThrows(RuntimeException.class, writer::finish));
            Assertions.assertArrayEquals(new Throwable[] {closeFailure}, failure.getSuppressed());
        } finally {
            writer.writeContext().writeWorkbookHolder().getWorkbook().close();
        }
    }

    @Test
    void callbackFailureDoesNotCloseCallerOwnedStream() throws Exception {
        TrackingStream output = new TrackingStream(false);
        RuntimeException failure = new IllegalStateException("callback failed");
        ExcelWriter writer = FesodSheet.write(output)
                .inMemory(true)
                .autoCloseStream(false)
                .registerWriteHandler(new WorkbookWriteHandler() {
                    @Override
                    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                        throw failure;
                    }
                })
                .build();
        Workbook workbook =
                Mockito.spy(writer.writeContext().writeWorkbookHolder().getWorkbook());
        writer.writeContext().writeWorkbookHolder().setWorkbook(workbook);
        try {
            Assertions.assertSame(failure, Assertions.assertThrows(RuntimeException.class, writer::finish));
            Assertions.assertFalse(output.closed);
            Mockito.verify(workbook).close();
        } finally {
            workbook.close();
            output.close();
        }
    }

    private static class TrackingStream extends OutputStream {
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private final boolean failWrites;
        private boolean closed;

        private TrackingStream(boolean failWrites) {
            this.failWrites = failWrites;
        }

        @Override
        public void write(int value) throws IOException {
            if (failWrites) {
                throw new IOException("write failed");
            }
            bytes.write(value);
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

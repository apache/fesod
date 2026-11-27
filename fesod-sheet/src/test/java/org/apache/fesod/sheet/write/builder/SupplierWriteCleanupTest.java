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

package org.apache.fesod.sheet.write.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

@Tag(Tags.WRITE)
class SupplierWriteCleanupTest {
    enum Operation {
        SHEET,
        TABLE,
        FILL,
        FILL_WITH_CONFIG
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void supplierFailureClosesOwnedResources(Operation operation) throws Exception {
        verifyFailure(operation, new IllegalStateException("query failed"), true, false);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void supplierErrorClosesOwnedResources(Operation operation) throws Exception {
        verifyFailure(operation, new AssertionError("query failed"), true, false);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void supplierFailureLeavesCallerOwnedStreamOpen(Operation operation) throws Exception {
        verifyFailure(operation, new IllegalStateException("query failed"), false, false);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void cleanupFailureDoesNotReplaceSupplierFailure(Operation operation) throws Exception {
        verifyFailure(operation, new IllegalStateException("query failed"), true, true);
    }

    private void verifyFailure(Operation operation, Throwable failure, boolean autoClose, boolean failOnClose)
            throws Exception {
        TrackingStream output = new TrackingStream(failOnClose);
        ExcelWriter writer = FesodSheet.write(output).autoCloseStream(autoClose).build();
        Workbook workbook =
                Mockito.spy(writer.writeContext().writeWorkbookHolder().getWorkbook());
        writer.writeContext().writeWorkbookHolder().setWorkbook(workbook);
        try {
            Throwable thrown = Assertions.assertThrows(
                    Throwable.class,
                    () -> execute(writer, operation, () -> {
                        if (failure instanceof Error) {
                            throw (Error) failure;
                        }
                        throw (RuntimeException) failure;
                    }));
            Assertions.assertSame(failure, thrown);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(autoClose ? 1 : 0, output.closeCount),
                    () -> Assertions.assertEquals(0, output.size()),
                    () -> Mockito.verify(workbook).close());
            if (failOnClose) {
                Assertions.assertEquals(1, failure.getSuppressed().length);
                Assertions.assertSame(output.closeFailure, failure.getSuppressed()[0].getCause());
            }
            writer.finish();
            Assertions.assertEquals(autoClose ? 1 : 0, output.closeCount);
            Assertions.assertEquals(0, output.size());
        } finally {
            // Also release resources when running against the unfixed implementation.
            output.failOnClose = false;
            writer.writeContext().finish(true);
        }
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void successfulSupplierWritesAndCloses(Operation operation) throws Exception {
        TrackingStream output = new TrackingStream(false);
        ByteArrayOutputStream template = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet().createRow(0).createCell(0).setCellValue("{value}");
            workbook.write(template);
        }
        try (ExcelWriter writer = FesodSheet.write(output)
                .withTemplate(new ByteArrayInputStream(template.toByteArray()))
                .build()) {
            AtomicInteger calls = new AtomicInteger();
            execute(writer, operation, () -> {
                calls.incrementAndGet();
                return operation == Operation.SHEET || operation == Operation.TABLE
                        ? Collections.singletonList(Collections.singletonList("written"))
                        : Collections.singletonMap("value", "written");
            });
            Assertions.assertEquals(1, calls.get());
            Assertions.assertEquals(1, output.closeCount);
        }
        List<Map<Integer, String>> rows = FesodSheet.read(new ByteArrayInputStream(output.toByteArray()))
                .headRowNumber(0)
                .sheet()
                .doReadSync();
        Assertions.assertTrue(rows.stream().anyMatch(row -> "written".equals(row.get(0))));
        Assertions.assertEquals(1, output.closeCount);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void supplierFailureHonorsWriteExcelOnException(Operation operation) throws Exception {
        TrackingStream output = new TrackingStream(false);
        RuntimeException failure = new IllegalStateException("query failed");
        try (ExcelWriter writer =
                FesodSheet.write(output).writeExcelOnException(true).build()) {
            writer.write(
                    Collections.singletonList(Collections.singletonList("previous")),
                    FesodSheet.writerSheet().build());
            Assertions.assertSame(
                    failure,
                    Assertions.assertThrows(
                            RuntimeException.class,
                            () -> execute(writer, operation, () -> {
                                throw failure;
                            })));
            Assertions.assertEquals(1, output.closeCount);
        }
        List<Map<Integer, String>> rows = FesodSheet.read(new ByteArrayInputStream(output.toByteArray()))
                .headRowNumber(0)
                .sheet()
                .doReadSync();
        Assertions.assertEquals("previous", rows.get(0).get(0));
    }

    private void execute(ExcelWriter writer, Operation operation, Supplier<?> supplier) {
        ExcelWriterSheetBuilder sheet = new ExcelWriterSheetBuilder(writer);
        switch (operation) {
            case SHEET:
                sheet.doWrite(() -> (Collection<?>) supplier.get());
                break;
            case TABLE:
                sheet.table().doWrite(() -> (Collection<?>) supplier.get());
                break;
            case FILL:
                sheet.doFill(supplier::get);
                break;
            case FILL_WITH_CONFIG:
                sheet.doFill(supplier::get, FillConfig.builder().build());
                break;
            default:
                throw new AssertionError(operation);
        }
    }

    private static class TrackingStream extends ByteArrayOutputStream {
        private final IOException closeFailure = new IOException("close failed");
        private boolean failOnClose;
        private int closeCount;

        private TrackingStream(boolean failOnClose) {
            this.failOnClose = failOnClose;
        }

        @Override
        public void close() throws IOException {
            closeCount++;
            if (failOnClose) {
                throw closeFailure;
            }
            super.close();
        }
    }
}

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(Tags.WRITE)
class TemplateStreamBufferingTest extends AbstractExcelTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preparingLargeTemplateDoesNotReadTheEntireInput(boolean autoClose) throws Exception {
        GeneratedInputStream source = new GeneratedInputStream(32 * 1024 * 1024);
        WriteWorkbook configuration = new WriteWorkbook();
        configuration.setOutputStream(new ByteArrayOutputStream());
        configuration.setTemplateInputStream(source);
        configuration.setAutoCloseStream(autoClose);
        WriteWorkbookHolder holder = new WriteWorkbookHolder(configuration);
        try (InputStream prepared = holder.getTempTemplateInputStream()) {
            Assertions.assertEquals(0, source.bytesRead, "preparing a template must not materialize its bytes");
            Assertions.assertEquals(0, prepared.read());
            Assertions.assertTrue(source.bytesRead < 32 * 1024 * 1024, "the first read must use bounded buffering");
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fileAndStreamTemplatesRetainFillBehaviorAndOwnership(ExcelFormat format) throws Exception {
        File template = template(format);
        byte[] original = Files.readAllBytes(template.toPath());
        for (boolean inMemory : new boolean[] {false, true}) {
            File fileOutput = createTempFile("file-template", format);
            fill(FesodSheet.write(fileOutput).withTemplate(template).inMemory(inMemory));
            assertFilled(fileOutput);

            for (boolean autoClose : new boolean[] {true, false}) {
                File streamOutput = createTempFile("stream-template", format);
                try (TrackingInputStream source = new TrackingInputStream(Files.newInputStream(template.toPath()))) {
                    ExcelWriterBuilder builder = FesodSheet.write(streamOutput)
                            .withTemplate(source)
                            .autoCloseStream(autoClose)
                            .inMemory(inMemory);
                    try (ExcelWriter writer = builder.build()) {
                        Assertions.assertEquals(autoClose ? 1 : 0, source.closeCalls);
                        InputStream prepared =
                                writer.writeContext().writeWorkbookHolder().getTempTemplateInputStream();
                        Assertions.assertNotNull(prepared, "the closed stream still marks a template workbook");
                        Assertions.assertThrows(IOException.class, prepared::read);
                        fill(writer);
                    }
                    Assertions.assertEquals(autoClose ? 1 : 0, source.closeCalls);
                    if (!autoClose) {
                        Assertions.assertEquals(-1, source.read());
                    }
                }
                assertFilled(streamOutput);
            }
        }
        Assertions.assertArrayEquals(original, Files.readAllBytes(template.toPath()));
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void writingAppendsToTheTemplateAfterItsStreamIsClosed(ExcelFormat format) throws Exception {
        File template = template(format);
        for (boolean inMemory : new boolean[] {false, true}) {
            File output = createTempFile("append-template", format);
            try (ExcelWriter writer = FesodSheet.write(output)
                    .withTemplate(template)
                    .inMemory(inMemory)
                    .build()) {
                writer.write(
                        Collections.singletonList(Collections.singletonList("appended")),
                        FesodSheet.writerSheet(0).needHead(false).build());
            }
            try (Workbook workbook = WorkbookFactory.create(output)) {
                Assertions.assertEquals(
                        "{.name}", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
                Assertions.assertEquals(
                        "footer", workbook.getSheetAt(0).getRow(2).getCell(0).getStringCellValue());
                Assertions.assertEquals(
                        "appended", workbook.getSheetAt(0).getRow(3).getCell(0).getStringCellValue());
                Assertions.assertEquals(
                        "untouched", workbook.getSheetAt(1).getRow(0).getCell(0).getStringCellValue());
            }
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void parsingFailureHonorsCallerStreamOwnership(ExcelFormat format) throws Exception {
        for (boolean autoClose : new boolean[] {true, false}) {
            try (TrackingInputStream source = new TrackingInputStream(new ByteArrayInputStream(new byte[128]))) {
                TrackingOutputStream output = new TrackingOutputStream();
                Assertions.assertThrows(RuntimeException.class, () -> FesodSheet.write(output)
                        .excelType(format.toExcelTypeEnum())
                        .withTemplate(source)
                        .autoCloseStream(autoClose)
                        .build());
                Assertions.assertEquals(autoClose ? 1 : 0, source.closeCalls);
                Assertions.assertEquals(autoClose ? 1 : 0, output.closeCalls);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void readingFailureHonorsOutputStreamOwnership(boolean autoClose) throws Exception {
        try (TrackingInputStream source = new TrackingInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("template read failed");
            }
        })) {
            TrackingOutputStream output = new TrackingOutputStream();
            RuntimeException failure = Assertions.assertThrows(RuntimeException.class, () -> FesodSheet.write(output)
                    .withTemplate(source)
                    .autoCloseStream(autoClose)
                    .build());
            Assertions.assertNotNull(failure.getCause());
            Assertions.assertEquals(autoClose ? 1 : 0, source.closeCalls);
            Assertions.assertEquals(autoClose ? 1 : 0, output.closeCalls);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void callbackFailureClosesOnlyOwnedTemplateStream(boolean autoClose) throws Exception {
        File template = template(ExcelFormat.XLSX);
        for (boolean beforeCreate : new boolean[] {true, false}) {
            try (TrackingInputStream source = new TrackingInputStream(Files.newInputStream(template.toPath()))) {
                TrackingOutputStream output = new TrackingOutputStream();
                Assertions.assertThrows(IllegalStateException.class, () -> FesodSheet.write(output)
                        .withTemplate(source)
                        .autoCloseStream(autoClose)
                        .registerWriteHandler(new WorkbookWriteHandler() {
                            @Override
                            public void beforeWorkbookCreate() {
                                if (beforeCreate) {
                                    throw new IllegalStateException("before callback failed");
                                }
                            }

                            @Override
                            public void afterWorkbookCreate(WriteWorkbookHolder holder) {
                                if (!beforeCreate) {
                                    throw new IllegalStateException("after callback failed");
                                }
                            }
                        })
                        .build());
                Assertions.assertEquals(autoClose ? 1 : 0, source.closeCalls);
                Assertions.assertEquals(autoClose ? 1 : 0, output.closeCalls);
            }
        }
    }

    @Test
    void callbackErrorRetainsCleanupFailure() throws Exception {
        File template = template(ExcelFormat.XLSX);
        AssertionError callbackFailure = new AssertionError("callback failed");
        IOException outputFailure = new IOException("output close failed");
        try (TrackingInputStream source = new TrackingInputStream(Files.newInputStream(template.toPath()))) {
            TrackingOutputStream output = new TrackingOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    throw outputFailure;
                }
            };
            RuntimeException failure = Assertions.assertThrows(RuntimeException.class, () -> FesodSheet.write(output)
                    .withTemplate(source)
                    .registerWriteHandler(new WorkbookWriteHandler() {
                        @Override
                        public void beforeWorkbookCreate() {
                            throw callbackFailure;
                        }
                    })
                    .build());
            Assertions.assertSame(callbackFailure, failure.getCause());
            Assertions.assertArrayEquals(new Throwable[] {outputFailure}, callbackFailure.getSuppressed());
            Assertions.assertEquals(1, source.closeCalls);
            Assertions.assertEquals(1, output.closeCalls);
        }
    }

    private void fill(ExcelWriterBuilder builder) {
        try (ExcelWriter writer = builder.build()) {
            fill(writer);
        }
    }

    private void fill(ExcelWriter writer) {
        WriteSheet sheet = FesodSheet.writerSheet(0).build();
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        writer.fill(
                Arrays.asList(Collections.singletonMap("name", "first"), Collections.singletonMap("name", "second")),
                config,
                sheet);
        writer.fill(Collections.singletonList(Collections.singletonMap("name", "third")), config, sheet);
    }

    private static void assertFilled(File file) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Assertions.assertEquals(
                    "first", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "second", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "third", workbook.getSheetAt(0).getRow(2).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "footer", workbook.getSheetAt(0).getRow(4).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "untouched", workbook.getSheetAt(1).getRow(0).getCell(0).getStringCellValue());
        }
    }

    private File template(ExcelFormat format) throws Exception {
        File template = createTempFile("template", format);
        try (Workbook workbook = format == ExcelFormat.XLS ? new HSSFWorkbook() : new XSSFWorkbook();
                FileOutputStream output = new FileOutputStream(template)) {
            workbook.createSheet("Data").createRow(0).createCell(0).setCellValue("{.name}");
            workbook.getSheetAt(0).createRow(2).createCell(0).setCellValue("footer");
            workbook.createSheet("Other").createRow(0).createCell(0).setCellValue("untouched");
            workbook.write(output);
        }
        return template;
    }

    private static class TrackingInputStream extends FilterInputStream {
        private int closeCalls;

        TrackingInputStream(InputStream input) {
            super(input);
        }

        @Override
        public void close() throws IOException {
            closeCalls++;
            super.close();
        }
    }

    private static class TrackingOutputStream extends ByteArrayOutputStream {
        private int closeCalls;

        @Override
        public void close() throws IOException {
            closeCalls++;
            super.close();
        }
    }

    private static class GeneratedInputStream extends InputStream {
        private final int size;
        private int bytesRead;

        GeneratedInputStream(int size) {
            this.size = size;
        }

        @Override
        public int read() {
            if (bytesRead >= size) {
                return -1;
            }
            bytesRead++;
            return 0;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            if (bytesRead >= size) {
                return -1;
            }
            int count = Math.min(length, size - bytesRead);
            Arrays.fill(buffer, offset, offset + count, (byte) 0);
            bytesRead += count;
            return count;
        }
    }
}

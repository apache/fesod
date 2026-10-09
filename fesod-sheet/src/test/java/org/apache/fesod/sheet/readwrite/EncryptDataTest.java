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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.readwrite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.output.BrokenOutputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.read.builder.ExcelReaderBuilder;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test encrypt read/write for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class EncryptDataTest extends AbstractExcelTest {
    private static final String PASSWORD = "123456";

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWriteAllCombinations(ExcelFormat format) throws Exception {
        ExcelTypeEnum excelType = format.toExcelTypeEnum();

        // File-based: no explicit type, no password
        readAndWrite(createTempFile("enc-f-np", format), null, false, false, excelType);
        // File-based: no explicit type, with password
        readAndWrite(createTempFile("enc-f-wp", format), null, true, false, excelType);
        // File-based: explicit type, no password
        readAndWrite(createTempFile("enc-f-tp", format), excelType, false, false, excelType);
        // File-based: explicit type, with password
        readAndWrite(createTempFile("enc-f-twp", format), excelType, true, false, excelType);

        // Stream-based: no explicit type, no password
        readAndWrite(createTempFile("enc-s-np", format), null, false, true, excelType);
        // Stream-based: no explicit type, with password
        readAndWrite(createTempFile("enc-s-wp", format), null, true, true, excelType);
        // Stream-based: explicit type, no password
        readAndWrite(createTempFile("enc-s-tp", format), excelType, false, true, excelType);
        // Stream-based: explicit type, with password
        readAndWrite(createTempFile("enc-s-twp", format), excelType, true, true, excelType);
    }

    private void readAndWrite(
            File file, ExcelTypeEnum excelType, boolean hasPassword, boolean isStream, ExcelTypeEnum streamType)
            throws Exception {
        ExcelWriterBuilder excelWriterBuilder = isStream
                ? FesodSheet.write(Files.newOutputStream(file.toPath()), SimpleData.class)
                : FesodSheet.write(file, SimpleData.class);

        ExcelReaderBuilder readerBuilder = isStream
                ? FesodSheet.read(
                        Files.newInputStream(file.toPath()), SimpleData.class, new CollectingReadListener<SimpleData>())
                : FesodSheet.read(file, SimpleData.class, new CollectingReadListener<SimpleData>());
        if (excelType != null) {
            excelWriterBuilder.excelType(excelType);
            readerBuilder.excelType(excelType);
        }
        if (isStream && excelType == null) {
            // Stream API needs type hint when not explicitly set
            excelWriterBuilder.excelType(streamType);
            readerBuilder.excelType(streamType);
        }
        if (hasPassword) {
            excelWriterBuilder.password(PASSWORD);
            readerBuilder.password(PASSWORD);
        }

        excelWriterBuilder.sheet().doWrite(TestDataBuilder.simpleData(10));
        List<SimpleData> dataList = readerBuilder.sheet().doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0).getName());
    }

    /**
     * Verifies that an XLS file written with a password is actually encrypted at the BIFF8 record level,
     * not merely flagged as write-protected. Without the correct password, reading the file content
     * must fail.
     */
    @Test
    void xlsPasswordWrite_isActuallyEncrypted() throws Exception {
        File file = createTempFile("enc-verify", ExcelFormat.XLS);

        // Write an encrypted XLS file
        FesodSheet.write(file, SimpleData.class)
                .excelType(ExcelTypeEnum.XLS)
                .password(PASSWORD)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(10));

        // Reading without the password must fail because the content is BIFF8-encrypted
        Assertions.assertThrows(EncryptedDocumentException.class, () -> FesodSheet.read(
                        file, SimpleData.class, new CollectingReadListener<SimpleData>())
                .excelType(ExcelTypeEnum.XLS)
                .sheet()
                .doReadSync());

        // Reading with the correct password must succeed
        List<SimpleData> dataList = FesodSheet.read(file, SimpleData.class, new CollectingReadListener<SimpleData>())
                .excelType(ExcelTypeEnum.XLS)
                .password(PASSWORD)
                .sheet()
                .doReadSync();
        Assertions.assertEquals(10, dataList.size());
    }

    /**
     * Verifies that when encrypting an XLSX stream write fails, nothing is written to the stream rather than
     * the unencrypted workbook.
     */
    @Test
    void xlsxStreamPasswordWrite_encryptionFails_writesNothing() throws Exception {
        // A temp directory under a regular file cannot be created, so the encryption step fails
        File regularFile = createTempFile("enc-blocker", ExcelFormat.XLSX);
        String originalPrefix = FileUtils.getTempFilePrefix();
        FileUtils.setTempFilePrefix(regularFile.getAbsolutePath() + File.separator + "sub" + File.separator);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Assertions.assertThrows(ExcelGenerateException.class, () -> FesodSheet.write(out, SimpleData.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .password(PASSWORD)
                    .sheet()
                    .doWrite(TestDataBuilder.simpleData(10)));
            Assertions.assertEquals(0, out.size());
        } finally {
            FileUtils.setTempFilePrefix(originalPrefix);
        }
    }

    /**
     * Verifies that when writing the unencrypted temp file for an XLSX stream write fails, the temp file is deleted.
     */
    @Test
    void xlsxStreamPasswordWrite_tempFileWriteFails_deletesTempFile() {
        File tempFileDir = new File(tempDir, "fesod-temp");
        WorkbookWriteHandler failingWorkbook = swapWorkbook(new SXSSFWorkbook() {
            @Override
            public void write(OutputStream stream) throws IOException {
                super.write(stream);
                throw new IOException("write failed");
            }
        });
        Assertions.assertThrows(
                ExcelGenerateException.class,
                () -> writeWithPassword(tempFileDir, new ByteArrayOutputStream(), failingWorkbook));
        Assertions.assertArrayEquals(new String[0], tempFileDir.list());
    }

    /**
     * Verifies that when closing the workbook after writing the temp file fails, the temp file is deleted.
     */
    @Test
    void xlsxStreamPasswordWrite_workbookCloseFails_deletesTempFile() {
        File tempFileDir = new File(tempDir, "fesod-temp");
        WorkbookWriteHandler failingWorkbook = swapWorkbook(new SXSSFWorkbook() {
            @Override
            public void close() throws IOException {
                super.close();
                throw new IOException("close failed");
            }
        });
        Assertions.assertThrows(
                ExcelGenerateException.class,
                () -> writeWithPassword(tempFileDir, new ByteArrayOutputStream(), failingWorkbook));
        Assertions.assertArrayEquals(new String[0], tempFileDir.list());
    }

    /**
     * Verifies that when closing the workbook after writing the temp file fails, the temp file stream is closed.
     */
    @Test
    void xlsxStreamPasswordWrite_workbookCloseFails_closesTempFileStream() {
        File tempFileDir = new File(tempDir, "fesod-temp");
        OutputStream[] tempFileStream = new OutputStream[1];
        WorkbookWriteHandler failingWorkbook = swapWorkbook(new SXSSFWorkbook() {
            @Override
            public void write(OutputStream stream) throws IOException {
                if (tempFileStream[0] == null) {
                    tempFileStream[0] = stream;
                }
                super.write(stream);
            }

            @Override
            public void close() throws IOException {
                super.close();
                throw new IOException("close failed");
            }
        });
        Assertions.assertThrows(
                ExcelGenerateException.class,
                () -> writeWithPassword(tempFileDir, new ByteArrayOutputStream(), failingWorkbook));
        Assertions.assertThrows(IOException.class, () -> tempFileStream[0].write(0));
    }

    /**
     * Verifies that when writing the encrypted workbook to the caller's stream fails, the temp file is deleted.
     */
    @Test
    void xlsxStreamPasswordWrite_encryptedWriteFails_deletesTempFile() {
        File tempFileDir = new File(tempDir, "fesod-temp");
        Assertions.assertThrows(
                ExcelGenerateException.class, () -> writeWithPassword(tempFileDir, BrokenOutputStream.INSTANCE, null));
        Assertions.assertArrayEquals(new String[0], tempFileDir.list());
    }

    /**
     * Verifies that when the temp file cannot be opened, the open error is reported rather than a failed delete.
     */
    @Test
    void xlsxStreamPasswordWrite_tempFileOpenFails_reportsOpenError() {
        File tempFileDir = new File(tempDir, "fesod-temp");
        Assertions.assertTrue(tempFileDir.mkdirs());
        Assertions.assertTrue(tempFileDir.setWritable(false));
        try {
            // Root and some file systems ignore the flag, so the open would not fail there
            Assumptions.assumeFalse(tempFileDir.canWrite());
            ExcelGenerateException e = Assertions.assertThrows(
                    ExcelGenerateException.class,
                    () -> writeWithPassword(tempFileDir, new ByteArrayOutputStream(), null));
            Assertions.assertInstanceOf(FileNotFoundException.class, e.getCause());
        } finally {
            Assertions.assertTrue(tempFileDir.setWritable(true));
        }
    }

    private static WorkbookWriteHandler swapWorkbook(SXSSFWorkbook workbook) {
        return new WorkbookWriteHandler() {
            @Override
            public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                workbook.createSheet("s").createRow(0).createCell(0).setCellValue("secret");
                context.getWriteWorkbookHolder().setWorkbook(workbook);
            }
        };
    }

    private void writeWithPassword(File tempFileDir, OutputStream outputStream, WorkbookWriteHandler writeHandler) {
        String originalPrefix = FileUtils.getTempFilePrefix();
        FileUtils.setTempFilePrefix(tempFileDir.getAbsolutePath() + File.separator);
        try {
            ExcelWriterBuilder writerBuilder = FesodSheet.write(outputStream, SimpleData.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .password(PASSWORD);
            if (writeHandler != null) {
                writerBuilder.registerWriteHandler(writeHandler);
            }
            writerBuilder.sheet("s").doWrite(TestDataBuilder.simpleData(10));
        } finally {
            FileUtils.setTempFilePrefix(originalPrefix);
        }
    }

    /**
     * Verifies that when encrypting an XLSX file write fails, the unencrypted file is not left behind.
     */
    @Test
    void xlsxFilePasswordWrite_encryptionFails_leavesNoFile() throws Exception {
        File file = createTempFile("enc-readonly", ExcelFormat.XLSX);
        // Root ignores file permissions, so the failure forced below cannot happen
        Assumptions.assumeTrue(file.setReadOnly() && !file.canWrite(), "file permissions are not enforced");
        Assertions.assertTrue(file.setWritable(true));
        // Once the write stream is open, making the file read-only makes the encrypt-in-place step fail
        WorkbookWriteHandler makeReadOnly = new WorkbookWriteHandler() {
            @Override
            public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
                Assertions.assertTrue(file.setReadOnly());
            }
        };

        Assertions.assertThrows(ExcelGenerateException.class, () -> FesodSheet.write(file, SimpleData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .password(PASSWORD)
                .registerWriteHandler(makeReadOnly)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(10)));
        Assertions.assertFalse(file.exists());
    }
}

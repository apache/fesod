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

package org.apache.fesod.excel.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipOutputStream;
import org.apache.fesod.excel.analysis.v07.XlsxSaxAnalyser;
import org.apache.fesod.excel.context.xlsx.DefaultXlsxReadContext;
import org.apache.fesod.excel.context.xlsx.XlsxReadContext;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.junit.jupiter.api.Test;

/**
 * Tests for XlsxSaxAnalyser.readOpcPackage error handling: it should wrap
 * POI's NotOfficeXmlFileException/InvalidFormatException into ExcelCommonException
 * with a message containing "Invalid OOXML/zip format".
 */
public class XlsxSaxAnalyserReadOpcPackageTest {

    @Test
    void invalidInputStream_mandatoryUseInputStream_throwsExcelCommonException() {
        ReadWorkbook rw = new ReadWorkbook();
        rw.setInputStream(new ByteArrayInputStream("not-xlsx".getBytes(StandardCharsets.UTF_8)));
        rw.setMandatoryUseInputStream(true);
        XlsxReadContext ctx = new DefaultXlsxReadContext(rw, ExcelTypeEnum.XLSX);

        ExcelCommonException ex = assertThrows(ExcelCommonException.class, () -> new XlsxSaxAnalyser(ctx, null));
        assertTrue(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("invalid ooxml/zip format"));
    }

    @Test
    void invalidFile_throwsExcelCommonException() throws Exception {
        File tmp = File.createTempFile("invalid-ooxml", ".xlsx");
        try {
            Files.write(tmp.toPath(), new byte[] {1, 2, 3, 4});
            ReadWorkbook rw = new ReadWorkbook();
            rw.setFile(tmp);
            XlsxReadContext ctx = new DefaultXlsxReadContext(rw, ExcelTypeEnum.XLSX);

            ExcelCommonException ex = assertThrows(ExcelCommonException.class, () -> new XlsxSaxAnalyser(ctx, null));
            assertTrue(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("invalid ooxml/zip format"));
        } finally {
            try {
                Files.deleteIfExists(tmp.toPath());
            } catch (Exception ignore) {
            }
        }
    }

    @Test
    void decryptedStreamProvided_throwsExcelCommonException() {
        ReadWorkbook rw = new ReadWorkbook();
        // do not set file/inputStream; pass decryptedStream directly
        XlsxReadContext ctx = new DefaultXlsxReadContext(rw, ExcelTypeEnum.XLSX);
        ByteArrayInputStream decrypted = new ByteArrayInputStream("still-not-xlsx".getBytes(StandardCharsets.UTF_8));

        ExcelCommonException ex = assertThrows(ExcelCommonException.class, () -> new XlsxSaxAnalyser(ctx, decrypted));
        assertTrue(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("invalid ooxml/zip format"));
    }

    @Test
    void emptyZipFile_throwsExcelCommonException() throws Exception {
        File tmp = File.createTempFile("empty-zip", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tmp);
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            // write an empty zip with no entries
        }
        try {
            ReadWorkbook rw = new ReadWorkbook();
            rw.setFile(tmp);
            XlsxReadContext ctx = new DefaultXlsxReadContext(rw, ExcelTypeEnum.XLSX);
            ExcelCommonException ex = assertThrows(ExcelCommonException.class, () -> new XlsxSaxAnalyser(ctx, null));
            assertTrue(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("invalid ooxml/zip format"));
        } finally {
            try {
                Files.deleteIfExists(tmp.toPath());
            } catch (Exception ignore) {
            }
        }
    }

    @Test
    void inputStream_nonMandatoryUseTempFileBranch_throwsExcelCommonException() {
        ReadWorkbook rw = new ReadWorkbook();
        rw.setInputStream(new ByteArrayInputStream("not-xlsx".getBytes(StandardCharsets.UTF_8)));
        // mandatoryUseInputStream unset or false -> will write to temp file and then open
        rw.setMandatoryUseInputStream(false);
        XlsxReadContext ctx = new DefaultXlsxReadContext(rw, ExcelTypeEnum.XLSX);

        ExcelCommonException ex = assertThrows(ExcelCommonException.class, () -> new XlsxSaxAnalyser(ctx, null));
        assertTrue(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("invalid ooxml/zip format"));
    }
}

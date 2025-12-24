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

package org.apache.fesod.sheet.support;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.Getter;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.read.metadata.ReadWorkbook;
import org.apache.fesod.sheet.util.StringUtils;
import org.apache.poi.EmptyFileException;
import org.apache.poi.util.IOUtils;

/**
 *
 */
@Getter
public enum ExcelTypeEnum {

    /**
     * csv
     */
    CSV(".csv", new byte[] {-27, -89, -109, -27}),

    /**
     * xls
     */
    XLS(".xls", new byte[] {-48, -49, 17, -32, -95, -79, 26, -31}),

    /**
     * xlsx
     */
    XLSX(".xlsx", new byte[] {80, 75, 3, 4}),

    /**
     * ods (OpenDocument Spreadsheet)
     */
    ODS(".ods", new byte[] {80, 75, 3, 4});

    final String value;
    final byte[] magic;

    ExcelTypeEnum(String value, byte[] magic) {
        this.value = value;
        this.magic = magic;
    }

    static final int MAX_PATTERN_LENGTH = 8;

    public static ExcelTypeEnum valueOf(ReadWorkbook readWorkbook) {
        File file = readWorkbook.getFile();
        InputStream inputStream = readWorkbook.getInputStream();
        if (file == null && inputStream == null) {
            throw new ExcelAnalysisException("File and inputStream must be a non-null.");
        }

        ExcelTypeEnum excelType = readWorkbook.getExcelType();
        boolean hasPassword = !StringUtils.isEmpty(readWorkbook.getPassword());
        ExcelTypeEnum recognitionType;
        try {
            if (file != null) {
                if (!file.exists()) {
                    throw new ExcelAnalysisException("File " + file.getAbsolutePath() + " not exists.");
                }

                // If there is a password, use the FileMagic first
                if (hasPassword) {
                    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                        recognitionType = recognitionExcelType(bufferedInputStream);
                        if (excelType == null || !excelType.equals(recognitionType)) {
                            return recognitionType;
                        }
                    }
                }

                if (excelType != null) {
                    return excelType;
                }

                // Use the name to determine the type
                String fileName = file.getName();
                if (fileName.endsWith(XLSX.getValue())) {
                    return XLSX;
                } else if (fileName.endsWith(XLS.getValue())) {
                    return XLS;
                } else if (fileName.endsWith(CSV.getValue())) {
                    return CSV;
                } else if (fileName.endsWith(ODS.getValue())) {
                    return ODS;
                }
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                    return recognitionExcelType(bufferedInputStream);
                }
            }
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
                readWorkbook.setInputStream(inputStream);
            }
            recognitionType = recognitionExcelType(inputStream);
            if (excelType == null || (hasPassword && !excelType.equals(recognitionType))) {
                return recognitionType;
            }
            return excelType;
        } catch (ExcelCommonException e) {
            throw e;
        } catch (EmptyFileException e) {
            throw new ExcelCommonException("The supplied file was empty (zero bytes long)");
        } catch (Exception e) {
            throw new ExcelCommonException(
                    "Convert excel format exception.You can try specifying the 'excelType' yourself", e);
        }
    }

    private static ExcelTypeEnum recognitionExcelType(InputStream inputStream) throws Exception {
        // Grab the first bytes of this stream
        byte[] data = IOUtils.peekFirstNBytes(inputStream, MAX_PATTERN_LENGTH);
        if (findMagic(XLSX.magic, data)) {
            // Both XLSX and ODS are ZIP files with the same magic bytes {80, 75, 3, 4}
            // Need to check internal structure to distinguish them
            return distinguishZipBasedFormat(inputStream);
        } else if (findMagic(XLS.magic, data)) {
            return XLS;
        }
        // csv has no fixed prefix, if the format is not specified, it defaults to csv
        return CSV;
    }

    /**
     * Distinguish between XLSX and ODS formats by checking ZIP internal structure.
     * ODS files contain a 'mimetype' file with content 'application/vnd.oasis.opendocument.spreadsheet'.
     * XLSX files contain '[Content_Types].xml' or 'xl/' directory.
     *
     * @param inputStream the input stream (must support mark/reset)
     * @return ODS if it's an ODS file, XLSX otherwise (default for ZIP-based spreadsheets)
     */
    private static ExcelTypeEnum distinguishZipBasedFormat(InputStream inputStream) throws Exception {
        // Read enough bytes to check the ZIP structure
        // Most ZIP files have the first entry within the first 4KB
        final int BUFFER_SIZE = 4096;
        if (!inputStream.markSupported()) {
            // If mark is not supported, default to XLSX
            return XLSX;
        }

        inputStream.mark(BUFFER_SIZE);
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead <= 0) {
                return XLSX; // Default to XLSX for empty files
            }

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(buffer, 0, bytesRead))) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    // ODS files have 'mimetype' as the first file (usually)
                    if ("mimetype".equals(entryName)) {
                        // Verify it's ODS by reading the mimetype content
                        byte[] mimeBytes = new byte[64];
                        int len = zipInputStream.read(mimeBytes);
                        if (len > 0) {
                            String mimeType = new String(mimeBytes, 0, len).trim();
                            if (mimeType.contains("opendocument.spreadsheet")) {
                                return ODS;
                            }
                        }
                    }
                    // XLSX files typically have these entries
                    if (entryName.equals("[Content_Types].xml") || entryName.startsWith("xl/")) {
                        return XLSX;
                    }
                    // ODS files also have content.xml
                    if ("content.xml".equals(entryName) || entryName.equals("META-INF/manifest.xml")) {
                        return ODS;
                    }
                    zipInputStream.closeEntry();
                }
            }
        } finally {
            inputStream.reset();
        }
        // Default to XLSX for unrecognized ZIP-based format
        return XLSX;
    }

    private static boolean findMagic(byte[] expected, byte[] actual) {
        int i = 0;
        for (byte expectedByte : expected) {
            if (actual[i++] != expectedByte && expectedByte != '?') {
                return false;
            }
        }
        return true;
    }
}

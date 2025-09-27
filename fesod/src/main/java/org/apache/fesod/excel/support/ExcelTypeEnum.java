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

package org.apache.fesod.excel.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.Getter;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.exception.ExcelCommonException;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.util.StringUtils;
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
    XLSX(".xlsx", new byte[] {80, 75, 3, 4});

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
            return XLSX;
        } else if (findMagic(XLS.magic, data)) {
            return XLS;
        }
        // csv has no fixed prefix, if the format is not specified, it defaults to csv
        return CSV;
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

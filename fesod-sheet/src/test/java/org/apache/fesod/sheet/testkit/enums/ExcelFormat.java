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

package org.apache.fesod.sheet.testkit.enums;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.apache.fesod.sheet.support.ExcelTypeEnum;

/**
 * Test-only enum bridging to the production {@link ExcelTypeEnum} with format capability metadata.
 *
 * <p>Each constant carries flags indicating which features (templates, images, encryption, styles)
 * are supported by that format, enabling parameterized tests to skip unsupported combinations
 * via {@code Assumptions.assumeTrue(format.supportsXxx())}.
 */
public enum ExcelFormat {
    XLSX(".xlsx", ExcelTypeEnum.XLSX, true, true, true, true),
    XLS(".xls", ExcelTypeEnum.XLS, true, true, false, true),
    CSV(".csv", ExcelTypeEnum.CSV, false, false, false, false);

    @Getter
    private final String extension;

    private final ExcelTypeEnum excelTypeEnum;
    private final boolean supportsTemplates;
    private final boolean supportsImages;
    private final boolean supportsEncryption;
    private final boolean supportsStyles;

    ExcelFormat(
            String extension,
            ExcelTypeEnum excelTypeEnum,
            boolean supportsTemplates,
            boolean supportsImages,
            boolean supportsEncryption,
            boolean supportsStyles) {
        this.extension = extension;
        this.excelTypeEnum = excelTypeEnum;
        this.supportsTemplates = supportsTemplates;
        this.supportsImages = supportsImages;
        this.supportsEncryption = supportsEncryption;
        this.supportsStyles = supportsStyles;
    }

    public ExcelTypeEnum toExcelTypeEnum() {
        return excelTypeEnum;
    }

    public boolean supportsTemplates() {
        return supportsTemplates;
    }

    public boolean supportsImages() {
        return supportsImages;
    }

    public boolean supportsEncryption() {
        return supportsEncryption;
    }

    public boolean supportsStyles() {
        return supportsStyles;
    }

    /**
     * Creates a temp file with the correct extension in the given directory.
     *
     * @param prefix filename prefix (at least 3 characters)
     * @param directory the directory in which the file is to be created
     * @return a newly created temp file with the format's extension
     * @throws IOException if the file cannot be created
     */
    public File createTempFile(String prefix, File directory) throws IOException {
        return File.createTempFile(prefix, extension, directory);
    }

    /**
     * Creates a temp file with the correct extension in the system temp directory.
     *
     * @param prefix filename prefix (at least 3 characters)
     * @return a newly created temp file with the format's extension
     * @throws IOException if the file cannot be created
     */
    public File createTempFile(String prefix) throws IOException {
        return File.createTempFile(prefix, extension);
    }

    /**
     * Looks up the {@code ExcelFormat} corresponding to the given production {@link ExcelTypeEnum}.
     *
     * @param type the production enum value
     * @return the matching {@code ExcelFormat}
     * @throws IllegalArgumentException if no match is found
     */
    public static ExcelFormat fromExcelTypeEnum(ExcelTypeEnum type) {
        for (ExcelFormat format : values()) {
            if (format.excelTypeEnum == type) {
                return format;
            }
        }
        throw new IllegalArgumentException("No ExcelFormat for ExcelTypeEnum: " + type);
    }
}

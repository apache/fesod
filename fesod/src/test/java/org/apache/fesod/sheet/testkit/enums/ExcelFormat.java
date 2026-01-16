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

import org.apache.fesod.sheet.support.ExcelTypeEnum;

/**
 * Test format enumeration with capability metadata.
 * Enables format-aware parameterized testing.
 */
public enum ExcelFormat {
    XLSX(".xlsx", ExcelTypeEnum.XLSX, true, true, true, true),
    XLS(".xls", ExcelTypeEnum.XLS, true, true, true, false),
    CSV(".csv", ExcelTypeEnum.CSV, false, false, false, false);

    private final String extension;
    private final ExcelTypeEnum excelType;
    private final boolean supportsStyle;
    private final boolean supportsMerge;
    private final boolean supportsFormula;
    private final boolean supportsStreaming;

    ExcelFormat(
            String extension,
            ExcelTypeEnum excelType,
            boolean supportsStyle,
            boolean supportsMerge,
            boolean supportsFormula,
            boolean supportsStreaming) {
        this.extension = extension;
        this.excelType = excelType;
        this.supportsStyle = supportsStyle;
        this.supportsMerge = supportsMerge;
        this.supportsFormula = supportsFormula;
        this.supportsStreaming = supportsStreaming;
    }

    // Getters...
    public String getExtension() {
        return extension;
    }

    public ExcelTypeEnum getExcelType() {
        return excelType;
    }

    public boolean supportsStyle() {
        return supportsStyle;
    }

    public boolean supportsMerge() {
        return supportsMerge;
    }

    public boolean supportsFormula() {
        return supportsFormula;
    }

    public boolean supportsStreaming() {
        return supportsStreaming;
    }

    /**
     * Check if this format supports a specific feature.
     * @param feature the feature to check
     * @return true if supported
     */
    public boolean supports(FormatFeature feature) {
        switch (feature) {
            case STYLE:
                return supportsStyle;
            case MERGE:
                return supportsMerge;
            case FORMULA:
                return supportsFormula;
            case STREAMING:
                return supportsStreaming;
            case SHEET_NAME:
                return this != CSV;
            case MULTIPLE_SHEETS:
                return this != CSV;
            default:
                return false;
        }
    }

    public enum FormatFeature {
        STYLE,
        MERGE,
        FORMULA,
        STREAMING,
        SHEET_NAME,
        MULTIPLE_SHEETS
    }
}

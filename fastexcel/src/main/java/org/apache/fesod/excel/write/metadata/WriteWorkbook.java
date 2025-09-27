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

package org.apache.fesod.excel.write.metadata;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.fesod.excel.support.ExcelTypeEnum;

/**
 * Workbook
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class WriteWorkbook extends WriteBasicParameter {
    /**
     * Excel type.The default is xlsx
     */
    private ExcelTypeEnum excelType;
    /**
     * Final output file
     * <p>
     * If 'outputStream' and 'file' all not empty, file first
     */
    private File file;
    /**
     * Final output stream
     * <p>
     * If 'outputStream' and 'file' all not empty, file first
     */
    private OutputStream outputStream;
    /**
     * charset.
     * Only work on the CSV file
     */
    private Charset charset;

    /**
     * Set the encoding prefix in the csv file, otherwise the office may open garbled characters.
     * Default true.
     */
    private Boolean withBom;

    /**
     * Template input stream
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private InputStream templateInputStream;

    /**
     * Template file.
     * This file is read into memory, excessive cases can lead to OOM.
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private File templateFile;
    /**
     * Default true.
     */
    private Boolean autoCloseStream;
    /**
     * Mandatory use 'inputStream' .Default is false
     */
    private Boolean mandatoryUseInputStream;
    /**
     * Whether the encryption
     * <p>
     * WARRING:Encryption is when the entire file is read into memory, so it is very memory intensive.
     */
    private String password;
    /**
     * Write excel in memory. Default false, the cache file is created and finally written to excel.
     * <p>
     * Comment and RichTextString are only supported in memory mode.
     */
    private Boolean inMemory;
    /**
     * Excel is also written in the event of an exception being thrown.The default false.
     */
    private Boolean writeExcelOnException;
    /**
     * Specifies CSVFormat for parsing.
     * Only work on the CSV file.
     */
    private CSVFormat csvFormat;
}

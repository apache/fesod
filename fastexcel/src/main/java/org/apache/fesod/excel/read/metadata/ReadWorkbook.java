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

package org.apache.fesod.excel.read.metadata;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.fesod.excel.cache.ReadCache;
import org.apache.fesod.excel.cache.selector.ReadCacheSelector;
import org.apache.fesod.excel.cache.selector.SimpleReadCacheSelector;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.fesod.excel.enums.ReadDefaultReturnEnum;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.read.listener.ModelBuildEventListener;
import org.apache.fesod.excel.support.ExcelTypeEnum;

/**
 * Workbook
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class ReadWorkbook extends ReadBasicParameter {
    /**
     * Excel type
     */
    private ExcelTypeEnum excelType;
    /**
     * Read InputStream
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private InputStream inputStream;
    /**
     * Read file
     * <p>
     * If 'inputStream' and 'file' all not empty, file first
     */
    private File file;
    /**
     * charset.
     * Only work on the CSV file
     */
    private Charset charset;
    /**
     * Mandatory use 'inputStream' .Default is false.
     * <p>
     * if false, Will transfer 'inputStream' to temporary files to improve efficiency
     */
    private Boolean mandatoryUseInputStream;
    /**
     * Default true
     */
    private Boolean autoCloseStream;
    /**
     * This object can be read in the Listener {@link AnalysisEventListener#invoke(Object, AnalysisContext)}
     * {@link AnalysisContext#getCustom()}
     */
    private Object customObject;
    /**
     * A cache that stores temp data to save memory.
     */
    private ReadCache readCache;
    /**
     * Ignore empty rows.Default is true.
     */
    private Boolean ignoreEmptyRow;
    /**
     * Select the cache.Default use {@link SimpleReadCacheSelector}
     */
    private ReadCacheSelector readCacheSelector;
    /**
     * Whether the encryption
     */
    private String password;
    /**
     * SAXParserFactory used when reading xlsx.
     * <p>
     * The default will automatically find.
     * <p>
     * Please pass in the name of a class ,like : "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
     *
     * @see SAXParserFactory#newInstance()
     * @see SAXParserFactory#newInstance(String, ClassLoader)
     */
    private String xlsxSAXParserFactoryName;
    /**
     * Whether to use the default listener, which is used by default.
     * <p>
     * The {@link ModelBuildEventListener} is loaded by default to convert the object.
     * default is true.
     */
    private Boolean useDefaultListener;

    /**
     * Read not to {@code org.apache.fesod.excel.metadata.BasicParameter#clazz} value, the default will return type.
     * Is only effective when set `useDefaultListener=true` or `useDefaultListener=null`.
     *
     * @see ReadDefaultReturnEnum
     */
    private ReadDefaultReturnEnum readDefaultReturn;

    /**
     * Read some additional fields. None are read by default.
     *
     * @see CellExtraTypeEnum
     */
    private Set<CellExtraTypeEnum> extraReadSet;

    /**
     * The number of rows to read, the default is all, start with 0.
     */
    private Integer numRows;

    /**
     * Ignore hidden sheet.
     */
    private Boolean ignoreHiddenSheet;

    /**
     * Specifies CSVFormat for parsing.
     * Only work on the CSV file.
     */
    private CSVFormat csvFormat;
}

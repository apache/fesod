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

package org.apache.fesod.excel;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.analysis.ExcelAnalyser;
import org.apache.fesod.excel.analysis.ExcelAnalyserImpl;
import org.apache.fesod.excel.analysis.ExcelReadExecutor;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;

/**
 * Excel readers are all read in event mode.
 *
 */
@Slf4j
public class ExcelReader implements Closeable {

    /**
     * Analyser
     */
    private final ExcelAnalyser excelAnalyser;

    public ExcelReader(ReadWorkbook readWorkbook) {
        excelAnalyser = new ExcelAnalyserImpl(readWorkbook);
    }

    /**
     * Parse all sheet content by default
     *
     * @deprecated lease use {@link #readAll()}
     */
    @Deprecated
    public void read() {
        readAll();
    }

    /***
     * Parse all sheet content by default
     */
    public void readAll() {
        excelAnalyser.analysis(null, Boolean.TRUE);
    }

    /**
     * Parse the specified sheet，SheetNo start from 0
     *
     * @param readSheet Read sheet
     */
    public ExcelReader read(ReadSheet... readSheet) {
        return read(Arrays.asList(readSheet));
    }

    /**
     * Read multiple sheets.
     *
     * @param readSheetList
     * @return
     */
    public ExcelReader read(List<ReadSheet> readSheetList) {
        excelAnalyser.analysis(readSheetList, Boolean.FALSE);
        return this;
    }

    /**
     * Context for the entire execution process
     *
     * @return
     */
    public AnalysisContext analysisContext() {
        return excelAnalyser.analysisContext();
    }

    /**
     * Current executor
     *
     * @return
     */
    public ExcelReadExecutor excelExecutor() {
        return excelAnalyser.excelExecutor();
    }

    /**
     * @return
     * @deprecated please use {@link #analysisContext()}
     */
    @Deprecated
    public AnalysisContext getAnalysisContext() {
        return analysisContext();
    }

    /**
     * Complete the entire read file.Release the cache and close stream.
     */
    public void finish() {
        if (excelAnalyser != null) {
            excelAnalyser.finish();
        }
    }

    @Override
    public void close() {
        finish();
    }

    /**
     * Prevents calls to {@link #finish} from freeing the cache
     *
     */
    @Override
    protected void finalize() {
        try {
            finish();
        } catch (Throwable e) {
            log.warn("Destroy object failed", e);
        }
    }
}

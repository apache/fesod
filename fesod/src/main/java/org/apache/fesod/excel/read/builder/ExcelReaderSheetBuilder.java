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

package org.apache.fesod.excel.read.builder;

import java.util.List;
import java.util.Objects;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.event.SyncReadListener;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.read.metadata.ReadSheet;

/**
 * Build sheet
 *
 *
 */
public class ExcelReaderSheetBuilder extends AbstractExcelReaderParameterBuilder<ExcelReaderSheetBuilder, ReadSheet> {
    private ExcelReader excelReader;
    /**
     * Sheet
     */
    private final ReadSheet readSheet;

    public ExcelReaderSheetBuilder() {
        this.readSheet = new ReadSheet();
    }

    public ExcelReaderSheetBuilder(ExcelReader excelReader) {
        this.readSheet = new ReadSheet();
        this.excelReader = excelReader;
    }

    /**
     * Starting from 0
     *
     * @param sheetNo
     * @return
     */
    public ExcelReaderSheetBuilder sheetNo(Integer sheetNo) {
        readSheet.setSheetNo(sheetNo);
        return this;
    }

    public ExcelReaderSheetBuilder sheetNoIfNotNull(Integer sheetNo) {
        if (Objects.nonNull(sheetNo)) {
            readSheet.setSheetNo(sheetNo);
        }
        return this;
    }

    /**
     * sheet name
     *
     * @param sheetName
     * @return
     */
    public ExcelReaderSheetBuilder sheetName(String sheetName) {
        readSheet.setSheetName(sheetName);
        return this;
    }

    public ExcelReaderSheetBuilder sheetNameIfNotNull(String sheetName) {
        if (Objects.nonNull(sheetName)) {
            readSheet.setSheetName(sheetName);
        }
        return this;
    }

    /**
     * numRows
     *
     * @param numRows
     * @return
     */
    public ExcelReaderSheetBuilder numRows(Integer numRows) {
        readSheet.setNumRows(numRows);
        return this;
    }

    public ExcelReaderSheetBuilder numRowsIfNotNull(Integer numRows) {
        if (Objects.nonNull(numRows)) {
            readSheet.setNumRows(numRows);
        }
        return this;
    }

    public ReadSheet build() {
        return readSheet;
    }

    /**
     * Sax read
     */
    public void doRead() {
        if (excelReader == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.read().sheet()' to call this method");
        }
        excelReader.read(build());
        excelReader.finish();
    }

    /**
     * Synchronous reads return results
     *
     * @return
     */
    public <T> List<T> doReadSync() {
        if (excelReader == null) {
            throw new ExcelAnalysisException("Must use 'FastExcelFactory.read().sheet()' to call this method");
        }
        SyncReadListener syncReadListener = new SyncReadListener();
        registerReadListener(syncReadListener);
        excelReader.read(build());
        excelReader.finish();
        return (List<T>) syncReadListener.getList();
    }

    @Override
    protected ReadSheet parameter() {
        return readSheet;
    }
}

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

package org.apache.fesod.sheet.analysis.v03.handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.metadata.data.CellData;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.StringRecord;

/**
 * Handles {@link StringRecord} in the XLS (BIFF) reading pipeline.
 * <p>
 * A {@code StringRecord} immediately follows a {@code FormulaRecord} whose cached result type is
 * {@code STRING}. It carries the formula's string value, which is assigned to the temporary cell data
 * stashed by {@link FormulaRecordHandler} and then committed to the current row's cell map.
 */
@Slf4j
public class StringRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Completes a pending string-type formula cell by setting its string value and storing it in the
     * current sheet's cell map.
     * <p>
     * If no temporary cell data exists (i.e. no preceding {@code FormulaRecord} was encountered), a
     * warning is logged and the record is skipped.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the record to process, expected to be a {@link StringRecord}
     * @throws ClassCastException if {@code record} is not a {@link StringRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        // String for formula
        StringRecord srec = (StringRecord) record;
        XlsReadSheetHolder xlsReadSheetHolder = xlsReadContext.xlsReadSheetHolder();
        CellData<?> tempCellData = xlsReadSheetHolder.getTempCellData();
        if (tempCellData == null) {
            log.warn("String type formula but no value found.");
            return;
        }
        tempCellData.setStringValue(srec.getString());
        xlsReadSheetHolder.getCellMap().put(tempCellData.getColumnIndex(), tempCellData);
        xlsReadSheetHolder.setTempCellData(null);
    }
}

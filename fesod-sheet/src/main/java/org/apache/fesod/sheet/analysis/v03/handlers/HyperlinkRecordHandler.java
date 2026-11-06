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

import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.CellExtraTypeEnum;
import org.apache.fesod.sheet.metadata.CellExtra;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Handles the XLS {@code HYPERLINK} record and exposes it as a {@link CellExtra} of type
 * {@link CellExtraTypeEnum#HYPERLINK}, covering the hyperlink's cell range and address.
 * <p>
 * Only active when {@link CellExtraTypeEnum#HYPERLINK} is present in the extra read set; otherwise the record is
 * skipped as an ignorable record.
 */
public class HyperlinkRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Determines whether this handler should process the record, i.e. whether hyperlink extras are requested.
     *
     * @param xlsReadContext the current XLS read context, not {@code null}
     * @param record         the record to test
     * @return {@code true} if the extra read set contains {@link CellExtraTypeEnum#HYPERLINK}
     */
    @Override
    public boolean support(XlsReadContext xlsReadContext, Record record) {
        return xlsReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.HYPERLINK);
    }

    /**
     * Builds a {@link CellExtra} from the hyperlink's address and cell range, stores it in the sheet read holder
     * and notifies the analysis event processor.
     *
     * @param xlsReadContext the current XLS read context, not {@code null}
     * @param record         the record to process, must be a {@link HyperlinkRecord}
     * @throws ClassCastException if {@code record} is not a {@link HyperlinkRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        HyperlinkRecord hr = (HyperlinkRecord) record;
        CellExtra cellExtra = new CellExtra(
                CellExtraTypeEnum.HYPERLINK,
                hr.getAddress(),
                hr.getFirstRow(),
                hr.getLastRow(),
                hr.getFirstColumn(),
                hr.getLastColumn());
        xlsReadContext.xlsReadSheetHolder().setCellExtra(cellExtra);
        xlsReadContext.analysisEventProcessor().extra(xlsReadContext);
    }
}

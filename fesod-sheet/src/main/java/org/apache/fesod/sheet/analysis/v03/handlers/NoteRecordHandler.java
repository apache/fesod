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
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Handles the XLS {@code NOTE} record and exposes the attached cell comment as a {@link CellExtra} of type
 * {@link CellExtraTypeEnum#COMMENT}, covering the comment's anchor row and column.
 * <p>
 * The {@code NOTE} record only carries the position; the comment text is taken from the object cache map, which is
 * filled beforehand by the {@code TEXTOBJECT} record using the same shape id. When no text has been cached for the
 * shape id, the extra's value will be {@code null}.
 * <p>
 * Only active when {@link CellExtraTypeEnum#COMMENT} is present in the extra read set; otherwise the record is
 * skipped as an ignorable record.
 */
public class NoteRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Determines whether this handler should process the record, i.e. whether comment extras are requested.
     *
     * @param xlsReadContext the current XLS read context, not {@code null}
     * @param record         the record to test
     * @return {@code true} if the extra read set contains {@link CellExtraTypeEnum#COMMENT}
     */
    @Override
    public boolean support(XlsReadContext xlsReadContext, Record record) {
        return xlsReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.COMMENT);
    }

    /**
     * Resolves the comment text cached for the record's shape id, builds a {@link CellExtra} with the comment's
     * cell position, stores it in the sheet read holder and notifies the analysis event processor.
     *
     * @param xlsReadContext the current XLS read context, not {@code null}
     * @param record         the record to process, must be a {@link NoteRecord}
     * @throws ClassCastException if {@code record} is not a {@link NoteRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        NoteRecord nr = (NoteRecord) record;
        String text = xlsReadContext.xlsReadSheetHolder().getObjectCacheMap().get(nr.getShapeId());
        CellExtra cellExtra = new CellExtra(CellExtraTypeEnum.COMMENT, text, nr.getRow(), nr.getColumn());
        xlsReadContext.xlsReadSheetHolder().setCellExtra(cellExtra);
        xlsReadContext.analysisEventProcessor().extra(xlsReadContext);
    }
}

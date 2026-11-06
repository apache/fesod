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
import org.apache.fesod.sheet.enums.CellExtraTypeEnum;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.TextObjectRecord;

/**
 * Handles the XLS {@link TextObjectRecord} (BIFF {@code TEXTOBJECT} record), which carries the text content of a
 * comment object.
 * <p>
 * The text is cached in the sheet's object cache map, keyed by the temporary object index previously stored by the
 * {@code OBJ} record, so that a subsequent {@code NOTE} record can resolve the comment text for the matching shape id.
 * <p>
 * Only active when {@link CellExtraTypeEnum#COMMENT} is present in the extra read set; otherwise the record is
 * skipped as an ignorable record.
 */
@Slf4j
public class TextObjectRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

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
     * Caches the record's text in the object cache map under the current temporary object index and then clears that
     * index. If no temporary object index is present, the record is ignored.
     *
     * @param xlsReadContext the current XLS read context, not {@code null}
     * @param record         the record to process, must be a {@link TextObjectRecord}
     * @throws ClassCastException if {@code record} is not a {@link TextObjectRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        TextObjectRecord tor = (TextObjectRecord) record;
        XlsReadSheetHolder xlsReadSheetHolder = xlsReadContext.xlsReadSheetHolder();
        Integer tempObjectIndex = xlsReadSheetHolder.getTempObjectIndex();
        if (tempObjectIndex == null) {
            log.debug("tempObjectIndex is null.");
            return;
        }
        xlsReadSheetHolder.getObjectCacheMap().put(tempObjectIndex, tor.getStr().getString());
        xlsReadSheetHolder.setTempObjectIndex(null);
    }
}

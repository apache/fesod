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

import java.util.LinkedHashMap;
import org.apache.fesod.common.util.BooleanUtils;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.Cell;
import org.apache.fesod.sheet.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.poi.hssf.record.Record;

/**
 * Handler for the XLS {@link org.apache.poi.hssf.record.EOFRecord} (end of file), which marks the end of a
 * worksheet's binary stream.
 * <p>
 * It finalizes the sheet by closing any pending row and then invoking the {@code endSheet} event, so that
 * listeners receive complete data even when the file lacks the termination records of the last row.
 */
public class EofRecordHandler extends AbstractXlsRecordHandler {

    /**
     * Processes the EOF record of the current worksheet.
     * <p>
     * If reading was stopped early or the sheet is ignored, only the pending {@code endSheet} event (when the
     * sheet was manually stopped) is emitted. Otherwise, when the sheet lacks the termination record of the last
     * row, a synthetic row is forged and flushed via the {@code endRow} event before the {@code endSheet} event
     * is triggered.
     *
     * @param xlsReadContext the context of the current XLS read operation
     * @param record         the EOF record being processed; not inspected directly
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        if (xlsReadContext.readSheetHolder() == null) {
            return;
        }

        // Represents the current sheet does not need to be read or the user manually stopped reading the sheet.
        if (BooleanUtils.isTrue(xlsReadContext.xlsReadWorkbookHolder().getIgnoreRecord())) {
            // When the user manually stops reading the sheet, the method to end the sheet needs to be called.
            if (BooleanUtils.isTrue(xlsReadContext.xlsReadWorkbookHolder().getCurrentSheetStopped())) {
                xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
            }
            return;
        }

        // Sometimes tables lack the end record of the last column
        if (!xlsReadContext.xlsReadSheetHolder().getCellMap().isEmpty()) {
            XlsReadSheetHolder xlsReadSheetHolder = xlsReadContext.xlsReadSheetHolder();
            // Forge a termination data
            xlsReadContext.readRowHolder(new ReadRowHolder(
                    xlsReadContext.xlsReadSheetHolder().getRowIndex() + 1,
                    xlsReadSheetHolder.getTempRowType(),
                    xlsReadContext.readSheetHolder().getGlobalConfiguration(),
                    xlsReadSheetHolder.getCellMap()));
            xlsReadContext.analysisEventProcessor().endRow(xlsReadContext);
            xlsReadSheetHolder.setCellMap(new LinkedHashMap<Integer, Cell>());
            xlsReadSheetHolder.setTempRowType(RowTypeEnum.EMPTY);
        }

        xlsReadContext.analysisEventProcessor().endSheet(xlsReadContext);
    }
}

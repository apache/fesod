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

package org.apache.fesod.excel.analysis.v03.handlers;

import java.util.LinkedHashMap;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.fesod.excel.util.BooleanUtils;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class EofRecordHandler extends AbstractXlsRecordHandler {

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

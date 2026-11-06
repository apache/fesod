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

import java.math.BigDecimal;
import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.constant.BuiltinFormats;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler for BIFF {@link RKRecord}.
 *
 * <p>RK is a compact numeric cell used by older Excel files. The event-API path still delivers these
 * records; they must be decoded like {@link NumberRecordHandler}, not treated as empty cells.
 */
public class RkRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record rec) {
        RKRecord re = (RKRecord) rec;

        Integer targetColumnIndex = xlsReadContext.readSheetHolder().determineTargetColumnIndex(re.getColumn());
        if (targetColumnIndex == null) {
            return;
        }

        short dataFormat = (short) xlsReadContext
                .xlsReadWorkbookHolder()
                .getFormatTrackingHSSFListener()
                .getFormatIndex(re);
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(dataFormat);
        dataFormatData.setFormat(BuiltinFormats.getBuiltinFormat(
                dataFormat,
                xlsReadContext
                        .xlsReadWorkbookHolder()
                        .getFormatTrackingHSSFListener()
                        .getFormatString(re),
                xlsReadContext.readSheetHolder().getGlobalConfiguration().getLocale()));

        ReadCellData<?> cellData =
                ReadCellData.newInstanceOriginal(BigDecimal.valueOf(re.getRKNumber()), re.getRow(), targetColumnIndex);
        cellData.setDataFormatData(dataFormatData);
        xlsReadContext.xlsReadSheetHolder().getCellMap().put(targetColumnIndex, cellData);
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
    }
}

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

import java.util.Map;
import org.apache.fesod.excel.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.excel.cache.ReadCache;
import org.apache.fesod.excel.context.xls.XlsReadContext;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class LabelSstRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        LabelSSTRecord lsrec = (LabelSSTRecord) record;
        ReadCache readCache = xlsReadContext.readWorkbookHolder().getReadCache();
        Map<Integer, Cell> cellMap = xlsReadContext.xlsReadSheetHolder().getCellMap();
        if (readCache == null) {
            cellMap.put(
                    (int) lsrec.getColumn(), ReadCellData.newEmptyInstance(lsrec.getRow(), (int) lsrec.getColumn()));
            return;
        }
        String data = readCache.get(lsrec.getSSTIndex());
        if (data == null) {
            cellMap.put(
                    (int) lsrec.getColumn(), ReadCellData.newEmptyInstance(lsrec.getRow(), (int) lsrec.getColumn()));
            return;
        }

        GlobalConfiguration globalConfiguration =
                xlsReadContext.currentReadHolder().globalConfiguration();
        if (globalConfiguration.getAutoStrip()) {
            data = StringUtils.strip(data);
        } else if (globalConfiguration.getAutoTrim()) {
            data = data.trim();
        }
        cellMap.put((int) lsrec.getColumn(), ReadCellData.newInstance(data, lsrec.getRow(), (int) lsrec.getColumn()));
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
    }
}

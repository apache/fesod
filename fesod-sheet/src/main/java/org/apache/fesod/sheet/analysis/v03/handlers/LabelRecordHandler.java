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

package org.apache.fesod.sheet.analysis.v03.handlers;

import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class LabelRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        LabelRecord lrec = (LabelRecord) record;
        String data = lrec.getValue();
        if (data != null) {
            GlobalConfiguration globalConfiguration =
                    xlsReadContext.currentReadHolder().globalConfiguration();
            if (globalConfiguration.getAutoStrip()) {
                data = StringUtils.strip(data);
            } else if (globalConfiguration.getAutoTrim()) {
                data = data.trim();
            }
        }
        xlsReadContext
                .xlsReadSheetHolder()
                .getCellMap()
                .put((int) lrec.getColumn(), ReadCellData.newInstance(data, lrec.getRow(), (int) lrec.getColumn()));
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
    }
}

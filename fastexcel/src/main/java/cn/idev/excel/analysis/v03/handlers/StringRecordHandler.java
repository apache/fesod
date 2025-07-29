/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.idev.excel.analysis.v03.handlers;

import cn.idev.excel.analysis.v03.IgnorableXlsRecordHandler;
import cn.idev.excel.context.xls.XlsReadContext;
import cn.idev.excel.metadata.data.CellData;
import cn.idev.excel.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.StringRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Record handler
 */
public class StringRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringRecordHandler.class);

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        // String for formula
        StringRecord srec = (StringRecord) record;
        XlsReadSheetHolder xlsReadSheetHolder = xlsReadContext.xlsReadSheetHolder();
        CellData<?> tempCellData = xlsReadSheetHolder.getTempCellData();
        if (tempCellData == null) {
            LOGGER.warn("String type formula but no value found.");
            return;
        }
        tempCellData.setStringValue(srec.getString());
        xlsReadSheetHolder.getCellMap().put(tempCellData.getColumnIndex(), tempCellData);
        xlsReadSheetHolder.setTempCellData(null);
    }
}

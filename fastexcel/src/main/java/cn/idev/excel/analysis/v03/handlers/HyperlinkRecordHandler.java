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
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.metadata.CellExtra;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class HyperlinkRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {
    @Override
    public boolean support(XlsReadContext xlsReadContext, Record record) {
        return xlsReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.HYPERLINK);
    }

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

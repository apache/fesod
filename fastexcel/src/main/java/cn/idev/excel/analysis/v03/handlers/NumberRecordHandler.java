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
import cn.idev.excel.constant.BuiltinFormats;
import cn.idev.excel.context.xls.XlsReadContext;
import cn.idev.excel.enums.RowTypeEnum;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.ReadCellData;
import java.math.BigDecimal;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Record handler
 */
public class NumberRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        NumberRecord nr = (NumberRecord) record;
        ReadCellData<?> cellData =
                ReadCellData.newInstanceOriginal(BigDecimal.valueOf(nr.getValue()), nr.getRow(), (int) nr.getColumn());
        short dataFormat = (short) xlsReadContext
                .xlsReadWorkbookHolder()
                .getFormatTrackingHSSFListener()
                .getFormatIndex(nr);
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(dataFormat);
        dataFormatData.setFormat(BuiltinFormats.getBuiltinFormat(
                dataFormat,
                xlsReadContext
                        .xlsReadWorkbookHolder()
                        .getFormatTrackingHSSFListener()
                        .getFormatString(nr),
                xlsReadContext.readSheetHolder().getGlobalConfiguration().getLocale()));
        cellData.setDataFormatData(dataFormatData);
        xlsReadContext.xlsReadSheetHolder().getCellMap().put((int) nr.getColumn(), cellData);
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
    }
}

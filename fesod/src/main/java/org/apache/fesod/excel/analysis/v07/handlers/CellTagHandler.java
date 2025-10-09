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

package org.apache.fesod.excel.analysis.v07.handlers;

import java.math.BigDecimal;
import org.apache.fesod.excel.constant.ExcelXmlConstants;
import org.apache.fesod.excel.constant.FastExcelConstants;
import org.apache.fesod.excel.context.xlsx.XlsxReadContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.excel.util.BooleanUtils;
import org.apache.fesod.excel.util.PositionUtils;
import org.apache.fesod.excel.util.StringUtils;
import org.xml.sax.Attributes;

/**
 * Cell Handler
 *
 */
public class CellTagHandler extends AbstractXlsxTagHandler {

    private static final int DEFAULT_FORMAT_INDEX = 0;

    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        xlsxReadSheetHolder.setColumnIndex(PositionUtils.getCol(
                attributes.getValue(ExcelXmlConstants.ATTRIBUTE_R), xlsxReadSheetHolder.getColumnIndex()));

        // t="s" ,it means String
        // t="str" ,it means String,but does not need to be read in the 'sharedStrings.xml'
        // t="inlineStr" ,it means String,but does not need to be read in the 'sharedStrings.xml'
        // t="b" ,it means Boolean
        // t="e" ,it means Error
        // t="n" ,it means Number
        // t is null ,it means Empty or Number
        CellDataTypeEnum type = CellDataTypeEnum.buildFromCellType(attributes.getValue(ExcelXmlConstants.ATTRIBUTE_T));
        xlsxReadSheetHolder.setTempCellData(new ReadCellData<>(type));
        xlsxReadSheetHolder.setTempData(new StringBuilder());

        // Put in data transformation information
        String dateFormatIndex = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_S);
        int dateFormatIndexInteger;
        if (StringUtils.isEmpty(dateFormatIndex)) {
            dateFormatIndexInteger = DEFAULT_FORMAT_INDEX;
        } else {
            dateFormatIndexInteger = Integer.parseInt(dateFormatIndex);
        }

        xlsxReadSheetHolder
                .getTempCellData()
                .setDataFormatData(xlsxReadContext.xlsxReadWorkbookHolder().dataFormatData(dateFormatIndexInteger));
    }

    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        ReadCellData<?> tempCellData = xlsxReadSheetHolder.getTempCellData();
        StringBuilder tempData = xlsxReadSheetHolder.getTempData();
        String tempDataString = tempData.toString();
        CellDataTypeEnum oldType = tempCellData.getType();
        switch (oldType) {
            case STRING:
                // In some cases, although cell type is a string, it may be an empty tag
                if (StringUtils.isEmpty(tempDataString)) {
                    break;
                }
                String stringValue =
                        xlsxReadContext.readWorkbookHolder().getReadCache().get(Integer.valueOf(tempDataString));
                tempCellData.setStringValue(stringValue);
                break;
            case DIRECT_STRING:
            case ERROR:
                tempCellData.setStringValue(tempDataString);
                tempCellData.setType(CellDataTypeEnum.STRING);
                break;
            case BOOLEAN:
                if (StringUtils.isEmpty(tempDataString)) {
                    tempCellData.setType(CellDataTypeEnum.EMPTY);
                    break;
                }
                tempCellData.setBooleanValue(BooleanUtils.valueOf(tempData.toString()));
                break;
            case NUMBER:
            case EMPTY:
                if (StringUtils.isEmpty(tempDataString)) {
                    tempCellData.setType(CellDataTypeEnum.EMPTY);
                    break;
                }
                tempCellData.setType(CellDataTypeEnum.NUMBER);
                tempCellData.setOriginalNumberValue(new BigDecimal(tempDataString));
                tempCellData.setNumberValue(
                        tempCellData.getOriginalNumberValue().round(FastExcelConstants.EXCEL_MATH_CONTEXT));
                break;
            default:
                throw new IllegalStateException("Cannot set values now");
        }

        if (tempCellData.getStringValue() != null) {
            GlobalConfiguration globalConfiguration =
                    xlsxReadContext.currentReadHolder().globalConfiguration();
            if (globalConfiguration.getAutoStrip()) {
                tempCellData.setStringValue(StringUtils.strip(tempCellData.getStringValue()));
            } else if (globalConfiguration.getAutoTrim()) {
                tempCellData.setStringValue(tempCellData.getStringValue().trim());
            }
        }

        tempCellData.checkEmpty();
        tempCellData.setRowIndex(xlsxReadSheetHolder.getRowIndex());
        tempCellData.setColumnIndex(xlsxReadSheetHolder.getColumnIndex());
        xlsxReadSheetHolder.getCellMap().put(xlsxReadSheetHolder.getColumnIndex(), tempCellData);
    }
}

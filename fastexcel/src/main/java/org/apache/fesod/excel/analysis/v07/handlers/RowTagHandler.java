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

import java.util.LinkedHashMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.fesod.excel.constant.ExcelXmlConstants;
import org.apache.fesod.excel.context.xlsx.XlsxReadContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.excel.util.PositionUtils;
import org.xml.sax.Attributes;

/**
 * Cell Handler
 *
 */
public class RowTagHandler extends AbstractXlsxTagHandler {

    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        int rowIndex = PositionUtils.getRowByRowTagt(
                attributes.getValue(ExcelXmlConstants.ATTRIBUTE_R), xlsxReadSheetHolder.getRowIndex());
        Integer lastRowIndex = xlsxReadContext.readSheetHolder().getRowIndex();
        while (lastRowIndex + 1 < rowIndex) {
            xlsxReadContext.readRowHolder(new ReadRowHolder(
                    lastRowIndex + 1,
                    RowTypeEnum.EMPTY,
                    xlsxReadSheetHolder.getGlobalConfiguration(),
                    new LinkedHashMap<Integer, Cell>()));
            xlsxReadContext.analysisEventProcessor().endRow(xlsxReadContext);
            xlsxReadSheetHolder.setColumnIndex(null);
            xlsxReadSheetHolder.setCellMap(new LinkedHashMap<Integer, Cell>());
            lastRowIndex++;
        }
        xlsxReadSheetHolder.setRowIndex(rowIndex);
    }

    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        RowTypeEnum rowType = MapUtils.isEmpty(xlsxReadSheetHolder.getCellMap()) ? RowTypeEnum.EMPTY : RowTypeEnum.DATA;
        // It's possible that all of the cells in the row are empty
        if (rowType == RowTypeEnum.DATA) {
            boolean hasData = false;
            for (Cell cell : xlsxReadSheetHolder.getCellMap().values()) {
                if (!(cell instanceof ReadCellData)) {
                    hasData = true;
                    break;
                }
                ReadCellData<?> readCellData = (ReadCellData<?>) cell;
                if (readCellData.getType() != CellDataTypeEnum.EMPTY) {
                    hasData = true;
                    break;
                }
            }
            if (!hasData) {
                rowType = RowTypeEnum.EMPTY;
            }
        }
        xlsxReadContext.readRowHolder(new ReadRowHolder(
                xlsxReadSheetHolder.getRowIndex(),
                rowType,
                xlsxReadSheetHolder.getGlobalConfiguration(),
                xlsxReadSheetHolder.getCellMap()));
        xlsxReadContext.analysisEventProcessor().endRow(xlsxReadContext);
        xlsxReadSheetHolder.setColumnIndex(null);
        xlsxReadSheetHolder.setCellMap(new LinkedHashMap<>());
    }
}

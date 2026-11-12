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

package org.apache.fesod.sheet.analysis.v07.handlers;

import java.util.LinkedHashMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.fesod.common.util.PositionUtils;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.Cell;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.xml.sax.Attributes;

/**
 * SAX handler for the XLSX {@code <row>} element.
 * <p>
 * On {@code startElement}, it reconciles the physical row index with the sheet's cursor so that
 * rows missing from the XML are surfaced as {@link RowTypeEnum#EMPTY} empty rows. On
 * {@code endElement}, it classifies the collected cells and dispatches the completed row to the
 * {@link org.apache.fesod.sheet.read.processor.AnalysisEventProcessor}.
 */
public class RowTagHandler extends AbstractXlsxTagHandler {

    /**
     * Handles the start of a {@code <row>} element: resolves the target row index from the {@code r}
     * attribute and, when it jumps ahead of the current cursor, emits intervening empty rows before
     * advancing the sheet holder to the new index.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name (always the {@code <row>} element)
     * @param attributes      the tag attributes, from which the {@code r} attribute is read
     */
    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        int rowIndex = PositionUtils.getRowByRowTag(
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

    /**
     * Handles the end of a {@code <row>} element: determines whether the row carries real data or
     * should be downgraded to {@link RowTypeEnum#EMPTY} when every cell is empty, then publishes the
     * row through the read holder and resets the sheet holder's column index and cell map.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name (always the {@code <row>} element)
     */
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

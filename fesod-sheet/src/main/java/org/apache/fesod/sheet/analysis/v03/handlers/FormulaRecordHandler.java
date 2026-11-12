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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.constant.BuiltinFormats;
import org.apache.fesod.sheet.constant.FesodSheetConstants;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.Cell;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.metadata.data.FormulaData;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Handler for XLS (BIFF8) {@link FormulaRecord}s, which carry the cached result and the parsed expression of a
 * formula cell.
 * <p>
 * Each record is converted into a {@link ReadCellData} that holds the formula text (restored via
 * {@link HSSFFormulaParser}) plus its cached value, whose data type comes from
 * {@link FormulaRecord#getCachedResultType()}. Numeric, error, boolean and empty results are written to the current
 * row immediately, while a string result is stashed as temporary cell data and finished by the following
 * {@code STRING} record.
 */
@Slf4j
public class FormulaRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Process a {@link FormulaRecord} and store the resulting cell data in the current sheet's cell map.
     * <p>
     * The record is skipped when the source column maps to no target column. If the formula text cannot be restored
     * from the parsed expression, the failure is logged at debug level and the cell is still emitted with a
     * {@code null} formula value.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the record to process, a {@link FormulaRecord}, never {@code null}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        FormulaRecord frec = (FormulaRecord) record;

        Integer targetColumnIndex = xlsReadContext.readSheetHolder().determineTargetColumnIndex(frec.getColumn());
        if (targetColumnIndex == null) {
            return;
        }

        Map<Integer, Cell> cellMap = xlsReadContext.xlsReadSheetHolder().getCellMap();
        ReadCellData<?> tempCellData = new ReadCellData<>();
        tempCellData.setRowIndex(frec.getRow());
        tempCellData.setColumnIndex(targetColumnIndex);
        CellType cellType = CellType.forInt(frec.getCachedResultType());
        String formulaValue = null;
        try {
            formulaValue = HSSFFormulaParser.toFormulaString(
                    xlsReadContext.xlsReadWorkbookHolder().getHssfWorkbook(), frec.getParsedExpression());
        } catch (Exception e) {
            log.debug("Get formula value error.", e);
        }
        FormulaData formulaData = new FormulaData();
        formulaData.setFormulaValue(formulaValue);
        tempCellData.setFormulaData(formulaData);
        xlsReadContext.xlsReadSheetHolder().setTempRowType(RowTypeEnum.DATA);
        switch (cellType) {
            case STRING:
                // Formula result is a string
                // This is stored in the next record
                tempCellData.setType(CellDataTypeEnum.STRING);
                xlsReadContext.xlsReadSheetHolder().setTempCellData(tempCellData);
                break;
            case NUMERIC:
                tempCellData.setType(CellDataTypeEnum.NUMBER);
                tempCellData.setOriginalNumberValue(BigDecimal.valueOf(frec.getValue()));
                tempCellData.setNumberValue(
                        tempCellData.getOriginalNumberValue().round(FesodSheetConstants.EXCEL_MATH_CONTEXT));
                int dataFormat = xlsReadContext
                        .xlsReadWorkbookHolder()
                        .getFormatTrackingHSSFListener()
                        .getFormatIndex(frec);
                DataFormatData dataFormatData = new DataFormatData();
                dataFormatData.setIndex((short) dataFormat);
                dataFormatData.setFormat(BuiltinFormats.getBuiltinFormat(
                        dataFormatData.getIndex(),
                        xlsReadContext
                                .xlsReadWorkbookHolder()
                                .getFormatTrackingHSSFListener()
                                .getFormatString(frec),
                        xlsReadContext
                                .readSheetHolder()
                                .getGlobalConfiguration()
                                .getLocale()));
                tempCellData.setDataFormatData(dataFormatData);
                cellMap.put(targetColumnIndex, tempCellData);
                break;
            case ERROR:
                tempCellData.setType(CellDataTypeEnum.ERROR);
                tempCellData.setStringValue(ErrorEval.getText(frec.getCachedErrorValue()));
                cellMap.put(targetColumnIndex, tempCellData);
                break;
            case BOOLEAN:
                tempCellData.setType(CellDataTypeEnum.BOOLEAN);
                tempCellData.setBooleanValue(frec.getCachedBooleanValue());
                cellMap.put(targetColumnIndex, tempCellData);
                break;
            default:
                tempCellData.setType(CellDataTypeEnum.EMPTY);
                cellMap.put(targetColumnIndex, tempCellData);
                break;
        }
    }
}

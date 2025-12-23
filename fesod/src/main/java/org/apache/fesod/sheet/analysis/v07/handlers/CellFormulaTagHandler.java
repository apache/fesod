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

package org.apache.fesod.sheet.analysis.v07.handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.metadata.data.FormulaData;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.sheet.util.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.xml.sax.Attributes;

/**
 * Cell Handler
 *
 */
@Slf4j
public class CellFormulaTagHandler extends AbstractXlsxTagHandler {

    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        xlsxReadSheetHolder.setTempFormula(new StringBuilder());

        String formulaType = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_T);
        String sharedIndex = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_SI);

        xlsxReadSheetHolder.setTempFormulaType(formulaType);
        xlsxReadSheetHolder.setTempFormulaSharedIndex(sharedIndex != null ? Integer.parseInt(sharedIndex) : null);
    }

    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        String formulaText = xlsxReadSheetHolder.getTempFormula().toString();
        String formulaType = xlsxReadSheetHolder.getTempFormulaType();
        Integer sharedIndex = xlsxReadSheetHolder.getTempFormulaSharedIndex();

        if (ExcelXmlConstants.ATTRIBUTE_SHARED.equals(formulaType) && sharedIndex != null) {
            formulaText = handleSharedFormula(xlsxReadSheetHolder, formulaText, sharedIndex);
        }

        FormulaData formulaData = new FormulaData();
        formulaData.setFormulaValue(formulaText);
        xlsxReadSheetHolder.getTempCellData().setFormulaData(formulaData);
    }

    @Override
    public void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length) {
        xlsxReadContext.xlsxReadSheetHolder().getTempFormula().append(ch, start, length);
    }

    /**
     * Handles shared formula when reading from Excel
     */
    private String handleSharedFormula(XlsxReadSheetHolder xlsxReadSheetHolder, String formulaText, int sharedIndex) {
        Integer currentRow = xlsxReadSheetHolder.getRowIndex();
        Integer currentCol = xlsxReadSheetHolder.getColumnIndex();

        if (currentRow == null || currentCol == null) {
            return formulaText;
        }

        // If formula text exists then this is the master cell
        if (!StringUtils.isEmpty(formulaText)) {
            xlsxReadSheetHolder
                    .getSharedFormulaMap()
                    .put(sharedIndex, new XlsxReadSheetHolder.SharedFormulaInfo(formulaText, currentRow, currentCol));
            return formulaText;
        } else {
            // No formula text means this is a shared reference cell
            XlsxReadSheetHolder.SharedFormulaInfo masterInfo =
                    xlsxReadSheetHolder.getSharedFormulaMap().get(sharedIndex);

            if (masterInfo == null) {
                log.warn(
                        "Master formula not found for shared index {} at row {}, col {}",
                        sharedIndex,
                        currentRow,
                        currentCol);
                return "";
            }

            return convertSharedFormula(masterInfo, currentRow, currentCol);
        }
    }

    /**
     * Converts shared formula based on row and column offset
     */
    private String convertSharedFormula(
            XlsxReadSheetHolder.SharedFormulaInfo masterInfo, int currentRow, int currentCol) {
        try {
            // Parse the master formula text into tokens
            Ptg[] masterPtgs = FormulaParser.parse(masterInfo.getFormulaText(), null, FormulaType.CELL, 0);

            // Calculate offset from the master cell position
            int rowOffset = currentRow - masterInfo.getFirstRow();
            int colOffset = currentCol - masterInfo.getFirstCol();

            // Use POI SharedFormula to convert with offset
            SharedFormula sharedFormula = new SharedFormula(SpreadsheetVersion.EXCEL2007);
            Ptg[] convertedPtgs = sharedFormula.convertSharedFormulas(masterPtgs, rowOffset, colOffset);

            // Convert tokens back to formula string
            return FormulaRenderer.toFormulaString(null, convertedPtgs);
        } catch (Exception e) {
            // If conversion fails, return the master formula as-is
            // This handles cases like volatile functions with no cell references
            // where the formula should be identical across all cells anyway
            log.warn(
                    "Failed to convert shared formula at row {}, col {}. Using master formula instead.",
                    currentRow,
                    currentCol);
            return masterInfo.getFormulaText();
        }
    }
}

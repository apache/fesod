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

package org.apache.fesod.sheet.write.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

final class StructuredTableFormulaShifter {
    private StructuredTableFormulaShifter() {}

    static Map<XSSFCell, String> shiftFormulas(
            XSSFSheet movedSheet, int firstRow, int lastRow, int number, Map<XSSFTable, CellRangeAddress> ranges) {
        XSSFWorkbook workbook = movedSheet.getWorkbook();
        FormulaShifter shifter = FormulaShifter.createForRowShift(
                workbook.getSheetIndex(movedSheet),
                movedSheet.getSheetName(),
                firstRow,
                lastRow,
                number,
                workbook.getSpreadsheetVersion());
        XSSFEvaluationWorkbook evaluationWorkbook = XSSFEvaluationWorkbook.create(workbook);
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Sheet sheet : workbook) {
            for (XSSFTable table : ((XSSFSheet) sheet).getTables()) {
                if (table.getName() != null) {
                    names.add(table.getName());
                }
            }
        }
        if (names.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<XSSFCell, String> formulas = new IdentityHashMap<>();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() != CellType.FORMULA
                            || !cell.getCellFormula().contains("[")) {
                        continue;
                    }
                    String shifted = shiftFormula((XSSFCell) cell, names, shifter, evaluationWorkbook, ranges);
                    if (shifted != null) {
                        formulas.put((XSSFCell) cell, shifted);
                    }
                }
            }
        }
        return formulas;
    }

    private static String shiftFormula(
            XSSFCell cell,
            Set<String> names,
            FormulaShifter shifter,
            XSSFEvaluationWorkbook workbook,
            Map<XSSFTable, CellRangeAddress> ranges) {
        String formula = cell.getCellFormula();
        String marker = "__fesod_table_reference_";
        while (formula.contains(marker)) {
            marker += "_";
        }
        List<String> references = new ArrayList<>();
        StringBuilder masked = new StringBuilder();
        int copied = 0;
        for (int index = 0; index < formula.length(); index++) {
            char current = formula.charAt(index);
            if (current == '"' || current == '\'') {
                while (++index < formula.length()) {
                    if (formula.charAt(index) == current) {
                        if (index + 1 < formula.length() && formula.charAt(index + 1) == current) {
                            index++;
                        } else {
                            break;
                        }
                    }
                }
            } else if (current == '[') {
                int start = index;
                while (start > copied && isNameCharacter(formula.charAt(start - 1))) {
                    start--;
                }
                boolean qualified = names.contains(formula.substring(start, index));
                boolean local = start == index && insideTable(cell, ranges);
                if (!qualified && !local) {
                    continue;
                }
                int depth = 1;
                int end = index + 1;
                for (; end < formula.length() && depth > 0; end++) {
                    char character = formula.charAt(end);
                    if (character == '\'') {
                        end++;
                    } else if (character == '[') {
                        depth++;
                    } else if (character == ']') {
                        depth--;
                    }
                }
                if (depth != 0) {
                    throw new ExcelGenerateException("Unbalanced structured table reference: " + formula);
                }
                if (!qualified
                        && end < formula.length()
                        && (isNameCharacter(formula.charAt(end)) || formula.charAt(end) == '!')) {
                    continue;
                }
                masked.append(formula, copied, start)
                        .append('"')
                        .append(marker)
                        .append(references.size())
                        .append('"');
                references.add(formula.substring(start, end));
                copied = end;
                index = end - 1;
            }
        }
        if (references.isEmpty()) {
            return null;
        }
        masked.append(formula, copied, formula.length());
        int sheetIndex = cell.getSheet().getWorkbook().getSheetIndex(cell.getSheet());
        // Mask only table expressions so POI can move A1 references without expanding tables into A1 ranges.
        Ptg[] tokens =
                FormulaParser.parse(masked.toString(), workbook, FormulaType.CELL, sheetIndex, cell.getRowIndex());
        if (!shifter.adjustFormula(tokens, sheetIndex)) {
            return formula;
        }
        String shifted = FormulaRenderer.toFormulaString(workbook, tokens);
        for (int index = 0; index < references.size(); index++) {
            shifted = shifted.replace('"' + marker + index + '"', references.get(index));
        }
        return shifted;
    }

    private static boolean isNameCharacter(char character) {
        return Character.isLetterOrDigit(character) || character == '_' || character == '.' || character == '\\';
    }

    private static boolean insideTable(XSSFCell cell, Map<XSSFTable, CellRangeAddress> ranges) {
        for (XSSFTable table : cell.getSheet().getTables()) {
            CellRangeAddress range = ranges.get(table);
            if (range == null) {
                range = CellRangeAddress.valueOf(table.getCTTable().getRef());
            }
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }
}

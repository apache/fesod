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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.metadata.fill.AnalysisCell;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;

/**
 * Tracks template table layout during collection fills and applies it before workbook serialization.
 */
public class StructuredTableWorkbookWriteHandler implements WorkbookWriteHandler {
    private final Map<XSSFTable, CellRangeAddress> ranges = new IdentityHashMap<>();
    private final Set<XSSFTable> growingTables = Collections.newSetFromMap(new IdentityHashMap<>());

    public void prepareFill(
            XSSFSheet sheet,
            List<AnalysisCell> cells,
            Map<AnalysisCell, Integer> lastIndexes,
            FillConfig config,
            int size) {
        growingTables.clear();
        if (cells == null) {
            return;
        }
        validateNames(sheet);
        boolean vertical = config.getDirection() == WriteDirectionEnum.VERTICAL;
        Map<XSSFTable, CellRangeAddress> updated = new IdentityHashMap<>();
        for (XSSFTable table : sheet.getTables()) {
            CellRangeAddress range = currentRange(table).copy();
            int lastDataRow = range.getLastRow() - table.getTotalsRowCount();
            for (AnalysisCell cell : cells) {
                int row = cell.getRowIndex();
                int column = cell.getColumnIndex();
                Integer previous = lastIndexes == null ? null : lastIndexes.get(cell);
                if (vertical) {
                    if (column < range.getFirstColumn() || column > range.getLastColumn()) {
                        continue;
                    }
                    int first = previous == null ? row : previous + 1;
                    int last = first + size - 1;
                    require(
                            last >= first
                                    && last
                                            <= sheet.getWorkbook()
                                                    .getSpreadsheetVersion()
                                                    .getLastRowIndex(),
                            table,
                            "The fill cannot extend beyond the last worksheet row.");
                    if (config.getForceNewRow()) {
                        if (row >= range.getFirstRow() && row <= range.getLastRow()) {
                            require(
                                    row > range.getFirstRow() && row <= lastDataRow,
                                    table,
                                    "Collection placeholders must be in the table's data rows.");
                            growingTables.add(table);
                        }
                    } else {
                        require(
                                first > range.getFirstRow() || last < range.getFirstRow(),
                                table,
                                "A collection fill cannot overwrite a table header.");
                        if (row > range.getFirstRow() && row <= lastDataRow) {
                            require(
                                    table.getTotalsRowCount() == 0 || last <= lastDataRow,
                                    table,
                                    "Use forceNewRow(true) to expand a table with a totals row.");
                            range.setLastRow(Math.max(range.getLastRow(), last));
                        } else {
                            require(
                                    last < range.getFirstRow() || first > range.getLastRow(),
                                    table,
                                    "A collection fill cannot overwrite an unrelated table.");
                        }
                    }
                } else if (row >= range.getFirstRow() && row <= range.getLastRow()) {
                    int first = previous == null ? column : previous + 1;
                    int last = first + size - 1;
                    require(
                            last >= first
                                    && last
                                            <= sheet.getWorkbook()
                                                    .getSpreadsheetVersion()
                                                    .getLastColumnIndex(),
                            table,
                            "The fill cannot extend beyond the last worksheet column.");
                    if ((column >= range.getFirstColumn() && column <= range.getLastColumn())
                            || (first <= range.getLastColumn() && last >= range.getFirstColumn())) {
                        require(
                                row > range.getFirstRow() && row <= lastDataRow,
                                table,
                                "A horizontal fill cannot overwrite table headers or totals.");
                        require(
                                first >= range.getFirstColumn() && last <= range.getLastColumn(),
                                table,
                                "Horizontal expansion of structured tables is not supported.");
                    }
                }
            }
            validate(table, range);
            updated.put(table, range);
        }
        validateOverlaps(sheet, updated);
        ranges.putAll(updated);
    }

    public void shiftRows(XSSFSheet sheet, int afterRow, int lastRow, int number) {
        Map<XSSFTable, CellRangeAddress> updated = new IdentityHashMap<>();
        for (XSSFTable table : sheet.getTables()) {
            CellRangeAddress range = currentRange(table).copy();
            if (range.getFirstRow() > afterRow) {
                range.setFirstRow(range.getFirstRow() + number);
                range.setLastRow(range.getLastRow() + number);
            } else if (range.getLastRow() > afterRow
                    || (range.getLastRow() == afterRow && growingTables.contains(table))) {
                range.setLastRow(range.getLastRow() + number);
            }
            validate(table, range);
            updated.put(table, range);
        }
        validateOverlaps(sheet, updated);
        if (afterRow < lastRow) {
            Map<XSSFCell, String> formulas =
                    StructuredTableFormulaShifter.shiftFormulas(sheet, afterRow + 1, lastRow, number, ranges);
            Map<XSSFCell, String> originals = new IdentityHashMap<>();
            for (Map.Entry<XSSFCell, String> formula : formulas.entrySet()) {
                XSSFCell cell = formula.getKey();
                originals.put(cell, cell.getCTCell().getF().getStringValue());
                cell.getCTCell().getF().setStringValue("0");
            }
            boolean shifted = false;
            try {
                sheet.shiftRows(afterRow + 1, lastRow, number, true, false);
                shifted = true;
            } finally {
                Map<XSSFCell, String> restored = shifted ? formulas : originals;
                for (Map.Entry<XSSFCell, String> formula : restored.entrySet()) {
                    formula.getKey().getCTCell().getF().setStringValue(formula.getValue());
                }
            }
        }
        ranges.putAll(updated);
    }

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder holder) {
        for (Map.Entry<XSSFTable, CellRangeAddress> entry : ranges.entrySet()) {
            XSSFTable table = entry.getKey();
            CellRangeAddress range = entry.getValue();
            validate(table, range);
            table.setCellReferences(new AreaReference(
                    new CellReference(range.getFirstRow(), range.getFirstColumn()),
                    new CellReference(range.getLastRow(), range.getLastColumn()),
                    table.getXSSFSheet().getWorkbook().getSpreadsheetVersion()));
            table.getXSSFSheet().getWorkbook().setForceFormulaRecalculation(true);
        }
    }

    private CellRangeAddress currentRange(XSSFTable table) {
        CellRangeAddress range = ranges.get(table);
        return range == null ? originalRange(table) : range;
    }

    private CellRangeAddress originalRange(XSSFTable table) {
        require(table.getCTTable().getRef() != null, table, "The table has no cell range.");
        CellRangeAddress range = CellRangeAddress.valueOf(table.getCTTable().getRef());
        validate(table, range);
        return range;
    }

    private void validate(XSSFTable table, CellRangeAddress range) {
        require(
                table.getHeaderRowCount() == 1 && table.getTotalsRowCount() >= 0 && table.getTotalsRowCount() <= 1,
                table,
                "Only tables with one header row and at most one totals row are supported.");
        require(
                range.getLastRow() - range.getFirstRow() >= 1 + table.getTotalsRowCount(),
                table,
                "The table must retain a data row between its header and totals.");
        require(
                range.getLastRow()
                        <= table.getXSSFSheet()
                                .getWorkbook()
                                .getSpreadsheetVersion()
                                .getLastRowIndex(),
                table,
                "The table cannot extend beyond the last worksheet row.");
        require(
                range.getLastColumn() - range.getFirstColumn() + 1 == table.getColumnCount()
                        && table.getColumnCount()
                                == table.getCTTable().getTableColumns().sizeOfTableColumnArray(),
                table,
                "The table range and column metadata have different widths.");
    }

    private void validateNames(XSSFSheet sheet) {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> displayNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Sheet workbookSheet : sheet.getWorkbook()) {
            for (XSSFTable table : ((XSSFSheet) workbookSheet).getTables()) {
                require(
                        table.getName() != null
                                && names.add(table.getName())
                                && table.getDisplayName() != null
                                && displayNames.add(table.getDisplayName()),
                        table,
                        "Table names and display names must be unique within the workbook.");
            }
        }
    }

    private void validateOverlaps(XSSFSheet sheet, Map<XSSFTable, CellRangeAddress> updated) {
        List<XSSFTable> tables = sheet.getTables();
        for (int first = 0; first < tables.size(); first++) {
            for (int second = first + 1; second < tables.size(); second++) {
                CellRangeAddress left = updated.get(tables.get(first));
                CellRangeAddress right = updated.get(tables.get(second));
                if (left != null && right != null) {
                    require(!left.intersects(right), tables.get(first), "The fill would overlap another table.");
                }
            }
        }
    }

    private static void require(boolean valid, XSSFTable table, String message) {
        if (!valid) {
            throw new ExcelGenerateException("Structured table '" + table.getName() + "': " + message);
        }
    }
}

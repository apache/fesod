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

package org.apache.fesod.sheet.testkit.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.model.SimpleData;
import org.apache.poi.ss.usermodel.*;

/**
 * Utility class for Excel-specific test operations and inspections.
 * Provides helper methods for inspecting workbook content and structure.
 */
public class ExcelTestHelper {

    /**
     * Gets the total number of cells in a workbook across all sheets.
     */
    public static int getTotalCellCount(Workbook workbook) {
        int totalCells = 0;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (Row row : sheet) {
                totalCells += row.getPhysicalNumberOfCells();
            }
        }
        return totalCells;
    }

    /**
     * Gets the number of used rows in a sheet (rows that have at least one non-empty cell).
     */
    public static int getUsedRowCount(Sheet sheet) {
        int usedRows = 0;
        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() > 0) {
                usedRows++;
            }
        }
        return usedRows;
    }

    /**
     * Checks if a workbook contains a sheet with the specified name.
     */
    public static boolean hasSheetNamed(Workbook workbook, String sheetName) {
        return workbook.getSheet(sheetName) != null;
    }

    /**
     * Gets the value of a cell as a string, handling different cell types.
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double value = cell.getNumericCellValue();
                    // Convert to integer if it's a whole number
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.getStringCellValue();
        }
    }

    /**
     * Gets the number of non-empty rows in a specific sheet.
     */
    public static int getNonEmptyRowCount(Sheet sheet) {
        int count = 0;
        for (Row row : sheet) {
            boolean hasNonEmptyCell = false;
            for (Cell cell : row) {
                if (!getCellValueAsString(cell).isEmpty()) {
                    hasNonEmptyCell = true;
                    break;
                }
            }
            if (hasNonEmptyCell) {
                count++;
            }
        }
        return count;
    }

    /**
     * Reads data from a sheet into a list of SimpleData objects.
     * Assumes the sheet has columns: name, number in that order.
     */
    public static List<SimpleData> readSimpleDataFromSheet(Sheet sheet) {
        List<SimpleData> list = new ArrayList<>();
        boolean firstRow = true;

        for (Row row : sheet) {
            // Skip header row
            if (firstRow) {
                firstRow = false;
                continue;
            }

            SimpleData data = new SimpleData();

            // Get the name (first column)
            Cell nameCell = row.getCell(0);
            if (nameCell != null) {
                data.setName(getCellValueAsString(nameCell));
            }

            // Get the number (second column)
            Cell numberCell = row.getCell(1);
            if (numberCell != null) {
                try {
                    if (numberCell.getCellType() == CellType.NUMERIC) {
                        data.setNumber(numberCell.getNumericCellValue());
                    } else {
                        data.setNumber(Double.parseDouble(getCellValueAsString(numberCell)));
                    }
                } catch (NumberFormatException e) {
                    // If we can't parse as number, set to 0
                    data.setNumber(0.0);
                }
            }

            list.add(data);
        }

        return list;
    }

    /**
     * Compares two workbooks for structural equality (same number of sheets, rows, cells).
     */
    public static boolean hasSameStructure(Workbook wb1, Workbook wb2) {
        if (wb1.getNumberOfSheets() != wb2.getNumberOfSheets()) {
            return false;
        }

        for (int i = 0; i < wb1.getNumberOfSheets(); i++) {
            Sheet sheet1 = wb1.getSheetAt(i);
            Sheet sheet2 = wb2.getSheetAt(i);

            if (getUsedRowCount(sheet1) != getUsedRowCount(sheet2)) {
                return false;
            }

            for (Row row : sheet1) {
                Row correspondingRow = sheet2.getRow(row.getRowNum());
                if (correspondingRow == null && row.getPhysicalNumberOfCells() > 0) {
                    return false;
                }
                if (correspondingRow != null
                        && row.getPhysicalNumberOfCells() != correspondingRow.getPhysicalNumberOfCells()) {
                    return false;
                }
            }
        }

        return true;
    }
}

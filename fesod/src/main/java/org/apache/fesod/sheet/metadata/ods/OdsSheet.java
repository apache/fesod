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

package org.apache.fesod.sheet.metadata.ods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.PaneType;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

/**
 * ODS sheet implementation for writing ODS files.
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class OdsSheet implements Sheet {

    /**
     * workbook
     */
    private OdsWorkbook odsWorkbook;

    /**
     * sheet name
     */
    private String sheetName;

    /**
     * row list
     */
    private List<OdsRow> rowList;

    /**
     * last row index
     */
    private Integer lastRowIndex;

    /**
     * ODF Table (created when flushing)
     */
    private OdfTable odfTable;

    public OdsSheet(OdsWorkbook odsWorkbook, String sheetName) {
        this.odsWorkbook = odsWorkbook;
        this.sheetName = sheetName;
        this.rowList = new ArrayList<>();
        this.lastRowIndex = -1;
    }

    /**
     * Flush all data to the ODF document.
     */
    public void flushToDocument() {
        try {
            odfTable = OdfTable.newTable(odsWorkbook.getOdfDocument(), rowList.size(), getMaxColumnCount());
            odfTable.setTableName(sheetName);

            for (OdsRow odsRow : rowList) {
                int rowIndex = odsRow.getRowIndex();
                OdfTableRow odfRow = odfTable.getRowByIndex(rowIndex);

                Iterator<Cell> cellIterator = odsRow.cellIterator();
                while (cellIterator.hasNext()) {
                    OdsCell odsCell = (OdsCell) cellIterator.next();
                    int colIndex = odsCell.getColumnIndex();
                    OdfTableCell odfCell = odfRow.getCellByIndex(colIndex);

                    writeCellValue(odfCell, odsCell);
                }
            }
        } catch (Exception e) {
            throw new ExcelGenerateException("Failed to flush ODS sheet data", e);
        }
    }

    /**
     * Get the maximum column count across all rows.
     */
    private int getMaxColumnCount() {
        int max = 1;
        for (OdsRow row : rowList) {
            if (row.getLastCellNum() > max) {
                max = row.getLastCellNum();
            }
        }
        return max;
    }

    /**
     * Write cell value to ODF cell.
     */
    private void writeCellValue(OdfTableCell odfCell, OdsCell odsCell) {
        if (odsCell == null || odfCell == null) {
            return;
        }

        switch (odsCell.getCellType()) {
            case STRING:
                String stringValue = odsCell.getStringCellValue();
                if (stringValue != null) {
                    odfCell.setStringValue(stringValue);
                }
                break;
            case NUMERIC:
                if (odsCell.getDateValue() != null) {
                    // Handle date
                    odfCell.setDateValue(java.util.Calendar.getInstance());
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(
                            odsCell.getDateValue().getYear(),
                            odsCell.getDateValue().getMonthValue() - 1,
                            odsCell.getDateValue().getDayOfMonth(),
                            odsCell.getDateValue().getHour(),
                            odsCell.getDateValue().getMinute(),
                            odsCell.getDateValue().getSecond());
                    odfCell.setDateValue(cal);
                } else if (odsCell.getNumberValue() != null) {
                    odfCell.setDoubleValue(odsCell.getNumberValue().doubleValue());
                }
                break;
            case BOOLEAN:
                odfCell.setBooleanValue(odsCell.getBooleanCellValue());
                break;
            case FORMULA:
                String formula = odsCell.getCellFormula();
                if (formula != null) {
                    odfCell.setFormula(formula);
                }
                break;
            case BLANK:
            case _NONE:
            default:
                // Leave cell empty
                break;
        }
    }

    @Override
    public Row createRow(int rownum) {
        lastRowIndex++;
        OdsRow odsRow = new OdsRow(odsWorkbook, this, rownum);
        rowList.add(odsRow);
        return odsRow;
    }

    @Override
    public void removeRow(Row row) {
        rowList.remove(row);
    }

    @Override
    public Row getRow(int rownum) {
        for (OdsRow row : rowList) {
            if (row.getRowIndex() == rownum) {
                return row;
            }
        }
        return null;
    }

    @Override
    public int getPhysicalNumberOfRows() {
        return rowList.size();
    }

    @Override
    public int getFirstRowNum() {
        if (rowList.isEmpty()) {
            return -1;
        }
        return 0;
    }

    @Override
    public int getLastRowNum() {
        return lastRowIndex;
    }

    @Override
    public void setColumnHidden(int columnIndex, boolean hidden) {}

    @Override
    public boolean isColumnHidden(int columnIndex) {
        return false;
    }

    @Override
    public void setRightToLeft(boolean value) {}

    @Override
    public boolean isRightToLeft() {
        return false;
    }

    @Override
    public void setColumnWidth(int columnIndex, int width) {}

    @Override
    public int getColumnWidth(int columnIndex) {
        return 0;
    }

    @Override
    public float getColumnWidthInPixels(int columnIndex) {
        return 0;
    }

    @Override
    public void setDefaultColumnWidth(int width) {}

    @Override
    public int getDefaultColumnWidth() {
        return 0;
    }

    @Override
    public short getDefaultRowHeight() {
        return 0;
    }

    @Override
    public float getDefaultRowHeightInPoints() {
        return 0;
    }

    @Override
    public void setDefaultRowHeight(short height) {}

    @Override
    public void setDefaultRowHeightInPoints(float height) {}

    @Override
    public CellStyle getColumnStyle(int column) {
        return null;
    }

    @Override
    public int addMergedRegion(CellRangeAddress region) {
        return 0;
    }

    @Override
    public int addMergedRegionUnsafe(CellRangeAddress region) {
        return 0;
    }

    @Override
    public void validateMergedRegions() {}

    @Override
    public void setVerticallyCenter(boolean value) {}

    @Override
    public void setHorizontallyCenter(boolean value) {}

    @Override
    public boolean getHorizontallyCenter() {
        return false;
    }

    @Override
    public boolean getVerticallyCenter() {
        return false;
    }

    @Override
    public void removeMergedRegion(int index) {}

    @Override
    public void removeMergedRegions(Collection<Integer> indices) {}

    @Override
    public int getNumMergedRegions() {
        return 0;
    }

    @Override
    public CellRangeAddress getMergedRegion(int index) {
        return null;
    }

    @Override
    public List<CellRangeAddress> getMergedRegions() {
        return null;
    }

    @Override
    public Iterator<Row> rowIterator() {
        return (Iterator<Row>) (Iterator<? extends Row>) rowList.iterator();
    }

    @Override
    public void setForceFormulaRecalculation(boolean value) {}

    @Override
    public boolean getForceFormulaRecalculation() {
        return false;
    }

    @Override
    public void setAutobreaks(boolean value) {}

    @Override
    public void setDisplayGuts(boolean value) {}

    @Override
    public void setDisplayZeros(boolean value) {}

    @Override
    public boolean isDisplayZeros() {
        return false;
    }

    @Override
    public void setFitToPage(boolean value) {}

    @Override
    public void setRowSumsBelow(boolean value) {}

    @Override
    public void setRowSumsRight(boolean value) {}

    @Override
    public boolean getAutobreaks() {
        return false;
    }

    @Override
    public boolean getDisplayGuts() {
        return false;
    }

    @Override
    public boolean getFitToPage() {
        return false;
    }

    @Override
    public boolean getRowSumsBelow() {
        return false;
    }

    @Override
    public boolean getRowSumsRight() {
        return false;
    }

    @Override
    public boolean isPrintGridlines() {
        return false;
    }

    @Override
    public void setPrintGridlines(boolean show) {}

    @Override
    public boolean isPrintRowAndColumnHeadings() {
        return false;
    }

    @Override
    public void setPrintRowAndColumnHeadings(boolean show) {}

    @Override
    public PrintSetup getPrintSetup() {
        return null;
    }

    @Override
    public Header getHeader() {
        return null;
    }

    @Override
    public Footer getFooter() {
        return null;
    }

    @Override
    public void setSelected(boolean value) {}

    @Override
    public double getMargin(short margin) {
        return 0;
    }

    @Override
    public double getMargin(PageMargin pageMargin) {
        return 0;
    }

    @Override
    public void setMargin(short margin, double size) {}

    @Override
    public void setMargin(PageMargin pageMargin, double v) {}

    @Override
    public boolean getProtect() {
        return false;
    }

    @Override
    public void protectSheet(String password) {}

    @Override
    public boolean getScenarioProtect() {
        return false;
    }

    @Override
    public void setZoom(int scale) {}

    @Override
    public short getTopRow() {
        return 0;
    }

    @Override
    public short getLeftCol() {
        return 0;
    }

    @Override
    public void showInPane(int topRow, int leftCol) {}

    @Override
    public void shiftRows(int startRow, int endRow, int n) {}

    @Override
    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {}

    @Override
    public void shiftColumns(int startColumn, int endColumn, int n) {}

    @Override
    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {}

    @Override
    public void createFreezePane(int colSplit, int rowSplit) {}

    @Override
    public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow, int activePane) {}

    @Override
    public void createSplitPane(int i, int i1, int i2, int i3, PaneType paneType) {}

    @Override
    public PaneInformation getPaneInformation() {
        return null;
    }

    @Override
    public void setDisplayGridlines(boolean show) {}

    @Override
    public boolean isDisplayGridlines() {
        return false;
    }

    @Override
    public void setDisplayFormulas(boolean show) {}

    @Override
    public boolean isDisplayFormulas() {
        return false;
    }

    @Override
    public void setDisplayRowColHeadings(boolean show) {}

    @Override
    public boolean isDisplayRowColHeadings() {
        return false;
    }

    @Override
    public void setRowBreak(int row) {}

    @Override
    public boolean isRowBroken(int row) {
        return false;
    }

    @Override
    public void removeRowBreak(int row) {}

    @Override
    public int[] getRowBreaks() {
        return new int[0];
    }

    @Override
    public int[] getColumnBreaks() {
        return new int[0];
    }

    @Override
    public void setColumnBreak(int column) {}

    @Override
    public boolean isColumnBroken(int column) {
        return false;
    }

    @Override
    public void removeColumnBreak(int column) {}

    @Override
    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {}

    @Override
    public void groupColumn(int fromColumn, int toColumn) {}

    @Override
    public void ungroupColumn(int fromColumn, int toColumn) {}

    @Override
    public void groupRow(int fromRow, int toRow) {}

    @Override
    public void ungroupRow(int fromRow, int toRow) {}

    @Override
    public void setRowGroupCollapsed(int row, boolean collapse) {}

    @Override
    public void setDefaultColumnStyle(int column, CellStyle style) {}

    @Override
    public void autoSizeColumn(int column) {}

    @Override
    public void autoSizeColumn(int column, boolean useMergedCells) {}

    @Override
    public Comment getCellComment(CellAddress ref) {
        return null;
    }

    @Override
    public Map<CellAddress, ? extends Comment> getCellComments() {
        return null;
    }

    @Override
    public Drawing<?> getDrawingPatriarch() {
        return null;
    }

    @Override
    public Drawing<?> createDrawingPatriarch() {
        return null;
    }

    @Override
    public Workbook getWorkbook() {
        return odsWorkbook;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public CellRange<? extends Cell> setArrayFormula(String formula, CellRangeAddress range) {
        return null;
    }

    @Override
    public CellRange<? extends Cell> removeArrayFormula(Cell cell) {
        return null;
    }

    @Override
    public DataValidationHelper getDataValidationHelper() {
        return null;
    }

    @Override
    public List<? extends DataValidation> getDataValidations() {
        return null;
    }

    @Override
    public void addValidationData(DataValidation dataValidation) {}

    @Override
    public AutoFilter setAutoFilter(CellRangeAddress range) {
        return null;
    }

    @Override
    public SheetConditionalFormatting getSheetConditionalFormatting() {
        return null;
    }

    @Override
    public CellRangeAddress getRepeatingRows() {
        return null;
    }

    @Override
    public CellRangeAddress getRepeatingColumns() {
        return null;
    }

    @Override
    public void setRepeatingRows(CellRangeAddress rowRangeRef) {}

    @Override
    public void setRepeatingColumns(CellRangeAddress columnRangeRef) {}

    @Override
    public int getColumnOutlineLevel(int columnIndex) {
        return 0;
    }

    @Override
    public Hyperlink getHyperlink(int row, int column) {
        return null;
    }

    @Override
    public Hyperlink getHyperlink(CellAddress addr) {
        return null;
    }

    @Override
    public List<? extends Hyperlink> getHyperlinkList() {
        return null;
    }

    @Override
    public CellAddress getActiveCell() {
        return null;
    }

    @Override
    public void setActiveCell(CellAddress address) {}

    @Override
    public Iterator<Row> iterator() {
        return rowIterator();
    }
}


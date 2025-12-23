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
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * ODS row implementation for writing ODS files.
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class OdsRow implements Row {

    /**
     * cell list
     */
    private final List<OdsCell> cellList;

    /**
     * workbook
     */
    private final OdsWorkbook odsWorkbook;

    /**
     * sheet
     */
    private final OdsSheet odsSheet;

    /**
     * row index
     */
    private Integer rowIndex;

    /**
     * style
     */
    private CellStyle cellStyle;

    public OdsRow(OdsWorkbook odsWorkbook, OdsSheet odsSheet, Integer rowIndex) {
        this.cellList = new ArrayList<>();
        this.odsWorkbook = odsWorkbook;
        this.odsSheet = odsSheet;
        this.rowIndex = rowIndex;
    }

    @Override
    public Cell createCell(int column) {
        OdsCell cell = new OdsCell(odsWorkbook, odsSheet, this, column, null);
        cellList.add(cell);
        return cell;
    }

    @Override
    public Cell createCell(int column, CellType type) {
        OdsCell cell = new OdsCell(odsWorkbook, odsSheet, this, column, type);
        cellList.add(cell);
        return cell;
    }

    @Override
    public void removeCell(Cell cell) {
        cellList.remove(cell);
    }

    @Override
    public void setRowNum(int rowNum) {
        this.rowIndex = rowNum;
    }

    @Override
    public int getRowNum() {
        return rowIndex;
    }

    @Override
    public Cell getCell(int cellnum) {
        for (OdsCell cell : cellList) {
            if (cell.getColumnIndex() == cellnum) {
                return cell;
            }
        }
        return null;
    }

    @Override
    public Cell getCell(int cellnum, MissingCellPolicy policy) {
        return getCell(cellnum);
    }

    @Override
    public short getFirstCellNum() {
        if (CollectionUtils.isEmpty(cellList)) {
            return -1;
        }
        return 0;
    }

    @Override
    public short getLastCellNum() {
        if (CollectionUtils.isEmpty(cellList)) {
            return -1;
        }
        int maxIndex = 0;
        for (OdsCell cell : cellList) {
            if (cell.getColumnIndex() > maxIndex) {
                maxIndex = cell.getColumnIndex();
            }
        }
        return (short) (maxIndex + 1);
    }

    @Override
    public int getPhysicalNumberOfCells() {
        return cellList.size();
    }

    @Override
    public void setHeight(short height) {}

    @Override
    public void setZeroHeight(boolean zHeight) {}

    @Override
    public boolean getZeroHeight() {
        return false;
    }

    @Override
    public void setHeightInPoints(float height) {}

    @Override
    public short getHeight() {
        return 0;
    }

    @Override
    public float getHeightInPoints() {
        return 0;
    }

    @Override
    public boolean isFormatted() {
        return false;
    }

    @Override
    public CellStyle getRowStyle() {
        return cellStyle;
    }

    @Override
    public void setRowStyle(CellStyle style) {
        this.cellStyle = style;
    }

    @Override
    public Iterator<Cell> cellIterator() {
        return (Iterator<Cell>) (Iterator<? extends Cell>) cellList.iterator();
    }

    @Override
    public Sheet getSheet() {
        return odsSheet;
    }

    @Override
    public int getOutlineLevel() {
        return 0;
    }

    @Override
    public void shiftCellsRight(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {}

    @Override
    public void shiftCellsLeft(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {}

    @Override
    public Iterator<Cell> iterator() {
        return cellIterator();
    }
}

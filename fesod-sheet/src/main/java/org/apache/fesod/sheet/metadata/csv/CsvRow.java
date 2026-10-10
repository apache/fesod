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

package org.apache.fesod.sheet.metadata.csv;

import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * csv row
 *
 *
 */
@Getter
@Setter
public class CsvRow implements Row {

    /**
     * cell list, kept sorted by column index by createCell; getCell binary-searches it and CsvSheet#flushData
     * writes it in order
     */
    private final List<CsvCell> cellList;

    /**
     * workbook
     */
    private final CsvWorkbook csvWorkbook;

    /**
     * sheet
     */
    private final CsvSheet csvSheet;

    /**
     * row index
     */
    private Integer rowIndex;

    /**
     * style
     */
    private CellStyle cellStyle;

    public CsvRow(CsvWorkbook csvWorkbook, CsvSheet csvSheet, Integer rowIndex) {
        cellList = Lists.newArrayList();
        this.csvWorkbook = csvWorkbook;
        this.csvSheet = csvSheet;
        this.rowIndex = rowIndex;
    }

    @Override
    public Cell createCell(int column) {
        return createCell(column, null);
    }

    @Override
    public Cell createCell(int column, CellType type) {
        CsvCell cell = new CsvCell(csvWorkbook, csvSheet, this, column, type);
        // Keep the list sorted by column index and replace the cell already in the column, since
        // CsvSheet#flushData writes the cells in list order
        int size = cellList.size();
        if (size == 0 || cellList.get(size - 1).getColumnIndex() < column) {
            cellList.add(cell);
            return cell;
        }
        int position = indexOf(column);
        if (position >= 0) {
            cellList.set(position, cell);
        } else {
            cellList.add(-position - 1, cell);
        }
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
        int position = indexOf(cellnum);
        return position >= 0 ? cellList.get(position) : null;
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
        return (short) (cellList.get(cellList.size() - 1).getColumnIndex() + 1);
    }

    /**
     * Binary search for the cell in the given column.
     *
     * @return the position of the cell, or {@code -(insertion point) - 1} if the column has no cell
     */
    private int indexOf(int column) {
        int low = 0;
        int high = cellList.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midColumn = cellList.get(mid).getColumnIndex();
            if (midColumn < column) {
                low = mid + 1;
            } else if (midColumn > column) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
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
        return csvSheet;
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

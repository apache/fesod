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

package org.apache.fesod.excel.metadata;

import org.apache.fesod.excel.constant.ExcelXmlConstants;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.poi.ss.util.CellReference;

/**
 * Cell extra information.
 *
 *
 */
public class CellExtra extends AbstractCell {
    /**
     * Cell extra type
     */
    private CellExtraTypeEnum type;
    /**
     * Cell extra data
     */
    private String text;
    /**
     * First row index, if this object is an interval
     */
    private Integer firstRowIndex;
    /**
     * Last row index, if this object is an interval
     */
    private Integer lastRowIndex;
    /**
     * First column index, if this object is an interval
     */
    private Integer firstColumnIndex;
    /**
     * Last column index, if this object is an interval
     */
    private Integer lastColumnIndex;

    public CellExtra(CellExtraTypeEnum type, String text, String range) {
        super();
        this.type = type;
        this.text = text;
        String[] ranges = range.split(ExcelXmlConstants.CELL_RANGE_SPLIT);
        CellReference first = new CellReference(ranges[0]);
        CellReference last = first;
        this.firstRowIndex = first.getRow();
        this.firstColumnIndex = (int) first.getCol();
        setRowIndex(this.firstRowIndex);
        setColumnIndex(this.firstColumnIndex);
        if (ranges.length > 1) {
            last = new CellReference(ranges[1]);
        }
        this.lastRowIndex = last.getRow();
        this.lastColumnIndex = (int) last.getCol();
    }

    public CellExtra(CellExtraTypeEnum type, String text, Integer rowIndex, Integer columnIndex) {
        this(type, text, rowIndex, rowIndex, columnIndex, columnIndex);
    }

    public CellExtra(
            CellExtraTypeEnum type,
            String text,
            Integer firstRowIndex,
            Integer lastRowIndex,
            Integer firstColumnIndex,
            Integer lastColumnIndex) {
        super();
        setRowIndex(firstRowIndex);
        setColumnIndex(firstColumnIndex);
        this.type = type;
        this.text = text;
        this.firstRowIndex = firstRowIndex;
        this.firstColumnIndex = firstColumnIndex;
        this.lastRowIndex = lastRowIndex;
        this.lastColumnIndex = lastColumnIndex;
    }

    public CellExtraTypeEnum getType() {
        return type;
    }

    public void setType(CellExtraTypeEnum type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getFirstRowIndex() {
        return firstRowIndex;
    }

    public void setFirstRowIndex(Integer firstRowIndex) {
        this.firstRowIndex = firstRowIndex;
    }

    public Integer getFirstColumnIndex() {
        return firstColumnIndex;
    }

    public void setFirstColumnIndex(Integer firstColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
    }

    public Integer getLastRowIndex() {
        return lastRowIndex;
    }

    public void setLastRowIndex(Integer lastRowIndex) {
        this.lastRowIndex = lastRowIndex;
    }

    public Integer getLastColumnIndex() {
        return lastColumnIndex;
    }

    public void setLastColumnIndex(Integer lastColumnIndex) {
        this.lastColumnIndex = lastColumnIndex;
    }
}

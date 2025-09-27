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

package org.apache.fesod.excel.read.metadata.holder;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.enums.HolderEnum;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.ReadSheet;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ReadSheetHolder extends AbstractReadHolder {

    /**
     * current param
     */
    private ReadSheet readSheet;
    /***
     * parent
     */
    private ReadWorkbookHolder parentReadWorkbookHolder;
    /***
     * sheetNo
     */
    private Integer sheetNo;
    /***
     * sheetName
     */
    private String sheetName;
    /**
     * Gets the total number of rows , data may be inaccurate
     */
    private Integer approximateTotalRowNumber;
    /**
     * Data storage of the current row.
     */
    private Map<Integer, Cell> cellMap;
    /**
     * Data storage of the current extra cell.
     */
    private CellExtra cellExtra;
    /**
     * Index of the current row.
     */
    private Integer rowIndex;
    /**
     * Current CellData
     */
    private ReadCellData<?> tempCellData;
    /**
     * Read the size of the largest head in sheet head data.
     */
    private Integer maxNotEmptyDataHeadSize;

    /**
     * Reading this sheet has ended.
     */
    private Boolean ended;

    public ReadSheetHolder(ReadSheet readSheet, ReadWorkbookHolder readWorkbookHolder) {
        super(readSheet, readWorkbookHolder);
        this.readSheet = readSheet;
        this.parentReadWorkbookHolder = readWorkbookHolder;
        this.sheetNo = readSheet.getSheetNo();
        this.sheetName = readSheet.getSheetName();
        this.cellMap = new LinkedHashMap<>();
        this.rowIndex = -1;
    }

    /**
     * Approximate total number of rows.
     * use: getApproximateTotalRowNumber()
     *
     * @return
     */
    @Deprecated
    public Integer getTotal() {
        return approximateTotalRowNumber;
    }

    @Override
    public HolderEnum holderType() {
        return HolderEnum.SHEET;
    }
}

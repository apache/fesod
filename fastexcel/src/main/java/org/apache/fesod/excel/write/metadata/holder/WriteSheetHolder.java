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

package org.apache.fesod.excel.write.metadata.holder;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.enums.HolderEnum;
import org.apache.fesod.excel.enums.WriteLastRowTypeEnum;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class WriteSheetHolder extends AbstractWriteHolder {
    /**
     * current param
     */
    private WriteSheet writeSheet;
    /***
     * Current poi Sheet.This is only for writing, and there may be no data in version 07 when template data needs to be
     * read.
     * <ul>
     * <li>03:{@link HSSFSheet}</li>
     * <li>07:{@link SXSSFSheet}</li>
     * </ul>
     */
    private Sheet sheet;
    /***
     * Current poi Sheet.Be sure to use and this method when reading template data.
     * <ul>
     * <li>03:{@link HSSFSheet}</li>
     * <li>07:{@link XSSFSheet}</li>
     * </ul>
     */
    private Sheet cachedSheet;
    /***
     * sheetNo
     */
    private Integer sheetNo;
    /***
     * sheetName
     */
    private String sheetName;
    /***
     * poi sheet
     */
    private WriteWorkbookHolder parentWriteWorkbookHolder;
    /***
     * has been initialized table
     */
    private Map<Integer, WriteTableHolder> hasBeenInitializedTable;

    /**
     * last column type
     *
     * @param writeSheet
     * @param writeWorkbookHolder
     */
    private WriteLastRowTypeEnum writeLastRowTypeEnum;

    /**
     * last row index
     */
    private Integer lastRowIndex;

    public WriteSheetHolder(WriteSheet writeSheet, WriteWorkbookHolder writeWorkbookHolder) {
        super(writeSheet, writeWorkbookHolder);

        // init handler
        initHandler(writeSheet, writeWorkbookHolder);

        this.writeSheet = writeSheet;
        if (writeSheet.getSheetNo() == null && StringUtils.isEmpty(writeSheet.getSheetName())) {
            this.sheetNo = 0;
        } else {
            this.sheetNo = writeSheet.getSheetNo();
        }
        this.sheetName = writeSheet.getSheetName();
        this.parentWriteWorkbookHolder = writeWorkbookHolder;
        this.hasBeenInitializedTable = new HashMap<>();
        if (writeWorkbookHolder.getTempTemplateInputStream() != null) {
            writeLastRowTypeEnum = WriteLastRowTypeEnum.TEMPLATE_EMPTY;
        } else {
            writeLastRowTypeEnum = WriteLastRowTypeEnum.COMMON_EMPTY;
        }
        lastRowIndex = 0;
    }

    /**
     * Get the last line of index, you have to make sure that the data is written next
     *
     * @return
     */
    public int getNewRowIndexAndStartDoWrite() {
        // 'getLastRowNum' doesn't matter if it has one or zero, it's zero
        int newRowIndex = 0;
        switch (writeLastRowTypeEnum) {
            case TEMPLATE_EMPTY:
                newRowIndex = Math.max(sheet.getLastRowNum(), cachedSheet.getLastRowNum());
                if (newRowIndex != 0 || cachedSheet.getRow(0) != null) {
                    newRowIndex++;
                }
                break;
            case HAS_DATA:
                newRowIndex = Math.max(sheet.getLastRowNum(), cachedSheet.getLastRowNum());
                newRowIndex++;
                break;
            default:
                break;
        }
        writeLastRowTypeEnum = WriteLastRowTypeEnum.HAS_DATA;
        return newRowIndex;
    }

    @Override
    public HolderEnum holderType() {
        return HolderEnum.SHEET;
    }
}

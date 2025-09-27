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

package org.apache.fesod.excel.write.handler.impl;

import java.lang.reflect.Field;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.fesod.excel.util.FieldUtils;
import org.apache.fesod.excel.write.handler.WorkbookWriteHandler;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

/**
 * Handle the problem of unable to write dimension.
 */
@Slf4j
public class DimensionWorkbookWriteHandler implements WorkbookWriteHandler {

    private static final String XSSF_SHEET_MEMBER_VARIABLE_NAME = "_sh";
    private static final Field XSSF_SHEET_FIELD =
            FieldUtils.getField(SXSSFSheet.class, XSSF_SHEET_MEMBER_VARIABLE_NAME, true);

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        if (writeWorkbookHolder == null || writeWorkbookHolder.getWorkbook() == null) {
            return;
        }
        if (!(writeWorkbookHolder.getWorkbook() instanceof SXSSFWorkbook)) {
            return;
        }

        Map<Integer, WriteSheetHolder> writeSheetHolderMap = writeWorkbookHolder.getHasBeenInitializedSheetIndexMap();
        if (MapUtils.isEmpty(writeSheetHolderMap)) {
            return;
        }
        for (WriteSheetHolder writeSheetHolder : writeSheetHolderMap.values()) {
            if (writeSheetHolder.getSheet() == null || !(writeSheetHolder.getSheet() instanceof SXSSFSheet)) {
                continue;
            }
            SXSSFSheet sxssfSheet = ((SXSSFSheet) writeSheetHolder.getSheet());
            XSSFSheet xssfSheet;
            try {
                xssfSheet = (XSSFSheet) XSSF_SHEET_FIELD.get(sxssfSheet);
            } catch (IllegalAccessException e) {
                log.debug("Can not found _sh.", e);
                continue;
            }
            if (xssfSheet == null) {
                continue;
            }
            CTWorksheet ctWorksheet = xssfSheet.getCTWorksheet();
            if (ctWorksheet == null) {
                continue;
            }
            int headSize = 0;
            if (MapUtils.isNotEmpty(writeSheetHolder.getExcelWriteHeadProperty().getHeadMap())) {
                headSize = writeSheetHolder
                        .getExcelWriteHeadProperty()
                        .getHeadMap()
                        .size();
                if (headSize > 0) {
                    headSize--;
                }
            }
            Integer lastRowIndex = writeSheetHolder.getLastRowIndex();
            if (lastRowIndex == null) {
                lastRowIndex = 0;
            }

            ctWorksheet
                    .getDimension()
                    .setRef("A1:" + CellReference.convertNumToColString(headSize) + (lastRowIndex + 1));
        }
    }
}

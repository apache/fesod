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

package cn.idev.excel.write.handler.impl;

import cn.idev.excel.constant.OrderConstant;
import cn.idev.excel.util.StringUtils;
import cn.idev.excel.write.handler.WorkbookWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Set the order of each worksheet after completing workbook processing.
 */
public class WriteSheetWorkbookWriteHandler implements WorkbookWriteHandler {

    @Override
    public int order() {
        return OrderConstant.SHEET_ORDER;
    }

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        if (writeWorkbookHolder == null || writeWorkbookHolder.getWorkbook() == null) {
            return;
        }
        Map<Integer, WriteSheetHolder> writeSheetHolderMap = writeWorkbookHolder.getHasBeenInitializedSheetIndexMap();
        if (MapUtils.isEmpty(writeSheetHolderMap)) {
            return;
        }
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        // sort by sheetNo.
        List<Integer> sheetNoSortList = writeSheetHolderMap.keySet().stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        int pos = 0;
        for (Integer key : sheetNoSortList) {
            WriteSheetHolder writeSheetHolder = writeSheetHolderMap.get(key);
            if (writeSheetHolder == null
                    || writeSheetHolder.getWriteSheet() == null
                    || writeSheetHolder.getSheetNo() == null
                    || StringUtils.isBlank(writeSheetHolder.getSheetName())) {
                continue;
            }
            // set the order of sheet.
            workbook.setSheetOrder(writeSheetHolder.getSheetName(), pos++);
        }
    }
}

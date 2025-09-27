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

package org.apache.fesod.excel.temp.simple;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.write.handler.CellWriteHandler;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 *
 */
@Slf4j
public class WriteCellHandler implements CellWriteHandler {

    @Override
    public void afterCellDataConverted(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            WriteCellData<?> cellData,
            Cell cell,
            Head head,
            Integer integer,
            Boolean isHead) {

        if (!isHead) {
            CreationHelper createHelper =
                    writeSheetHolder.getSheet().getWorkbook().getCreationHelper();
            CellStyle cellStyle = writeSheetHolder.getSheet().getWorkbook().createCellStyle();
            if (cellStyle != null) {
                DataFormat dataFormat = createHelper.createDataFormat();
                cellStyle.setWrapText(true);
                cellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
                cellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
                cellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd"));
                cell.setCellStyle(cellStyle);
            }
        }
    }
}

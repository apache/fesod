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

package org.apache.fesod.excel.handler;

import java.util.List;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.write.handler.CellWriteHandler;
import org.apache.fesod.excel.write.handler.RowWriteHandler;
import org.apache.fesod.excel.write.handler.SheetWriteHandler;
import org.apache.fesod.excel.write.handler.WorkbookWriteHandler;
import org.apache.fesod.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteTableHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Assertions;

/**
 *
 **/
public class WriteHandler implements WorkbookWriteHandler, SheetWriteHandler, RowWriteHandler, CellWriteHandler {

    private long beforeCellCreate = 0L;
    private long afterCellCreate = 0L;
    private long afterCellDataConverted = 0L;
    private long afterCellDispose = 0L;
    private long beforeRowCreate = 0L;
    private long afterRowCreate = 0L;
    private long afterRowDispose = 0L;
    private long beforeSheetCreate = 0L;
    private long afterSheetCreate = 0L;
    private long afterSheetDispose = 0L;
    private long beforeWorkbookCreate = 0L;
    private long afterWorkbookCreate = 0L;
    private long afterWorkbookDispose = 0L;

    @Override
    public void beforeCellCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Head head,
            Integer columnIndex,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(0L, beforeCellCreate);
            Assertions.assertEquals(0L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(0L, afterCellDispose);
            Assertions.assertEquals(1L, beforeRowCreate);
            Assertions.assertEquals(1L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            beforeCellCreate++;
        }
    }

    @Override
    public void afterCellCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(1L, beforeCellCreate);
            Assertions.assertEquals(0L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(0L, afterCellDispose);
            Assertions.assertEquals(1L, beforeRowCreate);
            Assertions.assertEquals(1L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            afterCellCreate++;
        }
    }

    @Override
    public void afterCellDataConverted(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            WriteCellData<?> cellData,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        Assertions.assertEquals(1L, beforeCellCreate);
        Assertions.assertEquals(1L, afterCellCreate);
        Assertions.assertEquals(0L, afterCellDataConverted);
        Assertions.assertEquals(1, afterCellDispose);
        Assertions.assertEquals(1L, beforeRowCreate);
        Assertions.assertEquals(1L, afterRowCreate);
        Assertions.assertEquals(1L, afterRowDispose);
        Assertions.assertEquals(1L, beforeSheetCreate);
        Assertions.assertEquals(1L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        afterCellDataConverted++;
    }

    @Override
    public void afterCellDispose(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            List<WriteCellData<?>> cellDataList,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(1L, beforeCellCreate);
            Assertions.assertEquals(1L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(0L, afterCellDispose);
            Assertions.assertEquals(1L, beforeRowCreate);
            Assertions.assertEquals(1L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            afterCellDispose++;
        }
    }

    @Override
    public void beforeRowCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Integer rowIndex,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(0L, beforeCellCreate);
            Assertions.assertEquals(0L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(0L, afterCellDispose);
            Assertions.assertEquals(0L, beforeRowCreate);
            Assertions.assertEquals(0L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            beforeRowCreate++;
        }
    }

    @Override
    public void afterRowCreate(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(0L, beforeCellCreate);
            Assertions.assertEquals(0L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(0L, afterCellDispose);
            Assertions.assertEquals(1L, beforeRowCreate);
            Assertions.assertEquals(0L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            afterRowCreate++;
        }
    }

    @Override
    public void afterRowDispose(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            Row row,
            Integer relativeRowIndex,
            Boolean isHead) {
        if (isHead) {
            Assertions.assertEquals(1L, beforeCellCreate);
            Assertions.assertEquals(1L, afterCellCreate);
            Assertions.assertEquals(0L, afterCellDataConverted);
            Assertions.assertEquals(1L, afterCellDispose);
            Assertions.assertEquals(1L, beforeRowCreate);
            Assertions.assertEquals(1L, afterRowCreate);
            Assertions.assertEquals(0L, afterRowDispose);
            Assertions.assertEquals(1L, beforeSheetCreate);
            Assertions.assertEquals(1L, afterSheetCreate);
            Assertions.assertEquals(0L, afterSheetDispose);
            Assertions.assertEquals(1L, beforeWorkbookCreate);
            Assertions.assertEquals(1L, afterWorkbookCreate);
            Assertions.assertEquals(0L, afterWorkbookDispose);
            afterRowDispose++;
        }
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Assertions.assertEquals(0L, beforeCellCreate);
        Assertions.assertEquals(0L, afterCellCreate);
        Assertions.assertEquals(0L, afterCellDataConverted);
        Assertions.assertEquals(0L, afterCellDispose);
        Assertions.assertEquals(0L, beforeRowCreate);
        Assertions.assertEquals(0L, afterRowCreate);
        Assertions.assertEquals(0L, afterRowDispose);
        Assertions.assertEquals(0L, beforeSheetCreate);
        Assertions.assertEquals(0L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        beforeSheetCreate++;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Assertions.assertEquals(0L, beforeCellCreate);
        Assertions.assertEquals(0L, afterCellCreate);
        Assertions.assertEquals(0L, afterCellDataConverted);
        Assertions.assertEquals(0L, afterCellDispose);
        Assertions.assertEquals(0L, beforeRowCreate);
        Assertions.assertEquals(0L, afterRowCreate);
        Assertions.assertEquals(0L, afterRowDispose);
        Assertions.assertEquals(1L, beforeSheetCreate);
        Assertions.assertEquals(0L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        afterSheetCreate++;
    }

    @Override
    public void beforeWorkbookCreate() {
        Assertions.assertEquals(0L, beforeCellCreate);
        Assertions.assertEquals(0L, afterCellCreate);
        Assertions.assertEquals(0L, afterCellDataConverted);
        Assertions.assertEquals(0L, afterCellDispose);
        Assertions.assertEquals(0L, beforeRowCreate);
        Assertions.assertEquals(0L, afterRowCreate);
        Assertions.assertEquals(0L, afterRowDispose);
        Assertions.assertEquals(0L, beforeSheetCreate);
        Assertions.assertEquals(0L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(0L, beforeWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        beforeWorkbookCreate++;
    }

    @Override
    public void afterWorkbookCreate(WriteWorkbookHolder writeWorkbookHolder) {
        Assertions.assertEquals(0L, beforeCellCreate);
        Assertions.assertEquals(0L, afterCellCreate);
        Assertions.assertEquals(0L, afterCellDataConverted);
        Assertions.assertEquals(0L, afterCellDispose);
        Assertions.assertEquals(0L, beforeRowCreate);
        Assertions.assertEquals(0L, afterRowCreate);
        Assertions.assertEquals(0L, afterRowDispose);
        Assertions.assertEquals(0L, beforeSheetCreate);
        Assertions.assertEquals(0L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        afterWorkbookCreate++;
    }

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        Assertions.assertEquals(1L, beforeCellCreate);
        Assertions.assertEquals(1L, afterCellCreate);
        Assertions.assertEquals(1L, afterCellDataConverted);
        Assertions.assertEquals(1L, afterCellDispose);
        Assertions.assertEquals(1L, beforeRowCreate);
        Assertions.assertEquals(1L, afterRowCreate);
        Assertions.assertEquals(1L, afterRowDispose);
        Assertions.assertEquals(1L, beforeSheetCreate);
        Assertions.assertEquals(1L, afterSheetCreate);
        Assertions.assertEquals(1L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        afterWorkbookDispose++;
    }

    @Override
    public void afterSheetDispose(SheetWriteHandlerContext context) {
        Assertions.assertEquals(1L, beforeCellCreate);
        Assertions.assertEquals(1L, afterCellCreate);
        Assertions.assertEquals(1L, afterCellDataConverted);
        Assertions.assertEquals(1L, afterCellDispose);
        Assertions.assertEquals(1L, beforeRowCreate);
        Assertions.assertEquals(1L, afterRowCreate);
        Assertions.assertEquals(1L, afterRowDispose);
        Assertions.assertEquals(1L, beforeSheetCreate);
        Assertions.assertEquals(1L, afterSheetCreate);
        Assertions.assertEquals(0L, afterSheetDispose);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(0L, afterWorkbookDispose);
        afterSheetDispose++;
    }

    public void afterAll() {
        Assertions.assertEquals(1L, beforeCellCreate);
        Assertions.assertEquals(1L, afterCellCreate);
        Assertions.assertEquals(1L, afterCellDataConverted);
        Assertions.assertEquals(1L, afterCellDispose);
        Assertions.assertEquals(1L, beforeRowCreate);
        Assertions.assertEquals(1L, afterRowCreate);
        Assertions.assertEquals(1L, afterRowDispose);
        Assertions.assertEquals(1L, beforeSheetCreate);
        Assertions.assertEquals(1L, afterSheetCreate);
        Assertions.assertEquals(1L, beforeWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookCreate);
        Assertions.assertEquals(1L, afterWorkbookDispose);
        Assertions.assertEquals(1L, afterSheetDispose);
    }
}

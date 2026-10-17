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

package org.apache.fesod.sheet.write.handler.context;

import java.util.Collections;
import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.WriteHandlerUtils;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteTableHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link CellWriteHandlerContextRecycler}.
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class CellWriteHandlerContextRecyclerTest {

    @Mock
    private WriteContext writeContext;

    @Mock
    private WriteWorkbookHolder writeWorkbookHolder;

    @Mock
    private WriteSheetHolder writeSheetHolder;

    @Mock
    private WriteTableHolder writeTableHolder;

    @Mock
    private Row row;

    @Mock
    private Head head;

    private final CellWriteHandlerContextRecycler recycler = new CellWriteHandlerContextRecycler();

    @BeforeEach
    void setUp() {
        Mockito.when(writeContext.writeWorkbookHolder()).thenReturn(writeWorkbookHolder);
        Mockito.when(writeContext.writeSheetHolder()).thenReturn(writeSheetHolder);
        Mockito.when(writeContext.writeTableHolder()).thenReturn(writeTableHolder);
    }

    @AfterEach
    void tearDown() {
        // The suite runs with the per-class test lifecycle, so the shared recycler
        // must not leak an in-flight cell into the next test method.
        recycler.release();
    }

    @Test
    void resetRestoresFreshInstanceState() {
        CellWriteHandlerContext fresh =
                WriteHandlerUtils.createCellWriteHandlerContext(writeContext, row, 7, head, 3, 5, Boolean.FALSE, null);
        CellWriteHandlerContext recycled =
                WriteHandlerUtils.createCellWriteHandlerContext(writeContext, row, 7, head, 3, 5, Boolean.FALSE, null);
        poison(recycled);
        recycled.reset(writeContext, row, 7, head, 3, 5, Boolean.FALSE, null);
        Assertions.assertEquals(fresh, recycled);
    }

    /**
     * Sets every field to a sentinel value so a {@code reset} that misses one field fails the
     * fresh-instance equality assertion — including fields only some write paths ever set.
     */
    private void poison(CellWriteHandlerContext context) {
        WriteCellData<String> cellData = new WriteCellData<>();
        context.setWriteContext(Mockito.mock(WriteContext.class));
        context.setWriteWorkbookHolder(Mockito.mock(WriteWorkbookHolder.class));
        context.setWriteSheetHolder(Mockito.mock(WriteSheetHolder.class));
        context.setWriteTableHolder(Mockito.mock(WriteTableHolder.class));
        context.setRow(Mockito.mock(Row.class));
        context.setRowIndex(99);
        context.setCell(Mockito.mock(Cell.class));
        context.setColumnIndex(99);
        context.setRelativeRowIndex(99);
        context.setHeadData(Mockito.mock(Head.class));
        context.setCellDataList(Collections.singletonList(cellData));
        context.setFirstCellData(cellData);
        context.setHead(Boolean.TRUE);
        context.setExcelContentProperty(Mockito.mock(ExcelContentProperty.class));
        context.setOriginalValue("stale");
        context.setOriginalFieldClass(String.class);
        context.setTargetCellDataType(CellDataTypeEnum.STRING);
        context.setIgnoreFillStyle(Boolean.TRUE);
    }

    @Test
    void renewRecyclesOneInstanceAcrossCells() {
        CellWriteHandlerContext first = renew(0, 0);
        recycler.cellCompleted(first);
        CellWriteHandlerContext second = renew(0, 1);
        Assertions.assertSame(first, second);
        Assertions.assertEquals(1, second.getColumnIndex());
    }

    @Test
    void conditionallySetFieldsDoNotLeakToNextCell() {
        CellWriteHandlerContext first = renew(0, 0);
        // Fields only some write paths set: CSV conversion and multi-variable fill.
        first.setTargetCellDataType(CellDataTypeEnum.STRING);
        first.setIgnoreFillStyle(Boolean.TRUE);
        first.setOriginalValue("v0");
        first.setOriginalFieldClass(String.class);
        first.setCell(Mockito.mock(Cell.class));
        first.setCellDataList(Collections.singletonList(new WriteCellData<>()));
        first.setFirstCellData(new WriteCellData<>());
        recycler.cellCompleted(first);

        CellWriteHandlerContext second = renew(1, 1);
        Assertions.assertSame(first, second);
        Assertions.assertNull(second.getTargetCellDataType());
        Assertions.assertNull(second.getIgnoreFillStyle());
        Assertions.assertNull(second.getOriginalValue());
        Assertions.assertNull(second.getOriginalFieldClass());
        Assertions.assertNull(second.getCell());
        Assertions.assertNull(second.getCellDataList());
        Assertions.assertNull(second.getFirstCellData());
    }

    @Test
    void nestedWriteDoesNotDisturbInFlightContext() {
        CellWriteHandlerContext outer = renew(0, 0);

        CellWriteHandlerContext nested = renew(5, 9);
        Assertions.assertNotSame(outer, nested);
        Assertions.assertEquals(0, outer.getColumnIndex());

        // The nested write completing must not free the outer cell's instance.
        recycler.cellCompleted(nested);
        Assertions.assertNotSame(outer, renew(6, 7));

        recycler.cellCompleted(outer);
        Assertions.assertSame(outer, renew(7, 2));
    }

    @Test
    void releaseDetachesRecycledInstance() {
        CellWriteHandlerContext first = renew(0, 0);
        recycler.cellCompleted(first);
        recycler.release();

        CellWriteHandlerContext second = renew(0, 0);
        Assertions.assertNotSame(first, second);

        // Recycling re-engages for the cells that follow.
        recycler.cellCompleted(second);
        Assertions.assertSame(second, renew(0, 1));
    }

    private CellWriteHandlerContext renew(int rowIndex, int columnIndex) {
        return recycler.renew(writeContext, row, rowIndex, head, columnIndex, 0, Boolean.FALSE, null);
    }
}

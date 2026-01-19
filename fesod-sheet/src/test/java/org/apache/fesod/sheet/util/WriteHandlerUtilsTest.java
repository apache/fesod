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

package org.apache.fesod.sheet.util;

import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.write.handler.chain.CellHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.RowHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.SheetHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.WorkbookHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.AbstractWriteHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteTableHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link WriteHandlerUtilsTest}
 */
@ExtendWith(MockitoExtension.class)
class WriteHandlerUtilsTest {

    @Mock
    private WriteContext writeContext;

    @Mock
    private WriteWorkbookHolder writeWorkbookHolder;

    @Mock
    private WriteSheetHolder writeSheetHolder;

    @Mock
    private WriteTableHolder writeTableHolder;

    @Mock
    private AbstractWriteHolder abstractWriteHolder;

    @Mock
    private WorkbookHandlerExecutionChain workbookChain;

    @Mock
    private SheetHandlerExecutionChain sheetChain;

    @Mock
    private RowHandlerExecutionChain rowChain;

    @Mock
    private CellHandlerExecutionChain cellChain;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(writeContext.writeWorkbookHolder()).thenReturn(writeWorkbookHolder);
        Mockito.lenient().when(writeContext.writeSheetHolder()).thenReturn(writeSheetHolder);
        Mockito.lenient().when(writeContext.writeTableHolder()).thenReturn(writeTableHolder);
        Mockito.lenient().when(writeContext.currentWriteHolder()).thenReturn(abstractWriteHolder);
    }

    @Test
    void test_createWorkbookWriteHandlerContext_success() {
        Assertions.assertDoesNotThrow(() -> {
            WorkbookWriteHandlerContext context = WriteHandlerUtils.createWorkbookWriteHandlerContext(writeContext);
            Assertions.assertNotNull(context);
            Assertions.assertEquals(writeContext, context.getWriteContext());

            Mockito.verify(writeWorkbookHolder).setWorkbookWriteHandlerContext(context);
        });
    }

    @Test
    void test_beforeWorkbookCreate_runOwn_false() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(workbookChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeWorkbookCreate(context));
        Mockito.verify(workbookChain).beforeWorkbookCreate(context);
    }

    @Test
    void test_beforeWorkbookCreate_runOwn_true() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getOwnWorkbookHandlerExecutionChain()).thenReturn(workbookChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeWorkbookCreate(context, true));

        Mockito.verify(workbookChain).beforeWorkbookCreate(context);
        Mockito.verify(abstractWriteHolder, Mockito.never()).getWorkbookHandlerExecutionChain();
    }

    @Test
    void test_beforeWorkbookCreate_chain_null() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeWorkbookCreate(context));
    }

    @Test
    void test_afterWorkbookCreate_runOwn_false() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(workbookChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterWorkbookCreate(context));
        Mockito.verify(workbookChain).afterWorkbookCreate(context);
    }

    @Test
    void test_afterWorkbookCreate_runOwn_true() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getOwnWorkbookHandlerExecutionChain()).thenReturn(workbookChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterWorkbookCreate(context, true));
        Mockito.verify(workbookChain).afterWorkbookCreate(context);
        Mockito.verify(abstractWriteHolder, Mockito.never()).getWorkbookHandlerExecutionChain();
    }

    @Test
    void test_afterWorkbookCreate_chain_null() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterWorkbookCreate(context));
    }

    @Test
    void test_afterWorkbookDispose_execution() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(workbookChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterWorkbookDispose(context));
        Mockito.verify(workbookChain).afterWorkbookDispose(context);
    }

    @Test
    void test_afterWorkbookDispose_chain_null() {
        WorkbookWriteHandlerContext context = Mockito.mock(WorkbookWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getWorkbookHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterWorkbookDispose(context));
    }

    @Test
    void test_createSheetWriteHandlerContext_fields_populated() {
        Assertions.assertDoesNotThrow(() -> {
            SheetWriteHandlerContext context = WriteHandlerUtils.createSheetWriteHandlerContext(writeContext);
            Assertions.assertNotNull(context);
            Assertions.assertEquals(writeSheetHolder, context.getWriteSheetHolder());
        });
    }

    @Test
    void test_beforeSheetCreate_runOwn_false() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(sheetChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeSheetCreate(context));

        Mockito.verify(sheetChain).beforeSheetCreate(context);
    }

    @Test
    void test_beforeSheetCreate_runOwn_true() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getOwnSheetHandlerExecutionChain()).thenReturn(sheetChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeSheetCreate(context, true));
        Mockito.verify(sheetChain).beforeSheetCreate(context);
        Mockito.verify(abstractWriteHolder, Mockito.never()).getSheetHandlerExecutionChain();
    }

    @Test
    void test_beforeSheetCreate_chain_null() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeSheetCreate(context));
    }

    @Test
    void test_afterSheetCreate_runOwn_false() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(sheetChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterSheetCreate(context));
        Mockito.verify(sheetChain).afterSheetCreate(context);
    }

    @Test
    void test_afterSheetCreate_runOwn_true() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getOwnSheetHandlerExecutionChain()).thenReturn(sheetChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterSheetCreate(context, true));
        Mockito.verify(sheetChain).afterSheetCreate(context);
        Mockito.verify(abstractWriteHolder, Mockito.never()).getSheetHandlerExecutionChain();
    }

    @Test
    void test_afterSheetCreate_chain_null() {
        SheetWriteHandlerContext context = Mockito.mock(SheetWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterSheetCreate(context));
    }

    @Test
    void test_afterSheetDispose_execution() {
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(sheetChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterSheetDispose(writeContext));
        Mockito.verify(sheetChain).afterSheetDispose(ArgumentMatchers.any(SheetWriteHandlerContext.class));
    }

    @Test
    void test_afterSheetDispose_chain_null() {
        Mockito.when(abstractWriteHolder.getSheetHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterSheetDispose(writeContext));
    }

    @Test
    void test_createRowWriteHandlerContext_args_mapping() {
        Assertions.assertDoesNotThrow(() -> {
            RowWriteHandlerContext context = WriteHandlerUtils.createRowWriteHandlerContext(writeContext, 10, 5, true);
            Assertions.assertEquals(10, context.getRowIndex());
            Assertions.assertEquals(5, context.getRelativeRowIndex());
            Assertions.assertTrue(context.getHead());
        });
    }

    @Test
    void test_beforeRowCreate_execution() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(rowChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeRowCreate(context));
        Mockito.verify(rowChain).beforeRowCreate(context);
    }

    @Test
    void test_beforeRowCreate_chain_null() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeRowCreate(context));
    }

    @Test
    void test_afterRowCreate_execution() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(rowChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterRowCreate(context));
        Mockito.verify(rowChain).afterRowCreate(context);
    }

    @Test
    void test_afterRowCreate_chain_null() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterRowCreate(context));
    }

    @Test
    void test_afterRowDispose_execution() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(rowChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterRowDispose(context));
        Mockito.verify(rowChain).afterRowDispose(context);
    }

    @Test
    void test_afterRowDispose_chain_null() {
        RowWriteHandlerContext context = Mockito.mock(RowWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getRowHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterRowDispose(context));
    }

    @Test
    void test_createCellWriteHandlerContext_full_args() {
        Row row = Mockito.mock(Row.class);
        Head head = Mockito.mock(Head.class);
        ExcelContentProperty property = Mockito.mock(ExcelContentProperty.class);

        Assertions.assertDoesNotThrow(() -> {
            CellWriteHandlerContext context =
                    WriteHandlerUtils.createCellWriteHandlerContext(writeContext, row, 1, head, 2, 0, false, property);

            Assertions.assertEquals(row, context.getRow());
            Assertions.assertEquals(1, context.getRowIndex());
            Assertions.assertEquals(head, context.getHeadData());
            Assertions.assertEquals(2, context.getColumnIndex());
            Assertions.assertEquals(property, context.getExcelContentProperty());
        });
    }

    @Test
    void test_beforeCellCreate_execution() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(cellChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeCellCreate(context));
        Mockito.verify(cellChain).beforeCellCreate(context);
    }

    @Test
    void test_beforeCellCreate_chain_null() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.beforeCellCreate(context));
    }

    @Test
    void test_afterCellCreate_execution() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(cellChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellCreate(context));
        Mockito.verify(cellChain).afterCellCreate(context);
    }

    @Test
    void test_afterCellCreate_chain_null() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellCreate(context));
    }

    @Test
    void test_afterCellDataConverted_execution() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(cellChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellDataConverted(context));
        Mockito.verify(cellChain).afterCellDataConverted(context);
    }

    @Test
    void test_afterCellDataConverted_chain_null() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellDataConverted(context));
    }

    @Test
    void test_afterCellDispose_execution() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(cellChain);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellDispose(context));
        Mockito.verify(cellChain).afterCellDispose(context);
    }

    @Test
    void test_afterCellDispose_chain_null() {
        CellWriteHandlerContext context = Mockito.mock(CellWriteHandlerContext.class);
        Mockito.when(context.getWriteContext()).thenReturn(writeContext);
        Mockito.when(abstractWriteHolder.getCellHandlerExecutionChain()).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> WriteHandlerUtils.afterCellDispose(context));
    }
}

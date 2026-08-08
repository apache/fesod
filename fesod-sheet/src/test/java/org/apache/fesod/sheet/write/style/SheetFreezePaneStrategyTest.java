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

package org.apache.fesod.sheet.write.style;

import org.apache.fesod.sheet.metadata.property.SheetFreezePaneProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Tag(Tags.UNIT)
class SheetFreezePaneStrategyTest {

    @Test
    void constructor_throws_whenColSplitNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SheetFreezePaneStrategy(-1, 0, 0, 0));
    }

    @Test
    void constructor_throws_whenRowSplitNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SheetFreezePaneStrategy(0, -1, 0, 0));
    }

    @Test
    void constructor_throws_whenLeftmostColumnNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SheetFreezePaneStrategy(0, 0, -1, 0));
    }

    @Test
    void constructor_throws_whenTopRowNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SheetFreezePaneStrategy(0, 0, 0, -1));
    }

    @Test
    void constructor_acceptsAllZeros() {
        Assertions.assertDoesNotThrow(() -> new SheetFreezePaneStrategy(0, 0, 0, 0));
        Assertions.assertDoesNotThrow(() -> new SheetFreezePaneStrategy(0, 0));
    }

    @Test
    void constructor_acceptsPositiveValues() {
        Assertions.assertDoesNotThrow(() -> new SheetFreezePaneStrategy(2, 3, 4, 5));
    }

    @Test
    void constructor_fromProperty_shouldNotThrowException() {
        SheetFreezePaneProperty property = new SheetFreezePaneProperty();
        property.setColSplit(1);
        property.setRowSplit(2);
        property.setLeftmostColumn(3);
        property.setTopRow(4);

        Assertions.assertDoesNotThrow(() -> new SheetFreezePaneStrategy(property));
    }

    @Test
    void afterSheetCreate_createsFreezePaneWithCorrectParams() {
        SheetFreezePaneStrategy strategy = new SheetFreezePaneStrategy(2, 3, 4, 5);

        WriteWorkbookHolder workbookHolder = Mockito.mock(WriteWorkbookHolder.class);
        WriteSheetHolder sheetHolder = Mockito.mock(WriteSheetHolder.class);
        Sheet sheet = Mockito.mock(Sheet.class);
        Mockito.when(sheetHolder.getSheet()).thenReturn(sheet);

        strategy.afterSheetCreate(workbookHolder, sheetHolder);

        Mockito.verify(sheet).createFreezePane(2, 3, 4, 5);
    }

    @Test
    void afterSheetCreate_withZeroValues() {
        SheetFreezePaneStrategy strategy = new SheetFreezePaneStrategy(0, 0);

        WriteWorkbookHolder workbookHolder = Mockito.mock(WriteWorkbookHolder.class);
        WriteSheetHolder sheetHolder = Mockito.mock(WriteSheetHolder.class);
        Sheet sheet = Mockito.mock(Sheet.class);
        Mockito.when(sheetHolder.getSheet()).thenReturn(sheet);

        strategy.afterSheetCreate(workbookHolder, sheetHolder);

        Mockito.verify(sheet).createFreezePane(0, 0, 0, 0);
    }

    @Test
    void afterSheetCreate_delegatesToSheetNotWorkbook() {
        SheetFreezePaneStrategy strategy = new SheetFreezePaneStrategy(1, 1, 1, 1);

        WriteWorkbookHolder workbookHolder = Mockito.mock(WriteWorkbookHolder.class);
        WriteSheetHolder sheetHolder = Mockito.mock(WriteSheetHolder.class);
        Sheet sheet = Mockito.mock(Sheet.class);
        Mockito.when(sheetHolder.getSheet()).thenReturn(sheet);

        strategy.afterSheetCreate(workbookHolder, sheetHolder);

        Mockito.verify(sheet).createFreezePane(1, 1, 1, 1);
        Mockito.verifyNoInteractions(workbookHolder);
    }
}

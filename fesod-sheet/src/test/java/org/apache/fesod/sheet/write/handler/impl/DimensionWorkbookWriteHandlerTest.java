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

package org.apache.fesod.sheet.write.handler.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;

class DimensionWorkbookWriteHandlerTest {

    @Test
    void afterWorkbookDispose_shouldNotThrowNPE_whenMapContainsNullValue() {
        // Given
        DimensionWorkbookWriteHandler handler = new DimensionWorkbookWriteHandler();
        WriteWorkbookHolder writeWorkbookHolder = mock(WriteWorkbookHolder.class);
        SXSSFWorkbook workbook = mock(SXSSFWorkbook.class);

        Map<Integer, WriteSheetHolder> sheetHolderMap = new HashMap<>();
        sheetHolderMap.put(0, null); // null entry that caused NPE

        when(writeWorkbookHolder.getWorkbook()).thenReturn(workbook);
        when(writeWorkbookHolder.getHasBeenInitializedSheetIndexMap()).thenReturn(sheetHolderMap);

        // When & Then
        assertDoesNotThrow(() -> handler.afterWorkbookDispose(writeWorkbookHolder));
    }
}

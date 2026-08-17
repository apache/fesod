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

package org.apache.fesod.sheet.write.metadata.holder;

import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WriteHolderHashCodeTest {

    @Test
    void workbookHolderHashCodeShouldNotRecurseThroughWorkbookHandlerContext() {
        WriteWorkbookHolder workbookHolder = new WriteWorkbookHolder(new WriteWorkbook());
        workbookHolder.setWorkbookWriteHandlerContext(new WorkbookWriteHandlerContext(null, workbookHolder));

        Assertions.assertDoesNotThrow(workbookHolder::hashCode);
    }

    @Test
    void writeHolderHashCodeShouldNotRecurseThroughParentAndInitializedHolderReferences() {
        WriteWorkbookHolder workbookHolder = new WriteWorkbookHolder(new WriteWorkbook());
        WriteSheetHolder sheetHolder = new WriteSheetHolder(writeSheet(), workbookHolder);
        WriteTableHolder tableHolder = new WriteTableHolder(writeTable(), sheetHolder);

        workbookHolder.getHasBeenInitializedSheetIndexMap().put(sheetHolder.getSheetNo(), sheetHolder);
        workbookHolder.getHasBeenInitializedSheetNameMap().put(sheetHolder.getSheetName(), sheetHolder);
        sheetHolder.getHasBeenInitializedTable().put(tableHolder.getTableNo(), tableHolder);

        Assertions.assertDoesNotThrow(workbookHolder::hashCode);
        Assertions.assertDoesNotThrow(sheetHolder::hashCode);
        Assertions.assertDoesNotThrow(tableHolder::hashCode);
    }

    private static WriteSheet writeSheet() {
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetNo(0);
        writeSheet.setSheetName("Sheet1");
        return writeSheet;
    }

    private static WriteTable writeTable() {
        WriteTable writeTable = new WriteTable();
        writeTable.setTableNo(0);
        return writeTable;
    }
}

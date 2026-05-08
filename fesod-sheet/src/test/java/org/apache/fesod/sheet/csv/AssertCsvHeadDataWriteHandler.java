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

package org.apache.fesod.sheet.csv;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;

@RequiredArgsConstructor
public class AssertCsvHeadDataWriteHandler implements WorkbookWriteHandler {

    private final List<List<String>> head;
    private final List<List<String>> data;

    @Override
    public void afterWorkbookDispose(WorkbookWriteHandlerContext context) {
        WriteWorkbookHolder workbook = context.getWriteWorkbookHolder();

        Sheet sheet = workbook.getWorkbook().getSheetAt(0);

        Row headRow = sheet.getRow(0);
        for (int i = 0; i < head.size(); i++) {
            Cell cell = headRow.getCell(i);

            Assertions.assertEquals(head.get(i).get(0), cell.getStringCellValue());
        }

        for (int i = 0; i < data.size(); i++) {
            Row dataRow = sheet.getRow(i + 1);

            for (int j = 0; j < data.get(i).size(); j++) {
                Cell cell = dataRow.getCell(j);

                Assertions.assertEquals(data.get(i).get(j), cell.getStringCellValue());
            }
        }
    }
}

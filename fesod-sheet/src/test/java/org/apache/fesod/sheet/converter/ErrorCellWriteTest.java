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

package org.apache.fesod.sheet.converter;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.converters.string.StringErrorConverter;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The registered {@code StringErrorConverter} writes an ERROR-typed cell data, and the write executor must be able to
 * store it: a registered converter whose output type the executor rejects makes the explicit converter unusable for
 * writing.
 */
@Tag(Tags.WRITE)
public class ErrorCellWriteTest extends AbstractExcelTest {

    public static class ErrorModel {
        @ExcelProperty(value = "err", converter = StringErrorConverter.class)
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Test
    public void stringErrorConverterWritesAnErrorCell() throws Exception {
        File file = createTempFile("errorCellWrite", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        ErrorModel model = new ErrorModel();
        model.setCode("#DIV/0!");

        FesodSheet.write(file, ErrorModel.class).sheet().doWrite(Collections.singletonList(model));

        try (Workbook workbook = WorkbookFactory.create(file)) {
            org.apache.poi.ss.usermodel.Cell cell =
                    workbook.getSheetAt(0).getRow(1).getCell(0);
            Assertions.assertEquals(CellType.ERROR, cell.getCellType(), "the cell must be an error cell");
            Assertions.assertEquals(
                    "#DIV/0!",
                    org.apache.poi.ss.formula.eval.ErrorEval.getText(cell.getErrorCellValue()),
                    "the error code round-trips");
        }
    }

    @Test
    public void plainStringFieldStillWrites() throws Exception {
        File file = createTempFile("plainStringWrite", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);

        List<List<String>> head = new java.util.ArrayList<>();
        head.add(Collections.singletonList("plain"));
        List<List<Object>> data = new java.util.ArrayList<>();
        data.add(Collections.singletonList("v"));
        FesodSheet.write(file).head(head).sheet().doWrite(data);

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Assertions.assertEquals(
                    "v", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
        }
    }
}

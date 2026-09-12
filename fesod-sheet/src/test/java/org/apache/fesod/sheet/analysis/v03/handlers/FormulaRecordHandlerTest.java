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

package org.apache.fesod.sheet.analysis.v03.handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.ReadDefaultReturnEnum;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.READ)
class FormulaRecordHandlerTest extends AbstractExcelTest {

    private static final String[] FORMULAS = {"1/0", "NA()", "SQRT(-1)", "\"x\"+1", "#NULL!", "#REF!", "#NAME?"};
    private static final FormulaError[] ERRORS = {
        FormulaError.DIV0,
        FormulaError.NA,
        FormulaError.NUM,
        FormulaError.VALUE,
        FormulaError.NULL,
        FormulaError.REF,
        FormulaError.NAME
    };

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readCachedFormulaErrorsInStringMode(ExcelFormat format) throws IOException {
        assertCachedFormulaErrors(format, ReadDefaultReturnEnum.STRING);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readCachedFormulaErrorsInActualDataMode(ExcelFormat format) throws IOException {
        assertCachedFormulaErrors(format, ReadDefaultReturnEnum.ACTUAL_DATA);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readCachedFormulaErrorsInCellDataMode(ExcelFormat format) throws IOException {
        assertCachedFormulaErrors(format, ReadDefaultReturnEnum.READ_CELL_DATA);
    }

    private void assertCachedFormulaErrors(ExcelFormat format, ReadDefaultReturnEnum mode) throws IOException {
        File file = writeCachedFormulaErrors(format);
        List<Map<Integer, Object>> rows = FesodSheet.read(file)
                .headRowNumber(0)
                .readDefaultReturn(mode)
                .sheet(0)
                .doReadSync();
        Assertions.assertEquals(1, rows.size());
        Map<Integer, Object> row = rows.get(0);
        Assertions.assertEquals(ERRORS.length, row.size());
        Assertions.assertAll(IntStream.range(0, ERRORS.length).mapToObj(column -> () -> {
            Object value = row.get(column);
            if (mode == ReadDefaultReturnEnum.READ_CELL_DATA) {
                ReadCellData<?> cellData = (ReadCellData<?>) value;
                if (format == ExcelFormat.XLS) {
                    Assertions.assertEquals(CellDataTypeEnum.ERROR, cellData.getType());
                }
                Assertions.assertEquals(
                        FORMULAS[column], cellData.getFormulaData().getFormulaValue());
                value = cellData.getStringValue();
            }
            Assertions.assertEquals(ERRORS[column].getString(), value, FORMULAS[column]);
        }));
    }

    private File writeCachedFormulaErrors(ExcelFormat format) throws IOException {
        File file = createTempFile(format);
        try (Workbook workbook = format == ExcelFormat.XLS ? new HSSFWorkbook() : new XSSFWorkbook();
                OutputStream out = Files.newOutputStream(file.toPath())) {
            Row row = workbook.createSheet("formulas").createRow(0);
            for (int column = 0; column < FORMULAS.length; column++) {
                row.createCell(column).setCellFormula(FORMULAS[column]);
            }
            // Store evaluated formula results, rather than writing literal error cells.
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            for (int column = 0; column < ERRORS.length; column++) {
                Cell cell = row.getCell(column);
                Assertions.assertEquals(CellType.FORMULA, cell.getCellType());
                Assertions.assertEquals(CellType.ERROR, cell.getCachedFormulaResultType());
                Assertions.assertEquals(ERRORS[column].getCode(), cell.getErrorCellValue());
            }
            workbook.write(out);
        }
        return file;
    }
}

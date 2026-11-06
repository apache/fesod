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

package org.apache.fesod.sheet.write.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The default XLSX write handler set includes {@code EscapeHexCellWriteHandler}, so literal {@code _xHHHH_}
 * sequences survive a round trip wherever text reaches a cell: data, head and multi-variable fill cells alike.
 * POI's reader decodes {@code _xHHHH_} to the character it names, so an unescaped literal comes back corrupted.
 */
@Tag(Tags.ROUND_TRIP)
class EscapeHexHeadAndFillWriteTest extends AbstractExcelTest {

    @Test
    void headCellRoundTripsLiteralEscapeSequences() throws Exception {
        File file = createTempFile("escape-hex-head", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);

        List<List<String>> head = new ArrayList<>();
        head.add(Collections.singletonList("_x0041_ col"));
        List<List<Object>> data = new ArrayList<>();
        data.add(Collections.singletonList("v"));
        FesodSheet.write(file).excelType(ExcelTypeEnum.XLSX).head(head).sheet().doWrite(data);

        Assertions.assertEquals("_x0041_ col", readCellValue(file, 0, 0));
    }

    @Test
    void fillMultiVariableCellRoundTripsLiteralEscapeSequences() throws Exception {
        File template = createTemplateWithPlaceholder();
        File file = createTempFile("escape-hex-fill", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);

        Map<String, Object> map = new HashMap<>();
        map.put("a", "_x0041_ x");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.XLSX)
                .withTemplate(template)
                .sheet()
                .doFill(map);

        Assertions.assertEquals("p _x0041_ x s", readCellValue(file, 0, 0));
    }

    private File createTemplateWithPlaceholder() throws Exception {
        File template =
                createTempFile("escape-hex-fill-template", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("S").createRow(0).createCell(0).setCellValue("p {a} s");
            try (FileOutputStream out = new FileOutputStream(template)) {
                workbook.write(out);
            }
        }
        return template;
    }

    private String readCellValue(File file, int rowIdx, int colIdx) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            return workbook.getSheetAt(0).getRow(rowIdx).getCell(colIdx).getStringCellValue();
        }
    }
}

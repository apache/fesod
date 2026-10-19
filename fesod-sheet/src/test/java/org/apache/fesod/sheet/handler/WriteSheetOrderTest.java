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

package org.apache.fesod.sheet.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Writing must only reorder sheets it wrote itself. A template fill that touches a subset of the tabs keeps the
 * template's tab order; writing every sheet still gets them sorted by sheet number.
 */
@Tag(Tags.WRITE)
public class WriteSheetOrderTest extends AbstractExcelTest {

    @Test
    public void fillingASingleTemplateSheetKeepsTheTemplateTabOrder() throws Exception {
        File template = createTemplate("T0", "{a}");
        File file = createTempFile("fillSubset", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);

        Map<String, Object> map = new HashMap<>();
        map.put("a", "filled");
        FesodSheet.write(file).withTemplate(template).sheet("T1").doFill(map);

        Assertions.assertEquals(
                Arrays.asList("T0", "T1"), readSheetNames(file), "the untouched template sheet keeps its place");
    }

    @Test
    public void writingEverySheetStillSortsBySheetNo() throws Exception {
        File file = createTempFile("writeAllSort", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);

        ExcelWriter writer = FesodSheet.write(file).build();
        List<List<Object>> second = new ArrayList<>();
        second.add(Arrays.asList("b0", "b1"));
        writer.write(second, FesodSheet.writerSheet(1, "second").build());
        List<List<Object>> first = new ArrayList<>();
        first.add(Arrays.asList("a0", "a1"));
        writer.write(first, FesodSheet.writerSheet(0, "first").build());
        writer.finish();

        Assertions.assertEquals(
                Arrays.asList("first", "second"),
                readSheetNames(file),
                "sheets written out of order are still sorted by sheet number");
    }

    private File createTemplate(String firstSheetCell, String secondSheetCell) throws Exception {
        File template = createTempFile("sheetOrderTemplate", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("T0").createRow(0).createCell(0).setCellValue(firstSheetCell);
            workbook.createSheet("T1").createRow(0).createCell(0).setCellValue(secondSheetCell);
            try (FileOutputStream out = new FileOutputStream(template)) {
                workbook.write(out);
            }
        }
        return template;
    }

    private List<String> readSheetNames(File file) throws Exception {
        List<String> names = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                names.add(workbook.getSheetName(i));
            }
        }
        return names;
    }
}

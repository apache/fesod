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

package org.apache.fesod.sheet.style;

import java.io.File;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class FillAnnotationDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("fillAnnotation", format);
        File fileTemplate = readFile("fill" + File.separator + "annotation" + format.getExtension());
        readAndWriteImpl(file, fileTemplate);
    }

    private void readAndWriteImpl(File file, File fileTemplate) throws Exception {
        FesodSheet.write()
                .file(file)
                .head(FillAnnotationData.class)
                .withTemplate(fileTemplate)
                .sheet()
                .doFill(TestDataBuilder.fillAnnotationData(
                        5, readFile("converter" + File.separator + "img.jpg").getAbsolutePath()));

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            Row row1 = sheet.getRow(1);
            Assertions.assertEquals(2000, row1.getHeight(), 0);
            Cell cell10 = row1.getCell(0);
            Date date = cell10.getDateCellValue();
            Assertions.assertEquals(DateUtils.parseDate("2020-01-01 01:01:01").getTime(), date.getTime());
            String dataFormatString = cell10.getCellStyle().getDataFormatString();
            Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", dataFormatString);
            Cell cell11 = row1.getCell(1);
            Assertions.assertEquals(99.99, cell11.getNumericCellValue(), 2);
            boolean hasMerge = false;
            for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
                if (mergedRegion.getFirstRow() == 1
                        && mergedRegion.getLastRow() == 1
                        && mergedRegion.getFirstColumn() == 2
                        && mergedRegion.getLastColumn() == 3) {
                    hasMerge = true;
                    break;
                }
            }
            Assertions.assertTrue(hasMerge);
            if (sheet instanceof XSSFSheet) {
                XSSFSheet xssfSheet = (XSSFSheet) sheet;
                List<XSSFShape> shapeList = xssfSheet.getDrawingPatriarch().getShapes();
                XSSFShape shape0 = shapeList.get(0);
                Assertions.assertInstanceOf(XSSFPicture.class, shape0);
                XSSFPicture picture0 = (XSSFPicture) shape0;
                CTMarker ctMarker0 = picture0.getPreferredSize().getFrom();
                Assertions.assertEquals(1, ctMarker0.getRow());
                Assertions.assertEquals(4, ctMarker0.getCol());
            } else {
                HSSFSheet hssfSheet = (HSSFSheet) sheet;
                List<HSSFShape> shapeList = hssfSheet.getDrawingPatriarch().getChildren();
                HSSFShape shape0 = shapeList.get(0);
                Assertions.assertInstanceOf(HSSFPicture.class, shape0);
                HSSFPicture picture0 = (HSSFPicture) shape0;
                HSSFClientAnchor anchor = (HSSFClientAnchor) picture0.getAnchor();
                Assertions.assertEquals(1, anchor.getRow1());
                Assertions.assertEquals(4, anchor.getCol1());
            }
        }
    }
}

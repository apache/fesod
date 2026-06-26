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
import java.io.FileInputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.assertions.RowAssert;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.metadata.style.WriteFont;
import org.apache.fesod.sheet.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class FillStyleDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fill(ExcelFormat format) throws Exception {
        File file = createTempFile("fileStyle", format);
        File template = readFile("fill" + File.separator + "style" + format.getExtension());
        fill(file, template);
        try (ExcelAssertions workbook = ExcelAssertions.assertThat(file)) {
            if (format == ExcelFormat.XLSX) {
                fillXlsxCheck(workbook.sheet(0).row(1));
                fillXlsxCheck(workbook.sheet(0).row(2));
            } else {
                fillXlsCheck(workbook.sheet(0).row(1));
                fillXlsCheck(workbook.sheet(0).row(2));
            }
        }
    }

    private void fillXlsxCheck(RowAssert row) {
        row.cell(0)
                .hasStringValue("Zhang San")
                .hasDataFormat(49)
                .hasBoldFont(true)
                .satisfies(cell -> {
                    XSSFCell xssfCell = (XSSFCell) cell;
                    Assertions.assertEquals(
                            "FF00B050",
                            xssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getARGBHex());
                    Assertions.assertEquals(
                            "FF7030A0",
                            xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
                });

        row.cell(1)
                .satisfies(cell -> Assertions.assertEquals(5.2, cell.getNumericCellValue(), 1))
                .hasDataFormat(0)
                .hasBoldFont(false)
                .satisfies(cell -> {
                    XSSFCell xssfCell = (XSSFCell) cell;
                    Assertions.assertEquals(
                            "FF92D050",
                            xssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getARGBHex());
                    Assertions.assertEquals(
                            "FF4BACC6",
                            xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
                });

        row.cell(2)
                .satisfies(cell -> Assertions.assertEquals(
                        "2020-01-01 01:01:01", DateUtils.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss")))
                .hasDataFormatString("yyyy-MM-dd HH:mm:ss")
                .hasBoldFont(true)
                .satisfies(cell -> {
                    XSSFCell xssfCell = (XSSFCell) cell;
                    Assertions.assertEquals(
                            "FFFFC000",
                            xssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getARGBHex());
                    Assertions.assertEquals(
                            "FFC0504D",
                            xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
                });

        row.cell(3)
                .hasStringValue("Zhang San is 5.2 years old this year")
                .hasDataFormat(0)
                .hasBoldFont(true)
                .satisfies(cell -> {
                    XSSFCell xssfCell = (XSSFCell) cell;
                    Assertions.assertEquals(
                            "FFFF0000",
                            xssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getARGBHex());
                    Assertions.assertEquals(
                            "FFEEECE1",
                            xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
                });

        row.cell(4)
                .hasStringValue("{.name} ignored，Zhang San")
                .hasDataFormat(0)
                .hasBoldFont(false)
                .satisfies(cell -> {
                    XSSFCell xssfCell = (XSSFCell) cell;
                    Assertions.assertEquals(
                            "FFC00000",
                            xssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getARGBHex());
                    Assertions.assertEquals(
                            "FF000000",
                            xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
                });

        row.cell(5).hasStringValue("Empty").hasDataFormat(0).hasBoldFont(false).satisfies(cell -> {
            XSSFCell xssfCell = (XSSFCell) cell;
            Assertions.assertEquals(
                    "FFF79646",
                    xssfCell.getCellStyle().getFillForegroundColorColor().getARGBHex());
            Assertions.assertEquals(
                    "FF8064A2", xssfCell.getCellStyle().getFont().getXSSFColor().getARGBHex());
        });
    }

    private void fillXlsCheck(RowAssert row) {
        row.cell(0)
                .hasStringValue("Zhang San")
                .hasDataFormat(49)
                .hasBoldFont(true)
                .satisfies(cell -> {
                    HSSFCell hssfCell = (HSSFCell) cell;
                    HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
                    Assertions.assertEquals(
                            "0:8080:0",
                            hssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getHexString());
                    Assertions.assertEquals(
                            "8080:0:8080",
                            hssfCell.getCellStyle()
                                    .getFont(workbook)
                                    .getHSSFColor(workbook)
                                    .getHexString());
                });

        row.cell(1)
                .satisfies(cell -> Assertions.assertEquals(5.2, cell.getNumericCellValue(), 1))
                .hasDataFormat(0)
                .hasBoldFont(false)
                .satisfies(cell -> {
                    HSSFCell hssfCell = (HSSFCell) cell;
                    HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
                    Assertions.assertEquals(
                            "9999:CCCC:0",
                            hssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getHexString());
                    Assertions.assertEquals(
                            "0:8080:8080",
                            hssfCell.getCellStyle()
                                    .getFont(workbook)
                                    .getHSSFColor(workbook)
                                    .getHexString());
                });

        row.cell(2)
                .satisfies(cell -> Assertions.assertEquals(
                        "2020-01-01 01:01:01", DateUtils.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss")))
                .hasDataFormatString("yyyy-MM-dd HH:mm:ss")
                .hasBoldFont(true)
                .satisfies(cell -> {
                    HSSFCell hssfCell = (HSSFCell) cell;
                    HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
                    Assertions.assertEquals(
                            "FFFF:CCCC:0",
                            hssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getHexString());
                    Assertions.assertEquals(
                            "8080:0:0",
                            hssfCell.getCellStyle()
                                    .getFont(workbook)
                                    .getHSSFColor(workbook)
                                    .getHexString());
                });

        row.cell(3)
                .hasStringValue("Zhang San is 5.2 years old this year")
                .hasDataFormat(0)
                .hasBoldFont(true)
                .satisfies(cell -> {
                    HSSFCell hssfCell = (HSSFCell) cell;
                    HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
                    Assertions.assertEquals(
                            "FFFF:0:0",
                            hssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getHexString());
                    Assertions.assertEquals(
                            "FFFF:FFFF:9999",
                            hssfCell.getCellStyle()
                                    .getFont(workbook)
                                    .getHSSFColor(workbook)
                                    .getHexString());
                });

        row.cell(4)
                .hasStringValue("{.name} ignored，Zhang San")
                .hasDataFormat(0)
                .hasBoldFont(false)
                .satisfies(cell -> {
                    HSSFCell hssfCell = (HSSFCell) cell;
                    HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
                    Assertions.assertEquals(
                            "9999:3333:0",
                            hssfCell.getCellStyle()
                                    .getFillForegroundColorColor()
                                    .getHexString());
                    Assertions.assertEquals(
                            "3333:3333:3333",
                            hssfCell.getCellStyle()
                                    .getFont(workbook)
                                    .getHSSFColor(workbook)
                                    .getHexString());
                });

        row.cell(5).hasStringValue("Empty").hasDataFormat(0).hasBoldFont(false).satisfies(cell -> {
            HSSFCell hssfCell = (HSSFCell) cell;
            HSSFWorkbook workbook = (HSSFWorkbook) hssfCell.getSheet().getWorkbook();
            Assertions.assertEquals(
                    "9999:3333:0",
                    hssfCell.getCellStyle().getFillForegroundColorColor().getHexString());
            Assertions.assertEquals(
                    "CCCC:9999:FFFF",
                    hssfCell.getCellStyle()
                            .getFont(workbook)
                            .getHSSFColor(workbook)
                            .getHexString());
        });
    }

    private void fill(File file, File template) throws Exception {
        FesodSheet.write(file, FillStyleData.class)
                .withTemplate(template)
                .sheet()
                .doFill(TestDataBuilder.fillStyleData(10));
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fillStyleHandler(ExcelFormat format) throws Exception {
        File file = createTempFile("fileStyleHandler", format);
        File template = readFile("fill" + File.separator + "style" + format.getExtension());
        fillStyleHandler(file, template);
        if (format == ExcelFormat.XLSX) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                fillStyleHandlerXlsxCheck(sheet.getRow(1));
                fillStyleHandlerXlsxCheck(sheet.getRow(2));
            }
        } else {
            try (HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file))) {
                HSSFSheet sheet = workbook.getSheetAt(0);
                fillStyleHandlerXlsCheck(workbook, sheet.getRow(1));
                fillStyleHandlerXlsCheck(workbook, sheet.getRow(2));
            }
        }
    }

    private void fillStyleHandlerXlsxCheck(XSSFRow row) {
        XSSFCell cell0 = row.getCell(0);
        Assertions.assertEquals("Zhang San", cell0.getStringCellValue());
        Assertions.assertEquals(49, cell0.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFFFF00", cell0.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FF808000", cell0.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertTrue(cell0.getCellStyle().getFont().getBold());

        XSSFCell cell1 = row.getCell(1);
        Assertions.assertEquals(5.2, cell1.getNumericCellValue(), 1);
        Assertions.assertEquals(0, cell1.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFF0000", cell1.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FF800000", cell1.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertTrue(cell1.getCellStyle().getFont().getBold());

        XSSFCell cell2 = row.getCell(2);
        Assertions.assertEquals(
                "2020-01-01 01:01:01", DateUtils.format(cell2.getDateCellValue(), "yyyy-MM-dd HH:mm:ss"));
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", cell2.getCellStyle().getDataFormatString());
        Assertions.assertEquals(
                "FF008000", cell2.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FF003300", cell2.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertTrue(cell2.getCellStyle().getFont().getBold());

        XSSFCell cell3 = row.getCell(3);
        Assertions.assertEquals("Zhang San is 5.2 years old this year", cell3.getStringCellValue());
        Assertions.assertEquals(0, cell3.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFF0000", cell3.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FFEEECE1", cell3.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertTrue(cell3.getCellStyle().getFont().getBold());

        XSSFCell cell4 = row.getCell(4);
        Assertions.assertEquals("{.name} ignored，Zhang San", cell4.getStringCellValue());
        Assertions.assertEquals(0, cell4.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFC00000", cell4.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FF000000", cell4.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertFalse(cell4.getCellStyle().getFont().getBold());

        XSSFCell cell5 = row.getCell(5);
        Assertions.assertEquals("Empty", cell5.getStringCellValue());
        Assertions.assertEquals(0, cell5.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFF79646", cell5.getCellStyle().getFillForegroundColorColor().getARGBHex());
        Assertions.assertEquals(
                "FF8064A2", cell5.getCellStyle().getFont().getXSSFColor().getARGBHex());
        Assertions.assertFalse(cell5.getCellStyle().getFont().getBold());
    }

    private void fillStyleHandlerXlsCheck(HSSFWorkbook workbook, HSSFRow row) {
        HSSFCell cell0 = row.getCell(0);
        Assertions.assertEquals("Zhang San", cell0.getStringCellValue());
        Assertions.assertEquals(49, cell0.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFF:FFFF:0",
                cell0.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "8080:8080:0",
                cell0.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertTrue(cell0.getCellStyle().getFont(workbook).getBold());

        HSSFCell cell1 = row.getCell(1);
        Assertions.assertEquals(5.2, cell1.getNumericCellValue(), 1);
        Assertions.assertEquals(0, cell1.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFF:0:0", cell1.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "8080:0:0",
                cell1.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertTrue(cell1.getCellStyle().getFont(workbook).getBold());

        HSSFCell cell2 = row.getCell(2);
        Assertions.assertEquals(
                "2020-01-01 01:01:01", DateUtils.format(cell2.getDateCellValue(), "yyyy-MM-dd HH:mm:ss"));
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", cell2.getCellStyle().getDataFormatString());
        Assertions.assertEquals(
                "0:8080:0", cell2.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "0:3333:0",
                cell2.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertTrue(cell2.getCellStyle().getFont(workbook).getBold());

        HSSFCell cell3 = row.getCell(3);
        Assertions.assertEquals("Zhang San is 5.2 years old this year", cell3.getStringCellValue());
        Assertions.assertEquals(0, cell3.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "FFFF:0:0", cell3.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "FFFF:FFFF:9999",
                cell3.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertTrue(cell3.getCellStyle().getFont(workbook).getBold());

        HSSFCell cell4 = row.getCell(4);
        Assertions.assertEquals("{.name} ignored，Zhang San", cell4.getStringCellValue());
        Assertions.assertEquals(0, cell4.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "9999:3333:0",
                cell4.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "3333:3333:3333",
                cell4.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertFalse(cell4.getCellStyle().getFont(workbook).getBold());

        HSSFCell cell5 = row.getCell(5);
        Assertions.assertEquals("Empty", cell5.getStringCellValue());
        Assertions.assertEquals(0, cell5.getCellStyle().getDataFormat());
        Assertions.assertEquals(
                "9999:3333:0",
                cell5.getCellStyle().getFillForegroundColorColor().getHexString());
        Assertions.assertEquals(
                "CCCC:9999:FFFF",
                cell5.getCellStyle().getFont(workbook).getHSSFColor(workbook).getHexString());
        Assertions.assertFalse(cell5.getCellStyle().getFont(workbook).getBold());
    }

    private void fillStyleHandler(File file, File template) throws Exception {
        FesodSheet.write(file, FillStyleData.class)
                .withTemplate(template)
                .sheet()
                .registerWriteHandler(new AbstractVerticalCellStyleStrategy() {

                    @Override
                    protected WriteCellStyle contentCellStyle(CellWriteHandlerContext context) {
                        WriteCellStyle writeCellStyle = new WriteCellStyle();
                        WriteFont writeFont = new WriteFont();
                        writeCellStyle.setWriteFont(writeFont);
                        writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                        writeFont.setBold(true);
                        if (context.getColumnIndex() == 0) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                            writeFont.setColor(IndexedColors.DARK_YELLOW.getIndex());
                        }
                        if (context.getColumnIndex() == 1) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                            writeFont.setColor(IndexedColors.DARK_RED.getIndex());
                        }
                        if (context.getColumnIndex() == 2) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                            writeFont.setColor(IndexedColors.DARK_GREEN.getIndex());
                        }
                        if (context.getColumnIndex() == 3) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                            writeFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                        }
                        if (context.getColumnIndex() == 4) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                            writeFont.setColor(IndexedColors.DARK_YELLOW.getIndex());
                        }
                        if (context.getColumnIndex() == 5) {
                            writeCellStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
                            writeFont.setColor(IndexedColors.DARK_TEAL.getIndex());
                        }
                        return writeCellStyle;
                    }

                    @Override
                    protected WriteCellStyle headCellStyle(Head head) {
                        return null;
                    }
                })
                .doFill(TestDataBuilder.fillStyleData(10));
    }
}

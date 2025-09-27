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

package org.apache.fesod.excel.temp.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.util.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * 测试poi
 *
 *
 **/
@Slf4j
public class PoiTest {

    @TempDir
    Path tempDir;

    @SneakyThrows
    @Test
    public void lastRowNum() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(new File(file))); ) {
            SXSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            Assertions.assertEquals(-1, xssfSheet.getLastRowNum());
            log.info("一共行数:{}", xssfSheet.getLastRowNum());
            xssfSheet.createRow(10);
            SXSSFRow row = xssfSheet.getRow(10);
            SXSSFCell cell1 = row.createCell(0);
            SXSSFCell cell2 = row.createCell(1);
            cell1.setCellValue(new Date());
            cell2.setCellValue(new Date());
            log.info("dd{}", row.getCell(0).getColumnIndex());
            Date date = row.getCell(1).getDateCellValue();
        }
    }

    @Test
    public void lastRowNumXSSF() throws IOException {

        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
                FileOutputStream fileOutputStream = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {

            log.info("一共:{}个sheet", xssfWorkbook.getNumberOfSheets());
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            log.info("一共行数:{}", xssfSheet.getLastRowNum());
            XSSFRow row = xssfSheet.getRow(1);
            log.info("dd{}", row.getCell(0).getRow().getRowNum());
            log.info("dd{}", xssfSheet.getLastRowNum());

            XSSFCellStyle cellStyle = row.getCell(0).getCellStyle();
            log.info("size1:{}", cellStyle.getFontIndexAsInt());

            XSSFCellStyle cellStyle1 = xssfWorkbook.createCellStyle();
            log.info("size2:{}", cellStyle1.getFontIndexAsInt());

            cellStyle1.cloneStyleFrom(cellStyle);
            log.info("size3:{}", cellStyle1.getFontIndexAsInt());

            log.info("bbb:{}", cellStyle1.getFont().getXSSFColor().getIndex());
            log.info("bbb:{}", cellStyle1.getFont().getXSSFColor().getIndexed());
            XSSFColor myColor =
                    new XSSFColor(cellStyle1.getFont().getXSSFColor().getRGB(), null);
            log.info("bbb:{}", cellStyle1.getFont().getXSSFColor().getRGB());
            log.info("bbb:{}", cellStyle1.getFont().getXSSFColor().getARGBHex());

            log.info("bbb:{}", cellStyle1.getFont().getBold());
            log.info("bbb:{}", cellStyle1.getFont().getFontName());

            XSSFFont xssfFont = xssfWorkbook.createFont();

            xssfFont.setColor(myColor);

            xssfFont.setFontHeightInPoints((short) 50);
            xssfFont.setBold(Boolean.TRUE);
            cellStyle1.setFont(xssfFont);
            cellStyle1.setFillForegroundColor(IndexedColors.PINK.getIndex());

            log.info("aaa:{}", cellStyle1.getFont().getColor());

            row.getCell(1).setCellStyle(cellStyle1);
            row.getCell(1).setCellValue(3334l);

            XSSFCellStyle cellStyle2 = xssfWorkbook.createCellStyle();
            cellStyle2.cloneStyleFrom(cellStyle);
            cellStyle2.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            // cellStyle2.setFont(cellStyle1.getFont());
            row.getCell(2).setCellStyle(cellStyle2);
            row.getCell(2).setCellValue(3334l);
            // log.info("date1:{}",  row.getCell(0).getStringCellValue());
            // log.info("date2:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).getIndex());
            // log.info("date2:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).isRGB());
            // log.info("date4:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).isIndexed());
            // log.info("date3:{}", cellStyle.getFont().getXSSFColor().getRGB());
            // log.info("date4:{}", cellStyle.getFont().getCTFont().getColorArray(0).getRgb());
            xssfWorkbook.write(fileOutputStream);
        }
    }

    @Test
    public void lastRowNumXSSFv22() throws IOException {

        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xls";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xls").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (HSSFWorkbook xssfWorkbook = new HSSFWorkbook(Files.newInputStream(Paths.get(file.toString())));
                FileOutputStream fileOutputStream = new FileOutputStream(new File(file))) {
            log.info("一共:{}个sheet", xssfWorkbook.getNumberOfSheets());
            HSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            log.info("一共行数:{}", xssfSheet.getLastRowNum());
            HSSFRow row = xssfSheet.getRow(1);
            log.info("dd{}", row.getCell(0).getRow().getRowNum());
            log.info("dd{}", xssfSheet.getLastRowNum());

            HSSFCellStyle cellStyle = row.getCell(0).getCellStyle();
            log.info("单元格1的字体:{}", cellStyle.getFontIndexAsInt());

            HSSFCellStyle cellStyle1 = xssfWorkbook.createCellStyle();
            log.info("size2:{}", cellStyle1.getFontIndexAsInt());

            cellStyle1.cloneStyleFrom(cellStyle);
            log.info("单元格2的字体:{}", cellStyle1.getFontIndexAsInt());

            log.info("bbb:{}", cellStyle1.getFont(xssfWorkbook).getColor());

            HSSFFont xssfFont = xssfWorkbook.createFont();

            xssfFont.setColor(cellStyle1.getFont(xssfWorkbook).getColor());
            xssfFont.setFontHeightInPoints((short) 50);
            xssfFont.setBold(Boolean.TRUE);
            cellStyle1.setFont(xssfFont);
            cellStyle1.setFillForegroundColor(IndexedColors.PINK.getIndex());

            log.info("aaa:{}", cellStyle1.getFont(xssfWorkbook).getColor());

            row.getCell(1).setCellStyle(cellStyle1);
            row.getCell(1).setCellValue(3334l);

            HSSFCellStyle cellStyle2 = xssfWorkbook.createCellStyle();
            cellStyle2.cloneStyleFrom(cellStyle);
            cellStyle2.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            // cellStyle2.setFont(cellStyle1.getFont());
            row.getCell(2).setCellStyle(cellStyle2);
            row.getCell(2).setCellValue(3334l);
            // log.info("date1:{}",  row.getCell(0).getStringCellValue());
            // log.info("date2:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).getIndex());
            // log.info("date2:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).isRGB());
            // log.info("date4:{}", ((XSSFColor) cellStyle.getFillForegroundColorColor()).isIndexed());
            // log.info("date3:{}", cellStyle.getFont().getXSSFColor().getRGB());
            // log.info("date4:{}", cellStyle.getFont().getCTFont().getColorArray(0).getRgb());
            xssfWorkbook.write(fileOutputStream);
        }
    }

    @Test
    public void lastRowNum233() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (XSSFWorkbook xx = new XSSFWorkbook(file);
                SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(xx);
                FileOutputStream fileout = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            System.out.println(new File(file).exists());
            Sheet xssfSheet = xssfWorkbook.getXSSFWorkbook().getSheetAt(0);
            Cell cell = xssfSheet.getRow(0).createCell(9);
            cell.setCellValue("testssdf是士大夫否t");
            xssfWorkbook.write(fileout);
        }
    }

    @Test
    public void lastRowNum255() throws IOException, InvalidFormatException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new File(file));
                SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook);
                FileOutputStream fileout = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
            xssfSheet.shiftRows(1, 4, 10, true, true);
            sxssfWorkbook.write(fileout);
        }
    }

    @Test
    public void cp() throws IOException, InvalidFormatException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file));
        SXSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        log.info("一共行数:{}", xssfSheet.getLastRowNum());
        SXSSFRow row = xssfSheet.getRow(0);
        log.info("第一行数据:{}", row);
        xssfSheet.createRow(20);
        log.info("一共行数:{}", xssfSheet.getLastRowNum());
    }

    @Test
    public void lastRowNum233443() throws IOException, InvalidFormatException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new File(file));
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        System.out.println(xssfSheet.getLastRowNum());
        System.out.println(xssfSheet.getRow(0));
    }

    @Test
    public void lastRowNum2333() throws IOException, InvalidFormatException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new File(file));
                SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook);
                FileOutputStream fileout = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
            Cell cell = xssfSheet.getRow(0).createCell(9);
            cell.setCellValue("testssdf是士大夫否t");
            sxssfWorkbook.write(fileout);
        }
    }

    @Test
    public void testread() throws IOException {
        String sourceFile = "src/test/resources/simple/simple07.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file)); ) {
            Sheet xssfSheet = xssfWorkbook.getXSSFWorkbook().getSheetAt(0);
            //
            // Cell cell = xssfSheet.getRow(0).createCell(9);
        }

        String file1 = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file1));

        try (SXSSFWorkbook xssfWorkbook1 = new SXSSFWorkbook(new XSSFWorkbook(file1)); ) {
            Sheet xssfSheet1 = xssfWorkbook1.getXSSFWorkbook().getSheetAt(0);
            // Cell cell1 = xssfSheet1.getRow(0).createCell(9);
        }
    }

    @Test
    public void testreadRead() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        FileUtils.readFileToByteArray(new File(file));
    }

    @Test
    public void lastRowNum2332222() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file));
                FileOutputStream fileout = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            Sheet xssfSheet = xssfWorkbook.getXSSFWorkbook().getSheetAt(0);
            Cell cell = xssfSheet.getRow(0).createCell(9);
            cell.setCellValue("testssdf是士大夫否t");
            xssfWorkbook.write(fileout);
        }
    }

    @Test
    public void lastRowNum23443() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        try (SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file));
                FileOutputStream fileout = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
            xssfWorkbook.write(fileout);
        }
    }

    @Test
    public void lastRowNum2() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file));
        Sheet xssfSheet = xssfWorkbook.getXSSFWorkbook().getSheetAt(0);
        log.info("一共行数:{}", xssfSheet.getPhysicalNumberOfRows());
        log.info("一共行数:{}", xssfSheet.getLastRowNum());
        log.info("一共行数:{}", xssfSheet.getFirstRowNum());
    }

    @Test
    public void lastRowNumXSSF2() throws IOException {
        String sourceFile = "src/test/resources/poi/last_row_number_xssf_date_test.xlsx";
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        Files.copy(Paths.get(sourceFile), Paths.get(file));
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        log.info("一共:{}个sheet", xssfWorkbook.getNumberOfSheets());
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        log.info("一共行数:{}", xssfSheet.getLastRowNum());
        XSSFRow row = xssfSheet.getRow(0);
        log.info("第一行数据:{}", row);
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.idev.excel.test.temp.poi;

import java.io.IOException;
import java.util.Locale;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试poi
 *
 *
 **/
public class PoiFormatTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoiFormatTest.class);

    @Test
    public void lastRowNum() throws IOException {
        String file = "src/test/resources/poi/last_row_number_test.xlsx";
        SXSSFWorkbook xssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(file));
        SXSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        LOGGER.info("一共行数:{}", xssfSheet.getLastRowNum());
        SXSSFRow row = xssfSheet.getRow(0);
        LOGGER.info("第一行数据:{}", row);
        xssfSheet.createRow(20);
        LOGGER.info("一共行数:{}", xssfSheet.getLastRowNum());
    }

    @Test
    public void lastRowNumXSSF() throws IOException {
        String file = "src/test/resources/poi/last_row_number_xssf_test.xlsx";
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
        LOGGER.info("一共:{}个sheet", xssfWorkbook.getNumberOfSheets());
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        LOGGER.info("一共行数:{}", xssfSheet.getLastRowNum());
        XSSFRow row = xssfSheet.getRow(1);
        XSSFCell xssfCell = row.getCell(0);
        DataFormatter d = new DataFormatter(Locale.CHINA);
        LOGGER.info("fo:{}", d.formatCellValue(xssfCell));
    }
}

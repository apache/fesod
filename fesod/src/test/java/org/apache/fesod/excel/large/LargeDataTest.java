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

package org.apache.fesod.excel.large;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class LargeDataTest {

    private static File fileFill07;
    private static File template07;
    private static File fileCsv;
    private static File fileWrite07;
    private static File fileWriteTemp07;
    private static File fileWritePoi07;

    private int i = 0;

    @BeforeAll
    public static void init() {
        fileFill07 = TestFileUtil.createNewFile("largefill07.xlsx");
        fileWrite07 = TestFileUtil.createNewFile("large" + File.separator + "fileWrite07.xlsx");
        fileWriteTemp07 = TestFileUtil.createNewFile("large" + File.separator + "fileWriteTemp07.xlsx");
        fileWritePoi07 = TestFileUtil.createNewFile("large" + File.separator + "fileWritePoi07.xlsx");
        template07 = TestFileUtil.readFile("large" + File.separator + "fill.xlsx");
        fileCsv = TestFileUtil.createNewFile("largefileCsv.csv");
    }

    @Test
    public void t01Read() throws Exception {
        long start = System.currentTimeMillis();
        FastExcel.read(
                        TestFileUtil.getPath() + "large" + File.separator + "large07.xlsx",
                        LargeData.class,
                        new LargeDataListener())
                .headRowNumber(2)
                .sheet()
                .doRead();
        log.info("Large data total time spent:{}", System.currentTimeMillis() - start);
    }

    @Test
    public void t02Fill() {
        try (ExcelWriter excelWriter =
                FastExcel.write(fileFill07).withTemplate(template07).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            for (int j = 0; j < 5000; j++) {
                excelWriter.fill(data(), writeSheet);
                log.info("{} fill success.", j);
            }
        }
    }

    @Test
    public void t03ReadAndWriteCsv() {
        // write
        long start = System.currentTimeMillis();
        try (ExcelWriter excelWriter = FastExcel.write(fileCsv).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            for (int j = 0; j < 5000; j++) {
                excelWriter.write(data(), writeSheet);
                log.info("{} write success.", j);
            }
        }
        log.info("CSV large data total time spent:{}", System.currentTimeMillis() - start);

        //  read
        start = System.currentTimeMillis();
        FastExcel.read(fileCsv, LargeData.class, new LargeDataListener())
                .sheet()
                .doRead();
        log.info("CSV large data total time spent:{}", System.currentTimeMillis() - start);
    }

    @Test
    public void t04Write() throws Exception {
        ExcelWriter excelWriter =
                FastExcel.write(fileWriteTemp07, LargeData.class).build();
        WriteSheet writeSheet = FastExcel.writerSheet().build();
        for (int j = 0; j < 2; j++) {
            excelWriter.write(data(), writeSheet);
        }
        excelWriter.finish();

        long start = System.currentTimeMillis();
        excelWriter = FastExcel.write(fileWrite07, LargeData.class).build();
        writeSheet = FastExcel.writerSheet().build();
        for (int j = 0; j < 5000; j++) {
            excelWriter.write(data(), writeSheet);
            log.info("{} write success.", j);
        }
        excelWriter.finish();
        long cost = System.currentTimeMillis() - start;
        log.info("write cost:{}", cost);
        start = System.currentTimeMillis();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileWritePoi07)) {
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = workbook.createSheet("sheet1");
            for (int i = 0; i < 100 * 5000; i++) {
                SXSSFRow row = sheet.createRow(i);
                for (int j = 0; j < 25; j++) {
                    SXSSFCell cell = row.createCell(j);
                    cell.setCellValue("str-" + j + "-" + i);
                }
                if (i % 5000 == 0) {
                    log.info("{} write success.", i);
                }
            }
            workbook.write(fileOutputStream);
            workbook.dispose();
            workbook.close();
        }
        long costPoi = System.currentTimeMillis() - start;
        log.info("poi write cost:{}", System.currentTimeMillis() - start);
        log.info("{} vs {}", cost, costPoi);
        Assertions.assertTrue(costPoi * 2 > cost);
    }

    private List<LargeData> data() {
        List<LargeData> list = new ArrayList<>();
        int size = i + 100;
        for (; i < size; i++) {
            LargeData largeData = new LargeData();
            list.add(largeData);
            largeData.setStr1("str1-" + i);
            largeData.setStr2("str2-" + i);
            largeData.setStr3("str3-" + i);
            largeData.setStr4("str4-" + i);
            largeData.setStr5("str5-" + i);
            largeData.setStr6("str6-" + i);
            largeData.setStr7("str7-" + i);
            largeData.setStr8("str8-" + i);
            largeData.setStr9("str9-" + i);
            largeData.setStr10("str10-" + i);
            largeData.setStr11("str11-" + i);
            largeData.setStr12("str12-" + i);
            largeData.setStr13("str13-" + i);
            largeData.setStr14("str14-" + i);
            largeData.setStr15("str15-" + i);
            largeData.setStr16("str16-" + i);
            largeData.setStr17("str17-" + i);
            largeData.setStr18("str18-" + i);
            largeData.setStr19("str19-" + i);
            largeData.setStr20("str20-" + i);
            largeData.setStr21("str21-" + i);
            largeData.setStr22("str22-" + i);
            largeData.setStr23("str23-" + i);
            largeData.setStr24("str24-" + i);
            largeData.setStr25("str25-" + i);
        }
        return list;
    }
}

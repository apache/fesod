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

import com.alibaba.fastjson2.JSON;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * 测试poi
 *
 *
 **/
public class PoiWriteTest {

    @Test
    public void write0(@TempDir Path path) throws IOException {
        try (FileOutputStream fileOutputStream =
                        new FileOutputStream(path.resolve("PoiWriteTest_" + System.currentTimeMillis() + ".xlsx")
                                .toFile());
                SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet("t1");
            SXSSFRow row = sheet.createRow(0);
            SXSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(999999999999999L);
            SXSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(1000000000000001L);
            SXSSFCell cell32 = row.createCell(2);
            cell32.setCellValue(300.35f);
            workbook.write(fileOutputStream);
        }
    }

    @Test
    public void write01() throws IOException {
        float ff = 300.35f;
        BigDecimal bd = new BigDecimal(Float.toString(ff));
        System.out.println(bd.doubleValue());
        System.out.println(bd.floatValue());
    }

    @Test
    public void write(@TempDir Path path) throws IOException {
        try (FileOutputStream fileOutputStream =
                        new FileOutputStream(path.resolve("PoiWriteTest_" + System.currentTimeMillis() + ".xlsx")
                                .toFile());
                SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet("t1");
            SXSSFRow row = sheet.createRow(0);
            SXSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(Long.toString(999999999999999L));
            SXSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(Long.toString(1000000000000001L));
            workbook.write(fileOutputStream);
        }
    }

    @Test
    public void write1() {
        System.out.println(JSON.toJSONString(long2Bytes(-999999999999999L)));
        System.out.println(JSON.toJSONString(long2Bytes(-9999999999999999L)));
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }
}

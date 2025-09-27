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

package org.apache.fesod.excel.temp.write;

import cn.idev.excel.support.cglib.beans.BeanMap;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.demo.write.CustomStringStringConverter;
import org.apache.fesod.excel.util.BeanMapUtils;
import org.apache.fesod.excel.util.FileUtils;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Slf4j
public class TempWriteTest {

    @Test
    public void write() {
        TempWriteData tempWriteData = new TempWriteData();
        tempWriteData.setName("zs\r\n \\ \r\n t4");
        FastExcel.write(
                        TestFileUtil.getPath() + "TempWriteTest" + System.currentTimeMillis() + ".xlsx",
                        TempWriteData.class)
                .sheet()
                .registerConverter(new CustomStringStringConverter())
                .doWrite(ListUtils.newArrayList(tempWriteData));

        FastExcel.write(
                        TestFileUtil.getPath() + "TempWriteTest" + System.currentTimeMillis() + ".xlsx",
                        TempWriteData.class)
                .sheet()
                .doWrite(ListUtils.newArrayList(tempWriteData));
    }

    @Test
    public void cglib() {
        TempWriteData tempWriteData = new TempWriteData();
        tempWriteData.setName("1");
        tempWriteData.setName1("2");
        BeanMap beanMap = BeanMapUtils.create(tempWriteData);

        log.info("d1{}", beanMap.get("name"));
        log.info("d2{}", beanMap.get("name1"));

        TempWriteData tempWriteData2 = new TempWriteData();

        Map<String, String> map = new HashMap<>();
        map.put("name", "zs");
        BeanMap beanMap2 = BeanMapUtils.create(tempWriteData2);
        beanMap2.putAll(map);
        log.info("3{}", tempWriteData2.getName());
    }

    @Test
    public void imageWrite() throws Exception {
        // String fileName = TestFileUtil.getPath() + "imageWrite" + System.currentTimeMillis() + ".xlsx";
        //
        //// 这里 需要指定写用哪个class去写
        // try (ExcelWriter excelWriter = FastExcel.write(fileName, DemoData.class).build()) {
        //    // 这里注意 如果同一个sheet只要创建一次
        //    WriteSheet writeSheet = FastExcel.writerSheet("模板").build();
        //    // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
        //    for (int i = 0; i < 5; i++) {
        //        // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
        //        List<DemoData> data = data();
        //        excelWriter.write(data, writeSheet);
        //    }
        // }
    }

    @Test
    public void imageWritePoi(@TempDir Path tempDir) throws Exception {
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        try (SXSSFWorkbook workbook = new SXSSFWorkbook();
                FileOutputStream fileOutputStream = new FileOutputStream(file); ) {
            SXSSFSheet sheet = workbook.createSheet("测试");
            CreationHelper helper = workbook.getCreationHelper();
            SXSSFDrawing sxssfDrawin = sheet.createDrawingPatriarch();

            byte[] imagebyte = FileUtils.readFileToByteArray(new File("src/test/resources/converter/img.jpg"));

            for (int i = 0; i < 1 * 10000; i++) {
                SXSSFRow row = sheet.createRow(i);
                SXSSFCell cell = row.createCell(0);
                cell.setCellValue(i);
                int pictureIdx = workbook.addPicture(imagebyte, Workbook.PICTURE_TYPE_JPEG);
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(i);
                // 插入图片
                Picture pict = sxssfDrawin.createPicture(anchor, pictureIdx);
                pict.resize();
                log.info("新增行:{}", i);
            }
            workbook.write(fileOutputStream);
        }
    }

    @Test
    public void tep(@TempDir Path tempDir) throws Exception {
        String file = tempDir.resolve(System.currentTimeMillis() + ".xlsx").toString();
        try (SXSSFWorkbook workbook = new SXSSFWorkbook();
                FileOutputStream fileOutputStream = new FileOutputStream(file); ) {
            SXSSFSheet sheet = workbook.createSheet("测试");
            CreationHelper helper = workbook.getCreationHelper();
            SXSSFDrawing sxssfDrawin = sheet.createDrawingPatriarch();

            byte[] imagebyte = FileUtils.readFileToByteArray(new File("src/test/resources/converter/img.jpg"));

            for (int i = 0; i < 1 * 10000; i++) {
                SXSSFRow row = sheet.createRow(i);
                SXSSFCell cell = row.createCell(0);
                cell.setCellValue(i);
                int pictureIdx = workbook.addPicture(imagebyte, Workbook.PICTURE_TYPE_JPEG);
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(i);
                // 插入图片
                Picture pict = sxssfDrawin.createPicture(anchor, pictureIdx);
                pict.resize();
                log.info("新增行:{}", i);
            }
            workbook.write(fileOutputStream);
        }
    }
}

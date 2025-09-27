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

package org.apache.fesod.excel.skip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.event.SyncReadListener;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.simple.SimpleData;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SkipDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("skip.xlsx");
        file03 = TestFileUtil.createNewFile("skip.xls");
        fileCsv = TestFileUtil.createNewFile("skip.csv");
    }

    @Test
    public void t01ReadAndWrite07() {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() {
        Assertions.assertThrows(ExcelGenerateException.class, () -> readAndWrite(fileCsv));
    }

    private void readAndWrite(File file) {
        try (ExcelWriter excelWriter = FastExcel.write(file, SimpleData.class).build(); ) {
            WriteSheet writeSheet0 = FastExcel.writerSheet(0, "第一个").build();
            WriteSheet writeSheet1 = FastExcel.writerSheet(1, "第二个").build();
            WriteSheet writeSheet2 = FastExcel.writerSheet(2, "第三个").build();
            WriteSheet writeSheet3 = FastExcel.writerSheet(3, "第四个").build();
            excelWriter.write(data("name1"), writeSheet0);
            excelWriter.write(data("name2"), writeSheet1);
            excelWriter.write(data("name3"), writeSheet2);
            excelWriter.write(data("name4"), writeSheet3);
        }

        List<SkipData> list =
                FastExcel.read(file, SkipData.class, null).sheet("第二个").doReadSync();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("name2", list.get(0).getName());

        SyncReadListener syncReadListener = new SyncReadListener();
        try (ExcelReader excelReader = FastExcel.read(file, SkipData.class, null)
                .registerReadListener(syncReadListener)
                .build()) {
            ReadSheet readSheet1 = FastExcel.readSheet("第二个").build();
            ReadSheet readSheet3 = FastExcel.readSheet("第四个").build();
            excelReader.read(readSheet1, readSheet3);
            List<Object> syncList = syncReadListener.getList();
            Assertions.assertEquals(2, syncList.size());
            Assertions.assertEquals("name2", ((SkipData) syncList.get(0)).getName());
            Assertions.assertEquals("name4", ((SkipData) syncList.get(1)).getName());
        }
    }

    private List<SkipData> data(String name) {
        List<SkipData> list = new ArrayList<SkipData>();
        SkipData data = new SkipData();
        data.setName(name);
        list.add(data);
        return list;
    }
}

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

package org.apache.fesod.excel.head;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class MaxHeadSizeTest {

    private static String headFile01;
    private static String headFile02;
    private static String headFile03;

    @BeforeAll
    public static void init() {
        headFile01 = TestFileUtil.getPath() + "temp/issue220" + File.separator + "test01.xlsx";
        headFile02 = TestFileUtil.getPath() + "temp/issue220" + File.separator + "test02.xlsx";
        headFile03 = TestFileUtil.getPath() + "temp/issue220" + File.separator + "test03.xlsx";
    }

    @Test
    public void t01ReadTest() throws Exception {
        // issue example
        readFileWithMap(headFile01, 6);
        readFileWithPOJO(headFile01);
    }

    @Test
    public void t02ReadTest() throws Exception {
        // 表头有空列
        readFileWithMap(headFile02, 8);
        readFileWithPOJO(headFile02);
    }

    @Test
    public void t03ReadTest() throws Exception {
        // 表头列数比实际数据行的列少
        readFileWithMap(headFile03, 4);
        readFileWithPOJO(headFile03);
    }

    private void readFileWithMap(String file, int expectHeadSize) {
        List<Map<Integer, String>> dataList;
        // default
        dataList = FastExcel.read(file).excelType(ExcelTypeEnum.XLSX).sheet().doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
            Assertions.assertTrue(d.size() >= expectHeadSize);
        });

        // custom listener
        dataList = FastExcel.read(file, new MaxHeadReadListener(expectHeadSize))
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
            Assertions.assertTrue(d.size() >= expectHeadSize);
        });
    }

    private void readFileWithPOJO(String file) {
        List<MaxHeadSizeData> dataList = FastExcel.read(file)
                .head(MaxHeadSizeData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
        });
    }
}

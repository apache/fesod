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

package org.apache.fesod.sheet.head;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
@Tag(Tags.READ)
@Slf4j
public class MaxHeadSizeTest extends AbstractExcelTest {

    @Test
    public void readIssueExample() {
        File headFile01 = headFile("test01.xlsx");
        readFileWithMap(headFile01, 6);
        readFileWithPOJO(headFile01);
    }

    @Test
    public void readWithEmptyHeadColumns() {
        File headFile02 = headFile("test02.xlsx");
        // The header row contains empty columns.
        readFileWithMap(headFile02, 8);
        readFileWithPOJO(headFile02);
    }

    @Test
    public void readWithFewerHeadColumns() {
        File headFile03 = headFile("test03.xlsx");
        // The header row has fewer columns than the actual data rows.
        readFileWithMap(headFile03, 4);
        readFileWithPOJO(headFile03);
    }

    private void readFileWithMap(File file, int expectHeadSize) {
        List<Map<Integer, String>> dataList;
        // default
        dataList = FesodSheet.read(file).excelType(ExcelTypeEnum.XLSX).sheet().doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
            Assertions.assertTrue(d.size() >= expectHeadSize);
        });

        // custom listener
        dataList = FesodSheet.read(file, new MaxHeadReadListener())
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
            Assertions.assertTrue(d.size() >= expectHeadSize);
        });
    }

    private void readFileWithPOJO(File file) {
        List<MaxHeadSizeData> dataList = FesodSheet.read(file)
                .head(MaxHeadSizeData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .doReadSync();
        dataList.forEach(d -> {
            log.info(JSON.toJSONString(d, JSONWriter.Feature.WriteMapNullValue));
        });
    }

    private File headFile(String fileName) {
        return readFile("temp/issue220" + File.separator + fileName);
    }
}

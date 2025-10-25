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

package org.apache.fesod.excel.writesheet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WriteSheetTest {

    @Test
    public void testSheetOrder03() {
        // normal
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(0));
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(0, 1, 2));
        // sheetNo is bigger than the size of workbook sheet num
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(2));
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(10, 6, 8));
        // negative numbers
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(-1));
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(-8, -10, -6));
        testSheetOrderInternal(ExcelTypeEnum.XLS, Arrays.asList(-8, 6));
        // build a WriteSheet using the sheet name
        testSheetOrderWithSheetName(ExcelTypeEnum.XLS);
    }

    @Test
    public void testSheetOrder07() {
        // normal
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(0));
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(0, 1, 2));
        // sheetNo is bigger than the size of workbook sheet num
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(2));
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(10, 6, 8));
        // negative numbers
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(-1));
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(-8, -10, -6));
        testSheetOrderInternal(ExcelTypeEnum.XLSX, Arrays.asList(-8, 6));
        // build a WriteSheet using the sheet name
        testSheetOrderWithSheetName(ExcelTypeEnum.XLSX);
    }

    private void testSheetOrderInternal(ExcelTypeEnum excelTypeEnum, List<Integer> sheetNoList) {
        Map<Integer, Integer> dataMap = initSheetDataSizeList(sheetNoList);

        File testFile = TestFileUtil.createNewFile("writesheet/write-sheet-order" + excelTypeEnum.getValue());
        // write a file in the order of sheetNoList.
        try (ExcelWriter excelWriter = FastExcel.write(testFile, WriteSheetData.class)
                .excelType(excelTypeEnum)
                .build()) {
            for (Integer sheetNo : sheetNoList) {
                excelWriter.write(
                        dataList(dataMap.get(sheetNo)),
                        FastExcel.writerSheet(sheetNo).build());
            }
        }

        for (int i = 0; i < sheetNoList.size(); i++) {
            List<WriteSheetData> sheetDataList = FastExcel.read(testFile)
                    .excelType(excelTypeEnum)
                    .head(WriteSheetData.class)
                    .sheet(i)
                    .doReadSync();
            Assertions.assertEquals(dataMap.get(sheetNoList.get(i)), sheetDataList.size());
        }
    }

    private Map<Integer, Integer> initSheetDataSizeList(List<Integer> sheetNoList) {
        // sort by sheetNo
        Collections.sort(sheetNoList);
        // key: sheetNo
        // value: data size
        Map<Integer, Integer> dataMap = new HashMap<>();
        for (int i = 0; i < sheetNoList.size(); i++) {
            dataMap.put(sheetNoList.get(i), i + 1);
        }
        return dataMap;
    }

    private void testSheetOrderWithSheetName(ExcelTypeEnum excelTypeEnum) {
        List<String> sheetNameList = Arrays.asList("Sheet1", "Sheet2", "Sheet3", "Sheet111112222233333444445555566666");
        List<Integer> sheetNoList = Arrays.asList(0, 1, 2, 3);

        Map<Integer, Integer> dataMap = initSheetDataSizeList(sheetNoList);
        File testFile = TestFileUtil.createNewFile("writesheet/write-sheet-order-name" + excelTypeEnum.getValue());

        try (ExcelWriter excelWriter = FastExcel.write(testFile, WriteSheetData.class)
                .excelType(excelTypeEnum)
                .build()) {

            // sheetName is empty
            int sheetNo = 0;
            WriteSheet writeSheet = FastExcel.writerSheet(sheetNo).build();
            excelWriter.write(dataList(dataMap.get(sheetNo)), writeSheet);
            Assertions.assertEquals(
                    sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            // sheetNo is empty
            sheetNo = 1;
            writeSheet = FastExcel.writerSheet(sheetNameList.get(sheetNo)).build();
            excelWriter.write(dataList(dataMap.get(sheetNo)), writeSheet);
            Assertions.assertEquals(
                    sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            sheetNo = 2;
            writeSheet =
                    FastExcel.writerSheet(sheetNo, sheetNameList.get(sheetNo)).build();
            excelWriter.write(dataList(dataMap.get(sheetNo)), writeSheet);
            Assertions.assertEquals(
                    sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            sheetNo = 3;
            writeSheet =
                    FastExcel.writerSheet(sheetNo, sheetNameList.get(sheetNo)).build();
            excelWriter.write(dataList(dataMap.get(sheetNo)), writeSheet);
            Assertions.assertEquals(
                    sheetNameList.get(sheetNo).substring(0, Workbook.MAX_SENSITIVE_SHEET_NAME_LEN),
                    excelWriter.writeContext().writeSheetHolder().getSheetName());
            Assertions.assertEquals(
                    sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());
        }

        for (int i = 0; i < sheetNoList.size(); i++) {
            List<WriteSheetData> sheetDataList = FastExcel.read(testFile)
                    .excelType(excelTypeEnum)
                    .head(WriteSheetData.class)
                    .sheet(i)
                    .doReadSync();
            Assertions.assertEquals(dataMap.get(sheetNoList.get(i)), sheetDataList.size());
        }
    }

    private static List<WriteSheetData> dataList(int size) {
        List<WriteSheetData> dataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            WriteSheetData data = new WriteSheetData();
            data.setString("String" + i);
            dataList.add(data);
        }
        return dataList;
    }
}

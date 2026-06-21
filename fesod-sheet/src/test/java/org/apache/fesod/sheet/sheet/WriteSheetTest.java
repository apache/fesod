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

package org.apache.fesod.sheet.sheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
@Tag(Tags.WRITE)
public class WriteSheetTest extends AbstractExcelTest {

    @Test
    public void testSheetOrderXls() throws Exception {
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(0));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(0, 1, 2));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(2));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(10, 6, 8));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(-1));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(-8, -10, -6));
        testSheetOrderInternal(ExcelFormat.XLS, Arrays.asList(-8, 6));
        testSheetOrderWithSheetName(ExcelFormat.XLS);
    }

    @Test
    public void testSheetOrderXlsx() throws Exception {
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(0));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(0, 1, 2));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(2));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(10, 6, 8));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(-1));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(-8, -10, -6));
        testSheetOrderInternal(ExcelFormat.XLSX, Arrays.asList(-8, 6));
        testSheetOrderWithSheetName(ExcelFormat.XLSX);
    }

    private void testSheetOrderInternal(ExcelFormat format, List<Integer> sheetNoList) throws Exception {
        Map<Integer, Integer> dataMap = initSheetDataSizeList(sheetNoList);

        File testFile = createTempFile(format);
        try (ExcelWriter excelWriter = FesodSheet.write(testFile, WriteSheetData.class)
                .excelType(format.toExcelTypeEnum())
                .build()) {
            for (Integer sheetNo : sheetNoList) {
                excelWriter.write(
                        TestDataBuilder.writeSheetData(dataMap.get(sheetNo)),
                        FesodSheet.writerSheet(sheetNo).build());
            }
        }

        for (int i = 0; i < sheetNoList.size(); i++) {
            List<WriteSheetData> sheetDataList = FesodSheet.read(testFile)
                    .excelType(format.toExcelTypeEnum())
                    .head(WriteSheetData.class)
                    .sheet(i)
                    .doReadSync();
            assertEquals(dataMap.get(sheetNoList.get(i)), sheetDataList.size());
        }
    }

    private Map<Integer, Integer> initSheetDataSizeList(List<Integer> sheetNoList) {
        Collections.sort(sheetNoList);
        Map<Integer, Integer> dataMap = new HashMap<>();
        for (int i = 0; i < sheetNoList.size(); i++) {
            dataMap.put(sheetNoList.get(i), i + 1);
        }
        return dataMap;
    }

    private void testSheetOrderWithSheetName(ExcelFormat format) throws Exception {
        List<String> sheetNameList = Arrays.asList("Sheet1", "Sheet2", "Sheet3", "Sheet111112222233333444445555566666");
        List<Integer> sheetNoList = Arrays.asList(0, 1, 2, 3);

        Map<Integer, Integer> dataMap = initSheetDataSizeList(sheetNoList);
        File testFile = createTempFile("sheet-order-name", format);

        try (ExcelWriter excelWriter = FesodSheet.write(testFile, WriteSheetData.class)
                .excelType(format.toExcelTypeEnum())
                .build()) {

            int sheetNo = 0;
            WriteSheet writeSheet = FesodSheet.writerSheet(sheetNo).build();
            excelWriter.write(TestDataBuilder.writeSheetData(dataMap.get(sheetNo)), writeSheet);
            assertEquals(sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            sheetNo = 1;
            writeSheet = FesodSheet.writerSheet(sheetNameList.get(sheetNo)).build();
            excelWriter.write(TestDataBuilder.writeSheetData(dataMap.get(sheetNo)), writeSheet);
            assertEquals(sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            sheetNo = 2;
            writeSheet =
                    FesodSheet.writerSheet(sheetNo, sheetNameList.get(sheetNo)).build();
            excelWriter.write(TestDataBuilder.writeSheetData(dataMap.get(sheetNo)), writeSheet);
            assertEquals(sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());

            sheetNo = 3;
            writeSheet =
                    FesodSheet.writerSheet(sheetNo, sheetNameList.get(sheetNo)).build();
            excelWriter.write(TestDataBuilder.writeSheetData(dataMap.get(sheetNo)), writeSheet);
            assertEquals(
                    sheetNameList.get(sheetNo).substring(0, Workbook.MAX_SENSITIVE_SHEET_NAME_LEN),
                    excelWriter.writeContext().writeSheetHolder().getSheetName());
            assertEquals(sheetNo, excelWriter.writeContext().writeSheetHolder().getSheetNo());
        }

        for (int i = 0; i < sheetNoList.size(); i++) {
            List<WriteSheetData> sheetDataList = FesodSheet.read(testFile)
                    .excelType(format.toExcelTypeEnum())
                    .head(WriteSheetData.class)
                    .sheet(i)
                    .doReadSync();
            assertEquals(dataMap.get(sheetNoList.get(i)), sheetDataList.size());
        }
    }
}

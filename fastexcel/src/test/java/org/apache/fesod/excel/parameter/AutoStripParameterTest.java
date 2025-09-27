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

package org.apache.fesod.excel.parameter;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.ParameterUtil;
import org.apache.fesod.excel.util.SheetUtils;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class AutoStripParameterTest {

    private static File testFile;
    private static final String FW_SPACES = "　";
    private static final String SPACES = " ";

    @Test
    public void test03() {
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, null, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, null, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, null, true);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, false, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, false, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, false, true);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, true, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, true, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLS, true, true);

        testAutoStripContentInternal(ExcelTypeEnum.XLS, null, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, null, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, null, true);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, false, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, false, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, false, true);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, true, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, true, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLS, true, true);
    }

    @Test
    public void test07() {
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, null, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, null, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, null, true);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, false, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, false, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, false, true);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, true, null);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, true, false);
        testAutoStripSheetNameInternal(ExcelTypeEnum.XLSX, true, true);

        testAutoStripContentInternal(ExcelTypeEnum.XLSX, null, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, null, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, null, true);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, false, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, false, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, false, true);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, true, null);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, true, false);
        testAutoStripContentInternal(ExcelTypeEnum.XLSX, true, true);
    }

    @Test
    public void testCSV() {
        testAutoStripContentInternal(ExcelTypeEnum.CSV, null, null);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, null, false);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, null, true);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, false, null);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, false, false);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, false, true);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, true, null);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, true, false);
        testAutoStripContentInternal(ExcelTypeEnum.CSV, true, true);
    }

    private void testAutoStripSheetNameInternal(
            final ExcelTypeEnum excelType, final Boolean autoTrim, final Boolean autoStrip) {
        testFile = TestFileUtil.createNewFile("auto-strip-sheet-name" + excelType.getValue());

        final String sheetNameSpaces = SPACES + "Sheet1" + SPACES;
        final String sheetNameFullWidthSpaces = FW_SPACES + "Sheet2" + FW_SPACES;

        List<ParameterData> demoList = new ArrayList<>();
        ParameterData simpleData = new ParameterData();
        simpleData.setName("string");
        demoList.add(simpleData);

        try (ExcelWriter excelWriter = FastExcel.write(testFile, ParameterData.class)
                .excelType(excelType)
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .build()) {
            WriteSheet writeSheet = FastExcel.writerSheet(sheetNameSpaces).build();
            excelWriter.write(demoList, writeSheet);
            writeSheet = FastExcel.writerSheet(sheetNameFullWidthSpaces).build();
            excelWriter.write(demoList, writeSheet);
        }

        try (ExcelReader excelReader = FastExcel.read(testFile)
                .excelType(excelType)
                .head(ParameterData.class)
                .registerReadListenerIfNotNull(new AnalysisEventListener<ParameterData>() {
                    @Override
                    public void invoke(ParameterData data, AnalysisContext context) {
                        log.info("Read one record: {}", JSON.toJSONString(data));
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        // global configuration match
                        Assertions.assertEquals(
                                autoTrim == null ? Boolean.TRUE : autoTrim,
                                ParameterUtil.getAutoTrimFlag(
                                        context.readSheetHolder().getReadSheet(), context));
                        Assertions.assertEquals(
                                autoStrip == null ? Boolean.FALSE : autoStrip,
                                ParameterUtil.getAutoStripFlag(
                                        context.readSheetHolder().getReadSheet(), context));

                        // sheet name match
                        ReadSheet readSheet = context.readSheetHolder().getReadSheet();
                        Assertions.assertEquals(readSheet, SheetUtils.match(readSheet, context));
                    }
                })
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .build()) {

            // set sheet name
            excelReader.read(
                    FastExcel.readSheet(sheetNameSpaces).build(),
                    FastExcel.readSheet(sheetNameFullWidthSpaces).build());
        }
    }

    private void testAutoStripContentInternal(
            final ExcelTypeEnum excelType, final Boolean autoTrim, final Boolean autoStrip) {
        testFile = TestFileUtil.createNewFile("auto-strip-content" + excelType.getValue());

        final String testContentSpaces = SPACES + "String Data1" + SPACES;
        final String testContentFullWidthSpaces = FW_SPACES + "String Data2" + FW_SPACES;

        List<ParameterData> demoList = new ArrayList<>();
        ParameterData simpleData = new ParameterData();
        // normal spaces
        simpleData.setName(testContentSpaces);
        demoList.add(simpleData);

        simpleData = new ParameterData();
        // full-width spaces
        simpleData.setName(testContentFullWidthSpaces);
        demoList.add(simpleData);

        FastExcel.write(testFile, ParameterData.class)
                .excelType(excelType)
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .sheet()
                .doWrite(demoList);

        List<ParameterData> dataList = FastExcel.read(testFile)
                .excelType(excelType)
                .head(ParameterData.class)
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .sheet()
                .doReadSync();

        log.info("Read records: {}", JSON.toJSONString(dataList));
        Assertions.assertEquals(2, dataList.size());
        if (Boolean.TRUE.equals(autoStrip)) {
            Assertions.assertEquals(
                    StringUtils.strip(testContentSpaces), dataList.get(0).getName());
            Assertions.assertEquals(
                    StringUtils.strip(testContentFullWidthSpaces),
                    dataList.get(1).getName());
        } else if (autoTrim == null || autoTrim) {
            Assertions.assertEquals(testContentSpaces.trim(), dataList.get(0).getName());
            Assertions.assertEquals(
                    testContentFullWidthSpaces.trim(), dataList.get(1).getName());
        } else {
            Assertions.assertEquals(testContentSpaces, dataList.get(0).getName());
            Assertions.assertEquals(testContentFullWidthSpaces, dataList.get(1).getName());
        }
    }
}

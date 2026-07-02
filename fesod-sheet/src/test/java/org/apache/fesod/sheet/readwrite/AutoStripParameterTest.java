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

package org.apache.fesod.sheet.readwrite;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.util.ParameterUtil;
import org.apache.fesod.sheet.util.SheetUtils;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
@Slf4j
public class AutoStripParameterTest extends AbstractExcelTest {

    private static final String FW_SPACES = "　";
    private static final String SPACES = " ";

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void testAutoStripSheetName(ExcelFormat format) {
        ExcelTypeEnum excelType = format.toExcelTypeEnum();
        testAutoStripSheetNameInternal(excelType, null, null);
        testAutoStripSheetNameInternal(excelType, null, false);
        testAutoStripSheetNameInternal(excelType, null, true);
        testAutoStripSheetNameInternal(excelType, false, null);
        testAutoStripSheetNameInternal(excelType, false, false);
        testAutoStripSheetNameInternal(excelType, false, true);
        testAutoStripSheetNameInternal(excelType, true, null);
        testAutoStripSheetNameInternal(excelType, true, false);
        testAutoStripSheetNameInternal(excelType, true, true);
    }

    @ParameterizedTest
    @ExcelFormatSource
    void testAutoStripContent(ExcelFormat format) {
        ExcelTypeEnum excelType = format.toExcelTypeEnum();
        testAutoStripContentInternal(excelType, null, null);
        testAutoStripContentInternal(excelType, null, false);
        testAutoStripContentInternal(excelType, null, true);
        testAutoStripContentInternal(excelType, false, null);
        testAutoStripContentInternal(excelType, false, false);
        testAutoStripContentInternal(excelType, false, true);
        testAutoStripContentInternal(excelType, true, null);
        testAutoStripContentInternal(excelType, true, false);
        testAutoStripContentInternal(excelType, true, true);
    }

    private void testAutoStripSheetNameInternal(
            final ExcelTypeEnum excelType, final Boolean autoTrim, final Boolean autoStrip) {
        File testFile = new File(tempDir, "auto-strip-sheet-name" + excelType.getValue());

        final String sheetNameSpaces = SPACES + "Sheet1" + SPACES;
        final String sheetNameFullWidthSpaces = FW_SPACES + "Sheet2" + FW_SPACES;

        List<SimpleData> demoList = new ArrayList<>();
        SimpleData simpleData = new SimpleData();
        simpleData.setName("string");
        demoList.add(simpleData);

        try (ExcelWriter excelWriter = FesodSheet.write(testFile, SimpleData.class)
                .excelType(excelType)
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet(sheetNameSpaces).build();
            excelWriter.write(demoList, writeSheet);
            writeSheet = FesodSheet.writerSheet(sheetNameFullWidthSpaces).build();
            excelWriter.write(demoList, writeSheet);
        }

        try (ExcelReader excelReader = FesodSheet.read(testFile)
                .excelType(excelType)
                .head(SimpleData.class)
                .registerReadListenerIfNotNull(new AnalysisEventListener<SimpleData>() {
                    @Override
                    public void invoke(SimpleData data, AnalysisContext context) {
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
                    FesodSheet.readSheet(sheetNameSpaces).build(),
                    FesodSheet.readSheet(sheetNameFullWidthSpaces).build());
        }
    }

    private void testAutoStripContentInternal(
            final ExcelTypeEnum excelType, final Boolean autoTrim, final Boolean autoStrip) {
        File testFile = new File(tempDir, "auto-strip-content" + excelType.getValue());

        final String testContentSpaces = SPACES + "String Data1" + SPACES;
        final String testContentFullWidthSpaces = FW_SPACES + "String Data2" + FW_SPACES;

        List<SimpleData> demoList = new ArrayList<>();
        SimpleData simpleData = new SimpleData();
        // normal spaces
        simpleData.setName(testContentSpaces);
        demoList.add(simpleData);

        simpleData = new SimpleData();
        // full-width spaces
        simpleData.setName(testContentFullWidthSpaces);
        demoList.add(simpleData);

        FesodSheet.write(testFile, SimpleData.class)
                .excelType(excelType)
                .autoTrim(autoTrim)
                .autoStrip(autoStrip)
                .sheet()
                .doWrite(demoList);

        List<SimpleData> dataList = FesodSheet.read(testFile)
                .excelType(excelType)
                .head(SimpleData.class)
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

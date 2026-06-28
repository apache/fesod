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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.metadata.property.FontProperty;
import org.apache.fesod.sheet.metadata.property.StyleProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.merge.LoopMergeStrategy;
import org.apache.fesod.sheet.write.merge.OnceAbsoluteMergeStrategy;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.metadata.style.WriteFont;
import org.apache.fesod.sheet.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.fesod.sheet.write.style.HorizontalCellStyleStrategy;
import org.apache.fesod.sheet.write.style.column.SimpleColumnWidthStyleStrategy;
import org.apache.fesod.sheet.write.style.row.SimpleRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test style write/read for binary Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class StyleDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("style", format);
        readAndWriteImpl(file);
    }

    @Test
    void abstractVerticalCellStyleStrategy() throws Exception {
        File file = createTempFile("verticalCellStyle", ExcelFormat.XLSX);
        AbstractVerticalCellStyleStrategy verticalCellStyleStrategy = new AbstractVerticalCellStyleStrategy() {
            @Override
            protected WriteCellStyle headCellStyle(Head head) {
                WriteCellStyle writeCellStyle = new WriteCellStyle();
                writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                DataFormatData dataFormatData = new DataFormatData();
                dataFormatData.setIndex((short) 0);
                writeCellStyle.setDataFormatData(dataFormatData);
                writeCellStyle.setHidden(false);
                writeCellStyle.setLocked(true);
                writeCellStyle.setQuotePrefix(true);
                writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
                writeCellStyle.setWrapped(true);
                writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                writeCellStyle.setRotation((short) 0);
                writeCellStyle.setIndent((short) 10);
                writeCellStyle.setBorderLeft(BorderStyle.THIN);
                writeCellStyle.setBorderRight(BorderStyle.THIN);
                writeCellStyle.setBorderTop(BorderStyle.THIN);
                writeCellStyle.setBorderBottom(BorderStyle.THIN);
                writeCellStyle.setLeftBorderColor(IndexedColors.RED.getIndex());
                writeCellStyle.setRightBorderColor(IndexedColors.RED.getIndex());
                writeCellStyle.setTopBorderColor(IndexedColors.RED.getIndex());
                writeCellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
                writeCellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
                writeCellStyle.setShrinkToFit(Boolean.TRUE);

                if (head.getColumnIndex() == 0) {
                    writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    WriteFont writeFont = new WriteFont();
                    writeFont.setItalic(true);
                    writeFont.setStrikeout(true);
                    writeFont.setTypeOffset(Font.SS_NONE);
                    writeFont.setUnderline(Font.U_DOUBLE);
                    writeFont.setBold(true);
                    writeFont.setCharset((int) Font.DEFAULT_CHARSET);
                } else {
                    writeCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                }
                return writeCellStyle;
            }

            @Override
            protected WriteCellStyle contentCellStyle(Head head) {
                WriteCellStyle writeCellStyle = new WriteCellStyle();
                writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                if (head.getColumnIndex() == 0) {
                    writeCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
                } else {
                    writeCellStyle.setFillForegroundColor(IndexedColors.PINK.getIndex());
                }
                return writeCellStyle;
            }
        };
        FesodSheet.write(file, StyleData.class)
                .registerWriteHandler(verticalCellStyleStrategy)
                .sheet()
                .doWrite(TestDataBuilder.styleData(2));
    }

    @Test
    void abstractVerticalCellStyleStrategy02() throws Exception {
        File file = createTempFile("verticalCellStyle2", ExcelFormat.XLSX);
        final StyleProperty styleProperty = StyleProperty.build(StyleData.class.getAnnotation(HeadStyle.class));
        final FontProperty fontProperty = FontProperty.build(StyleData.class.getAnnotation(HeadFontStyle.class));
        AbstractVerticalCellStyleStrategy verticalCellStyleStrategy = new AbstractVerticalCellStyleStrategy() {
            @Override
            protected WriteCellStyle headCellStyle(Head head) {
                WriteCellStyle writeCellStyle = WriteCellStyle.build(styleProperty, fontProperty);

                if (head.getColumnIndex() == 0) {
                    writeCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    WriteFont writeFont = new WriteFont();
                    writeFont.setItalic(true);
                    writeFont.setStrikeout(true);
                    writeFont.setTypeOffset(Font.SS_NONE);
                    writeFont.setUnderline(Font.U_DOUBLE);
                    writeFont.setBold(true);
                    writeFont.setCharset((int) Font.DEFAULT_CHARSET);
                } else {
                    writeCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                }
                return writeCellStyle;
            }

            @Override
            protected WriteCellStyle contentCellStyle(Head head) {
                WriteCellStyle writeCellStyle = new WriteCellStyle();
                writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                if (head.getColumnIndex() == 0) {
                    writeCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
                } else {
                    writeCellStyle.setFillForegroundColor(IndexedColors.PINK.getIndex());
                }
                return writeCellStyle;
            }
        };
        FesodSheet.write(file, StyleData.class)
                .registerWriteHandler(verticalCellStyleStrategy)
                .sheet()
                .doWrite(TestDataBuilder.styleData(2));
    }

    @Test
    void loopMergeStrategy() throws Exception {
        File file = createTempFile("loopMergeStrategy", ExcelFormat.XLSX);
        FesodSheet.write(file, StyleData.class)
                .sheet()
                .registerWriteHandler(new LoopMergeStrategy(2, 1))
                .doWrite(TestDataBuilder.styleData(10));
    }

    private void readAndWriteImpl(File file) {
        SimpleColumnWidthStyleStrategy simpleColumnWidthStyleStrategy = new SimpleColumnWidthStyleStrategy(50);
        SimpleRowHeightStyleStrategy simpleRowHeightStyleStrategy =
                new SimpleRowHeightStyleStrategy((short) 40, (short) 50);

        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteFont.setColor(IndexedColors.DARK_YELLOW.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 30);
        contentWriteFont.setColor(IndexedColors.DARK_TEAL.getIndex());
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        OnceAbsoluteMergeStrategy onceAbsoluteMergeStrategy = new OnceAbsoluteMergeStrategy(2, 2, 0, 1);
        FesodSheet.write(file, StyleData.class)
                .registerWriteHandler(simpleColumnWidthStyleStrategy)
                .registerWriteHandler(simpleRowHeightStyleStrategy)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(onceAbsoluteMergeStrategy)
                .sheet()
                .doWrite(TestDataBuilder.styleData(2));

        List<StyleData> result =
                FesodSheet.read(file).head(StyleData.class).sheet().doReadSync();
        assertEquals(2, result.size());
        assertEquals("String0", result.get(0).getString());
        assertEquals("String1", result.get(1).getString());

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .hasColumnWidth(0, 50 * 256)
                    .row(0)
                    .hasHeight((short) 800)
                    .cell(0)
                    .hasFillColor(new byte[] {-1, -1, 0})
                    .hasFontColor(new byte[] {-128, -128, 0})
                    .hasFontSize((short) 20)
                    .and()
                    .cell(1)
                    .hasFillColor(new byte[] {-1, -1, 0})
                    .hasFontColor(new byte[] {-128, -128, 0})
                    .hasFontSize((short) 20)
                    .and()
                    .and()
                    .row(1)
                    .hasHeight((short) 1000)
                    .cell(0)
                    .hasFillColor(new byte[] {0, -128, -128})
                    .hasFontColor(new byte[] {0, 51, 102})
                    .hasFontSize((short) 30)
                    .and()
                    .cell(1)
                    .hasFillColor(new byte[] {0, -128, -128})
                    .hasFontColor(new byte[] {0, 51, 102})
                    .hasFontSize((short) 30);
        }
    }
}

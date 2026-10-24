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

package org.apache.fesod.sheet.util;

import java.util.function.Consumer;
import org.apache.fesod.sheet.constant.BuiltinFormats;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.metadata.data.HyperlinkData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.metadata.style.WriteFont;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class StyleUtilTest {

    @Mock
    private WriteWorkbookHolder writeWorkbookHolder;

    @Test
    void testBuildCellStyle_withNullWriteCellStyle_shouldReturnDefaultStyle() {
        Workbook workbook = new HSSFWorkbook();
        CellStyle origin = workbook.createCellStyle();
        origin.setAlignment(HorizontalAlignment.CENTER);

        CellStyle result = StyleUtil.buildCellStyle(workbook, origin, null);

        Assertions.assertEquals(HorizontalAlignment.CENTER, result.getAlignment());
    }

    @Test
    void testBuildCellStyle_withAllPropertiesSet_shouldApplyAllProperties() {
        // Given
        Workbook workbook = new XSSFWorkbook();
        CellStyle originStyle = workbook.createCellStyle();

        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHidden(true);
        writeCellStyle.setLocked(false);
        writeCellStyle.setQuotePrefix(true);
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        writeCellStyle.setWrapped(true);
        writeCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        writeCellStyle.setRotation((short) 45);
        writeCellStyle.setIndent((short) 2);
        writeCellStyle.setBorderLeft(BorderStyle.THIN);
        writeCellStyle.setBorderRight(BorderStyle.MEDIUM);
        writeCellStyle.setBorderTop(BorderStyle.DASHED);
        writeCellStyle.setBorderBottom(BorderStyle.DOTTED);
        writeCellStyle.setLeftBorderColor((short) 10);
        writeCellStyle.setRightBorderColor((short) 11);
        writeCellStyle.setTopBorderColor((short) 12);
        writeCellStyle.setBottomBorderColor((short) 13);
        writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        writeCellStyle.setFillBackgroundColor((short) 14);
        writeCellStyle.setFillForegroundColor((short) 15);
        writeCellStyle.setShrinkToFit(true);

        CellStyle result = StyleUtil.buildCellStyle(workbook, originStyle, writeCellStyle);

        Assertions.assertTrue(result.getHidden());
        Assertions.assertFalse(result.getLocked());
        Assertions.assertTrue(result.getQuotePrefixed());
        Assertions.assertEquals(HorizontalAlignment.RIGHT, result.getAlignment());
        Assertions.assertTrue(result.getWrapText());
        Assertions.assertEquals(VerticalAlignment.BOTTOM, result.getVerticalAlignment());
        Assertions.assertEquals((short) 45, result.getRotation());
        Assertions.assertEquals((short) 2, result.getIndention());
        Assertions.assertEquals(BorderStyle.THIN, result.getBorderLeft());
        Assertions.assertEquals(BorderStyle.MEDIUM, result.getBorderRight());
        Assertions.assertEquals(BorderStyle.DASHED, result.getBorderTop());
        Assertions.assertEquals(BorderStyle.DOTTED, result.getBorderBottom());
        Assertions.assertEquals((short) 10, result.getLeftBorderColor());
        Assertions.assertEquals((short) 11, result.getRightBorderColor());
        Assertions.assertEquals((short) 12, result.getTopBorderColor());
        Assertions.assertEquals((short) 13, result.getBottomBorderColor());
        Assertions.assertEquals(FillPatternType.SOLID_FOREGROUND, result.getFillPattern());
        Assertions.assertEquals((short) 14, result.getFillBackgroundColor());
        Assertions.assertEquals((short) 15, result.getFillForegroundColor());
        Assertions.assertTrue(result.getShrinkToFit());
    }

    @Test
    void testBuildCellStyle_withPartialPropertiesSet_shouldOnlyApplyNonNullProperties() {
        Workbook workbook = new HSSFWorkbook();
        CellStyle originStyle = workbook.createCellStyle();
        originStyle.setVerticalAlignment(VerticalAlignment.CENTER); // original value

        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);

        CellStyle result = StyleUtil.buildCellStyle(workbook, originStyle, writeCellStyle);

        Assertions.assertEquals(HorizontalAlignment.LEFT, result.getAlignment());
        Assertions.assertEquals(VerticalAlignment.CENTER, result.getVerticalAlignment());
        Assertions.assertFalse(result.getHidden());
        Assertions.assertTrue(result.getLocked());
    }

    @Test
    void testBuildCellStyle_withNullOriginStyle_shouldCreateNewStyle() {
        Workbook workbook = new XSSFWorkbook();
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setWrapped(true);
        writeCellStyle.setFillForegroundColor((short) 7);

        CellStyle result = StyleUtil.buildCellStyle(workbook, null, writeCellStyle);

        Assertions.assertTrue(result.getWrapText());
        Assertions.assertEquals((short) 7, result.getFillForegroundColor());
        Assertions.assertFalse(result.getHidden());
        Assertions.assertTrue(result.getLocked());
    }

    @Test
    void testBuildDataFormat_withNull_shouldReturnGeneral() {
        Workbook workbook = new HSSFWorkbook();
        short format = StyleUtil.buildDataFormat(workbook, null);
        Assertions.assertEquals(BuiltinFormats.GENERAL, format);
    }

    @Test
    void testBuildDataFormat_withIndex_shouldReturnIndex() {
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex((short) 10);

        Workbook workbook = new HSSFWorkbook();
        short format = StyleUtil.buildDataFormat(workbook, dataFormatData);
        Assertions.assertEquals(10, format);
    }

    @Test
    void testBuildFont_withWriteFont_shouldApplyProperties() {
        Workbook workbook = new HSSFWorkbook();
        WriteFont writeFont = new WriteFont();
        writeFont.setFontName("Arial");
        writeFont.setFontHeightInPoints((short) 12);
        writeFont.setItalic(true);
        writeFont.setColor((short) 10); // Red color
        writeFont.setTypeOffset((short) 1);
        writeFont.setUnderline((byte) 1);
        writeFont.setCharset(3);
        writeFont.setBold(true);

        Font font = StyleUtil.buildFont(workbook, null, writeFont);

        Assertions.assertNotNull(font);
        Assertions.assertEquals("Arial", font.getFontName());
        Assertions.assertEquals(12, font.getFontHeightInPoints());
        Assertions.assertTrue(font.getItalic());
        Assertions.assertEquals(10, font.getColor());
        Assertions.assertEquals(1, font.getTypeOffset());
        Assertions.assertEquals(1, font.getUnderline());
        Assertions.assertEquals(3, font.getCharSet());
        Assertions.assertTrue(font.getBold());
    }

    @Test
    void testBuildRichTextString_withNull_shouldReturnNull() {
        Assertions.assertNull(StyleUtil.buildRichTextString(writeWorkbookHolder, null));
    }

    @Test
    void testGetHyperlinkType_withNull_shouldReturnNone() {
        Assertions.assertEquals(HyperlinkType.NONE, StyleUtil.getHyperlinkType(null));
    }

    @Test
    void testGetHyperlinkType_withUrl_shouldReturnUrl() {
        Assertions.assertEquals(HyperlinkType.URL, StyleUtil.getHyperlinkType(HyperlinkData.HyperlinkType.URL));
    }

    @Test
    void testGetCoordinate_withNull_shouldReturnZero() {
        Assertions.assertEquals(0, StyleUtil.getCoordinate(null));
    }

    @Test
    void testGetCoordinate_withValue_shouldConvertToEMU() {
        int coord = 100;
        int emu = StyleUtil.getCoordinate(coord);
        Assertions.assertTrue(emu > 0);
    }

    @Test
    void testGetCellCoordinate_withAbsolute_shouldReturnAbsolute() {
        Assertions.assertEquals(500, StyleUtil.getCellCoordinate(100, 500, 200));
    }

    @Test
    void testGetCellCoordinate_withRelative_shouldReturnRelativeAdded() {
        Assertions.assertEquals(300, StyleUtil.getCellCoordinate(100, null, 200));
    }

    @Test
    void testGetCellCoordinate_withNull_shouldReturnCurrent() {
        Assertions.assertEquals(100, StyleUtil.getCellCoordinate(100, null, null));
    }

    @Test
    void testSetIfNotNull_withNullValue_shouldNotInvokeSetter() {
        Consumer<String> setter = Mockito.mock(Consumer.class);
        String value = null;
        StyleUtil.setIfNotNull(setter, value);
        Mockito.verify(setter, Mockito.never()).accept(ArgumentMatchers.any());
    }

    @Test
    void testSetIfNotNull_withNonNullValue_shouldInvokeSetter() {
        Consumer<String> setter = Mockito.mock(Consumer.class);
        String value = "testValue";
        StyleUtil.setIfNotNull(setter, value);
        Mockito.verify(setter).accept("testValue");
    }

    @Test
    void testSetIfNotNull_withIntegerValue_shouldInvokeSetter() {
        Consumer<Integer> setter = Mockito.mock(Consumer.class);
        Integer value = 42;
        StyleUtil.setIfNotNull(setter, value);
        Mockito.verify(setter).accept(42);
    }
}

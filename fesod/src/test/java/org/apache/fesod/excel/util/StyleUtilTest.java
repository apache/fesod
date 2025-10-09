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

package org.apache.fesod.excel.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.util.function.Consumer;
import org.apache.fesod.excel.constant.BuiltinFormats;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.fesod.excel.metadata.data.HyperlinkData;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.excel.write.metadata.style.WriteCellStyle;
import org.apache.fesod.excel.write.metadata.style.WriteFont;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        assertEquals(HorizontalAlignment.CENTER, result.getAlignment());
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

        assertTrue(result.getHidden());
        assertFalse(result.getLocked());
        assertTrue(result.getQuotePrefixed());
        assertEquals(HorizontalAlignment.RIGHT, result.getAlignment());
        assertTrue(result.getWrapText());
        assertEquals(VerticalAlignment.BOTTOM, result.getVerticalAlignment());
        assertEquals((short) 45, result.getRotation());
        assertEquals((short) 2, result.getIndention());
        assertEquals(BorderStyle.THIN, result.getBorderLeft());
        assertEquals(BorderStyle.MEDIUM, result.getBorderRight());
        assertEquals(BorderStyle.DASHED, result.getBorderTop());
        assertEquals(BorderStyle.DOTTED, result.getBorderBottom());
        assertEquals((short) 10, result.getLeftBorderColor());
        assertEquals((short) 11, result.getRightBorderColor());
        assertEquals((short) 12, result.getTopBorderColor());
        assertEquals((short) 13, result.getBottomBorderColor());
        assertEquals(FillPatternType.SOLID_FOREGROUND, result.getFillPattern());
        assertEquals((short) 14, result.getFillBackgroundColor());
        assertEquals((short) 15, result.getFillForegroundColor());
        assertTrue(result.getShrinkToFit());
    }

    @Test
    void testBuildCellStyle_withPartialPropertiesSet_shouldOnlyApplyNonNullProperties() {
        Workbook workbook = new HSSFWorkbook();
        CellStyle originStyle = workbook.createCellStyle();
        originStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 原始值

        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);

        CellStyle result = StyleUtil.buildCellStyle(workbook, originStyle, writeCellStyle);

        assertEquals(HorizontalAlignment.LEFT, result.getAlignment());
        assertEquals(VerticalAlignment.CENTER, result.getVerticalAlignment());
        assertFalse(result.getHidden());
        assertTrue(result.getLocked());
    }

    @Test
    void testBuildCellStyle_withNullOriginStyle_shouldCreateNewStyle() {
        Workbook workbook = new XSSFWorkbook();
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setWrapped(true);
        writeCellStyle.setFillForegroundColor((short) 7);

        CellStyle result = StyleUtil.buildCellStyle(workbook, null, writeCellStyle);

        assertTrue(result.getWrapText());
        assertEquals((short) 7, result.getFillForegroundColor());
        assertFalse(result.getHidden());
        assertTrue(result.getLocked());
    }

    @Test
    void testBuildDataFormat_withNull_shouldReturnGeneral() {
        Workbook workbook = new HSSFWorkbook();
        short format = StyleUtil.buildDataFormat(workbook, null);
        assertEquals(BuiltinFormats.GENERAL, format);
    }

    @Test
    void testBuildDataFormat_withIndex_shouldReturnIndex() {
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex((short) 10);

        Workbook workbook = new HSSFWorkbook();
        short format = StyleUtil.buildDataFormat(workbook, dataFormatData);
        assertEquals(10, format);
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

        assertNotNull(font);
        assertEquals("Arial", font.getFontName());
        assertEquals(12, font.getFontHeightInPoints());
        assertTrue(font.getItalic());
        assertEquals(10, font.getColor());
        assertEquals(1, font.getTypeOffset());
        assertEquals(1, font.getUnderline());
        assertEquals(3, font.getCharSet());
        assertTrue(font.getBold());
    }

    @Test
    void testBuildRichTextString_withNull_shouldReturnNull() {
        assertNull(StyleUtil.buildRichTextString(writeWorkbookHolder, null));
    }

    @Test
    void testGetHyperlinkType_withNull_shouldReturnNone() {
        assertEquals(HyperlinkType.NONE, StyleUtil.getHyperlinkType(null));
    }

    @Test
    void testGetHyperlinkType_withUrl_shouldReturnUrl() {
        assertEquals(HyperlinkType.URL, StyleUtil.getHyperlinkType(HyperlinkData.HyperlinkType.URL));
    }

    @Test
    void testGetCoordinate_withNull_shouldReturnZero() {
        assertEquals(0, StyleUtil.getCoordinate(null));
    }

    @Test
    void testGetCoordinate_withValue_shouldConvertToEMU() {
        int coord = 100;
        int emu = StyleUtil.getCoordinate(coord);
        assertTrue(emu > 0);
    }

    @Test
    void testGetCellCoordinate_withAbsolute_shouldReturnAbsolute() {
        assertEquals(500, StyleUtil.getCellCoordinate(100, 500, 200));
    }

    @Test
    void testGetCellCoordinate_withRelative_shouldReturnRelativeAdded() {
        assertEquals(300, StyleUtil.getCellCoordinate(100, null, 200));
    }

    @Test
    void testGetCellCoordinate_withNull_shouldReturnCurrent() {
        assertEquals(100, StyleUtil.getCellCoordinate(100, null, null));
    }

    @Test
    void testSetIfNotNull_withNullValue_shouldNotInvokeSetter() {
        Consumer<String> setter = mock(Consumer.class);
        String value = null;
        StyleUtil.setIfNotNull(setter, value);
        verify(setter, never()).accept(any());
    }

    @Test
    void testSetIfNotNull_withNonNullValue_shouldInvokeSetter() {
        Consumer<String> setter = mock(Consumer.class);
        String value = "testValue";
        StyleUtil.setIfNotNull(setter, value);
        verify(setter).accept("testValue");
    }

    @Test
    void testSetIfNotNull_withIntegerValue_shouldInvokeSetter() {
        Consumer<Integer> setter = mock(Consumer.class);
        Integer value = 42;
        StyleUtil.setIfNotNull(setter, value);
        verify(setter).accept(42);
    }
}

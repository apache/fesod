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

import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.excel.constant.BuiltinFormats;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.fesod.excel.metadata.data.HyperlinkData;
import org.apache.fesod.excel.metadata.data.RichTextStringData;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.excel.write.metadata.style.WriteCellStyle;
import org.apache.fesod.excel.write.metadata.style.WriteFont;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 *
 */
@Slf4j
public class StyleUtil {

    private StyleUtil() {}

    /**
     * Build  cell style
     *
     * @param workbook
     * @param originCellStyle
     * @param writeCellStyle
     * @return
     */
    public static CellStyle buildCellStyle(
            Workbook workbook, CellStyle originCellStyle, WriteCellStyle writeCellStyle) {
        CellStyle cellStyle = workbook.createCellStyle();
        if (originCellStyle != null) {
            cellStyle.cloneStyleFrom(originCellStyle);
        }
        if (writeCellStyle == null) {
            return cellStyle;
        }
        buildCellStyle(cellStyle, writeCellStyle);
        return cellStyle;
    }

    private static void buildCellStyle(CellStyle cellStyle, WriteCellStyle writeCellStyle) {
        setIfNotNull(cellStyle::setHidden, writeCellStyle.getHidden());
        setIfNotNull(cellStyle::setLocked, writeCellStyle.getLocked());
        setIfNotNull(cellStyle::setQuotePrefixed, writeCellStyle.getQuotePrefix());
        setIfNotNull(cellStyle::setAlignment, writeCellStyle.getHorizontalAlignment());
        setIfNotNull(cellStyle::setWrapText, writeCellStyle.getWrapped());
        setIfNotNull(cellStyle::setVerticalAlignment, writeCellStyle.getVerticalAlignment());
        setIfNotNull(cellStyle::setRotation, writeCellStyle.getRotation());
        setIfNotNull(cellStyle::setIndention, writeCellStyle.getIndent());
        setIfNotNull(cellStyle::setBorderLeft, writeCellStyle.getBorderLeft());
        setIfNotNull(cellStyle::setBorderRight, writeCellStyle.getBorderRight());
        setIfNotNull(cellStyle::setBorderTop, writeCellStyle.getBorderTop());
        setIfNotNull(cellStyle::setBorderBottom, writeCellStyle.getBorderBottom());
        setIfNotNull(cellStyle::setLeftBorderColor, writeCellStyle.getLeftBorderColor());
        setIfNotNull(cellStyle::setRightBorderColor, writeCellStyle.getRightBorderColor());
        setIfNotNull(cellStyle::setTopBorderColor, writeCellStyle.getTopBorderColor());
        setIfNotNull(cellStyle::setBottomBorderColor, writeCellStyle.getBottomBorderColor());
        setIfNotNull(cellStyle::setFillPattern, writeCellStyle.getFillPatternType());
        setIfNotNull(cellStyle::setFillBackgroundColor, writeCellStyle.getFillBackgroundColor());
        setIfNotNull(cellStyle::setFillForegroundColor, writeCellStyle.getFillForegroundColor());
        setIfNotNull(cellStyle::setShrinkToFit, writeCellStyle.getShrinkToFit());
    }

    public static short buildDataFormat(Workbook workbook, DataFormatData dataFormatData) {
        if (dataFormatData == null) {
            return BuiltinFormats.GENERAL;
        }
        if (dataFormatData.getIndex() != null && dataFormatData.getIndex() >= 0) {
            return dataFormatData.getIndex();
        }
        if (StringUtils.isNotBlank(dataFormatData.getFormat())) {
            if (log.isDebugEnabled()) {
                log.debug("create new data format:{}", dataFormatData);
            }
            DataFormat dataFormatCreate = workbook.createDataFormat();
            return dataFormatCreate.getFormat(dataFormatData.getFormat());
        }
        return BuiltinFormats.GENERAL;
    }

    public static Font buildFont(Workbook workbook, Font originFont, WriteFont writeFont) {
        if (log.isDebugEnabled()) {
            log.debug("create new font:{},{}", writeFont, originFont);
        }
        if (writeFont == null && originFont == null) {
            return null;
        }
        Font font = createFont(workbook, originFont, writeFont);
        if (writeFont == null || font == null) {
            return font;
        }
        setIfNotNull(font::setFontName, writeFont.getFontName());
        setIfNotNull(font::setFontHeightInPoints, writeFont.getFontHeightInPoints());
        setIfNotNull(font::setItalic, writeFont.getItalic());
        setIfNotNull(font::setStrikeout, writeFont.getStrikeout());
        setIfNotNull(font::setColor, writeFont.getColor());
        setIfNotNull(font::setTypeOffset, writeFont.getTypeOffset());
        setIfNotNull(font::setUnderline, writeFont.getUnderline());
        setIfNotNull(font::setCharSet, writeFont.getCharset());
        setIfNotNull(font::setBold, writeFont.getBold());
        return font;
    }

    private static Font createFont(Workbook workbook, Font originFont, WriteFont writeFont) {
        Font font = workbook.createFont();
        if (originFont == null) {
            return font;
        }
        if (originFont instanceof XSSFFont) {
            XSSFFont xssfFont = (XSSFFont) font;
            XSSFFont xssfOriginFont = ((XSSFFont) originFont);
            xssfFont.setFontName(xssfOriginFont.getFontName());
            xssfFont.setFontHeightInPoints(xssfOriginFont.getFontHeightInPoints());
            xssfFont.setItalic(xssfOriginFont.getItalic());
            xssfFont.setStrikeout(xssfOriginFont.getStrikeout());
            // Colors cannot be overwritten
            if (writeFont == null || writeFont.getColor() == null) {
                xssfFont.setColor(Optional.of(xssfOriginFont)
                        .map(XSSFFont::getXSSFColor)
                        .map(XSSFColor::getRGB)
                        .map(rgb -> new XSSFColor(rgb, null))
                        .orElse(null));
            }
            xssfFont.setTypeOffset(xssfOriginFont.getTypeOffset());
            xssfFont.setUnderline(xssfOriginFont.getUnderline());
            xssfFont.setCharSet(xssfOriginFont.getCharSet());
            xssfFont.setBold(xssfOriginFont.getBold());
            return xssfFont;
        } else if (originFont instanceof HSSFFont) {
            HSSFFont hssfFont = (HSSFFont) font;
            HSSFFont hssfOriginFont = (HSSFFont) originFont;
            hssfFont.setFontName(hssfOriginFont.getFontName());
            hssfFont.setFontHeightInPoints(hssfOriginFont.getFontHeightInPoints());
            hssfFont.setItalic(hssfOriginFont.getItalic());
            hssfFont.setStrikeout(hssfOriginFont.getStrikeout());
            hssfFont.setColor(hssfOriginFont.getColor());
            hssfFont.setTypeOffset(hssfOriginFont.getTypeOffset());
            hssfFont.setUnderline(hssfOriginFont.getUnderline());
            hssfFont.setCharSet(hssfOriginFont.getCharSet());
            hssfFont.setBold(hssfOriginFont.getBold());
            return hssfFont;
        }
        return font;
    }

    public static RichTextString buildRichTextString(
            WriteWorkbookHolder writeWorkbookHolder, RichTextStringData richTextStringData) {
        if (richTextStringData == null) {
            return null;
        }
        RichTextString richTextString;
        if (writeWorkbookHolder.getExcelType() == ExcelTypeEnum.XLSX) {
            richTextString = new XSSFRichTextString(richTextStringData.getTextString());
        } else {
            richTextString = new HSSFRichTextString(richTextStringData.getTextString());
        }
        if (richTextStringData.getWriteFont() != null) {
            richTextString.applyFont(writeWorkbookHolder.createFont(richTextStringData.getWriteFont(), null, true));
        }
        if (CollectionUtils.isNotEmpty(richTextStringData.getIntervalFontList())) {
            for (RichTextStringData.IntervalFont intervalFont : richTextStringData.getIntervalFontList()) {
                richTextString.applyFont(
                        intervalFont.getStartIndex(),
                        intervalFont.getEndIndex(),
                        writeWorkbookHolder.createFont(intervalFont.getWriteFont(), null, true));
            }
        }
        return richTextString;
    }

    public static HyperlinkType getHyperlinkType(HyperlinkData.HyperlinkType hyperlinkType) {
        if (hyperlinkType == null) {
            return HyperlinkType.NONE;
        }
        return hyperlinkType.getValue();
    }

    public static int getCoordinate(Integer coordinate) {
        if (coordinate == null) {
            return 0;
        }
        return Units.toEMU(coordinate);
    }

    public static int getCellCoordinate(
            Integer currentCoordinate, Integer absoluteCoordinate, Integer relativeCoordinate) {
        if (absoluteCoordinate != null && absoluteCoordinate > 0) {
            return absoluteCoordinate;
        }
        if (relativeCoordinate != null) {
            return currentCoordinate + relativeCoordinate;
        }
        return currentCoordinate;
    }

    public static <T> void setIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}

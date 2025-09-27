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

package org.apache.fesod.excel.metadata.property;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.write.style.ContentFontStyle;
import org.apache.fesod.excel.annotation.write.style.HeadFontStyle;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Configuration from annotations
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class FontProperty {
    /**
     * The name for the font (i.e. Arial)
     */
    private String fontName;
    /**
     * Height in the familiar unit of measure - points
     */
    private Short fontHeightInPoints;
    /**
     * Whether to use italics or not
     */
    private Boolean italic;
    /**
     * Whether to use a strikeout horizontal line through the text or not
     */
    private Boolean strikeout;
    /**
     * The color for the font
     *
     * @see Font#COLOR_NORMAL
     * @see Font#COLOR_RED
     * @see HSSFPalette#getColor(short)
     * @see IndexedColors
     */
    private Short color;
    /**
     * Set normal, super or subscript.
     *
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    private Short typeOffset;
    /**
     * set type of text underlining to use
     *
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    private Byte underline;
    /**
     * Set character-set to use.
     *
     * @see FontCharset
     * @see Font#ANSI_CHARSET
     * @see Font#DEFAULT_CHARSET
     * @see Font#SYMBOL_CHARSET
     */
    private Integer charset;
    /**
     * Bold
     */
    private Boolean bold;

    public static FontProperty build(HeadFontStyle headFontStyle) {
        if (headFontStyle == null) {
            return null;
        }
        FontProperty styleProperty = new FontProperty();
        if (StringUtils.isNotBlank(headFontStyle.fontName())) {
            styleProperty.setFontName(headFontStyle.fontName());
        }
        if (headFontStyle.fontHeightInPoints() >= 0) {
            styleProperty.setFontHeightInPoints(headFontStyle.fontHeightInPoints());
        }
        styleProperty.setItalic(headFontStyle.italic().getBooleanValue());
        styleProperty.setStrikeout(headFontStyle.strikeout().getBooleanValue());
        if (headFontStyle.color() >= 0) {
            styleProperty.setColor(headFontStyle.color());
        }
        if (headFontStyle.typeOffset() >= 0) {
            styleProperty.setTypeOffset(headFontStyle.typeOffset());
        }
        if (headFontStyle.underline() >= 0) {
            styleProperty.setUnderline(headFontStyle.underline());
        }
        if (headFontStyle.charset() >= 0) {
            styleProperty.setCharset(headFontStyle.charset());
        }
        styleProperty.setBold(headFontStyle.bold().getBooleanValue());
        return styleProperty;
    }

    public static FontProperty build(ContentFontStyle contentFontStyle) {
        if (contentFontStyle == null) {
            return null;
        }
        FontProperty styleProperty = new FontProperty();
        if (StringUtils.isNotBlank(contentFontStyle.fontName())) {
            styleProperty.setFontName(contentFontStyle.fontName());
        }
        if (contentFontStyle.fontHeightInPoints() >= 0) {
            styleProperty.setFontHeightInPoints(contentFontStyle.fontHeightInPoints());
        }
        styleProperty.setItalic(contentFontStyle.italic().getBooleanValue());
        styleProperty.setStrikeout(contentFontStyle.strikeout().getBooleanValue());
        if (contentFontStyle.color() >= 0) {
            styleProperty.setColor(contentFontStyle.color());
        }
        if (contentFontStyle.typeOffset() >= 0) {
            styleProperty.setTypeOffset(contentFontStyle.typeOffset());
        }
        if (contentFontStyle.underline() >= 0) {
            styleProperty.setUnderline(contentFontStyle.underline());
        }
        if (contentFontStyle.charset() >= 0) {
            styleProperty.setCharset(contentFontStyle.charset());
        }
        styleProperty.setBold(contentFontStyle.bold().getBooleanValue());
        return styleProperty;
    }
}

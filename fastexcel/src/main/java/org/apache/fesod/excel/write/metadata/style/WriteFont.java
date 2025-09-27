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

package org.apache.fesod.excel.write.metadata.style;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Font when writing
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class WriteFont {
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

    /**
     * The source is not empty merge the data to the target.
     *
     * @param source source
     * @param target target
     */
    public static void merge(WriteFont source, WriteFont target) {
        if (source == null || target == null) {
            return;
        }
        if (StringUtils.isNotBlank(source.getFontName())) {
            target.setFontName(source.getFontName());
        }
        if (source.getFontHeightInPoints() != null) {
            target.setFontHeightInPoints(source.getFontHeightInPoints());
        }
        if (source.getItalic() != null) {
            target.setItalic(source.getItalic());
        }
        if (source.getStrikeout() != null) {
            target.setStrikeout(source.getStrikeout());
        }
        if (source.getColor() != null) {
            target.setColor(source.getColor());
        }
        if (source.getTypeOffset() != null) {
            target.setTypeOffset(source.getTypeOffset());
        }
        if (source.getUnderline() != null) {
            target.setUnderline(source.getUnderline());
        }
        if (source.getCharset() != null) {
            target.setCharset(source.getCharset());
        }
        if (source.getBold() != null) {
            target.setBold(source.getBold());
        }
    }

    @Override
    public WriteFont clone() {
        WriteFont writeFont = new WriteFont();
        writeFont.setFontName(getFontName());
        writeFont.setFontHeightInPoints(getFontHeightInPoints());
        writeFont.setItalic(getItalic());
        writeFont.setStrikeout(getStrikeout());
        writeFont.setColor(getColor());
        writeFont.setTypeOffset(getTypeOffset());
        writeFont.setUnderline(getUnderline());
        writeFont.setCharset(getCharset());
        writeFont.setBold(getBold());
        return writeFont;
    }
}

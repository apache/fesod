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

package org.apache.fesod.excel.metadata.csv;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * csv cell style
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CsvCellStyle implements CellStyle {

    /**
     * data format
     */
    private DataFormatData dataFormatData;

    /**
     * index
     */
    private Short index;

    public CsvCellStyle(Short index) {
        this.index = index;
    }

    @Override
    public short getIndex() {
        return index;
    }

    @Override
    public void setDataFormat(short fmt) {
        initDataFormatData();
        dataFormatData.setIndex(fmt);
    }

    private void initDataFormatData() {
        if (dataFormatData == null) {
            dataFormatData = new DataFormatData();
        }
    }

    @Override
    public short getDataFormat() {
        if (dataFormatData == null) {
            return 0;
        }
        return dataFormatData.getIndex();
    }

    @Override
    public String getDataFormatString() {
        if (dataFormatData == null) {
            return null;
        }
        return dataFormatData.getFormat();
    }

    @Override
    public void setFont(Font font) {}

    @Override
    public int getFontIndex() {
        return 0;
    }

    @Override
    public int getFontIndexAsInt() {
        return 0;
    }

    @Override
    public void setHidden(boolean hidden) {}

    @Override
    public boolean getHidden() {
        return false;
    }

    @Override
    public void setLocked(boolean locked) {}

    @Override
    public boolean getLocked() {
        return false;
    }

    @Override
    public void setQuotePrefixed(boolean quotePrefix) {}

    @Override
    public boolean getQuotePrefixed() {
        return false;
    }

    @Override
    public void setAlignment(HorizontalAlignment align) {}

    @Override
    public HorizontalAlignment getAlignment() {
        return null;
    }

    @Override
    public void setWrapText(boolean wrapped) {}

    @Override
    public boolean getWrapText() {
        return false;
    }

    @Override
    public void setVerticalAlignment(VerticalAlignment align) {}

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return null;
    }

    @Override
    public void setRotation(short rotation) {}

    @Override
    public short getRotation() {
        return 0;
    }

    @Override
    public void setIndention(short indent) {}

    @Override
    public short getIndention() {
        return 0;
    }

    @Override
    public void setBorderLeft(BorderStyle border) {}

    @Override
    public BorderStyle getBorderLeft() {
        return null;
    }

    @Override
    public void setBorderRight(BorderStyle border) {}

    @Override
    public BorderStyle getBorderRight() {
        return null;
    }

    @Override
    public void setBorderTop(BorderStyle border) {}

    @Override
    public BorderStyle getBorderTop() {
        return null;
    }

    @Override
    public void setBorderBottom(BorderStyle border) {}

    @Override
    public BorderStyle getBorderBottom() {
        return null;
    }

    @Override
    public void setLeftBorderColor(short color) {}

    @Override
    public short getLeftBorderColor() {
        return 0;
    }

    @Override
    public void setRightBorderColor(short color) {}

    @Override
    public short getRightBorderColor() {
        return 0;
    }

    @Override
    public void setTopBorderColor(short color) {}

    @Override
    public short getTopBorderColor() {
        return 0;
    }

    @Override
    public void setBottomBorderColor(short color) {}

    @Override
    public short getBottomBorderColor() {
        return 0;
    }

    @Override
    public void setFillPattern(FillPatternType fp) {}

    @Override
    public FillPatternType getFillPattern() {
        return null;
    }

    @Override
    public void setFillBackgroundColor(short bg) {}

    @Override
    public void setFillBackgroundColor(Color color) {}

    @Override
    public short getFillBackgroundColor() {
        return 0;
    }

    @Override
    public Color getFillBackgroundColorColor() {
        return null;
    }

    @Override
    public void setFillForegroundColor(short bg) {}

    @Override
    public void setFillForegroundColor(Color color) {}

    @Override
    public short getFillForegroundColor() {
        return 0;
    }

    @Override
    public Color getFillForegroundColorColor() {
        return null;
    }

    @Override
    public void cloneStyleFrom(CellStyle source) {}

    @Override
    public void setShrinkToFit(boolean shrinkToFit) {}

    @Override
    public boolean getShrinkToFit() {
        return false;
    }
}

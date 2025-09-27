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

package org.apache.fesod.excel.metadata.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Class representing data for writing to a cell in an Excel sheet.
 *
 * @param <T> The type of data being written to the cell.
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class WriteCellData<T> extends CellData<T> {

    /**
     * Only supported when writing. Represents the date value in the cell.
     * Associated with {@link CellDataTypeEnum#DATE}.
     */
    private LocalDateTime dateValue;

    /**
     * Represents rich text data in the cell.
     * Associated with {@link CellDataTypeEnum#RICH_TEXT_STRING}.
     */
    private RichTextStringData richTextStringDataValue;

    /**
     * List of image data associated with the cell.
     */
    private List<ImageData> imageDataList;

    /**
     * Comment data associated with the cell.
     */
    private CommentData commentData;

    /**
     * Hyperlink data associated with the cell.
     */
    private HyperlinkData hyperlinkData;

    /**
     * Style information for the cell.
     */
    private WriteCellStyle writeCellStyle;

    /**
     * If originCellStyle is empty, one will be created.
     * If both writeCellStyle and originCellStyle exist, copy from writeCellStyle to originCellStyle.
     */
    private CellStyle originCellStyle;

    /**
     * Constructor for creating a WriteCellData object with a string value.
     *
     * @param stringValue The string value to be written to the cell.
     */
    public WriteCellData(String stringValue) {
        this(CellDataTypeEnum.STRING, stringValue);
    }

    /**
     * Constructor for creating a WriteCellData object with a specified cell data type.
     *
     * @param type The type of data being written to the cell.
     */
    public WriteCellData(CellDataTypeEnum type) {
        super();
        setType(type);
    }

    /**
     * Constructor for creating a WriteCellData object with a specified cell data type and string value.
     *
     * @param type        The type of data being written to the cell.
     * @param stringValue The string value to be written to the cell.
     * @throws IllegalArgumentException If the type is not STRING or ERROR, or if the string value is null.
     */
    public WriteCellData(CellDataTypeEnum type, String stringValue) {
        super();
        if (type != CellDataTypeEnum.STRING && type != CellDataTypeEnum.ERROR) {
            throw new IllegalArgumentException("Only support CellDataTypeEnum.STRING and  CellDataTypeEnum.ERROR");
        }
        if (stringValue == null) {
            throw new IllegalArgumentException("StringValue can not be null");
        }
        setType(type);
        setStringValue(stringValue);
    }

    /**
     * Constructor for creating a WriteCellData object with a BigDecimal number value.
     *
     * @param numberValue The BigDecimal number value to be written to the cell.
     * @throws IllegalArgumentException If the number value is null.
     */
    public WriteCellData(BigDecimal numberValue) {
        super();
        if (numberValue == null) {
            throw new IllegalArgumentException("DoubleValue can not be null");
        }
        setType(CellDataTypeEnum.NUMBER);
        setNumberValue(numberValue);
    }

    /**
     * Constructor for creating a WriteCellData object with a boolean value.
     *
     * @param booleanValue The boolean value to be written to the cell.
     * @throws IllegalArgumentException If the boolean value is null.
     */
    public WriteCellData(Boolean booleanValue) {
        super();
        if (booleanValue == null) {
            throw new IllegalArgumentException("BooleanValue can not be null");
        }
        setType(CellDataTypeEnum.BOOLEAN);
        setBooleanValue(booleanValue);
    }

    /**
     * Constructor for creating a WriteCellData object with a Date value.
     *
     * @param dateValue The Date value to be written to the cell.
     * @throws IllegalArgumentException If the date value is null.
     */
    public WriteCellData(Date dateValue) {
        super();
        if (dateValue == null) {
            throw new IllegalArgumentException("DateValue can not be null");
        }
        setType(CellDataTypeEnum.DATE);
        this.dateValue = LocalDateTime.ofInstant(dateValue.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Constructor for creating a WriteCellData object with a LocalDateTime value.
     *
     * @param dateValue The LocalDateTime value to be written to the cell.
     * @throws IllegalArgumentException If the date value is null.
     */
    public WriteCellData(LocalDateTime dateValue) {
        super();
        if (dateValue == null) {
            throw new IllegalArgumentException("DateValue can not be null");
        }
        setType(CellDataTypeEnum.DATE);
        this.dateValue = dateValue;
    }

    /**
     * Constructor for creating a WriteCellData object with image data.
     *
     * @param image The byte array representing the image data.
     * @throws IllegalArgumentException If the image data is null.
     */
    public WriteCellData(byte[] image) {
        super();
        if (image == null) {
            throw new IllegalArgumentException("Image can not be null");
        }
        setType(CellDataTypeEnum.EMPTY);
        this.imageDataList = ListUtils.newArrayList();
        ImageData imageData = new ImageData();
        imageData.setImage(image);
        imageDataList.add(imageData);
    }

    /**
     * Returns or creates a new style for the cell.
     *
     * @return The WriteCellStyle object for the cell.
     */
    public WriteCellStyle getOrCreateStyle() {
        if (this.writeCellStyle == null) {
            this.writeCellStyle = new WriteCellStyle();
        }
        return this.writeCellStyle;
    }
}

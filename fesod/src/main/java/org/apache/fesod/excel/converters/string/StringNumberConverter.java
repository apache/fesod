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

package org.apache.fesod.excel.converters.string;

import java.math.BigDecimal;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.DateUtils;
import org.apache.fesod.excel.util.NumberDataFormatterUtils;
import org.apache.fesod.excel.util.NumberUtils;
import org.apache.fesod.excel.util.StringUtils;

/**
 * String and number converter
 *
 *
 */
public class StringNumberConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public String convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // If there are "DateTimeFormat", read as date
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            return DateUtils.format(
                    cellData.getNumberValue(),
                    contentProperty.getDateTimeFormatProperty().getUse1904windowing(),
                    contentProperty.getDateTimeFormatProperty().getFormat());
        }
        // If there are "NumberFormat", read as number
        if (contentProperty != null && contentProperty.getNumberFormatProperty() != null) {
            return NumberUtils.format(cellData.getNumberValue(), contentProperty);
        }
        // Excel defines formatting
        boolean hasDataFormatData = cellData.getDataFormatData() != null
                && cellData.getDataFormatData().getIndex() != null
                && !StringUtils.isEmpty(cellData.getDataFormatData().getFormat());
        if (hasDataFormatData) {
            return NumberDataFormatterUtils.format(
                    cellData.getNumberValue(),
                    cellData.getDataFormatData().getIndex(),
                    cellData.getDataFormatData().getFormat(),
                    globalConfiguration);
        }
        // Default conversion number
        return NumberUtils.format(cellData.getNumberValue(), contentProperty);
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(new BigDecimal(value));
    }
}

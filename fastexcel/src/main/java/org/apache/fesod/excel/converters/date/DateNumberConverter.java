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

package org.apache.fesod.excel.converters.date;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Date and number converter
 *
 *
 */
public class DateNumberConverter implements Converter<Date> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Date convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return DateUtils.getJavaDate(
                    cellData.getNumberValue().doubleValue(), globalConfiguration.getUse1904windowing());
        } else {
            return DateUtils.getJavaDate(
                    cellData.getNumberValue().doubleValue(),
                    contentProperty.getDateTimeFormatProperty().getUse1904windowing());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(
                    BigDecimal.valueOf(DateUtil.getExcelDate(value, globalConfiguration.getUse1904windowing())));
        } else {
            return new WriteCellData<>(BigDecimal.valueOf(DateUtil.getExcelDate(
                    value, contentProperty.getDateTimeFormatProperty().getUse1904windowing())));
        }
    }
}

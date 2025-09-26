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

package cn.idev.excel.converters.date;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.idev.excel.util.DateUtils;
import java.text.ParseException;
import java.util.Date;

/**
 * Date and string converter
 *
 *
 */
public class DateStringConverter implements Converter<Date> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws ParseException {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return DateUtils.parseDate(cellData.getStringValue(), null);
        } else {
            return DateUtils.parseDate(
                    cellData.getStringValue(),
                    contentProperty.getDateTimeFormatProperty().getFormat());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(DateUtils.format(value, null));
        } else {
            return new WriteCellData<>(DateUtils.format(
                    value, contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }
}

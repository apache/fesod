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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converters.offsetdatetime;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

/** OffsetDateTime and number converter. */
public class OffsetDateTimeNumberConverter implements Converter<OffsetDateTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return OffsetDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public OffsetDateTime convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        boolean use1904windowing = globalConfiguration.getUse1904windowing();
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            use1904windowing = contentProperty.getDateTimeFormatProperty().getUse1904windowing();
        }
        return DateUtils.getLocalDateTime(cellData.getNumberValue().doubleValue(), use1904windowing)
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            OffsetDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        boolean use1904windowing = globalConfiguration.getUse1904windowing();
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            use1904windowing = contentProperty.getDateTimeFormatProperty().getUse1904windowing();
        }
        return new WriteCellData<>(
                BigDecimal.valueOf(DateUtil.getExcelDate(value.toLocalDateTime(), use1904windowing)));
    }
}

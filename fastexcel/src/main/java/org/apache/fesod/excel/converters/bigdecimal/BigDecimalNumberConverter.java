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

package org.apache.fesod.excel.converters.bigdecimal;

import java.math.BigDecimal;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.NumberUtils;

/**
 * Converter for handling the conversion between BigDecimal and Excel number types.
 *
 *
 */
public class BigDecimalNumberConverter implements Converter<BigDecimal> {

    /**
     * Specifies the Java type supported by this converter.
     *
     * @return The class type of BigDecimal.
     */
    @Override
    public Class<BigDecimal> supportJavaTypeKey() {
        return BigDecimal.class;
    }

    /**
     * Specifies the Excel cell data type supported by this converter.
     *
     * @return The cell data type enumeration for numbers.
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    /**
     * Converts Excel cell data to a BigDecimal object.
     *
     * @param cellData               The Excel cell data to be converted.
     * @param contentProperty        The content property associated with the cell.
     * @param globalConfiguration    The global configuration for the conversion process.
     * @return                       The converted BigDecimal value from the cell data.
     */
    @Override
    public BigDecimal convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return cellData.getNumberValue();
    }

    /**
     * Converts a BigDecimal object to Excel cell data.
     *
     * @param value                  The BigDecimal value to be converted.
     * @param contentProperty        The content property associated with the cell.
     * @param globalConfiguration    The global configuration for the conversion process.
     * @return                       The WriteCellData object containing the formatted number.
     */
    @Override
    public WriteCellData<?> convertToExcelData(
            BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return NumberUtils.formatToCellData(value, contentProperty);
    }
}

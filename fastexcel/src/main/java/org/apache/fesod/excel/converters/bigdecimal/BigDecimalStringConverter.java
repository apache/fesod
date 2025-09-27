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
import java.text.ParseException;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.NumberUtils;

/**
 * Converter for handling the conversion between BigDecimal and Excel string types.
 *
 *
 */
public class BigDecimalStringConverter implements Converter<BigDecimal> {

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
     * @return The cell data type enumeration for strings.
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * Converts Excel cell data to a BigDecimal object.
     * This method parses the string value from the cell data into a BigDecimal using utility methods.
     *
     * @param cellData               The Excel cell data containing the string value.
     * @param contentProperty        The content property associated with the cell.
     * @param globalConfiguration    The global configuration for the conversion process.
     * @return                       The converted BigDecimal value from the string.
     * @throws ParseException        If there is an error parsing the string to a BigDecimal.
     */
    @Override
    public BigDecimal convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws ParseException {
        return NumberUtils.parseBigDecimal(cellData.getStringValue(), contentProperty);
    }

    /**
     * Converts a BigDecimal object to Excel cell data in string format.
     * This method formats the BigDecimal value into a string representation suitable for Excel cells.
     *
     * @param value                  The BigDecimal value to be converted.
     * @param contentProperty        The content property associated with the cell.
     * @param globalConfiguration    The global configuration for the conversion process.
     * @return                       The WriteCellData object containing the formatted string.
     */
    @Override
    public WriteCellData<?> convertToExcelData(
            BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return NumberUtils.formatToCellDataString(value, contentProperty);
    }
}

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

/**
 * BigDecimal and boolean converter
 *
 * This converter is responsible for converting between Java type BigDecimal and Excel's boolean type.
 * It treats Excel's true as BigDecimal.ONE, and false as BigDecimal.ZERO.
 *
 *
 */
public class BigDecimalBooleanConverter implements Converter<BigDecimal> {

    /**
     * Returns the Java type key supported by this converter.
     *
     * @return Returns the class type of BigDecimal.
     */
    @Override
    public Class<BigDecimal> supportJavaTypeKey() {
        return BigDecimal.class;
    }

    /**
     * Returns the Excel cell data type key supported by this converter.
     *
     * @return Returns the cell data type enumeration of boolean type.
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.BOOLEAN;
    }

    /**
     * Converts Excel cell data to Java type BigDecimal.
     * If the cell data represents true, returns BigDecimal.ONE, otherwise returns BigDecimal.ZERO.
     *
     * @param cellData               Excel cell data.
     * @param contentProperty        Excel content property.
     * @param globalConfiguration    Global configuration.
     * @return                       Returns the converted BigDecimal object.
     */
    @Override
    public BigDecimal convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (cellData.getBooleanValue()) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Converts Java type BigDecimal to Excel cell data.
     * If the value is BigDecimal.ONE, returns a WriteCellData containing true, otherwise returns a WriteCellData containing false.
     *
     * @param value                  Java type BigDecimal value.
     * @param contentProperty        Excel content property.
     * @param globalConfiguration    Global configuration.
     * @return                       Returns the converted Excel cell data.
     */
    @Override
    public WriteCellData<?> convertToExcelData(
            BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (BigDecimal.ONE.equals(value)) {
            return new WriteCellData<>(Boolean.TRUE);
        }
        return new WriteCellData<>(Boolean.FALSE);
    }
}

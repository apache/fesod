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

package org.apache.fesod.excel.converters;

import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;

/**
 * Convert between Java objects and excel objects
 */
public interface Converter<T> {

    /**
     * Back to object types in Java
     *
     * @return Support for Java class
     */
    default Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("The current operation is not supported by the current converter.");
    }

    /**
     * Back to object enum in excel
     *
     * @return Support for {@link CellDataTypeEnum}
     */
    default CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("The current operation is not supported by the current converter.");
    }

    /**
     * Convert excel objects to Java objects
     *
     * @param cellData            Excel cell data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return Data to put into a Java object
     * @throws Exception Exception.
     */
    default T convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws Exception {
        throw new UnsupportedOperationException("The current operation is not supported by the current converter.");
    }

    /**
     * Convert excel objects to Java objects
     *
     * @param context read converter context
     * @return Data to put into a Java object
     * @throws Exception Exception.
     */
    default T convertToJavaData(ReadConverterContext<?> context) throws Exception {
        return convertToJavaData(
                context.getReadCellData(),
                context.getContentProperty(),
                context.getAnalysisContext().currentReadHolder().globalConfiguration());
    }

    /**
     * Convert Java objects to excel objects
     *
     * @param value               Java Data.NotNull.
     * @param contentProperty     Content property.Nullable.
     * @param globalConfiguration Global configuration.NotNull.
     * @return Data to put into a Excel
     * @throws Exception Exception.
     */
    default WriteCellData<?> convertToExcelData(
            T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        throw new UnsupportedOperationException("The current operation is not supported by the current converter.");
    }

    /**
     * Convert Java objects to excel objects
     *
     * @param context write context
     * @return Data to put into a Excel
     * @throws Exception Exception.
     */
    default WriteCellData<?> convertToExcelData(WriteConverterContext<T> context) throws Exception {
        return convertToExcelData(
                context.getValue(),
                context.getContentProperty(),
                context.getWriteContext().currentWriteHolder().globalConfiguration());
    }
}

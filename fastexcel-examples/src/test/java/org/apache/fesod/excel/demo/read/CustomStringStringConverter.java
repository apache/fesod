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

package org.apache.fesod.excel.demo.read;

import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.converters.ReadConverterContext;
import org.apache.fesod.excel.converters.WriteConverterContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.data.WriteCellData;

/**
 * String and string converter
 *
 *
 */
public class CustomStringStringConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * This method is called when reading data from Excel.
     *
     * @param context The context containing the cell data read from Excel.
     * @return The converted Java data.
     */
    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        return "Custom：" + context.getReadCellData().getStringValue();
    }

    /**
     * This method is called when writing data to Excel.
     * (This is not relevant in this context and can be ignored.)
     *
     * @param context The context containing the Java data to be written.
     * @return The converted Excel data.
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>(context.getValue());
    }
}

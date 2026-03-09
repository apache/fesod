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

package org.apache.fesod.sheet.examples.advanced.converter;

import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * Custom String-to-String converter that adds a "Custom:" prefix during both read and write.
 *
 * <h2>Conversion Behavior</h2>
 * <pre>
 * Read:  Excel cell "Hello"  →  Java string "Custom:Hello"
 * Write: Java string "Hello" →  Excel cell "Custom:Hello"
 * </pre>
 *
 * <h2>Difference from Read Converter</h2>
 * <p>Unlike {@link org.apache.fesod.sheet.examples.read.converters.CustomStringStringConverter}
 * (which uses the newer {@code ReadConverterContext}/{@code WriteConverterContext} API),
 * this converter uses the legacy method signatures with
 * {@link ExcelContentProperty} and {@link GlobalConfiguration} parameters.
 * Both approaches are supported by Fesod.</p>
 *
 * <h2>API Versions</h2>
 * <table>
 *   <tr><th>Style</th><th>Method Signature</th><th>Used By</th></tr>
 *   <tr>
 *     <td>New (recommended)</td>
 *     <td>{@code convertToJavaData(ReadConverterContext)}</td>
 *     <td>read/converters/ package</td>
 *   </tr>
 *   <tr>
 *     <td>Legacy (still supported)</td>
 *     <td>{@code convertToJavaData(ReadCellData, ExcelContentProperty, GlobalConfiguration)}</td>
 *     <td>this class</td>
 *   </tr>
 * </table>
 *
 * @see Converter
 * @see org.apache.fesod.sheet.examples.advanced.CustomConverterExample
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

    @Override
    public String convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return "Custom:" + cellData.getStringValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>("Custom:" + value);
    }
}

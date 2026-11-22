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

package org.apache.fesod.sheet.converters;

import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * Converter for transforming Java objects into spreadsheet cell data.
 *
 * @param <T> the source Java type
 */
public interface WriteConverter<T> {

    /**
     * The Java type supported by this converter (non-null).
     */
    Class<?> supportJavaTypeKey();

    /**
     * Convert the given Java value into spreadsheet cell data.
     *
     * @param value               the source Java value (non-null)
     * @param contentProperty     optional content property (nullable)
     * @param globalConfiguration global configuration (non-null)
     * @return the converted spreadsheet cell data
     * @throws Exception if conversion fails
     */
    default WriteCellData<?> convertToExcelData(
            T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        throw new UnsupportedOperationException("The current operation is not supported by the current converter.");
    }

    /**
     * Convert the Java value into spreadsheet cell data using the given context.
     *
     * @param context the write conversion context
     * @return the converted spreadsheet cell data
     * @throws Exception if conversion fails
     */
    default WriteCellData<?> convertToExcelData(WriteConverterContext<T> context) throws Exception {
        return convertToExcelData(
                context.getValue(),
                context.getContentProperty(),
                context.getWriteContext().currentWriteHolder().globalConfiguration());
    }
}

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

package org.apache.fesod.sheet.converters.clob;

import java.io.Reader;
import java.sql.Clob;
import javax.sql.rowset.serial.SerialClob;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * Clob and string converter. Clob data is represented as a string cell; reads return a
 * {@link SerialClob} so callers receive a usable, detached JDBC Clob value.
 */
public class ClobStringConverter implements Converter<Clob> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Clob.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Clob convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws Exception {
        // Materialize cell text into a detached JDBC Clob, avoiding a database-specific Clob implementation.
        return new SerialClob(cellData.getStringValue().toCharArray());
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Clob value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws Exception {
        StringBuilder text = new StringBuilder();
        char[] buffer = new char[4096];
        try (Reader reader = value.getCharacterStream()) {
            int count;
            while ((count = reader.read(buffer)) != -1) {
                text.append(buffer, 0, count);
            }
        }
        return new WriteCellData<>(text.toString());
    }
}

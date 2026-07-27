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

package org.apache.fesod.sheet.read;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.read.listener.PageReadListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A converter registered on one {@link org.apache.fesod.sheet.ExcelReader} must not leak into a
 * later, unrelated read.
 */
public class ReadConverterIsolationTest {

    @Data
    public static class StringRow {
        private String value;
    }

    /** Appends a marker so leakage is observable. */
    public static class MarkerConverter implements Converter<String> {
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
                ReadCellData<?> cellData,
                ExcelContentProperty contentProperty,
                GlobalConfiguration globalConfiguration) {
            return cellData.getStringValue() + " [MARKER]";
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new WriteCellData<>(value);
        }
    }

    @Test
    void registeredConverterDoesNotLeakIntoLaterRead() throws Exception {
        File file = File.createTempFile("conv-iso", ".xlsx");
        file.deleteOnExit();
        StringRow out = new StringRow();
        out.setValue("hello");
        FesodSheet.write(file, StringRow.class).sheet().doWrite(Collections.singletonList(out));

        // First read: register the marker converter -> values carry the marker.
        List<StringRow> first = new ArrayList<>();
        FesodSheet.read(file, StringRow.class, new PageReadListener<StringRow>(first::addAll))
                .registerConverter(new MarkerConverter())
                .sheet()
                .doRead();
        Assertions.assertEquals(Collections.singletonList("hello [MARKER]"), values(first));

        // Second read: fresh reader, NO converter registered -> must NOT see the marker.
        List<StringRow> second = new ArrayList<>();
        FesodSheet.read(file, StringRow.class, new PageReadListener<StringRow>(second::addAll))
                .sheet()
                .doRead();
        Assertions.assertEquals(Collections.singletonList("hello"), values(second));
    }

    private static List<String> values(List<StringRow> rows) {
        List<String> out = new ArrayList<>();
        for (StringRow r : rows) {
            out.add(r.getValue());
        }
        return out;
    }
}

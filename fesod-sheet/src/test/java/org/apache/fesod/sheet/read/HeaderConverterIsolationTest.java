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
import java.util.Map;
import lombok.Data;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The header text used for {@code @ExcelProperty} name matching — and handed to
 * {@link AnalysisEventListener#invokeHeadMap} — must be resolved against the built-in read
 * converters only. A user-registered {@code Converter<String>} applies to data cells, never to
 * the header. See issue #1098.
 */
@Tag(Tags.READ)
class HeaderConverterIsolationTest {

    private static final String HEADER = "flag";
    private static final String CELL_VALUE = "abc";

    @Data
    public static class FlagRow {
        @ExcelProperty(HEADER)
        private String flag;
    }

    /** Explicit {@code STRING}-key converter: data cells are upper-cased. */
    public static class UpperCaseStringConverter implements Converter<String> {
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
            return cellData.getStringValue().toUpperCase();
        }
    }

    /** Wildcard variant: declares no excel type key ({@code supportExcelTypeKey() == null}). */
    public static class WildcardUpperCaseStringConverter extends UpperCaseStringConverter {
        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }
    }

    /** Captures both the header map handed to the listener and the mapped rows. */
    private static class CapturingListener extends AnalysisEventListener<FlagRow> {
        private Map<Integer, String> headMap;
        private final List<FlagRow> rows = new ArrayList<>();

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            this.headMap = headMap;
        }

        @Override
        public void invoke(FlagRow data, AnalysisContext context) {
            rows.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}
    }

    @Test
    void headerStaysRaw_soNameMatchingWorks_andTheDataCellIsStillConverted() throws Exception {
        File file = writeSample();

        CapturingListener listener = new CapturingListener();
        FesodSheet.read(file, FlagRow.class, listener)
                .registerConverter(new UpperCaseStringConverter())
                .sheet()
                .doRead();

        Assertions.assertEquals(1, listener.rows.size());
        // Before the fix the header is upper-cased to "FLAG", name matching fails silently and
        // this is null; the data cell itself is not converted either.
        Assertions.assertEquals("ABC", listener.rows.get(0).getFlag());
    }

    @Test
    void invokeHeadMapReceivesTheRawHeaderText() throws Exception {
        File file = writeSample();

        CapturingListener listener = new CapturingListener();
        FesodSheet.read(file, FlagRow.class, listener)
                .registerConverter(new UpperCaseStringConverter())
                .sheet()
                .doRead();

        Assertions.assertNotNull(listener.headMap);
        // Before the fix the listener sees "FLAG" — the header went through the user converter.
        Assertions.assertEquals("flag", listener.headMap.get(0));
    }

    @Test
    void wildcardStringConverter_doesNotAffectHeaderOrNameMatching() throws Exception {
        File file = writeSample();

        CapturingListener listener = new CapturingListener();
        FesodSheet.read(file, FlagRow.class, listener)
                .registerConverter(new WildcardUpperCaseStringConverter())
                .sheet()
                .doRead();

        // The header path never consults user registrations, wildcard or not, so name matching
        // keeps working. The cell text is deliberately not asserted: a wildcard read converter
        // does not reach data cells today (it is only expanded by the read-side change tracked in
        // #1086), so the value is "abc" now and would become "ABC" once that lands — the header
        // must stay raw in both cases.
        Assertions.assertEquals("flag", listener.headMap.get(0));
        Assertions.assertEquals(1, listener.rows.size());
        Assertions.assertNotNull(listener.rows.get(0).getFlag());
    }

    private static File writeSample() throws Exception {
        File file = File.createTempFile("header-converter-isolation", ".xlsx");
        file.deleteOnExit();
        FlagRow row = new FlagRow();
        row.setFlag(CELL_VALUE);
        FesodSheet.write(file, FlagRow.class).sheet().doWrite(Collections.singletonList(row));
        return file;
    }
}

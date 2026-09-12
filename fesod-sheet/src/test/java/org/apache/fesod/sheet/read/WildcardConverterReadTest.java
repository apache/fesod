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
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
@Slf4j
public class WildcardConverterReadTest extends AbstractExcelTest {

    private List<StringWriteData> dataList() {
        List<StringWriteData> dataList = new ArrayList<>();
        StringWriteData yesRow = new StringWriteData();
        yesRow.setFlag("yes");
        StringWriteData noRow = new StringWriteData();
        noRow.setFlag("no");
        dataList.add(yesRow);
        dataList.add(noRow);
        return dataList;
    }

    @Test
    public void testWildcardConverterAppliesOnRead() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        FesodSheet.write(file, StringWriteData.class).sheet().doWrite(dataList());

        List<BooleanReadData> rows = FesodSheet.read(file, BooleanReadData.class, new BooleanReadListener())
                .registerConverter(new BooleanYesNoReadConverter(null))
                .sheet()
                .doReadSync();

        Assertions.assertEquals(2, rows.size());
        Assertions.assertEquals(Boolean.TRUE, rows.get(0).getFlag());
        Assertions.assertEquals(Boolean.FALSE, rows.get(1).getFlag());
    }

    @Test
    public void testExplicitStringKeyConverterAppliesOnRead() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        FesodSheet.write(file, StringWriteData.class).sheet().doWrite(dataList());

        List<BooleanReadData> rows = FesodSheet.read(file, BooleanReadData.class, new BooleanReadListener())
                .registerConverter(new BooleanYesNoReadConverter(CellDataTypeEnum.STRING))
                .sheet()
                .doReadSync();

        Assertions.assertEquals(2, rows.size());
        Assertions.assertEquals(Boolean.TRUE, rows.get(0).getFlag());
        Assertions.assertEquals(Boolean.FALSE, rows.get(1).getFlag());
    }

    public static class BooleanYesNoReadConverter implements Converter<Boolean> {

        private final CellDataTypeEnum excelTypeKey;

        public BooleanYesNoReadConverter(CellDataTypeEnum excelTypeKey) {
            this.excelTypeKey = excelTypeKey;
        }

        @Override
        public Class<?> supportJavaTypeKey() {
            return Boolean.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            // null means the converter matches every cell type
            return excelTypeKey;
        }

        @Override
        public Boolean convertToJavaData(
                ReadCellData<?> cellData,
                ExcelContentProperty contentProperty,
                GlobalConfiguration globalConfiguration) {
            String value = cellData.getStringValue();
            return "yes".equalsIgnoreCase(value);
        }
    }

    @Test
    void testExplicitStringKeyConverterWinsOverLaterWildcardRegistration() throws Exception {
        File file = createTempFile(ExcelFormat.XLSX);
        FesodSheet.write(file, StringWriteData.class).sheet().doWrite(dataList());

        List<BooleanReadData> rows = FesodSheet.read(file, BooleanReadData.class, new BooleanReadListener())
                // explicit STRING key first, wildcard second: the explicit registration must keep
                // handling STRING cells regardless of registration order
                .registerConverter(new BooleanYesNoReadConverter(CellDataTypeEnum.STRING))
                .registerConverter(new AlwaysFalseReadConverter())
                .sheet()
                .doReadSync();

        Assertions.assertEquals(2, rows.size());
        Assertions.assertEquals(Boolean.TRUE, rows.get(0).getFlag());
        Assertions.assertEquals(Boolean.FALSE, rows.get(1).getFlag());
    }

    public static class AlwaysFalseReadConverter implements Converter<Boolean> {

        @Override
        public Class<?> supportJavaTypeKey() {
            return Boolean.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public Boolean convertToJavaData(
                ReadCellData<?> cellData,
                ExcelContentProperty contentProperty,
                GlobalConfiguration globalConfiguration) {
            return Boolean.FALSE;
        }
    }

    @Getter
    @Setter
    public static class StringWriteData {

        @ExcelProperty("flag")
        private String flag;
    }

    @Getter
    @Setter
    public static class BooleanReadData {

        @ExcelProperty("flag")
        private Boolean flag;
    }

    public static class BooleanReadListener extends AnalysisEventListener<BooleanReadData> {

        @Override
        public void invoke(BooleanReadData data, AnalysisContext context) {}

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}
    }
}

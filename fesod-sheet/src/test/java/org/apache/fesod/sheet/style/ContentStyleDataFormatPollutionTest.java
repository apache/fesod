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

package org.apache.fesod.sheet.style;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.style.HorizontalCellStyleStrategy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * A registered cell style strategy must not overwrite the cached {@link ContentStyle#dataFormat()}
 * of a field. Before the fix, {@code WriteCellStyle.merge} aliased the annotation's shared
 * {@link DataFormatData} into the per-cell style without cloning, so a later merge from another
 * style source mutated the cached annotation format in place and subsequent cells (or, with
 * {@link CacheLocationEnum#MEMORY}, subsequent writes) silently lost the annotation's number format.
 */
@Tag(Tags.ROUND_TRIP)
public class ContentStyleDataFormatPollutionTest extends AbstractExcelTest {

    private static final short STRATEGY_FORMAT = 2; // "0.00"
    private static final String ANNOTATION_FORMAT = "#,##0.00"; // builtin index 4

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnotationFormatData {
        @ExcelProperty("value")
        @ContentStyle(dataFormat = 4)
        private Double value;
    }

    /** Separate bean class so the static MEMORY cache of the other test cannot interfere. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnotationFormatMemoryData {
        @ExcelProperty("value")
        @ContentStyle(dataFormat = 4)
        private Double value;
    }

    /**
     * Content style list alternating between a style that defines a data format and one that does
     * not, so odd data rows must fall back to the annotation's format.
     */
    private static HorizontalCellStyleStrategy alternatingStrategy() {
        WriteCellStyle withFormat = new WriteCellStyle();
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(STRATEGY_FORMAT);
        withFormat.setDataFormatData(dataFormatData);
        WriteCellStyle withoutFormat = new WriteCellStyle();
        return new HorizontalCellStyleStrategy(null, Arrays.asList(withFormat, withoutFormat));
    }

    @Test
    void annotationFormatSurvivesStyleStrategyInSameWrite() throws Exception {
        File file = createTempFile("dataFormatPollution", ExcelFormat.XLSX);
        List<AnnotationFormatData> rows = Arrays.asList(
                new AnnotationFormatData(1111.5), new AnnotationFormatData(2222.5), new AnnotationFormatData(3333.5));

        FesodSheet.write(file, AnnotationFormatData.class)
                .registerWriteHandler(alternatingStrategy())
                .sheet()
                .doWrite(rows);

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(1)
                    .cell(0)
                    .hasDataFormat(STRATEGY_FORMAT)
                    .and()
                    .and()
                    // The strategy style for this row defines no format, so the
                    // @ContentStyle(dataFormat = 4) annotation must win.
                    .row(2)
                    .cell(0)
                    .hasDataFormatString(ANNOTATION_FORMAT)
                    .and()
                    .and()
                    .row(3)
                    .cell(0)
                    .hasDataFormat(STRATEGY_FORMAT);
        }
    }

    @Test
    void annotationFormatSurvivesAcrossWritesWithMemoryCache() throws Exception {
        List<AnnotationFormatMemoryData> rows =
                Arrays.asList(new AnnotationFormatMemoryData(1111.5), new AnnotationFormatMemoryData(2222.5));

        File first = createTempFile("dataFormatPollutionMemA", ExcelFormat.XLSX);
        FesodSheet.write(first, AnnotationFormatMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .registerWriteHandler(alternatingStrategy())
                .sheet()
                .doWrite(rows);

        // A completely separate write without any custom handler: every data row
        // must show the annotation's format.
        File second = createTempFile("dataFormatPollutionMemB", ExcelFormat.XLSX);
        FesodSheet.write(second, AnnotationFormatMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(rows);

        try (ExcelAssertions ea = ExcelAssertions.assertThat(second)) {
            ea.sheet(0)
                    .row(1)
                    .cell(0)
                    .hasDataFormatString(ANNOTATION_FORMAT)
                    .and()
                    .and()
                    .row(2)
                    .cell(0)
                    .hasDataFormatString(ANNOTATION_FORMAT);
        }
    }
}

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

package org.apache.fesod.sheet.read.listener;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * The documented {@code numRows} sentinel: zero means no row limit, so all rows are read, while a
 * positive limit still stops the read early.
 */
@Tag(Tags.ROUND_TRIP)
public class NumRowsReadLimitTest extends AbstractExcelTest {

    private static final int TOTAL_ROWS = 3;

    @ParameterizedTest
    @ExcelFormatSource
    void numRowsZeroReadsAllRowsLikeNoLimit(ExcelFormat format) throws IOException {
        File file = createTempFile(format);
        writeHeadless(file);

        Assertions.assertEquals(TOTAL_ROWS, readRows(file, 0));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void omittedNumRowsReadsAllRows(ExcelFormat format) throws IOException {
        File file = createTempFile(format);
        writeHeadless(file);

        Assertions.assertEquals(TOTAL_ROWS, readRows(file, null));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void positiveNumRowsStillStopsEarly(ExcelFormat format) throws IOException {
        File file = createTempFile(format);
        writeHeadless(file);

        Assertions.assertEquals(1, readRows(file, 1));
    }

    /**
     * Writes rows without a header row, so the numRows limit maps one-to-one onto data rows
     * regardless of the head row numbering.
     */
    private void writeHeadless(File file) {
        List<List<String>> rows = new java.util.ArrayList<>();
        for (int i = 1; i <= TOTAL_ROWS; i++) {
            rows.add(Arrays.asList("row-" + i));
        }
        FesodSheet.write(file).sheet().doWrite(rows);
    }

    private int readRows(File file, Integer numRows) {
        CollectingReadListener<RowData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, RowData.class, listener)
                .sheet()
                .headRowNumber(0)
                .numRows(numRows)
                .doRead();
        return listener.getRows().size();
    }

    @Getter
    @Setter
    public static class RowData {

        @ExcelProperty("name")
        private String name;
    }
}

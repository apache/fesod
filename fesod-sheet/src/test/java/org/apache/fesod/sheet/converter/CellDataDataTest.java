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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converter;

import java.io.File;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Parameterized read/write round-trip tests for CellData typed cells.
 */
@Tag(Tags.ROUND_TRIP)
public class CellDataDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, CellDataWriteData.class).sheet().doWrite(TestDataBuilder.cellDataWriteData());

        CollectingReadListener<CellDataReadData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, CellDataReadData.class, listener).sheet().doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        CellDataReadData row = listener.getFirstRow();

        Assertions.assertEquals("2020年01月01日", row.getDate().getData());
        Assertions.assertEquals(2L, (long) row.getInteger1().getData());
        Assertions.assertEquals(2L, (long) row.getInteger2());
        if (format.toExcelTypeEnum() != ExcelTypeEnum.CSV) {
            Assertions.assertEquals(
                    "B2+C2", row.getFormulaValue().getFormulaData().getFormulaValue());
        } else {
            Assertions.assertNull(row.getFormulaValue().getData());
        }
    }
}

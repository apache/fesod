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

package org.apache.fesod.sheet.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Verifies that recycling the {@code CellWriteHandlerContext} across the cells of a real write
 * leaves every handler callback observing exactly the state of its own cell.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.WRITE)
class CellWriteHandlerContextRecycleWriteTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void recycledContextShowsCorrectStatePerCell(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        RecordingCellWriteHandler handler = new RecordingCellWriteHandler();
        FesodSheet.write(file)
                .head(SimpleData.class)
                .includeColumnFieldNames(Collections.singletonList("name"))
                .registerWriteHandler(handler)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(2));

        Assertions.assertEquals(
                Arrays.asList("0:0:true:null", "1:0:false:Name0", "2:0:false:Name1"), handler.snapshots);
        // Header and data cells all went through one recycled instance.
        Assertions.assertEquals(1, handler.instances.size());
    }

    /**
     * Captures what a handler sees at {@code afterCellDispose}: the cell coordinates, whether it
     * is a header cell, and the not-yet-converted original value.
     */
    private static final class RecordingCellWriteHandler implements CellWriteHandler {

        private final List<String> snapshots = new ArrayList<>();

        private final Set<CellWriteHandlerContext> instances = Collections.newSetFromMap(new IdentityHashMap<>());

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            instances.add(context);
            snapshots.add(context.getRowIndex() + ":" + context.getColumnIndex() + ":"
                    + Boolean.TRUE.equals(context.getHead()) + ":" + context.getOriginalValue());
        }
    }
}

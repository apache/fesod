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

package org.apache.fesod.sheet.readwrite;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.write.handler.EscapeHexCellWriteHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Regression test for <a href="https://github.com/apache/fesod/issues/696">issue #696</a>: the {@code _xHHHH_}
 * escapes were undone only for cells backed by {@code sharedStrings.xml}, so an inline
 * string - what the default writer emits - reached the caller with the raw escape.
 */
@Tag(Tags.ROUND_TRIP)
class HexEscapeRoundTripTest extends AbstractExcelTest {

    /**
     * The handler is what puts a real escape in the cell, storing the literal as {@code Product_x005F_x0002_Code}.
     * Taking it from the writer's own output instead would tie the expectation to a writer default, not to the
     * reader under test.
     */
    @Test
    void escapedOnWrite_readsBackAsTheLiteral() throws IOException {
        SimpleData data = new SimpleData();
        data.setName("Product_x0002_Code");
        File file = createTempFile("hex-escape", ExcelFormat.XLSX);
        FesodSheet.write(file, SimpleData.class)
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .sheet()
                .doWrite(Collections.singletonList(data));

        CollectingReadListener<SimpleData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SimpleData.class, listener).sheet().doRead();
        List<SimpleData> rows = listener.getRows();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("Product_x0002_Code", rows.get(0).getName());
    }
}

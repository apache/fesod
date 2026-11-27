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

package org.apache.fesod.sheet.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The row index carried by row handler contexts must match the physical row being written: consecutive head rows see
 * consecutive indexes, and data rows continue after the head.
 */
@Tag(Tags.WRITE)
public class HeadRowHandlerContextTest extends AbstractExcelTest {

    @Test
    public void rowHandlerSeesConsecutiveIndexesAcrossHeadAndData() throws Exception {
        List<Integer> headIndexes = new ArrayList<>();
        List<Integer> dataIndexes = new ArrayList<>();

        File file = createTempFile("headRowHandlerIndex", org.apache.fesod.sheet.testkit.enums.ExcelFormat.XLSX);
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("c0r0", "c0r1"));
        head.add(Arrays.asList("c1r0", "c1r1"));
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("v0", "v1"));
        data.add(Arrays.asList("w0", "w1"));

        FesodSheet.write(file)
                .head(head)
                .sheet()
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowDispose(RowWriteHandlerContext context) {
                        if (Boolean.TRUE.equals(context.getHead())) {
                            headIndexes.add(context.getRowIndex());
                        } else {
                            dataIndexes.add(context.getRowIndex());
                        }
                    }
                })
                .doWrite(data);

        Assertions.assertEquals(Arrays.asList(0, 1), headIndexes, "head rows must report their physical row indexes");
        Assertions.assertEquals(Arrays.asList(2, 3), dataIndexes, "data rows continue after the head rows");
    }
}

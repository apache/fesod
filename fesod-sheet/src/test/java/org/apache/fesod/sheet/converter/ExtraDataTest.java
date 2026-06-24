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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.enums.CellExtraTypeEnum;
import org.apache.fesod.sheet.metadata.CellExtra;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests verifying handling of extra (unexpected) columns during read.
 */
@Tag(Tags.ROUND_TRIP)
public class ExtraDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void read(ExcelFormat format) {
        File file = readFile("extra" + File.separator + "extra" + format.getExtension());
        ExtraDataListener listener = new ExtraDataListener();
        FesodSheet.read(file, ExtraData.class, listener)
                .extraRead(CellExtraTypeEnum.COMMENT)
                .extraRead(CellExtraTypeEnum.HYPERLINK)
                .extraRead(CellExtraTypeEnum.MERGE)
                .sheet()
                .doRead();

        List<CellExtra> extras = listener.getExtras();
        for (CellExtra extra : extras) {
            switch (extra.getType()) {
                case COMMENT:
                    assertEquals("批注的内容", extra.getText());
                    assertEquals(4, (int) extra.getRowIndex());
                    assertEquals(0, (int) extra.getColumnIndex());
                    break;
                case HYPERLINK:
                    if ("Sheet1!A1".equals(extra.getText())) {
                        assertEquals(1, (int) extra.getRowIndex());
                        assertEquals(0, (int) extra.getColumnIndex());
                    } else if ("Sheet2!A1".equals(extra.getText())) {
                        assertEquals(2, (int) extra.getFirstRowIndex());
                        assertEquals(0, (int) extra.getFirstColumnIndex());
                        assertEquals(3, (int) extra.getLastRowIndex());
                        assertEquals(1, (int) extra.getLastColumnIndex());
                    } else {
                        fail("Unknown hyperlink!");
                    }
                    break;
                case MERGE:
                    assertEquals(5, (int) extra.getFirstRowIndex());
                    assertEquals(0, (int) extra.getFirstColumnIndex());
                    assertEquals(6, (int) extra.getLastRowIndex());
                    assertEquals(1, (int) extra.getLastColumnIndex());
                    break;
                default:
            }
        }
    }

    @Test
    void readExtraRelationships() {
        File extraRelationships = readFile("extra" + File.separator + "extraRelationships.xlsx");
        FesodSheet.read(extraRelationships, ExtraData.class, new ReadListener() {
                    @Override
                    public void invoke(Object data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}

                    @Override
                    public void extra(CellExtra extra, AnalysisContext context) {
                        switch (extra.getType()) {
                            case HYPERLINK:
                                if ("222222222".equals(extra.getText())) {
                                    assertEquals(1, (int) extra.getRowIndex());
                                    assertEquals(0, (int) extra.getColumnIndex());
                                } else if ("333333333333".equals(extra.getText())) {
                                    assertEquals(1, (int) extra.getRowIndex());
                                    assertEquals(1, (int) extra.getColumnIndex());
                                } else {
                                    fail("Unknown hyperlink!");
                                }
                                break;
                            default:
                        }
                    }
                })
                .extraRead(CellExtraTypeEnum.HYPERLINK)
                .sheet()
                .doRead();
    }
}

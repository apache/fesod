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

package org.apache.fesod.excel.extra;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 */
@Slf4j
public class ExtraDataTest {
    private static File file03;
    private static File file07;

    private static File extraRelationships;

    @BeforeAll
    public static void init() {
        file03 = TestFileUtil.readFile("extra" + File.separator + "extra.xls");
        file07 = TestFileUtil.readFile("extra" + File.separator + "extra.xlsx");
        extraRelationships = TestFileUtil.readFile("extra" + File.separator + "extraRelationships.xlsx");
    }

    @Test
    public void t01Read07() {
        read(file07);
    }

    @Test
    public void t02Read03() {
        read(file03);
    }

    @Test
    public void t03Read() {
        FastExcel.read(extraRelationships, ExtraData.class, new ReadListener() {
                    @Override
                    public void invoke(Object data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}

                    @Override
                    public void extra(CellExtra extra, AnalysisContext context) {
                        log.info("extra data:{}", JSON.toJSONString(extra));
                        switch (extra.getType()) {
                            case HYPERLINK:
                                if ("222222222".equals(extra.getText())) {
                                    Assertions.assertEquals(1, (int) extra.getRowIndex());
                                    Assertions.assertEquals(0, (int) extra.getColumnIndex());
                                } else if ("333333333333".equals(extra.getText())) {
                                    Assertions.assertEquals(1, (int) extra.getRowIndex());
                                    Assertions.assertEquals(1, (int) extra.getColumnIndex());
                                } else {
                                    Assertions.fail("Unknown hyperlink!");
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

    private void read(File file) {
        FastExcel.read(file, ExtraData.class, new ExtraDataListener())
                .extraRead(CellExtraTypeEnum.COMMENT)
                .extraRead(CellExtraTypeEnum.HYPERLINK)
                .extraRead(CellExtraTypeEnum.MERGE)
                .sheet()
                .doRead();
    }
}

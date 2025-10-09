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
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.metadata.CellExtra;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class ExtraDataListener extends AnalysisEventListener<ExtraData> {

    @Override
    public void invoke(ExtraData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("extra data:{}", JSON.toJSONString(extra));
        switch (extra.getType()) {
            case COMMENT:
                Assertions.assertEquals("批注的内容", extra.getText());
                Assertions.assertEquals(4, (int) extra.getRowIndex());
                Assertions.assertEquals(0, (int) extra.getColumnIndex());
                break;
            case HYPERLINK:
                if ("Sheet1!A1".equals(extra.getText())) {
                    Assertions.assertEquals(1, (int) extra.getRowIndex());
                    Assertions.assertEquals(0, (int) extra.getColumnIndex());
                } else if ("Sheet2!A1".equals(extra.getText())) {
                    Assertions.assertEquals(2, (int) extra.getFirstRowIndex());
                    Assertions.assertEquals(0, (int) extra.getFirstColumnIndex());
                    Assertions.assertEquals(3, (int) extra.getLastRowIndex());
                    Assertions.assertEquals(1, (int) extra.getLastColumnIndex());
                } else {
                    Assertions.fail("Unknown hyperlink!");
                }
                break;
            case MERGE:
                Assertions.assertEquals(5, (int) extra.getFirstRowIndex());
                Assertions.assertEquals(0, (int) extra.getFirstColumnIndex());
                Assertions.assertEquals(6, (int) extra.getLastRowIndex());
                Assertions.assertEquals(1, (int) extra.getLastColumnIndex());
                break;
            default:
        }
    }
}

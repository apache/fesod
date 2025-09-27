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

package org.apache.fesod.excel.demo.read;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.junit.jupiter.api.Assertions;

/**
 * Listener to read cell comments, hyperlinks, and merged cells.
 *
 *
 **/
@Slf4j
public class DemoExtraListener implements ReadListener<DemoExtraData> {

    @Override
    public void invoke(DemoExtraData data, AnalysisContext context) {}

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("Read an extra piece of information: {}", JSON.toJSONString(extra));
        switch (extra.getType()) {
            case COMMENT:
                log.info(
                        "The extra information is a comment, at rowIndex:{}, columnIndex:{}, content:{}",
                        extra.getRowIndex(),
                        extra.getColumnIndex(),
                        extra.getText());
                break;
            case HYPERLINK:
                if ("Sheet1!A1".equals(extra.getText())) {
                    log.info(
                            "The extra information is a hyperlink, at rowIndex:{}, columnIndex:{}, content:{}",
                            extra.getRowIndex(),
                            extra.getColumnIndex(),
                            extra.getText());
                } else if ("Sheet2!A1".equals(extra.getText())) {
                    log.info(
                            "The extra information is a hyperlink, covering a range, firstRowIndex:{}, firstColumnIndex:{}, "
                                    + "lastRowIndex:{}, lastColumnIndex:{}, content:{}",
                            extra.getFirstRowIndex(),
                            extra.getFirstColumnIndex(),
                            extra.getLastRowIndex(),
                            extra.getLastColumnIndex(),
                            extra.getText());
                } else {
                    Assertions.fail("Unknown hyperlink!");
                }
                break;
            case MERGE:
                log.info(
                        "The extra information is a merged cell, covering a range, firstRowIndex:{}, firstColumnIndex:{}, "
                                + "lastRowIndex:{}, lastColumnIndex:{}",
                        extra.getFirstRowIndex(),
                        extra.getFirstColumnIndex(),
                        extra.getLastRowIndex(),
                        extra.getLastColumnIndex());
                break;
            default:
        }
    }
}

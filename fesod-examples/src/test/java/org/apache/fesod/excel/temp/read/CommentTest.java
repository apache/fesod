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

package org.apache.fesod.excel.temp.read;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CommentTest {

    private final List<String> commentList = Arrays.asList("测试", "comment");

    private void runCommentTest(String filePath, ExcelTypeEnum excelType) throws Exception {
        File file = new File(filePath);
        FastExcel.read(file, new ReadListener() {
                    @Override
                    public void invoke(Object data, AnalysisContext context) {
                        // 当前测试不关心数据读取
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        // 当前测试不关心读取完成后的逻辑
                    }

                    @Override
                    public void extra(CellExtra extra, AnalysisContext context) {
                        log.info("读取到了一条额外信息:{}", JSON.toJSONString(extra));
                        if (extra.getType().equals(CellExtraTypeEnum.COMMENT)) {
                            Assertions.assertTrue(commentList.contains(extra.getText()));
                        }
                    }
                })
                .excelType(excelType)
                .extraRead(CellExtraTypeEnum.COMMENT)
                .sheet()
                .doRead();
    }

    @Test
    public void xlsxCommentTest() throws Exception {
        runCommentTest("src/test/resources/comment/comment.xlsx", ExcelTypeEnum.XLSX);
    }

    @Test
    public void xlsCommentTest() throws Exception {
        runCommentTest("src/test/resources/comment/comment.xls", ExcelTypeEnum.XLS);
    }
}

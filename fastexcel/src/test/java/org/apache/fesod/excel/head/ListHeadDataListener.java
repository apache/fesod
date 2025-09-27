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

package org.apache.fesod.excel.head;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class ListHeadDataListener implements ReadListener<Map<Integer, String>> {

    List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Assertions.assertNotNull(context.readRowHolder().getRowIndex());
        headMap.forEach((key, value) -> {
            Assertions.assertEquals(value.getRowIndex(), context.readRowHolder().getRowIndex());
            Assertions.assertEquals(value.getColumnIndex(), key);
        });
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(list.size(), 1);
        Map<Integer, String> data = list.get(0);
        Assertions.assertEquals("字符串0", data.get(0));
        Assertions.assertEquals("1", data.get(1));
        Assertions.assertEquals("2020-01-01 01:01:01", data.get(2));
        Assertions.assertEquals("额外数据", data.get(3));
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}

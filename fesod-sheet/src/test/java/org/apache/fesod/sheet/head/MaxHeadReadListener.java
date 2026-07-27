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

package org.apache.fesod.sheet.head;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;

@Slf4j
public class MaxHeadReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private Map<Integer, String> headTitleMap;

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        // Data validation stays in the test; this listener only captures head metadata.
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("origin head : {}", JSON.toJSONString(headTitleMap, JSONWriter.Feature.WriteMapNullValue));
        log.info("max not empty head size : {}", context.readSheetHolder().getMaxNotEmptyDataHeadSize());
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headTitleMap = headMap;
    }
}

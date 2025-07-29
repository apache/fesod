/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.idev.excel.test.core.head;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxHeadReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final Logger log = LoggerFactory.getLogger(MaxHeadReadListener.class);
    List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
    private List<Map<Integer, String>> headList = new ArrayList<>();
    private Map<Integer, String> headTitleMap = new HashMap<>();
    private int headSize;

    public MaxHeadReadListener(int headSize) {
        this.headSize = headSize;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("origin head : {}", JSON.toJSONString(headTitleMap, JSONWriter.Feature.WriteMapNullValue));
        log.info("max not empty head size : {}", context.readSheetHolder().getMaxNotEmptyDataHeadSize());
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headTitleMap = headMap;
        headList.add(headMap);
    }
}

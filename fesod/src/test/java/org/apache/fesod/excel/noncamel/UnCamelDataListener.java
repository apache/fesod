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

package org.apache.fesod.excel.noncamel;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class UnCamelDataListener extends AnalysisEventListener<UnCamelData> {
    List<UnCamelData> list = new ArrayList<>();

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.debug("Head is:{}", JSON.toJSONString(headMap));
        Assertions.assertEquals(headMap.get(0), "string1");
        Assertions.assertEquals(headMap.get(1), "string2");
        Assertions.assertEquals(headMap.get(2), "STring3");
        Assertions.assertEquals(headMap.get(3), "STring4");
        Assertions.assertEquals(headMap.get(4), "STRING5");
        Assertions.assertEquals(headMap.get(5), "STRing6");
    }

    @Override
    public void invoke(UnCamelData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(list.size(), 10);
        UnCamelData unCamelData = list.get(0);
        Assertions.assertEquals(unCamelData.getString1(), "string1");
        Assertions.assertEquals(unCamelData.getString2(), "string2");
        Assertions.assertEquals(unCamelData.getSTring3(), "string3");
        Assertions.assertEquals(unCamelData.getSTring4(), "string4");
        Assertions.assertEquals(unCamelData.getSTRING5(), "string5");
        Assertions.assertEquals(unCamelData.getSTRing6(), "string6");
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}

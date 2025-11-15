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

package org.apache.fesod.sheet.noncamel;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
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
        Assertions.assertEquals("string1", headMap.get(0));
        Assertions.assertEquals("string2", headMap.get(1));
        Assertions.assertEquals("STring3", headMap.get(2));
        Assertions.assertEquals("STring4", headMap.get(3));
        Assertions.assertEquals("STRING5", headMap.get(4));
        Assertions.assertEquals("STRing6", headMap.get(5));
    }

    @Override
    public void invoke(UnCamelData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(10, list.size());
        UnCamelData unCamelData = list.get(0);
        Assertions.assertEquals("string1", unCamelData.getString1());
        Assertions.assertEquals("string2", unCamelData.getString2());
        Assertions.assertEquals("string3", unCamelData.getSTring3());
        Assertions.assertEquals("string4", unCamelData.getSTring4());
        Assertions.assertEquals("string5", unCamelData.getSTRING5());
        Assertions.assertEquals("string6", unCamelData.getSTRing6());
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}

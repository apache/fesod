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

package org.apache.fesod.excel.parameter;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class ParameterDataListener extends AnalysisEventListener<ParameterData> {

    List<ParameterData> list = new ArrayList<ParameterData>();

    @Override
    public void invoke(ParameterData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(list.size(), 10);
        Assertions.assertEquals(list.get(0).getName(), "姓名0");
        Assertions.assertEquals((int) (context.readSheetHolder().getSheetNo()), 0);
        Assertions.assertEquals(
                context.readSheetHolder()
                        .getExcelReadHeadProperty()
                        .getHeadMap()
                        .get(0)
                        .getHeadNameList()
                        .get(0),
                "姓名");
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}

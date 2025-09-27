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

package org.apache.fesod.excel.multiplesheets;

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
public class MultipleSheetsListener extends AnalysisEventListener<MultipleSheetsData> {

    List<MultipleSheetsData> list = new ArrayList<MultipleSheetsData>();

    @Override
    public void invoke(MultipleSheetsData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("A form is read finished.");
        Assertions.assertEquals(list.get(0).getTitle(), "表1数据");
        log.debug("All row:{}", JSON.toJSONString(list));
    }

    public List<MultipleSheetsData> getList() {
        return list;
    }

    public void setList(List<MultipleSheetsData> list) {
        this.list = list;
    }
}

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

package org.apache.fesod.excel.sort;

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
public class SortDataListener extends AnalysisEventListener<SortData> {

    List<SortData> list = new ArrayList<SortData>();

    @Override
    public void invoke(SortData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(list.size(), 1);
        SortData sortData = list.get(0);
        Assertions.assertEquals("column1", sortData.getColumn1());
        Assertions.assertEquals("column2", sortData.getColumn2());
        Assertions.assertEquals("column3", sortData.getColumn3());
        Assertions.assertEquals("column4", sortData.getColumn4());
        Assertions.assertEquals("column5", sortData.getColumn5());
        Assertions.assertEquals("column6", sortData.getColumn6());
    }
}

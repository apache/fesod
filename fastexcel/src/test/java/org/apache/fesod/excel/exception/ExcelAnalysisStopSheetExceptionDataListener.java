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

package org.apache.fesod.excel.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.MapUtils;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Getter
@Slf4j
public class ExcelAnalysisStopSheetExceptionDataListener extends AnalysisEventListener<ExceptionData> {

    private Map<Integer, List<String>> dataMap = MapUtils.newHashMap();

    @Override
    public void invoke(ExceptionData data, AnalysisContext context) {
        List<String> sheetDataList =
                dataMap.computeIfAbsent(context.readSheetHolder().getSheetNo(), key -> ListUtils.newArrayList());
        sheetDataList.add(data.getName());
        if (sheetDataList.size() >= 5) {
            throw new ExcelAnalysisStopSheetException();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        List<String> sheetDataList = dataMap.get(context.readSheetHolder().getSheetNo());
        Assertions.assertNotNull(sheetDataList);
        Assertions.assertEquals(5, sheetDataList.size());
    }
}

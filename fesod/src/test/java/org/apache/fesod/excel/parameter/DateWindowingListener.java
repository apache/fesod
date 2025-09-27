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
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class DateWindowingListener extends AnalysisEventListener<ParameterData> {

    private Boolean expectFlag = Boolean.FALSE;

    private DateWindowingListener() {}

    public DateWindowingListener(Boolean expectFlag) {
        this.expectFlag = expectFlag == null ? Boolean.FALSE : expectFlag;
    }

    @Override
    public void invoke(ParameterData data, AnalysisContext context) {
        log.info("row data:{}", JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        boolean isUse1904windowing =
                context.readWorkbookHolder().globalConfiguration().getUse1904windowing();
        log.info(
                "excel type:{},isUse1904windowing: {}",
                context.readWorkbookHolder().getExcelType(),
                isUse1904windowing);
        Assertions.assertEquals(isUse1904windowing, this.expectFlag);
    }
}

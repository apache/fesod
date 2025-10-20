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

package org.apache.fesod.excel.demo.read;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.listener.ReadListener;

/**
 * Il8nHeadDemoDataListener
 *
 * @author GGBOUD
 * @date 2025/10/20
 */
@Slf4j
public class Il8nHeadDemoDataListener implements ReadListener<Il8nZhHeadDemoData> {

    public Il8nHeadDemoDataListener() {

    }

    @Override
    public void invoke(Il8nZhHeadDemoData data, AnalysisContext context) {
        log.info("Parsed one row of data: {}", JSON.toJSONString(data));
    }

    /**
     * doAfterAllAnalysed
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All data has been parsed!");
    }


}

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

package org.apache.fesod.sheet.examples.read.listeners;

import com.alibaba.fastjson2.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.examples.read.data.DemoDAO;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.util.ListUtils;

/**
 * Template reading class.
 * <p>
 * An important point is that DemoDataListener should not be managed by Spring.
 * It needs to be newly created each time an Excel file is read.
 */
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    /**
     * Store data in the database every 100 records.
     */
    private static final int BATCH_COUNT = 100;
    /**
     * Cached data
     */
    private List<DemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * Mock DAO
     */
    private DemoDAO demoDAO;

    public DemoDataListener() {
        // This is a demo, so a new instance is created here.
        demoDAO = new DemoDAO();
    }

    /**
     * If Spring is used, please use this constructor.
     *
     * @param demoDAO
     */
    public DemoDataListener(DemoDAO demoDAO) {
        this.demoDAO = demoDAO;
    }

    /**
     * This method will be called for each data parsed.
     *
     * @param data    one row value.
     * @param context
     */
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Parsed one row of data: {}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * This method will be called after all data has been parsed.
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("All data has been parsed!");
    }

    /**
     * Save data to database.
     */
    private void saveData() {
        log.info("{} records saved to database!", cachedDataList.size());
        demoDAO.save(cachedDataList);
    }
}

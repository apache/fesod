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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.util.ListUtils;

/**
 * Template reading class
 *
 *
 */
// An important point is that DemoDataListener should not be managed by Spring.
// It needs to be newly created each time an Excel file is read.
// If Spring is used inside, it can be passed through the constructor.
@Slf4j
public class DemoDataListener implements ReadListener<DemoData> {

    /**
     * Store data in the database every 5 records. In actual use, it can be 100 records,
     * and then clear the list to facilitate memory recycling.
     */
    private static final int BATCH_COUNT = 100;
    /**
     * Cached data
     */
    private List<DemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * Assume this is a DAO. Of course, if there is business logic, this can also be a service.
     * If the data does not need to be stored, this object is useless.
     */
    private DemoDAO demoDAO;

    public DemoDataListener() {
        // This is a demo, so a new instance is created here. In actual use with Spring,
        // please use the constructor with parameters below.
        demoDAO = new DemoDAO();
    }

    /**
     * If Spring is used, please use this constructor. When creating a Listener, the class managed by Spring needs to be passed in.
     *
     * @param demoDAO
     */
    public DemoDataListener(DemoDAO demoDAO) {
        this.demoDAO = demoDAO;
    }

    /**
     * This method will be called for each data parsed.
     *
     * @param data    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("Parsed one row of data: {}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // When the number of records reaches BATCH_COUNT, the data needs to be stored in the database to prevent tens
        // of
        // thousands of records from being held in memory, which can easily cause OutOfMemoryError (OOM).
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // Clear the list after storage
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
        // Data needs to be saved here to ensure that any remaining data is stored in the database.
        saveData();
        log.info("All data has been parsed!");
    }

    /**
     * Store data in the database
     */
    private void saveData() {
        log.info("{} records are being stored in the database!", cachedDataList.size());
        demoDAO.save(cachedDataList);
        log.info("Data has been successfully stored in the database!");
    }
}

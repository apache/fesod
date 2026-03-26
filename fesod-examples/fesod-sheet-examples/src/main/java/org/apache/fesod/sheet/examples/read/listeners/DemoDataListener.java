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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.examples.read.listeners;

import com.alibaba.fastjson2.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.examples.read.data.DemoDAO;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.read.listener.ReadListener;

/**
 * Production-pattern listener demonstrating batch read-and-persist for Excel data.
 *
 * <h2>Scenario</h2>
 * <p>Reading a large Excel file (thousands or millions of rows) and inserting the data
 * into a database. Loading all rows into memory at once would cause an OutOfMemoryError.
 * This listener accumulates rows in a small batch and persists every {@value #BATCH_COUNT}
 * rows, then clears the cache.</p>
 *
 * <h2>Lifecycle</h2>
 * <pre>
 * ┌───────────────────────────────────────────────────────────────────┐
 * │  invoke(data, context)  ← called once per data row              │
 * │    └─ add to cachedDataList                                    │
 * │    └─ if cache.size() >= 100 → saveData() → clear cache       │
 * ├───────────────────────────────────────────────────────────────────┤
 * │  doAfterAllAnalysed(context)  ← called once after last row      │
 * │    └─ saveData() for remaining rows in cache                   │
 * └───────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <h2>Thread Safety &amp; Lifecycle</h2>
 * <p><b>IMPORTANT:</b> This class must NOT be managed by Spring (or any IoC container) as a
 * singleton. Create a <em>new instance</em> for each read operation because:
 * <ul>
 *   <li>The {@code cachedDataList} is mutable state that must not be shared.</li>
 *   <li>Reusing a listener across files would mix data from different files.</li>
 * </ul>
 *
 * <h2>Spring Integration</h2>
 * <p>If you need to inject Spring beans (e.g., a real DAO), use the constructor that
 * accepts a {@link DemoDAO} parameter. Create the listener in your service method:</p>
 * <pre>{@code
 * @Service
 * public class ExcelService {
 *     @Autowired
 *     private DemoDAO demoDAO;
 *
 *     public void importExcel(String fileName) {
 *         // Create a NEW listener for each read, passing the Spring-managed DAO
 *         FesodSheet.read(fileName, DemoData.class, new DemoDataListener(demoDAO))
 *             .sheet().doRead();
 *     }
 * }
 * }</pre>
 *
 * @see ReadListener
 * @see org.apache.fesod.sheet.examples.read.BasicReadExample
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

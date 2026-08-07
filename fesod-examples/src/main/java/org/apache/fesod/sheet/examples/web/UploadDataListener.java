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

package org.apache.fesod.sheet.examples.web;

import com.alibaba.fastjson2.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;

/**
 * Listener for processing uploaded Excel data in a web environment.
 *
 * <h2>Scenario</h2>
 * <p>When a user uploads an Excel file via a web form, this listener processes the rows
 * in batches and persists them using the injected {@link UploadDAO}.</p>
 *
 * <h2>Design Pattern</h2>
 * <p>Follows the same batch-save pattern as {@link org.apache.fesod.sheet.examples.read.listeners.DemoDataListener}:
 * caches up to {@value #BATCH_COUNT} rows, then flushes to the database.</p>
 *
 * <h2>Spring Integration</h2>
 * <p>This listener is NOT a Spring bean. It accepts a Spring-managed {@link UploadDAO}
 * via constructor injection. Create a new instance per upload request:</p>
 * <pre>{@code
 * // In WebExampleController
 * FesodSheet.read(file.getInputStream(), UploadData.class, new UploadDataListener(uploadDAO))
 *     .sheet().doRead();
 * }</pre>
 *
 * @see org.apache.fesod.sheet.examples.web.WebExampleController#upload(MultipartFile)
 * @see UploadDAO
 */
@Slf4j
public class UploadDataListener implements ReadListener<UploadData> {

    private static final int BATCH_COUNT = 100;
    private List<UploadData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private UploadDAO uploadDAO;

    public UploadDataListener(UploadDAO uploadDAO) {
        this.uploadDAO = uploadDAO;
    }

    @Override
    public void invoke(UploadData data, AnalysisContext context) {
        log.info("Parsed a data row: {}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("All data parsing completed!");
    }

    private void saveData() {
        log.info("{} records saved to database!", cachedDataList.size());
        uploadDAO.save(cachedDataList);
    }
}

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

package org.apache.fesod.sheet.analysis;

import java.util.List;
import org.apache.fesod.sheet.read.metadata.ReadSheet;

/**
 * Executes the read process of a single workbook.
 *
 * <p>An implementation is format specific (XLS, XLSX or CSV) and is created by
 * {@link ExcelAnalyserImpl} once the file type has been detected. It exposes the sheets
 * declared in the workbook and streams the parsed sheet data to the handlers configured
 * in the surrounding read context.</p>
 */
public interface ExcelReadExecutor {

    /**
     * Returns every sheet contained in the workbook, in the order they are declared.
     *
     * @return the sheets of the workbook, never {@code null}; empty when the workbook
     *         cannot be analysed (for instance an unsupported legacy format)
     */
    List<ReadSheet> sheetList();

    /**
     * Reads the sheets selected by the current read configuration and dispatches the
     * parsed rows to the registered listeners.
     *
     * <p>The processing stops as soon as a listener raises {@code ExcelAnalysisStopException},
     * which is handled by the caller and is not reported as a failure.</p>
     */
    void execute();
}

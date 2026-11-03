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
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.metadata.ReadSheet;

/**
 * Analyser that drives the streaming read pipeline of a single workbook.
 *
 * <p>It is created by {@link org.apache.fesod.sheet.ExcelReader} and is not intended to be reused
 * after {@link #finish()} has been called.
 */
public interface ExcelAnalyser {

    /**
     * Read the workbook, delivering the parsed data to the registered read listeners.
     *
     * <p>On any failure the resources held by this analyser are released before the exception is
     * propagated, so calling {@link #finish()} afterwards is not required.
     *
     * @param readSheetList the sheets to read; may be {@code null} when {@code readAll} is {@code true}
     * @param readAll       {@code true} to read every sheet in the workbook, in which case
     *                      {@code readSheetList} is ignored; must not be {@code null}
     * @throws IllegalArgumentException              if {@code readAll} is {@code false} and
     *                                               {@code readSheetList} contains no sheet
     * @throws org.apache.fesod.sheet.exception.ExcelAnalysisException if the workbook cannot be parsed
     */
    void analysis(List<ReadSheet> readSheetList, Boolean readAll);

    /**
     * Complete the entire read file.Release the cache and close stream
     */
    void finish();

    /**
     * Acquisition excel executor
     *
     * @return Excel file Executor
     */
    ExcelReadExecutor excelExecutor();

    /**
     * get the analysis context.
     *
     * @return analysis context
     */
    AnalysisContext analysisContext();
}

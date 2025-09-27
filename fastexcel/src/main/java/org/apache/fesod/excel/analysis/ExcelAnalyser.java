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

package org.apache.fesod.excel.analysis;

import java.util.List;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.metadata.ReadSheet;

/**
 * Excel file analyser
 *
 */
public interface ExcelAnalyser {
    /**
     * parse the sheet
     *
     * @param readSheetList
     *            Which sheets you need to read.
     * @param readAll
     *            The <code>readSheetList</code> parameter is ignored, and all sheets are read.
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

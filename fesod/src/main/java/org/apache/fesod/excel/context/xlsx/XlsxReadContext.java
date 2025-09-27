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

package org.apache.fesod.excel.context.xlsx;

import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.excel.read.metadata.holder.xlsx.XlsxReadWorkbookHolder;

/**
 * A context is the main anchorage point of a ls xlsx reader.
 *
 *
 **/
public interface XlsxReadContext extends AnalysisContext {
    /**
     * All information about the workbook you are currently working on.
     *
     * @return Current workbook holder
     */
    XlsxReadWorkbookHolder xlsxReadWorkbookHolder();

    /**
     * All information about the sheet you are currently working on.
     *
     * @return Current sheet holder
     */
    XlsxReadSheetHolder xlsxReadSheetHolder();
}

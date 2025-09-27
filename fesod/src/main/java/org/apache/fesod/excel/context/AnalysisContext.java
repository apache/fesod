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

package org.apache.fesod.excel.context;

import java.io.InputStream;
import java.util.List;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.ReadHolder;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.excel.read.metadata.holder.ReadWorkbookHolder;
import org.apache.fesod.excel.read.processor.AnalysisEventProcessor;
import org.apache.fesod.excel.support.ExcelTypeEnum;

/**
 *
 * A context is the main anchorage point of a excel reader.
 *
 */
public interface AnalysisContext {
    /**
     * Select the current table
     *
     * @param readSheet
     *            sheet to read
     */
    void currentSheet(ReadSheet readSheet);

    /**
     * All information about the workbook you are currently working on
     *
     * @return Current workbook holder
     */
    ReadWorkbookHolder readWorkbookHolder();

    /**
     * All information about the sheet you are currently working on
     *
     * @return Current sheet holder
     */
    ReadSheetHolder readSheetHolder();

    /**
     * Set row of currently operated cell
     *
     * @param readRowHolder
     *            Current row holder
     */
    void readRowHolder(ReadRowHolder readRowHolder);

    /**
     * Row of currently operated cell
     *
     * @return Current row holder
     */
    ReadRowHolder readRowHolder();

    /**
     * The current read operation corresponds to the <code>readSheetHolder</code> or <code>readWorkbookHolder</code>
     *
     * @return Current holder
     */
    ReadHolder currentReadHolder();

    /**
     * Custom attribute
     *
     * @return
     */
    Object getCustom();

    /**
     * Event processor
     *
     * @return
     */
    AnalysisEventProcessor analysisEventProcessor();

    /**
     * Data that the customer needs to read
     *
     * @return
     */
    List<ReadSheet> readSheetList();

    /**
     * Data that the customer needs to read
     *
     * @param readSheetList
     */
    void readSheetList(List<ReadSheet> readSheetList);

    /**
     *
     * get excel type
     *
     * @return excel type
     * @deprecated please use {@link #readWorkbookHolder()}
     */
    @Deprecated
    ExcelTypeEnum getExcelType();

    /**
     * get in io
     *
     * @return file io
     * @deprecated please use {@link #readWorkbookHolder()}
     */
    @Deprecated
    InputStream getInputStream();

    /**
     * get current row
     *
     * @return
     * @deprecated please use {@link #readRowHolder()}
     */
    @Deprecated
    Integer getCurrentRowNum();

    /**
     * get total row ,Data may be inaccurate
     *
     * @return
     * @deprecated please use {@link #readRowHolder()}
     */
    @Deprecated
    Integer getTotalCount();

    /**
     * get current result
     *
     * @return get current result
     * @deprecated please use {@link #readRowHolder()}
     */
    @Deprecated
    Object getCurrentRowAnalysisResult();

    /**
     * Interrupt execution
     *
     * @deprecated please use {@link AnalysisEventListener#hasNext(AnalysisContext)}
     */
    @Deprecated
    void interrupt();
}

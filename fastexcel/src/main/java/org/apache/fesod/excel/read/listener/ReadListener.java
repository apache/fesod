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

package org.apache.fesod.excel.read.listener;

import java.util.Map;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.Listener;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;

/**
 * Interface to listen for read results
 *
 *
 */
public interface ReadListener<T> extends Listener {
    /**
     * All listeners receive this method when any one Listener does an error report. If an exception is thrown here, the
     * entire read will terminate.
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    default void onException(Exception exception, AnalysisContext context) throws Exception {
        throw exception;
    }

    /**
     * When analysis one head row trigger invoke function.
     *
     * @param headMap
     * @param context
     */
    default void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {}

    /**
     * When analysis one row trigger invoke function.
     *
     * @param data    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context analysis context
     */
    void invoke(T data, AnalysisContext context);

    /**
     * The current method is called when extra information is returned
     *
     * @param extra   extra information
     * @param context analysis context
     */
    default void extra(CellExtra extra, AnalysisContext context) {}

    /**
     * if have something to do after all analysis
     *
     * @param context
     */
    void doAfterAllAnalysed(AnalysisContext context);

    /**
     * Verify that there is another piece of data.You can stop the read by returning false
     *
     * @param context
     * @return
     */
    default boolean hasNext(AnalysisContext context) {
        if (context == null
                || context.readRowHolder() == null
                || context.readSheetHolder() == null
                || context.readSheetHolder().getReadSheet() == null
                || context.readWorkbookHolder().getReadWorkbook() == null) {
            return true;
        }
        ReadRowHolder readRowHolder = context.readRowHolder();
        int index = readRowHolder.getRowIndex();

        Integer limit = context.readSheetHolder().getReadSheet().getNumRows();
        if (limit == null) {
            limit = context.readWorkbookHolder().getReadWorkbook().getNumRows();
        }
        if (limit != null && index >= limit) {
            return false;
        }
        return true;
    }
}

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

package cn.idev.excel.write;

import cn.idev.excel.context.WriteContext;
import cn.idev.excel.write.merge.OnceAbsoluteMergeStrategy;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import cn.idev.excel.write.metadata.fill.FillConfig;
import java.util.Collection;

/**
 *
 */
public interface ExcelBuilder {

    /**
     * WorkBook increase value
     *
     * @param data
     *            java basic type or java model extend BaseModel
     * @param writeSheet
     *            Write the sheet
     * @deprecated please use{@link ExcelBuilder#addContent(Collection, WriteSheet, WriteTable)}
     */
    @Deprecated
    void addContent(Collection<?> data, WriteSheet writeSheet);

    /**
     * WorkBook increase value
     *
     * @param data
     *            java basic type or java model extend BaseModel
     * @param writeSheet
     *            Write the sheet
     * @param writeTable
     *            Write the table
     */
    void addContent(Collection<?> data, WriteSheet writeSheet, WriteTable writeTable);

    /**
     * WorkBook fill value
     *
     * @param data
     * @param fillConfig
     * @param writeSheet
     */
    void fill(Object data, FillConfig fillConfig, WriteSheet writeSheet);

    /**
     * Creates new cell range. Indexes are zero-based.
     *
     * @param firstRow
     *            Index of first row
     * @param lastRow
     *            Index of last row (inclusive), must be equal to or larger than {@code firstRow}
     * @param firstCol
     *            Index of first column
     * @param lastCol
     *            Index of last column (inclusive), must be equal to or larger than {@code firstCol}
     * @deprecated please use{@link OnceAbsoluteMergeStrategy}
     */
    @Deprecated
    void merge(int firstRow, int lastRow, int firstCol, int lastCol);

    /**
     * Gets the written data
     *
     * @return
     */
    WriteContext writeContext();

    /**
     * Close io
     *
     * @param onException
     */
    void finish(boolean onException);
}

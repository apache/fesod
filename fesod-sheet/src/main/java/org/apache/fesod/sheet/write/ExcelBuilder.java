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

package org.apache.fesod.sheet.write;

import java.util.Collection;
import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.write.merge.OnceAbsoluteMergeStrategy;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;

/**
 * The fluent builder returned after a write context has been created (typically via
 * {@code FesodSheet.write(...)}) and used to push content, fill templates, register merges and
 * finally flush everything to the underlying output.
 *
 * <p>Typical lifecycle: call one or more of {@link #addContent}, {@link #fill} and
 * {@link #merge}, then invoke {@link #finish(boolean)} exactly once to release resources.
 * Instances are not thread-safe.</p>
 */
public interface ExcelBuilder {

    /**
     * Appends a collection of rows to the target sheet.
     *
     * @param data       a collection of Java primitive wrappers, {@code String}s, or model objects
     *                   extending {@code BaseModel}; {@code null} elements are written as blank
     *                   cells
     * @param writeSheet the sheet the rows should be written to; must not be {@code null}
     * @deprecated use {@link #addContent(Collection, WriteSheet, WriteTable)} instead, which also
     *             supports writing into a specific table
     */
    @Deprecated
    void addContent(Collection<?> data, WriteSheet writeSheet);

    /**
     * Appends a collection of rows to a specific table inside the target sheet.
     *
     * @param data       a collection of Java primitive wrappers, {@code String}s, or model objects
     *                   extending {@code BaseModel}; {@code null} elements are written as blank
     *                   cells
     * @param writeSheet the sheet the table lives on; must not be {@code null}
     * @param writeTable the table the rows should be appended to; may be {@code null} to write
     *                   directly to the sheet
     */
    void addContent(Collection<?> data, WriteSheet writeSheet, WriteTable writeTable);

    /**
     * Fills data into a template according to the given fill configuration.
     *
     * @param data       the data used to fill the template; may be a single object or a collection
     *                   depending on {@code fillConfig}
     * @param fillConfig the fill options such as whether to create a new sheet per collection item
     *                   and the prune/force options; must not be {@code null}
     * @param writeSheet the sheet whose template should be filled; must not be {@code null}
     */
    void fill(Object data, FillConfig fillConfig, WriteSheet writeSheet);

    /**
     * Merges a rectangular region of cells. All indexes are zero-based.
     *
     * @param firstRow the index of the first row of the merged region (inclusive)
     * @param lastRow  the index of the last row of the merged region (inclusive); must be greater
     *                 than or equal to {@code firstRow}
     * @param firstCol the index of the first column of the merged region (inclusive)
     * @param lastCol  the index of the last column of the merged region (inclusive); must be
     *                 greater than or equal to {@code firstCol}
     * @deprecated prefer {@link OnceAbsoluteMergeStrategy}, which applies merges declaratively via
     *             a write handler and composes better with other strategies
     */
    @Deprecated
    void merge(int firstRow, int lastRow, int firstCol, int lastCol);

    /**
     * Returns the underlying write context that this builder is driving.
     *
     * @return the current {@link WriteContext}; never {@code null} while the builder is usable.
     *         Exposed mainly for advanced integrations such as custom handlers or diagnostics
     */
    WriteContext writeContext();

    /**
     * Flushes buffered content and releases the underlying output resources. This method must be
     * called exactly once at the end of a write session.
     *
     * @param onException whether the call is happening from an exception path. When {@code true},
     *                    implementation may skip non-critical cleanup so that the original
     *                    exception is not masked; when {@code false}, a normal graceful shutdown
     *                    is performed
     */
    void finish(boolean onException);
}

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

package org.apache.fesod.excel;

import java.io.Closeable;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.WriteContext;
import org.apache.fesod.excel.write.ExcelBuilder;
import org.apache.fesod.excel.write.ExcelBuilderImpl;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.apache.fesod.excel.write.metadata.WriteWorkbook;
import org.apache.fesod.excel.write.metadata.fill.FillConfig;

/**
 * Excel Writer This tool is used to write value out to Excel via POI. This object can perform the following two
 * functions.
 *
 * <pre>
 *    1. Create a new empty Excel workbook, write the value to the stream after the value is filled.
 *    2. Edit existing Excel, write the original Excel file, or write it to other places.}
 * </pre>
 *
 */
@Slf4j
public class ExcelWriter implements Closeable {

    private final ExcelBuilder excelBuilder;

    /**
     * Create new writer
     *
     * @param writeWorkbook
     */
    public ExcelWriter(WriteWorkbook writeWorkbook) {
        excelBuilder = new ExcelBuilderImpl(writeWorkbook);
    }

    /**
     * Write data to a sheet
     *
     * @param data       Data to be written
     * @param writeSheet Write to this sheet
     * @return this current writer
     */
    public ExcelWriter write(Collection<?> data, WriteSheet writeSheet) {
        return write(data, writeSheet, null);
    }

    /**
     * Write data to a sheet
     *
     * @param supplier   Data to be written
     * @param writeSheet Write to this sheet
     * @return this current writer
     */
    public ExcelWriter write(Supplier<Collection<?>> supplier, WriteSheet writeSheet) {
        return write(supplier.get(), writeSheet, null);
    }

    /**
     * Write value to a sheet
     *
     * @param data       Data to be written
     * @param writeSheet Write to this sheet
     * @param writeTable Write to this table
     * @return this
     */
    public ExcelWriter write(Collection<?> data, WriteSheet writeSheet, WriteTable writeTable) {
        excelBuilder.addContent(data, writeSheet, writeTable);
        return this;
    }

    /**
     * Write value to a sheet
     *
     * @param supplier   Data to be written
     * @param writeSheet Write to this sheet
     * @param writeTable Write to this table
     * @return this
     */
    public ExcelWriter write(Supplier<Collection<?>> supplier, WriteSheet writeSheet, WriteTable writeTable) {
        excelBuilder.addContent(supplier.get(), writeSheet, writeTable);
        return this;
    }

    /**
     * Fill value to a sheet
     *
     * @param data
     * @param writeSheet
     * @return
     */
    public ExcelWriter fill(Object data, WriteSheet writeSheet) {
        return fill(data, null, writeSheet);
    }

    /**
     * Fill value to a sheet
     *
     * @param data
     * @param fillConfig
     * @param writeSheet
     * @return
     */
    public ExcelWriter fill(Object data, FillConfig fillConfig, WriteSheet writeSheet) {
        excelBuilder.fill(data, fillConfig, writeSheet);
        return this;
    }

    /**
     * Fill value to a sheet
     *
     * @param supplier
     * @param writeSheet
     * @return
     */
    public ExcelWriter fill(Supplier<Object> supplier, WriteSheet writeSheet) {
        return fill(supplier.get(), null, writeSheet);
    }

    /**
     * Fill value to a sheet
     *
     * @param supplier
     * @param fillConfig
     * @param writeSheet
     * @return
     */
    public ExcelWriter fill(Supplier<Object> supplier, FillConfig fillConfig, WriteSheet writeSheet) {
        excelBuilder.fill(supplier.get(), fillConfig, writeSheet);
        return this;
    }

    /**
     * Close IO
     */
    public void finish() {
        if (excelBuilder != null) {
            excelBuilder.finish(false);
        }
    }

    /**
     * The context of the entire writing process
     *
     * @return
     */
    public WriteContext writeContext() {
        return excelBuilder.writeContext();
    }

    @Override
    public void close() {
        finish();
    }

    /**
     * Prevents calls to {@link #finish} from freeing the cache
     */
    @Override
    protected void finalize() {
        try {
            finish();
        } catch (Throwable e) {
            log.warn("Destroy object failed", e);
        }
    }
}

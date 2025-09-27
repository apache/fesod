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

import java.io.OutputStream;
import org.apache.fesod.excel.enums.WriteTypeEnum;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.apache.fesod.excel.write.metadata.holder.WriteHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteTableHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Write context
 *
 */
public interface WriteContext {
    /**
     * If the current sheet already exists, select it; if not, create it
     *
     * @param writeSheet
     *            Current sheet
     * @param writeType
     */
    void currentSheet(WriteSheet writeSheet, WriteTypeEnum writeType);

    /**
     * If the current table already exists, select it; if not, create it
     *
     * @param writeTable
     */
    void currentTable(WriteTable writeTable);

    /**
     * All information about the workbook you are currently working on
     *
     * @return
     */
    WriteWorkbookHolder writeWorkbookHolder();

    /**
     * All information about the sheet you are currently working on
     *
     * @return
     */
    WriteSheetHolder writeSheetHolder();

    /**
     * All information about the table you are currently working on
     *
     * @return
     */
    WriteTableHolder writeTableHolder();

    /**
     * Configuration of currently operated cell. May be 'writeSheetHolder' or 'writeTableHolder' or
     * 'writeWorkbookHolder'
     *
     * @return
     */
    WriteHolder currentWriteHolder();

    /**
     * close
     *
     * @param onException
     */
    void finish(boolean onException);

    /**
     * Current sheet
     *
     * @return
     * @deprecated please us e{@link #writeSheetHolder()}
     */
    @Deprecated
    Sheet getCurrentSheet();

    /**
     * Need head
     *
     * @return
     * @deprecated please us e{@link #writeSheetHolder()}
     */
    @Deprecated
    boolean needHead();

    /**
     * Get outputStream
     *
     * @return
     * @deprecated please us e{@link #writeWorkbookHolder()} ()}
     */
    @Deprecated
    OutputStream getOutputStream();

    /**
     * Get workbook
     *
     * @return
     * @deprecated please us e{@link #writeWorkbookHolder()} ()}
     */
    @Deprecated
    Workbook getWorkbook();
}

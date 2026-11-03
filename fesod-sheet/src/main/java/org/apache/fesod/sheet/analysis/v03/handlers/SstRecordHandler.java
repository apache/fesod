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

package org.apache.fesod.sheet.analysis.v03.handlers;

import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.cache.XlsCache;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

/**
 * Handler for the BIFF {@link SSTRecord} (Shared String Table), which contains all strings
 * used in the worksheet cells.
 * <p>
 * Builds an {@link XlsCache} from the shared string table and installs it on the workbook holder
 * so that subsequent cell references can resolve string values by index.
 */
public class SstRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Casts the record to {@link SSTRecord}, wraps it in an {@link XlsCache}, and sets it as
     * the shared-strings cache on {@link XlsReadContext#readWorkbookHolder()}.
     *
     * @param xlsReadContext the current XLS reading context
     * @param record         the {@link SSTRecord} to process
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        xlsReadContext.readWorkbookHolder().setReadCache(new XlsCache((SSTRecord) record));
    }
}

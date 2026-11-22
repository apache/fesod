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

package org.apache.fesod.sheet.analysis.v03.handlers;

import org.apache.fesod.sheet.analysis.v03.IgnorableXlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.Record;

/**
 * Handler for {@link DateWindow1904Record}, which specifies whether the workbook
 * uses the 1904 date windowing system instead of the default 1900 system.
 *
 * <p>The record is ignored when {@code use1904windowing} has already been
 * explicitly configured on the {@link org.apache.fesod.sheet.read.metadata.ReadWorkbook}.
 */
public class DateWindow1904RecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Applies the date windowing flag from the given {@link DateWindow1904Record}
     * to the global configuration of the current read context, unless the user has
     * already specified an explicit value.
     *
     * @param xlsReadContext the current XLS read context holding the global configuration
     * @param record         the {@link DateWindow1904Record} to process; a windowing value of {@code 1}
     *                       enables 1904 date tracking
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        DateWindow1904Record dwr = (DateWindow1904Record) record;
        if (xlsReadContext.xlsReadWorkbookHolder().getReadWorkbook().getUse1904windowing() != null) {
            return;
        }
        xlsReadContext.xlsReadWorkbookHolder().getGlobalConfiguration().setUse1904windowing(dwr.getWindowing() == 1);
    }
}

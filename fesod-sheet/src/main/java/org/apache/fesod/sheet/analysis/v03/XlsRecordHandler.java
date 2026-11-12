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

package org.apache.fesod.sheet.analysis.v03;

import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.poi.hssf.record.Record;

/**
 * Handler for a single XLS (BIFF) record type during streaming read.
 * <p>
 * Implementations are registered by record sid in {@link XlsSaxAnalyser} and {@link XlsListSheetListener}, and are
 * invoked only when {@link #support(XlsReadContext, Record)} returns {@code true}.
 */
public interface XlsRecordHandler {

    /**
     * Determine whether this handler can process the given record in the current read context.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the record to test, never {@code null}
     * @return {@code true} if {@link #processRecord(XlsReadContext, Record)} should be invoked for this record
     */
    boolean support(XlsReadContext xlsReadContext, Record record);

    /**
     * Process the given record and update the read context accordingly.
     * <p>
     * Only called after {@link #support(XlsReadContext, Record)} returned {@code true}. Throwing
     * {@link org.apache.fesod.sheet.exception.ExcelAnalysisStopSheetException} stops the current sheet and makes
     * subsequent ignorable records be skipped.
     *
     * @param xlsReadContext the current XLS read context, never {@code null}
     * @param record         the record to process, never {@code null}
     */
    void processRecord(XlsReadContext xlsReadContext, Record record);
}

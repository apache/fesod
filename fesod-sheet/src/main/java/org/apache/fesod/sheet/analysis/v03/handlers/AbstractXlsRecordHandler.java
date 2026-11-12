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

import org.apache.fesod.sheet.analysis.v03.XlsRecordHandler;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.poi.hssf.record.Record;

/**
 * Base class for {@link XlsRecordHandler} implementations.
 * <p>
 * Defaults {@link #support(XlsReadContext, Record)} to {@code true}, so subclasses only need to implement
 * {@link #processRecord(XlsReadContext, Record)} unless they require custom filtering.
 */
public abstract class AbstractXlsRecordHandler implements XlsRecordHandler {

    /**
     * {@inheritDoc}
     *
     * @return always {@code true}; override in subclasses to filter records
     */
    @Override
    public boolean support(XlsReadContext xlsReadContext, Record record) {
        return true;
    }
}

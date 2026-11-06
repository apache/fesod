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
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SubRecord;

/**
 * Handles XLS {@link ObjRecord} (BIFF object record).
 *
 * <p>Scans the record's sub-records for a {@link CommonObjectDataSubRecord} describing a
 * comment object, and stores its object id in the read context so that a subsequent
 * note record can be associated with the correct cell.
 */
public class ObjRecordHandler extends AbstractXlsRecordHandler implements IgnorableXlsRecordHandler {

    /**
     * Process the {@link ObjRecord} and cache the comment object index if present.
     *
     * @param xlsReadContext the current XLS read context used to store the temporary object index
     * @param record         the record to process, must be an {@link ObjRecord}
     */
    @Override
    public void processRecord(XlsReadContext xlsReadContext, Record record) {
        ObjRecord or = (ObjRecord) record;
        for (SubRecord subRecord : or.getSubRecords()) {
            if (subRecord instanceof CommonObjectDataSubRecord) {
                CommonObjectDataSubRecord codsr = (CommonObjectDataSubRecord) subRecord;
                if (CommonObjectDataSubRecord.OBJECT_TYPE_COMMENT == codsr.getObjectType()) {
                    xlsReadContext.xlsReadSheetHolder().setTempObjectIndex(codsr.getObjectId());
                }
                break;
            }
        }
    }
}

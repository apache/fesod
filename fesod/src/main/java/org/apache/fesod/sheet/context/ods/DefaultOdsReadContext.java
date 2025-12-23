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

package org.apache.fesod.sheet.context.ods;

import org.apache.fesod.sheet.context.AnalysisContextImpl;
import org.apache.fesod.sheet.read.metadata.ReadWorkbook;
import org.apache.fesod.sheet.read.metadata.holder.ods.OdsReadSheetHolder;
import org.apache.fesod.sheet.read.metadata.holder.ods.OdsReadWorkbookHolder;
import org.apache.fesod.sheet.support.ExcelTypeEnum;

/**
 * A context is the main anchorage point of an ODS reader.
 *
 */
public class DefaultOdsReadContext extends AnalysisContextImpl implements OdsReadContext {

    public DefaultOdsReadContext(ReadWorkbook readWorkbook, ExcelTypeEnum actualExcelType) {
        super(readWorkbook, actualExcelType);
    }

    @Override
    public OdsReadWorkbookHolder odsReadWorkbookHolder() {
        return (OdsReadWorkbookHolder) readWorkbookHolder();
    }

    @Override
    public OdsReadSheetHolder odsReadSheetHolder() {
        return (OdsReadSheetHolder) readSheetHolder();
    }
}


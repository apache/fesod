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

package org.apache.fesod.sheet.analysis.v07.handlers;

import org.apache.fesod.common.util.PositionUtils;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.xml.sax.Attributes;

/**
 * Handler for the {@code dimension} tag of an XLSX worksheet. The tag's {@code ref}
 * attribute holds the used range of the sheet (for example {@code A1:C10}), whose
 * ending row is used to estimate the total number of rows before the rows themselves
 * are streamed. The estimate is stored on the sheet holder and is typically used to
 * drive progress reporting.
 */
public class CountTagHandler extends AbstractXlsxTagHandler {

    /**
     * Reads the {@code ref} range from the {@code dimension} tag, extracts the end cell
     * after the {@code ':'} separator, and records its zero-based row plus one as the
     * sheet's approximate total row number.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name, always the {@code dimension} tag
     * @param attributes      the tag attributes, from which {@code ref} is read
     */
    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        String d = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_REF);
        String totalStr = d.substring(d.indexOf(":") + 1);
        xlsxReadContext.readSheetHolder().setApproximateTotalRowNumber(PositionUtils.getRow(totalStr) + 1);
    }
}

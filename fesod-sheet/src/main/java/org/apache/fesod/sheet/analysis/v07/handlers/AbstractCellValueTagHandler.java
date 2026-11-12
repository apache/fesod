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

import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;

/**
 * Abstract base handler for XLSX cell value tags (e.g. {@code <v>}).
 *
 * <p>It accumulates the character content of the cell value element into the
 * temporary data buffer of the current sheet holder, which is later parsed into
 * the actual cell value.
 */
public abstract class AbstractCellValueTagHandler extends AbstractXlsxTagHandler {

    /**
     * Append the character data of the cell value element to the temporary data buffer.
     *
     * @param xlsxReadContext the current XLSX read context, providing access to the sheet holder
     * @param ch              the characters from the XML document
     * @param start           the starting position in the character array
     * @param length          the number of characters to read
     */
    @Override
    public void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length) {
        xlsxReadContext.xlsxReadSheetHolder().getTempData().append(ch, start, length);
    }
}

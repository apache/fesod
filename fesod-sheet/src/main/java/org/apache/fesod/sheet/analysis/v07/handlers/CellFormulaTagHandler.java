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
import org.apache.fesod.sheet.metadata.data.FormulaData;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.xml.sax.Attributes;

/**
 * Handler for the {@code <f>} (cell formula) tag in XLSX sheet XML.
 *
 * <p>Accumulates the formula text via {@link #characters} into a temporary
 * {@link StringBuilder}, then attaches the resulting {@link FormulaData} to
 * the current cell data when the element ends.
 */
public class CellFormulaTagHandler extends AbstractXlsxTagHandler {

    /**
     * Invoked when the {@code <f>} element starts. Initializes a temporary
     * builder to accumulate the formula content.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name
     * @param attributes      the attributes of the tag (unused)
     */
    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        xlsxReadSheetHolder.setTempFormula(new StringBuilder());
    }

    /**
     * Invoked when the {@code <f>} element ends. Wraps the accumulated formula
     * text in a {@link FormulaData} and attaches it to the current cell data.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name (unused)
     */
    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {
        XlsxReadSheetHolder xlsxReadSheetHolder = xlsxReadContext.xlsxReadSheetHolder();
        FormulaData formulaData = new FormulaData();
        formulaData.setFormulaValue(xlsxReadSheetHolder.getTempFormula().toString());
        xlsxReadSheetHolder.getTempCellData().setFormulaData(formulaData);
    }

    /**
     * Invoked for the character content of the {@code <f>} element. Appends
     * the text to the temporary formula builder.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param ch              the character array containing the text
     * @param start           the start offset in the array
     * @param length          the number of characters to append
     */
    @Override
    public void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length) {
        xlsxReadContext.xlsxReadSheetHolder().getTempFormula().append(ch, start, length);
    }
}

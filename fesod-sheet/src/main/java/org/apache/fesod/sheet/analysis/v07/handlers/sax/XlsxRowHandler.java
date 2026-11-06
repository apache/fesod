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

package org.apache.fesod.sheet.analysis.v07.handlers.sax;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.analysis.v07.handlers.CellFormulaTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.CellInlineStringValueTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.CellTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.CellValueTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.CountTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.HyperlinkTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.MergeCellTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.RowTagHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.XlsxTagHandler;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for the row-level XML of an XLSX worksheet. It dispatches each encountered tag to the
 * corresponding {@link XlsxTagHandler} by tag name.
 */
@Slf4j
public class XlsxRowHandler extends DefaultHandler {
    private final XlsxReadContext xlsxReadContext;
    private boolean inPhoneticRun;
    private boolean inlineStringCell;
    private static final Map<String, XlsxTagHandler> XLSX_CELL_HANDLER_MAP = new HashMap<>(64);

    static {
        CellFormulaTagHandler cellFormulaTagHandler = new CellFormulaTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_FORMULA_TAG, cellFormulaTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_CELL_FORMULA_TAG, cellFormulaTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_CELL_FORMULA_TAG, cellFormulaTagHandler);
        CellInlineStringValueTagHandler cellInlineStringValueTagHandler = new CellInlineStringValueTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_INLINE_STRING_VALUE_TAG, cellInlineStringValueTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_CELL_INLINE_STRING_VALUE_TAG, cellInlineStringValueTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_CELL_INLINE_STRING_VALUE_TAG, cellInlineStringValueTagHandler);
        CellTagHandler cellTagHandler = new CellTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_TAG, cellTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_CELL_TAG, cellTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_CELL_TAG, cellTagHandler);
        CellValueTagHandler cellValueTagHandler = new CellValueTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.CELL_VALUE_TAG, cellValueTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_CELL_VALUE_TAG, cellValueTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_CELL_VALUE_TAG, cellValueTagHandler);
        CountTagHandler countTagHandler = new CountTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.DIMENSION_TAG, countTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_DIMENSION_TAG, countTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_DIMENSION_TAG, countTagHandler);
        HyperlinkTagHandler hyperlinkTagHandler = new HyperlinkTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.HYPERLINK_TAG, hyperlinkTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_HYPERLINK_TAG, hyperlinkTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_HYPERLINK_TAG, hyperlinkTagHandler);
        MergeCellTagHandler mergeCellTagHandler = new MergeCellTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.MERGE_CELL_TAG, mergeCellTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_MERGE_CELL_TAG, mergeCellTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_MERGE_CELL_TAG, mergeCellTagHandler);
        RowTagHandler rowTagHandler = new RowTagHandler();
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.ROW_TAG, rowTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.X_ROW_TAG, rowTagHandler);
        XLSX_CELL_HANDLER_MAP.put(ExcelXmlConstants.NS2_ROW_TAG, rowTagHandler);
    }

    /**
     * Creates a handler bound to the given read context.
     *
     * @param xlsxReadContext the current XLSX read context
     */
    public XlsxRowHandler(XlsxReadContext xlsxReadContext) {
        this.xlsxReadContext = xlsxReadContext;
    }

    /**
     * Invoked when the start of an element is encountered. Pushes the tag onto the tag deque and delegates
     * to the matching handler; tags without a handler or unsupported by the current context are ignored.
     *
     * @param uri        the namespace URI
     * @param localName  the local name of the element
     * @param name       the qualified name of the element
     * @param attributes the attributes attached to the element
     * @throws SAXException if any handler fails to process the element
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (isPhoneticRun(name)) {
            inPhoneticRun = true;
            return;
        }
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(name);
        if (handler == null || !handler.support(xlsxReadContext)) {
            return;
        }
        if (handler instanceof CellTagHandler) {
            // Both inlineStr and str map to DIRECT_STRING, but only inlineStr must ignore v elements.
            inlineStringCell = "inlineStr".equals(attributes.getValue(ExcelXmlConstants.ATTRIBUTE_T));
        }
        xlsxReadContext.xlsxReadSheetHolder().getTagDeque().push(name);
        handler.startElement(xlsxReadContext, name, attributes);
    }

    /**
     * Invoked when character data inside the current element is encountered. Delegates the text to the handler
     * of the tag on top of the tag deque; does nothing when there is no current tag or no matching handler.
     *
     * @param ch     the characters
     * @param start  the start position in the character array
     * @param length the number of characters to use from the character array
     * @throws SAXException if the handler fails to process the characters
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Phonetic runs annotate the inline text; they are not part of the cell value.
        if (inPhoneticRun) {
            return;
        }
        String currentTag = xlsxReadContext.xlsxReadSheetHolder().getTagDeque().peek();
        if (currentTag == null) {
            return;
        }
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(currentTag);
        if (handler == null || !handler.support(xlsxReadContext)) {
            return;
        }
        if (inlineStringCell && handler instanceof CellValueTagHandler) {
            // Tolerate stray v elements in inlineStr cells, including supported prefixed tags.
            return;
        }
        handler.characters(xlsxReadContext, ch, start, length);
    }

    /**
     * Invoked when the end of an element is encountered. Delegates to the matching handler and then pops the
     * tag from the tag deque; tags without a handler or unsupported by the current context are ignored.
     *
     * @param uri       the namespace URI
     * @param localName the local name of the element
     * @param name      the qualified name of the element
     * @throws SAXException if the handler fails to process the end of the element
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (isPhoneticRun(name)) {
            inPhoneticRun = false;
            return;
        }
        XlsxTagHandler handler = XLSX_CELL_HANDLER_MAP.get(name);
        if (handler == null || !handler.support(xlsxReadContext)) {
            return;
        }
        handler.endElement(xlsxReadContext, name);
        xlsxReadContext.xlsxReadSheetHolder().getTagDeque().pop();
    }

    private static boolean isPhoneticRun(String name) {
        return ExcelXmlConstants.SHAREDSTRINGS_RPH_TAG.equals(name)
                || ExcelXmlConstants.SHAREDSTRINGS_X_RPH_TAG.equals(name)
                || ExcelXmlConstants.SHAREDSTRINGS_NS2_RPH_TAG.equals(name);
    }
}

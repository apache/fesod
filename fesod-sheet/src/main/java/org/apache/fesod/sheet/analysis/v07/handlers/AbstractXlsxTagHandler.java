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
import org.xml.sax.Attributes;

/**
 * Abstract base implementation of {@link XlsxTagHandler} that provides no-op default
 * behavior for all lifecycle callbacks, so subclasses only need to override the
 * methods relevant to the tags they handle.
 */
public abstract class AbstractXlsxTagHandler implements XlsxTagHandler {

    /**
     * Returns {@code true} by default, meaning the handler supports all tags
     * unless a subclass overrides this method.
     *
     * @param xlsxReadContext the current XLSX read context
     * @return {@code true} if this handler supports the given context
     */
    @Override
    public boolean support(XlsxReadContext xlsxReadContext) {
        return true;
    }

    /**
     * Invoked when the start of a tag is encountered. Does nothing by default.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name
     * @param attributes      the tag attributes
     */
    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {}

    /**
     * Invoked when the end of a tag is encountered. Does nothing by default.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name
     */
    @Override
    public void endElement(XlsxReadContext xlsxReadContext, String name) {}

    /**
     * Invoked when the character data within a tag is read. Does nothing by default.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param ch              the characters from the XML document
     * @param start           the start position in the array
     * @param length          the number of characters to read from the array
     */
    @Override
    public void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length) {}
}

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
 * Handler for a specific XLSX XML tag during SAX-based streaming reading.
 * <p>
 * Implementations are selected by {@link #support(XlsxReadContext)} and then receive the SAX
 * lifecycle callbacks for the matched tag.
 */
public interface XlsxTagHandler {

    /**
     * Determine whether this handler should handle the tag in the current reading context.
     *
     * @param xlsxReadContext the current XLSX reading context, never {@code null}
     * @return {@code true} if this handler supports the current tag, {@code false} otherwise
     */
    boolean support(XlsxReadContext xlsxReadContext);

    /**
     * Handle the start of the matched XML element.
     *
     * @param xlsxReadContext the current XLSX reading context, never {@code null}
     * @param name            the qualified name of the tag
     * @param attributes      the attributes of the tag, never {@code null}
     */
    void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes);

    /**
     * Handle the end of the matched XML element.
     *
     * @param xlsxReadContext the current XLSX reading context, never {@code null}
     * @param name            the qualified name of the tag
     */
    void endElement(XlsxReadContext xlsxReadContext, String name);

    /**
     * Handle the character data contained in the matched XML element.
     *
     * @param xlsxReadContext the current XLSX reading context, never {@code null}
     * @param ch              the characters from the XML document
     * @param start           the start position in the character array
     * @param length          the number of characters to read from the character array
     */
    void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length);
}

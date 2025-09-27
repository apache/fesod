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

package org.apache.fesod.excel.analysis.v07.handlers;

import org.apache.fesod.excel.context.xlsx.XlsxReadContext;
import org.xml.sax.Attributes;

/**
 * Tag handler
 */
public interface XlsxTagHandler {

    /**
     * Whether to support
     *
     * @param xlsxReadContext
     * @return
     */
    boolean support(XlsxReadContext xlsxReadContext);

    /**
     * Start handle
     *
     * @param xlsxReadContext
     *            xlsxReadContext
     * @param name
     *            Tag name
     * @param attributes
     *            Tag attributes
     */
    void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes);

    /**
     * End handle
     *
     * @param xlsxReadContext
     *            xlsxReadContext
     * @param name
     *            Tag name
     */
    void endElement(XlsxReadContext xlsxReadContext, String name);

    /**
     * Read data
     *
     * @param xlsxReadContext
     * @param ch
     * @param start
     * @param length
     */
    void characters(XlsxReadContext xlsxReadContext, char[] ch, int start, int length);
}

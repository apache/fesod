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

package org.apache.fesod.sheet.analysis.v07.handlers.sax;

import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.util.XlsxEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler that streams <code>sharedStrings.xml</code> and feeds the decoded
 * shared strings into a {@link ReadCache}.
 * <p>
 * Only the text inside {@code <t>} elements is collected; {@code <rPh>} (phonetic)
 * runs are skipped. Each {@code <si>} element produces exactly one cache entry,
 * with rich-text runs concatenated into a single value.
 */
public class SharedStringsTableHandler extends DefaultHandler {

    /**
     * The final piece of data
     */
    private StringBuilder currentData;
    /**
     * Current element data
     */
    private StringBuilder currentElementData;

    private final ReadCache readCache;
    /**
     * Some fields in the T tag need to be ignored
     */
    private boolean ignoreTagt = false;
    /**
     * The only time you need to read the characters in the T tag is when it is used
     */
    private boolean isTagt = false;

    /**
     * Creates a handler that writes the parsed shared strings into the given cache.
     *
     * @param readCache the cache to populate, must not be {@code null}
     */
    public SharedStringsTableHandler(ReadCache readCache) {
        this.readCache = readCache;
    }

    /**
     * Starts tracking a shared-string item, a text run, or ignores a phonetic run.
     *
     * @param uri        the namespace URI
     * @param localName  the local element name
     * @param name       the qualified element name
     * @param attributes the element attributes (not used)
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        if (name == null) {
            return;
        }
        switch (name) {
            case ExcelXmlConstants.SHAREDSTRINGS_T_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_T_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_T_TAG:
                currentElementData = null;
                isTagt = true;
                break;
            case ExcelXmlConstants.SHAREDSTRINGS_SI_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_SI_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_SI_TAG:
                currentData = null;
                break;
            case ExcelXmlConstants.SHAREDSTRINGS_RPH_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_RPH_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_RPH_TAG:
                ignoreTagt = true;
                break;
            default:
                // ignore
        }
    }

    /**
     * Finishes the current text run or shared-string item and writes the assembled
     * value to the {@link ReadCache}. An {@code <si>} without any {@code <t>} content
     * is cached as {@code null}.
     *
     * @param uri       the namespace URI
     * @param localName the local element name
     * @param name      the qualified element name
     */
    @Override
    public void endElement(String uri, String localName, String name) {
        if (name == null) {
            return;
        }
        switch (name) {
            case ExcelXmlConstants.SHAREDSTRINGS_T_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_T_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_T_TAG:
                if (currentElementData != null) {
                    if (currentData == null) {
                        currentData = new StringBuilder();
                    }
                    currentData.append(currentElementData);
                }
                isTagt = false;
                break;
            case ExcelXmlConstants.SHAREDSTRINGS_SI_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_SI_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_SI_TAG:
                if (currentData == null) {
                    readCache.put(null);
                } else {
                    readCache.put(XlsxEscapeUtils.utfDecode(currentData.toString()));
                }
                break;
            case ExcelXmlConstants.SHAREDSTRINGS_RPH_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_X_RPH_TAG:
            case ExcelXmlConstants.SHAREDSTRINGS_NS2_RPH_TAG:
                ignoreTagt = false;
                break;
            default:
                // ignore
        }
    }

    /**
     * Appends character data of the current {@code <t>} element, unless it belongs
     * to an ignored phonetic run.
     *
     * @param ch     the characters from the XML document
     * @param start  the start position in the array
     * @param length the number of characters to read
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (!isTagt || ignoreTagt) {
            return;
        }
        if (currentElementData == null) {
            currentElementData = new StringBuilder();
        }
        currentElementData.append(ch, start, length);
    }
}

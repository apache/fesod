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

import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.util.XlsxEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sax read sharedStringsTable.xml
 *
 *
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

    public SharedStringsTableHandler(ReadCache readCache) {
        this.readCache = readCache;
    }

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

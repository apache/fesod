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

package org.apache.fesod.excel.metadata.csv;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.fesod.excel.constant.BuiltinFormats;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.MapUtils;
import org.apache.poi.ss.usermodel.DataFormat;

/**
 * format data
 *
 *
 */
public class CsvDataFormat implements DataFormat {
    /**
     * It is stored in both map and list for easy retrieval
     */
    private final Map<String, Short> formatMap;

    private final List<String> formatList;

    /**
     * Excel's built-in format conversion.
     */
    private final Map<String, Short> builtinFormatsMap;

    private final String[] builtinFormats;

    public CsvDataFormat(Locale locale) {
        formatMap = MapUtils.newHashMap();
        formatList = ListUtils.newArrayList();
        builtinFormatsMap = BuiltinFormats.switchBuiltinFormatsMap(locale);
        builtinFormats = BuiltinFormats.switchBuiltinFormats(locale);
    }

    @Override
    public short getFormat(String format) {
        Short index = builtinFormatsMap.get(format);
        if (index != null) {
            return index;
        }
        index = formatMap.get(format);
        if (index != null) {
            return index;
        }
        short indexPrimitive = (short) (formatList.size() + BuiltinFormats.MIN_CUSTOM_DATA_FORMAT_INDEX);
        index = indexPrimitive;
        formatList.add(format);
        formatMap.put(format, index);
        return indexPrimitive;
    }

    @Override
    public String getFormat(short index) {
        if (index < BuiltinFormats.MIN_CUSTOM_DATA_FORMAT_INDEX) {
            return builtinFormats[index];
        }
        int actualIndex = index - BuiltinFormats.MIN_CUSTOM_DATA_FORMAT_INDEX;
        if (actualIndex < formatList.size()) {
            return formatList.get(actualIndex);
        }
        return null;
    }
}

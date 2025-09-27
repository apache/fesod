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

package org.apache.fesod.excel.enums;

import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.excel.util.StringUtils;

/**
 * excel internal data type
 *
 *
 */
public enum CellDataTypeEnum {
    /**
     * string
     */
    STRING,
    /**
     * This type of data does not need to be read in the 'sharedStrings.xml', it is only used for overuse, and the data
     * will be stored as a {@link #STRING}
     */
    DIRECT_STRING,
    /**
     * number
     */
    NUMBER,
    /**
     * boolean
     */
    BOOLEAN,
    /**
     * empty
     */
    EMPTY,
    /**
     * error
     */
    ERROR,
    /**
     * date. Support only when writing.
     */
    DATE,
    /**
     * rich text string.Support only when writing.
     */
    RICH_TEXT_STRING;

    private static final Map<String, CellDataTypeEnum> TYPE_ROUTING_MAP = new HashMap<String, CellDataTypeEnum>(16);

    static {
        TYPE_ROUTING_MAP.put("s", STRING);
        TYPE_ROUTING_MAP.put("str", DIRECT_STRING);
        TYPE_ROUTING_MAP.put("inlineStr", DIRECT_STRING);
        TYPE_ROUTING_MAP.put("e", ERROR);
        TYPE_ROUTING_MAP.put("b", BOOLEAN);
        TYPE_ROUTING_MAP.put("n", NUMBER);
    }

    /**
     * Build data types
     *
     * @param cellType
     * @return
     */
    public static CellDataTypeEnum buildFromCellType(String cellType) {
        if (StringUtils.isEmpty(cellType)) {
            return EMPTY;
        }
        return TYPE_ROUTING_MAP.get(cellType);
    }
}

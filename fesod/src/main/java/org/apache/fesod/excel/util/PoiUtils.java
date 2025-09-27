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

package org.apache.fesod.excel.util;

import java.lang.reflect.Field;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 * utils
 *
 *
 */
public class PoiUtils {

    /**
     * Whether to customize the height
     */
    public static final BitField CUSTOM_HEIGHT = BitFieldFactory.getInstance(0x640);

    private static final Field ROW_RECORD_FIELD = FieldUtils.getField(HSSFRow.class, "row", true);

    /**
     * Whether to customize the height
     *
     * @param row row
     * @return
     */
    public static boolean customHeight(Row row) {
        if (row instanceof XSSFRow) {
            XSSFRow xssfRow = (XSSFRow) row;
            return xssfRow.getCTRow().getCustomHeight();
        }
        if (row instanceof HSSFRow) {
            HSSFRow hssfRow = (HSSFRow) row;
            try {
                RowRecord record = (RowRecord) ROW_RECORD_FIELD.get(hssfRow);
                return CUSTOM_HEIGHT.getValue(record.getOptionFlags()) == 1;
            } catch (IllegalAccessException ignore) {
            }
        }
        return false;
    }
}

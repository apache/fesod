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

package org.apache.fesod.sheet.metadata.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CellDataTest {

    /**
     * xlsx boolean cells use Excel's {@code 0}/{@code 1} markers, so only {@code "1"} maps to {@code true}.
     * This mirrors POI's {@code XSSFCell.getBooleanCellValue()}; {@code "true"} intentionally maps to
     * {@code false}.
     */
    @Test
    void setBooleanValueFromStringMatchesExcelMarkers() {
        Assertions.assertTrue(booleanFrom("1"));
        Assertions.assertFalse(booleanFrom("0"));
        Assertions.assertFalse(booleanFrom("true"));
        Assertions.assertFalse(booleanFrom("TRUE"));
        Assertions.assertFalse(booleanFrom(""));
        Assertions.assertFalse(booleanFrom(null));
    }

    private static Boolean booleanFrom(String str) {
        CellData<?> cellData = new CellData<>();
        cellData.setBooleanValueFromString(str);
        return cellData.getBooleanValue();
    }
}

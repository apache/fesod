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

package org.apache.fesod.excel.temp.issue406;

import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReadSheetTest {

    @Test
    void testAll() {
        testEquals_SameInstance();
        testEquals_Null();
        testEquals_DifferentClass();
        testEquals_EqualObjects();

        testHashCode_Consistency();
        testHashCode_EqualityForEqualObjects();

        // If @EqualsAndHashCode is not implemented, the following error will occur
        testEquals_DifferentSheetNo();
        testEquals_DifferentSheetName();
        testEquals_DifferentNumRows();
        testEquals_DifferentHiddenState();
        testEquals_DifferentVeryHiddenState();
    }

    @Test
    void testEquals_SameInstance() {
        ReadSheet sheet = new ReadSheet(1, "Sheet1");
        Assertions.assertEquals(sheet, sheet);
    }

    @Test
    void testEquals_Null() {
        ReadSheet sheet = new ReadSheet(1, "Sheet1");
        Assertions.assertNotEquals(null, sheet);
    }

    @Test
    void testEquals_DifferentClass() {
        ReadSheet sheet = new ReadSheet(1, "Sheet1");
        Assertions.assertNotEquals("Not a ReadSheet object", sheet);
    }

    @Test
    void testEquals_EqualObjects() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        sheet1.setNumRows(100);
        sheet1.setHidden(false);
        sheet1.setVeryHidden(true);

        ReadSheet sheet2 = new ReadSheet(1, "Sheet1");
        sheet2.setNumRows(100);
        sheet2.setHidden(false);
        sheet2.setVeryHidden(true);

        Assertions.assertEquals(sheet1, sheet2);
    }

    @Test
    void testEquals_DifferentSheetNo() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        ReadSheet sheet2 = new ReadSheet(2, "Sheet1");
        Assertions.assertNotEquals(sheet1, sheet2);
    }

    @Test
    void testEquals_DifferentSheetName() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        ReadSheet sheet2 = new ReadSheet(1, "Sheet2");
        Assertions.assertNotEquals(sheet1, sheet2);
    }

    @Test
    void testEquals_DifferentNumRows() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        sheet1.setNumRows(100);
        ReadSheet sheet2 = new ReadSheet(1, "Sheet1");
        sheet2.setNumRows(200);
        Assertions.assertNotEquals(sheet1, sheet2);
    }

    @Test
    void testEquals_DifferentHiddenState() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        sheet1.setHidden(true);
        ReadSheet sheet2 = new ReadSheet(1, "Sheet1");
        sheet2.setHidden(false);
        Assertions.assertNotEquals(sheet1, sheet2);
    }

    @Test
    void testEquals_DifferentVeryHiddenState() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        sheet1.setVeryHidden(true);
        ReadSheet sheet2 = new ReadSheet(1, "Sheet1");
        sheet2.setVeryHidden(false);
        Assertions.assertNotEquals(sheet1, sheet2);
    }

    @Test
    void testHashCode_Consistency() {
        ReadSheet sheet = new ReadSheet(1, "Sheet1");
        int initialHashCode = sheet.hashCode();
        Assertions.assertEquals(initialHashCode, sheet.hashCode());
    }

    @Test
    void testHashCode_EqualityForEqualObjects() {
        ReadSheet sheet1 = new ReadSheet(1, "Sheet1");
        sheet1.setNumRows(100);
        sheet1.setHidden(false);
        sheet1.setVeryHidden(true);

        ReadSheet sheet2 = new ReadSheet(1, "Sheet1");
        sheet2.setNumRows(100);
        sheet2.setHidden(false);
        sheet2.setVeryHidden(true);

        Assertions.assertEquals(sheet1.hashCode(), sheet2.hashCode());
    }
}

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

package org.apache.fesod.sheet.converters.charconverter;

import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class CharacterStringConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();
    private final CharacterStringConverter converter = new CharacterStringConverter();

    @Test
    void supportJavaTypeKey() {
        Assertions.assertEquals(Character.class, converter.supportJavaTypeKey());
    }

    @Test
    void supportExcelTypeKey() {
        Assertions.assertEquals(CellDataTypeEnum.STRING, converter.supportExcelTypeKey());
    }

    @Test
    void convertToJavaDataReturnsSingleCharacter() {
        Assertions.assertEquals(
                Character.valueOf('A'),
                converter.convertToJavaData(new ReadCellData<>("A"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataReturnsNullForEmptyString() {
        Assertions.assertNull(converter.convertToJavaData(new ReadCellData<>(""), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToJavaDataThrowsOnLongerStrings() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToJavaData(new ReadCellData<>("AB"), null, GLOBAL_CONFIGURATION));
    }

    @Test
    void convertToExcelDataWritesStringCell() {
        WriteCellData<?> cellData = converter.convertToExcelData('A', null, GLOBAL_CONFIGURATION);
        Assertions.assertEquals(CellDataTypeEnum.STRING, cellData.getType());
        Assertions.assertEquals("A", cellData.getStringValue());
    }
}

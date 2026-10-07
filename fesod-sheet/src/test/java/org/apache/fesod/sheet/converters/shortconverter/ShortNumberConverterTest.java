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

package org.apache.fesod.sheet.converters.shortconverter;

import java.math.BigDecimal;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ShortNumberConverter}.
 */
@Tag(Tags.UNIT)
class ShortNumberConverterTest {

    private final ShortNumberConverter converter = new ShortNumberConverter();

    @Test
    void convertToJavaDataAcceptsInRangeNumber() {
        Short actual = converter.convertToJavaData(new ReadCellData<>(new BigDecimal("100")), null, null);
        Assertions.assertEquals((short) 100, actual);
    }

    @Test
    void convertToJavaDataTruncatesFractionLikeBefore() {
        Short actual = converter.convertToJavaData(new ReadCellData<>(new BigDecimal("123.9")), null, null);
        Assertions.assertEquals((short) 123, actual);
    }

    @Test
    void convertToJavaDataAcceptsBoundaryValues() {
        Assertions.assertEquals(
                Short.MAX_VALUE, converter.convertToJavaData(new ReadCellData<>(new BigDecimal("32767")), null, null));
        Assertions.assertEquals(
                Short.MIN_VALUE, converter.convertToJavaData(new ReadCellData<>(new BigDecimal("-32768")), null, null));
    }

    @Test
    void convertToJavaDataRejectsOutOfRangeNumber() {
        Assertions.assertThrows(
                ArithmeticException.class,
                () -> converter.convertToJavaData(new ReadCellData<>(new BigDecimal("40000")), null, null));
    }
}

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

package org.apache.fesod.sheet.converters.bigdecimal;

import java.math.BigDecimal;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BigDecimalBooleanConverter}.
 */
@Tag(Tags.UNIT)
class BigDecimalBooleanConverterTest {

    private final BigDecimalBooleanConverter converter = new BigDecimalBooleanConverter();

    @Test
    void convertToExcelDataTreatsEveryValueEqualToOneAsTrueRegardlessOfScale() {
        Assertions.assertEquals(Boolean.TRUE, convertToBoolean(BigDecimal.ONE));
        Assertions.assertEquals(Boolean.TRUE, convertToBoolean(new BigDecimal("1.0")));
        Assertions.assertEquals(Boolean.TRUE, convertToBoolean(new BigDecimal("1.00")));
    }

    @Test
    void convertToExcelDataTreatsValuesOtherThanOneAsFalse() {
        Assertions.assertEquals(Boolean.FALSE, convertToBoolean(BigDecimal.ZERO));
        Assertions.assertEquals(Boolean.FALSE, convertToBoolean(new BigDecimal("0.0")));
        Assertions.assertEquals(Boolean.FALSE, convertToBoolean(new BigDecimal("2")));
        Assertions.assertEquals(Boolean.FALSE, convertToBoolean(null));
    }

    @Test
    void convertToJavaDataMapsBooleanToOneAndZero() {
        Assertions.assertEquals(
                BigDecimal.ONE, converter.convertToJavaData(new ReadCellData<>(Boolean.TRUE), null, null));
        Assertions.assertEquals(
                BigDecimal.ZERO, converter.convertToJavaData(new ReadCellData<>(Boolean.FALSE), null, null));
    }

    private Boolean convertToBoolean(BigDecimal value) {
        WriteCellData<?> cellData = converter.convertToExcelData(value, null, null);
        return cellData.getBooleanValue();
    }
}

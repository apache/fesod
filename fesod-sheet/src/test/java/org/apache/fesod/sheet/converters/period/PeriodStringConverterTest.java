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

package org.apache.fesod.sheet.converters.period;

import java.time.Period;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link PeriodStringConverter}.
 */
@Tag(Tags.UNIT)
class PeriodStringConverterTest {

    private final PeriodStringConverter converter = new PeriodStringConverter();

    @Test
    void convertToJavaDataParsesIsoPeriod() {
        Assertions.assertEquals(
                Period.of(1, 2, 3), converter.convertToJavaData(new ReadCellData<>("P1Y2M3D"), null, null));
    }

    @Test
    void convertToJavaDataRejectsInvalidPeriod() {
        Assertions.assertThrows(
                Exception.class, () -> converter.convertToJavaData(new ReadCellData<>("1Y2M3D"), null, null));
    }

    @Test
    void convertToExcelDataWritesIsoPeriod() {
        WriteCellData<?> cellData = converter.convertToExcelData(Period.of(1, 2, 3), null, null);
        Assertions.assertEquals("P1Y2M3D", cellData.getStringValue());
    }
}

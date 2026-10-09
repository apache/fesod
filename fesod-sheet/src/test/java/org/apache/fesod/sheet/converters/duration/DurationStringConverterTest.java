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

package org.apache.fesod.sheet.converters.duration;

import java.time.Duration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DurationStringConverter}.
 */
@Tag(Tags.UNIT)
class DurationStringConverterTest {

    private final DurationStringConverter converter = new DurationStringConverter();

    @Test
    void convertToJavaDataParsesIsoDuration() {
        Assertions.assertEquals(
                Duration.ofHours(5).plusMinutes(30),
                converter.convertToJavaData(new ReadCellData<>("PT5H30M"), null, null));
    }

    @Test
    void convertToJavaDataRejectsInvalidDuration() {
        Assertions.assertThrows(
                Exception.class, () -> converter.convertToJavaData(new ReadCellData<>("5h30m"), null, null));
    }

    @Test
    void convertToExcelDataWritesIsoDuration() {
        WriteCellData<?> cellData =
                converter.convertToExcelData(Duration.ofHours(5).plusMinutes(30), null, null);
        Assertions.assertEquals("PT5H30M", cellData.getStringValue());
    }
}

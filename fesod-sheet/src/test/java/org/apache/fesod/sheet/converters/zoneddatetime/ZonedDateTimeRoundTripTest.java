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

package org.apache.fesod.sheet.converters.zoneddatetime;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class ZonedDateTimeRoundTripTest extends AbstractExcelTest {
    @ParameterizedTest
    @ExcelFormatSource
    void roundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        ZonedDateTimeRoundTripData input = new ZonedDateTimeRoundTripData();
        input.setZoned(ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneId.of("UTC")));
        List<ZonedDateTimeRoundTripData> result =
                RoundTripHelper.writeAndRead(file, ZonedDateTimeRoundTripData.class, Collections.singletonList(input));

        Assertions.assertEquals(1, result.size());
        // Excel cells carry no zone; reading attaches the system default zone but keeps the local date-time.
        Assertions.assertEquals(
                input.getZoned().toLocalDateTime(), result.get(0).getZoned().toLocalDateTime());
        Assertions.assertEquals(ZoneId.systemDefault(), result.get(0).getZoned().getZone());
    }
}

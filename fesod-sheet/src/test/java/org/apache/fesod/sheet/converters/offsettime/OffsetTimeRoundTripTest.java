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

package org.apache.fesod.sheet.converters.offsettime;

import java.io.File;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class OffsetTimeRoundTripTest extends AbstractExcelTest {
    @ParameterizedTest
    @ExcelFormatSource(FormatScope.ALL)
    void roundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        OffsetTimeRoundTripData input = new OffsetTimeRoundTripData();
        input.setIso(OffsetTime.of(9, 30, 45, 0, ZoneOffset.ofHours(8)));
        input.setCustom(OffsetTime.of(18, 5, 1, 0, ZoneOffset.ofHours(-5)));
        List<OffsetTimeRoundTripData> result =
                RoundTripHelper.writeAndRead(file, OffsetTimeRoundTripData.class, Collections.singletonList(input));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(input.getIso(), result.get(0).getIso());
        Assertions.assertEquals(input.getCustom(), result.get(0).getCustom());
    }
}

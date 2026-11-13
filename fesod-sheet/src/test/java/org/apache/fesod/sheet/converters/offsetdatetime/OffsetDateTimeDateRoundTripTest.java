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

package org.apache.fesod.sheet.converters.offsetdatetime;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class OffsetDateTimeDateRoundTripTest extends AbstractExcelTest {
    private static final OffsetDateTime VALUE = OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(-5));

    @Test
    void csvDateConverterPreservesOffset() throws Exception {
        File file = createTempFile(ExcelFormat.CSV);
        OffsetDateTimeDateWriteData input = new OffsetDateTimeDateWriteData();
        input.setValue(VALUE);

        List<OffsetDateTimePlainReadData> result = RoundTripHelper.writeAndRead(
                file,
                OffsetDateTimeDateWriteData.class,
                Collections.singletonList(input),
                OffsetDateTimePlainReadData.class);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(VALUE, result.get(0).getValue());
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(content.contains("2020-01-02T03:04:05-05:00"), content);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void binaryDateConverterDropsOffset(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        OffsetDateTimeDateWriteData input = new OffsetDateTimeDateWriteData();
        input.setValue(VALUE);

        List<OffsetDateTimePlainReadData> result = RoundTripHelper.writeAndRead(
                file,
                OffsetDateTimeDateWriteData.class,
                Collections.singletonList(input),
                OffsetDateTimePlainReadData.class);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
                VALUE.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                result.get(0).getValue());
    }
}

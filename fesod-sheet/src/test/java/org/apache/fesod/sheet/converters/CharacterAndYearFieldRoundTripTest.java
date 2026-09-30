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

package org.apache.fesod.sheet.converters;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Field-level round trips for the Character and Year converters across every supported format.
 * The xlsx/xls legs exercise the wildcard write key ((type, null)); the csv leg exercises the
 * (type, STRING) key.
 */
@Tag(Tags.ROUND_TRIP)
public class CharacterAndYearFieldRoundTripTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void yearFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<YearFieldData> data = new ArrayList<>();
        YearFieldData row = new YearFieldData();
        row.setFlag(Year.of(2026));
        data.add(row);

        List<YearFieldData> result = RoundTripHelper.writeAndRead(file, YearFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Year.of(2026), result.get(0).getFlag());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void characterFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<CharacterFieldData> data = new ArrayList<>();
        CharacterFieldData row = new CharacterFieldData();
        row.setFlag('A');
        data.add(row);

        List<CharacterFieldData> result = RoundTripHelper.writeAndRead(file, CharacterFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(Character.valueOf('A'), result.get(0).getFlag());
    }

    @Getter
    @Setter
    public static class YearFieldData {

        @ExcelProperty("flag")
        private Year flag;
    }

    @Getter
    @Setter
    public static class CharacterFieldData {

        @ExcelProperty("flag")
        private Character flag;
    }
}

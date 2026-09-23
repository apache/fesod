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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converters.instant;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Round trip tests for the java.time.Instant converters.
 *
 * <p>Excel has no time zone, so the values are compared through the default time zone of the JVM.
 */
@Tag(Tags.ROUND_TRIP)
public class InstantDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, InstantData.class).sheet().doWrite(data());

        CollectingReadListener<InstantData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, InstantData.class, listener).sheet().doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        InstantData row = listener.getFirstRow();
        Assertions.assertEquals(instant("2020-01-01T00:00:00Z"), row.getInstant());
        Assertions.assertEquals(instant("2020-01-01T00:00:00Z"), row.getInstantString());
        Assertions.assertEquals(instant("2021-12-31T00:00:00Z"), row.getInstantFormattedString());
    }

    /**
     * The written value only keeps the precision of a second, so compare on the second.
     */
    private static Instant instant(String value) {
        return Instant.parse(value).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    }

    private List<InstantData> data() {
        List<InstantData> list = new ArrayList<InstantData>();
        InstantData data = new InstantData();
        data.setInstant(Instant.parse("2020-01-01T00:00:00Z"));
        data.setInstantString(Instant.parse("2020-01-01T00:00:00Z"));
        data.setInstantFormattedString(Instant.parse("2021-12-31T00:00:00Z"));
        list.add(data);
        return list;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class InstantData {
        @ExcelProperty("instant")
        private Instant instant;

        @ExcelProperty("instantString")
        private Instant instantString;

        @ExcelProperty("instantFormattedString")
        @DateTimeFormat("yyyy/MM/dd HH:mm:ss")
        private Instant instantFormattedString;
    }
}

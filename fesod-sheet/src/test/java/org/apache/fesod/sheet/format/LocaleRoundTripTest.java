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

package org.apache.fesod.sheet.format;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Round-trip test for the {@code GlobalConfiguration.locale} fix: a POJO with both a {@link Date} and a
 * {@link LocalDateTime} field is written and read with the configured locale through the real
 * {@code WriteContext}/{@code AnalysisContext} wiring, verifying the locale-configured read path
 * reconstructs the original values.
 */
@Tag(Tags.ROUND_TRIP)
public class LocaleRoundTripTest extends AbstractExcelTest {

    private static final String LOCALIZED_PATTERN = "dd MMMM yyyy";

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void writeAndReadHonorsConfiguredLocaleForDateAndLocalDateTime(ExcelFormat format) throws Exception {
        Locale locale = Locale.CHINESE;
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2026-11-16");
        LocalDateTime localDateTime = LocalDateTime.of(2026, 11, 16, 0, 0);

        LocaleBean bean = new LocaleBean();
        bean.setDate(date);
        bean.setLocalDateTime(localDateTime);
        List<LocaleBean> data = new ArrayList<>();
        data.add(bean);

        File file = createTempFile("locale-roundtrip", format);

        // Write with the Chinese locale.
        FesodSheet.write(file, LocaleBean.class).locale(locale).sheet().doWrite(data);

        // Read back with the same locale.
        List<LocaleBean> result = FesodSheet.read(file, LocaleBean.class, null)
                .locale(locale)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(date, result.get(0).getDate());
        Assertions.assertEquals(localDateTime, result.get(0).getLocalDateTime());
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class LocaleBean {
        @ExcelProperty("date")
        @DateTimeFormat(LOCALIZED_PATTERN)
        private Date date;

        @ExcelProperty("ldt")
        @DateTimeFormat(LOCALIZED_PATTERN)
        private LocalDateTime localDateTime;
    }
}

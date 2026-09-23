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

package org.apache.fesod.sheet.converters.sqltime;

import java.io.File;
import java.sql.Time;
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
 * Round trip tests for the java.sql.Time converters.
 */
@Tag(Tags.ROUND_TRIP)
public class SqlTimeDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, SqlTimeData.class).sheet().doWrite(data());

        CollectingReadListener<SqlTimeData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, SqlTimeData.class, listener).sheet().doRead();

        Assertions.assertEquals(1, listener.getRowCount());
        SqlTimeData row = listener.getFirstRow();
        Assertions.assertEquals(Time.valueOf("13:14:15"), row.getTime());
        Assertions.assertEquals(Time.valueOf("01:02:03"), row.getTimeString());
        Assertions.assertEquals(Time.valueOf("23:59:00"), row.getTimeFormattedString());
    }

    private List<SqlTimeData> data() {
        List<SqlTimeData> list = new ArrayList<SqlTimeData>();
        SqlTimeData data = new SqlTimeData();
        data.setTime(Time.valueOf("13:14:15"));
        data.setTimeString(Time.valueOf("01:02:03"));
        // HH:mm has no seconds, so use a value that the format keeps completely
        data.setTimeFormattedString(Time.valueOf("23:59:00"));
        list.add(data);
        return list;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class SqlTimeData {
        @ExcelProperty("time")
        private Time time;

        @ExcelProperty("timeString")
        private Time timeString;

        @ExcelProperty("timeFormattedString")
        @DateTimeFormat("HH:mm")
        private Time timeFormattedString;
    }
}

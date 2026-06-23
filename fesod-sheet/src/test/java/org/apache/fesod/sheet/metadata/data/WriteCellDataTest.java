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

package org.apache.fesod.sheet.metadata.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WriteCellDataTest {

    @Test
    void constructorShouldSupportSqlDate() {
        WriteCellData<?> cellData = new WriteCellData<>(java.sql.Date.valueOf("2026-05-07"));

        Assertions.assertEquals(CellDataTypeEnum.DATE, cellData.getType());
        Assertions.assertEquals(
                LocalDate.of(2026, 5, 7), cellData.getDateValue().toLocalDate());
    }

    @Test
    void constructorShouldSupportSqlTime() {
        WriteCellData<?> cellData = new WriteCellData<>(java.sql.Time.valueOf("12:34:56"));

        Assertions.assertEquals(CellDataTypeEnum.DATE, cellData.getType());
        Assertions.assertEquals(
                LocalTime.of(12, 34, 56), cellData.getDateValue().toLocalTime());
    }

    @Test
    void constructorShouldKeepSupportingSqlTimestampNanos() {
        WriteCellData<?> cellData = new WriteCellData<>(java.sql.Timestamp.valueOf("2026-05-07 12:34:56.789123456"));

        Assertions.assertEquals(CellDataTypeEnum.DATE, cellData.getType());
        Assertions.assertEquals(LocalDateTime.of(2026, 5, 7, 12, 34, 56, 789_123_456), cellData.getDateValue());
    }
}

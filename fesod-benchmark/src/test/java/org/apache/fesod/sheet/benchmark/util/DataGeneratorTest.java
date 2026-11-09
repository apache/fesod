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

package org.apache.fesod.sheet.benchmark.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.fesod.sheet.benchmark.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataGeneratorTest {

    private static final int ROWS = 200;

    @Test
    void sameSeedProducesIdenticalData() {
        List<BenchmarkData> first = new DataGenerator(42L).generateData(ROWS);
        List<BenchmarkData> second = new DataGenerator(42L).generateData(ROWS);

        Assertions.assertEquals(
                first.stream().map(BenchmarkData::toString).collect(Collectors.toList()),
                second.stream().map(BenchmarkData::toString).collect(Collectors.toList()));
    }

    @Test
    void defaultConstructorUsesFixedSeed() {
        List<BenchmarkData> fromDefault = new DataGenerator().generateData(ROWS);
        List<BenchmarkData> fromSeed42 = new DataGenerator(42L).generateData(ROWS);

        Assertions.assertEquals(
                fromSeed42.stream().map(BenchmarkData::toString).collect(Collectors.toList()),
                fromDefault.stream().map(BenchmarkData::toString).collect(Collectors.toList()));
    }

    @Test
    void differentSeedsProduceDifferentData() {
        List<String> first = new DataGenerator(42L)
                .generateData(ROWS).stream().map(BenchmarkData::toString).collect(Collectors.toList());
        List<String> second = new DataGenerator(43L)
                .generateData(ROWS).stream().map(BenchmarkData::toString).collect(Collectors.toList());

        Assertions.assertFalse(first.equals(second));
    }

    @Test
    void generatedRowsAreComplete() {
        List<BenchmarkData> data = new DataGenerator(7L).generateData(ROWS);

        Assertions.assertEquals(ROWS, data.size());
        for (int i = 0; i < data.size(); i++) {
            BenchmarkData row = data.get(i);
            Assertions.assertEquals(i + 1L, row.getId());
            Assertions.assertFalse(row.getStringData().isEmpty());
            Assertions.assertFalse(row.getCategory().isEmpty());
            Assertions.assertFalse(row.getStatus().isEmpty());
        }
    }

    @Test
    void datesStayWithinAnchoredWindows() {
        List<BenchmarkData> data = new DataGenerator(7L).generateData(ROWS);

        LocalDate dateLowerBound = LocalDate.of(2019, 1, 1);
        LocalDate dateUpperBound = LocalDate.of(2024, 1, 1);
        LocalDateTime dateTimeLowerBound = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime dateTimeUpperBound = LocalDateTime.of(2024, 1, 1, 0, 0);

        for (BenchmarkData row : data) {
            Assertions.assertTrue(row.getDateValue().isAfter(dateLowerBound.minusDays(1)));
            Assertions.assertTrue(row.getDateValue().isBefore(dateUpperBound.plusDays(1)));
            Assertions.assertFalse(row.getDateTimeValue().isBefore(dateTimeLowerBound));
            Assertions.assertFalse(row.getDateTimeValue().isAfter(dateTimeUpperBound));
        }
    }

    @Test
    void generateTestDataMatchesDatasetSize() {
        for (BenchmarkConfiguration.DatasetSize size : BenchmarkConfiguration.DatasetSize.values()) {
            Assertions.assertEquals(
                    size.getRowCount(), DataGenerator.generateTestData(size).size());
        }
    }
}

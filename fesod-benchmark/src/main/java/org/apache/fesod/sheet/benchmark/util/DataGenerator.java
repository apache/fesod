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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.fesod.sheet.benchmark.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates deterministic benchmark data: a fixed default seed and a fixed date
 * anchor mean the same seed always produces the same rows, so benchmark results
 * from different runs are comparable.
 */
public class DataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    /** Fixed anchor for generated dates; never uses the wall clock. */
    private static final LocalDate DATE_ANCHOR = LocalDate.of(2024, 1, 1);

    private static final String[] CATEGORIES = {
        "Electronics",
        "Books",
        "Clothing",
        "Home & Garden",
        "Sports",
        "Automotive",
        "Health & Beauty",
        "Toys & Games",
        "Food & Beverage",
        "Office Supplies"
    };

    private static final String[] STATUSES = {
        "Active", "Inactive", "Pending", "Processing", "Completed", "Cancelled", "On Hold"
    };

    private static final String[] SAMPLE_WORDS = {
        "Lorem",
        "ipsum",
        "dolor",
        "sit",
        "amet",
        "consectetur",
        "adipiscing",
        "elit",
        "sed",
        "do",
        "eiusmod",
        "tempor",
        "incididunt",
        "ut",
        "labore",
        "et",
        "dolore",
        "magna",
        "aliqua",
        "enim",
        "ad",
        "minim",
        "veniam",
        "quis",
        "nostrud",
        "exercitation",
        "ullamco",
        "laboris",
        "nisi",
        "aliquip",
        "ex",
        "ea",
        "commodo"
    };

    private final Random random;

    public DataGenerator() {
        this(42L);
    }

    public DataGenerator(long seed) {
        this.random = new Random(seed);
    }

    public List<BenchmarkData> generateData(BenchmarkConfiguration.DatasetSize size) {
        return generateData(size.getRowCount());
    }

    public List<BenchmarkData> generateData(int rowCount) {
        logger.info("Generating {} rows of benchmark data", rowCount);

        List<BenchmarkData> data = new ArrayList<>(rowCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < rowCount; i++) {
            data.add(generateSingleRow(i + 1));
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info(
                "Generated {} rows in {} ms ({} rows/sec)",
                rowCount,
                duration,
                duration > 0 ? rowCount * 1000L / duration : "N/A");

        return data;
    }

    private BenchmarkData generateSingleRow(long id) {
        BenchmarkData data = new BenchmarkData();

        data.setId(id);
        data.setStringData(generateRandomString(10, 50));
        data.setIntValue(random.nextInt(1_000_000));
        data.setLongValue(random.nextLong());
        data.setDoubleValue(random.nextDouble() * 1_000_000);
        data.setBigDecimalValue(
                BigDecimal.valueOf(random.nextDouble() * 1_000_000).setScale(2, RoundingMode.HALF_UP));
        data.setBooleanFlag(random.nextBoolean());
        data.setDateValue(generateRandomDate());
        data.setDateTimeValue(generateRandomDateTime());
        data.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
        data.setDescription(generateRandomDescription());
        data.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
        data.setFloatValue(random.nextFloat() * 1000);
        data.setShortValue((short) random.nextInt(Short.MAX_VALUE));
        data.setByteValue((byte) random.nextInt(Byte.MAX_VALUE));
        data.setExtraData1(generateRandomString(5, 20));
        data.setExtraData2(generateRandomString(5, 20));
        data.setExtraData3(generateRandomString(5, 20));
        data.setExtraData4(generateRandomString(5, 20));
        data.setExtraData5(generateRandomString(5, 20));

        return data;
    }

    private String generateRandomString(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                sb.append((char) ('a' + random.nextInt(26)));
            } else {
                sb.append((char) ('0' + random.nextInt(10)));
            }
        }

        return sb.toString();
    }

    private String generateRandomDescription() {
        int wordCount = random.nextInt(8) + 3; // 3-10 words
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(SAMPLE_WORDS[random.nextInt(SAMPLE_WORDS.length)]);
        }

        return sb.toString();
    }

    /** Random date in the five years before {@link #DATE_ANCHOR}. */
    private LocalDate generateRandomDate() {
        LocalDate fiveYearsAgo = DATE_ANCHOR.minusYears(5);
        long daysBetween = ChronoUnit.DAYS.between(fiveYearsAgo, DATE_ANCHOR);
        long randomDays = Math.floorMod(random.nextLong(), daysBetween);
        return fiveYearsAgo.plusDays(randomDays);
    }

    /** Random datetime in the year before {@link #DATE_ANCHOR}. */
    private LocalDateTime generateRandomDateTime() {
        LocalDateTime oneYearAgo = DATE_ANCHOR.atStartOfDay().minusYears(1);
        long secondsBetween = ChronoUnit.SECONDS.between(oneYearAgo, DATE_ANCHOR.atStartOfDay());
        long randomSeconds = Math.floorMod(random.nextLong(), secondsBetween);
        return oneYearAgo.plusSeconds(randomSeconds);
    }

    private static final DataGenerator defaultGenerator = new DataGenerator(42L);

    /**
     * Generate test data with the default generator (fixed seed).
     */
    public static List<BenchmarkData> generateTestData(BenchmarkConfiguration.DatasetSize size) {
        return defaultGenerator.generateData(size);
    }
}

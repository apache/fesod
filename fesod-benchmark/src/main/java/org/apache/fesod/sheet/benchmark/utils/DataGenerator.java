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

package org.apache.fesod.sheet.benchmark.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.fesod.sheet.benchmark.core.BenchmarkConfiguration;
import org.apache.fesod.sheet.benchmark.data.BenchmarkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for generating test data for benchmarks
 */
public class DataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    // Predefined data sets for realistic data generation
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
        this.random = ThreadLocalRandom.current();
    }

    public DataGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generate a list of benchmark data with the specified size
     */
    public List<BenchmarkData> generateData(BenchmarkConfiguration.DatasetSize size) {
        return generateData(size.getRowCount());
    }

    /**
     * Generate a list of benchmark data with the specified row count
     */
    public List<BenchmarkData> generateData(int rowCount) {
        logger.info("Generating {} rows of benchmark data", rowCount);

        List<BenchmarkData> data = new ArrayList<>(rowCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < rowCount; i++) {
            data.add(generateSingleRow(i + 1));

            // Log progress for large datasets
            if (rowCount > 10000 && i > 0 && i % 10000 == 0) {
                logger.debug("Generated {} rows", i);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info(
                "Generated {} rows in {} ms ({} rows/sec)",
                rowCount,
                duration,
                duration > 0 ? (rowCount * 1000 / duration) : "N/A");

        return data;
    }

    /**
     * Generate benchmark data in batches to control memory usage
     */
    public List<List<BenchmarkData>> generateDataInBatches(int totalRows, int batchSize) {
        logger.info("Generating {} rows in batches of {}", totalRows, batchSize);

        List<List<BenchmarkData>> batches = new ArrayList<>();
        int remainingRows = totalRows;
        int currentBatch = 1;
        int startId = 1;

        while (remainingRows > 0) {
            int currentBatchSize = Math.min(batchSize, remainingRows);
            List<BenchmarkData> batch = new ArrayList<>(currentBatchSize);

            for (int i = 0; i < currentBatchSize; i++) {
                batch.add(generateSingleRow(startId + i));
            }

            batches.add(batch);
            remainingRows -= currentBatchSize;
            startId += currentBatchSize;

            logger.debug("Generated batch {} with {} rows", currentBatch++, currentBatchSize);
        }

        logger.info("Generated {} batches totaling {} rows", batches.size(), totalRows);
        return batches;
    }

    /**
     * Generate a single row of benchmark data
     */
    private BenchmarkData generateSingleRow(long id) {
        BenchmarkData data = new BenchmarkData();

        data.setId(id);
        data.setStringData(generateRandomString(10, 50));
        data.setIntValue(random.nextInt(1000000));
        data.setLongValue(random.nextLong());
        data.setDoubleValue(random.nextDouble() * 1000000);
        data.setBigDecimalValue(
                BigDecimal.valueOf(random.nextDouble() * 1000000).setScale(2, RoundingMode.HALF_UP));
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

    /**
     * Generate random string with variable length
     */
    private String generateRandomString(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                // Add random letter
                sb.append((char) ('a' + random.nextInt(26)));
            } else {
                // Add random digit
                sb.append((char) ('0' + random.nextInt(10)));
            }
        }

        return sb.toString();
    }

    /**
     * Generate random description using sample words
     */
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

    /**
     * Generate random date within the last 5 years
     */
    private LocalDate generateRandomDate() {
        LocalDate now = LocalDate.now();
        LocalDate fiveYearsAgo = now.minusYears(5);
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(fiveYearsAgo, now);
        long randomDays = random.nextLong() % daysBetween;
        return fiveYearsAgo.plusDays(Math.abs(randomDays));
    }

    /**
     * Generate random datetime within the last year
     */
    private LocalDateTime generateRandomDateTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);
        long secondsBetween = java.time.temporal.ChronoUnit.SECONDS.between(oneYearAgo, now);
        long randomSeconds = random.nextLong() % secondsBetween;
        return oneYearAgo.plusSeconds(Math.abs(randomSeconds));
    }

    /**
     * Generate memory-efficient streaming data
     */
    public DataStream generateStreamingData(int totalRows) {
        return new DataStream(totalRows, this);
    }

    /**
     * Iterator-based data stream for memory-efficient data generation
     */
    public static class DataStream implements Iterable<BenchmarkData> {
        private final int totalRows;
        private final DataGenerator generator;

        public DataStream(int totalRows, DataGenerator generator) {
            this.totalRows = totalRows;
            this.generator = generator;
        }

        @Override
        public java.util.Iterator<BenchmarkData> iterator() {
            return new java.util.Iterator<BenchmarkData>() {
                private int currentRow = 0;

                @Override
                public boolean hasNext() {
                    return currentRow < totalRows;
                }

                @Override
                public BenchmarkData next() {
                    if (!hasNext()) {
                        throw new java.util.NoSuchElementException();
                    }
                    return generator.generateSingleRow(++currentRow);
                }
            };
        }

        public int getTotalRows() {
            return totalRows;
        }
    }

    /**
     * Generate data with specific characteristics for performance testing
     */
    public List<BenchmarkData> generateDataWithCharacteristics(int rowCount, DataCharacteristics characteristics) {
        logger.info("Generating {} rows with specific characteristics: {}", rowCount, characteristics);

        List<BenchmarkData> data = new ArrayList<>(rowCount);

        for (int i = 0; i < rowCount; i++) {
            BenchmarkData row = generateSingleRow(i + 1);

            // Apply characteristics
            if (characteristics.isLargeStrings()) {
                row.setStringData(generateRandomString(100, 500));
                row.setDescription(generateLargeDescription());
            }

            if (characteristics.isRepeatedValues()) {
                // Use limited set of values to create repetition
                row.setCategory(CATEGORIES[i % 3]);
                row.setStatus(STATUSES[i % 2]);
            }

            if (characteristics.isNullValues()) {
                // Randomly nullify some fields
                if (random.nextFloat() < 0.1) { // 10% chance
                    row.setExtraData1(null);
                    row.setExtraData2(null);
                }
            }

            data.add(row);
        }

        return data;
    }

    private String generateLargeDescription() {
        int sentenceCount = random.nextInt(10) + 5; // 5-14 sentences
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sentenceCount; i++) {
            if (i > 0) {
                sb.append(". ");
            }

            int wordsInSentence = random.nextInt(15) + 5; // 5-19 words per sentence
            for (int j = 0; j < wordsInSentence; j++) {
                if (j > 0) {
                    sb.append(" ");
                }
                sb.append(SAMPLE_WORDS[random.nextInt(SAMPLE_WORDS.length)]);
            }
        }

        return sb.toString();
    }

    /**
     * Configuration class for data characteristics
     */
    public static class DataCharacteristics {
        private boolean largeStrings = false;
        private boolean repeatedValues = false;
        private boolean nullValues = false;

        public static DataCharacteristics defaults() {
            return new DataCharacteristics();
        }

        public DataCharacteristics withLargeStrings() {
            this.largeStrings = true;
            return this;
        }

        public DataCharacteristics withRepeatedValues() {
            this.repeatedValues = true;
            return this;
        }

        public DataCharacteristics withNullValues() {
            this.nullValues = true;
            return this;
        }

        public boolean isLargeStrings() {
            return largeStrings;
        }

        public boolean isRepeatedValues() {
            return repeatedValues;
        }

        public boolean isNullValues() {
            return nullValues;
        }

        @Override
        public String toString() {
            return "DataCharacteristics{" + "largeStrings="
                    + largeStrings + ", repeatedValues="
                    + repeatedValues + ", nullValues="
                    + nullValues + '}';
        }
    }

    // Static convenience methods for backward compatibility
    private static final DataGenerator defaultGenerator = new DataGenerator();

    /**
     * Generate test data list using default generator
     */
    public static List<BenchmarkData> generateTestDataList(int rowCount) {
        return defaultGenerator.generateData(rowCount);
    }

    /**
     * Generate test data with specific size using default generator
     */
    public static List<BenchmarkData> generateTestDataList(BenchmarkConfiguration.DatasetSize size) {
        return defaultGenerator.generateData(size);
    }

    /**
     * Generate test data with specific size (alias method)
     */
    public static List<BenchmarkData> generateTestData(BenchmarkConfiguration.DatasetSize size) {
        return defaultGenerator.generateData(size);
    }

    /**
     * Alias for BenchmarkData to maintain compatibility
     */
    public static class TestData extends BenchmarkData {
        // This class exists for backward compatibility
    }
}

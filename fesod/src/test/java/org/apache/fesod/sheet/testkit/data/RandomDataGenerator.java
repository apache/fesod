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

package org.apache.fesod.sheet.testkit.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import org.apache.fesod.sheet.model.ComplexData;
import org.apache.fesod.sheet.model.SimpleData;

/**
 * Generates random test data for fuzz testing and edge case validation.
 * Helps identify issues with unexpected input values.
 */
public class RandomDataGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] NAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "测试A", "测试B", "测试C", "验证", "校验", "数据", "样本", "示例"
    };

    /**
     * Generates a random string.
     */
    public static String randomString() {
        int length = RANDOM.nextInt(20) + 1; // 1-20 characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) (RANDOM.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Generates a random Chinese name.
     */
    public static String randomChineseName() {
        return NAMES[RANDOM.nextInt(NAMES.length)] + RANDOM.nextInt(100);
    }

    /**
     * Generates a random number.
     */
    public static Double randomDouble() {
        return RANDOM.nextDouble() * 10000 - 5000; // -5000 to 5000
    }

    /**
     * Generates a random integer.
     */
    public static Integer randomInteger() {
        return RANDOM.nextInt(10000) - 5000; // -5000 to 4999
    }

    /**
     * Generates a random date in the last 10 years.
     */
    public static Date randomDate() {
        long start = System.currentTimeMillis() - (365L * 10 * 24 * 60 * 60 * 1000); // 10 years ago
        long end = System.currentTimeMillis();
        long randomTime = start + (long) (RANDOM.nextDouble() * (end - start));
        return new Date(randomTime);
    }

    /**
     * Generates random SimpleData.
     */
    public static SimpleData randomSimpleData() {
        return new SimpleData(randomChineseName(), randomDouble(), randomInteger());
    }

    /**
     * Generates random ComplexData.
     */
    public static ComplexData randomComplexData() {
        ComplexData data = new ComplexData();
        data.setString(randomString());
        data.setDate(randomDate());
        data.setDoubleData(randomDouble());
        data.setBooleanData(RANDOM.nextBoolean());
        data.setBigDecimal(BigDecimal.valueOf(randomDouble()));
        data.setBigInteger(BigInteger.valueOf(randomInteger()));
        data.setLongData(randomInteger().longValue());
        data.setIntegerData(randomInteger());
        data.setShortData(randomInteger().shortValue());
        data.setByteData(randomInteger().byteValue());
        data.setFloatData(randomDouble().floatValue());
        data.setLocalDate(LocalDate.now().minusDays(RANDOM.nextInt(365 * 5))); // Last 5 years
        data.setLocalDateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365 * 5)));
        return data;
    }

    /**
     * Generates multiple random SimpleData instances.
     */
    public static SimpleData[] randomSimpleDataArray(int count) {
        SimpleData[] array = new SimpleData[count];
        for (int i = 0; i < count; i++) {
            array[i] = randomSimpleData();
        }
        return array;
    }

    /**
     * Generates multiple random ComplexData instances.
     */
    public static ComplexData[] randomComplexDataArray(int count) {
        ComplexData[] array = new ComplexData[count];
        for (int i = 0; i < count; i++) {
            array[i] = randomComplexData();
        }
        return array;
    }
}

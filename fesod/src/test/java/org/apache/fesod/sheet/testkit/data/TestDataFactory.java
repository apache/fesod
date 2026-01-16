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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.fesod.sheet.model.ComplexData;
import org.apache.fesod.sheet.model.SimpleData;

/**
 * Static factory for creating test data instances.
 * Provides consistent test data generation across all test classes.
 */
public class TestDataFactory {

    private static final Random RANDOM = new Random();

    // ==================== SimpleData Factories ====================

    /**
     * Creates a single SimpleData instance with default values.
     */
    public static SimpleData simpleData() {
        return new SimpleData("测试数据", 100.0, 42);
    }

    /**
     * Creates a SimpleData instance with sequential name.
     */
    public static SimpleData simpleData(int index) {
        return new SimpleData("姓名" + index, (double) index, index);
    }

    /**
     * Creates a list of SimpleData instances.
     */
    public static List<SimpleData> simpleDataList(int count) {
        List<SimpleData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(simpleData(i));
        }
        return list;
    }

    /**
     * Creates a list of SimpleData with sequential names and incrementing numbers.
     */
    public static List<SimpleData> simpleDataList(int count, String prefix) {
        List<SimpleData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new SimpleData(prefix + i, (double) i, i));
        }
        return list;
    }

    // ==================== ComplexData Factories ====================

    /**
     * Creates a single ComplexData instance with default values.
     */
    public static ComplexData complexData() {
        ComplexData data = new ComplexData();
        data.setString("测试字符串");
        data.setDate(new Date());
        data.setDoubleData(123.45);
        data.setBooleanData(true);
        data.setBigDecimal(new BigDecimal("123.45"));
        data.setBigInteger(BigInteger.valueOf(12345));
        data.setLongData(12345L);
        data.setIntegerData(1234);
        data.setShortData((short) 123);
        data.setByteData((byte) 42);
        data.setFloatData(12.34f);
        data.setLocalDate(LocalDate.now());
        data.setLocalDateTime(LocalDateTime.now());
        return data;
    }

    /**
     * Creates a ComplexData instance with custom string.
     */
    public static ComplexData complexData(String value) {
        ComplexData data = complexData();
        data.setString(value);
        return data;
    }

    /**
     * Creates a list of ComplexData instances.
     */
    public static List<ComplexData> complexDataList(int count) {
        List<ComplexData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(complexData("数据" + i));
        }
        return list;
    }

    /**
     * Creates a list of ComplexData with the same values repeated.
     */
    public static List<ComplexData> complexDataList(int count, String prefix) {
        List<ComplexData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(complexData(prefix + i));
        }
        return list;
    }

    // ==================== Random Data Generation ====================

    /**
     * Creates a SimpleData with random values.
     */
    public static SimpleData randomSimpleData() {
        return new SimpleData("随机姓名" + RANDOM.nextInt(1000), RANDOM.nextDouble() * 1000, RANDOM.nextInt(100));
    }

    /**
     * Creates a ComplexData with random values.
     */
    public static ComplexData randomComplexData() {
        ComplexData data = new ComplexData();
        data.setString("随机字符串" + RANDOM.nextInt(10000));
        data.setDate(new Date(System.currentTimeMillis() - RANDOM.nextInt(100000000)));
        data.setDoubleData(RANDOM.nextDouble() * 10000);
        data.setBooleanData(RANDOM.nextBoolean());
        data.setBigDecimal(new BigDecimal(String.valueOf(RANDOM.nextDouble() * 10000)));
        data.setBigInteger(BigInteger.valueOf(RANDOM.nextInt(100000)));
        data.setLongData(RANDOM.nextInt(100000));
        data.setIntegerData(RANDOM.nextInt(10000));
        data.setShortData((short) RANDOM.nextInt(1000));
        data.setByteData((byte) RANDOM.nextInt(100));
        data.setFloatData(RANDOM.nextFloat() * 1000);
        data.setLocalDate(LocalDate.now().minusDays(RANDOM.nextInt(365)));
        data.setLocalDateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
        return data;
    }

    /**
     * Creates a list of random SimpleData instances.
     */
    public static List<SimpleData> randomSimpleDataList(int count) {
        List<SimpleData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(randomSimpleData());
        }
        return list;
    }

    /**
     * Creates a list of random ComplexData instances.
     */
    public static List<ComplexData> randomComplexDataList(int count) {
        List<ComplexData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(randomComplexData());
        }
        return list;
    }
}

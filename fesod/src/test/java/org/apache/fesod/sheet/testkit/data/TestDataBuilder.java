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
import org.apache.fesod.sheet.model.ComplexData;
import org.apache.fesod.sheet.model.SimpleData;

/**
 * Builder pattern for creating complex test data with fluent API.
 * Useful for creating test data with specific field values without
 * having to set all fields manually.
 */
public class TestDataBuilder {

    // ==================== SimpleData Builder ====================

    public static SimpleDataBuilder simpleData() {
        return new SimpleDataBuilder();
    }

    public static class SimpleDataBuilder {
        private String name = "默认姓名";
        private Double number = 0.0;
        private Integer integer = 0;

        public SimpleDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleDataBuilder withNumber(Double number) {
            this.number = number;
            return this;
        }

        public SimpleDataBuilder withInteger(Integer integer) {
            this.integer = integer;
            return this;
        }

        public SimpleData build() {
            SimpleData data = new SimpleData();
            data.setName(name);
            data.setNumber(number);
            data.setInteger(integer);
            return data;
        }
    }

    // ==================== ComplexData Builder ====================

    public static ComplexDataBuilder complexData() {
        return new ComplexDataBuilder();
    }

    public static class ComplexDataBuilder {
        private String string = "默认字符串";
        private Date date = new Date();
        private LocalDate localDate = LocalDate.now();
        private LocalDateTime localDateTime = LocalDateTime.now();
        private Boolean booleanData = true;
        private BigDecimal bigDecimal = BigDecimal.ZERO;
        private BigInteger bigInteger = BigInteger.ZERO;
        private long longData = 0L;
        private Integer integerData = 0;
        private Short shortData = (short) 0;
        private Byte byteData = (byte) 0;
        private double doubleData = 0.0;
        private Float floatData = 0.0f;

        public ComplexDataBuilder withString(String string) {
            this.string = string;
            return this;
        }

        public ComplexDataBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public ComplexDataBuilder withLocalDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }

        public ComplexDataBuilder withLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }

        public ComplexDataBuilder withBoolean(Boolean booleanData) {
            this.booleanData = booleanData;
            return this;
        }

        public ComplexDataBuilder withBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
            return this;
        }

        public ComplexDataBuilder withBigInteger(BigInteger bigInteger) {
            this.bigInteger = bigInteger;
            return this;
        }

        public ComplexDataBuilder withLong(long longData) {
            this.longData = longData;
            return this;
        }

        public ComplexDataBuilder withInteger(Integer integerData) {
            this.integerData = integerData;
            return this;
        }

        public ComplexDataBuilder withShort(Short shortData) {
            this.shortData = shortData;
            return this;
        }

        public ComplexDataBuilder withByte(Byte byteData) {
            this.byteData = byteData;
            return this;
        }

        public ComplexDataBuilder withDouble(double doubleData) {
            this.doubleData = doubleData;
            return this;
        }

        public ComplexDataBuilder withFloat(Float floatData) {
            this.floatData = floatData;
            return this;
        }

        public ComplexData build() {
            ComplexData data = new ComplexData();
            data.setString(string);
            data.setDate(date);
            data.setLocalDate(localDate);
            data.setLocalDateTime(localDateTime);
            data.setBooleanData(booleanData);
            data.setBigDecimal(bigDecimal);
            data.setBigInteger(bigInteger);
            data.setLongData(longData);
            data.setIntegerData(integerData);
            data.setShortData(shortData);
            data.setByteData(byteData);
            data.setDoubleData(doubleData);
            data.setFloatData(floatData);
            return data;
        }
    }
}

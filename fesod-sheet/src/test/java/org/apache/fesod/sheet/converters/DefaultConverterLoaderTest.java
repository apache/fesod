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

package org.apache.fesod.sheet.converters;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import org.apache.fesod.sheet.converters.ConverterKeyBuild.ConverterKey;
import org.apache.fesod.sheet.converters.instant.InstantStringConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeDateConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeNumberConverter;
import org.apache.fesod.sheet.converters.localtime.LocalTimeStringConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeDateConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeNumberConverter;
import org.apache.fesod.sheet.converters.sqltime.SqlTimeStringConverter;
import org.apache.fesod.sheet.converters.timestamp.TimestampDateConverter;
import org.apache.fesod.sheet.converters.timestamp.TimestampNumberConverter;
import org.apache.fesod.sheet.converters.timestamp.TimestampStringConverter;
import org.apache.fesod.sheet.converters.uuid.UUIDStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultConverterLoaderTest {

    @Test
    void loadDefaultWriteConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultWriteConverter(), DefaultConverterLoader.copyDefaultWriteConverter());
    }

    @Test
    void loadDefaultReadConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadDefaultReadConverter(), DefaultConverterLoader.copyDefaultReadConverter());
    }

    @Test
    void loadAllConverterIsImmutableAndCopyIsMutable() {
        assertLoadIsImmutableAndCopyIsMutable(
                DefaultConverterLoader.loadAllConverter(), DefaultConverterLoader.copyAllConverter());
    }

    @Test
    void loadConvertersRegistersLocalTimeFamily() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                LocalTimeNumberConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(LocalTime.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertInstanceOf(
                LocalTimeStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(LocalTime.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                LocalTimeDateConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(LocalTime.class)));

        Map<ConverterKey, Converter<?>> writeStringConverter = DefaultConverterLoader.loadDefaultWriteStringConverter();
        Assertions.assertInstanceOf(
                LocalTimeStringConverter.class, writeStringConverter.get(ConverterKeyBuild.buildKey(LocalTime.class)));
    }

    @Test
    void loadConvertersRegistersInstantStringConverter() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                InstantStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Instant.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                InstantStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Instant.class)));

        Map<ConverterKey, Converter<?>> writeStringConverter = DefaultConverterLoader.loadDefaultWriteStringConverter();
        Assertions.assertInstanceOf(
                InstantStringConverter.class, writeStringConverter.get(ConverterKeyBuild.buildKey(Instant.class)));
    }

    @Test
    void loadConvertersRegistersUUIDFamily() {
        Assertions.assertInstanceOf(
                UUIDStringConverter.class,
                DefaultConverterLoader.loadDefaultReadConverter()
                        .get(ConverterKeyBuild.buildKey(UUID.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                UUIDStringConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(UUID.class)));

        Map<ConverterKey, Converter<?>> writeStringConverter = DefaultConverterLoader.loadDefaultWriteStringConverter();
        Assertions.assertInstanceOf(
                UUIDStringConverter.class, writeStringConverter.get(ConverterKeyBuild.buildKey(UUID.class)));
    }

    @Test
    void loadConvertersRegistersSqlTimeFamily() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                SqlTimeNumberConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertInstanceOf(
                SqlTimeStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Time.class, CellDataTypeEnum.STRING)));
        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                SqlTimeDateConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Time.class)));
        Map<ConverterKey, Converter<?>> writeStringConverter = DefaultConverterLoader.loadDefaultWriteStringConverter();
        Assertions.assertInstanceOf(
                SqlTimeStringConverter.class, writeStringConverter.get(ConverterKeyBuild.buildKey(Time.class)));
    }

    @Test
    void loadConvertersRegistersTimestampFamily() {
        Map<ConverterKey, Converter<?>> allConverter = DefaultConverterLoader.loadAllConverter();
        Assertions.assertInstanceOf(
                TimestampNumberConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Timestamp.class, CellDataTypeEnum.NUMBER)));
        Assertions.assertInstanceOf(
                TimestampStringConverter.class,
                allConverter.get(ConverterKeyBuild.buildKey(Timestamp.class, CellDataTypeEnum.STRING)));

        Map<ConverterKey, Converter<?>> writeConverter = DefaultConverterLoader.loadDefaultWriteConverter();
        Assertions.assertInstanceOf(
                TimestampDateConverter.class, writeConverter.get(ConverterKeyBuild.buildKey(Timestamp.class)));
        Map<ConverterKey, Converter<?>> writeStringConverter = DefaultConverterLoader.loadDefaultWriteStringConverter();
        Assertions.assertInstanceOf(
                TimestampStringConverter.class, writeStringConverter.get(ConverterKeyBuild.buildKey(Timestamp.class)));
    }

    private static void assertLoadIsImmutableAndCopyIsMutable(
            Map<ConverterKey, Converter<?>> loaded, Map<ConverterKey, Converter<?>> copy) {
        Map.Entry<ConverterKey, Converter<?>> entry =
                loaded.entrySet().iterator().next();

        Assertions.assertThrows(
                UnsupportedOperationException.class, () -> loaded.put(entry.getKey(), entry.getValue()));

        copy.remove(entry.getKey());
        Assertions.assertFalse(copy.containsKey(entry.getKey()));
        Assertions.assertTrue(loaded.containsKey(entry.getKey()));
    }
}

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

package org.apache.fesod.excel.converters;

import java.util.Map;
import org.apache.fesod.excel.converters.ConverterKeyBuild.ConverterKey;
import org.apache.fesod.excel.converters.bigdecimal.BigDecimalBooleanConverter;
import org.apache.fesod.excel.converters.bigdecimal.BigDecimalNumberConverter;
import org.apache.fesod.excel.converters.bigdecimal.BigDecimalStringConverter;
import org.apache.fesod.excel.converters.biginteger.BigIntegerBooleanConverter;
import org.apache.fesod.excel.converters.biginteger.BigIntegerNumberConverter;
import org.apache.fesod.excel.converters.biginteger.BigIntegerStringConverter;
import org.apache.fesod.excel.converters.booleanconverter.BooleanBooleanConverter;
import org.apache.fesod.excel.converters.booleanconverter.BooleanNumberConverter;
import org.apache.fesod.excel.converters.booleanconverter.BooleanStringConverter;
import org.apache.fesod.excel.converters.bytearray.BoxingByteArrayImageConverter;
import org.apache.fesod.excel.converters.bytearray.ByteArrayImageConverter;
import org.apache.fesod.excel.converters.byteconverter.ByteBooleanConverter;
import org.apache.fesod.excel.converters.byteconverter.ByteNumberConverter;
import org.apache.fesod.excel.converters.byteconverter.ByteStringConverter;
import org.apache.fesod.excel.converters.date.DateDateConverter;
import org.apache.fesod.excel.converters.date.DateNumberConverter;
import org.apache.fesod.excel.converters.date.DateStringConverter;
import org.apache.fesod.excel.converters.doubleconverter.DoubleBooleanConverter;
import org.apache.fesod.excel.converters.doubleconverter.DoubleNumberConverter;
import org.apache.fesod.excel.converters.doubleconverter.DoubleStringConverter;
import org.apache.fesod.excel.converters.file.FileImageConverter;
import org.apache.fesod.excel.converters.floatconverter.FloatBooleanConverter;
import org.apache.fesod.excel.converters.floatconverter.FloatNumberConverter;
import org.apache.fesod.excel.converters.floatconverter.FloatStringConverter;
import org.apache.fesod.excel.converters.inputstream.InputStreamImageConverter;
import org.apache.fesod.excel.converters.integer.IntegerBooleanConverter;
import org.apache.fesod.excel.converters.integer.IntegerNumberConverter;
import org.apache.fesod.excel.converters.integer.IntegerStringConverter;
import org.apache.fesod.excel.converters.localdate.LocalDateDateConverter;
import org.apache.fesod.excel.converters.localdate.LocalDateNumberConverter;
import org.apache.fesod.excel.converters.localdate.LocalDateStringConverter;
import org.apache.fesod.excel.converters.localdatetime.LocalDateTimeDateConverter;
import org.apache.fesod.excel.converters.localdatetime.LocalDateTimeNumberConverter;
import org.apache.fesod.excel.converters.localdatetime.LocalDateTimeStringConverter;
import org.apache.fesod.excel.converters.longconverter.LongBooleanConverter;
import org.apache.fesod.excel.converters.longconverter.LongNumberConverter;
import org.apache.fesod.excel.converters.longconverter.LongStringConverter;
import org.apache.fesod.excel.converters.shortconverter.ShortBooleanConverter;
import org.apache.fesod.excel.converters.shortconverter.ShortNumberConverter;
import org.apache.fesod.excel.converters.shortconverter.ShortStringConverter;
import org.apache.fesod.excel.converters.string.StringBooleanConverter;
import org.apache.fesod.excel.converters.string.StringErrorConverter;
import org.apache.fesod.excel.converters.string.StringNumberConverter;
import org.apache.fesod.excel.converters.string.StringStringConverter;
import org.apache.fesod.excel.converters.url.UrlImageConverter;
import org.apache.fesod.excel.util.MapUtils;

/**
 * Load default handler
 *
 *
 */
public class DefaultConverterLoader {
    private static Map<ConverterKey, Converter<?>> defaultWriteConverter;
    private static Map<ConverterKey, Converter<?>> allConverter;

    static {
        initDefaultWriteConverter();
        initAllConverter();
    }

    private static void initAllConverter() {
        allConverter = MapUtils.newHashMapWithExpectedSize(40);
        putAllConverter(new BigDecimalBooleanConverter());
        putAllConverter(new BigDecimalNumberConverter());
        putAllConverter(new BigDecimalStringConverter());

        putAllConverter(new BigIntegerBooleanConverter());
        putAllConverter(new BigIntegerNumberConverter());
        putAllConverter(new BigIntegerStringConverter());

        putAllConverter(new BooleanBooleanConverter());
        putAllConverter(new BooleanNumberConverter());
        putAllConverter(new BooleanStringConverter());

        putAllConverter(new ByteBooleanConverter());
        putAllConverter(new ByteNumberConverter());
        putAllConverter(new ByteStringConverter());

        putAllConverter(new DateNumberConverter());
        putAllConverter(new DateStringConverter());

        putAllConverter(new LocalDateNumberConverter());
        putAllConverter(new LocalDateStringConverter());

        putAllConverter(new LocalDateTimeNumberConverter());
        putAllConverter(new LocalDateTimeStringConverter());

        putAllConverter(new DoubleBooleanConverter());
        putAllConverter(new DoubleNumberConverter());
        putAllConverter(new DoubleStringConverter());

        putAllConverter(new FloatBooleanConverter());
        putAllConverter(new FloatNumberConverter());
        putAllConverter(new FloatStringConverter());

        putAllConverter(new IntegerBooleanConverter());
        putAllConverter(new IntegerNumberConverter());
        putAllConverter(new IntegerStringConverter());

        putAllConverter(new LongBooleanConverter());
        putAllConverter(new LongNumberConverter());
        putAllConverter(new LongStringConverter());

        putAllConverter(new ShortBooleanConverter());
        putAllConverter(new ShortNumberConverter());
        putAllConverter(new ShortStringConverter());

        putAllConverter(new StringBooleanConverter());
        putAllConverter(new StringNumberConverter());
        putAllConverter(new StringStringConverter());
        putAllConverter(new StringErrorConverter());
    }

    private static void initDefaultWriteConverter() {
        defaultWriteConverter = MapUtils.newHashMapWithExpectedSize(40);
        putWriteConverter(new BigDecimalNumberConverter());
        putWriteConverter(new BigIntegerNumberConverter());
        putWriteConverter(new BooleanBooleanConverter());
        putWriteConverter(new ByteNumberConverter());
        putWriteConverter(new DateDateConverter());
        putWriteConverter(new LocalDateTimeDateConverter());
        putWriteConverter(new LocalDateDateConverter());
        putWriteConverter(new DoubleNumberConverter());
        putWriteConverter(new FloatNumberConverter());
        putWriteConverter(new IntegerNumberConverter());
        putWriteConverter(new LongNumberConverter());
        putWriteConverter(new ShortNumberConverter());
        putWriteConverter(new StringStringConverter());
        putWriteConverter(new FileImageConverter());
        putWriteConverter(new InputStreamImageConverter());
        putWriteConverter(new ByteArrayImageConverter());
        putWriteConverter(new BoxingByteArrayImageConverter());
        putWriteConverter(new UrlImageConverter());

        // In some cases, it must be converted to string
        putWriteStringConverter(new BigDecimalStringConverter());
        putWriteStringConverter(new BigIntegerStringConverter());
        putWriteStringConverter(new BooleanStringConverter());
        putWriteStringConverter(new ByteStringConverter());
        putWriteStringConverter(new DateStringConverter());
        putWriteStringConverter(new LocalDateStringConverter());
        putWriteStringConverter(new LocalDateTimeStringConverter());
        putWriteStringConverter(new DoubleStringConverter());
        putWriteStringConverter(new FloatStringConverter());
        putWriteStringConverter(new IntegerStringConverter());
        putWriteStringConverter(new LongStringConverter());
        putWriteStringConverter(new ShortStringConverter());
        putWriteStringConverter(new StringStringConverter());
    }

    /**
     * Load default write converter
     *
     * @return
     */
    public static Map<ConverterKey, Converter<?>> loadDefaultWriteConverter() {
        return defaultWriteConverter;
    }

    private static void putWriteConverter(Converter<?> converter) {
        defaultWriteConverter.put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey()), converter);
    }

    private static void putWriteStringConverter(Converter<?> converter) {
        defaultWriteConverter.put(
                ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey()), converter);
    }

    /**
     * Load default read converter
     *
     * @return
     */
    public static Map<ConverterKey, Converter<?>> loadDefaultReadConverter() {
        return loadAllConverter();
    }

    /**
     * Load all converter
     *
     * @return
     */
    public static Map<ConverterKey, Converter<?>> loadAllConverter() {
        return allConverter;
    }

    private static void putAllConverter(Converter<?> converter) {
        allConverter.put(
                ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey()), converter);
    }
}

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

package cn.idev.excel.converters;

import cn.idev.excel.converters.ConverterKeyBuild.ConverterKey;
import cn.idev.excel.converters.bigdecimal.BigDecimalBooleanConverter;
import cn.idev.excel.converters.bigdecimal.BigDecimalNumberConverter;
import cn.idev.excel.converters.bigdecimal.BigDecimalStringConverter;
import cn.idev.excel.converters.biginteger.BigIntegerBooleanConverter;
import cn.idev.excel.converters.biginteger.BigIntegerNumberConverter;
import cn.idev.excel.converters.biginteger.BigIntegerStringConverter;
import cn.idev.excel.converters.booleanconverter.BooleanBooleanConverter;
import cn.idev.excel.converters.booleanconverter.BooleanNumberConverter;
import cn.idev.excel.converters.booleanconverter.BooleanStringConverter;
import cn.idev.excel.converters.bytearray.BoxingByteArrayImageConverter;
import cn.idev.excel.converters.bytearray.ByteArrayImageConverter;
import cn.idev.excel.converters.byteconverter.ByteBooleanConverter;
import cn.idev.excel.converters.byteconverter.ByteNumberConverter;
import cn.idev.excel.converters.byteconverter.ByteStringConverter;
import cn.idev.excel.converters.date.DateDateConverter;
import cn.idev.excel.converters.date.DateNumberConverter;
import cn.idev.excel.converters.date.DateStringConverter;
import cn.idev.excel.converters.doubleconverter.DoubleBooleanConverter;
import cn.idev.excel.converters.doubleconverter.DoubleNumberConverter;
import cn.idev.excel.converters.doubleconverter.DoubleStringConverter;
import cn.idev.excel.converters.file.FileImageConverter;
import cn.idev.excel.converters.floatconverter.FloatBooleanConverter;
import cn.idev.excel.converters.floatconverter.FloatNumberConverter;
import cn.idev.excel.converters.floatconverter.FloatStringConverter;
import cn.idev.excel.converters.inputstream.InputStreamImageConverter;
import cn.idev.excel.converters.integer.IntegerBooleanConverter;
import cn.idev.excel.converters.integer.IntegerNumberConverter;
import cn.idev.excel.converters.integer.IntegerStringConverter;
import cn.idev.excel.converters.localdate.LocalDateDateConverter;
import cn.idev.excel.converters.localdate.LocalDateNumberConverter;
import cn.idev.excel.converters.localdate.LocalDateStringConverter;
import cn.idev.excel.converters.localdatetime.LocalDateTimeDateConverter;
import cn.idev.excel.converters.localdatetime.LocalDateTimeNumberConverter;
import cn.idev.excel.converters.localdatetime.LocalDateTimeStringConverter;
import cn.idev.excel.converters.longconverter.LongBooleanConverter;
import cn.idev.excel.converters.longconverter.LongNumberConverter;
import cn.idev.excel.converters.longconverter.LongStringConverter;
import cn.idev.excel.converters.shortconverter.ShortBooleanConverter;
import cn.idev.excel.converters.shortconverter.ShortNumberConverter;
import cn.idev.excel.converters.shortconverter.ShortStringConverter;
import cn.idev.excel.converters.string.StringBooleanConverter;
import cn.idev.excel.converters.string.StringErrorConverter;
import cn.idev.excel.converters.string.StringNumberConverter;
import cn.idev.excel.converters.string.StringStringConverter;
import cn.idev.excel.converters.url.UrlImageConverter;
import cn.idev.excel.util.MapUtils;
import java.util.Map;

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

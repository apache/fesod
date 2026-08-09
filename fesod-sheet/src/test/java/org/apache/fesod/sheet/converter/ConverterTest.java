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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.WriteConverterContext;
import org.apache.fesod.sheet.converters.bigdecimal.BigDecimalBooleanConverter;
import org.apache.fesod.sheet.converters.bigdecimal.BigDecimalNumberConverter;
import org.apache.fesod.sheet.converters.bigdecimal.BigDecimalStringConverter;
import org.apache.fesod.sheet.converters.biginteger.BigIntegerBooleanConverter;
import org.apache.fesod.sheet.converters.biginteger.BigIntegerNumberConverter;
import org.apache.fesod.sheet.converters.biginteger.BigIntegerStringConverter;
import org.apache.fesod.sheet.converters.booleanconverter.BooleanBooleanConverter;
import org.apache.fesod.sheet.converters.booleanconverter.BooleanNumberConverter;
import org.apache.fesod.sheet.converters.booleanconverter.BooleanStringConverter;
import org.apache.fesod.sheet.converters.byteconverter.ByteBooleanConverter;
import org.apache.fesod.sheet.converters.byteconverter.ByteNumberConverter;
import org.apache.fesod.sheet.converters.byteconverter.ByteStringConverter;
import org.apache.fesod.sheet.converters.doubleconverter.DoubleBooleanConverter;
import org.apache.fesod.sheet.converters.doubleconverter.DoubleNumberConverter;
import org.apache.fesod.sheet.converters.doubleconverter.DoubleStringConverter;
import org.apache.fesod.sheet.converters.floatconverter.FloatBooleanConverter;
import org.apache.fesod.sheet.converters.floatconverter.FloatNumberConverter;
import org.apache.fesod.sheet.converters.floatconverter.FloatStringConverter;
import org.apache.fesod.sheet.converters.integer.IntegerBooleanConverter;
import org.apache.fesod.sheet.converters.integer.IntegerNumberConverter;
import org.apache.fesod.sheet.converters.integer.IntegerStringConverter;
import org.apache.fesod.sheet.converters.longconverter.LongBooleanConverter;
import org.apache.fesod.sheet.converters.longconverter.LongNumberConverter;
import org.apache.fesod.sheet.converters.longconverter.LongStringConverter;
import org.apache.fesod.sheet.converters.shortconverter.ShortBooleanConverter;
import org.apache.fesod.sheet.converters.shortconverter.ShortNumberConverter;
import org.apache.fesod.sheet.converters.shortconverter.ShortStringConverter;
import org.apache.fesod.sheet.converters.string.StringBooleanConverter;
import org.apache.fesod.sheet.converters.string.StringErrorConverter;
import org.apache.fesod.sheet.converters.string.StringNumberConverter;
import org.apache.fesod.sheet.converters.string.StringStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for individual converter implementations.
 */
@Tag(Tags.UNIT)
class ConverterTest {

    private static final GlobalConfiguration GLOBAL_CONFIGURATION = new GlobalConfiguration();

    @ParameterizedTest
    @MethodSource("supportKeysProvider")
    void supportKeys(Converter<?> converter, Class<?> javaType, CellDataTypeEnum excelType) {
        assertEquals(javaType, converter.supportJavaTypeKey());
        assertEquals(excelType, converter.supportExcelTypeKey());
    }

    static Stream<Arguments> supportKeysProvider() {
        return Stream.of(
                Arguments.of(new BooleanBooleanConverter(), Boolean.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new BooleanNumberConverter(), Boolean.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new BooleanStringConverter(), Boolean.class, CellDataTypeEnum.STRING),
                Arguments.of(new IntegerBooleanConverter(), Integer.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new IntegerNumberConverter(), Integer.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new IntegerStringConverter(), Integer.class, CellDataTypeEnum.STRING),
                Arguments.of(new LongBooleanConverter(), Long.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new LongNumberConverter(), Long.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new LongStringConverter(), Long.class, CellDataTypeEnum.STRING),
                Arguments.of(new ShortBooleanConverter(), Short.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new ShortNumberConverter(), Short.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new ShortStringConverter(), Short.class, CellDataTypeEnum.STRING),
                Arguments.of(new ByteBooleanConverter(), Byte.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new ByteNumberConverter(), Byte.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new ByteStringConverter(), Byte.class, CellDataTypeEnum.STRING),
                Arguments.of(new FloatBooleanConverter(), Float.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new FloatNumberConverter(), Float.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new FloatStringConverter(), Float.class, CellDataTypeEnum.STRING),
                Arguments.of(new DoubleBooleanConverter(), Double.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new DoubleNumberConverter(), Double.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new DoubleStringConverter(), Double.class, CellDataTypeEnum.STRING),
                Arguments.of(new BigDecimalBooleanConverter(), BigDecimal.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new BigDecimalNumberConverter(), BigDecimal.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new BigDecimalStringConverter(), BigDecimal.class, CellDataTypeEnum.STRING),
                Arguments.of(new BigIntegerBooleanConverter(), BigInteger.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new BigIntegerNumberConverter(), BigInteger.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new BigIntegerStringConverter(), BigInteger.class, CellDataTypeEnum.STRING),
                Arguments.of(new StringStringConverter(), String.class, CellDataTypeEnum.STRING),
                Arguments.of(new StringBooleanConverter(), String.class, CellDataTypeEnum.BOOLEAN),
                Arguments.of(new StringNumberConverter(), String.class, CellDataTypeEnum.NUMBER),
                Arguments.of(new StringErrorConverter(), String.class, CellDataTypeEnum.ERROR));
    }

    @Test
    void booleanBooleanConverter() throws Exception {
        BooleanBooleanConverter converter = new BooleanBooleanConverter();

        Assertions.assertTrue(toJava(converter, new ReadCellData<>(Boolean.TRUE)));
        Assertions.assertFalse(toJava(converter, new ReadCellData<>(Boolean.FALSE)));

        Assertions.assertTrue(toExcel(converter, Boolean.TRUE).getBooleanValue());
        Assertions.assertFalse(toExcel(converter, Boolean.FALSE).getBooleanValue());
    }

    @Test
    void booleanNumberConverter() throws Exception {
        BooleanNumberConverter converter = new BooleanNumberConverter();

        Assertions.assertTrue(toJava(converter, new ReadCellData<>(BigDecimal.ONE)));
        Assertions.assertFalse(toJava(converter, new ReadCellData<>(BigDecimal.ZERO)));
        Assertions.assertFalse(toJava(converter, new ReadCellData<>(new BigDecimal("2"))));

        assertEquals(0, toExcel(converter, Boolean.TRUE).getNumberValue().compareTo(BigDecimal.ONE));
        assertEquals(0, toExcel(converter, Boolean.FALSE).getNumberValue().compareTo(BigDecimal.ZERO));
    }

    @Test
    void booleanStringConverter() throws Exception {
        BooleanStringConverter converter = new BooleanStringConverter();

        Assertions.assertTrue(toJava(converter, new ReadCellData<>("true")));
        Assertions.assertTrue(toJava(converter, new ReadCellData<>("TRUE")));
        Assertions.assertFalse(toJava(converter, new ReadCellData<>("false")));
        Assertions.assertFalse(toJava(converter, new ReadCellData<>("other")));

        assertEquals("true", toExcel(converter, Boolean.TRUE).getStringValue());
        assertEquals("false", toExcel(converter, Boolean.FALSE).getStringValue());
    }

    @Test
    void integerConverters() throws Exception {
        IntegerBooleanConverter booleanConverter = new IntegerBooleanConverter();
        assertEquals(1, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals(0, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, 1).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, 0).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, 2).getBooleanValue());

        IntegerNumberConverter numberConverter = new IntegerNumberConverter();
        assertEquals(42, toJava(numberConverter, new ReadCellData<>(new BigDecimal("42.9"))));
        WriteConverterContext<Integer> writeContext = new WriteConverterContext<>();
        writeContext.setValue(42);
        WriteCellData<?> numberCell = numberConverter.convertToExcelData(writeContext);
        assertEquals(0, numberCell.getNumberValue().compareTo(new BigDecimal("42")));

        IntegerStringConverter stringConverter = new IntegerStringConverter();
        assertEquals(123, toJava(stringConverter, new ReadCellData<>("123")));
        assertEquals("456", toExcel(stringConverter, 456).getStringValue());
    }

    @Test
    void longConverters() throws Exception {
        LongBooleanConverter booleanConverter = new LongBooleanConverter();
        assertEquals(1L, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals(0L, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, 1L).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, 0L).getBooleanValue());

        LongNumberConverter numberConverter = new LongNumberConverter();
        assertEquals(99L, toJava(numberConverter, new ReadCellData<>(new BigDecimal("99.1"))));
        WriteConverterContext<Long> writeContext = new WriteConverterContext<>();
        writeContext.setValue(99L);
        assertEquals(
                0,
                numberConverter
                        .convertToExcelData(writeContext)
                        .getNumberValue()
                        .compareTo(new BigDecimal("99")));

        LongStringConverter stringConverter = new LongStringConverter();
        assertEquals(1000L, toJava(stringConverter, new ReadCellData<>("1000")));
        assertEquals("1000", toExcel(stringConverter, 1000L).getStringValue());
    }

    @Test
    void shortConverters() throws Exception {
        ShortBooleanConverter booleanConverter = new ShortBooleanConverter();
        assertEquals((short) 1, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals((short) 0, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, (short) 1).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, (short) 0).getBooleanValue());

        ShortNumberConverter numberConverter = new ShortNumberConverter();
        assertEquals((short) 7, toJava(numberConverter, new ReadCellData<>(new BigDecimal("7.8"))));
        WriteConverterContext<Short> writeContext = new WriteConverterContext<>();
        writeContext.setValue((short) 7);
        assertEquals(
                0,
                numberConverter
                        .convertToExcelData(writeContext)
                        .getNumberValue()
                        .compareTo(new BigDecimal("7")));

        ShortStringConverter stringConverter = new ShortStringConverter();
        assertEquals((short) 12, toJava(stringConverter, new ReadCellData<>("12")));
        assertEquals("12", toExcel(stringConverter, (short) 12).getStringValue());
    }

    @Test
    void byteConverters() throws Exception {
        ByteBooleanConverter booleanConverter = new ByteBooleanConverter();
        assertEquals((byte) 1, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals((byte) 0, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, (byte) 1).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, (byte) 0).getBooleanValue());

        ByteNumberConverter numberConverter = new ByteNumberConverter();
        assertEquals((byte) 3, toJava(numberConverter, new ReadCellData<>(new BigDecimal("3.9"))));
        assertEquals(0, toExcel(numberConverter, (byte) 3).getNumberValue().compareTo(new BigDecimal("3")));

        ByteStringConverter stringConverter = new ByteStringConverter();
        assertEquals((byte) 8, toJava(stringConverter, new ReadCellData<>("8")));
        assertEquals("8", toExcel(stringConverter, (byte) 8).getStringValue());
    }

    @Test
    void floatConverters() throws Exception {
        FloatBooleanConverter booleanConverter = new FloatBooleanConverter();
        assertEquals(1.0F, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals(0.0F, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, 1.0F).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, 0.0F).getBooleanValue());

        FloatNumberConverter numberConverter = new FloatNumberConverter();
        assertEquals(95.62F, toJava(numberConverter, new ReadCellData<>(new BigDecimal("95.62"))));
        WriteConverterContext<Float> writeContext = new WriteConverterContext<>();
        writeContext.setValue(95.62F);
        assertEquals(
                0,
                numberConverter
                        .convertToExcelData(writeContext)
                        .getNumberValue()
                        .compareTo(new BigDecimal("95.62")));

        FloatStringConverter stringConverter = new FloatStringConverter();
        assertEquals(1.5F, toJava(stringConverter, new ReadCellData<>("1.5")));
        assertEquals("1.5", toExcel(stringConverter, 1.5F).getStringValue());
    }

    @Test
    void doubleConverters() throws Exception {
        DoubleBooleanConverter booleanConverter = new DoubleBooleanConverter();
        assertEquals(1.0D, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals(0.0D, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, 1.0D).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, 0.0D).getBooleanValue());

        DoubleNumberConverter numberConverter = new DoubleNumberConverter();
        assertEquals(2.5D, toJava(numberConverter, new ReadCellData<>(new BigDecimal("2.5"))));
        assertEquals(0, toExcel(numberConverter, 2.5D).getNumberValue().compareTo(new BigDecimal("2.5")));

        DoubleStringConverter stringConverter = new DoubleStringConverter();
        assertEquals(3.25D, toJava(stringConverter, new ReadCellData<>("3.25")));
        assertEquals("3.25", toExcel(stringConverter, 3.25D).getStringValue());
    }

    @Test
    void bigDecimalConverters() throws Exception {
        BigDecimalBooleanConverter booleanConverter = new BigDecimalBooleanConverter();
        assertEquals(
                0, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)).compareTo(BigDecimal.ONE));
        assertEquals(
                0, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)).compareTo(BigDecimal.ZERO));
        Assertions.assertTrue(toExcel(booleanConverter, BigDecimal.ONE).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, BigDecimal.ZERO).getBooleanValue());

        BigDecimalNumberConverter numberConverter = new BigDecimalNumberConverter();
        BigDecimal value = new BigDecimal("123.45");
        assertEquals(0, toJava(numberConverter, new ReadCellData<>(value)).compareTo(value));
        assertEquals(0, toExcel(numberConverter, value).getNumberValue().compareTo(value));

        BigDecimalStringConverter stringConverter = new BigDecimalStringConverter();
        assertEquals(0, toJava(stringConverter, new ReadCellData<>("123.45")).compareTo(value));
        assertEquals("123.45", toExcel(stringConverter, value).getStringValue());
    }

    @Test
    void bigIntegerConverters() throws Exception {
        BigIntegerBooleanConverter booleanConverter = new BigIntegerBooleanConverter();
        assertEquals(BigInteger.ONE, toJava(booleanConverter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals(BigInteger.ZERO, toJava(booleanConverter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(booleanConverter, BigInteger.ONE).getBooleanValue());
        Assertions.assertFalse(toExcel(booleanConverter, BigInteger.ZERO).getBooleanValue());

        BigIntegerNumberConverter numberConverter = new BigIntegerNumberConverter();
        assertEquals(BigInteger.valueOf(88), toJava(numberConverter, new ReadCellData<>(new BigDecimal("88.9"))));
        assertEquals(
                0,
                toExcel(numberConverter, BigInteger.valueOf(88))
                        .getNumberValue()
                        .compareTo(new BigDecimal("88")));

        BigIntegerStringConverter stringConverter = new BigIntegerStringConverter();
        assertEquals(BigInteger.valueOf(100), toJava(stringConverter, new ReadCellData<>("100")));
        assertEquals("100", toExcel(stringConverter, BigInteger.valueOf(100)).getStringValue());
    }

    @Test
    void stringStringConverter() throws Exception {
        StringStringConverter converter = new StringStringConverter();
        assertEquals("hello", toJava(converter, new ReadCellData<>("hello")));
        assertEquals("world", toExcel(converter, "world").getStringValue());
    }

    @Test
    void stringBooleanConverter() throws Exception {
        StringBooleanConverter converter = new StringBooleanConverter();
        assertEquals("true", toJava(converter, new ReadCellData<>(Boolean.TRUE)));
        assertEquals("false", toJava(converter, new ReadCellData<>(Boolean.FALSE)));
        Assertions.assertTrue(toExcel(converter, "true").getBooleanValue());
        Assertions.assertFalse(toExcel(converter, "false").getBooleanValue());
        Assertions.assertFalse(toExcel(converter, "other").getBooleanValue());
    }

    @Test
    void stringNumberConverter() throws Exception {
        StringNumberConverter converter = new StringNumberConverter();
        assertEquals("12.5", toJava(converter, new ReadCellData<>(new BigDecimal("12.5"))));
        assertEquals(0, toExcel(converter, "12.5").getNumberValue().compareTo(new BigDecimal("12.5")));
    }

    @Test
    void stringErrorConverter() throws Exception {
        StringErrorConverter converter = new StringErrorConverter();
        assertEquals("#N/A", toJava(converter, new ReadCellData<>(CellDataTypeEnum.ERROR, "#N/A")));

        WriteCellData<?> writeCellData = toExcel(converter, "#DIV/0!");
        assertEquals(CellDataTypeEnum.ERROR, writeCellData.getType());
        assertEquals("#DIV/0!", writeCellData.getStringValue());
    }

    private static <T> T toJava(Converter<T> converter, ReadCellData<?> cellData) throws Exception {
        return converter.convertToJavaData(cellData, null, GLOBAL_CONFIGURATION);
    }

    private static <T> WriteCellData<?> toExcel(Converter<T> converter, T value) throws Exception {
        return converter.convertToExcelData(value, null, GLOBAL_CONFIGURATION);
    }
}

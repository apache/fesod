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

package org.apache.fesod.sheet.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.converters.CellDataConverterRegistry;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.NullableObjectConverter;
import org.apache.fesod.sheet.converters.ReadConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.exception.ExcelDataConvertException;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.sheet.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link ConverterUtils}
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class ConverterUtilsTest {

    @Mock
    private AnalysisContext context;

    @Mock
    private ReadSheetHolder readSheetHolder;

    @Mock
    private ReadRowHolder readRowHolder;

    @Mock
    private Converter stringConverter;

    @Mock
    private Converter integerConverter;

    private CellDataConverterRegistry converterRegistry;

    @BeforeEach
    void setUp() {
        converterRegistry = new CellDataConverterRegistry();

        Mockito.lenient().when(context.readSheetHolder()).thenReturn(readSheetHolder);
        Mockito.lenient().when(context.readRowHolder()).thenReturn(readRowHolder);
        Mockito.lenient().when(readSheetHolder.converterRegistry()).thenReturn(converterRegistry);
        Mockito.lenient().when(readRowHolder.getRowIndex()).thenReturn(1);
    }

    @Test
    void test_convertToStringMap_normal() throws Exception {
        // {0: "A", 1: "B"}
        Map<Integer, ReadCellData<?>> cellDataMap = new TreeMap<>();
        cellDataMap.put(0, new ReadCellData<>("A"));
        cellDataMap.put(1, new ReadCellData<>("B"));

        ReadConverter<String> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(String.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn("A", "B");

        converterRegistry.addCustomReadConverter(readConverter);

        Map<Integer, String> result = ConverterUtils.convertToStringMap(cellDataMap, context);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("A", result.get(0));
        Assertions.assertEquals("B", result.get(1));
    }

    @Test
    void test_convertToStringMap_withGap() throws Exception {
        // {0: "A", 2: "C"}
        Map<Integer, ReadCellData<?>> cellDataMap = new TreeMap<>();
        cellDataMap.put(0, new ReadCellData<>("A"));
        cellDataMap.put(2, new ReadCellData<>("C"));

        ReadConverter<String> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(String.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn("A", "C");

        converterRegistry.addCustomReadConverter(readConverter);

        Map<Integer, String> result = ConverterUtils.convertToStringMap(cellDataMap, context);

        // 0->A, 1->null, 2->C
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("A", result.get(0));
        Assertions.assertNull(result.get(1));
        Assertions.assertEquals("C", result.get(2));
    }

    @Test
    void test_convertToStringMap_emptyCell() throws Exception {
        Map<Integer, ReadCellData<?>> cellDataMap = new HashMap<>();
        cellDataMap.put(0, new ReadCellData<>(CellDataTypeEnum.EMPTY));

        Map<Integer, String> result = ConverterUtils.convertToStringMap(cellDataMap, context);

        Assertions.assertNull(result.get(0));
        Mockito.verify(stringConverter, Mockito.never()).convertToJavaData(Mockito.any());
    }

    @Test
    void test_convertToStringMap_noConverter() {
        Map<Integer, ReadCellData<?>> cellDataMap = new HashMap<>();
        cellDataMap.put(0, new ReadCellData<>(CellDataTypeEnum.BOOLEAN));

        Assertions.assertThrows(ExcelDataConvertException.class, () -> {
            ConverterUtils.convertToStringMap(cellDataMap, context);
        });
    }

    class DemoData {
        private String stringField;
        private ReadCellData<Integer> cellDataIntField;
        private ReadCellData rawCellDataField;
    }

    @Test
    void test_convertToJavaData_simpleConversion() throws Exception {
        ReadCellData<?> cellData = new ReadCellData<>(new BigDecimal("123"));

        ReadConverter<Integer> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(Integer.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.NUMBER).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn(123);

        converterRegistry.addCustomReadConverter(readConverter);

        Object result = ConverterUtils.convertToJavaObject(
                cellData, null, Integer.class, null, null, converterRegistry, context, 1, 0);

        Assertions.assertEquals(123, result);
    }

    @Test
    void test_convertToJavaData() throws Exception {
        ReadCellData<?> cellData = new ReadCellData<>("123");

        ReadConverter<String> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(String.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn("123");

        converterRegistry.addCustomReadConverter(readConverter);

        Object result1 =
                ConverterUtils.convertToJavaObject(cellData, null, null, null, null, converterRegistry, context, 1, 0);

        Field field = DemoData.class.getDeclaredField("stringField");
        Object result2 =
                ConverterUtils.convertToJavaObject(cellData, field, null, null, null, converterRegistry, context, 1, 0);

        Assertions.assertEquals("123", result1);
        Assertions.assertEquals("123", result2);
    }

    @Test
    void test_ReadCellData_generic_inference() throws Exception, NoSuchFieldException {
        ReadCellData<?> cellData = new ReadCellData<>(new BigDecimal("100"));
        Field field = DemoData.class.getDeclaredField("cellDataIntField");

        ReadConverter<Integer> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(Integer.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.NUMBER).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn(100);

        converterRegistry.addCustomReadConverter(readConverter);

        Object result = ConverterUtils.convertToJavaObject(
                cellData, field, ReadCellData.class, null, null, converterRegistry, context, 1, 0);

        Assertions.assertInstanceOf(ReadCellData.class, result);
        ReadCellData<?> resultData = (ReadCellData<?>) result;
        Assertions.assertEquals(100, resultData.getData());
    }

    @Test
    void test_ReadCellData_raw_defaultString() throws Exception, NoSuchFieldException {
        ReadCellData<?> cellData = new ReadCellData<>("test");
        Field field = DemoData.class.getDeclaredField("rawCellDataField");

        ReadConverter<String> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(String.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(readConverter).supportExcelTypeKey();
        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenReturn("test");

        converterRegistry.addCustomReadConverter(readConverter);

        Object result = ConverterUtils.convertToJavaObject(
                cellData, field, ReadCellData.class, null, null, converterRegistry, context, 1, 0);

        Assertions.assertInstanceOf(ReadCellData.class, result);
        Mockito.verify(readConverter).convertToJavaData(Mockito.any());
    }

    @Test
    void test_Priority_ContentProperty() throws Exception {
        ReadCellData<?> cellData = new ReadCellData<>("test");

        Converter globalConverter = Mockito.mock(Converter.class);
        Converter customConverter = Mockito.mock(Converter.class);

        Mockito.doReturn(String.class).when(globalConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(globalConverter).supportExcelTypeKey();

        converterRegistry.addCustomReadConverter(globalConverter);

        ExcelContentProperty property = Mockito.mock(ExcelContentProperty.class);
        Mockito.when(property.getConverter()).thenReturn(customConverter);
        Mockito.when(customConverter.convertToJavaData(Mockito.any())).thenReturn("Custom");

        Object result = ConverterUtils.convertToJavaObject(
                cellData, null, String.class, null, property, converterRegistry, context, 1, 0);

        Assertions.assertEquals("Custom", result);
        Mockito.verify(customConverter).convertToJavaData(Mockito.any());
        Mockito.verify(globalConverter, Mockito.never()).convertToJavaData(Mockito.any());
    }

    @Test
    void test_EmptyCell_NormalConverter() throws Exception, NoSuchFieldException {
        ReadCellData<?> cellData = new ReadCellData<>(CellDataTypeEnum.EMPTY);
        ExcelContentProperty property = Mockito.mock(ExcelContentProperty.class);
        Mockito.when(property.getConverter()).thenReturn(stringConverter);

        Object result = ConverterUtils.convertToJavaObject(
                cellData, null, String.class, null, property, converterRegistry, context, 1, 0);

        Assertions.assertNull(result);
        Mockito.verify(stringConverter, Mockito.never()).convertToJavaData(Mockito.any());
    }

    @Test
    void test_EmptyCell_NullableConverter() throws Exception {
        ReadCellData<?> cellData = new ReadCellData<>(CellDataTypeEnum.EMPTY);
        ExcelContentProperty property = Mockito.mock(ExcelContentProperty.class);

        Converter nullableConverter =
                Mockito.mock(Converter.class, Mockito.withSettings().extraInterfaces(NullableObjectConverter.class));
        Mockito.when(property.getConverter()).thenReturn(nullableConverter);
        Mockito.when(nullableConverter.convertToJavaData(Mockito.any())).thenReturn("HandledNull");

        Object result = ConverterUtils.convertToJavaObject(
                cellData, null, String.class, null, property, converterRegistry, context, 1, 0);

        Assertions.assertEquals("HandledNull", result);
        Mockito.verify(nullableConverter).convertToJavaData(Mockito.any());
    }

    @Test
    void test_exception_wrapping() throws Exception {
        ReadCellData<?> cellData = new ReadCellData<>("ErrorData");

        ReadConverter<String> readConverter = Mockito.mock(ReadConverter.class);
        Mockito.doReturn(String.class).when(readConverter).supportJavaTypeKey();
        Mockito.doReturn(CellDataTypeEnum.STRING).when(readConverter).supportExcelTypeKey();

        converterRegistry.addCustomReadConverter(readConverter);

        Mockito.when(readConverter.convertToJavaData(Mockito.any())).thenThrow(new RuntimeException("Inner error"));

        ExcelDataConvertException ex = Assertions.assertThrows(ExcelDataConvertException.class, () -> {
            ConverterUtils.convertToJavaObject(
                    cellData, null, String.class, null, null, converterRegistry, context, 99, 88);
        });

        Assertions.assertEquals(99, ex.getRowIndex());
        Assertions.assertEquals(88, ex.getColumnIndex());
        Assertions.assertTrue(ex.getMessage().contains("Convert data"));
    }
}

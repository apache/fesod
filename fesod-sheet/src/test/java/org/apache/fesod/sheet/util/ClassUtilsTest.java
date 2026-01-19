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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.string.StringStringConverter;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.metadata.FieldCache;
import org.apache.fesod.sheet.metadata.FieldWrapper;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.metadata.property.FontProperty;
import org.apache.fesod.sheet.metadata.property.NumberFormatProperty;
import org.apache.fesod.sheet.metadata.property.StyleProperty;
import org.apache.fesod.sheet.write.metadata.holder.WriteHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link ClassUtils}
 */
@ExtendWith(MockitoExtension.class)
class ClassUtilsTest {

    @Mock
    private WriteHolder writeHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(writeHolder.globalConfiguration()).thenReturn(globalConfiguration);
    }

    @AfterEach
    void tearDown() {
        ClassUtils.removeThreadLocalCache();
        ClassUtils.FIELD_CACHE.clear();
        ClassUtils.CONTENT_CACHE.clear();
        ClassUtils.CLASS_CONTENT_CACHE.clear();
    }

    private static class SimpleEntity {
        @ExcelProperty("Name")
        private String name;

        @ExcelProperty(value = "Age", order = 1)
        private Integer age;
    }

    private static class ComplexEntity {
        @ExcelProperty(index = 0)
        private String id;

        @ExcelProperty(index = 2)
        private String name;

        @ExcelProperty(order = 10)
        private String email;

        @ExcelIgnore
        private String ignoredField;

        private String noAnnotationField;
    }

    private static class FormatEntity {
        @DateTimeFormat("yyyy-MM-dd")
        private Date date;

        @NumberFormat("#.00")
        private Double money;

        @ExcelProperty(converter = StringStringConverter.class)
        private String customConvert;
    }

    @Test
    void test_declaredFields_cache_memory() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.MEMORY);

        FieldCache cache1 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        Assertions.assertNotNull(cache1);

        Assertions.assertFalse(ClassUtils.FIELD_CACHE.isEmpty());

        FieldCache cache2 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        Assertions.assertSame(cache1, cache2);
    }

    @Test
    void test_declaredFields_cache_ThreadLocal() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.THREAD_LOCAL);

        FieldCache cache1 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        Assertions.assertNotNull(cache1);

        Assertions.assertTrue(ClassUtils.FIELD_CACHE.isEmpty());

        FieldCache cache2 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        Assertions.assertSame(cache1, cache2);
    }

    @Test
    void test_declaredFields_non_cache() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        FieldCache cache1 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        FieldCache cache2 = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);

        Assertions.assertNotSame(cache1, cache2);
        Assertions.assertTrue(ClassUtils.FIELD_CACHE.isEmpty());
    }

    @Test
    void test_declaredFields_ordering() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        FieldCache fieldCache = ClassUtils.declaredFields(ComplexEntity.class, writeHolder);
        Map<Integer, FieldWrapper> sortedMap = fieldCache.getSortedFieldMap();

        Assertions.assertTrue(sortedMap.containsKey(0));
        Assertions.assertEquals("id", sortedMap.get(0).getFieldName());

        Assertions.assertTrue(sortedMap.containsKey(2));
        Assertions.assertEquals("name", sortedMap.get(2).getFieldName());

        Assertions.assertTrue(sortedMap.containsKey(1));
        Assertions.assertEquals("email", sortedMap.get(1).getFieldName());

        Assertions.assertTrue(sortedMap.containsKey(3));
        Assertions.assertEquals("noAnnotationField", sortedMap.get(3).getFieldName());
    }

    @Test
    void test_declaredFields_ignore() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        FieldCache fieldCache = ClassUtils.declaredFields(ComplexEntity.class, writeHolder);

        boolean containsIgnored =
                fieldCache.getSortedFieldMap().values().stream().anyMatch(f -> "ignoredField".equals(f.getFieldName()));

        Assertions.assertFalse(containsIgnored);
    }

    @Test
    void test_declaredFields_WriteHolder_exclude() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        Mockito.when(writeHolder.excludeColumnFieldNames()).thenReturn(Collections.singleton("name"));
        Mockito.when(writeHolder.ignore(Mockito.anyString(), Mockito.anyInt())).thenReturn(false);
        Mockito.when(writeHolder.ignore(Mockito.eq("name"), Mockito.anyInt())).thenReturn(true);

        FieldCache fieldCache = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);

        Map<Integer, FieldWrapper> map = fieldCache.getSortedFieldMap();

        boolean hasName = map.values().stream().anyMatch(f -> "name".equals(f.getFieldName()));
        Assertions.assertFalse(hasName);

        boolean hasAge = map.values().stream().anyMatch(f -> "age".equals(f.getFieldName()));
        Assertions.assertTrue(hasAge);
    }

    @Test
    void test_declaredFields_resort() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        Mockito.when(writeHolder.orderByIncludeColumn()).thenReturn(true);
        List<String> include = Arrays.asList("age", "name");
        Mockito.when(writeHolder.includeColumnFieldNames()).thenReturn(include);

        FieldCache fieldCache = ClassUtils.declaredFields(SimpleEntity.class, writeHolder);
        Map<Integer, FieldWrapper> map = fieldCache.getSortedFieldMap();

        Assertions.assertEquals("age", map.get(0).getFieldName());
        Assertions.assertEquals("name", map.get(1).getFieldName());
    }

    @Test
    void test_declaredFields_resort_byIndex() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);
        Mockito.when(writeHolder.orderByIncludeColumn()).thenReturn(true);
        Mockito.when(writeHolder.includeColumnFieldNames()).thenReturn(null);
        Mockito.when(writeHolder.includeColumnIndexes()).thenReturn(Arrays.asList(2, 0));
        Mockito.when(writeHolder.ignore(Mockito.anyString(), Mockito.anyInt())).thenReturn(false);

        FieldCache fieldCache = ClassUtils.declaredFields(ComplexEntity.class, writeHolder);
        Map<Integer, FieldWrapper> sortedMap = fieldCache.getSortedFieldMap();

        Assertions.assertEquals(2, sortedMap.size());

        Assertions.assertTrue(sortedMap.containsKey(0));
        Assertions.assertEquals("name", sortedMap.get(0).getFieldName());

        Assertions.assertTrue(sortedMap.containsKey(1));
        Assertions.assertEquals("id", sortedMap.get(1).getFieldName());
    }

    @Test
    void test_declaredExcelContentProperty() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(null, FormatEntity.class, "date", writeHolder);

        Assertions.assertNotNull(property);
        Assertions.assertNotNull(property.getDateTimeFormatProperty());
        Assertions.assertEquals(
                "yyyy-MM-dd", property.getDateTimeFormatProperty().getFormat());
    }

    @Test
    void test_declaredExcelContentProperty_cache_memory() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.MEMORY);

        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(null, FormatEntity.class, "date", writeHolder);

        Assertions.assertNotNull(property);
        Assertions.assertNotNull(property.getDateTimeFormatProperty());
        Assertions.assertEquals(
                "yyyy-MM-dd", property.getDateTimeFormatProperty().getFormat());
    }

    @Test
    void test_declaredExcelContentProperty_cache_ThreadLocal() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.THREAD_LOCAL);

        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(null, FormatEntity.class, "date", writeHolder);

        Assertions.assertNotNull(property);
        Assertions.assertNotNull(property.getDateTimeFormatProperty());
        Assertions.assertEquals(
                "yyyy-MM-dd", property.getDateTimeFormatProperty().getFormat());
    }

    @Test
    void test_declaredExcelContentProperty_BeanMap() {
        BeanMap beanMapMocked = Mockito.mock(BeanMap.class);
        FormatEntity beanMocked = Mockito.mock(FormatEntity.class);

        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);
        Mockito.when(beanMapMocked.getBean()).thenReturn(beanMocked);

        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(beanMapMocked, FormatEntity.class, "date", writeHolder);

        Assertions.assertNotNull(property);
        Assertions.assertNotNull(property.getDateTimeFormatProperty());
        Assertions.assertEquals(
                "yyyy-MM-dd", property.getDateTimeFormatProperty().getFormat());
    }

    @Test
    void test_declaredExcelContentProperty_converter() {
        Mockito.when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);

        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(null, FormatEntity.class, "customConvert", writeHolder);

        Assertions.assertNotNull(property);
        Assertions.assertNotNull(property.getConverter());
        Assertions.assertInstanceOf(StringStringConverter.class, property.getConverter());
    }

    @Test
    void test_combineExcelContentProperty() throws NoSuchFieldException {
        Field field = SimpleEntity.class.getDeclaredField("name");
        Converter converter = Mockito.mock(Converter.class);
        DateTimeFormatProperty dateTimeFormatProperty = Mockito.mock(DateTimeFormatProperty.class);
        NumberFormatProperty numberFormatProperty = Mockito.mock(NumberFormatProperty.class);
        StyleProperty styleProperty = Mockito.mock(StyleProperty.class);
        FontProperty fontProperty = Mockito.mock(FontProperty.class);

        ExcelContentProperty propertyMocked = Mockito.mock(ExcelContentProperty.class);
        Mockito.when(propertyMocked.getField()).thenReturn(field);
        Mockito.when(propertyMocked.getConverter()).thenReturn(converter);
        Mockito.when(propertyMocked.getDateTimeFormatProperty()).thenReturn(dateTimeFormatProperty);
        Mockito.when(propertyMocked.getNumberFormatProperty()).thenReturn(numberFormatProperty);
        Mockito.when(propertyMocked.getContentStyleProperty()).thenReturn(styleProperty);
        Mockito.when(propertyMocked.getContentFontProperty()).thenReturn(fontProperty);

        ExcelContentProperty combine = new ExcelContentProperty();

        ClassUtils.combineExcelContentProperty(combine, propertyMocked);

        Assertions.assertEquals(field, combine.getField());
        Assertions.assertEquals(converter, combine.getConverter());
        Assertions.assertEquals(dateTimeFormatProperty, combine.getDateTimeFormatProperty());
        Assertions.assertEquals(numberFormatProperty, combine.getNumberFormatProperty());
        Assertions.assertEquals(styleProperty, combine.getContentStyleProperty());
        Assertions.assertEquals(fontProperty, combine.getContentFontProperty());
    }

    interface InterfaceA {}

    interface InterfaceB extends InterfaceA {}

    static class ClassImpl implements InterfaceB {}

    @Test
    void test_getAllInterfaces() {
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(ClassImpl.class);

        Assertions.assertNull(ClassUtils.getAllInterfaces(null));
        Assertions.assertNotNull(interfaces);
        Assertions.assertTrue(interfaces.contains(InterfaceB.class));
        Assertions.assertTrue(interfaces.contains(InterfaceA.class));
        Assertions.assertEquals(InterfaceB.class, interfaces.get(0));
    }
}

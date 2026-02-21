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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.sheet.metadata.NullObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Tests {@link FieldUtils}
 */
class FieldUtilsTest {

    @Test
    void test_getFieldClass_normalValue() {
        Class<?> clazz = FieldUtils.getFieldClass(null, "any", "StringValue");

        Assertions.assertEquals(String.class, clazz);
    }

    @Test
    void test_getFieldClass_nullValue() {
        Class<?> clazz = FieldUtils.getFieldClass(null, "any", null);

        Assertions.assertEquals(NullObject.class, clazz);
    }

    @Test
    void test_getFieldClass_BeanMap() {
        BeanMap mockBeanMap = Mockito.mock(BeanMap.class);
        Mockito.when(mockBeanMap.getPropertyType("name")).thenReturn(Integer.class);

        Class<?> clazz = FieldUtils.getFieldClass(mockBeanMap, "name", "123");

        Assertions.assertEquals(Integer.class, clazz);
    }

    @Test
    void test_getFieldClass_BeanMap_fallback() {
        BeanMap mockBeanMap = Mockito.mock(BeanMap.class);
        Mockito.when(mockBeanMap.getPropertyType("unknown")).thenReturn(null);

        Class<?> clazz = FieldUtils.getFieldClass(mockBeanMap, "unknown", "Value");

        Assertions.assertEquals(String.class, clazz);
    }

    @Test
    void test_getFieldClass_normalMap() {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = FieldUtils.getFieldClass(map, "any", 100L);

        Assertions.assertEquals(Long.class, clazz);
    }

    @Test
    void test_resolveCglibFieldName_nullField() {
        Assertions.assertNull(FieldUtils.resolveCglibFieldName(null));
    }

    @ParameterizedTest
    @MethodSource("cglibNameProvider")
    void test_resolveCglibFieldName_resolveLogic(String fieldName, String expected) throws NoSuchFieldException {
        Field field = FieldNameFixture.class.getDeclaredField(fieldName);

        String result = FieldUtils.resolveCglibFieldName(field);
        Assertions.assertEquals(expected, result);
    }

    static class FieldNameFixture {
        private String a;
        private String A;
        private String ab;
        private String AB;
        private String string1;
        private String STRING5;
        private String String2;
        private String sTring3;
        private String aBc;
    }

    static Stream<Arguments> cglibNameProvider() {
        return Stream.of(
                Arguments.of("a", "a"),
                Arguments.of("A", "A"),
                // lower, lower
                Arguments.of("ab", "ab"),
                // Upper, Upper
                Arguments.of("AB", "AB"),
                // s(l), t(l) -> keep
                Arguments.of("string1", "string1"),
                // S(U), T(U) -> keep
                Arguments.of("STRING5", "STRING5"),
                // String2 -> string2
                Arguments.of("String2", "string2"),
                // sTring3 -> STring3
                Arguments.of("sTring3", "STring3"),
                // aBc -> a(l), B(U) -> A(U) + Bc -> ABc
                Arguments.of("aBc", "ABc"));
    }

    @Test
    void test_getField_nullClass() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(null, "any", true));
    }

    interface InterfaceA {
        String interfaceField = "I_A";
    }

    interface InterfaceB {
        String interfaceField = "I_B";
    }

    static class Parent {
        public String parentPublic;
        protected String parentProtected;
        private String parentPrivate;
    }

    static class Child extends Parent implements InterfaceA {
        public String childPublic;
        private String childPrivate;
    }

    static class AmbiguousChild implements InterfaceA, InterfaceB {}

    @Test
    void test_getField_blankName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FieldUtils.getField(Child.class, "  ", true));
    }

    @Test
    void test_getField_self_public() {
        Field field = FieldUtils.getField(Child.class, "childPublic", false);
        Assertions.assertNotNull(field);
        Assertions.assertEquals("childPublic", field.getName());
    }

    @Test
    void test_getField_self_private_force() {
        Field field = FieldUtils.getField(Child.class, "childPrivate", true);
        Assertions.assertNotNull(field);
        Assertions.assertTrue(field.isAccessible());
    }

    @Test
    void test_getField_self_private_noForce() {
        Field field = FieldUtils.getField(Child.class, "childPrivate", false);

        Assertions.assertNull(field);
    }

    @Test
    void test_getField_super_public() {
        Field field = FieldUtils.getField(Child.class, "parentPublic", false);
        Assertions.assertNotNull(field);
        Assertions.assertEquals(Parent.class, field.getDeclaringClass());
    }

    @Test
    void test_getField_super_private_force() {
        Field field = FieldUtils.getField(Child.class, "parentPrivate", true);

        Assertions.assertNotNull(field);
        Assertions.assertEquals("parentPrivate", field.getName());
        Assertions.assertEquals(Parent.class, field.getDeclaringClass());
    }

    @Test
    void test_getField_interface_field() {
        Field field = FieldUtils.getField(Child.class, "interfaceField", false);
        Assertions.assertNotNull(field);
        Assertions.assertEquals(InterfaceA.class, field.getDeclaringClass());
    }

    @Test
    void test_getField_notFound() {
        Field field = FieldUtils.getField(Child.class, "notExists", true);
        Assertions.assertNull(field);
    }

    @Test
    void test_getField_ambiguous_interface() {
        try {
            FieldUtils.getField(AmbiguousChild.class, "interfaceField", true);
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("ambiguous"));
        } catch (NoClassDefFoundError e) {
        }
    }

    public static class PublicTarget {
        public String publicField;
    }

    static class PackagePrivateTarget {
        public String publicField;
    }

    @Test
    void test_getField_publicClass_publicField() {
        Field field = FieldUtils.getField(PublicTarget.class, "publicField");

        Assertions.assertNotNull(field);
        Assertions.assertFalse(field.isAccessible());
    }

    @Test
    void test_getField_packagePrivateClass_publicField() {
        Field field = FieldUtils.getField(PackagePrivateTarget.class, "publicField");

        Assertions.assertNotNull(field);
        Assertions.assertTrue(field.isAccessible());
    }
}

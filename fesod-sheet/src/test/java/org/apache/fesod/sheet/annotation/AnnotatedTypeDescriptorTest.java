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

package org.apache.fesod.sheet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotatedTypeDescriptor}
 */
class AnnotatedTypeDescriptorTest {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {}

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface AnotherAnnotation {}

    @TestAnnotation
    static class AnnotatedClass {}

    static class PlainClass {}

    // ---- Helper methods ----

    private AnnotationMap createAnnotationMap(
            Class<? extends java.lang.annotation.Annotation> type, Map<String, Object> attrs) {
        return AnnotationMap.builder()
                .put(type, new AnnotationAttributes(type, attrs))
                .build();
    }

    private AnnotationMap createEmptyAnnotationMap() {
        return new AnnotationMap(new LinkedHashMap<>());
    }

    // ---- EMPTY constant tests ----

    @Test
    void shouldHaveNullElementAndMap_whenEmpty() {
        // when / then
        Assertions.assertNull(AnnotatedTypeDescriptor.EMPTY.getAnnotatedElement());
        Assertions.assertNull(AnnotatedTypeDescriptor.EMPTY.getAnnotationMap());
        Assertions.assertEquals(0, AnnotatedTypeDescriptor.EMPTY.getAnnotationCount());
        Assertions.assertFalse(AnnotatedTypeDescriptor.EMPTY.hasAnnotation(TestAnnotation.class));
        Assertions.assertNull(AnnotatedTypeDescriptor.EMPTY.getAnnotation(TestAnnotation.class));
    }

    // ---- Constructor tests ----

    @Test
    void shouldCreateDescriptor_whenValidArguments() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();

        // when
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(AnnotatedClass.class, map);

        // then
        Assertions.assertSame(AnnotatedClass.class, descriptor.getAnnotatedElement());
        Assertions.assertSame(map, descriptor.getAnnotationMap());
    }

    @Test
    void shouldAcceptNullElement() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();

        // when
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(null, map);

        // then
        Assertions.assertNull(descriptor.getAnnotatedElement());
        Assertions.assertSame(map, descriptor.getAnnotationMap());
    }

    @Test
    void shouldAcceptNullAnnotationMap() {
        // when
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, null);

        // then
        Assertions.assertSame(PlainClass.class, descriptor.getAnnotatedElement());
        Assertions.assertNull(descriptor.getAnnotationMap());
    }

    @Test
    void shouldAcceptBothNull() {
        // when
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(null, null);

        // then
        Assertions.assertNull(descriptor.getAnnotatedElement());
        Assertions.assertNull(descriptor.getAnnotationMap());
    }

    // ---- Inherited: getAnnotatedElement tests ----

    @Test
    void shouldReturnClass_whenGetAnnotatedElement() {
        // given
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, null);

        // when / then
        Assertions.assertSame(PlainClass.class, descriptor.getAnnotatedElement());
    }

    // ---- Inherited: hasAnnotation tests ----

    @Test
    void shouldReturnTrue_whenAnnotationPresent() {
        // given
        AnnotationMap map = createAnnotationMap(TestAnnotation.class, new LinkedHashMap<>());
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(AnnotatedClass.class, map);

        // when / then
        Assertions.assertTrue(descriptor.hasAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenAnnotationNotPresent() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, map);

        // when / then
        Assertions.assertFalse(descriptor.hasAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenAnnotationMapIsNull() {
        // given
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, null);

        // when / then
        Assertions.assertFalse(descriptor.hasAnnotation(TestAnnotation.class));
    }

    // ---- Inherited: getAnnotationCount tests ----

    @Test
    void shouldReturnCorrectCount_whenAnnotationsPresent() {
        // given
        Map<String, Object> attrs = new LinkedHashMap<>();
        AnnotationMap map = AnnotationMap.builder()
                .put(TestAnnotation.class, new AnnotationAttributes(TestAnnotation.class, attrs))
                .put(AnotherAnnotation.class, new AnnotationAttributes(AnotherAnnotation.class, attrs))
                .build();
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(AnnotatedClass.class, map);

        // when / then
        Assertions.assertEquals(2, descriptor.getAnnotationCount());
    }

    @Test
    void shouldReturnZero_whenNoAnnotations() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, map);

        // when / then
        Assertions.assertEquals(0, descriptor.getAnnotationCount());
    }

    @Test
    void shouldReturnZero_whenAnnotationMapIsNull() {
        // given
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, null);

        // when / then
        Assertions.assertEquals(0, descriptor.getAnnotationCount());
    }

    // ---- Inherited: getAnnotation tests ----

    @Test
    void shouldReturnAttributes_whenAnnotationPresent() {
        // given
        Map<String, Object> attrsMap = new LinkedHashMap<>();
        attrsMap.put("value", "test");
        AnnotationMap map = createAnnotationMap(TestAnnotation.class, attrsMap);
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(AnnotatedClass.class, map);

        // when
        AnnotationAttributes result = descriptor.getAnnotation(TestAnnotation.class);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.get("value"));
    }

    @Test
    void shouldReturnNull_whenAnnotationNotPresent() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, map);

        // when / then
        Assertions.assertNull(descriptor.getAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnNull_whenAnnotationMapIsNull() {
        // given
        AnnotatedTypeDescriptor descriptor = new AnnotatedTypeDescriptor(PlainClass.class, null);

        // when / then
        Assertions.assertNull(descriptor.getAnnotation(TestAnnotation.class));
    }
}

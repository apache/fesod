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
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotatedFieldDescriptor}
 */
@Tag(Tags.UNIT)
class AnnotatedFieldDescriptorTest {

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {}

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface AnotherAnnotation {}

    static class SampleClass {
        @TestAnnotation
        String sampleField;

        String plainField;
    }

    // ---- Helper methods ----

    private Field getField(String name) throws NoSuchFieldException {
        return SampleClass.class.getDeclaredField(name);
    }

    private AnnotationMap createAnnotationMap(
            Class<? extends java.lang.annotation.Annotation> type, Map<String, Object> attrs) {
        return AnnotationMap.builder()
                .put(type, new AnnotationAttributes(type, attrs))
                .build();
    }

    private AnnotationMap createEmptyAnnotationMap() {
        return new AnnotationMap(new LinkedHashMap<>());
    }

    // ---- Constructor tests ----

    @Test
    void shouldCreateDescriptor_whenValidArguments() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        AnnotationMap map = createEmptyAnnotationMap();

        // when
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "sampleField", map);

        // then
        Assertions.assertSame(field, descriptor.getAnnotatedElement());
        Assertions.assertEquals("sampleField", descriptor.getFieldName());
        Assertions.assertSame(map, descriptor.getAnnotationMap());
    }

    @Test
    void shouldThrow_whenFieldIsNull() {
        // given
        AnnotationMap map = createEmptyAnnotationMap();

        // when / then
        Assertions.assertThrows(NullPointerException.class, () -> new AnnotatedFieldDescriptor(null, "field", map));
    }

    @Test
    void shouldThrow_whenFieldNameIsNull() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        AnnotationMap map = createEmptyAnnotationMap();

        // when / then
        Assertions.assertThrows(NullPointerException.class, () -> new AnnotatedFieldDescriptor(field, null, map));
    }

    @Test
    void shouldThrow_whenFieldNameIsBlank() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        AnnotationMap map = createEmptyAnnotationMap();

        // when / then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AnnotatedFieldDescriptor(field, "  ", map));
    }

    @Test
    void shouldAcceptNullAnnotationMap() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");

        // when
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "sampleField", null);

        // then
        Assertions.assertNull(descriptor.getAnnotationMap());
    }

    // ---- Inherited: getAnnotatedElement tests ----

    @Test
    void shouldReturnField_whenGetAnnotatedElement() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", null);

        // when / then
        Assertions.assertSame(field, descriptor.getAnnotatedElement());
    }

    // ---- Inherited: hasAnnotation tests ----

    @Test
    void shouldReturnTrue_whenAnnotationPresent() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        AnnotationMap map = createAnnotationMap(TestAnnotation.class, new LinkedHashMap<>());
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "sampleField", map);

        // when / then
        Assertions.assertTrue(descriptor.hasAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenAnnotationNotPresent() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", map);

        // when / then
        Assertions.assertFalse(descriptor.hasAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenAnnotationMapIsNull() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", null);

        // when / then
        Assertions.assertFalse(descriptor.hasAnnotation(TestAnnotation.class));
    }

    // ---- Inherited: getAnnotationCount tests ----

    @Test
    void shouldReturnCorrectCount_whenAnnotationsPresent() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        Map<String, Object> attrs = new LinkedHashMap<>();
        AnnotationMap map = AnnotationMap.builder()
                .put(TestAnnotation.class, new AnnotationAttributes(TestAnnotation.class, attrs))
                .put(AnotherAnnotation.class, new AnnotationAttributes(AnotherAnnotation.class, attrs))
                .build();
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "sampleField", map);

        // when / then
        Assertions.assertEquals(2, descriptor.getAnnotationCount());
    }

    @Test
    void shouldReturnZero_whenNoAnnotations() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", map);

        // when / then
        Assertions.assertEquals(0, descriptor.getAnnotationCount());
    }

    @Test
    void shouldReturnZero_whenAnnotationMapIsNull() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", null);

        // when / then
        Assertions.assertEquals(0, descriptor.getAnnotationCount());
    }

    // ---- Inherited: getAnnotation tests ----

    @Test
    void shouldReturnAttributes_whenAnnotationPresent() throws NoSuchFieldException {
        // given
        Field field = getField("sampleField");
        Map<String, Object> attrsMap = new LinkedHashMap<>();
        attrsMap.put("value", "test");
        AnnotationMap map = createAnnotationMap(TestAnnotation.class, attrsMap);
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "sampleField", map);

        // when
        AnnotationAttributes result = descriptor.getAnnotation(TestAnnotation.class);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.get("value"));
    }

    @Test
    void shouldReturnNull_whenAnnotationNotPresent() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotationMap map = createEmptyAnnotationMap();
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", map);

        // when / then
        Assertions.assertNull(descriptor.getAnnotation(TestAnnotation.class));
    }

    @Test
    void shouldReturnNull_whenAnnotationMapIsNull() throws NoSuchFieldException {
        // given
        Field field = getField("plainField");
        AnnotatedFieldDescriptor descriptor = new AnnotatedFieldDescriptor(field, "plainField", null);

        // when / then
        Assertions.assertNull(descriptor.getAnnotation(TestAnnotation.class));
    }
}

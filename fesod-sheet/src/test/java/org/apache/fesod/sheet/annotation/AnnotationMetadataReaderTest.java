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
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationMetadataReader}
 */
class AnnotationMetadataReaderTest {

    // ---- Test annotation definitions ----

    @FesodMarked
    @ColumnWidth(35)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposableColumnWidth {
        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int value() default 25;
    }

    // ---- Annotated elements ----

    @ColumnWidth(20)
    static String columnWidthField;

    @ComposableColumnWidth(15)
    static String composableField;

    static String plainField;

    @ColumnWidth(30)
    static class ClassWithColumnWidth {}

    private AnnotationMetadataReader reader;

    @BeforeEach
    void setUp() {
        reader = new AnnotationMetadataReader();
    }

    private Field getField(String name) {
        try {
            return AnnotationMetadataReaderTest.class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- read tests ----

    @Test
    void shouldReadInnerAnnotation_fromAnnotatedField() {
        // given
        Field field = getField("columnWidthField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        AnnotationAttributes attrs = result.getAttributes(ColumnWidth.class);

        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(20, actualValue);
    }

    @Test
    void shouldReadInnerAnnotation_fromAnnotatedClass() {
        // given
        Class<?> clazz = ClassWithColumnWidth.class;

        // when
        AnnotationMap result = reader.read(clazz);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        AnnotationAttributes attrs = result.getAttributes(ColumnWidth.class);

        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(30, actualValue);
    }

    @Test
    void shouldSynthesizeAlias_fromComposableAnnotation() {
        // given
        Field field = getField("composableField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        // AliasFor overrides ColumnWidth.value from 25 (on meta-annotation) to 15 (from composable)
        AnnotationAttributes attrs = result.getAttributes(ColumnWidth.class);

        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(15, actualValue);
    }

    @Test
    void shouldContainComposableAnnotation_inAnnotationMap() {
        // given
        Field field = getField("composableField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ComposableColumnWidth.class));
        AnnotationAttributes attrs = result.getAttributes(ComposableColumnWidth.class);

        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(15, actualValue);
    }

    @Test
    void shouldReturnNull_fromUnannotatedField() {
        // given
        Field field = getField("plainField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNull(result);
    }

    // ---- Caching tests ----

    @Test
    void shouldReturnSameInstance_whenReadTwiceOnSameElement() {
        // given
        Field field = getField("columnWidthField");

        // when
        AnnotationMap first = reader.read(field);
        AnnotationMap second = reader.read(field);

        // then
        Assertions.assertSame(first, second);
    }

    @Test
    void shouldReturnIndependentResults_forDifferentElements() {
        // given
        Field field = getField("columnWidthField");
        Class<?> clazz = ClassWithColumnWidth.class;

        // when
        AnnotationMap fieldResult = reader.read(field);
        AnnotationMap classResult = reader.read(clazz);

        // then
        Assertions.assertNotSame(fieldResult, classResult);
        AnnotationAttributes fieldAttrs = fieldResult.getAttributes(ColumnWidth.class);
        AnnotationAttributes classAttrs = classResult.getAttributes(ColumnWidth.class);

        Integer fieldValue =
                Assertions.assertDoesNotThrow(() -> fieldAttrs.getRequiredAttribute("value", Integer.class));
        Integer classValue =
                Assertions.assertDoesNotThrow(() -> classAttrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(20, fieldValue);
        Assertions.assertEquals(30, classValue);
    }
}

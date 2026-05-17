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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationAttributes}
 */
class AnnotationAttributesTest {

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {}

    // ---- Constructor tests ----

    @Test
    void shouldSetAnnotationTypeAndName_whenConstructedWithTypeOnly() {
        // given
        Class<? extends Annotation> type = TestAnnotation.class;

        // when
        AnnotationAttributes attrs = new AnnotationAttributes(type);

        // then
        Assertions.assertSame(type, attrs.getAnnotationType());
        Assertions.assertEquals(type.getCanonicalName(), attrs.getAnnotationName());
        Assertions.assertEquals(0, attrs.getDistance());
        Assertions.assertTrue(attrs.isEmpty());
    }

    @Test
    void shouldContainAllEntries_whenConstructedWithMap() {
        // given
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", "test");
        map.put("index", 5);

        // when
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class, map);

        // then
        Assertions.assertEquals(2, attrs.size());
        Assertions.assertEquals("test", attrs.get("value"));
        Assertions.assertEquals(5, attrs.get("index"));
        Assertions.assertEquals(0, attrs.getDistance());
    }

    @Test
    void shouldBeIndependentFromSourceMap_whenConstructedWithMap() {
        // given
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", "original");
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class, map);

        // when - modify source map after construction
        map.put("key", "modified");
        map.put("extra", "value");

        // then - attrs should not be affected
        Assertions.assertEquals("original", attrs.get("key"));
        Assertions.assertEquals(1, attrs.size());
    }

    // ---- getRequiredAttribute tests ----

    @Test
    void shouldReturnValue_whenAttributePresentAndTypeMatches() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("name", "hello");

        // when
        String result = attrs.getRequiredAttribute("name", String.class);

        // then
        Assertions.assertEquals("hello", result);
    }

    @Test
    void shouldReturnInteger_whenAttributeIsInteger() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("count", 42);

        // when
        Integer result = attrs.getRequiredAttribute("count", Integer.class);

        // then
        Assertions.assertEquals(42, result);
    }

    @Test
    void shouldReturnShort_whenAttributeIsShort() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("height", (short) 14);

        // when
        Short result = attrs.getRequiredAttribute("height", Short.class);

        // then
        Assertions.assertEquals((short) 14, result);
    }

    @Test
    void shouldReturnEnum_whenAttributeIsEnum() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("bold", BooleanEnum.TRUE);

        // when
        BooleanEnum result = attrs.getRequiredAttribute("bold", BooleanEnum.class);

        // then
        Assertions.assertEquals(BooleanEnum.TRUE, result);
    }

    @Test
    void shouldReturnStringArray_whenAttributeIsStringArray() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", new String[] {"first", "second"});

        // when
        String[] result = attrs.getRequiredAttribute("value", String[].class);

        // then
        Assertions.assertArrayEquals(new String[] {"first", "second"}, result);
    }

    @Test
    void shouldWrapSingleValueIntoArray_whenArrayTypeRequestedButScalarStored() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "single");

        // when
        String[] result = attrs.getRequiredAttribute("value", String[].class);

        // then
        Assertions.assertArrayEquals(new String[] {"single"}, result);
    }

    @Test
    void shouldThrow_whenAttributeNotFound() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when / then
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class, () -> attrs.getRequiredAttribute("missing", String.class));
        Assertions.assertTrue(ex.getMessage().contains("missing"));
        Assertions.assertTrue(ex.getMessage().contains(TestAnnotation.class.getCanonicalName()));
    }

    @Test
    void shouldThrow_whenTypeMismatch() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "string-value");

        // when / then
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class, () -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertTrue(ex.getMessage().contains("value"));
        Assertions.assertTrue(ex.getMessage().contains("String"));
        Assertions.assertTrue(ex.getMessage().contains("Integer"));
    }

    @Test
    void shouldThrow_whenAttrNameIsNull() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "test");

        // when / then
        Assertions.assertThrows(Exception.class, () -> attrs.getRequiredAttribute(null, String.class));
    }

    @Test
    void shouldThrow_whenAttrNameIsBlank() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "test");

        // when / then
        Assertions.assertThrows(Exception.class, () -> attrs.getRequiredAttribute("  ", String.class));
    }

    // ---- Distance tests ----

    @Test
    void shouldReturnDefaultDistance_whenConstructed() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when / then
        Assertions.assertEquals(0, attrs.getDistance());
    }

    @Test
    void shouldUpdateDistance_whenSetDistanceCalled() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when
        attrs.setDistance(3);

        // then
        Assertions.assertEquals(3, attrs.getDistance());
    }

    // ---- Map operation tests ----

    @Test
    void shouldSupportPutAndGet_whenUsedAsMap() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when
        attrs.put("key1", "value1");
        attrs.put("key2", 100);

        // then
        Assertions.assertEquals(2, attrs.size());
        Assertions.assertEquals("value1", attrs.get("key1"));
        Assertions.assertEquals(100, attrs.get("key2"));
    }

    @Test
    void shouldOverwriteValue_whenSameKeyPutTwice() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("key", "first");

        // when
        attrs.put("key", "second");

        // then
        Assertions.assertEquals(1, attrs.size());
        Assertions.assertEquals("second", attrs.get("key"));
    }

    @Test
    void shouldReturnNull_whenKeyNotPresent() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when / then
        Assertions.assertNull(attrs.get("nonexistent"));
    }

    // ---- Equality tests ----

    @Test
    void shouldBeEqual_whenSameTypeSameEntriesSameDistance() {
        // given
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", "test");
        map.put("index", 1);

        AnnotationAttributes attrs1 = new AnnotationAttributes(TestAnnotation.class, map);
        AnnotationAttributes attrs2 = new AnnotationAttributes(TestAnnotation.class, map);

        // when / then
        Assertions.assertEquals(attrs1, attrs2);
        Assertions.assertEquals(attrs1.hashCode(), attrs2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentEntries() {
        // given
        AnnotationAttributes attrs1 = new AnnotationAttributes(TestAnnotation.class);
        attrs1.put("value", "a");

        AnnotationAttributes attrs2 = new AnnotationAttributes(TestAnnotation.class);
        attrs2.put("value", "b");

        // when / then
        Assertions.assertNotEquals(attrs1, attrs2);
    }

    @Test
    void shouldNotBeEqual_whenDifferentAnnotationType() {
        // given
        AnnotationAttributes attrs1 = new AnnotationAttributes(TestAnnotation.class);
        AnnotationAttributes attrs2 = new AnnotationAttributes(Target.class);

        // when / then
        Assertions.assertNotEquals(attrs1, attrs2);
    }
}

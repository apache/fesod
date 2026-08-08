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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationMap}
 */
@Tag(Tags.UNIT)
class AnnotationMapTest {

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface FirstAnnotation {}

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface SecondAnnotation {}

    // ---- isEmpty tests ----

    @Test
    void shouldBeEmpty_whenConstructedWithEmptyMap() {
        // given
        AnnotationMap map = new AnnotationMap(Collections.emptyMap());

        // when / then
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void shouldBeEmpty_whenConstructedWithNullMap() {
        // given
        AnnotationMap map = new AnnotationMap(null);

        // when / then
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void shouldNotBeEmpty_whenContainingAnnotations() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        AnnotationMap map = new AnnotationMap(data);

        // when / then
        Assertions.assertFalse(map.isEmpty());
    }

    // ---- size tests ----

    @Test
    void shouldReturnZero_whenEmpty() {
        // given
        AnnotationMap map = new AnnotationMap(Collections.emptyMap());

        // when / then
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void shouldReturnCorrectSize_whenContainingAnnotations() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        data.put(SecondAnnotation.class, new AnnotationAttributes(SecondAnnotation.class));
        AnnotationMap map = new AnnotationMap(data);

        // when / then
        Assertions.assertEquals(2, map.size());
    }

    // ---- hasAnnotation tests ----

    @Test
    void shouldReturnTrue_whenAnnotationPresent() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        AnnotationMap map = new AnnotationMap(data);

        // when / then
        Assertions.assertTrue(map.hasAnnotation(FirstAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenAnnotationAbsent() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        AnnotationMap map = new AnnotationMap(data);

        // when / then
        Assertions.assertFalse(map.hasAnnotation(SecondAnnotation.class));
    }

    @Test
    void shouldReturnFalse_whenMapIsEmpty() {
        // given
        AnnotationMap map = new AnnotationMap(Collections.emptyMap());

        // when / then
        Assertions.assertFalse(map.hasAnnotation(FirstAnnotation.class));
    }

    // ---- getAttributes tests ----

    @Test
    void shouldReturnAttributes_whenAnnotationPresent() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);
        attrs.put("value", "test");
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, attrs);
        AnnotationMap map = new AnnotationMap(data);

        // when
        AnnotationAttributes result = map.getAttributes(FirstAnnotation.class);

        // then
        Assertions.assertSame(attrs, result);
        Assertions.assertEquals("test", result.get("value"));
    }

    @Test
    void shouldReturnNull_whenAnnotationAbsent() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data = new LinkedHashMap<>();
        data.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        AnnotationMap map = new AnnotationMap(data);

        // when / then
        Assertions.assertNull(map.getAttributes(SecondAnnotation.class));
    }

    @Test
    void shouldReturnNull_whenMapIsEmpty() {
        // given
        AnnotationMap map = new AnnotationMap(Collections.emptyMap());

        // when / then
        Assertions.assertNull(map.getAttributes(FirstAnnotation.class));
    }

    // ---- Builder.put tests ----

    @Test
    void shouldPutAnnotation_whenUsingBuilder() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);
        attrs.put("value", "hello");

        // when
        AnnotationMap map =
                AnnotationMap.builder().put(FirstAnnotation.class, attrs).build();

        // then
        Assertions.assertTrue(map.hasAnnotation(FirstAnnotation.class));
        Assertions.assertEquals(
                "hello", map.getAttributes(FirstAnnotation.class).get("value"));
    }

    @Test
    void shouldOverwrite_whenPutSameTypeTwice() {
        // given
        AnnotationAttributes first = new AnnotationAttributes(FirstAnnotation.class);
        first.put("value", "first");
        AnnotationAttributes second = new AnnotationAttributes(FirstAnnotation.class);
        second.put("value", "second");

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, first)
                .put(FirstAnnotation.class, second)
                .build();

        // then
        Assertions.assertEquals(
                "second", map.getAttributes(FirstAnnotation.class).get("value"));
    }

    @Test
    void shouldPutMultiple_whenDifferentTypes() {
        // given
        AnnotationAttributes attrs1 = new AnnotationAttributes(FirstAnnotation.class);
        AnnotationAttributes attrs2 = new AnnotationAttributes(SecondAnnotation.class);

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, attrs1)
                .put(SecondAnnotation.class, attrs2)
                .build();

        // then
        Assertions.assertEquals(2, map.size());
        Assertions.assertTrue(map.hasAnnotation(FirstAnnotation.class));
        Assertions.assertTrue(map.hasAnnotation(SecondAnnotation.class));
    }

    @Test
    void shouldThrow_whenPutNullAnnotationType() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);

        // when / then
        Assertions.assertThrows(
                NullPointerException.class, () -> AnnotationMap.builder().put(null, attrs));
    }

    @Test
    void shouldThrow_whenPutNullAttributes() {
        // when / then
        Assertions.assertThrows(
                NullPointerException.class, () -> AnnotationMap.builder().put(FirstAnnotation.class, null));
    }

    // ---- Builder.merge tests ----

    @Test
    void shouldAddWhenNoExisting_whenMerge() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);
        attrs.setDistance(0);

        // when
        AnnotationMap map =
                AnnotationMap.builder().merge(FirstAnnotation.class, attrs).build();

        // then
        Assertions.assertTrue(map.hasAnnotation(FirstAnnotation.class));
        Assertions.assertSame(attrs, map.getAttributes(FirstAnnotation.class));
    }

    @Test
    void shouldMerge_whenNewDistanceLowerThanExisting() {
        // given
        AnnotationAttributes existing = new AnnotationAttributes(FirstAnnotation.class);
        existing.put("value", "original");
        existing.setDistance(2);

        AnnotationAttributes incoming = new AnnotationAttributes(FirstAnnotation.class);
        incoming.put("value", "override");
        incoming.put("extra", "added");
        incoming.setDistance(1);

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, existing)
                .merge(FirstAnnotation.class, incoming)
                .build();

        // then
        AnnotationAttributes result = map.getAttributes(FirstAnnotation.class);
        Assertions.assertEquals("override", result.get("value"));
        Assertions.assertEquals("added", result.get("extra"));
    }

    @Test
    void shouldNotMerge_whenNewDistanceHigherThanExisting() {
        // given
        AnnotationAttributes existing = new AnnotationAttributes(FirstAnnotation.class);
        existing.put("value", "original");
        existing.setDistance(1);

        AnnotationAttributes incoming = new AnnotationAttributes(FirstAnnotation.class);
        incoming.put("value", "should-not-override");
        incoming.setDistance(2);

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, existing)
                .merge(FirstAnnotation.class, incoming)
                .build();

        // then
        Assertions.assertEquals(
                "original", map.getAttributes(FirstAnnotation.class).get("value"));
    }

    @Test
    void shouldNotMerge_whenNewDistanceSameAsExisting() {
        // given
        AnnotationAttributes existing = new AnnotationAttributes(FirstAnnotation.class);
        existing.put("value", "original");
        existing.setDistance(1);

        AnnotationAttributes incoming = new AnnotationAttributes(FirstAnnotation.class);
        incoming.put("value", "should-not-override");
        incoming.setDistance(1);

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, existing)
                .merge(FirstAnnotation.class, incoming)
                .build();

        // then
        Assertions.assertEquals(
                "original", map.getAttributes(FirstAnnotation.class).get("value"));
    }

    @Test
    void shouldMergeMultipleSteps_whenDistanceProgressivelyLower() {
        // given
        AnnotationAttributes dist2 = new AnnotationAttributes(FirstAnnotation.class);
        dist2.put("a", "from-dist2");
        dist2.setDistance(2);

        AnnotationAttributes dist1 = new AnnotationAttributes(FirstAnnotation.class);
        dist1.put("a", "from-dist1");
        dist1.put("b", "from-dist1");
        dist1.setDistance(1);

        AnnotationAttributes dist0 = new AnnotationAttributes(FirstAnnotation.class);
        dist0.put("c", "from-dist0");
        dist0.setDistance(0);

        // when
        AnnotationMap map = AnnotationMap.builder()
                .merge(FirstAnnotation.class, dist2)
                .merge(FirstAnnotation.class, dist1)
                .merge(FirstAnnotation.class, dist0)
                .build();

        // then
        AnnotationAttributes result = map.getAttributes(FirstAnnotation.class);
        Assertions.assertEquals("from-dist1", result.get("a"));
        Assertions.assertEquals("from-dist1", result.get("b"));
        Assertions.assertEquals("from-dist0", result.get("c"));
    }

    @Test
    void shouldThrow_whenMergeNullAnnotationType() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);

        // when / then
        Assertions.assertThrows(
                NullPointerException.class, () -> AnnotationMap.builder().merge(null, attrs));
    }

    @Test
    void shouldThrow_whenMergeNullAttributes() {
        // when / then
        Assertions.assertThrows(
                NullPointerException.class, () -> AnnotationMap.builder().merge(FirstAnnotation.class, null));
    }

    // ---- Builder.build tests ----

    @Test
    void shouldBuildEmptyMap_whenNoPuts() {
        // when
        AnnotationMap map = AnnotationMap.builder().build();

        // then
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void shouldBuildWithAllEntries_whenMultiplePuts() {
        // given
        AnnotationAttributes attrs1 = new AnnotationAttributes(FirstAnnotation.class);
        attrs1.put("key1", "val1");
        AnnotationAttributes attrs2 = new AnnotationAttributes(SecondAnnotation.class);
        attrs2.put("key2", "val2");

        // when
        AnnotationMap map = AnnotationMap.builder()
                .put(FirstAnnotation.class, attrs1)
                .put(SecondAnnotation.class, attrs2)
                .build();

        // then
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("val1", map.getAttributes(FirstAnnotation.class).get("key1"));
        Assertions.assertEquals(
                "val2", map.getAttributes(SecondAnnotation.class).get("key2"));
    }

    // ---- Equality tests ----

    @Test
    void shouldBeEqual_whenSameAnnotations() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(FirstAnnotation.class);
        Map<Class<? extends Annotation>, AnnotationAttributes> data1 = new LinkedHashMap<>();
        data1.put(FirstAnnotation.class, attrs);
        Map<Class<? extends Annotation>, AnnotationAttributes> data2 = new LinkedHashMap<>();
        data2.put(FirstAnnotation.class, attrs);

        AnnotationMap map1 = new AnnotationMap(data1);
        AnnotationMap map2 = new AnnotationMap(data2);

        // when / then
        Assertions.assertEquals(map1, map2);
        Assertions.assertEquals(map1.hashCode(), map2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentAnnotations() {
        // given
        Map<Class<? extends Annotation>, AnnotationAttributes> data1 = new LinkedHashMap<>();
        data1.put(FirstAnnotation.class, new AnnotationAttributes(FirstAnnotation.class));
        Map<Class<? extends Annotation>, AnnotationAttributes> data2 = new LinkedHashMap<>();
        data2.put(SecondAnnotation.class, new AnnotationAttributes(SecondAnnotation.class));

        AnnotationMap map1 = new AnnotationMap(data1);
        AnnotationMap map2 = new AnnotationMap(data2);

        // when / then
        Assertions.assertNotEquals(map1, map2);
    }
}

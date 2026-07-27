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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationMetadata}
 */
class AnnotationMetadataTest {

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {}

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TargetAnnotation {}

    // ---- Constructor / getter tests ----

    @Test
    void shouldStoreAttributesAndAliases_whenConstructed() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "test");
        List<AliasFor> aliases =
                Collections.singletonList(new AliasFor(TestAnnotation.class, TargetAnnotation.class, "value", "test"));

        // when
        AnnotationMetadata metadata = new AnnotationMetadata(attrs, aliases);

        // then
        Assertions.assertSame(attrs, metadata.getAttributes());
        Assertions.assertSame(aliases, metadata.getAliases());
        Assertions.assertEquals("test", metadata.getAttributes().get("value"));
    }

    @Test
    void shouldStoreEmptyAliases_whenConstructedWithEmptyList() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);

        // when
        AnnotationMetadata metadata = new AnnotationMetadata(attrs, Collections.emptyList());

        // then
        Assertions.assertTrue(metadata.getAliases().isEmpty());
    }

    @Test
    void shouldStoreMultipleAliases_whenConstructedWithMultiple() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        List<AliasFor> aliases = Arrays.asList(
                new AliasFor(TestAnnotation.class, TargetAnnotation.class, "value", "a"),
                new AliasFor(TestAnnotation.class, TargetAnnotation.class, "index", 1));

        // when
        AnnotationMetadata metadata = new AnnotationMetadata(attrs, aliases);

        // then
        Assertions.assertEquals(2, metadata.getAliases().size());
        Assertions.assertEquals("value", metadata.getAliases().get(0).getAttribute());
        Assertions.assertEquals("index", metadata.getAliases().get(1).getAttribute());
    }

    // ---- addTo tests ----

    @Test
    void shouldAddAllAliases_whenAddToTargetList() {
        // given
        AliasFor alias1 = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "value", "a");
        AliasFor alias2 = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "index", 1);
        AnnotationMetadata metadata =
                new AnnotationMetadata(new AnnotationAttributes(TestAnnotation.class), Arrays.asList(alias1, alias2));
        List<AliasFor> target = new ArrayList<>();

        // when
        metadata.addTo(target);

        // then
        Assertions.assertEquals(2, target.size());
        Assertions.assertSame(alias1, target.get(0));
        Assertions.assertSame(alias2, target.get(1));
    }

    @Test
    void shouldNotModifyTarget_whenAliasesEmpty() {
        // given
        AnnotationMetadata metadata =
                new AnnotationMetadata(new AnnotationAttributes(TestAnnotation.class), Collections.emptyList());
        List<AliasFor> target = new ArrayList<>();

        // when
        metadata.addTo(target);

        // then
        Assertions.assertTrue(target.isEmpty());
    }

    @Test
    void shouldAppendToExisting_whenTargetNotEmpty() {
        // given
        AliasFor existingAlias = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "existing", "val");
        AliasFor newAlias = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "added", "val");
        AnnotationMetadata metadata = new AnnotationMetadata(
                new AnnotationAttributes(TestAnnotation.class), Collections.singletonList(newAlias));
        List<AliasFor> target = new ArrayList<>();
        target.add(existingAlias);

        // when
        metadata.addTo(target);

        // then
        Assertions.assertEquals(2, target.size());
        Assertions.assertSame(existingAlias, target.get(0));
        Assertions.assertSame(newAlias, target.get(1));
    }

    // ---- setDistance tests ----

    @Test
    void shouldDelegateToAttributes_whenSetDistance() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        AnnotationMetadata metadata = new AnnotationMetadata(attrs, Collections.emptyList());

        // when
        metadata.setDistance(3);

        // then
        Assertions.assertEquals(3, attrs.getDistance());
        Assertions.assertEquals(3, metadata.getAttributes().getDistance());
    }

    @Test
    void shouldUpdateDistance_whenSetDistanceMultipleTimes() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        AnnotationMetadata metadata = new AnnotationMetadata(attrs, Collections.emptyList());

        // when
        metadata.setDistance(5);
        metadata.setDistance(0);

        // then
        Assertions.assertEquals(0, attrs.getDistance());
    }

    // ---- Equality tests ----

    @Test
    void shouldBeEqual_whenSameAttributesAndAliases() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        attrs.put("value", "test");
        AliasFor alias = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "value", "test");

        AnnotationMetadata meta1 = new AnnotationMetadata(attrs, Collections.singletonList(alias));
        AnnotationMetadata meta2 = new AnnotationMetadata(attrs, Collections.singletonList(alias));

        // when / then
        Assertions.assertEquals(meta1, meta2);
        Assertions.assertEquals(meta1.hashCode(), meta2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentAttributes() {
        // given
        AnnotationAttributes attrs1 = new AnnotationAttributes(TestAnnotation.class);
        AnnotationAttributes attrs2 = new AnnotationAttributes(TestAnnotation.class);
        attrs2.put("value", "different");

        AnnotationMetadata meta1 = new AnnotationMetadata(attrs1, Collections.emptyList());
        AnnotationMetadata meta2 = new AnnotationMetadata(attrs2, Collections.emptyList());

        // when / then
        Assertions.assertNotEquals(meta1, meta2);
    }

    @Test
    void shouldNotBeEqual_whenDifferentAliases() {
        // given
        AnnotationAttributes attrs = new AnnotationAttributes(TestAnnotation.class);
        AliasFor alias = new AliasFor(TestAnnotation.class, TargetAnnotation.class, "value", "a");

        AnnotationMetadata meta1 = new AnnotationMetadata(attrs, Collections.singletonList(alias));
        AnnotationMetadata meta2 = new AnnotationMetadata(attrs, Collections.emptyList());

        // when / then
        Assertions.assertNotEquals(meta1, meta2);
    }
}

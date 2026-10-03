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
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link AnnotatedElementUtils}: composable-annotation merging, {@code @FesodMarked.AliasFor} overrides and
 * synthesized annotation proxies.
 */
@Tag(Tags.UNIT)
class AnnotatedElementUtilsTest {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(index = 3, value = "meta-head")
    @interface MetaProperty {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(index = 7)
    @interface AliasedProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        int column() default 42;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface SingleHeadProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String head() default "single";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface SameNameProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class)
        int index() default 11;
    }

    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @CycleB
    @interface CycleA {}

    @Target(ElementType.ANNOTATION_TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @CycleA
    @interface CycleB {}

    /**
     * Declares an alias for a meta-annotation that is not meta-present, which the
     * resolver must reject instead of silently ignoring.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @interface BrokenAliasProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        int column() default 1;
    }

    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface LayeredProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        int column() default 42;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @LayeredProperty(column = 8)
    @interface ComposedLayeredProperty {}

    /** Aliases an attribute name that the target annotation does not declare. */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface TypoAliasProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "indx")
        int column() default 1;
    }

    /** Declares an alias whose return type is incompatible with the target attribute. */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface BadTypeAliasProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        String column() default "x";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @interface SuppressedAliasProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String head() default "Preset";

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        int column() default 42;
    }

    /** Two composed annotations declaring the same target type with different presets. */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(index = 1, value = "first-declared")
    @interface FirstDeclaredProperty {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(index = 2, value = "second-declared")
    @interface SecondDeclaredProperty {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    public @interface Middle {
        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String name() default "Preset";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @Middle
    public @interface Outer {
        @FesodMarked.AliasFor(annotation = Middle.class, attribute = "name")
        String title() default "";
    }

    @MetaProperty
    private String composedOnly;

    @MetaProperty
    @ExcelProperty(index = 5)
    private String directAndComposed;

    @LayeredProperty
    @ComposedLayeredProperty
    private String layered;

    @AliasedProperty
    private String aliasedDefault;

    @AliasedProperty(column = 9)
    private String aliasedExplicit;

    @SingleHeadProperty
    private String scalarAlias;

    @SameNameProperty
    private String sameNameAlias;

    @CycleA
    private String cyclic;

    @BrokenAliasProperty
    private String brokenAlias;

    @TypoAliasProperty
    private String typoAlias;

    @BadTypeAliasProperty
    private String badTypeAlias;

    @ExcelProperty(index = 5)
    @SuppressedAliasProperty
    private String suppressedAlias;

    @FirstDeclaredProperty
    @SecondDeclaredProperty
    private String duplicateTarget;

    @ExcelProperty(index = 2, value = "proxy")
    private String proxySource;

    @Outer(title = "chained")
    private String chained;

    private String unannotated;

    @Test
    void shouldSurfaceMetaDeclaredAttributesThroughComposedAnnotation() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("composedOnly"), ExcelProperty.class);

        Assertions.assertNotNull(merged);
        Assertions.assertEquals(3, merged.index());
        Assertions.assertArrayEquals(new String[] {"meta-head"}, merged.value());
        Assertions.assertTrue(AnnotatedElementUtils.isAnnotated(field("composedOnly"), ExcelProperty.class));
    }

    @Test
    void shouldPreferDirectAnnotationOverComposedDeclaration() throws Exception {
        Field field = field("directAndComposed");

        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class);
        Assertions.assertEquals(5, merged.index());
        Assertions.assertArrayEquals(new String[] {""}, merged.value());

        AnnotationAttributes attributes =
                AnnotatedElementUtils.getMergedAnnotationAttributes(field, ExcelProperty.class);
        Assertions.assertNotNull(attributes);
        Assertions.assertEquals(Integer.valueOf(5), attributes.getRequiredAttribute("index", Integer.class));
    }

    @Test
    void shouldPreferDirectMarkedOccurrenceOverComposedPreset() throws Exception {
        Field field = field("layered");

        Assertions.assertEquals(
                42,
                AnnotatedElementUtils.getMergedAnnotation(field, LayeredProperty.class)
                        .column());
        Assertions.assertEquals(
                42,
                AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class)
                        .index());
    }

    @Test
    void shouldApplyAliasOverrideEvenWhenAliasAttributeIsAtDefault() throws Exception {
        // the alias value always overrides the target, so meta-declared index=7 must not win
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("aliasedDefault"), ExcelProperty.class);
        Assertions.assertEquals(42, merged.index());
    }

    @Test
    void shouldApplyExplicitAliasOverride() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("aliasedExplicit"), ExcelProperty.class);
        Assertions.assertEquals(9, merged.index());
    }

    @Test
    void shouldIgnoreComposedAliasesWhenTargetDirectlyAnnotated() throws Exception {
        Field field = field("suppressedAlias");

        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class);
        Assertions.assertEquals(5, merged.index());
        Assertions.assertArrayEquals(new String[] {""}, merged.value());

        SuppressedAliasProperty customAnnotation =
                AnnotatedElementUtils.getMergedAnnotation(field, SuppressedAliasProperty.class);
        Assertions.assertEquals(42, customAnnotation.column());
        Assertions.assertEquals("Preset", customAnnotation.head());
    }

    @Test
    void shouldKeepFirstDeclaredOccurrenceForDuplicateTargetType() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("duplicateTarget"), ExcelProperty.class);

        // first-occurrence-wins: the later composed declaration of the same target type is discarded wholesale
        Assertions.assertEquals(1, merged.index());
        Assertions.assertArrayEquals(new String[] {"first-declared"}, merged.value());
    }

    @Test
    void shouldPromoteScalarAliasValueToArrayAttribute() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("scalarAlias"), ExcelProperty.class);
        Assertions.assertArrayEquals(new String[] {"single"}, merged.value());
    }

    @Test
    void shouldDefaultBlankAliasAttributeToSameNamedAttribute() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("sameNameAlias"), ExcelProperty.class);
        Assertions.assertEquals(11, merged.index());
    }

    @Test
    void shouldTerminateOnCyclicMarkedAnnotations() throws Exception {
        Field field = field("cyclic");

        Assertions.assertNotNull(AnnotatedElementUtils.getMergedAnnotation(field, CycleA.class));
        Assertions.assertTrue(AnnotatedElementUtils.isAnnotated(field, CycleB.class));
    }

    @Test
    void shouldFailFastWhenAliasTargetIsNotMetaPresent() throws Exception {
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> AnnotatedElementUtils.getMergedAnnotation(field("brokenAlias"), ExcelProperty.class));
    }

    @Test
    void shouldFailFastWhenAliasAttributeIsNotDeclaredOnTarget() throws Exception {
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> AnnotatedElementUtils.getMergedAnnotation(field("typoAlias"), ExcelProperty.class));
    }

    @Test
    void shouldFailFastWhenAliasReturnTypeIsIncompatible() throws Exception {
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> AnnotatedElementUtils.getMergedAnnotation(field("badTypeAlias"), ExcelProperty.class));
    }

    @Test
    void shouldRejectAttributeRequestedWithWrongType() throws Exception {
        AnnotationAttributes attributes =
                AnnotatedElementUtils.getMergedAnnotationAttributes(field("proxySource"), ExcelProperty.class);

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> attributes.getRequiredAttribute("index", String.class));
    }

    @Test
    void shouldReadPrimitiveAttributesFromSynthesizedAnnotation() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("proxySource"), ExcelProperty.class);
        Assertions.assertEquals(2, merged.index());
    }

    @Test
    void shouldServeObjectMethodsOnSynthesizedAnnotation() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("proxySource"), ExcelProperty.class);

        Assertions.assertEquals(ExcelProperty.class, merged.annotationType());
        Assertions.assertEquals(
                merged, AnnotatedElementUtils.getMergedAnnotation(field("proxySource"), ExcelProperty.class));
        Assertions.assertEquals(merged, field("proxySource").getAnnotation(ExcelProperty.class));
        // equal objects must hash equally: synthesized hashCode follows the JLS annotation formula
        Assertions.assertEquals(
                field("proxySource").getAnnotation(ExcelProperty.class).hashCode(), merged.hashCode());
        Assertions.assertFalse(merged.equals(null));
        Assertions.assertTrue(merged.toString().contains("proxy"));
    }

    @Test
    void shouldPropagateChainedAliasesInNestedComposedAnnotations() throws Exception {
        ExcelProperty merged = AnnotatedElementUtils.getMergedAnnotation(field("chained"), ExcelProperty.class);
        Assertions.assertArrayEquals(new String[] {"chained"}, merged.value());
    }

    @Test
    void shouldReportMissingAnnotationAsAbsent() throws Exception {
        Field field = field("unannotated");

        Assertions.assertNull(AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class));
        Assertions.assertNull(AnnotatedElementUtils.getMergedAnnotationAttributes(field, ExcelProperty.class));
        Assertions.assertFalse(AnnotatedElementUtils.isAnnotated(field, ExcelProperty.class));
    }

    @Test
    void shouldTolerateNullElement() {
        Assertions.assertNull(AnnotatedElementUtils.getMergedAnnotation(null, ExcelProperty.class));
        Assertions.assertFalse(AnnotatedElementUtils.isAnnotated(null, ExcelProperty.class));
    }

    private Field field(String name) throws NoSuchFieldException {
        return getClass().getDeclaredField(name);
    }
}

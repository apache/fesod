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
import java.util.List;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationMetadataReader}
 */
@Tag(Tags.UNIT)
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

    /**
     * Method name ({@code width}) differs from the aliased target attribute ({@code value}),
     * exercising the {@code customAttribute} path in {@link AliasFor}.
     */
    @FesodMarked
    @ColumnWidth(35)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposableColumnWidthRenamed {
        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int width() default 25;
    }

    // ---- Annotated elements ----

    @ColumnWidth(20)
    static String columnWidthField;

    @ComposableColumnWidth(15)
    static String composableField;

    @ComposableColumnWidthRenamed(width = 18)
    static String renamedAliasField;

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

    // ---- enableMetaMarked = false tests ----

    @Test
    void shouldReadInnerAnnotation_whenMetaMarkedDisabled() {
        // given
        AnnotationMetadataReader disabledReader = new AnnotationMetadataReader(Boolean.FALSE);
        Field field = getField("columnWidthField");

        // when
        AnnotationMap result = disabledReader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        AnnotationAttributes attrs = result.getAttributes(ColumnWidth.class);
        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(20, actualValue);
    }

    @Test
    void shouldNotResolveComposableAnnotation_whenMetaMarkedDisabled() {
        // given
        AnnotationMetadataReader disabledReader = new AnnotationMetadataReader(Boolean.FALSE);
        Field field = getField("composableField");

        // when
        AnnotationMap result = disabledReader.read(field);

        // then
        // ComposableColumnWidth is not an inner annotation and meta-marked scanning is disabled,
        // so neither ComposableColumnWidth nor its meta-annotation ColumnWidth should appear.
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.hasAnnotation(ComposableColumnWidth.class));
        Assertions.assertFalse(result.hasAnnotation(ColumnWidth.class));
    }

    @Test
    void shouldReturnNull_fromUnannotatedField_whenMetaMarkedDisabled() {
        // given
        AnnotationMetadataReader disabledReader = new AnnotationMetadataReader(Boolean.FALSE);
        Field field = getField("plainField");

        // when
        AnnotationMap result = disabledReader.read(field);

        // then
        Assertions.assertNull(result);
    }

    @Test
    void shouldReturnSameInstance_whenReadTwiceWithMetaMarkedDisabled() {
        // given
        AnnotationMetadataReader disabledReader = new AnnotationMetadataReader(Boolean.FALSE);
        Field field = getField("columnWidthField");

        // when
        AnnotationMap first = disabledReader.read(field);
        AnnotationMap second = disabledReader.read(field);

        // then
        Assertions.assertSame(first, second);
    }

    @Test
    void shouldResolveComposableAnnotation_whenMetaMarkedEnabled() {
        // given
        AnnotationMetadataReader enabledReader = new AnnotationMetadataReader(Boolean.TRUE);
        Field field = getField("composableField");

        // when
        AnnotationMap result = enabledReader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ComposableColumnWidth.class));
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        // AliasFor should synthesize: ComposableColumnWidth.value(15) -> ColumnWidth.value
        AnnotationAttributes columnWidthAttrs = result.getAttributes(ColumnWidth.class);
        Integer value =
                Assertions.assertDoesNotThrow(() -> columnWidthAttrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(15, value);
    }

    // ---- Renamed @AliasFor (customAttribute) tests ----

    @Test
    void shouldSynthesizeAlias_whenMethodNameDiffersFromTargetAttribute() {
        // given: @ComposableColumnWidthRenamed uses width() → @AliasFor(attribute="value"),
        //        so the method name "width" differs from the target attribute "value"
        Field field = getField("renamedAliasField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        // AliasFor should synthesize: ComposableColumnWidthRenamed.width(18) → ColumnWidth.value(18)
        AnnotationAttributes attrs = result.getAttributes(ColumnWidth.class);
        Integer actualValue = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(18, actualValue);
    }

    @Test
    void shouldContainComposableAnnotation_withRenamedAliasInAnnotationMap() {
        // given
        Field field = getField("renamedAliasField");

        // when
        AnnotationMap result = reader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ComposableColumnWidthRenamed.class));
        AnnotationAttributes attrs = result.getAttributes(ComposableColumnWidthRenamed.class);
        Integer actualWidth = Assertions.assertDoesNotThrow(() -> attrs.getRequiredAttribute("width", Integer.class));
        Assertions.assertEquals(18, actualWidth);
    }

    @Test
    void shouldResolveRenamedAlias_whenMetaMarkedEnabled() {
        // given
        AnnotationMetadataReader enabledReader = new AnnotationMetadataReader(Boolean.TRUE);
        Field field = getField("renamedAliasField");

        // when
        AnnotationMap result = enabledReader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasAnnotation(ComposableColumnWidthRenamed.class));
        Assertions.assertTrue(result.hasAnnotation(ColumnWidth.class));
        AnnotationAttributes columnWidthAttrs = result.getAttributes(ColumnWidth.class);
        Integer value =
                Assertions.assertDoesNotThrow(() -> columnWidthAttrs.getRequiredAttribute("value", Integer.class));
        Assertions.assertEquals(18, value);
    }

    @Test
    void shouldNotResolveRenamedAlias_whenMetaMarkedDisabled() {
        // given
        AnnotationMetadataReader disabledReader = new AnnotationMetadataReader(Boolean.FALSE);
        Field field = getField("renamedAliasField");

        // when
        AnnotationMap result = disabledReader.read(field);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.hasAnnotation(ComposableColumnWidthRenamed.class));
        Assertions.assertFalse(result.hasAnnotation(ColumnWidth.class));
    }

    @Test
    void shouldPopulateCustomAttribute_inAliasFor_whenMethodNameDiffersFromTarget() {
        // given: resolve the annotation directly via DefaultAnnotationMetadataResolver
        //        to inspect the AliasFor value object
        DefaultAnnotationMetadataResolver resolver = new DefaultAnnotationMetadataResolver();
        ComposableColumnWidthRenamed ann =
                getField("renamedAliasField").getAnnotation(ComposableColumnWidthRenamed.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        List<AliasFor> aliases = metadata.getAliases();
        Assertions.assertEquals(1, aliases.size());

        AliasFor alias = aliases.get(0);
        Assertions.assertEquals(ComposableColumnWidthRenamed.class, alias.getMarked());
        Assertions.assertEquals(ColumnWidth.class, alias.getTarget());
        // customAttribute is the source method name "width" (different from target "value")
        Assertions.assertEquals("width", alias.getCustomAttribute());
        // attribute is the target attribute name "value"
        Assertions.assertEquals("value", alias.getAttribute());
        Assertions.assertEquals(18, alias.getValue());
    }

    @Test
    void shouldSetCustomAttributeEqualToAttribute_whenMethodNameMatchesTarget() {
        // given: the existing ComposableColumnWidth uses value() → @AliasFor(attribute="value"),
        //        same-name case, so customAttribute should equal attribute
        DefaultAnnotationMetadataResolver resolver = new DefaultAnnotationMetadataResolver();
        ComposableColumnWidth ann = getField("composableField").getAnnotation(ComposableColumnWidth.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        List<AliasFor> aliases = metadata.getAliases();
        Assertions.assertEquals(1, aliases.size());

        AliasFor alias = aliases.get(0);
        // Same-name case: customAttribute == attribute == "value"
        Assertions.assertEquals("value", alias.getCustomAttribute());
        Assertions.assertEquals("value", alias.getAttribute());
        Assertions.assertEquals(15, alias.getValue());
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

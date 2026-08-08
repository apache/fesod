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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Native;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationMetadataResolver}, {@link DefaultAnnotationMetadataResolver}
 */
@Tag(Tags.UNIT)
class AnnotationMetadataResolverTest {

    // ---- Test annotation definitions ----

    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface PlainAnnotation {
        String name() default "";

        int value() default 0;
    }

    @FesodMarked
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface MarkedAnnotation {
        String name() default "";
    }

    @FesodMarked
    @ColumnWidth(25)
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasAnnotation {
        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int width() default 25;
    }

    @FesodMarked
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface BadAliasAnnotation {
        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int width() default 20;
    }

    // ---- Annotated fields for retrieving annotation instances ----

    @PlainAnnotation(name = "test", value = 42)
    static String plainField;

    @MarkedAnnotation(name = "marked")
    static String markedField;

    @MarkedAnnotation
    static String defaultMarkedField;

    @AliasAnnotation(width = 30)
    static String aliasField;

    @BadAliasAnnotation(width = 15)
    static String badAliasField;

    @HeadStyle
    static String headStyleField;

    private DefaultAnnotationMetadataResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultAnnotationMetadataResolver();
    }

    private <A extends Annotation> A getFieldAnnotation(String fieldName, Class<A> type) {
        try {
            Field field = AnnotationMetadataResolverTest.class.getDeclaredField(fieldName);
            return field.getAnnotation(type);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- shouldIgnore tests ----

    @Test
    void shouldIgnore_javaLangMetaAnnotations() {
        // when / then
        Assertions.assertTrue(resolver.shouldIgnore(Target.class));
        Assertions.assertTrue(resolver.shouldIgnore(Retention.class));
        Assertions.assertTrue(resolver.shouldIgnore(Documented.class));
        Assertions.assertTrue(resolver.shouldIgnore(Repeatable.class));
        Assertions.assertTrue(resolver.shouldIgnore(Native.class));
        Assertions.assertTrue(resolver.shouldIgnore(Inherited.class));
    }

    @Test
    void shouldNotIgnore_innerAnnotations() {
        // when / then
        Assertions.assertFalse(resolver.shouldIgnore((ExcelProperty.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ExcelIgnoreUnannotated.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ExcelIgnore.class)));
        Assertions.assertFalse(resolver.shouldIgnore((DateTimeFormat.class)));
        Assertions.assertFalse(resolver.shouldIgnore((NumberFormat.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ColumnWidth.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ContentFontStyle.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ContentLoopMerge.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ContentRowHeight.class)));
        Assertions.assertFalse(resolver.shouldIgnore((ContentStyle.class)));
        Assertions.assertFalse(resolver.shouldIgnore((HeadFontStyle.class)));
        Assertions.assertFalse(resolver.shouldIgnore((HeadRowHeight.class)));
        Assertions.assertFalse(resolver.shouldIgnore((HeadStyle.class)));
        Assertions.assertFalse(resolver.shouldIgnore((OnceAbsoluteMerge.class)));
    }

    @Test
    void shouldNotIgnore_customAnnotations() {
        // when / then
        Assertions.assertFalse(resolver.shouldIgnore(PlainAnnotation.class));
        Assertions.assertFalse(resolver.shouldIgnore(MarkedAnnotation.class));
    }

    // ---- isInnerAnnotated tests ----

    @Test
    void shouldBeInnerAnnotated_forColumnWidth() {
        // given
        ColumnWidth cw = AliasAnnotation.class.getAnnotation(ColumnWidth.class);

        // when / then
        Assertions.assertTrue(resolver.isInnerAnnotated(cw));
    }

    @Test
    void shouldBeInnerAnnotated_forHeadStyle() {
        // given
        HeadStyle hs = getFieldAnnotation("headStyleField", HeadStyle.class);

        // when / then
        Assertions.assertTrue(resolver.isInnerAnnotated(hs));
    }

    @Test
    void shouldNotBeInnerAnnotated_forPlainAnnotation() {
        // given
        PlainAnnotation ann = getFieldAnnotation("plainField", PlainAnnotation.class);

        // when / then
        Assertions.assertFalse(resolver.isInnerAnnotated(ann));
    }

    @Test
    void shouldNotBeInnerAnnotated_forJavaLangAnnotation() {
        // given
        Target target = PlainAnnotation.class.getAnnotation(Target.class);

        // when / then
        Assertions.assertFalse(resolver.isInnerAnnotated(target));
    }

    // ---- isMetaMarked tests ----

    @Test
    void shouldBeMetaMarked_forFesodMarkedAnnotation() {
        // given
        MarkedAnnotation ann = getFieldAnnotation("markedField", MarkedAnnotation.class);

        // when / then
        Assertions.assertTrue(resolver.isMetaMarked(ann));
    }

    @Test
    void shouldNotBeMetaMarked_forPlainAnnotation() {
        // given
        PlainAnnotation ann = getFieldAnnotation("plainField", PlainAnnotation.class);

        // when / then
        Assertions.assertFalse(resolver.isMetaMarked(ann));
    }

    @Test
    void shouldNotBeMetaMarked_forInnerAnnotation() {
        // given
        ColumnWidth cw = AliasAnnotation.class.getAnnotation(ColumnWidth.class);

        // when / then
        Assertions.assertFalse(resolver.isMetaMarked(cw));
    }

    // ---- resolve tests ----

    @Test
    void shouldResolveAttributes_forPlainAnnotation() {
        // given
        PlainAnnotation ann = getFieldAnnotation("plainField", PlainAnnotation.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        Assertions.assertEquals(PlainAnnotation.class, metadata.getAttributes().getAnnotationType());
        Assertions.assertEquals("test", metadata.getAttributes().get("name"));
        Assertions.assertEquals(42, metadata.getAttributes().get("value"));
        Assertions.assertTrue(metadata.getAliases().isEmpty());
    }

    @Test
    void shouldResolveAttributes_forMarkedAnnotationWithoutAlias() {
        // given
        MarkedAnnotation ann = getFieldAnnotation("markedField", MarkedAnnotation.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        Assertions.assertEquals(MarkedAnnotation.class, metadata.getAttributes().getAnnotationType());
        Assertions.assertEquals("marked", metadata.getAttributes().get("name"));
        Assertions.assertTrue(metadata.getAliases().isEmpty());
    }

    @Test
    void shouldResolveDefaultValues_whenNotExplicitlySet() {
        // given
        MarkedAnnotation ann = getFieldAnnotation("defaultMarkedField", MarkedAnnotation.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        Assertions.assertEquals("", metadata.getAttributes().get("name"));
        Assertions.assertTrue(metadata.getAliases().isEmpty());
    }

    @Test
    void shouldResolveAlias_forAliasAnnotation() {
        // given
        AliasAnnotation ann = getFieldAnnotation("aliasField", AliasAnnotation.class);

        // when
        AnnotationMetadata metadata = resolver.resolve(ann);

        // then
        Assertions.assertEquals(AliasAnnotation.class, metadata.getAttributes().getAnnotationType());
        Assertions.assertEquals(30, metadata.getAttributes().get("width"));
        Assertions.assertEquals(1, metadata.getAliases().size());

        AliasFor alias = metadata.getAliases().get(0);
        Assertions.assertEquals(AliasAnnotation.class, alias.getMarked());
        Assertions.assertEquals(ColumnWidth.class, alias.getTarget());
        Assertions.assertEquals("value", alias.getAttribute());
        Assertions.assertEquals(30, alias.getValue());
    }

    @Test
    void shouldThrow_whenAliasTargetNotPresentOnMarkedAnnotation() {
        // given
        BadAliasAnnotation ann = getFieldAnnotation("badAliasField", BadAliasAnnotation.class);

        // when / then
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () -> resolver.resolve(ann));
        Assertions.assertTrue(ex.getMessage().contains("ColumnWidth"));
        Assertions.assertTrue(ex.getMessage().contains("BadAliasAnnotation"));
    }
}

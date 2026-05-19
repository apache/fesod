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

package org.apache.fesod.sheet.annotation.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import lombok.Data;
import org.apache.fesod.sheet.annotation.AnnotatedFieldDescriptor;
import org.apache.fesod.sheet.annotation.AnnotatedTypeDescriptor;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.AnnotationMap;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.FesodMarked;
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
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.property.ExcelHeadProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for composable-annotation initialization analysis.
 * <p />
 * Covered inner annotations:
 * <ul>
 *   <li>{@link ExcelProperty}</li>
 *   <li>{@link DateTimeFormat}</li>
 *   <li>{@link NumberFormat}</li>
 *   <li>{@link ColumnWidth}</li>
 *   <li>{@link HeadStyle}</li>
 *   <li>{@link HeadFontStyle}</li>
 *   <li>{@link ContentStyle}</li>
 *   <li>{@link ContentFontStyle}</li>
 *   <li>{@link ContentLoopMerge}</li>
 *   <li>{@link HeadRowHeight}</li>
 *   <li>{@link ContentRowHeight}</li>
 *   <li>{@link OnceAbsoluteMerge}</li>
 * </ul>
 * <p />
 * Covered test scenarios:
 * <ul>
 *   <li><b>Field-level composable</b> — partial {@code @AliasFor}, full {@code @AliasFor},
 *       no-methods grouping, priority when direct and composable coexist</li>
 *   <li><b>Class-level composable</b> — {@code @AliasFor} with value propagation,
 *       no-methods grouping (single and multi-annotation presets)</li>
 *   <li><b>Mixed-level composable</b> — class + field composable simultaneously,
 *       multiple fields with independent composable annotations per field</li>
 *   <li><b>Error cases</b> — {@code @FesodMarked} with invalid {@code @AliasFor} target,
 *       mixed valid/invalid {@code @AliasFor} targets on the same composable annotation</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CompositeAnnotationTest {

    @Mock
    private ConfigurationHolder configurationHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @BeforeEach
    void setup() {
        Mockito.lenient().when(configurationHolder.globalConfiguration()).thenReturn(globalConfiguration);
        Mockito.lenient().when(globalConfiguration.getEnableMetaMarked()).thenReturn(true);
        Mockito.lenient().when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);
    }

    // ---- Custom composable annotations ----

    /**
     * A composable annotation with {@code @FesodMarked} but missing the target meta-annotation
     * that the {@code @AliasFor} points to. Used to verify error handling.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @Inherited
    public @interface CustomExcelProperty1 {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String[] value() default {"Default"};
    }

    /**
     * A composable annotation with a single {@code @AliasFor} for {@code @ExcelProperty.value}.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @Inherited
    public @interface ComposableExcelProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String[] value() default {""};
    }

    /**
     * A composable annotation where ALL attributes of {@code @ExcelProperty} are aliased.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @Inherited
    public @interface FullyComposableExcelProperty {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String[] value() default {""};

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "index")
        int index() default -1;

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "order")
        int order() default Integer.MAX_VALUE;
    }

    /**
     * A composable annotation for ColumnWidth usable on both TYPE and FIELD.
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ColumnWidth
    @Inherited
    public @interface ComposableColumnWidth {

        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int value() default -1;
    }

    /**
     * A composable annotation with no methods — groups {@code @ColumnWidth} and {@code @HeadStyle}
     * as a class-level style preset.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ColumnWidth(value = 10)
    @HeadStyle(fillForegroundColor = 10)
    @Inherited
    public @interface ComposableAnnotationWithCommonStyle {}

    /**
     * A composable annotation with no methods, meta-annotated with {@code @ExcelProperty}.
     * Used to verify that when both the original annotation and the composable annotation
     * coexist at the same level, the original annotation has higher priority.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(value = {"Full Name"})
    @Inherited
    public @interface ComposableExcelPropertyPreset {}

    /**
     * Composable annotation with {@code @AliasFor} for {@code @NumberFormat.value}.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @NumberFormat
    @Inherited
    public @interface ComposableNumberFormat {

        @FesodMarked.AliasFor(annotation = NumberFormat.class, attribute = "value")
        String value() default "";
    }

    /**
     * Composable annotation with no methods — groups {@code @ContentStyle} and {@code @ContentFontStyle}
     * as a content style preset.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentStyle(wrapped = BooleanEnum.TRUE, fillForegroundColor = 10)
    @ContentFontStyle(fontName = "Arial", fontHeightInPoints = 12, bold = BooleanEnum.TRUE)
    @Inherited
    public @interface ComposableContentStylePreset {}

    /**
     * Composable annotation with no methods — groups {@code @HeadRowHeight}, {@code @ContentRowHeight},
     * and {@code @OnceAbsoluteMerge} as a table style preset for class-level use.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadRowHeight(30)
    @ContentRowHeight(20)
    @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 0, firstColumnIndex = 0, lastColumnIndex = 3)
    @Inherited
    public @interface ComposableTableStylePreset {}

    /**
     * Composable annotation with {@code @AliasFor} for {@code @DateTimeFormat.value}.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @DateTimeFormat
    @Inherited
    public @interface ComposableDateTimeFormat {

        @FesodMarked.AliasFor(annotation = DateTimeFormat.class, attribute = "value")
        String value() default "";
    }

    /**
     * Composable annotation with {@code @AliasFor} for both attributes of {@code @ContentLoopMerge}.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentLoopMerge
    @Inherited
    public @interface ComposableContentLoopMerge {

        @FesodMarked.AliasFor(annotation = ContentLoopMerge.class, attribute = "eachRow")
        int eachRow() default 1;

        @FesodMarked.AliasFor(annotation = ContentLoopMerge.class, attribute = "columnExtend")
        int columnExtend() default 1;
    }

    /**
     * Composable annotation with no methods — groups {@code @HeadStyle} and {@code @HeadFontStyle}
     * as a header style preset for class-level use.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadStyle(fillForegroundColor = 10)
    @HeadFontStyle(fontName = "Calibri", fontHeightInPoints = 14, bold = BooleanEnum.TRUE)
    @Inherited
    public @interface ComposableHeaderStylePreset {}

    // ---- Model classes ----

    @Data
    static class ExcelModelAliasError {

        @CustomExcelProperty1
        private String str1;
    }

    /**
     * A composable annotation with mixed valid/invalid @AliasFor targets:
     * valid — ExcelProperty.value (ExcelProperty IS a meta-annotation)
     * invalid — ColumnWidth.value (ColumnWidth is NOT a meta-annotation)
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @Inherited
    public @interface CustomExcelPropertyMixedAlias {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String[] value() default {""};

        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int width() default -1;
    }

    @Data
    static class ExcelModelMixedAliasError {

        @CustomExcelPropertyMixedAlias
        private String str1;
    }

    static class ExcelModelWithComposableField {

        @ComposableExcelProperty({"Custom Name"})
        private String name;
    }

    static class ExcelModelWithFullyComposableField {

        @FullyComposableExcelProperty(
                value = {"Full Name", "Common Config"},
                index = 2,
                order = 100)
        private String name;
    }

    @ComposableColumnWidth(25)
    static class ExcelModelWithComposableClassAnnotation {

        @ExcelProperty("Name")
        private String name;
    }

    @ComposableAnnotationWithCommonStyle
    static class ExcelModelWithComposableGroupAnnotation {

        @ExcelProperty("Name")
        private String name;
    }

    static class ExcelModelWithPriorityConflict {

        @ExcelProperty(value = {"First Name"})
        @ComposableExcelPropertyPreset
        private String name;
    }

    static class ExcelModelWithComposableNumberFormat {

        @ComposableNumberFormat("#,##0.00")
        private String amount;
    }

    static class ExcelModelWithComposableContentStyle {

        @ComposableContentStylePreset
        @ExcelProperty("Data")
        private String data;
    }

    @ComposableTableStylePreset
    static class ExcelModelWithComposableTableStyle {

        @ExcelProperty("Name")
        private String name;
    }

    @ComposableTableStylePreset
    static class ExcelModelMixedClassAndFieldComposable {

        @ComposableExcelProperty({"Mixed Name"})
        private String name;
    }

    @ComposableAnnotationWithCommonStyle
    static class ExcelModelMixedBothNoMethods {

        @ComposableContentStylePreset
        @ExcelProperty("Data")
        private String data;
    }

    @ComposableColumnWidth(50)
    static class ExcelModelMixedAliasForBothLevels {

        @ComposableNumberFormat("0.00%")
        private String ratio;
    }

    @ComposableTableStylePreset
    static class ExcelModelMixedMultipleFields {

        @ComposableExcelProperty({"Name"})
        private String name;

        @ComposableNumberFormat("#,##0.00")
        private String amount;
    }

    static class ExcelModelWithComposableDateTimeFormat {

        @ComposableDateTimeFormat("yyyy-MM-dd HH:mm")
        private String date;
    }

    static class ExcelModelWithComposableContentLoopMerge {

        @ComposableContentLoopMerge(eachRow = 3, columnExtend = 2)
        private String value;
    }

    @ComposableHeaderStylePreset
    static class ExcelModelWithComposableHeaderStyle {

        @ExcelProperty("Name")
        private String name;
    }

    @ComposableHeaderStylePreset
    static class ExcelModelMixedHeaderStyleAndDateFormat {

        @ComposableDateTimeFormat("yyyy-MM-dd")
        private String date;
    }

    // ---- Tests ----

    @Nested
    class FieldLevelCompositeAnnotationTest {

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenPartialAliasFor() {
            // given - ExcelModelWithComposableField has @ComposableExcelProperty({"Custom Name"})
            //         which only aliases "value" attribute of @ExcelProperty

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableField.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            Assertions.assertNotNull(head);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertFalse(fieldAnnotationMap.isEmpty());
            Assertions.assertEquals(2, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableExcelProperty.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));

            String[] expectedValue = {"Custom Name"};

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, customAttrs.getRequiredAttribute("value", String[].class));

            AnnotationAttributes targetAttrs = fieldAnnotationMap.getAttributes(ExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, targetAttrs.getRequiredAttribute("value", String[].class));
        }

        @Test
        void shouldIncludeAllAliasedValuesInComposable_whenAllParamsAliasFor() {
            // given - ExcelModelWithFullyComposableField has @FullyComposableExcelProperty
            //         which aliases ALL attributes (value, index, order) of @ExcelProperty

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithFullyComposableField.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(2);
            Assertions.assertNotNull(head);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertFalse(fieldAnnotationMap.isEmpty());
            Assertions.assertEquals(2, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(FullyComposableExcelProperty.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));

            String[] expectedValue = {"Full Name", "Common Config"};
            int expectedIndex = 2;
            int expectedOrder = 100;

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(FullyComposableExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, customAttrs.getRequiredAttribute("value", String[].class));
            Assertions.assertEquals(expectedIndex, customAttrs.getRequiredAttribute("index", Integer.class));
            Assertions.assertEquals(expectedOrder, customAttrs.getRequiredAttribute("order", Integer.class));

            AnnotationAttributes targetAttrs = fieldAnnotationMap.getAttributes(ExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, targetAttrs.getRequiredAttribute("value", String[].class));
            Assertions.assertEquals(expectedIndex, targetAttrs.getRequiredAttribute("index", Integer.class));
            Assertions.assertEquals(expectedOrder, targetAttrs.getRequiredAttribute("order", Integer.class));
        }

        @Test
        void shouldPreserveDirectAnnotationValue_whenOriginalAndComposableAtSameLevel() {
            // given - field has both @ExcelProperty({"First Name"}) directly and
            //         @ComposableExcelPropertyPreset (which meta-annotates @ExcelProperty({"Full Name"}))
            //         At the same level, the direct annotation has higher priority

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithPriorityConflict.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            Assertions.assertNotNull(head);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertFalse(fieldAnnotationMap.isEmpty());
            Assertions.assertEquals(2, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableExcelPropertyPreset.class));

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableExcelPropertyPreset.class);
            Assertions.assertNotNull(customAttrs);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes attrs = fieldAnnotationMap.getAttributes(ExcelProperty.class);
            Assertions.assertArrayEquals(
                    new String[] {"First Name"}, attrs.getRequiredAttribute("value", String[].class));
        }

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenNumberFormatWithAliasFor() {
            // given - ExcelModelWithComposableNumberFormat has @ComposableNumberFormat("#,##0.00")
            //         which aliases "value" attribute of @NumberFormat

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertEquals(2, annotationMap.size());
            Assertions.assertTrue(annotationMap.hasAnnotation(ComposableNumberFormat.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(NumberFormat.class));

            AnnotationAttributes customAttrs = annotationMap.getAttributes(ComposableNumberFormat.class);
            Assertions.assertEquals("#,##0.00", customAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes targetAttrs = annotationMap.getAttributes(NumberFormat.class);
            Assertions.assertEquals("#,##0.00", targetAttrs.getRequiredAttribute("value", String.class));
            Assertions.assertEquals(
                    RoundingMode.HALF_UP, targetAttrs.getRequiredAttribute("roundingMode", RoundingMode.class));
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenContentStylePresetNoMethods() {
            // given - ExcelModelWithComposableContentStyle has @ComposableContentStylePreset
            //         which groups @ContentStyle and @ContentFontStyle with no methods

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableContentStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertEquals(4, annotationMap.size());
            Assertions.assertTrue(annotationMap.hasAnnotation(ComposableContentStylePreset.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentStyle.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentFontStyle.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(ExcelProperty.class));

            AnnotationAttributes customAttrs = annotationMap.getAttributes(ComposableContentStylePreset.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes styleAttrs = annotationMap.getAttributes(ContentStyle.class);
            Assertions.assertEquals(BooleanEnum.TRUE, styleAttrs.getRequiredAttribute("wrapped", BooleanEnum.class));
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));

            AnnotationAttributes fontAttrs = annotationMap.getAttributes(ContentFontStyle.class);
            Assertions.assertEquals("Arial", fontAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 12, fontAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, fontAttrs.getRequiredAttribute("bold", BooleanEnum.class));
        }

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenDateTimeFormatWithAliasFor() {
            // given - ExcelModelWithComposableDateTimeFormat has @ComposableDateTimeFormat("yyyy-MM-dd HH:mm")
            //         which aliases "value" attribute of @DateTimeFormat

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableDateTimeFormat.class, null);

            // then
            Assertions.assertNotNull(property.getHeadMap());
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertEquals(2, annotationMap.size());
            Assertions.assertTrue(annotationMap.hasAnnotation(ComposableDateTimeFormat.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(DateTimeFormat.class));

            AnnotationAttributes customAttrs = annotationMap.getAttributes(ComposableDateTimeFormat.class);
            Assertions.assertEquals("yyyy-MM-dd HH:mm", customAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes targetAttrs = annotationMap.getAttributes(DateTimeFormat.class);
            Assertions.assertEquals("yyyy-MM-dd HH:mm", targetAttrs.getRequiredAttribute("value", String.class));
        }

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenContentLoopMergeWithAliasFor() {
            // given - ExcelModelWithComposableContentLoopMerge has @ComposableContentLoopMerge(eachRow=3,
            // columnExtend=2)
            //         which aliases both attributes of @ContentLoopMerge

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableContentLoopMerge.class, null);

            // then
            Assertions.assertNotNull(property.getHeadMap());
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertEquals(2, annotationMap.size());
            Assertions.assertTrue(annotationMap.hasAnnotation(ComposableContentLoopMerge.class));
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentLoopMerge.class));

            AnnotationAttributes customAttrs = annotationMap.getAttributes(ComposableContentLoopMerge.class);
            Assertions.assertEquals(3, customAttrs.getRequiredAttribute("eachRow", Integer.class));
            Assertions.assertEquals(2, customAttrs.getRequiredAttribute("columnExtend", Integer.class));

            AnnotationAttributes targetAttrs = annotationMap.getAttributes(ContentLoopMerge.class);
            Assertions.assertEquals(3, targetAttrs.getRequiredAttribute("eachRow", Integer.class));
            Assertions.assertEquals(2, targetAttrs.getRequiredAttribute("columnExtend", Integer.class));
        }

        @Test
        void shouldPopulateFieldDescriptor_withCorrectFieldNameAndElement() {
            // given - ExcelModelWithComposableField has @ComposableExcelProperty({"Custom Name"})
            //         on the "name" field

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableField.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);
            Assertions.assertEquals("name", fieldDescriptor.getFieldName());
            Assertions.assertNotNull(fieldDescriptor.getAnnotatedElement());
            Assertions.assertEquals(
                    "name", fieldDescriptor.getAnnotatedElement().getName());
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughFieldDescriptor() {
            // given - ExcelModelWithComposableContentStyle has @ComposableContentStylePreset + @ExcelProperty("Data")
            //         on the "data" field

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableContentStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);
            Assertions.assertEquals(4, fieldDescriptor.getAnnotationCount());
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ComposableContentStylePreset.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ContentStyle.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ContentFontStyle.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ExcelProperty.class));
            Assertions.assertFalse(fieldDescriptor.hasAnnotation(ColumnWidth.class));
        }

        @Test
        void shouldDelegateGetAnnotation_throughFieldDescriptor() {
            // given - ExcelModelWithComposableNumberFormat has @ComposableNumberFormat("#,##0.00")
            //         on the "amount" field

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);

            AnnotationAttributes numberAttrs = fieldDescriptor.getAnnotation(NumberFormat.class);
            Assertions.assertNotNull(numberAttrs);
            Assertions.assertEquals("#,##0.00", numberAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes missingAttrs = fieldDescriptor.getAnnotation(ColumnWidth.class);
            Assertions.assertNull(missingAttrs);
        }
    }

    @Nested
    class ClassLevelCompositeAnnotationTest {

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenAliasForPresent() {
            // given - ExcelModelWithComposableClassAnnotation has @ComposableColumnWidth(25)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableClassAnnotation.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertFalse(classAnnotationMap.isEmpty());
            Assertions.assertEquals(2, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));

            AnnotationAttributes customAttrs = classAnnotationMap.getAttributes(ComposableColumnWidth.class);
            Assertions.assertEquals(25, customAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes targetAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(25, targetAttrs.getRequiredAttribute("value", Integer.class));
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenNoMethodsInComposable() {
            // given - ExcelModelWithComposableGroupAnnotation has @ComposableAnnotationWithCommonStyle
            //         which has no methods, but meta-annotates @ColumnWidth(10) and @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableGroupAnnotation.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertFalse(classAnnotationMap.isEmpty());
            Assertions.assertEquals(3, classAnnotationMap.size());

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableAnnotationWithCommonStyle.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));

            AnnotationAttributes customAttrs =
                    classAnnotationMap.getAttributes(ComposableAnnotationWithCommonStyle.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes widthAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(10, widthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes styleAttrs = classAnnotationMap.getAttributes(HeadStyle.class);
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenTableStylePresetNoMethods() {
            // given - ExcelModelWithComposableTableStyle has @ComposableTableStylePreset
            //         which groups @HeadRowHeight(30), @ContentRowHeight(20), @OnceAbsoluteMerge(...)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableTableStyle.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(4, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableTableStylePreset.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(OnceAbsoluteMerge.class));

            AnnotationAttributes customAttrs = classAnnotationMap.getAttributes(ComposableTableStylePreset.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes headHeightAttrs = classAnnotationMap.getAttributes(HeadRowHeight.class);
            Assertions.assertEquals((short) 30, headHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes contentHeightAttrs = classAnnotationMap.getAttributes(ContentRowHeight.class);
            Assertions.assertEquals((short) 20, contentHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes mergeAttrs = classAnnotationMap.getAttributes(OnceAbsoluteMerge.class);
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("lastRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstColumnIndex", Integer.class));
            Assertions.assertEquals(3, mergeAttrs.getRequiredAttribute("lastColumnIndex", Integer.class));
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenHeaderStylePresetNoMethods() {
            // given - ExcelModelWithComposableHeaderStyle has @ComposableHeaderStylePreset
            //         which groups @HeadStyle(fillForegroundColor=10) and @HeadFontStyle(fontName="Calibri",
            // fontHeightInPoints=14, bold=TRUE)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableHeaderStyle.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(3, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableHeaderStylePreset.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadFontStyle.class));

            AnnotationAttributes customAttrs = classAnnotationMap.getAttributes(ComposableHeaderStylePreset.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes styleAttrs = classAnnotationMap.getAttributes(HeadStyle.class);
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));

            AnnotationAttributes fontAttrs = classAnnotationMap.getAttributes(HeadFontStyle.class);
            Assertions.assertEquals("Calibri", fontAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 14, fontAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, fontAttrs.getRequiredAttribute("bold", BooleanEnum.class));
        }

        @Test
        void shouldPopulateTypeDescriptor_withCorrectAnnotatedElement() {
            // given - ExcelModelWithComposableClassAnnotation has @ComposableColumnWidth(25)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableClassAnnotation.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertSame(ExcelModelWithComposableClassAnnotation.class, typeDescriptor.getAnnotatedElement());
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughTypeDescriptor() {
            // given - ExcelModelWithComposableGroupAnnotation has @ComposableAnnotationWithCommonStyle
            //         which groups @ColumnWidth(10) and @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableGroupAnnotation.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertEquals(3, typeDescriptor.getAnnotationCount());
            Assertions.assertTrue(typeDescriptor.hasAnnotation(ComposableAnnotationWithCommonStyle.class));
            Assertions.assertTrue(typeDescriptor.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(typeDescriptor.hasAnnotation(HeadStyle.class));
            Assertions.assertFalse(typeDescriptor.hasAnnotation(ContentRowHeight.class));
        }

        @Test
        void shouldDelegateGetAnnotation_throughTypeDescriptor() {
            // given - ExcelModelWithComposableClassAnnotation has @ComposableColumnWidth(25)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableClassAnnotation.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);

            AnnotationAttributes widthAttrs = typeDescriptor.getAnnotation(ColumnWidth.class);
            Assertions.assertNotNull(widthAttrs);
            Assertions.assertEquals(25, widthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes missingAttrs = typeDescriptor.getAnnotation(HeadStyle.class);
            Assertions.assertNull(missingAttrs);
        }
    }

    @Nested
    class MixedLevelCompositeAnnotationTest {

        @Test
        void shouldPopulateBothLevels_whenClassComposableGroupAndFieldComposableAliasFor() {
            // given - class has @ComposableTableStylePreset (groups HeadRowHeight, ContentRowHeight, OnceAbsoluteMerge)
            //         field has @ComposableExcelProperty({"Mixed Name"}) (aliases ExcelProperty.value)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedClassAndFieldComposable.class, null);

            // then - class-level annotationMap
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(4, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableTableStylePreset.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(OnceAbsoluteMerge.class));

            AnnotationAttributes customTypeAttrs = classAnnotationMap.getAttributes(ComposableTableStylePreset.class);
            Assertions.assertTrue(customTypeAttrs.isEmpty());

            AnnotationAttributes headHeightAttrs = classAnnotationMap.getAttributes(HeadRowHeight.class);
            Assertions.assertEquals((short) 30, headHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes contentHeightAttrs = classAnnotationMap.getAttributes(ContentRowHeight.class);
            Assertions.assertEquals((short) 20, contentHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes mergeAttrs = classAnnotationMap.getAttributes(OnceAbsoluteMerge.class);
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("lastRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstColumnIndex", Integer.class));
            Assertions.assertEquals(3, mergeAttrs.getRequiredAttribute("lastColumnIndex", Integer.class));

            // then - field-level annotationMap
            Assertions.assertNotNull(property.getHeadMap());
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertEquals(2, fieldAnnotationMap.size());
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableExcelProperty.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));

            String[] expectedValue = {"Mixed Name"};

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, customAttrs.getRequiredAttribute("value", String[].class));

            AnnotationAttributes targetAttrs = fieldAnnotationMap.getAttributes(ComposableExcelProperty.class);
            Assertions.assertArrayEquals(expectedValue, targetAttrs.getRequiredAttribute("value", String[].class));
        }

        @Test
        void shouldPopulateBothLevels_whenClassAndFieldBothUseNoMethodsComposable() {
            // given - class has @ComposableAnnotationWithCommonStyle (groups ColumnWidth, HeadStyle)
            //         field has @ComposableContentStylePreset (groups ContentStyle, ContentFontStyle)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedBothNoMethods.class, null);

            // then - class-level annotationMap
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(3, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableAnnotationWithCommonStyle.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));

            AnnotationAttributes customTypeAttrs =
                    classAnnotationMap.getAttributes(ComposableAnnotationWithCommonStyle.class);
            Assertions.assertTrue(customTypeAttrs.isEmpty());

            AnnotationAttributes widthAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(10, widthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes styleTypeAttrs = classAnnotationMap.getAttributes(HeadStyle.class);
            Assertions.assertEquals(
                    (short) 10, styleTypeAttrs.getRequiredAttribute("fillForegroundColor", Short.class));

            // then - field-level annotationMap
            Assertions.assertNotNull(property.getHeadMap());
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertEquals(4, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableContentStylePreset.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ContentStyle.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ContentFontStyle.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableContentStylePreset.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes styleAttrs = fieldAnnotationMap.getAttributes(ContentStyle.class);
            Assertions.assertEquals(BooleanEnum.TRUE, styleAttrs.getRequiredAttribute("wrapped", BooleanEnum.class));
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));

            AnnotationAttributes fontAttrs = fieldAnnotationMap.getAttributes(ContentFontStyle.class);
            Assertions.assertEquals("Arial", fontAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 12, fontAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, fontAttrs.getRequiredAttribute("bold", BooleanEnum.class));
        }

        @Test
        void shouldPopulateBothLevels_whenClassAndFieldBothUseAliasForComposable() {
            // given - class has @ComposableColumnWidth(50) (aliases ColumnWidth.value)
            //         field has @ComposableNumberFormat("0.00%") (aliases NumberFormat.value)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedAliasForBothLevels.class, null);

            // then - class-level annotationMap
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(2, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));

            AnnotationAttributes customWidthAttrs = classAnnotationMap.getAttributes(ComposableColumnWidth.class);
            Assertions.assertEquals(50, customWidthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes targetTypeAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(50, targetTypeAttrs.getRequiredAttribute("value", Integer.class));

            // then - field-level annotationMap
            Head head = property.getHeadMap().get(0);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertEquals(2, fieldAnnotationMap.size());
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableNumberFormat.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(NumberFormat.class));

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableNumberFormat.class);
            Assertions.assertEquals("0.00%", customAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes targetAttrs = fieldAnnotationMap.getAttributes(NumberFormat.class);
            Assertions.assertEquals("0.00%", targetAttrs.getRequiredAttribute("value", String.class));
            Assertions.assertEquals(
                    RoundingMode.HALF_UP, targetAttrs.getRequiredAttribute("roundingMode", RoundingMode.class));
        }

        @Test
        void shouldPopulateEachFieldIndependently_whenClassComposableAndMultipleFieldsWithComposable() {
            // given - class has @ComposableTableStylePreset
            //         field 0 has @ComposableExcelProperty({"Name"})
            //         field 1 has @ComposableNumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedMultipleFields.class, null);

            // then - class-level annotationMap is shared
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(4, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableTableStylePreset.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentRowHeight.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(OnceAbsoluteMerge.class));

            AnnotationAttributes customAttrs = classAnnotationMap.getAttributes(ComposableTableStylePreset.class);
            Assertions.assertTrue(customAttrs.isEmpty());

            AnnotationAttributes headHeightAttrs = classAnnotationMap.getAttributes(HeadRowHeight.class);
            Assertions.assertEquals((short) 30, headHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes contentHeightAttrs = classAnnotationMap.getAttributes(ContentRowHeight.class);
            Assertions.assertEquals((short) 20, contentHeightAttrs.getRequiredAttribute("value", Short.class));

            AnnotationAttributes mergeAttrs = classAnnotationMap.getAttributes(OnceAbsoluteMerge.class);
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("lastRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstColumnIndex", Integer.class));
            Assertions.assertEquals(3, mergeAttrs.getRequiredAttribute("lastColumnIndex", Integer.class));

            // then - each field has its own independent annotationMap
            Assertions.assertNotNull(property.getHeadMap());
            Assertions.assertEquals(2, property.getHeadMap().size());

            Head nameHead = property.getHeadMap().get(0);
            AnnotationMap nameAnnotationMap = nameHead.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(nameAnnotationMap);
            Assertions.assertTrue(nameAnnotationMap.hasAnnotation(ComposableExcelProperty.class));
            Assertions.assertTrue(nameAnnotationMap.hasAnnotation(ExcelProperty.class));

            String[] expectedValue = {"Name"};

            AnnotationAttributes customField1Attrs = nameAnnotationMap.getAttributes(ComposableExcelProperty.class);
            Assertions.assertArrayEquals(
                    expectedValue, customField1Attrs.getRequiredAttribute("value", String[].class));

            AnnotationAttributes targetField1Attrs = nameAnnotationMap.getAttributes(ExcelProperty.class);
            Assertions.assertArrayEquals(
                    expectedValue, targetField1Attrs.getRequiredAttribute("value", String[].class));

            Head amountHead = property.getHeadMap().get(1);
            AnnotationMap amountAnnotationMap = amountHead.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(amountAnnotationMap);
            Assertions.assertTrue(amountAnnotationMap.hasAnnotation(ComposableNumberFormat.class));
            Assertions.assertTrue(amountAnnotationMap.hasAnnotation(NumberFormat.class));

            AnnotationAttributes customField2Attrs = amountAnnotationMap.getAttributes(ComposableNumberFormat.class);
            Assertions.assertEquals("#,##0.00", customField2Attrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes targetField2Attrs = amountAnnotationMap.getAttributes(NumberFormat.class);
            Assertions.assertEquals("#,##0.00", targetField2Attrs.getRequiredAttribute("value", String.class));
            Assertions.assertEquals(
                    RoundingMode.HALF_UP, targetField2Attrs.getRequiredAttribute("roundingMode", RoundingMode.class));
        }

        @Test
        void shouldPopulateBothLevels_whenClassHeaderStylePresetAndFieldDateTimeFormat() {
            // given - class has @ComposableHeaderStylePreset (groups HeadStyle + HeadFontStyle)
            //         field has @ComposableDateTimeFormat("yyyy-MM-dd") (aliases DateTimeFormat.value)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedHeaderStyleAndDateFormat.class, null);

            // then - class-level annotationMap
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(3, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ComposableHeaderStylePreset.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadFontStyle.class));

            AnnotationAttributes customTypeAttrs = classAnnotationMap.getAttributes(ComposableHeaderStylePreset.class);
            Assertions.assertTrue(customTypeAttrs.isEmpty());

            AnnotationAttributes styleAttrs = classAnnotationMap.getAttributes(HeadStyle.class);
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));

            AnnotationAttributes fontAttrs = classAnnotationMap.getAttributes(HeadFontStyle.class);
            Assertions.assertEquals("Calibri", fontAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 14, fontAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, fontAttrs.getRequiredAttribute("bold", BooleanEnum.class));

            // then - field-level annotationMap
            Head head = property.getHeadMap().get(0);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertEquals(2, fieldAnnotationMap.size());
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ComposableDateTimeFormat.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(DateTimeFormat.class));

            AnnotationAttributes customAttrs = fieldAnnotationMap.getAttributes(ComposableDateTimeFormat.class);
            Assertions.assertEquals("yyyy-MM-dd", customAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes targetAttrs = fieldAnnotationMap.getAttributes(DateTimeFormat.class);
            Assertions.assertEquals("yyyy-MM-dd", targetAttrs.getRequiredAttribute("value", String.class));
        }

        @Test
        void shouldPopulateDescriptorProperties_atBothLevels() {
            // given - class has @ComposableTableStylePreset
            //         field "name" has @ComposableExcelProperty({"Mixed Name"})

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedClassAndFieldComposable.class, null);

            // then - type descriptor
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertSame(ExcelModelMixedClassAndFieldComposable.class, typeDescriptor.getAnnotatedElement());
            Assertions.assertEquals(4, typeDescriptor.getAnnotationCount());

            // then - field descriptor
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);
            Assertions.assertEquals("name", fieldDescriptor.getFieldName());
            Assertions.assertNotNull(fieldDescriptor.getAnnotatedElement());
            Assertions.assertEquals(
                    "name", fieldDescriptor.getAnnotatedElement().getName());
            Assertions.assertEquals(2, fieldDescriptor.getAnnotationCount());
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ComposableExcelProperty.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ExcelProperty.class));
        }

        @Test
        void shouldPopulateIndependentFieldDescriptors_forMultipleFields() {
            // given - class has @ComposableTableStylePreset
            //         field 0 "name" has @ComposableExcelProperty({"Name"})
            //         field 1 "amount" has @ComposableNumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedMultipleFields.class, null);

            // then
            Assertions.assertEquals(2, property.getHeadMap().size());

            Head nameHead = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor nameDescriptor = nameHead.getFieldDescriptor();
            Assertions.assertEquals("name", nameDescriptor.getFieldName());
            Assertions.assertEquals("name", nameDescriptor.getAnnotatedElement().getName());
            Assertions.assertTrue(nameDescriptor.hasAnnotation(ComposableExcelProperty.class));
            Assertions.assertFalse(nameDescriptor.hasAnnotation(ComposableNumberFormat.class));

            Head amountHead = property.getHeadMap().get(1);
            AnnotatedFieldDescriptor amountDescriptor = amountHead.getFieldDescriptor();
            Assertions.assertEquals("amount", amountDescriptor.getFieldName());
            Assertions.assertEquals(
                    "amount", amountDescriptor.getAnnotatedElement().getName());
            Assertions.assertTrue(amountDescriptor.hasAnnotation(ComposableNumberFormat.class));
            Assertions.assertFalse(amountDescriptor.hasAnnotation(ComposableExcelProperty.class));
        }
    }

    @Nested
    class ErrorCases {

        @Test
        void shouldThrow_whenMarkedAnnotationHasInvalidAliasForTarget() {
            // given - ExcelModelAliasError has @FesodMarked as meta-annotation,
            //         with @AliasFor: one targets ExcelProperty (invalid, NOT meta-present)

            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> new ExcelHeadProperty(configurationHolder, ExcelModelAliasError.class, null));
        }

        @Test
        void shouldThrow_whenMarkedAnnotationHasMixedValidAndInvalidAliasForTargets() {
            // given - CustomExcelPropertyMixedAlias has @FesodMarked and @ExcelProperty as meta-annotation,
            //         with two @AliasFor: one targets ExcelProperty (valid, meta-present) and
            //         one targets ColumnWidth (invalid, NOT meta-present)

            // when / then
            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> new ExcelHeadProperty(configurationHolder, ExcelModelMixedAliasError.class, null));
        }
    }
}

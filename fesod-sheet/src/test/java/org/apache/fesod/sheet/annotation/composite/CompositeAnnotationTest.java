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
import java.lang.reflect.Field;
import java.math.RoundingMode;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.FesodMarked;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.FreezePane;
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
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
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
 *   <li>{@link FreezePane}</li>
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
@Tag(Tags.UNIT)
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
     * Composable annotation with no methods — groups {@code @HeadStyle}, {@code @HeadFontStyle} and {@code @FreezePane}
     * as a header style preset for class-level use.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadStyle(fillForegroundColor = 10)
    @HeadFontStyle(fontName = "Calibri", fontHeightInPoints = 14, bold = BooleanEnum.TRUE)
    @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Custom Name"})
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Custom Name"});
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(FullyComposableExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Full Name", "Common Config"})
                    .hasAttributeWithValue("index", 2)
                    .hasAttributeWithValue("order", 100)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Full Name", "Common Config"})
                    .hasAttributeWithValue("index", 2)
                    .hasAttributeWithValue("order", 100);
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableExcelPropertyPreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"First Name"});
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableNumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAttributeWithValue("roundingMode", RoundingMode.HALF_UP);
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableContentStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(ContentStyle.class)
                    .hasAttributeWithValue("wrapped", BooleanEnum.TRUE)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10)
                    .hasAnnotation(ContentFontStyle.class)
                    .hasAttributeWithValue("fontName", "Arial")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 12)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE)
                    .hasAnnotation(ExcelProperty.class);
        }

        @Test
        void shouldIncludeComposableAndInnerAnnotation_whenDateTimeFormatWithAliasFor() {
            // given - ExcelModelWithComposableDateTimeFormat has @ComposableDateTimeFormat("yyyy-MM-dd HH:mm")
            //         which aliases "value" attribute of @DateTimeFormat

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableDateTimeFormat.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableDateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd HH:mm")
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd HH:mm");
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
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableContentLoopMerge.class)
                    .hasAttributeWithValue("eachRow", 3)
                    .hasAttributeWithValue("columnExtend", 2)
                    .hasAnnotation(ContentLoopMerge.class)
                    .hasAttributeWithValue("eachRow", 3)
                    .hasAttributeWithValue("columnExtend", 2);
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
            Field element = FieldUtils.getDeclaredField(ExcelModelWithComposableField.class, "name", true);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .isAnnotatedElementEquals(element)
                    .satisfies(fieldDescriptor -> {
                        Assertions.assertEquals("name", fieldDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "name", fieldDescriptor.getAnnotatedElement().getName());
                    });
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(4)
                    .extractingAnnotations()
                    .hasAnnotation(ComposableContentStylePreset.class)
                    .hasAnnotation(ContentStyle.class)
                    .hasAnnotation(ContentFontStyle.class)
                    .hasAnnotation(ExcelProperty.class)
                    .and()
                    .doesNotHaveAnnotation(ColumnWidth.class);
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

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .and()
                    .doesNotHaveAnnotation(ColumnWidth.class);
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
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableColumnWidth.class)
                    .hasAttributeWithValue("value", 25)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 25);
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenNoMethodsInComposable() {
            // given - ExcelModelWithComposableGroupAnnotation has @ComposableAnnotationWithCommonStyle
            //         which has no methods, but meta-annotates @ColumnWidth(10) and @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableGroupAnnotation.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(3)
                    .hasAnnotation(ComposableAnnotationWithCommonStyle.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 10)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10);
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenTableStylePresetNoMethods() {
            // given - ExcelModelWithComposableTableStyle has @ComposableTableStylePreset
            //         which groups @HeadRowHeight(30), @ContentRowHeight(20), @OnceAbsoluteMerge(...)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableTableStyle.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableTableStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(HeadRowHeight.class)
                    .hasAttributeWithValue("value", (short) 30)
                    .hasAnnotation(ContentRowHeight.class)
                    .hasAttributeWithValue("value", (short) 20)
                    .hasAnnotation(OnceAbsoluteMerge.class)
                    .hasAttributeWithValue("firstRowIndex", 0)
                    .hasAttributeWithValue("lastRowIndex", 0)
                    .hasAttributeWithValue("firstColumnIndex", 0)
                    .hasAttributeWithValue("lastColumnIndex", 3);
        }

        @Test
        void shouldExpandAllInnerAnnotations_whenHeaderStylePresetNoMethods() {
            // given - ExcelModelWithComposableHeaderStyle has @ComposableHeaderStylePreset
            //         which groups @HeadStyle(fillForegroundColor=10), @HeadFontStyle(fontName="Calibri",
            //         fontHeightInPoints=14, bold=TRUE) and @FreezePane(colSplit = 1, rowSplit = 1,
            //         leftmostColumn = 3, topRow = 5)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableHeaderStyle.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableHeaderStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10)
                    .hasAnnotation(HeadFontStyle.class)
                    .hasAttributeWithValue("fontName", "Calibri")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 14)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE)
                    .hasAnnotation(FreezePane.class)
                    .hasAttributeWithValue("colSplit", 1)
                    .hasAttributeWithValue("rowSplit", 1)
                    .hasAttributeWithValue("leftmostColumn", 3)
                    .hasAttributeWithValue("topRow", 5);
        }

        @Test
        void shouldPopulateTypeDescriptor_withCorrectAnnotatedElement() {
            // given - ExcelModelWithComposableClassAnnotation has @ComposableColumnWidth(25)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableClassAnnotation.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .isAnnotatedElementEquals(ExcelModelWithComposableClassAnnotation.class);
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughTypeDescriptor() {
            // given - ExcelModelWithComposableGroupAnnotation has @ComposableAnnotationWithCommonStyle
            //         which groups @ColumnWidth(10) and @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableGroupAnnotation.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(3)
                    .extractingAnnotations()
                    .hasAnnotation(ComposableAnnotationWithCommonStyle.class)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAnnotation(HeadStyle.class)
                    .and()
                    .doesNotHaveAnnotation(ContentRowHeight.class);
        }

        @Test
        void shouldDelegateGetAnnotation_throughTypeDescriptor() {
            // given - ExcelModelWithComposableClassAnnotation has @ComposableColumnWidth(25)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithComposableClassAnnotation.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 25)
                    .and()
                    .doesNotHaveAnnotation(HeadStyle.class);
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
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableTableStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(HeadRowHeight.class)
                    .hasAttributeWithValue("value", (short) 30)
                    .hasAnnotation(ContentRowHeight.class)
                    .hasAttributeWithValue("value", (short) 20)
                    .hasAnnotation(OnceAbsoluteMerge.class)
                    .hasAttributeWithValue("firstRowIndex", 0)
                    .hasAttributeWithValue("lastRowIndex", 0)
                    .hasAttributeWithValue("firstColumnIndex", 0)
                    .hasAttributeWithValue("lastColumnIndex", 3);

            // then - field-level annotationMap
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Mixed Name"})
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Mixed Name"});
        }

        @Test
        void shouldPopulateBothLevels_whenClassAndFieldBothUseNoMethodsComposable() {
            // given - class has @ComposableAnnotationWithCommonStyle (groups ColumnWidth, HeadStyle)
            //         field has @ComposableContentStylePreset (groups ContentStyle, ContentFontStyle)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedBothNoMethods.class, null);

            // then - class-level annotationMap
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(3)
                    .hasAnnotation(ComposableAnnotationWithCommonStyle.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 10)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10);

            // then - field-level annotationMap
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableContentStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(ContentStyle.class)
                    .hasAttributeWithValue("wrapped", BooleanEnum.TRUE)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10)
                    .hasAnnotation(ContentFontStyle.class)
                    .hasAttributeWithValue("fontName", "Arial")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 12)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE)
                    .hasAnnotation(ExcelProperty.class);
        }

        @Test
        void shouldPopulateBothLevels_whenClassAndFieldBothUseAliasForComposable() {
            // given - class has @ComposableColumnWidth(50) (aliases ColumnWidth.value)
            //         field has @ComposableNumberFormat("0.00%") (aliases NumberFormat.value)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedAliasForBothLevels.class, null);

            // then - class-level annotationMap
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableColumnWidth.class)
                    .hasAttributeWithValue("value", 50)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 50);

            // then - field-level annotationMap
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableNumberFormat.class)
                    .hasAttributeWithValue("value", "0.00%")
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "0.00%")
                    .hasAttributeWithValue("roundingMode", RoundingMode.HALF_UP);
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
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableTableStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(HeadRowHeight.class)
                    .hasAttributeWithValue("value", (short) 30)
                    .hasAnnotation(ContentRowHeight.class)
                    .hasAttributeWithValue("value", (short) 20)
                    .hasAnnotation(OnceAbsoluteMerge.class)
                    .hasAttributeWithValue("firstRowIndex", 0)
                    .hasAttributeWithValue("lastRowIndex", 0)
                    .hasAttributeWithValue("firstColumnIndex", 0)
                    .hasAttributeWithValue("lastColumnIndex", 3);

            // then - each field has its own independent annotationMap
            Assertions.assertEquals(2, property.getHeadMap().size());

            Head nameHead = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(nameHead.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ComposableExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Name"})
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Name"});

            Head amountHead = property.getHeadMap().get(1);
            AnnotatedDescriptorAssertions.assertThat(amountHead.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ComposableNumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAttributeWithValue("roundingMode", RoundingMode.HALF_UP);
        }

        @Test
        void shouldPopulateBothLevels_whenClassHeaderStylePresetAndFieldDateTimeFormat() {
            // given - class has @ComposableHeaderStylePreset (groups HeadStyle + HeadFontStyle + FreezePane)
            //         field has @ComposableDateTimeFormat("yyyy-MM-dd") (aliases DateTimeFormat.value)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedHeaderStyleAndDateFormat.class, null);

            // then - class-level annotationMap
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ComposableHeaderStylePreset.class)
                    .hasAttributeSize(0)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10)
                    .hasAnnotation(HeadFontStyle.class)
                    .hasAttributeWithValue("fontName", "Calibri")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 14)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE)
                    .hasAnnotation(FreezePane.class)
                    .hasAttributeWithValue("colSplit", 1)
                    .hasAttributeWithValue("rowSplit", 1)
                    .hasAttributeWithValue("leftmostColumn", 3)
                    .hasAttributeWithValue("topRow", 5);

            // then - field-level annotationMap
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ComposableDateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd")
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd");
        }

        @Test
        void shouldPopulateDescriptorProperties_atBothLevels() {
            // given - class has @ComposableTableStylePreset
            //         field "name" has @ComposableExcelProperty({"Mixed Name"})

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedClassAndFieldComposable.class, null);

            // then - type descriptor
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .isAnnotatedElementEquals(ExcelModelMixedClassAndFieldComposable.class)
                    .hasAnnotationCount(4);

            // then - field descriptor
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(2)
                    .satisfies(fieldDescriptor -> {
                        Assertions.assertEquals("name", fieldDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "name", fieldDescriptor.getAnnotatedElement().getName());
                    })
                    .extractingAnnotations()
                    .hasAnnotation(ComposableExcelProperty.class)
                    .hasAnnotation(ExcelProperty.class);
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
            AnnotatedDescriptorAssertions.assertThat(nameHead.getFieldDescriptor())
                    .isNotNull()
                    .satisfies(nameDescriptor -> {
                        Assertions.assertEquals("name", nameDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "name", nameDescriptor.getAnnotatedElement().getName());
                    })
                    .extractingAnnotations()
                    .hasAnnotation(ComposableExcelProperty.class)
                    .and()
                    .doesNotHaveAnnotation(ComposableNumberFormat.class);

            Head amountHead = property.getHeadMap().get(1);
            AnnotatedDescriptorAssertions.assertThat(amountHead.getFieldDescriptor())
                    .isNotNull()
                    .satisfies(amountDescriptor -> {
                        Assertions.assertEquals("amount", amountDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "amount", amountDescriptor.getAnnotatedElement().getName());
                    })
                    .extractingAnnotations()
                    .hasAnnotation(ComposableNumberFormat.class)
                    .and()
                    .doesNotHaveAnnotation(ComposableExcelProperty.class);
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

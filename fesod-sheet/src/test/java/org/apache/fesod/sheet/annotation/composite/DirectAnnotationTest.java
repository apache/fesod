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

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.fesod.sheet.annotation.AnnotatedTypeDescriptor;
import org.apache.fesod.sheet.annotation.ExcelProperty;
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
 * Tests for direct (non-composable) annotation initialization analysis.
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
 *   <li><b>Field-level</b> — null annotationMap, single annotation, multiple annotations per field</li>
 *   <li><b>Class-level</b> — null headClazzAnnotationMap, single annotation, multiple annotations per class</li>
 *   <li><b>Mixed-level</b> — class + field annotations coexisting, all 13 annotations in a single model,
 *       per-field independence with shared class-level annotations</li>
 * </ul>
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class DirectAnnotationTest {

    @Mock
    private ConfigurationHolder configurationHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @BeforeEach
    void setup() {
        Mockito.lenient().when(configurationHolder.globalConfiguration()).thenReturn(globalConfiguration);
        Mockito.lenient().when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);
    }

    // ---- Model classes ----

    static class ExcelModelWithPlainField {

        private String name;
    }

    static class ExcelModelWithFieldProperty {

        @ExcelProperty("Name")
        private String name;
    }

    static class ExcelModelWithMultipleFieldAnnotations {

        @ExcelProperty("Date")
        @DateTimeFormat("yyyy-MM-dd")
        @ColumnWidth(30)
        private String date;
    }

    static class ExcelModelWithNumberFormat {

        @ExcelProperty("Amount")
        @NumberFormat("#,##0.00")
        private String amount;
    }

    static class ExcelModelWithContentFontStyle {

        @ExcelProperty("Name")
        @ContentFontStyle(fontName = "Arial", fontHeightInPoints = 12, bold = BooleanEnum.TRUE)
        private String name;
    }

    static class ExcelModelWithContentLoopMerge {

        @ExcelProperty("Value")
        @ContentLoopMerge(eachRow = 2, columnExtend = 3)
        private String value;
    }

    static class ExcelModelWithContentStyle {

        @ExcelProperty("Data")
        @ContentStyle(wrapped = BooleanEnum.TRUE, fillForegroundColor = 10)
        private String data;
    }

    static class ExcelModelWithHeadFontStyle {

        @ExcelProperty("Title")
        @HeadFontStyle(fontName = "Calibri", color = 10)
        private String title;
    }

    @Nested
    class FieldLevelAnnotationTest {

        // ---- Tests ----

        @Test
        void shouldSetNullFieldAnnotationMap_whenFieldHasNoRelevantAnnotations() {
            // given - ExcelModelWithPlainField has a plain field without annotations

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithPlainField.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            Assertions.assertNotNull(head);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .doesNotHaveAnnotationMap();
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withExcelProperty_whenFieldAnnotated() {
            // given - ExcelModelWithFieldProperty has @ExcelProperty("Name")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithFieldProperty.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            Assertions.assertNotNull(head);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Name"});
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withMultipleAnnotations_whenFieldAnnotated() {
            // given - ExcelModelWithMultipleFieldAnnotations has @ExcelProperty, @DateTimeFormat, @ColumnWidth

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleFieldAnnotations.class, null);

            // then
            Assertions.assertEquals(1, property.getHeadMap().size());

            Head head = property.getHeadMap().get(0);
            Assertions.assertNotNull(head);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(3)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd")
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 30);
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withNumberFormat_whenFieldAnnotated() {
            // given - ExcelModelWithNumberFormat has @NumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAttributeWithValue("roundingMode", RoundingMode.HALF_UP);
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withContentFontStyle_whenFieldAnnotated() {
            // given - ExcelModelWithContentFontStyle has @ContentFontStyle(fontName="Arial", fontHeightInPoints=12,
            // bold=TRUE)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentFontStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ContentFontStyle.class)
                    .hasAttributeWithValue("fontName", "Arial")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 12)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE);
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withContentLoopMerge_whenFieldAnnotated() {
            // given - ExcelModelWithContentLoopMerge has @ContentLoopMerge(eachRow=2, columnExtend=3)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentLoopMerge.class, null);

            // then
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ContentLoopMerge.class)
                    .hasAttributeWithValue("eachRow", 2)
                    .hasAttributeWithValue("columnExtend", 3);
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withContentStyle_whenFieldAnnotated() {
            // given - ExcelModelWithContentStyle has @ContentStyle(wrapped=TRUE, fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(ContentStyle.class)
                    .hasAttributeWithValue("wrapped", BooleanEnum.TRUE)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10);
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withHeadFontStyle_whenFieldAnnotated() {
            // given - ExcelModelWithHeadFontStyle has @HeadFontStyle(fontName="Calibri", color=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithHeadFontStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(HeadFontStyle.class)
                    .hasAttributeWithValue("fontName", "Calibri")
                    .hasAttributeWithValue("color", (short) 10);
        }

        @Test
        void shouldPopulateFieldDescriptor_withCorrectFieldNameAndElement_whenFieldAnnotated() {
            // given - ExcelModelWithFieldProperty has @ExcelProperty("Name") on "name" field

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithFieldProperty.class, null);

            // then
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .satisfies(fieldDescriptor -> {
                        Assertions.assertEquals("name", fieldDescriptor.getFieldName());
                        Assertions.assertNotNull(fieldDescriptor.getAnnotatedElement());
                        Assertions.assertEquals(
                                "name", fieldDescriptor.getAnnotatedElement().getName());
                    });
        }

        @Test
        void shouldPopulateFieldDescriptor_withCorrectFieldNameAndElement_whenPlainField() {
            // given - ExcelModelWithPlainField has a plain "name" field without annotations

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithPlainField.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            Field element = FieldUtils.getDeclaredField(ExcelModelWithPlainField.class, "name", true);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(0)
                    .isAnnotatedElementEquals(element)
                    .satisfies(fieldDescriptor -> {
                        Assertions.assertEquals("name", fieldDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "name", fieldDescriptor.getAnnotatedElement().getName());
                    });
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughFieldDescriptor() {
            // given - ExcelModelWithMultipleFieldAnnotations has @ExcelProperty, @DateTimeFormat, @ColumnWidth

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleFieldAnnotations.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(3)
                    .extractingAnnotations()
                    .hasAnnotation(ExcelProperty.class)
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAnnotation(ColumnWidth.class)
                    .and()
                    .doesNotHaveAnnotation(NumberFormat.class);
        }

        @Test
        void shouldDelegateGetAnnotation_throughFieldDescriptor() {
            // given - ExcelModelWithNumberFormat has @ExcelProperty("Amount") + @NumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .and()
                    .doesNotHaveAnnotation(DateTimeFormat.class);
        }
    }

    // ---- Model classes ----

    static class ExcelModelWithoutAnnotations {

        @ExcelProperty("Name")
        private String name;
    }

    @ColumnWidth(20)
    static class ExcelModelWithClassColumnWidth {

        @ExcelProperty("Name")
        private String name;
    }

    @ColumnWidth(15)
    @HeadStyle(fillForegroundColor = 10)
    static class ExcelModelWithMultipleClassAnnotations {

        @ExcelProperty("Name")
        private String name;
    }

    @ContentRowHeight(20)
    static class ExcelModelWithContentRowHeight {

        @ExcelProperty("Name")
        private String name;
    }

    @HeadRowHeight(30)
    static class ExcelModelWithHeadRowHeight {

        @ExcelProperty("Name")
        private String name;
    }

    @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 1, firstColumnIndex = 0, lastColumnIndex = 2)
    static class ExcelModelWithOnceAbsoluteMerge {

        @ExcelProperty("Name")
        private String name;
    }

    @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
    static class ExcelModelWithFreezePane {

        @ExcelProperty("Name")
        private String name;
    }

    @HeadRowHeight(30)
    @ContentRowHeight(20)
    @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 0, firstColumnIndex = 0, lastColumnIndex = 4)
    @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
    @ColumnWidth(25)
    @HeadStyle(fillForegroundColor = 15)
    @HeadFontStyle(fontName = "Header", fontHeightInPoints = 14, bold = BooleanEnum.TRUE)
    @ContentStyle(wrapped = BooleanEnum.TRUE)
    @ContentFontStyle(fontName = "Content", fontHeightInPoints = 11)
    static class ExcelModelMixedAllAnnotations {

        @ExcelProperty("Date")
        @DateTimeFormat("yyyy-MM-dd")
        @NumberFormat("#,##0.00")
        @ContentLoopMerge(eachRow = 2, columnExtend = 3)
        private String date;
    }

    @ColumnWidth(20)
    @HeadStyle(fillForegroundColor = 10)
    static class ExcelModelMixedClassStyleAndFieldFormat {

        @ExcelProperty("Amount")
        @NumberFormat("#,##0.00")
        private String amount;

        @ExcelProperty("Date")
        @DateTimeFormat("yyyy-MM-dd")
        private String date;
    }

    @Nested
    class ClassLevelAnnotationTest {

        @Test
        void shouldSetNullHeadClazzAnnotationMap_whenNoHeadClazzProvided() {
            // given
            List<List<String>> head = new ArrayList<>();
            head.add(Arrays.asList("Name"));

            // when
            ExcelHeadProperty property = new ExcelHeadProperty(configurationHolder, null, head);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .doesNotHaveAnnotationMap();
        }

        @Test
        void shouldSetNullHeadClazzAnnotationMap_whenHeadClazzHasNoAnnotations() {
            // given - ExcelModelWithoutAnnotations has no class-level annotations

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithoutAnnotations.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .doesNotHaveAnnotationMap();
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withColumnWidth_whenClassAnnotated() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20) at class level

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 20);
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withMultipleAnnotations_whenClassAnnotated() {
            // given - ExcelModelWithMultipleClassAnnotations has @ColumnWidth(15) and
            // @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleClassAnnotations.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 15)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10);
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withContentRowHeight_whenClassAnnotated() {
            // given - ExcelModelWithContentRowHeight has @ContentRowHeight(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentRowHeight.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(ContentRowHeight.class)
                    .hasAttributeWithValue("value", (short) 20);
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withHeadRowHeight_whenClassAnnotated() {
            // given - ExcelModelWithHeadRowHeight has @HeadRowHeight(30)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithHeadRowHeight.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(HeadRowHeight.class)
                    .hasAttributeWithValue("value", (short) 30);
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withOnceAbsoluteMerge_whenClassAnnotated() {
            // given - ExcelModelWithOnceAbsoluteMerge has @OnceAbsoluteMerge(firstRowIndex=0, lastRowIndex=1,
            // firstColumnIndex=0, lastColumnIndex=2)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithOnceAbsoluteMerge.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(OnceAbsoluteMerge.class)
                    .hasAttributeWithValue("firstRowIndex", 0)
                    .hasAttributeWithValue("lastRowIndex", 1)
                    .hasAttributeWithValue("firstColumnIndex", 0)
                    .hasAttributeWithValue("lastColumnIndex", 2);
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withFreezePane_whenClassAnnotated() {
            // given - ExcelModelWithFreezePane has @FreezePane(colSplit = 1, rowSplit = 1,
            // leftmostColumn = 3, topRow = 5)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithFreezePane.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .hasAnnotation(FreezePane.class)
                    .hasAttributeWithValue("colSplit", 1)
                    .hasAttributeWithValue("rowSplit", 1)
                    .hasAttributeWithValue("leftmostColumn", 3)
                    .hasAttributeWithValue("topRow", 5);
        }

        @Test
        void shouldSetEmptyTypeDescriptor_whenNoHeadClazzProvided() {
            // given
            List<List<String>> head = new ArrayList<>();
            head.add(Arrays.asList("Name"));

            // when
            ExcelHeadProperty property = new ExcelHeadProperty(configurationHolder, null, head);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .satisfies(typeDescriptor -> {
                        Assertions.assertSame(AnnotatedTypeDescriptor.EMPTY, typeDescriptor);
                        Assertions.assertNull(typeDescriptor.getAnnotatedElement());
                        Assertions.assertNull(typeDescriptor.getAnnotationMap());
                        Assertions.assertEquals(0, typeDescriptor.getAnnotationCount());
                        Assertions.assertFalse(typeDescriptor.hasAnnotation(ColumnWidth.class));
                    });
        }

        @Test
        void shouldPopulateTypeDescriptor_withCorrectAnnotatedElement() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .satisfies(typeDescriptor -> {
                        Assertions.assertSame(
                                ExcelModelWithClassColumnWidth.class, typeDescriptor.getAnnotatedElement());
                    });
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughTypeDescriptor() {
            // given - ExcelModelWithMultipleClassAnnotations has @ColumnWidth(15) and
            // @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleClassAnnotations.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 15)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 10)
                    .and()
                    .doesNotHaveAnnotation(ContentRowHeight.class);
        }

        @Test
        void shouldDelegateGetAnnotation_throughTypeDescriptor() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(1)
                    .doesNotHaveAnnotation(HeadStyle.class)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 20);
        }
    }

    @Nested
    class MixedLevelAnnotationTest {

        @Test
        void shouldPopulateBothLevels_whenAllTwelveAnnotationsUsedAcrossClassAndField() {
            // given - class-level: HeadRowHeight, ContentRowHeight, OnceAbsoluteMerge, FreezePane,
            //         ColumnWidth, HeadStyle, HeadFontStyle, ContentStyle, ContentFontStyle
            //         field-level: ExcelProperty, DateTimeFormat, NumberFormat, ContentLoopMerge

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedAllAnnotations.class, null);

            // then - class-level annotationMap covers 9 annotations
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(9)
                    .hasAnnotation(HeadRowHeight.class)
                    .hasAttributeWithValue("value", (short) 30)
                    .hasAnnotation(ContentRowHeight.class)
                    .hasAttributeWithValue("value", (short) 20)
                    .hasAnnotation(OnceAbsoluteMerge.class)
                    .hasAttributeWithValue("firstRowIndex", 0)
                    .hasAttributeWithValue("lastRowIndex", 0)
                    .hasAttributeWithValue("firstColumnIndex", 0)
                    .hasAttributeWithValue("lastColumnIndex", 4)
                    .hasAnnotation(FreezePane.class)
                    .hasAttributeWithValue("colSplit", 1)
                    .hasAttributeWithValue("rowSplit", 1)
                    .hasAttributeWithValue("leftmostColumn", 3)
                    .hasAttributeWithValue("topRow", 5)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAttributeWithValue("value", 25)
                    .hasAnnotation(HeadStyle.class)
                    .hasAttributeWithValue("fillForegroundColor", (short) 15)
                    .hasAnnotation(HeadFontStyle.class)
                    .hasAttributeWithValue("fontName", "Header")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 14)
                    .hasAttributeWithValue("bold", BooleanEnum.TRUE)
                    .hasAnnotation(ContentStyle.class)
                    .hasAttributeWithValue("wrapped", BooleanEnum.TRUE)
                    .hasAnnotation(ContentFontStyle.class)
                    .hasAttributeWithValue("fontName", "Content")
                    .hasAttributeWithValue("fontHeightInPoints", (short) 11);

            // then - field-level annotationMap covers 4 annotations
            Head head = property.getHeadMap().get(0);

            AnnotatedDescriptorAssertions.assertThat(head.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(4)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAttributeWithValue("value", new String[] {"Date"})
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd")
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00")
                    .hasAnnotation(ContentLoopMerge.class)
                    .hasAttributeWithValue("eachRow", 2)
                    .hasAttributeWithValue("columnExtend", 3);
        }

        @Test
        void shouldPopulateEachFieldIndependently_whenMixedClassAndFieldAnnotations() {
            // given - class has @ColumnWidth(20) and @HeadStyle(fillForegroundColor=10)
            //         field 0 has @ExcelProperty("Amount") + @NumberFormat("#,##0.00")
            //         field 1 has @ExcelProperty("Date") + @DateTimeFormat("yyyy-MM-dd")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedClassStyleAndFieldFormat.class, null);

            // then - class-level annotationMap
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ColumnWidth.class)
                    .hasAnnotation(HeadStyle.class);

            // then - each field has its own independent annotationMap
            Assertions.assertEquals(2, property.getHeadMap().size());

            Head amountHead = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(amountHead.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAnnotation(NumberFormat.class)
                    .hasAttributeWithValue("value", "#,##0.00");

            Head dateHead = property.getHeadMap().get(1);
            AnnotatedDescriptorAssertions.assertThat(dateHead.getFieldDescriptor())
                    .isNotNull()
                    .extractingAnnotations()
                    .hasSize(2)
                    .hasAnnotation(ExcelProperty.class)
                    .hasAnnotation(DateTimeFormat.class)
                    .hasAttributeWithValue("value", "yyyy-MM-dd");
        }

        @Test
        void shouldPopulateDescriptorProperties_atBothLevels() {
            // given - class has @ColumnWidth(20) and @HeadStyle(fillForegroundColor=10)
            //         field 0 "amount" has @ExcelProperty("Amount") + @NumberFormat("#,##0.00")
            //         field 1 "date" has @ExcelProperty("Date") + @DateTimeFormat("yyyy-MM-dd")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedClassStyleAndFieldFormat.class, null);

            // then - type descriptor
            AnnotatedDescriptorAssertions.assertThat(property.getTypeDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(2)
                    .isAnnotatedElementEquals(ExcelModelMixedClassStyleAndFieldFormat.class);

            // then - field descriptors have correct names and elements
            Head amountHead = property.getHeadMap().get(0);
            AnnotatedDescriptorAssertions.assertThat(amountHead.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(2)
                    .satisfies(amountDescriptor -> {
                        Assertions.assertEquals("amount", amountDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "amount", amountDescriptor.getAnnotatedElement().getName());
                    });

            Head dateHead = property.getHeadMap().get(1);
            AnnotatedDescriptorAssertions.assertThat(dateHead.getFieldDescriptor())
                    .isNotNull()
                    .hasAnnotationCount(2)
                    .satisfies(dateDescriptor -> {
                        Assertions.assertEquals("date", dateDescriptor.getFieldName());
                        Assertions.assertEquals(
                                "date", dateDescriptor.getAnnotatedElement().getName());
                    });
        }
    }
}

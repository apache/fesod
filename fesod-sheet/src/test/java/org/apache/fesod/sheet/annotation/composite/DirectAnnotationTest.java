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

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.annotation.AnnotatedFieldDescriptor;
import org.apache.fesod.sheet.annotation.AnnotatedTypeDescriptor;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.AnnotationMap;
import org.apache.fesod.sheet.annotation.ExcelProperty;
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
 * </ul>
 * <p />
 * Covered test scenarios:
 * <ul>
 *   <li><b>Field-level</b> — null annotationMap, single annotation, multiple annotations per field</li>
 *   <li><b>Class-level</b> — null headClazzAnnotationMap, single annotation, multiple annotations per class</li>
 *   <li><b>Mixed-level</b> — class + field annotations coexisting, all 12 annotations in a single model,
 *       per-field independence with shared class-level annotations</li>
 * </ul>
 */
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
            Assertions.assertNull(head.getFieldDescriptor().getAnnotationMap());
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
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertFalse(fieldAnnotationMap.isEmpty());
            Assertions.assertEquals(1, fieldAnnotationMap.size());
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));

            AnnotationAttributes attrs = fieldAnnotationMap.getAttributes(ExcelProperty.class);
            String[] value = attrs.getRequiredAttribute("value", String[].class);
            Assertions.assertArrayEquals(new String[] {"Name"}, value);
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
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertFalse(fieldAnnotationMap.isEmpty());
            Assertions.assertEquals(3, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(DateTimeFormat.class));
            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ColumnWidth.class));

            AnnotationAttributes dtAttrs = fieldAnnotationMap.getAttributes(DateTimeFormat.class);
            Assertions.assertEquals("yyyy-MM-dd", dtAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes cwAttrs = fieldAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(30, cwAttrs.getRequiredAttribute("value", Integer.class));
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withNumberFormat_whenFieldAnnotated() {
            // given - ExcelModelWithNumberFormat has @NumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertTrue(annotationMap.hasAnnotation(NumberFormat.class));

            AnnotationAttributes attrs = annotationMap.getAttributes(NumberFormat.class);
            Assertions.assertEquals("#,##0.00", attrs.getRequiredAttribute("value", String.class));
            Assertions.assertEquals(
                    RoundingMode.HALF_UP, attrs.getRequiredAttribute("roundingMode", RoundingMode.class));
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
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentFontStyle.class));

            AnnotationAttributes attrs = annotationMap.getAttributes(ContentFontStyle.class);
            Assertions.assertEquals("Arial", attrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 12, attrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, attrs.getRequiredAttribute("bold", BooleanEnum.class));
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withContentLoopMerge_whenFieldAnnotated() {
            // given - ExcelModelWithContentLoopMerge has @ContentLoopMerge(eachRow=2, columnExtend=3)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentLoopMerge.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentLoopMerge.class));

            AnnotationAttributes attrs = annotationMap.getAttributes(ContentLoopMerge.class);
            Assertions.assertEquals(2, attrs.getRequiredAttribute("eachRow", Integer.class));
            Assertions.assertEquals(3, attrs.getRequiredAttribute("columnExtend", Integer.class));
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withContentStyle_whenFieldAnnotated() {
            // given - ExcelModelWithContentStyle has @ContentStyle(wrapped=TRUE, fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertTrue(annotationMap.hasAnnotation(ContentStyle.class));

            AnnotationAttributes attrs = annotationMap.getAttributes(ContentStyle.class);
            Assertions.assertEquals(BooleanEnum.TRUE, attrs.getRequiredAttribute("wrapped", BooleanEnum.class));
            Assertions.assertEquals((short) 10, attrs.getRequiredAttribute("fillForegroundColor", Short.class));
        }

        @Test
        void shouldPopulateFieldAnnotationMap_withHeadFontStyle_whenFieldAnnotated() {
            // given - ExcelModelWithHeadFontStyle has @HeadFontStyle(fontName="Calibri", color=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithHeadFontStyle.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotationMap annotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(annotationMap);
            Assertions.assertTrue(annotationMap.hasAnnotation(HeadFontStyle.class));

            AnnotationAttributes attrs = annotationMap.getAttributes(HeadFontStyle.class);
            Assertions.assertEquals("Calibri", attrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 10, attrs.getRequiredAttribute("color", Short.class));
        }

        @Test
        void shouldPopulateFieldDescriptor_withCorrectFieldNameAndElement_whenFieldAnnotated() {
            // given - ExcelModelWithFieldProperty has @ExcelProperty("Name") on "name" field

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithFieldProperty.class, null);

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
        void shouldPopulateFieldDescriptor_withCorrectFieldNameAndElement_whenPlainField() {
            // given - ExcelModelWithPlainField has a plain "name" field without annotations

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithPlainField.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);
            Assertions.assertEquals("name", fieldDescriptor.getFieldName());
            Assertions.assertNotNull(fieldDescriptor.getAnnotatedElement());
            Assertions.assertEquals(
                    "name", fieldDescriptor.getAnnotatedElement().getName());
            Assertions.assertEquals(0, fieldDescriptor.getAnnotationCount());
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughFieldDescriptor() {
            // given - ExcelModelWithMultipleFieldAnnotations has @ExcelProperty, @DateTimeFormat, @ColumnWidth

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleFieldAnnotations.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);
            Assertions.assertEquals(3, fieldDescriptor.getAnnotationCount());
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ExcelProperty.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(DateTimeFormat.class));
            Assertions.assertTrue(fieldDescriptor.hasAnnotation(ColumnWidth.class));
            Assertions.assertFalse(fieldDescriptor.hasAnnotation(NumberFormat.class));
        }

        @Test
        void shouldDelegateGetAnnotation_throughFieldDescriptor() {
            // given - ExcelModelWithNumberFormat has @ExcelProperty("Amount") + @NumberFormat("#,##0.00")

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithNumberFormat.class, null);

            // then
            Head head = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor fieldDescriptor = head.getFieldDescriptor();
            Assertions.assertNotNull(fieldDescriptor);

            AnnotationAttributes numberAttrs = fieldDescriptor.getAnnotation(NumberFormat.class);
            Assertions.assertNotNull(numberAttrs);
            Assertions.assertEquals("#,##0.00", numberAttrs.getRequiredAttribute("value", String.class));

            AnnotationAttributes missingAttrs = fieldDescriptor.getAnnotation(DateTimeFormat.class);
            Assertions.assertNull(missingAttrs);
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

    @HeadRowHeight(30)
    @ContentRowHeight(20)
    @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 0, firstColumnIndex = 0, lastColumnIndex = 4)
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
            Assertions.assertNull(property.getTypeDescriptor().getAnnotationMap());
        }

        @Test
        void shouldSetNullHeadClazzAnnotationMap_whenHeadClazzHasNoAnnotations() {
            // given - ExcelModelWithoutAnnotations has no class-level annotations

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithoutAnnotations.class, null);

            // then
            Assertions.assertNull(property.getTypeDescriptor().getAnnotationMap());
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withColumnWidth_whenClassAnnotated() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20) at class level

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertFalse(classAnnotationMap.isEmpty());
            Assertions.assertEquals(1, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));

            AnnotationAttributes widthAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(20, widthAttrs.getRequiredAttribute("value", Integer.class));
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withMultipleAnnotations_whenClassAnnotated() {
            // given - ExcelModelWithMultipleClassAnnotations has @ColumnWidth(15) and
            // @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleClassAnnotations.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertFalse(classAnnotationMap.isEmpty());
            Assertions.assertEquals(2, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));

            AnnotationAttributes widthAttrs = classAnnotationMap.getAttributes(ColumnWidth.class);
            Assertions.assertEquals(15, widthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes styleAttrs = classAnnotationMap.getAttributes(HeadStyle.class);
            Assertions.assertEquals((short) 10, styleAttrs.getRequiredAttribute("fillForegroundColor", Short.class));
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withContentRowHeight_whenClassAnnotated() {
            // given - ExcelModelWithContentRowHeight has @ContentRowHeight(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithContentRowHeight.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentRowHeight.class));

            AnnotationAttributes attrs = classAnnotationMap.getAttributes(ContentRowHeight.class);
            Assertions.assertEquals((short) 20, attrs.getRequiredAttribute("value", Short.class));
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withHeadRowHeight_whenClassAnnotated() {
            // given - ExcelModelWithHeadRowHeight has @HeadRowHeight(30)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithHeadRowHeight.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadRowHeight.class));

            AnnotationAttributes attrs = classAnnotationMap.getAttributes(HeadRowHeight.class);
            Assertions.assertEquals((short) 30, attrs.getRequiredAttribute("value", Short.class));
        }

        @Test
        void shouldPopulateHeadClazzAnnotationMap_withOnceAbsoluteMerge_whenClassAnnotated() {
            // given - ExcelModelWithOnceAbsoluteMerge has @OnceAbsoluteMerge(firstRowIndex=0, lastRowIndex=1,
            // firstColumnIndex=0, lastColumnIndex=2)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithOnceAbsoluteMerge.class, null);

            // then
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(OnceAbsoluteMerge.class));

            AnnotationAttributes attrs = classAnnotationMap.getAttributes(OnceAbsoluteMerge.class);
            Assertions.assertEquals(0, attrs.getRequiredAttribute("firstRowIndex", Integer.class));
            Assertions.assertEquals(1, attrs.getRequiredAttribute("lastRowIndex", Integer.class));
            Assertions.assertEquals(0, attrs.getRequiredAttribute("firstColumnIndex", Integer.class));
            Assertions.assertEquals(2, attrs.getRequiredAttribute("lastColumnIndex", Integer.class));
        }

        @Test
        void shouldSetEmptyTypeDescriptor_whenNoHeadClazzProvided() {
            // given
            List<List<String>> head = new ArrayList<>();
            head.add(Arrays.asList("Name"));

            // when
            ExcelHeadProperty property = new ExcelHeadProperty(configurationHolder, null, head);

            // then
            Assertions.assertSame(AnnotatedTypeDescriptor.EMPTY, property.getTypeDescriptor());
            Assertions.assertNull(property.getTypeDescriptor().getAnnotatedElement());
            Assertions.assertNull(property.getTypeDescriptor().getAnnotationMap());
            Assertions.assertEquals(0, property.getTypeDescriptor().getAnnotationCount());
            Assertions.assertFalse(property.getTypeDescriptor().hasAnnotation(ColumnWidth.class));
        }

        @Test
        void shouldPopulateTypeDescriptor_withCorrectAnnotatedElement() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertSame(ExcelModelWithClassColumnWidth.class, typeDescriptor.getAnnotatedElement());
        }

        @Test
        void shouldDelegateHasAnnotationAndCount_throughTypeDescriptor() {
            // given - ExcelModelWithMultipleClassAnnotations has @ColumnWidth(15) and
            // @HeadStyle(fillForegroundColor=10)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithMultipleClassAnnotations.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertEquals(2, typeDescriptor.getAnnotationCount());
            Assertions.assertTrue(typeDescriptor.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(typeDescriptor.hasAnnotation(HeadStyle.class));
            Assertions.assertFalse(typeDescriptor.hasAnnotation(ContentRowHeight.class));
        }

        @Test
        void shouldDelegateGetAnnotation_throughTypeDescriptor() {
            // given - ExcelModelWithClassColumnWidth has @ColumnWidth(20)

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelWithClassColumnWidth.class, null);

            // then
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);

            AnnotationAttributes widthAttrs = typeDescriptor.getAnnotation(ColumnWidth.class);
            Assertions.assertNotNull(widthAttrs);
            Assertions.assertEquals(20, widthAttrs.getRequiredAttribute("value", Integer.class));

            AnnotationAttributes missingAttrs = typeDescriptor.getAnnotation(HeadStyle.class);
            Assertions.assertNull(missingAttrs);
        }
    }

    @Nested
    class MixedLevelAnnotationTest {

        @Test
        void shouldPopulateBothLevels_whenAllTwelveAnnotationsUsedAcrossClassAndField() {
            // given - class-level: HeadRowHeight, ContentRowHeight, OnceAbsoluteMerge, ColumnWidth,
            //         HeadStyle, HeadFontStyle, ContentStyle, ContentFontStyle
            //         field-level: ExcelProperty, DateTimeFormat, NumberFormat, ContentLoopMerge

            // when
            ExcelHeadProperty property =
                    new ExcelHeadProperty(configurationHolder, ExcelModelMixedAllAnnotations.class, null);

            // then - class-level annotationMap covers 8 annotations
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(8, classAnnotationMap.size());

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadRowHeight.class));
            Assertions.assertEquals(
                    (short) 30,
                    classAnnotationMap.getAttributes(HeadRowHeight.class).getRequiredAttribute("value", Short.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentRowHeight.class));
            Assertions.assertEquals(
                    (short) 20,
                    classAnnotationMap
                            .getAttributes(ContentRowHeight.class)
                            .getRequiredAttribute("value", Short.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(OnceAbsoluteMerge.class));
            AnnotationAttributes mergeAttrs = classAnnotationMap.getAttributes(OnceAbsoluteMerge.class);
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("lastRowIndex", Integer.class));
            Assertions.assertEquals(0, mergeAttrs.getRequiredAttribute("firstColumnIndex", Integer.class));
            Assertions.assertEquals(4, mergeAttrs.getRequiredAttribute("lastColumnIndex", Integer.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));
            Assertions.assertEquals(
                    25,
                    classAnnotationMap.getAttributes(ColumnWidth.class).getRequiredAttribute("value", Integer.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));
            Assertions.assertEquals(
                    (short) 15,
                    classAnnotationMap
                            .getAttributes(HeadStyle.class)
                            .getRequiredAttribute("fillForegroundColor", Short.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadFontStyle.class));
            AnnotationAttributes hfAttrs = classAnnotationMap.getAttributes(HeadFontStyle.class);
            Assertions.assertEquals("Header", hfAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 14, hfAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));
            Assertions.assertEquals(BooleanEnum.TRUE, hfAttrs.getRequiredAttribute("bold", BooleanEnum.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentStyle.class));
            Assertions.assertEquals(
                    BooleanEnum.TRUE,
                    classAnnotationMap
                            .getAttributes(ContentStyle.class)
                            .getRequiredAttribute("wrapped", BooleanEnum.class));

            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ContentFontStyle.class));
            AnnotationAttributes cfAttrs = classAnnotationMap.getAttributes(ContentFontStyle.class);
            Assertions.assertEquals("Content", cfAttrs.getRequiredAttribute("fontName", String.class));
            Assertions.assertEquals((short) 11, cfAttrs.getRequiredAttribute("fontHeightInPoints", Short.class));

            // then - field-level annotationMap covers 4 annotations
            Head head = property.getHeadMap().get(0);
            AnnotationMap fieldAnnotationMap = head.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(fieldAnnotationMap);
            Assertions.assertEquals(4, fieldAnnotationMap.size());

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ExcelProperty.class));
            Assertions.assertArrayEquals(
                    new String[] {"Date"},
                    fieldAnnotationMap
                            .getAttributes(ExcelProperty.class)
                            .getRequiredAttribute("value", String[].class));

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(DateTimeFormat.class));
            Assertions.assertEquals(
                    "yyyy-MM-dd",
                    fieldAnnotationMap.getAttributes(DateTimeFormat.class).getRequiredAttribute("value", String.class));

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(NumberFormat.class));
            Assertions.assertEquals(
                    "#,##0.00",
                    fieldAnnotationMap.getAttributes(NumberFormat.class).getRequiredAttribute("value", String.class));

            Assertions.assertTrue(fieldAnnotationMap.hasAnnotation(ContentLoopMerge.class));
            AnnotationAttributes clmAttrs = fieldAnnotationMap.getAttributes(ContentLoopMerge.class);
            Assertions.assertEquals(2, clmAttrs.getRequiredAttribute("eachRow", Integer.class));
            Assertions.assertEquals(3, clmAttrs.getRequiredAttribute("columnExtend", Integer.class));
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
            AnnotationMap classAnnotationMap = property.getTypeDescriptor().getAnnotationMap();
            Assertions.assertNotNull(classAnnotationMap);
            Assertions.assertEquals(2, classAnnotationMap.size());
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(ColumnWidth.class));
            Assertions.assertTrue(classAnnotationMap.hasAnnotation(HeadStyle.class));

            // then - each field has its own independent annotationMap
            Assertions.assertEquals(2, property.getHeadMap().size());

            Head amountHead = property.getHeadMap().get(0);
            AnnotationMap amountMap = amountHead.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(amountMap);
            Assertions.assertEquals(2, amountMap.size());
            Assertions.assertTrue(amountMap.hasAnnotation(ExcelProperty.class));
            Assertions.assertTrue(amountMap.hasAnnotation(NumberFormat.class));
            Assertions.assertEquals(
                    "#,##0.00",
                    amountMap.getAttributes(NumberFormat.class).getRequiredAttribute("value", String.class));

            Head dateHead = property.getHeadMap().get(1);
            AnnotationMap dateMap = dateHead.getFieldDescriptor().getAnnotationMap();
            Assertions.assertNotNull(dateMap);
            Assertions.assertEquals(2, dateMap.size());
            Assertions.assertTrue(dateMap.hasAnnotation(ExcelProperty.class));
            Assertions.assertTrue(dateMap.hasAnnotation(DateTimeFormat.class));
            Assertions.assertEquals(
                    "yyyy-MM-dd",
                    dateMap.getAttributes(DateTimeFormat.class).getRequiredAttribute("value", String.class));
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
            AnnotatedTypeDescriptor typeDescriptor = property.getTypeDescriptor();
            Assertions.assertNotNull(typeDescriptor);
            Assertions.assertSame(ExcelModelMixedClassStyleAndFieldFormat.class, typeDescriptor.getAnnotatedElement());
            Assertions.assertEquals(2, typeDescriptor.getAnnotationCount());

            // then - field descriptors have correct names and elements
            Head amountHead = property.getHeadMap().get(0);
            AnnotatedFieldDescriptor amountDescriptor = amountHead.getFieldDescriptor();
            Assertions.assertEquals("amount", amountDescriptor.getFieldName());
            Assertions.assertEquals(
                    "amount", amountDescriptor.getAnnotatedElement().getName());
            Assertions.assertEquals(2, amountDescriptor.getAnnotationCount());

            Head dateHead = property.getHeadMap().get(1);
            AnnotatedFieldDescriptor dateDescriptor = dateHead.getFieldDescriptor();
            Assertions.assertEquals("date", dateDescriptor.getFieldName());
            Assertions.assertEquals("date", dateDescriptor.getAnnotatedElement().getName());
            Assertions.assertEquals(2, dateDescriptor.getAnnotationCount());
        }
    }
}

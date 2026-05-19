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
import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;
import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;
import org.apache.fesod.sheet.enums.poi.VerticalAlignmentEnum;

/**
 * All composable (composite) annotation definitions used in integration tests.
 * Each composable annotation bundles one or more inner annotations via {@link FesodMarked}.
 */
public class IntegrationAnnotations {

    private IntegrationAnnotations() {}

    // ---- Field-Level (or dual-target) Composable Annotations ----

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(value = "Name")
    @Inherited
    public @interface CompositeExcelProperty {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty
    @Inherited
    public @interface CompositeExcelPropertyAliasFor {
        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String[] value() default {""};
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @DateTimeFormat(value = "yyyy-MM-dd")
    @Inherited
    public @interface CompositeDateTimeFormat {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @NumberFormat(value = "#,##0.00")
    @Inherited
    public @interface CompositeNumberFormat {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ColumnWidth(value = 25)
    @Inherited
    public @interface CompositeColumnWidth {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadStyle(
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,
            fillForegroundColor = 42,
            fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
    @Inherited
    public @interface CompositeHeadStyle {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadFontStyle(bold = BooleanEnum.TRUE, fontHeightInPoints = 14)
    @Inherited
    public @interface CompositeHeadFontStyle {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER)
    @Inherited
    public @interface CompositeContentStyle {}

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentFontStyle(italic = BooleanEnum.TRUE, fontName = "Arial")
    @Inherited
    public @interface CompositeContentFontStyle {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentLoopMerge(eachRow = 2, columnExtend = 1)
    @Inherited
    public @interface CompositeContentLoopMerge {}

    // ---- @AliasFor Composable Annotations ----

    /**
     * Aliases {@code width} to {@link ColumnWidth#value()} (different attribute name).
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ColumnWidth
    @Inherited
    public @interface CompositeColumnWidthAliasFor {
        @FesodMarked.AliasFor(annotation = ColumnWidth.class, attribute = "value")
        int width() default -1;
    }

    /**
     * Aliases {@code pattern} to {@link DateTimeFormat#value()} (different attribute name).
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @DateTimeFormat
    @Inherited
    public @interface CompositeDateTimeFormatAliasFor {
        @FesodMarked.AliasFor(annotation = DateTimeFormat.class, attribute = "value")
        String pattern() default "";
    }

    /**
     * Aliases {@code pattern} to {@link NumberFormat#value()} (different attribute name).
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @NumberFormat
    @Inherited
    public @interface CompositeNumberFormatAliasFor {
        @FesodMarked.AliasFor(annotation = NumberFormat.class, attribute = "value")
        String pattern() default "";
    }

    /**
     * Multiple {@code @AliasFor} attributes mapping to the same inner annotation.
     * Aliases {@code fontSize} → {@link HeadFontStyle#fontHeightInPoints()},
     *         {@code fontColor} → {@link HeadFontStyle#color()}.
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadFontStyle
    @Inherited
    public @interface CompositeHeadFontStyleAliasFor {
        @FesodMarked.AliasFor(annotation = HeadFontStyle.class, attribute = "fontHeightInPoints")
        short fontSize() default -1;

        @FesodMarked.AliasFor(annotation = HeadFontStyle.class, attribute = "color")
        short fontColor() default -1;
    }

    /**
     * Aliases {@code alignment} to {@link HeadStyle#horizontalAlignment()},
     *         {@code bgColor} to {@link HeadStyle#fillForegroundColor()}.
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadStyle
    @Inherited
    public @interface CompositeHeadStyleAliasFor {
        @FesodMarked.AliasFor(annotation = HeadStyle.class, attribute = "horizontalAlignment")
        HorizontalAlignmentEnum alignment() default HorizontalAlignmentEnum.DEFAULT;

        @FesodMarked.AliasFor(annotation = HeadStyle.class, attribute = "fillForegroundColor")
        short bgColor() default -1;
    }

    /**
     * Aliases {@code wrap} to {@link ContentStyle#wrapped()},
     *         {@code vAlign} to {@link ContentStyle#verticalAlignment()}.
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentStyle
    @Inherited
    public @interface CompositeContentStyleAliasFor {
        @FesodMarked.AliasFor(annotation = ContentStyle.class, attribute = "wrapped")
        BooleanEnum wrap() default BooleanEnum.DEFAULT;

        @FesodMarked.AliasFor(annotation = ContentStyle.class, attribute = "verticalAlignment")
        VerticalAlignmentEnum vAlign() default VerticalAlignmentEnum.DEFAULT;
    }

    /**
     * Aliases {@code font} to {@link ContentFontStyle#fontName()},
     *         {@code size} to {@link ContentFontStyle#fontHeightInPoints()}.
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentFontStyle
    @Inherited
    public @interface CompositeContentFontStyleAliasFor {
        @FesodMarked.AliasFor(annotation = ContentFontStyle.class, attribute = "fontName")
        String font() default "";

        @FesodMarked.AliasFor(annotation = ContentFontStyle.class, attribute = "fontHeightInPoints")
        short size() default -1;
    }

    /**
     * Aliases {@code rows} to {@link ContentLoopMerge#eachRow()},
     *         {@code cols} to {@link ContentLoopMerge#columnExtend()}.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentLoopMerge
    @Inherited
    public @interface CompositeContentLoopMergeAliasFor {
        @FesodMarked.AliasFor(annotation = ContentLoopMerge.class, attribute = "eachRow")
        int rows() default 1;

        @FesodMarked.AliasFor(annotation = ContentLoopMerge.class, attribute = "columnExtend")
        int cols() default 1;
    }

    /**
     * Aliases {@code height} to {@link HeadRowHeight#value()}.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadRowHeight
    @Inherited
    public @interface CompositeHeadRowHeightAliasFor {
        @FesodMarked.AliasFor(annotation = HeadRowHeight.class, attribute = "value")
        short height() default -1;
    }

    /**
     * Aliases {@code height} to {@link ContentRowHeight#value()}.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentRowHeight
    @Inherited
    public @interface CompositeContentRowHeightAliasFor {
        @FesodMarked.AliasFor(annotation = ContentRowHeight.class, attribute = "value")
        short height() default -1;
    }

    /**
     * Aliases {@code startRow} → {@link OnceAbsoluteMerge#firstRowIndex()},
     *         {@code endRow} → {@link OnceAbsoluteMerge#lastRowIndex()},
     *         {@code startCol} → {@link OnceAbsoluteMerge#firstColumnIndex()},
     *         {@code endCol} → {@link OnceAbsoluteMerge#lastColumnIndex()}.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @OnceAbsoluteMerge
    @Inherited
    public @interface CompositeOnceAbsoluteMergeAliasFor {
        @FesodMarked.AliasFor(annotation = OnceAbsoluteMerge.class, attribute = "firstRowIndex")
        int startRow() default -1;

        @FesodMarked.AliasFor(annotation = OnceAbsoluteMerge.class, attribute = "lastRowIndex")
        int endRow() default -1;

        @FesodMarked.AliasFor(annotation = OnceAbsoluteMerge.class, attribute = "firstColumnIndex")
        int startCol() default -1;

        @FesodMarked.AliasFor(annotation = OnceAbsoluteMerge.class, attribute = "lastColumnIndex")
        int endCol() default -1;
    }

    // ---- Type-Level Composable Annotations ----

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @HeadRowHeight(value = 40)
    @Inherited
    public @interface CompositeHeadRowHeight {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ContentRowHeight(value = 30)
    @Inherited
    public @interface CompositeContentRowHeight {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 0, firstColumnIndex = 0, lastColumnIndex = 1)
    @Inherited
    public @interface CompositeOnceAbsoluteMerge {}
}

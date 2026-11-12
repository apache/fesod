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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.annotation.write.style;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Customizes the font used when writing the content (data) cells.
 * <p>
 * Declare this annotation on a field to style a single column, or on a class to style every field in
 * that class. An attribute left at its default value ({@code -1}, an empty name, or
 * {@link BooleanEnum#DEFAULT}) is treated as "not set" and is therefore ignored.
 *
 * @see ContentStyle
 * @see HeadFontStyle
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ContentFontStyle {

    /**
     * The name of the font, for example {@code Arial}.
     */
    String fontName() default "";

    /**
     * The height of the font, in points.
     */
    short fontHeightInPoints() default -1;

    /**
     * Whether the text is rendered in italics.
     */
    BooleanEnum italic() default BooleanEnum.DEFAULT;

    /**
     * Whether a horizontal strikeout line is drawn through the text.
     */
    BooleanEnum strikeout() default BooleanEnum.DEFAULT;

    /**
     * The color of the font, as an indexed color value.
     *
     * @see Font#COLOR_NORMAL
     * @see Font#COLOR_RED
     * @see HSSFPalette#getColor(short)
     * @see IndexedColors
     */
    short color() default -1;

    /**
     * The type of offset to use, i.e. normal, superscript or subscript.
     *
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    short typeOffset() default -1;

    /**
     * The type of underline to use.
     *
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    byte underline() default -1;

    /**
     * The character set to use.
     *
     * @see FontCharset
     * @see Font#ANSI_CHARSET
     * @see Font#DEFAULT_CHARSET
     * @see Font#SYMBOL_CHARSET
     */
    int charset() default -1;

    /**
     * Whether the text is rendered in bold.
     */
    BooleanEnum bold() default BooleanEnum.DEFAULT;
}

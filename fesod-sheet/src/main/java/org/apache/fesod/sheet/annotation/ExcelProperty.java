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

package org.apache.fesod.sheet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.sheet.converters.AutoConverter;
import org.apache.fesod.sheet.converters.Converter;

/**
 * Maps a Java field to a spreadsheet column, controlling the header name, column position,
 * sort order, and the converter used to read or write its value.
 *
 * <p>This annotation is only applicable to fields and is retained at runtime, so it can be
 * discovered reflectively by the read/write pipeline.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelProperty {

    /**
     * The header name(s) of the column. Use multiple values for a multi-level (merged) header.
     *
     * <p>write: adjacent identical levels are automatically merged when more than one head is present.
     * <p>read: the last value is used as the matching key when multiple heads are present.
     *
     * @return the header name(s) of the column, empty by default
     */
    String[] value() default {""};

    /**
     * The zero-based index of the column to read from or write to.
     *
     * <p>When equal to -1 (the default), the column is not fixed by index and is ordered by
     * {@link #order()} or, if that is unset, by the declaration order in the Java class.
     *
     * <p>Resolution priority: index &gt; order &gt; default sort.
     *
     * @return the column index, or -1 when not explicitly fixed
     */
    int index() default -1;

    /**
     * The sort order of the column when {@link #index()} is not used.
     *
     * <p>Smaller values come first. Defaults to {@link Integer#MAX_VALUE}, meaning the field keeps
     * the natural declaration order of the Java class.
     *
     * <p>Resolution priority: index &gt; order &gt; default sort.
     *
     * @return the sort order of the column
     */
    int order() default Integer.MAX_VALUE;

    /**
     * The converter forced on this field, bypassing the default converter resolution.
     *
     * <p>Defaults to {@link AutoConverter}, which lets the framework choose a converter based on
     * the field's Java type.
     *
     * @return the converter class to use for this field
     */
    Class<? extends Converter<?>> converter() default AutoConverter.class;
}

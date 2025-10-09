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

package org.apache.fesod.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.excel.annotation.format.DateTimeFormat;
import org.apache.fesod.excel.converters.AutoConverter;
import org.apache.fesod.excel.converters.Converter;

/**
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelProperty {

    /**
     * The name of the sheet header.
     *
     * <p>
     * write: It automatically merges when you have more than one head
     * <p>
     * read: When you have multiple heads, take the last one
     *
     * @return The name of the sheet header
     */
    String[] value() default {""};

    /**
     * Index of column
     *
     * Read or write it on the index of column, If it's equal to -1, it's sorted by Java class.
     *
     * priority: index &gt; order &gt; default sort
     *
     * @return Index of column
     */
    int index() default -1;

    /**
     * Defines the sort order for an column.
     *
     * priority: index &gt; order &gt; default sort
     *
     * @return Order of column
     */
    int order() default Integer.MAX_VALUE;

    /**
     * Force the current field to use this converter.
     *
     * @return Converter
     */
    Class<? extends Converter<?>> converter() default AutoConverter.class;

    /**
     *
     * default @see org.apache.fesod.excel.util.TypeUtil if default is not meet you can set format
     *
     * @return Format string
     * @deprecated please use {@link DateTimeFormat}
     */
    @Deprecated
    String format() default "";
}

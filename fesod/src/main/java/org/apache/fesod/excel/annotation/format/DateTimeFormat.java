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

package org.apache.fesod.excel.annotation.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.excel.enums.BooleanEnum;

/**
 * Convert date format.
 *
 * <p>
 * write: It can be used on classes {@link java.util.Date}
 * <p>
 * read: It can be used on classes {@link String}
 *
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DateTimeFormat {

    /**
     *
     * Specific format reference {@link java.text.SimpleDateFormat}
     *
     * @return Format pattern
     */
    String value() default "";

    /**
     * True if date uses 1904 windowing, or false if using 1900 date windowing.
     *
     * @return True if date uses 1904 windowing, or false if using 1900 date windowing.
     */
    BooleanEnum use1904windowing() default BooleanEnum.DEFAULT;
}

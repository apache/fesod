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

package org.apache.fesod.sheet.annotation.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

/**
 * Specifies the numeric format of a field for reading and writing.
 *
 * <p>When writing, it can be used on fields of any type that extends {@link Number};
 * when reading, it can be used on fields of type {@link String}.
 *
 * @see java.text.DecimalFormat
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NumberFormat {

    /**
     * The numeric format pattern.
     * <p>
     * See {@link java.text.DecimalFormat} for the supported pattern syntax.
     * When empty, the default format determined by the conversion logic is used.
     *
     * @return the format pattern
     */
    String value() default "";

    /**
     * The rounding mode used when writing numeric values.
     * <p>
     * Defaults to {@link RoundingMode#HALF_UP} (round half up, the common
     * commercial rounding behavior).
     *
     * @return the rounding mode
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;
}

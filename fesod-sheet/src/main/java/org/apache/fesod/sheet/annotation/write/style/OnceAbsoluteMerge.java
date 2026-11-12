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

/**
 * Declares a single absolute cell merge region on the annotated head class.
 * <p>
 * The region is defined by the inclusive range from
 * {@link #firstRowIndex()}/{@link #firstColumnIndex()} to
 * {@link #lastRowIndex()}/{@link #lastColumnIndex()}. A value of {@code -1}
 * means the coordinate is unset.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OnceAbsoluteMerge {

    /**
     * The index of the first row (inclusive) of the merge region.
     *
     * @return the first row index, or {@code -1} if not set
     */
    int firstRowIndex() default -1;

    /**
     * The index of the last row (inclusive) of the merge region.
     *
     * @return the last row index, or {@code -1} if not set
     */
    int lastRowIndex() default -1;

    /**
     * The index of the first column (inclusive) of the merge region.
     *
     * @return the first column index, or {@code -1} if not set
     */
    int firstColumnIndex() default -1;

    /**
     * The index of the last column (inclusive) of the merge region.
     *
     * @return the last column index, or {@code -1} if not set
     */
    int lastColumnIndex() default -1;
}

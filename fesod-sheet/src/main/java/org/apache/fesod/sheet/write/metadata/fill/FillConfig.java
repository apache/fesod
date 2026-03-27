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

package org.apache.fesod.sheet.write.metadata.fill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.sheet.enums.FillMergeStrategy;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;

/**
 * Fill config
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillConfig {
    private WriteDirectionEnum direction;
    /**
     * Create a new row each time you use the list parameter.The default create if necessary.
     * <p>
     * Warning:If you use <code>forceNewRow</code> set true, will not be able to use asynchronous write file, simply
     * say the whole file will be stored in memory.
     */
    private Boolean forceNewRow;

    /**
     * Automatically inherit style
     *
     * default true.
     */
    private Boolean autoStyle;

    /**
     * Strategy for handle merged regions during loop filling.
     * <p>
     * This strategy applies <b>ONLY</b> when using {@link WriteDirectionEnum#VERTICAL} fill direction with
     * collection-based data.
     * </p>
     * <p>
     * If used with {@link WriteDirectionEnum#HORIZONTAL}, these strategies (except {@link FillMergeStrategy#NONE})
     * will throw an exception.
     * </p>
     * Defaults {@link FillMergeStrategy#NONE}.
     */
    private FillMergeStrategy mergeStrategy;

    private boolean hasInit;

    public void init() {
        if (hasInit) {
            return;
        }
        if (direction == null) {
            direction = WriteDirectionEnum.VERTICAL;
        }
        if (forceNewRow == null) {
            forceNewRow = Boolean.FALSE;
        }
        if (autoStyle == null) {
            autoStyle = Boolean.TRUE;
        }
        if (mergeStrategy == null) {
            mergeStrategy = FillMergeStrategy.NONE;
        }

        validateConfigConstraint();
        hasInit = true;
    }

    private void validateConfigConstraint() {
        if (direction == WriteDirectionEnum.HORIZONTAL && mergeStrategy != FillMergeStrategy.NONE) {
            throw new IllegalArgumentException("Conflict detected: Multi-row merge strategy (" + mergeStrategy + ") "
                    + "is NOT supported when fill direction is HORIZONTAL.");
        }
    }
}

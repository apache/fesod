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

package org.apache.fesod.sheet.metadata.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;

/**
 * Configuration from annotations
 *
 *
 */
@Getter
@AllArgsConstructor
public class OnceAbsoluteMergeProperty {
    /**
     * First row
     */
    private int firstRowIndex;
    /**
     * Last row
     */
    private int lastRowIndex;
    /**
     * First column
     */
    private int firstColumnIndex;
    /**
     * Last row
     */
    private int lastColumnIndex;

    /**
     * @deprecated This setter will be removed in a future release to make the class immutable.
     */
    @Deprecated
    public void setFirstRowIndex(int firstRowIndex) {
        this.firstRowIndex = firstRowIndex;
    }

    /**
     * @deprecated This setter will be removed in a future release to make the class immutable.
     */
    @Deprecated
    public void setLastRowIndex(int lastRowIndex) {
        this.lastRowIndex = lastRowIndex;
    }

    /**
     * @deprecated This setter will be removed in a future release to make the class immutable.
     */
    @Deprecated
    public void setFirstColumnIndex(int firstColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
    }

    /**
     * @deprecated This setter will be removed in a future release to make the class immutable.
     */
    @Deprecated
    public void setLastColumnIndex(int lastColumnIndex) {
        this.lastColumnIndex = lastColumnIndex;
    }

    public static OnceAbsoluteMergeProperty build(OnceAbsoluteMerge onceAbsoluteMerge) {
        if (onceAbsoluteMerge == null) {
            return null;
        }
        return new OnceAbsoluteMergeProperty(
                onceAbsoluteMerge.firstRowIndex(),
                onceAbsoluteMerge.lastRowIndex(),
                onceAbsoluteMerge.firstColumnIndex(),
                onceAbsoluteMerge.lastColumnIndex());
    }
}

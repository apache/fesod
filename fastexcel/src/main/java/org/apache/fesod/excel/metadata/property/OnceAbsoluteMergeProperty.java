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

package org.apache.fesod.excel.metadata.property;

import org.apache.fesod.excel.annotation.write.style.OnceAbsoluteMerge;

/**
 * Configuration from annotations
 *
 *
 */
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

    public OnceAbsoluteMergeProperty(int firstRowIndex, int lastRowIndex, int firstColumnIndex, int lastColumnIndex) {
        this.firstRowIndex = firstRowIndex;
        this.lastRowIndex = lastRowIndex;
        this.firstColumnIndex = firstColumnIndex;
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

    public int getFirstRowIndex() {
        return firstRowIndex;
    }

    public void setFirstRowIndex(int firstRowIndex) {
        this.firstRowIndex = firstRowIndex;
    }

    public int getLastRowIndex() {
        return lastRowIndex;
    }

    public void setLastRowIndex(int lastRowIndex) {
        this.lastRowIndex = lastRowIndex;
    }

    public int getFirstColumnIndex() {
        return firstColumnIndex;
    }

    public void setFirstColumnIndex(int firstColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
    }

    public int getLastColumnIndex() {
        return lastColumnIndex;
    }

    public void setLastColumnIndex(int lastColumnIndex) {
        this.lastColumnIndex = lastColumnIndex;
    }
}

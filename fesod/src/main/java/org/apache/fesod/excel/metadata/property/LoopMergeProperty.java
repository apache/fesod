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

import org.apache.fesod.excel.annotation.write.style.ContentLoopMerge;

/**
 * Configuration from annotations
 *
 *
 */
public class LoopMergeProperty {
    /**
     * Each row
     */
    private int eachRow;
    /**
     * Extend column
     */
    private int columnExtend;

    public LoopMergeProperty(int eachRow, int columnExtend) {
        this.eachRow = eachRow;
        this.columnExtend = columnExtend;
    }

    public static LoopMergeProperty build(ContentLoopMerge contentLoopMerge) {
        if (contentLoopMerge == null) {
            return null;
        }
        return new LoopMergeProperty(contentLoopMerge.eachRow(), contentLoopMerge.columnExtend());
    }

    public int getEachRow() {
        return eachRow;
    }

    public void setEachRow(int eachRow) {
        this.eachRow = eachRow;
    }

    public int getColumnExtend() {
        return columnExtend;
    }

    public void setColumnExtend(int columnExtend) {
        this.columnExtend = columnExtend;
    }
}

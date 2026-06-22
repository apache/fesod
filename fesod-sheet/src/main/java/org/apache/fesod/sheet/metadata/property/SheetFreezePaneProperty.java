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

package org.apache.fesod.sheet.metadata.property;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.write.style.FreezePane;

/**
 * Property configuration for an Excel sheet freeze pane.
 * <p>
 * This class holds the configuration details required to create a freeze pane.
 * It is typically built from the {@link FreezePane} annotation.
 */
@Getter
@Setter
@EqualsAndHashCode
public class SheetFreezePaneProperty {

    /**
     * Horizontal position of split.
     */
    private int colSplit;

    /**
     * Vertical position of split.
     */
    private int rowSplit;

    /**
     * Left column visible in right pane.
     */
    private int leftmostColumn;

    /**
     * Top row visible in bottom pane
     */
    private int topRow;

    public static SheetFreezePaneProperty build(FreezePane freezePane) {
        if (freezePane == null) {
            return null;
        }
        SheetFreezePaneProperty result = new SheetFreezePaneProperty();
        result.setColSplit(freezePane.colSplit());
        result.setRowSplit(freezePane.rowSplit());
        result.setLeftmostColumn(getOrDefault(freezePane.leftmostColumn(), freezePane.colSplit()));
        result.setTopRow(getOrDefault(freezePane.topRow(), freezePane.rowSplit()));
        return result;
    }

    private static Integer getOrDefault(Integer value, Integer defaultValue) {
        if (value == -1) {
            return defaultValue;
        }
        return value;
    }
}

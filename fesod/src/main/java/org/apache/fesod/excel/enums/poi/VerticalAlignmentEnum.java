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

package org.apache.fesod.excel.enums.poi;

import lombok.Getter;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * This enumeration value indicates the type of vertical alignment for a cell, i.e.,
 * whether it is aligned top, bottom, vertically centered, justified or distributed.
 *
 * <!-- FIXME: Identical to {@link org.apache.poi.ss.usermodel.VerticalAlignment}. Should merge these to
 * {@link org.apache.poi.common.usermodel}.VerticalAlignment in the future. -->
 *
 *
 */
@Getter
public enum VerticalAlignmentEnum {
    /**
     * null
     */
    DEFAULT(null),
    /**
     * The vertical alignment is aligned-to-top.
     */
    TOP(VerticalAlignment.TOP),

    /**
     * The vertical alignment is centered across the height of the cell.
     */
    CENTER(VerticalAlignment.CENTER),

    /**
     * The vertical alignment is aligned-to-bottom. (typically the default value)
     */
    BOTTOM(VerticalAlignment.BOTTOM),

    /**
     * <p>
     * When text direction is horizontal: the vertical alignment of lines of text is distributed vertically,
     * where each line of text inside the cell is evenly distributed across the height of the cell,
     * with flush top and bottom margins.
     * </p>
     * <p>
     * When text direction is vertical: similar behavior as horizontal justification.
     * The alignment is justified (flush top and bottom in this case). For each line of text, each
     * line of the wrapped text in a cell is aligned to the top and bottom (except the last line).
     * If no single line of text wraps in the cell, then the text is not justified.
     * </p>
     */
    JUSTIFY(VerticalAlignment.JUSTIFY),

    /**
     * <p>
     * When text direction is horizontal: the vertical alignment of lines of text is distributed vertically,
     * where each line of text inside the cell is evenly distributed across the height of the cell,
     * with flush top
     * </p>
     * <p>
     * When text direction is vertical: behaves exactly as distributed horizontal alignment.
     * The first words in a line of text (appearing at the top of the cell) are flush
     * with the top edge of the cell, and the last words of a line of text are flush with the bottom edge of the cell,
     * and the line of text is distributed evenly from top to bottom.
     * </p>
     */
    DISTRIBUTED(VerticalAlignment.DISTRIBUTED);

    VerticalAlignment poiVerticalAlignmentEnum;

    VerticalAlignmentEnum(VerticalAlignment poiVerticalAlignmentEnum) {
        this.poiVerticalAlignmentEnum = poiVerticalAlignmentEnum;
    }
}

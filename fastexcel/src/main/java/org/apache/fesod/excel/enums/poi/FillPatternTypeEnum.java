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
import org.apache.poi.ss.usermodel.FillPatternType;

/**
 * The enumeration value indicating the style of fill pattern being used for a cell format.
 *
 *
 */
@Getter
public enum FillPatternTypeEnum {

    /**
     * null
     */
    DEFAULT(null),

    /**
     * No background
     */
    NO_FILL(FillPatternType.NO_FILL),

    /**
     * Solidly filled
     */
    SOLID_FOREGROUND(FillPatternType.SOLID_FOREGROUND),

    /**
     * Small fine dots
     */
    FINE_DOTS(FillPatternType.FINE_DOTS),

    /**
     * Wide dots
     */
    ALT_BARS(FillPatternType.ALT_BARS),

    /**
     * Sparse dots
     */
    SPARSE_DOTS(FillPatternType.SPARSE_DOTS),

    /**
     * Thick horizontal bands
     */
    THICK_HORZ_BANDS(FillPatternType.THICK_HORZ_BANDS),

    /**
     * Thick vertical bands
     */
    THICK_VERT_BANDS(FillPatternType.THICK_VERT_BANDS),

    /**
     * Thick backward facing diagonals
     */
    THICK_BACKWARD_DIAG(FillPatternType.THICK_BACKWARD_DIAG),

    /**
     * Thick forward facing diagonals
     */
    THICK_FORWARD_DIAG(FillPatternType.THICK_FORWARD_DIAG),

    /**
     * Large spots
     */
    BIG_SPOTS(FillPatternType.BIG_SPOTS),

    /**
     * Brick-like layout
     */
    BRICKS(FillPatternType.BRICKS),

    /**
     * Thin horizontal bands
     */
    THIN_HORZ_BANDS(FillPatternType.THIN_HORZ_BANDS),

    /**
     * Thin vertical bands
     */
    THIN_VERT_BANDS(FillPatternType.THIN_VERT_BANDS),

    /**
     * Thin backward diagonal
     */
    THIN_BACKWARD_DIAG(FillPatternType.THIN_BACKWARD_DIAG),

    /**
     * Thin forward diagonal
     */
    THIN_FORWARD_DIAG(FillPatternType.THIN_FORWARD_DIAG),

    /**
     * Squares
     */
    SQUARES(FillPatternType.SQUARES),

    /**
     * Diamonds
     */
    DIAMONDS(FillPatternType.DIAMONDS),

    /**
     * Less Dots
     */
    LESS_DOTS(FillPatternType.LESS_DOTS),

    /**
     * Least Dots
     */
    LEAST_DOTS(FillPatternType.LEAST_DOTS);

    FillPatternType poiFillPatternType;

    FillPatternTypeEnum(FillPatternType poiFillPatternType) {
        this.poiFillPatternType = poiFillPatternType;
    }
}

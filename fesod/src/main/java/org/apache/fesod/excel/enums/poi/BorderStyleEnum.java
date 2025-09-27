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
import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * The enumeration value indicating the line style of a border in a cell,
 * i.e., whether it is bordered dash dot, dash dot dot, dashed, dotted, double, hair, medium,
 * medium dash dot, medium dash dot dot, medium dashed, none, slant dash dot, thick or thin.
 *
 *
 */
@Getter
public enum BorderStyleEnum {
    /**
     * null
     */
    DEFAULT(null),

    /**
     * No border (default)
     */
    NONE(BorderStyle.NONE),

    /**
     * Thin border
     */
    THIN(BorderStyle.THIN),

    /**
     * Medium border
     */
    MEDIUM(BorderStyle.MEDIUM),

    /**
     * dash border
     */
    DASHED(BorderStyle.DASHED),

    /**
     * dot border
     */
    DOTTED(BorderStyle.DOTTED),

    /**
     * Thick border
     */
    THICK(BorderStyle.THICK),

    /**
     * double-line border
     */
    DOUBLE(BorderStyle.DOUBLE),

    /**
     * hair-line border
     */
    HAIR(BorderStyle.HAIR),

    /**
     * Medium dashed border
     */
    MEDIUM_DASHED(BorderStyle.MEDIUM_DASHED),

    /**
     * dash-dot border
     */
    DASH_DOT(BorderStyle.DASH_DOT),

    /**
     * medium dash-dot border
     */
    MEDIUM_DASH_DOT(BorderStyle.MEDIUM_DASH_DOT),

    /**
     * dash-dot-dot border
     */
    DASH_DOT_DOT(BorderStyle.DASH_DOT_DOT),

    /**
     * medium dash-dot-dot border
     */
    MEDIUM_DASH_DOT_DOT(BorderStyle.MEDIUM_DASH_DOT_DOT),

    /**
     * slanted dash-dot border
     */
    SLANTED_DASH_DOT(BorderStyle.SLANTED_DASH_DOT);

    BorderStyle poiBorderStyle;

    BorderStyleEnum(BorderStyle poiBorderStyle) {
        this.poiBorderStyle = poiBorderStyle;
    }
}

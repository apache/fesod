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

package org.apache.fesod.sheet.metadata.data;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.AbstractCell;

/**
 * Excel internal cell data.
 *
 * <p>
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CellData<T> extends AbstractCell {
    /**
     * Marker Excel writes for a {@code true} boolean cell value ({@code <v>1</v>} for {@code t="b"} cells).
     */
    private static final String TRUE_NUMBER = "1";

    /**
     * cell type
     */
    private CellDataTypeEnum type;
    /**
     * {@link CellDataTypeEnum#NUMBER}
     */
    private BigDecimal numberValue;
    /**
     * {@link CellDataTypeEnum#STRING} and{@link CellDataTypeEnum#ERROR}
     */
    private String stringValue;
    /**
     * {@link CellDataTypeEnum#BOOLEAN}
     */
    private Boolean booleanValue;

    /**
     * The resulting converted data.
     */
    private T data;

    /**
     * formula
     */
    private FormulaData formulaData;

    /**
     * Sets {@link #booleanValue} from the raw {@code <v>} text of a boolean ({@code t="b"}) xlsx cell.
     *
     * <p>Excel encodes boolean cells using the numeric markers {@code 0}/{@code 1}, so only the exact
     * string {@code "1"} maps to {@code true}; any other input (including {@code "true"}) maps to
     * {@code false}. This mirrors Apache POI's {@code XSSFCell.getBooleanCellValue()}.
     *
     * @param str the raw cell value text
     */
    public void setBooleanValueFromString(String str) {
        this.booleanValue = TRUE_NUMBER.equals(str);
    }

    /**
     * Ensure that the object does not appear null
     */
    public void checkEmpty() {
        if (type == null) {
            type = CellDataTypeEnum.EMPTY;
        }
        switch (type) {
            case STRING:
            case DIRECT_STRING:
            case ERROR:
                if (StringUtils.isEmpty(stringValue)) {
                    type = CellDataTypeEnum.EMPTY;
                }
                return;
            case NUMBER:
                if (numberValue == null) {
                    type = CellDataTypeEnum.EMPTY;
                }
                return;
            case BOOLEAN:
                if (booleanValue == null) {
                    type = CellDataTypeEnum.EMPTY;
                }
                return;
            default:
        }
    }
}

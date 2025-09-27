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

package org.apache.fesod.excel.enums;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.apache.fesod.excel.metadata.data.ReadCellData;

/**
 * Read not to {@code org.apache.fesod.excel.metadata.BasicParameter#clazz} value, the default will return type.
 *
 *
 */
public enum ReadDefaultReturnEnum {
    /**
     * default.The content of cells into string, is the same as you see in the excel.
     */
    STRING,

    /**
     * Returns the actual type.
     * Will be automatically selected according to the cell contents what return type, will return the following class:
     * <ol>
     *     <li>{@link BigDecimal}</li>
     *     <li>{@link Boolean}</li>
     *     <li>{@link String}</li>
     *     <li>{@link LocalDateTime}</li>
     * </ol>
     */
    ACTUAL_DATA,

    /**
     * Return to {@link ReadCellData}, can decide which field you need.
     */
    READ_CELL_DATA
}

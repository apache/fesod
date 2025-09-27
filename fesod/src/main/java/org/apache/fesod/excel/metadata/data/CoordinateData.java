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

package org.apache.fesod.excel.metadata.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * coordinate.
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class CoordinateData {
    /**
     * first row index.Priority is higher than {@link #relativeFirstRowIndex}.
     */
    private Integer firstRowIndex;
    /**
     * first column index.Priority is higher than {@link #relativeFirstColumnIndex}.
     */
    private Integer firstColumnIndex;
    /**
     * last row index.Priority is higher than {@link #relativeLastRowIndex}.
     */
    private Integer lastRowIndex;
    /**
     * last column index.Priority is higher than {@link #relativeLastColumnIndex}.
     */
    private Integer lastColumnIndex;

    /**
     * relative first row index
     */
    private Integer relativeFirstRowIndex;
    /**
     * relative first column index
     */
    private Integer relativeFirstColumnIndex;
    /**
     * relative last row index
     */
    private Integer relativeLastRowIndex;
    /**
     *relative  last column index
     */
    private Integer relativeLastColumnIndex;
}

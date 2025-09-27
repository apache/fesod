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

package org.apache.fesod.excel.sort;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelProperty;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class SortData {
    private String column5;
    private String column6;

    @ExcelProperty(order = 100)
    private String column4;

    @ExcelProperty(order = 99)
    private String column3;

    @ExcelProperty(value = "column2", index = 1)
    private String column2;

    @ExcelProperty(value = "column1", index = 0)
    private String column1;
}

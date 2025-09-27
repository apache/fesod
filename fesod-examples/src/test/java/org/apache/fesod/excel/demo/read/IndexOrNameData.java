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

package org.apache.fesod.excel.demo.read;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelProperty;

/**
 * Basic data class
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class IndexOrNameData {
    /**
     * Force reading the third column. It is not recommended to use both index and name at the same time.
     * Either use index only or use name only for matching within a single object.
     */
    @ExcelProperty(index = 2)
    private Double doubleData;
    /**
     * Match by name. Note that if the name is duplicated, only one field will be populated with data.
     */
    @ExcelProperty("String")
    private String string;

    @ExcelProperty("Date")
    private Date date;
}

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

package org.apache.fesod.sheet.examples.read.data;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Data model demonstrating mixed index-based and name-based column matching.
 *
 * <h2>Mapping Strategy</h2>
 * <pre>
 * Field       | Annotation                  | Matches Column
 * ────────────|─────────────────────────────|───────────────
 * doubleData  | @ExcelProperty(index = 2)   | Column 3 (by position)
 * string      | @ExcelProperty("String")    | Header "String" (by name)
 * date        | @ExcelProperty("Date")      | Header "Date" (by name)
 * </pre>
 *
 * <h2>Priority Rules</h2>
 * <p>When both {@code index} and name are specified on the same field, {@code index} wins.
 * The full priority order is: {@code index} &gt; {@code order} &gt; field declaration order.</p>
 *
 * <p><b>Tip:</b> Use index-based matching when the Excel column position is fixed and known.
 * Use name-based matching when users might reorder columns but headers remain consistent.</p>
 *
 * @see org.apache.fesod.sheet.annotation.ExcelProperty
 * @see org.apache.fesod.sheet.examples.read.IndexOrNameReadExample
 */
@Getter
@Setter
@EqualsAndHashCode
public class IndexOrNameData {
    /**
     * Force reading the third column.
     */
    @ExcelProperty(index = 2)
    private Double doubleData;
    /**
     * Match by name.
     */
    @ExcelProperty("String")
    private String string;

    @ExcelProperty("Date")
    private Date date;
}

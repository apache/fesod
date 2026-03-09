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
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Data model for the read examples, mapping Excel columns to Java fields.
 *
 * <p>This class demonstrates the standard pattern for Fesod data models:
 * annotate fields with {@link ExcelProperty} to map them to Excel columns by header name.
 * Use {@link ExcelIgnore} to exclude fields that should not participate in reading or writing.</p>
 *
 * <h2>Column Mapping</h2>
 * <pre>
 * Excel Column:      | String Title | Date Title          | Number Title |
 * Java Field:        | string       | date                | doubleData   |
 * Java Type:         | String       | Date                | Double       |
 * </pre>
 *
 * <p>The {@code ignore} field is excluded from Excel operations via {@code @ExcelIgnore},
 * making it suitable for internal-only data like database IDs or computed values.</p>
 *
 * @see ExcelProperty
 * @see ExcelIgnore
 */
@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    /**
     * String Title
     */
    @ExcelProperty("String Title")
    private String string;

    /**
     * Date Title
     */
    @ExcelProperty("Date Title")
    private Date date;

    /**
     * Number Title
     */
    @ExcelProperty("Number Title")
    private Double doubleData;

    /**
     * Ignore this field
     */
    @ExcelIgnore
    private String ignore;
}

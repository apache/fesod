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

package org.apache.fesod.sheet.examples.quickstart.data;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Data model for the quickstart examples, mapping Java fields to Excel columns.
 *
 * <h2>Column Mapping</h2>
 * <p>Each field annotated with {@link ExcelProperty} maps to an Excel column by header name.
 * The generated Excel file will have these columns:</p>
 * <pre>
 * | String Title | Date Title          | Number Title |
 * |------------- |---------------------|--------------|
 * | (string)     | (date)              | (doubleData) |
 * </pre>
 *
 * <h2>Annotations Used</h2>
 * <ul>
 *   <li>{@link ExcelProperty} — Maps a field to an Excel column by header name or index.</li>
 *   <li>{@link ExcelIgnore} — Excludes a field from both reading and writing.
 *       Useful for internal/transient fields like IDs or computed values.</li>
 * </ul>
 *
 * <h2>Supported Field Types</h2>
 * <p>Fesod automatically converts between Excel cell types and common Java types:
 * {@code String}, {@code Date}, {@code Double}, {@code Integer}, {@code BigDecimal}, etc.
 * For custom conversions, see
 * {@link org.apache.fesod.sheet.examples.read.data.ConverterData}.</p>
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

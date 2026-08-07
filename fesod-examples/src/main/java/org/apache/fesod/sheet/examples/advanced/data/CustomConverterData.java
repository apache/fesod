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

package org.apache.fesod.sheet.examples.advanced.data;

import java.util.Date;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.examples.advanced.converter.CustomStringStringConverter;

/**
 * Data model demonstrating three types of data conversion during Excel read/write.
 *
 * <p>This class uses a custom converter, a date format annotation, and a number format
 * annotation to control how Excel cell values are converted to and from Java objects.</p>
 *
 * <h2>Conversion Mapping</h2>
 * <pre>
 * Field      | Type   | Annotation / Converter                    | Excel Cell → Java Value
 * ───────────|────────|───────────────────────────────────────────|────────────────────────
 * string     | String | @ExcelProperty(converter=Custom...)        | "Hello" → "Custom:Hello"
 * date       | Date   | @DateTimeFormat("yyyy-MM-dd HH:mm:ss")    | 2025-01-01 → Date object
 * doubleData | Double | @NumberFormat("#.##%")                     | 0.56 → 0.56 (displayed as "56%")
 * </pre>
 *
 * <h2>Converter Types</h2>
 * <ul>
 *   <li><b>Custom converter</b> ({@link CustomStringStringConverter}) — Implements
 *       {@link org.apache.fesod.sheet.converters.Converter} for full control over the
 *       transformation logic. Applied via {@code @ExcelProperty(converter = ...)}.</li>
 *   <li><b>Date format</b> ({@code @DateTimeFormat}) — Specifies the date pattern used
 *       when converting between Excel date cells and {@link java.util.Date} objects.</li>
 *   <li><b>Number format</b> ({@code @NumberFormat}) — Specifies the number pattern used
 *       when converting between Excel numeric cells and {@link Double} values.</li>
 * </ul>
 *
 * @see CustomStringStringConverter
 * @see DateTimeFormat
 * @see NumberFormat
 */
@Data
public class CustomConverterData {

    /**
     * Custom converter.
     */
    @ExcelProperty(converter = CustomStringStringConverter.class)
    private String string;

    /**
     * Date format.
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * Number format.
     */
    @NumberFormat("#.##%")
    private Double doubleData;
}

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

package org.apache.fesod.sheet.examples.read.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.examples.read.converters.CustomStringStringConverter;

/**
 * Data model demonstrating three types of data conversion during Excel reading.
 *
 * <p>All fields in this class are {@code String} type, but Fesod applies converters
 * and format annotations to transform the raw Excel cell values before setting them.</p>
 *
 * <h2>Conversion Mapping</h2>
 * <pre>
 * Field      | Annotation / Converter                    | Excel Cell → Java Value
 * ───────────|───────────────────────────────────────────|────────────────────────
 * string     | @ExcelProperty(converter=Custom...)        | "Hello" → "Custom：Hello"
 * date       | @DateTimeFormat("yyyy-MM-dd HH:mm:ss")    | 2025-01-01 → "2025-01-01 00:00:00"
 * doubleData | @NumberFormat("#.##%")                     | 0.56 → "56%"
 * </pre>
 *
 * <h2>Converter Types</h2>
 * <ul>
 *   <li><b>Custom converter</b> ({@link CustomStringStringConverter}) — Implements
 *       {@link org.apache.fesod.sheet.converters.Converter} for full control over the
 *       transformation logic. Applied via {@code @ExcelProperty(converter = ...)}.</li>
 *   <li><b>Date format</b> ({@code @DateTimeFormat}) — Uses {@link java.text.SimpleDateFormat}
 *       patterns to format date cells as strings.</li>
 *   <li><b>Number format</b> ({@code @NumberFormat}) — Uses {@link java.text.DecimalFormat}
 *       patterns to format numeric cells as strings.</li>
 * </ul>
 *
 * @see CustomStringStringConverter
 * @see org.apache.fesod.sheet.annotation.format.DateTimeFormat
 * @see org.apache.fesod.sheet.annotation.format.NumberFormat
 */
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {

    /**
     * Custom converter.
     */
    @ExcelProperty(converter = CustomStringStringConverter.class)
    private String string;

    /**
     * Date format.
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String date;

    /**
     * Number format.
     */
    @NumberFormat("#.##%")
    private String doubleData;
}

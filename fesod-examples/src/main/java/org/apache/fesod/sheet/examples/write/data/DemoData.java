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

package org.apache.fesod.sheet.examples.write.data;

import java.util.Date;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Data model for the write examples, mapping Java fields to Excel columns.
 *
 * <p>Identical structure to the read examples' DemoData, but uses Lombok's
 * {@code @Data} for brevity (generates getters, setters, equals, hashCode, toString).
 * The {@link ExcelProperty} annotations define column headers in the output file,
 * while {@link ExcelIgnore} excludes the {@code ignore} field from the Excel output.</p>
 *
 * <h2>Generated Excel Headers</h2>
 * <pre>
 * | String Title | Date Title | Number Title |
 * </pre>
 *
 * @see ExcelProperty
 * @see ExcelIgnore
 */
@Data
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

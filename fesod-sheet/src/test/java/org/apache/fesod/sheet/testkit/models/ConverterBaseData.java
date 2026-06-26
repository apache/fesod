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

package org.apache.fesod.sheet.testkit.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Abstract base class extracting the 13 common fields shared by
 * {@code ConverterReadData} and {@code ConverterWriteData}.
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class ConverterBaseData {
    @ExcelProperty("Date")
    private Date date;

    @ExcelProperty("Local Date")
    private LocalDate localDate;

    @ExcelProperty("Local Date Time")
    private LocalDateTime localDateTime;

    @ExcelProperty("Boolean")
    private Boolean booleanData;

    @ExcelProperty("Big Decimal")
    private BigDecimal bigDecimal;

    @ExcelProperty("Big Integer")
    private BigInteger bigInteger;

    @ExcelProperty("Long")
    private long longData;

    @ExcelProperty("Integer")
    private Integer integerData;

    @ExcelProperty("Short")
    private Short shortData;

    @ExcelProperty("Byte")
    private Byte byteData;

    @ExcelProperty("Double")
    private double doubleData;

    @ExcelProperty("Float")
    private Float floatData;

    @ExcelProperty("String")
    private String string;
}

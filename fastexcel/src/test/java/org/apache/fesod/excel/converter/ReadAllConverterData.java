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

package org.apache.fesod.excel.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class ReadAllConverterData {
    private BigDecimal bigDecimalBoolean;
    private BigDecimal bigDecimalNumber;
    private BigDecimal bigDecimalString;
    private BigInteger bigIntegerBoolean;
    private BigInteger bigIntegerNumber;
    private BigInteger bigIntegerString;
    private Boolean booleanBoolean;
    private Boolean booleanNumber;
    private Boolean booleanString;
    private Byte byteBoolean;
    private Byte byteNumber;
    private Byte byteString;
    private Date dateNumber;
    private Date dateString;
    private LocalDateTime localDateTimeNumber;
    private LocalDateTime localDateTimeString;
    private Double doubleBoolean;
    private Double doubleNumber;
    private Double doubleString;
    private Float floatBoolean;
    private Float floatNumber;
    private Float floatString;
    private Integer integerBoolean;
    private Integer integerNumber;
    private Integer integerString;
    private Long longBoolean;
    private Long longNumber;
    private Long longString;
    private Short shortBoolean;
    private Short shortNumber;
    private Short shortString;
    private String stringBoolean;
    private String stringNumber;
    private String stringString;
    private String stringError;
    private String stringFormulaNumber;
    private String stringFormulaString;
    private String stringNumberDate;
}

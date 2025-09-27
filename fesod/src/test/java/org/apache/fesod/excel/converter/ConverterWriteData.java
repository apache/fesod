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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.metadata.data.WriteCellData;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class ConverterWriteData {
    @ExcelProperty("日期")
    private Date date;

    @ExcelProperty("本地日期")
    private LocalDate localDate;

    @ExcelProperty("本地日期时间")
    private LocalDateTime localDateTime;

    @ExcelProperty("布尔")
    private Boolean booleanData;

    @ExcelProperty("大数")
    private BigDecimal bigDecimal;

    @ExcelProperty("大整数")
    private BigInteger bigInteger;

    @ExcelProperty("长整型")
    private long longData;

    @ExcelProperty("整型")
    private Integer integerData;

    @ExcelProperty("短整型")
    private Short shortData;

    @ExcelProperty("字节型")
    private Byte byteData;

    @ExcelProperty("双精度浮点型")
    private double doubleData;

    @ExcelProperty("浮点型")
    private Float floatData;

    @ExcelProperty("字符串")
    private String string;

    @ExcelProperty("自定义")
    private WriteCellData<?> cellData;
}

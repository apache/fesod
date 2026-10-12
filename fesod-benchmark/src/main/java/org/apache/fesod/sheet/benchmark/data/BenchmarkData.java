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

package org.apache.fesod.sheet.benchmark.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;

/**
 * Standard benchmark data model with various data types for comprehensive testing
 */
@Data
@NoArgsConstructor
public class BenchmarkData {

    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ExcelProperty(value = "String Data", index = 1)
    private String stringData;

    @ExcelProperty(value = "Integer Value", index = 2)
    private Integer intValue;

    @ExcelProperty(value = "Long Value", index = 3)
    private Long longValue;

    @ExcelProperty(value = "Double Value", index = 4)
    private Double doubleValue;

    @ExcelProperty(value = "BigDecimal Value", index = 5)
    private BigDecimal bigDecimalValue;

    @ExcelProperty(value = "Boolean Flag", index = 6)
    private Boolean booleanFlag;

    @ExcelProperty(value = "Date Value", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate dateValue;

    @ExcelProperty(value = "DateTime Value", index = 8)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeValue;

    @ExcelProperty(value = "Category", index = 9)
    private String category;

    @ExcelProperty(value = "Description", index = 10)
    private String description;

    @ExcelProperty(value = "Status", index = 11)
    private String status;

    @ExcelProperty(value = "Float Value", index = 12)
    private Float floatValue;

    @ExcelProperty(value = "Short Value", index = 13)
    private Short shortValue;

    @ExcelProperty(value = "Byte Value", index = 14)
    private Byte byteValue;

    @ExcelProperty(value = "Extra Data 1", index = 15)
    private String extraData1;

    @ExcelProperty(value = "Extra Data 2", index = 16)
    private String extraData2;

    @ExcelProperty(value = "Extra Data 3", index = 17)
    private String extraData3;

    @ExcelProperty(value = "Extra Data 4", index = 18)
    private String extraData4;

    @ExcelProperty(value = "Extra Data 5", index = 19)
    private String extraData5;
}

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
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;

/**
 * Standard benchmark data model with various data types for comprehensive testing
 */
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

    // Default constructor
    public BenchmarkData() {}

    // Full constructor
    public BenchmarkData(
            Long id,
            String stringData,
            Integer intValue,
            Long longValue,
            Double doubleValue,
            BigDecimal bigDecimalValue,
            Boolean booleanFlag,
            LocalDate dateValue,
            LocalDateTime dateTimeValue,
            String category,
            String description,
            String status,
            Float floatValue,
            Short shortValue,
            Byte byteValue,
            String extraData1,
            String extraData2,
            String extraData3,
            String extraData4,
            String extraData5) {
        this.id = id;
        this.stringData = stringData;
        this.intValue = intValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.bigDecimalValue = bigDecimalValue;
        this.booleanFlag = booleanFlag;
        this.dateValue = dateValue;
        this.dateTimeValue = dateTimeValue;
        this.category = category;
        this.description = description;
        this.status = status;
        this.floatValue = floatValue;
        this.shortValue = shortValue;
        this.byteValue = byteValue;
        this.extraData1 = extraData1;
        this.extraData2 = extraData2;
        this.extraData3 = extraData3;
        this.extraData4 = extraData4;
        this.extraData5 = extraData5;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public Boolean getBooleanFlag() {
        return booleanFlag;
    }

    public void setBooleanFlag(Boolean booleanFlag) {
        this.booleanFlag = booleanFlag;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public String getExtraData1() {
        return extraData1;
    }

    public void setExtraData1(String extraData1) {
        this.extraData1 = extraData1;
    }

    public String getExtraData2() {
        return extraData2;
    }

    public void setExtraData2(String extraData2) {
        this.extraData2 = extraData2;
    }

    public String getExtraData3() {
        return extraData3;
    }

    public void setExtraData3(String extraData3) {
        this.extraData3 = extraData3;
    }

    public String getExtraData4() {
        return extraData4;
    }

    public void setExtraData4(String extraData4) {
        this.extraData4 = extraData4;
    }

    public String getExtraData5() {
        return extraData5;
    }

    public void setExtraData5(String extraData5) {
        this.extraData5 = extraData5;
    }

    @Override
    public String toString() {
        return "BenchmarkData{" + "id="
                + id + ", stringData='"
                + stringData + '\'' + ", intValue="
                + intValue + ", longValue="
                + longValue + ", doubleValue="
                + doubleValue + ", bigDecimalValue="
                + bigDecimalValue + ", booleanFlag="
                + booleanFlag + ", dateValue="
                + dateValue + ", dateTimeValue="
                + dateTimeValue + ", category='"
                + category + '\'' + ", description='"
                + description + '\'' + ", status='"
                + status + '\'' + ", floatValue="
                + floatValue + ", shortValue="
                + shortValue + ", byteValue="
                + byteValue + ", extraData1='"
                + extraData1 + '\'' + ", extraData2='"
                + extraData2 + '\'' + ", extraData3='"
                + extraData3 + '\'' + ", extraData4='"
                + extraData4 + '\'' + ", extraData5='"
                + extraData5 + '\'' + '}';
    }
}

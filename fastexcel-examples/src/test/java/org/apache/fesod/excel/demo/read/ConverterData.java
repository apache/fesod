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

package org.apache.fesod.excel.demo.read;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.annotation.format.DateTimeFormat;
import org.apache.fesod.excel.annotation.format.NumberFormat;

/**
 * Basic data class. The order here is consistent with the order in the Excel file.
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {

    /**
     * I use a custom converter. No matter what is passed from the database, I prepend "Custom:".
     */
    @ExcelProperty(converter = CustomStringStringConverter.class)
    private String string;

    /**
     * I use a string to receive the date so that it can be formatted. I want to receive the date in the format of yyyy-MM-dd HH:mm:ss.
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String date;

    /**
     * I want to receive a number in percentage format.
     */
    @NumberFormat("#.##%")
    private String doubleData;
}

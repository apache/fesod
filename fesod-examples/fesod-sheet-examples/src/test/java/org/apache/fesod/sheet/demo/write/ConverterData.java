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

package org.apache.fesod.sheet.demo.write;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;

/**
 * Basic data class. The sorting here is consistent with the sorting in Excel.
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    /**
     * I want to add "Custom:" to the beginning of all strings.
     */
    @ExcelProperty(value = "String Title", converter = CustomStringStringConverter.class)
    private String string;
    /**
     * I want to write to Excel using the format "yyyy-MM-dd HH:mm:ss"
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("Date Title")
    private Date date;
    /**
     * I want to write to Excel using percentage format.
     */
    @NumberFormat("#.##%")
    @ExcelProperty(value = "Double Title")
    private Double doubleData;
}

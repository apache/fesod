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

package org.apache.fesod.sheet.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;

/**
 * Simple data model for basic read/write operations.
 * Consolidated from various feature-specific packages.
 */
@Getter
@Setter
@EqualsAndHashCode
public class SimpleData {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("数字")
    private Double number;

    @ExcelProperty("整数")
    private Integer integer;

    public SimpleData() {}

    public SimpleData(String name) {
        this.name = name;
    }

    public SimpleData(String name, Double number) {
        this.name = name;
        this.number = number;
    }

    public SimpleData(String name, Double number, Integer integer) {
        this.name = name;
        this.number = number;
        this.integer = integer;
    }

    @Override
    public String toString() {
        return "SimpleData{" + "name='" + name + '\'' + ", number=" + number + ", integer=" + integer + '}';
    }
}

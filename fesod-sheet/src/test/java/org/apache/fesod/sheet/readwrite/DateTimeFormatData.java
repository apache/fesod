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

package org.apache.fesod.sheet.readwrite;

import java.time.LocalDateTime;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;

/**
 * POJO with a {@link LocalDateTime} field annotated with {@code @DateTimeFormat} for the pattern only,
 * leaving {@code use1904windowing} at its {@code DEFAULT} (see issue #1042).
 */
public class DateTimeFormatData {

    @ExcelProperty("time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    public DateTimeFormatData() {}

    public DateTimeFormatData(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}

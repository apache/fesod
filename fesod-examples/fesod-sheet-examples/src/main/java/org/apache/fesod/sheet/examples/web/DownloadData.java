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

package org.apache.fesod.sheet.examples.web;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data model for the web download example.
 *
 * <p>Represents a row in the downloaded Excel file. In production, this would
 * typically be populated from a database query or service call.</p>
 *
 * <p>Headers are generated from field names by default ("string", "date", "doubleData").
 * Add {@code @ExcelProperty("Custom Header")} annotations for user-friendly column names.</p>
 *
 * @see org.apache.fesod.sheet.examples.web.WebExampleController#download(javax.servlet.http.HttpServletResponse)
 */
@Getter
@Setter
@EqualsAndHashCode
public class DownloadData {
    private String string;
    private Date date;
    private Double doubleData;
}

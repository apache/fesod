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

package org.apache.fesod.sheet.examples.read.data;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data model intentionally designed to trigger conversion errors during reading.
 *
 * <p>This class maps all Excel data to a single {@code Date} field. When the Excel file
 * contains string values (e.g., "String Title") that cannot be parsed as dates,
 * Fesod throws an {@link org.apache.fesod.sheet.exception.ExcelDataConvertException}.</p>
 *
 * <p>Used by {@link org.apache.fesod.sheet.examples.read.ExceptionHandlingExample} to
 * demonstrate the {@code onException()} callback in
 * {@link org.apache.fesod.sheet.examples.read.listeners.ExceptionListener}.</p>
 *
 * @see org.apache.fesod.sheet.examples.read.ExceptionHandlingExample
 * @see org.apache.fesod.sheet.exception.ExcelDataConvertException
 */
@Getter
@Setter
@EqualsAndHashCode
public class ExceptionDemoData {
    /**
     * Using a Date to receive a string will cause an error.
     */
    private Date date;
}

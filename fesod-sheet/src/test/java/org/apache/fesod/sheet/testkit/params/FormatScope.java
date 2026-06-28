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

package org.apache.fesod.sheet.testkit.params;

/**
 * Selects which {@link org.apache.fesod.sheet.testkit.enums.ExcelFormat} values an
 * {@link ExcelFormatSource} supplies.
 *
 * <ul>
 *   <li>{@link #ALL} &mdash; every supported format (XLSX, XLS, CSV)</li>
 *   <li>{@link #BINARY} &mdash; the binary workbook formats only (XLSX, XLS); excludes CSV,
 *       which lacks workbook-level features such as encryption, images and templates</li>
 * </ul>
 */
public enum FormatScope {
    ALL,
    BINARY
}

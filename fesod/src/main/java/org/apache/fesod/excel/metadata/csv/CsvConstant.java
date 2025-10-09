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

package org.apache.fesod.excel.metadata.csv;

/**
 * Constant definitions for CSV file processing.
 */
public class CsvConstant {
    /**
     * commonly used character
     */
    public static final char SPACE = ' ';

    public static final char BACKSLASH = '\\';
    public static final char BACKSPACE = '\b';
    public static final char PIPE = '|';
    public static final char DOUBLE_QUOTE = '"';

    /**
     * line break
     */
    public static final String CR = "\r";

    public static final String FF = "\f";
    public static final String LF = "\n";
    public static final String CRLF = "\r\n";

    /**
     * field related
     */
    public static final String TAB = "\t";

    public static final String COMMA = ",";
    public static final String EMPTY = "";
    public static final String AT = "@";

    /**
     * unicode
     */
    public static final String UNICODE_EMPTY = "\u0000";

    public static final String UNICODE_NEX_LINE = "\u0085";
    public static final String UNICODE_LINE_SEPARATOR = "\u2028";

    /**
     * database NULL value
     */
    public static final String SQL_NULL_STRING = "\\N";

    private CsvConstant() {}
}

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

package org.apache.fesod.excel.metadata;

import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CacheLocationEnum;

/**
 * Basic parameter
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class BasicParameter {
    /**
     * You can only choose one of the {@link BasicParameter#head} and {@link BasicParameter#clazz}
     */
    private List<List<String>> head;
    /**
     * You can only choose one of the {@link BasicParameter#head} and {@link BasicParameter#clazz}
     */
    private Class<?> clazz;
    /**
     * Custom type conversions override the default
     */
    private List<Converter<?>> customConverterList;
    /**
     * Automatic trim includes sheet name and content
     */
    private Boolean autoTrim;
    /**
     * true if date uses 1904 windowing, or false if using 1900 date windowing.
     *
     * default is false
     *
     * @return
     */
    private Boolean use1904windowing;
    /**
     * A <code>Locale</code> object represents a specific geographical, political, or cultural region. This parameter is
     * used when formatting dates and numbers.
     */
    private Locale locale;

    /**
     * Whether to use scientific Format.
     *
     * default is false
     */
    private Boolean useScientificFormat;

    /**
     * The cache used when parsing fields such as head.
     *
     * default is THREAD_LOCAL.
     */
    private CacheLocationEnum filedCacheLocation;
    /**
     * Automatic strip includes sheet name and content
     */
    private Boolean autoStrip;
}

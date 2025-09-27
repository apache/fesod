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
import java.util.Objects;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CacheLocationEnum;
import org.apache.fesod.excel.util.ListUtils;

/**
 * ExcelBuilder
 *
 *
 */
public abstract class AbstractParameterBuilder<T extends AbstractParameterBuilder, C extends BasicParameter> {
    /**
     * You can only choose one of the {@link #head(List)} and {@link #head(Class)}
     *
     * @param head
     * @return
     */
    public T head(List<List<String>> head) {
        parameter().setHead(head);
        return self();
    }

    /**
     * You can only choose one of the {@link #head(List)} and {@link #head(Class)}
     *
     * @param clazz
     * @return
     */
    public T head(Class<?> clazz) {
        parameter().setClazz(clazz);
        return self();
    }

    public T headIfNotNull(Class<?> clazz) {
        if (Objects.nonNull(clazz)) {
            parameter().setClazz(clazz);
        }
        return self();
    }

    /**
     * Custom type conversions override the default.
     *
     * @param converter
     * @return
     */
    public T registerConverter(Converter<?> converter) {
        if (parameter().getCustomConverterList() == null) {
            parameter().setCustomConverterList(ListUtils.newArrayList());
        }
        parameter().getCustomConverterList().add(converter);
        return self();
    }

    /**
     * true if date uses 1904 windowing, or false if using 1900 date windowing.
     * <p>
     * default is false
     *
     * @param use1904windowing
     * @return
     */
    public T use1904windowing(Boolean use1904windowing) {
        parameter().setUse1904windowing(use1904windowing);
        return self();
    }

    /**
     * A <code>Locale</code> object represents a specific geographical, political, or cultural region. This parameter is
     * used when formatting dates and numbers.
     *
     * @param locale
     * @return
     */
    public T locale(Locale locale) {
        parameter().setLocale(locale);
        return self();
    }

    /**
     * The cache used when parsing fields such as head.
     * <p>
     * default is THREAD_LOCAL.
     *
     * @since 3.3.0
     */
    public T filedCacheLocation(CacheLocationEnum filedCacheLocation) {
        parameter().setFiledCacheLocation(filedCacheLocation);
        return self();
    }

    /**
     * Automatic trim includes sheet name and content
     *
     * @param autoTrim
     * @return
     */
    public T autoTrim(Boolean autoTrim) {
        parameter().setAutoTrim(autoTrim);
        return self();
    }

    /**
     * Automatic strip includes sheet name and content
     *
     * @param autoStrip
     * @return
     */
    public T autoStrip(Boolean autoStrip) {
        parameter().setAutoStrip(autoStrip);
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    /**
     * Get parameter
     *
     * @return
     */
    protected abstract C parameter();
}

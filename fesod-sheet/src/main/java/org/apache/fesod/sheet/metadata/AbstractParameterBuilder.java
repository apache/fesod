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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CacheLocationEnum;

/**
 * Common fluent base builder shared by every Fesod read/write parameter builder.
 *
 * <p>Options configured here (head definition, converters, locale, date windowing, field cache
 * location, auto trim, …) apply to both reading and writing; write- or read-specific options live
 * in the concrete subclasses such as
 * {@code AbstractExcelWriterParameterBuilder}.</p>
 *
 * <p>All setters mutate the underlying {@link C} returned by {@link #parameter()} and then return
 * {@code self()} so calls can be chained fluently.</p>
 *
 * @param <T> the concrete builder type, used for fluent self-chaining
 * @param <C> the concrete {@link BasicParameter} subtype this builder mutates
 */
public abstract class AbstractParameterBuilder<T extends AbstractParameterBuilder, C extends BasicParameter> {

    /**
     * Configure sheet headers dynamically using a raw {@code List<List<String>>}.
     *
     * <p>
     * <strong>Note:</strong> Use of this method is mutually exclusive with {@link #head(Class)}.
     * </p>
     *
     * @param head the raw header data list
     * @see #head(Consumer)
     * @return this builder
     */
    public T head(List<List<String>> head) {
        parameter().setHead(toMutableListIfNecessary(head));
        return self();
    }

    /**
     * Configure sheet headers dynamically using a {@code HeadBuilder} consumer.
     *
     * <p>
     * <strong>Note:</strong> Use of this method is mutually exclusive with {@link #head(Class)}.
     * </p>
     *
     * @param headBuilderConsumer the consumer to configure the headers
     * @see #head(List)
     * @return this builder
     */
    public T head(Consumer<HeadBuilder> headBuilderConsumer) {
        parameter().setHead(DefaultHeadBuilder.define(headBuilderConsumer));
        return self();
    }

    /**
     * Ensures and returns a mutable head list.
     *
     * @param head The source list to create a mutable from.
     * @return A new mutable list, or the original list if the input is null or empty.
     */
    private List<List<String>> toMutableListIfNecessary(List<List<String>> head) {
        if (null == head || head.isEmpty()) {
            return head;
        }
        List<List<String>> result = new ArrayList<>();
        for (List<String> headColumn : head) {
            result.add(new ArrayList<>(headColumn));
        }
        return result;
    }

    /**
     * Configure sheet headers using a Java model.
     *
     * <p>
     * <strong>Note:</strong> Use of this method is mutually exclusive with
     * {@link #head(List)} and {@link #head(Consumer)}.
     * </p>
     *
     * @param clazz the Java model class
     * @return this builder
     */
    public T head(Class<?> clazz) {
        parameter().setClazz(clazz);
        return self();
    }

    /**
     * Sets the model class only when {@code clazz} is non-{@code null}; otherwise this is a no-op.
     * Useful when the class is optional and you want to avoid a {@code null} head overwriting an
     * earlier value.
     *
     * @param clazz the model class to derive the head from; if {@code null} the current value is
     *              preserved
     * @return this builder instance, for chaining
     * @see #head(Class)
     */
    public T headIfNotNull(Class<?> clazz) {
        if (Objects.nonNull(clazz)) {
            parameter().setClazz(clazz);
        }
        return self();
    }

    /**
     * Registers a custom {@link Converter} that will be consulted before Fesod's built-in
     * converters. Handlers accumulate in registration order.
     *
     * @param converter the converter to register; passing {@code null} appends a null entry and
     *                  is not supported by the runtime, so callers should never do so
     * @return this builder instance, for chaining
     */
    public T registerConverter(Converter<?> converter) {
        if (parameter().getCustomConverterList() == null) {
            parameter().setCustomConverterList(ListUtils.newArrayList());
        }
        parameter().getCustomConverterList().add(converter);
        return self();
    }

    /**
     * Chooses the Excel date epoch used when converting {@link java.util.Date} values.
     * Pass {@code true} to use the 1904 windowing system, {@code false} to use the more common
     * 1900 windowing system. The default value is {@code false}.
     *
     * @param use1904windowing whether to interpret date serial numbers relative to 1904
     * @return this builder instance, for chaining
     */
    public T use1904windowing(Boolean use1904windowing) {
        parameter().setUse1904windowing(use1904windowing);
        return self();
    }

    /**
     * Sets the {@link Locale} used when formatting dates and numbers during reading or writing.
     *
     * @param locale the locale to apply; {@code null} keeps the platform default
     * @return this builder instance, for chaining
     */
    public T locale(Locale locale) {
        parameter().setLocale(locale);
        return self();
    }

    /**
     * Sets where the reflection cache for field metadata (such as head resolution) is stored.
     * The default value is {@link CacheLocationEnum#THREAD_LOCAL}.
     *
     * @param filedCacheLocation the cache location to use; the parameter name keeps the historical
     *                           "filed" spelling for API compatibility, but it refers to the
     *                           <em>field</em> cache location
     * @return this builder instance, for chaining
     */
    public T filedCacheLocation(CacheLocationEnum filedCacheLocation) {
        parameter().setFiledCacheLocation(filedCacheLocation);
        return self();
    }

    /**
     * Whether string values (sheet names and cell content) should be automatically trimmed of
     * leading/trailing whitespace.
     *
     * @param autoTrim {@code true} to trim values, {@code false} to leave them untouched
     * @return this builder instance, for chaining
     */
    public T autoTrim(Boolean autoTrim) {
        parameter().setAutoTrim(autoTrim);
        return self();
    }

    /**
     * Whether cell values and sheet names should be automatically stripped of leading/trailing
     * whitespace (and other non-visible characters) before being handed to converters or written
     * to the target sheet.
     *
     * <p>When both this flag and {@link #autoTrim(Boolean)} are enabled, stripping takes
     * precedence. The default value is {@code false}.</p>
     *
     * @param autoStrip {@code true} to strip leading/trailing whitespace from string values,
     *                  {@code false} to leave them untouched
     * @return this builder instance, for chaining
     * @see #autoTrim(Boolean)
     */
    public T autoStrip(Boolean autoStrip) {
        parameter().setAutoStrip(autoStrip);
        return self();
    }

    /**
     * Returns this builder cast to the concrete subtype {@code T}, so that fluent setters can be
     * chained without losing the specific builder type.
     *
     * @return this instance, typed as the concrete builder {@code T}
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    /**
     * Returns the parameter object that this builder mutates. Implementations typically hold a
     * single {@link C} instance and expose it here.
     *
     * @return the concrete parameter object currently being built; never {@code null} while the
     *         builder is usable
     */
    protected abstract C parameter();
}

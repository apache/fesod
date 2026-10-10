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

package org.apache.fesod.sheet.write.builder;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.fesod.sheet.enums.HeaderMergeStrategy;
import org.apache.fesod.sheet.metadata.AbstractParameterBuilder;
import org.apache.fesod.sheet.write.handler.WriteHandler;
import org.apache.fesod.sheet.write.metadata.WriteBasicParameter;

/**
 * Fluent base builder for the write-side parameter objects shared by all {@code ExcelWriter}
 * entry points (workbook, sheet and table builders).
 *
 * <p>Each setter stores its value into the underlying {@link C} via {@code parameter()} and
 * returns {@code self()} so calls can be chained. This class only exposes write-specific options;
 * cross-cutting options such as {@code head(Class)} live in
 * {@link AbstractParameterBuilder}.</p>
 *
 * @param <T> the concrete builder type, used for fluent self-chaining
 * @param <C> the concrete {@link WriteBasicParameter} subtype this builder mutates
 */
public abstract class AbstractExcelWriterParameterBuilder<
                T extends AbstractExcelWriterParameterBuilder, C extends WriteBasicParameter>
        extends AbstractParameterBuilder<T, C> {

    /**
     * Shifts the header block (and therefore all data) down by the given number of rows so that
     * content can be written below rows that already exist in the sheet. Indexes are zero-based.
     *
     * @param relativeHeadRowIndex the number of blank rows to leave above the header; {@code null}
     *                             or {@code 0} starts writing at row 0
     * @return this builder instance, for chaining
     */
    public T relativeHeadRowIndex(Integer relativeHeadRowIndex) {
        parameter().setRelativeHeadRowIndex(relativeHeadRowIndex);
        return self();
    }

    /**
     * Controls whether a header row should be emitted at all.
     *
     * @param needHead {@code true} (default) to write the header; {@code false} to skip it
     * @return this builder instance, for chaining
     */
    public T needHead(Boolean needHead) {
        parameter().setNeedHead(needHead);
        return self();
    }

    /**
     * Registers a custom {@link WriteHandler} that will be invoked during the write pipeline.
     * Handlers are appended to the existing list and executed in registration order, subject to
     * any {@code Order} the handler implements.
     *
     * @param writeHandler the handler to register; ignored at runtime if {@code null} is passed,
     *                     but callers should not rely on that
     * @return this builder instance, for chaining
     */
    public T registerWriteHandler(WriteHandler writeHandler) {
        if (parameter().getCustomWriteHandlerList() == null) {
            parameter().setCustomWriteHandlerList(new ArrayList<WriteHandler>());
        }
        parameter().getCustomWriteHandlerList().add(writeHandler);
        return self();
    }

    /**
     * Enables or disables Fesod's built-in default style. The default value is {@code true}.
     *
     * @param useDefaultStyle {@code true} to apply the default style, {@code false} to leave cells
     *                        unstyled unless an explicit style handler is registered
     * @return this builder instance, for chaining
     */
    public T useDefaultStyle(Boolean useDefaultStyle) {
        parameter().setUseDefaultStyle(useDefaultStyle);
        return self();
    }

    /**
     * Whether adjacent equal header cells should be merged automatically. The default value is
     * {@code true}. Ignored when a {@link HeaderMergeStrategy} is set explicitly via
     * {@link #headerMergeStrategy(HeaderMergeStrategy)}.
     *
     * @param automaticMergeHead {@code true} to enable automatic header merging, {@code false} to
     *                           disable it
     * @return this builder instance, for chaining
     */
    public T automaticMergeHead(Boolean automaticMergeHead) {
        parameter().setAutomaticMergeHead(automaticMergeHead);
        return self();
    }

    /**
     * Sets the header merge strategy explicitly. If not set, the merge behavior falls back to
     * {@link #automaticMergeHead(Boolean)} for backward compatibility.
     *
     * @param strategy the {@link HeaderMergeStrategy} to apply to header cells; {@code null} to
     *                 fall back to the {@link #automaticMergeHead(Boolean)} behavior
     * @return this builder instance, for chaining
     */
    public T headerMergeStrategy(HeaderMergeStrategy strategy) {
        parameter().setHeaderMergeStrategy(strategy);
        return self();
    }

    /**
     * Excludes the columns with the given zero-based indexes from the output.
     *
     * @param excludeColumnIndexes the indexes of columns to skip; {@code null} or empty means no
     *                             column is excluded by index
     * @return this builder instance, for chaining
     */
    public T excludeColumnIndexes(Collection<Integer> excludeColumnIndexes) {
        parameter().setExcludeColumnIndexes(excludeColumnIndexes);
        return self();
    }

    /**
     * Excludes the columns whose field names are in the given collection from the output.
     *
     * @param excludeColumnFieldNames the field names of columns to skip; {@code null} or empty
     *                                means no column is excluded by field name
     * @return this builder instance, for chaining
     * @deprecated misspelled; use {@link #excludeColumnFieldNames(Collection)} instead
     */
    @Deprecated
    public T excludeColumnFiledNames(Collection<String> excludeColumnFieldNames) {
        parameter().setExcludeColumnFieldNames(excludeColumnFieldNames);
        return self();
    }

    /**
     * Excludes the columns whose field names are in the given collection from the output.
     *
     * @param excludeColumnFieldNames the field names of columns to skip; {@code null} or empty
     *                                means no column is excluded by field name
     * @return this builder instance, for chaining
     */
    public T excludeColumnFieldNames(Collection<String> excludeColumnFieldNames) {
        parameter().setExcludeColumnFieldNames(excludeColumnFieldNames);
        return self();
    }

    /**
     * Restricts the output to the columns with the given zero-based indexes.
     *
     * @param includeColumnIndexes the indexes of columns to keep; {@code null} or empty means no
     *                             inclusion filter is applied by index
     * @return this builder instance, for chaining
     */
    public T includeColumnIndexes(Collection<Integer> includeColumnIndexes) {
        parameter().setIncludeColumnIndexes(includeColumnIndexes);
        return self();
    }

    /**
     * Restricts the output to the columns whose field names are in the given collection.
     *
     * @param includeColumnFieldNames the field names of columns to keep; {@code null} or empty
     *                                means no inclusion filter is applied by field name
     * @return this builder instance, for chaining
     * @deprecated misspelled; use {@link #includeColumnFieldNames(Collection)} instead
     */
    @Deprecated
    public T includeColumnFiledNames(Collection<String> includeColumnFieldNames) {
        parameter().setIncludeColumnFieldNames(includeColumnFieldNames);
        return self();
    }

    /**
     * Restricts the output to the columns whose field names are in the given collection.
     *
     * @param includeColumnFieldNames the field names of columns to keep; {@code null} or empty
     *                                means no inclusion filter is applied by field name
     * @return this builder instance, for chaining
     */
    public T includeColumnFieldNames(Collection<String> includeColumnFieldNames) {
        parameter().setIncludeColumnFieldNames(includeColumnFieldNames);
        return self();
    }

    /**
     * Data will be ordered by {@link #includeColumnFieldNames} or {@link #includeColumnIndexes}.
     *
     * @param orderByIncludeColumn {@code true} to order by included column; default is {@code false}
     * @return this
     */
    public T orderByIncludeColumn(Boolean orderByIncludeColumn) {
        parameter().setOrderByIncludeColumn(orderByIncludeColumn);
        return self();
    }
}

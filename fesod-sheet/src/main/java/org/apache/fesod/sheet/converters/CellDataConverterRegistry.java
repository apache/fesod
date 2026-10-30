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

package org.apache.fesod.sheet.converters;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;

/**
 * Registry for managing converters that transform between Java objects and spreadsheet cell data.
 */
public class CellDataConverterRegistry {

    /**
     * The parent converter registry, or {@code null} if this is the root registry.
     */
    private final CellDataConverterRegistry parent;

    private static final Map<ConverterKeyBuild.ConverterKey, Converter<?>> TO_STRING_CONVERTERS =
            DefaultConverterLoader.loadDefaultWriteStringConverter();

    private final Map<ConverterKeyBuild.ConverterKey, Deque<WriteConverter<?>>> customWriteConverters =
            new LinkedHashMap<>();
    private final Map<ConverterKeyBuild.ConverterKey, Deque<ReadConverter<?>>> customReadConverters =
            new LinkedHashMap<>();

    private final Map<ConverterKeyBuild.ConverterKey, Converter<?>> defaultConverters = new LinkedHashMap<>();

    private final Map<WriteCacheKey, ConverterHolder<WriteConverter<?>>> writeConvertersCache =
            new ConcurrentHashMap<>(40);
    private final Map<ReadCacheKey, ConverterHolder<ReadConverter<?>>> readConvertersCache =
            new ConcurrentHashMap<>(40);

    public CellDataConverterRegistry() {
        this(null);
    }

    public CellDataConverterRegistry(CellDataConverterRegistry parent) {
        this.parent = parent;
    }

    /**
     * Registers the default converters.
     */
    public void addDefaultConverters(Map<ConverterKeyBuild.ConverterKey, Converter<?>> map) {
        defaultConverters.putAll(map);
    }

    /**
     * Registers a custom write converter. When multiple converters of the same type exist,
     * the converter registered later takes precedence in matching.
     */
    public void addCustomWriteConverter(WriteConverter<?> converter) {
        Validate.notNull(converter, "WriteConverter must not be null");
        Validate.notNull(
                converter.supportJavaTypeKey(),
                "WriteConverter [" + converter.getClass().getName() + "] must explicitly specify supportJavaTypeKey()");

        ConverterKeyBuild.ConverterKey key = ConverterKeyBuild.buildKey(converter.supportJavaTypeKey());
        customWriteConverters.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(converter);

        writeConvertersCache.clear();
    }

    /**
     * Registers a custom read converter. When multiple converters of the same type exist,
     * the converter registered later takes precedence in matching.
     */
    public void addCustomReadConverter(ReadConverter<?> converter) {
        Validate.notNull(converter, "ReadConverter must not be null");
        Validate.notNull(
                converter.supportJavaTypeKey(),
                "ReadConverter [" + converter.getClass().getName() + "] must explicitly specify supportJavaTypeKey()");
        Validate.notNull(
                converter.supportExcelTypeKey(),
                "ReadConverter [" + converter.getClass().getName() + "] must explicitly specify supportExcelTypeKey()");

        ConverterKeyBuild.ConverterKey key =
                ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey());
        customReadConverters.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(converter);

        readConvertersCache.clear();
    }

    /**
     * Retrieves the global write converter for the specified Java type.
     *
     * @param sourceType the source Java type
     * @return the matched write converter or {@code null} if not found
     */
    public WriteConverter<?> findWriteConverter(Class<?> sourceType) {
        return findWriteConverter(sourceType, null);
    }

    /**
     * Retrieves the write converter for the specified Java type and target column index.
     *
     * @param sourceType the source Java type
     * @param column     optional target cell column index (0-based)
     * @return the matched write converter or {@code null} if not found
     */
    public WriteConverter<?> findWriteConverter(Class<?> sourceType, Integer column) {
        return findWriteConverter(sourceType, column, false);
    }

    /**
     * Retrieves the write converter for the specified Java type and target column index, and default fallback strategy.
     *
     * @param sourceType      the source Java type
     * @param column          optional target cell column index (0-based)
     * @param defaultToString {@code true} to default to the string converter set when no custom converter matches
     * @return the matched write converter or {@code null} if not found
     */
    public WriteConverter<?> findWriteConverter(Class<?> sourceType, Integer column, boolean defaultToString) {
        if (sourceType == null) {
            return null;
        }

        WriteCacheKey cacheKey = new WriteCacheKey(sourceType, column, defaultToString);
        ConverterHolder<WriteConverter<?>> cachedHolder = writeConvertersCache.get(cacheKey);
        if (cachedHolder != null) {
            return cachedHolder.isNull() ? null : cachedHolder.converter;
        }

        WriteConverter<?> result = resolveWriteConverter(sourceType, column, defaultToString);
        writeConvertersCache.put(cacheKey, new ConverterHolder<>(result));
        return result;
    }

    /**
     * Retrieves the write converter.
     *
     * <p>
     * Priority: current registry (column‑bound converters take precedence over global converters),
     * then parent registry, and finally default converters.
     * </p>
     *
     * @param sourceType      the source Java type
     * @param column          optional target cell column index (0-based)
     * @param defaultToString {@code true} to default to the string converter set when no custom converter matches
     * @return the matched write converter or {@code null} if not found
     */
    private WriteConverter<?> resolveWriteConverter(Class<?> sourceType, Integer column, boolean defaultToString) {
        ConverterKeyBuild.ConverterKey key = ConverterKeyBuild.buildKey(sourceType);

        WriteConverter<?> localSelected = selectConverter(customWriteConverters.get(key), column);

        if (localSelected != null) {
            return localSelected;
        }

        if (parent != null) {
            return parent.findWriteConverter(sourceType, column, defaultToString);
        }
        return defaultToString ? TO_STRING_CONVERTERS.get(key) : defaultConverters.get(key);
    }

    /**
     * Retrieves the read converter for the specified source cell type and target Java type.
     *
     * @param targetType the target Java type
     * @param sourceType the source spreadsheet cell data type
     * @return the matched read converter or {@code null} if not found
     */
    public ReadConverter<?> findReadConverter(Class<?> targetType, CellDataTypeEnum sourceType) {
        return findReadConverter(targetType, sourceType, null);
    }

    /**
     * Retrieves the read converter for the specified source cell type, target Java type and target column index.
     *
     * @param targetType the target Java type
     * @param sourceType the source spreadsheet cell data type
     * @param column     optional target cell column index (0-based)
     * @return the matched read converter or {@code null} if not found
     */
    public ReadConverter<?> findReadConverter(Class<?> targetType, CellDataTypeEnum sourceType, Integer column) {
        if (targetType == null || sourceType == null) {
            return null;
        }

        ReadCacheKey cacheKey = new ReadCacheKey(targetType, sourceType, column);
        ConverterHolder<ReadConverter<?>> cachedHolder = readConvertersCache.get(cacheKey);
        if (cachedHolder != null) {
            return cachedHolder.isNull() ? null : cachedHolder.converter;
        }

        ReadConverter<?> result = resolveReadConverter(targetType, sourceType, column);
        readConvertersCache.put(cacheKey, new ConverterHolder<>(result));
        return result;
    }

    /**
     * Retrieves the read converter.
     *
     * <p>
     * Priority: current registry (column‑bound converters take precedence over global converters),
     * then parent registry, and finally default converters.
     * </p>
     *
     * @param targetType the target Java type
     * @param sourceType the source spreadsheet cell data type
     * @param column     optional target cell column index (0-based)
     * @return the matched read converter or {@code null} if not found
     */
    private ReadConverter<?> resolveReadConverter(Class<?> targetType, CellDataTypeEnum sourceType, Integer column) {
        ConverterKeyBuild.ConverterKey key = ConverterKeyBuild.buildKey(targetType, sourceType);

        ReadConverter<?> localSelected = selectConverter(customReadConverters.get(key), column);

        if (localSelected != null) {
            return localSelected;
        }

        if (parent != null) {
            return parent.findReadConverter(targetType, sourceType, column);
        }
        return defaultConverters.get(key);
    }

    /**
     * Selects the best-matching (read or write) converter from a candidates.
     *
     * <p>
     * Priority: column‑bound converter take precedence over global converter.
     * </p>
     */
    private <T> T selectConverter(Deque<T> candidates, Integer column) {
        if (CollectionUtils.isEmpty(candidates)) {
            return null;
        }

        T globalMatched = null;
        for (T candidate : candidates) {
            if (candidate instanceof ColumnBinding) {
                if (isColumnMatched((ColumnBinding) candidate, column)) {
                    return candidate;
                }
                continue;
            }

            if (globalMatched == null) {
                if (column == null) {
                    return candidate;
                }
                globalMatched = candidate;
            }
        }
        return globalMatched;
    }

    /**
     * Determines whether the given column binding matches the specified column index.
     */
    private boolean isColumnMatched(ColumnBinding columnBinding, Integer column) {
        if (column == null) {
            return false;
        }
        Set<Integer> columnIndexes = columnBinding.columnIndexes();
        return CollectionUtils.isNotEmpty(columnIndexes) && columnIndexes.contains(column);
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class WriteCacheKey {
        private final Class<?> javaType;
        private final Integer column;
        private final boolean defaultToString;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class ReadCacheKey {
        private final Class<?> javaType;
        private final CellDataTypeEnum cellDataType;
        private final Integer column;
    }

    @EqualsAndHashCode
    private static class ConverterHolder<T> {
        private final T converter;

        ConverterHolder(T converter) {
            this.converter = converter;
        }

        boolean isNull() {
            return converter == null;
        }
    }
}

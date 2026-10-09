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

package org.apache.fesod.sheet.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.ConverterKeyBuild.ConverterKey;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * Memoizes the per-cell metadata resolution of one sheet: the {@link ExcelContentProperty} of every
 * mapped field and the {@link Converter} of every (target class, cell type) pair actually
 * encountered. Both are constant per sheet — every row model is instantiated from the head class,
 * so the row class the per-cell path kept re-deriving never changes — so each value is resolved
 * once by delegating to the same lookups the per-cell path used
 * ({@link ClassUtils#declaredExcelContentProperty} and the sheet's converter map) and then served
 * from the memo: no {@code ContentPropertyKey} or {@code ConverterKey} build, no map probe.
 *
 * <p>This class only caches; it contains no resolution logic of its own and cannot drift from
 * {@link ClassUtils} or the converter map.
 *
 * <p>A resolver belongs to one sheet and is driven by the single analysis thread; no synchronization.
 */
public final class SheetConverterResolver {

    private final ConfigurationHolder configurationHolder;

    private final Class<?> headClazz;

    private final Map<ConverterKey, Converter<?>> converterMap;

    /**
     * Property per field name; may hold null values for fields without configuration.
     */
    private final Map<String, ExcelContentProperty> contentPropertiesByFieldName = new HashMap<>();

    /**
     * Converter per target class, indexed by {@link CellDataTypeEnum} ordinal.
     */
    private final Map<Class<?>, Converter<?>[]> convertersByClassAndType = new HashMap<>();

    public SheetConverterResolver(
            ConfigurationHolder configurationHolder, Class<?> headClazz, Map<ConverterKey, Converter<?>> converterMap) {
        this.configurationHolder = configurationHolder;
        this.headClazz = headClazz;
        this.converterMap = converterMap;
    }

    /**
     * Memoized {@link ClassUtils#declaredExcelContentProperty}. {@code dataMap} is read only on the
     * first call of a field name, to derive the row class — exactly the input the per-cell path
     * passed every cell.
     */
    public ExcelContentProperty contentProperty(Map<?, ?> dataMap, String fieldName) {
        if (contentPropertiesByFieldName.containsKey(fieldName)) {
            return contentPropertiesByFieldName.get(fieldName);
        }
        ExcelContentProperty property =
                ClassUtils.declaredExcelContentProperty(dataMap, headClazz, fieldName, configurationHolder);
        contentPropertiesByFieldName.put(fieldName, property);
        return property;
    }

    /**
     * Memoized lookup of the converter for a (target class, cell type) pair from the sheet's
     * converter map. Returns {@code null} exactly when the direct probe would.
     */
    public Converter<?> converter(Class<?> clazz, CellDataTypeEnum cellType) {
        Converter<?>[] byCellType = convertersByClassAndType.get(clazz);
        if (byCellType == null) {
            byCellType = new Converter<?>[CellDataTypeEnum.values().length];
            convertersByClassAndType.put(clazz, byCellType);
        }
        Converter<?> converter = byCellType[cellType.ordinal()];
        if (converter == null) {
            converter = converterMap.get(ConverterKeyBuild.buildKey(clazz, cellType));
            byCellType[cellType.ordinal()] = converter;
        }
        return converter;
    }
}

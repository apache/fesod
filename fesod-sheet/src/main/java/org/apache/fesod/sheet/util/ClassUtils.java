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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.FieldCache;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.write.metadata.holder.WriteHolder;

public class ClassUtils {

    /**
     * memory cache
     */
    public static final Map<FieldCacheKey, FieldCache> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * The cache configuration information for each of the class
     */
    public static final ConcurrentHashMap<Class<?>, Map<String, ExcelContentProperty>> CLASS_CONTENT_CACHE =
            new ConcurrentHashMap<>();

    /**
     * The cache configuration information for each of the class
     */
    public static final ConcurrentHashMap<ContentPropertyKey, ExcelContentProperty> CONTENT_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Calculate the configuration information for the class
     *
     * @param dataMap
     * @param headClazz
     * @param fieldName
     * @return
     */
    public static ExcelContentProperty declaredExcelContentProperty(
            Map<?, ?> dataMap, Class<?> headClazz, String fieldName, ConfigurationHolder configurationHolder) {
        return SheetContentPropertyResolver.resolve(dataMap, headClazz, fieldName, configurationHolder);
    }

    public static void combineExcelContentProperty(
            ExcelContentProperty combineExcelContentProperty, ExcelContentProperty excelContentProperty) {
        SheetContentPropertyResolver.combineExcelContentProperty(combineExcelContentProperty, excelContentProperty);
    }

    /**
     * Parsing field in the class
     *
     * @param clazz               Need to parse the class
     * @param configurationHolder configuration
     */
    public static FieldCache declaredFields(Class<?> clazz, ConfigurationHolder configurationHolder) {
        return SheetHeadFieldResolver.resolve(clazz, configurationHolder);
    }

    /**
     * <p>Gets a {@code List} of all interfaces implemented by the given
     * class and its superclasses.</p>
     *
     * <p>The order is determined by looking through each interface in turn as
     * declared in the source file and following its hierarchy up. Then each
     * superclass is considered in the same way. Later duplicates are ignored,
     * so the order is maintained.</p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@code List} of interfaces in order,
     * {@code null} if null input
     */
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    /**
     * Gets the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@code Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ContentPropertyKey {
        private Class<?> clazz;
        private Class<?> headClass;
        private String fieldName;
    }

    @Data
    public static class FieldCacheKey {
        private Class<?> clazz;
        private Collection<String> excludeColumnFieldNames;
        private Collection<Integer> excludeColumnIndexes;
        private Collection<String> includeColumnFieldNames;
        private Collection<Integer> includeColumnIndexes;

        FieldCacheKey(Class<?> clazz, ConfigurationHolder configurationHolder) {
            this.clazz = clazz;
            if (configurationHolder instanceof WriteHolder) {
                WriteHolder writeHolder = (WriteHolder) configurationHolder;
                this.excludeColumnFieldNames = writeHolder.excludeColumnFieldNames();
                this.excludeColumnIndexes = writeHolder.excludeColumnIndexes();
                this.includeColumnFieldNames = writeHolder.includeColumnFieldNames();
                this.includeColumnIndexes = writeHolder.includeColumnIndexes();
            }
        }
    }

    public static void removeThreadLocalCache() {
        SheetHeadFieldResolver.removeThreadLocalCache();
        SheetContentPropertyResolver.removeThreadLocalCache();
    }
}

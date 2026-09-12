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

package org.apache.fesod.sheet.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.FieldCache;
import org.apache.fesod.sheet.metadata.FieldWrapper;
import org.apache.fesod.sheet.util.ClassUtils.FieldCacheKey;
import org.apache.fesod.sheet.write.metadata.holder.WriteHolder;

/**
 * Internal helper used by {@link ClassUtils} for resolving which declared fields of a class become
 * spreadsheet columns, and in which order.
 * <p>
 * Not intended for direct use; use {@link ClassUtils} as the primary entry point instead.
 * </p>
 */
final class SheetHeadFieldResolver {

    /**
     * thread local cache
     */
    private static final ThreadLocal<Map<FieldCacheKey, FieldCache>> FIELD_THREAD_LOCAL = new ThreadLocal<>();

    private static final MetadataCaches<FieldCacheKey, FieldCache> FIELD_CACHES =
            new MetadataCaches<>(ClassUtils.FIELD_CACHE, FIELD_THREAD_LOCAL);

    private SheetHeadFieldResolver() {}

    /**
     * Parsing field in the class
     *
     * @param clazz               Need to parse the class
     * @param configurationHolder configuration
     */
    static FieldCache resolve(Class<?> clazz, ConfigurationHolder configurationHolder) {
        return FIELD_CACHES.get(
                configurationHolder,
                new FieldCacheKey(clazz, configurationHolder),
                key -> doResolve(clazz, configurationHolder));
    }

    static void removeThreadLocalCache() {
        FIELD_THREAD_LOCAL.remove();
    }

    private static FieldCache doResolve(Class<?> clazz, ConfigurationHolder configurationHolder) {
        List<Field> tempFieldList = FieldUtils.resolveAllFields(clazz);

        ExcelIgnoreUnannotated excelIgnoreUnannotated = clazz.getAnnotation(ExcelIgnoreUnannotated.class);
        Set<String> ignoreSet = new HashSet<>();
        // First collect all field names annotated with ExcelIgnore (including subclass overrides)
        for (Field field : tempFieldList) {
            if (field.getAnnotation(ExcelIgnore.class) != null) {
                ignoreSet.add(FieldUtils.resolveCglibFieldName(field));
            }
        }
        Map<Integer, List<FieldWrapper>> orderFieldMap = new TreeMap<>();
        Map<Integer, FieldWrapper> indexFieldMap = new TreeMap<>();
        for (Field field : tempFieldList) {
            String fieldName = FieldUtils.resolveCglibFieldName(field);
            // Skip if ignored
            if (ignoreSet.contains(fieldName)) {
                continue;
            }
            declaredOneField(field, orderFieldMap, indexFieldMap, ignoreSet, excelIgnoreUnannotated);
        }
        Map<Integer, FieldWrapper> sortedFieldMap = buildSortedAllFieldMap(orderFieldMap, indexFieldMap);
        FieldCache fieldCache = new FieldCache(sortedFieldMap, indexFieldMap);

        if (!(configurationHolder instanceof WriteHolder)) {
            return fieldCache;
        }

        WriteHolder writeHolder = (WriteHolder) configurationHolder;

        boolean needIgnore = !CollectionUtils.isEmpty(writeHolder.excludeColumnFieldNames())
                || !CollectionUtils.isEmpty(writeHolder.excludeColumnIndexes())
                || !CollectionUtils.isEmpty(writeHolder.includeColumnFieldNames())
                || !CollectionUtils.isEmpty(writeHolder.includeColumnIndexes());

        if (!needIgnore) {
            return fieldCache;
        }
        // ignore filed
        Map<Integer, FieldWrapper> tempSortedFieldMap = MapUtils.newHashMap();
        int index = 0;
        for (Map.Entry<Integer, FieldWrapper> entry : sortedFieldMap.entrySet()) {
            Integer key = entry.getKey();
            FieldWrapper field = entry.getValue();

            // The current field needs to be ignored
            if (writeHolder.ignore(field.getFieldName(), entry.getKey())) {
                ignoreSet.add(field.getFieldName());
                // indexFieldMap is keyed by the field's explicit @ExcelProperty(index), which for
                // explicit-index fields equals the sortedFieldMap position (entry.getKey()); remove
                // by that key, not the running counter, otherwise an unrelated explicit-index entry
                // is dropped and the ignored field's entry may survive.
                indexFieldMap.remove(key);
            } else {
                // Mandatory sorted fields
                if (indexFieldMap.containsKey(key)) {
                    tempSortedFieldMap.put(key, field);
                } else {
                    // Need to reorder automatically
                    // Check whether the current key is already in use
                    while (tempSortedFieldMap.containsKey(index)) {
                        index++;
                    }
                    tempSortedFieldMap.put(index++, field);
                }
            }
        }
        fieldCache.setSortedFieldMap(tempSortedFieldMap);

        // resort field
        resortField(writeHolder, fieldCache);
        return fieldCache;
    }

    /**
     * it only works when {@link WriteHolder#includeColumnFieldNames()}  or
     * {@link WriteHolder#includeColumnIndexes()}  has value
     * and {@link WriteHolder#orderByIncludeColumn()}  is true
     **/
    private static void resortField(WriteHolder writeHolder, FieldCache fieldCache) {
        if (!writeHolder.orderByIncludeColumn()) {
            return;
        }
        Map<Integer, FieldWrapper> indexFieldMap = fieldCache.getIndexFieldMap();

        Collection<String> includeColumnFieldNames = writeHolder.includeColumnFieldNames();
        if (!CollectionUtils.isEmpty(includeColumnFieldNames)) {
            // Field sorted map
            Map<String, Integer> filedIndexMap = MapUtils.newHashMap();
            int fieldIndex = 0;
            for (String includeColumnFieldName : includeColumnFieldNames) {
                filedIndexMap.put(includeColumnFieldName, fieldIndex++);
            }

            // rebuild sortedFieldMap
            Map<Integer, FieldWrapper> tempSortedFieldMap = MapUtils.newHashMap();
            fieldCache.getSortedFieldMap().forEach((index, field) -> {
                Integer tempFieldIndex = filedIndexMap.get(field.getFieldName());
                if (tempFieldIndex != null) {
                    tempSortedFieldMap.put(tempFieldIndex, field);

                    //  The user has redefined the ordering and the ordering of annotations needs to be invalidated
                    if (!tempFieldIndex.equals(index)) {
                        indexFieldMap.remove(index);
                    }
                }
            });
            fieldCache.setSortedFieldMap(tempSortedFieldMap);
            return;
        }

        Collection<Integer> includeColumnIndexes = writeHolder.includeColumnIndexes();
        if (!CollectionUtils.isEmpty(includeColumnIndexes)) {
            // Index sorted map
            Map<Integer, Integer> filedIndexMap = MapUtils.newHashMap();
            int fieldIndex = 0;
            for (Integer includeColumnIndex : includeColumnIndexes) {
                filedIndexMap.put(includeColumnIndex, fieldIndex++);
            }

            // rebuild sortedFieldMap
            Map<Integer, FieldWrapper> tempSortedFieldMap = MapUtils.newHashMap();
            fieldCache.getSortedFieldMap().forEach((index, field) -> {
                Integer tempFieldIndex = filedIndexMap.get(index);

                //  The user has redefined the ordering and the ordering of annotations needs to be invalidated
                if (tempFieldIndex != null) {
                    tempSortedFieldMap.put(tempFieldIndex, field);
                }
            });
            fieldCache.setSortedFieldMap(tempSortedFieldMap);
        }
    }

    private static Map<Integer, FieldWrapper> buildSortedAllFieldMap(
            Map<Integer, List<FieldWrapper>> orderFieldMap, Map<Integer, FieldWrapper> indexFieldMap) {

        Map<Integer, FieldWrapper> sortedAllFieldMap =
                new HashMap<>((orderFieldMap.size() + indexFieldMap.size()) * 4 / 3 + 1);

        Map<Integer, FieldWrapper> tempIndexFieldMap = new HashMap<>(indexFieldMap);
        int index = 0;
        for (List<FieldWrapper> fieldList : orderFieldMap.values()) {
            for (FieldWrapper field : fieldList) {
                while (tempIndexFieldMap.containsKey(index)) {
                    sortedAllFieldMap.put(index, tempIndexFieldMap.get(index));
                    tempIndexFieldMap.remove(index);
                    index++;
                }
                sortedAllFieldMap.put(index, field);
                index++;
            }
        }
        sortedAllFieldMap.putAll(tempIndexFieldMap);
        return sortedAllFieldMap;
    }

    private static void declaredOneField(
            Field field,
            Map<Integer, List<FieldWrapper>> orderFieldMap,
            Map<Integer, FieldWrapper> indexFieldMap,
            Set<String> ignoreSet,
            ExcelIgnoreUnannotated excelIgnoreUnannotated) {
        String fieldName = FieldUtils.resolveCglibFieldName(field);
        // skip if the field is in ignoreSet
        if (ignoreSet.contains(fieldName)) {
            return;
        }
        FieldWrapper fieldWrapper = new FieldWrapper();
        fieldWrapper.setField(field);
        fieldWrapper.setFieldName(fieldName);

        ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
        boolean noExcelProperty = excelProperty == null && excelIgnoreUnannotated != null;
        boolean isStaticFinalOrTransient =
                (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                        || Modifier.isTransient(field.getModifiers());
        if (noExcelProperty || (excelProperty == null && isStaticFinalOrTransient)) {
            ignoreSet.add(fieldName);
            return;
        }
        // set heads
        if (excelProperty != null) {
            fieldWrapper.setHeads(excelProperty.value());
        }
        if (excelProperty != null && excelProperty.index() >= 0) {
            if (indexFieldMap.containsKey(excelProperty.index())) {
                throw new ExcelCommonException("The index of '"
                        + indexFieldMap.get(excelProperty.index()).getFieldName() + "' and '" + field.getName()
                        + "' must be inconsistent");
            }
            indexFieldMap.put(excelProperty.index(), fieldWrapper);
            return;
        }
        int order = Integer.MAX_VALUE;
        if (excelProperty != null) {
            order = excelProperty.order();
        }
        List<FieldWrapper> orderFieldList = orderFieldMap.computeIfAbsent(order, key -> ListUtils.newArrayList());
        orderFieldList.add(fieldWrapper);
    }
}

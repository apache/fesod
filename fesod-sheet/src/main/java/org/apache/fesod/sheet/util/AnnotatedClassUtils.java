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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.sheet.annotation.AnnotatedFieldDescriptor;
import org.apache.fesod.sheet.annotation.AnnotatedTypeDescriptor;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.AnnotationMap;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.converters.AutoConverter;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.metadata.CachedFields;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.metadata.property.FontProperty;
import org.apache.fesod.sheet.metadata.property.NumberFormatProperty;
import org.apache.fesod.sheet.metadata.property.StyleProperty;
import org.apache.fesod.sheet.write.metadata.holder.WriteHolder;

/**
 * Similar to {@link ClassUtils}, provides support for composable annotations. (beta yet)
 */
public final class AnnotatedClassUtils {

    /**
     * memory cache
     */
    public static final Map<FieldCacheKey, CachedFields> FIELD_CACHE = new ConcurrentHashMap<>();
    /**
     * thread local cache
     */
    private static final ThreadLocal<Map<FieldCacheKey, CachedFields>> FIELD_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * The cache configuration information for each of the class
     */
    private static final ThreadLocal<Map<Class<?>, Map<String, ExcelContentProperty>>> CLASS_CONTENT_THREAD_LOCAL =
            new ThreadLocal<>();

    /**
     * The cache configuration information for each of the class
     */
    public static final Map<ClassUtils.ContentPropertyKey, ExcelContentProperty> CONTENT_CACHE =
            new ConcurrentHashMap<>();

    /**
     * The cache configuration information for each of the class
     */
    private static final ThreadLocal<Map<ClassUtils.ContentPropertyKey, ExcelContentProperty>> CONTENT_THREAD_LOCAL =
            new ThreadLocal<>();

    /**
     * Calculate the configuration information for the class. (beta yet)
     */
    public static ExcelContentProperty declaredExcelContentProperty(
            Map<?, ?> dataMap,
            AnnotatedTypeDescriptor typeDescriptor,
            AnnotatedFieldDescriptor fieldDescriptor,
            ConfigurationHolder configurationHolder) {
        Class<?> clazz = null;
        if (dataMap instanceof BeanMap) {
            Object bean = ((BeanMap) dataMap).getBean();
            if (bean != null) {
                clazz = bean.getClass();
            }
        }
        return getExcelContentProperty(clazz, typeDescriptor, fieldDescriptor, configurationHolder);
    }

    private static ExcelContentProperty getExcelContentProperty(
            Class<?> clazz,
            AnnotatedTypeDescriptor typeDescriptor,
            AnnotatedFieldDescriptor fieldDescriptor,
            ConfigurationHolder configurationHolder) {
        Class<?> headClass = typeDescriptor.getAnnotatedElement();
        String fieldName = fieldDescriptor.getFieldName();

        switch (configurationHolder.globalConfiguration().getFiledCacheLocation()) {
            case THREAD_LOCAL:
                Map<ClassUtils.ContentPropertyKey, ExcelContentProperty> contentCacheMap = CONTENT_THREAD_LOCAL.get();
                if (contentCacheMap == null) {
                    contentCacheMap = MapUtils.newHashMap();
                    CONTENT_THREAD_LOCAL.set(contentCacheMap);
                }
                return contentCacheMap.computeIfAbsent(buildKey(clazz, headClass, fieldName), key -> {
                    return doGetExcelContentProperty(typeDescriptor, fieldDescriptor);
                });
            case MEMORY:
                return CONTENT_CACHE.computeIfAbsent(buildKey(clazz, headClass, fieldName), key -> {
                    return doGetExcelContentProperty(typeDescriptor, fieldDescriptor);
                });
            case NONE:
                return doGetExcelContentProperty(typeDescriptor, fieldDescriptor);
            default:
                throw new UnsupportedOperationException("unsupported enum");
        }
    }

    private static ClassUtils.ContentPropertyKey buildKey(Class<?> clazz, Class<?> headClass, String fieldName) {
        return new ClassUtils.ContentPropertyKey(clazz, headClass, fieldName);
    }

    private static ExcelContentProperty doGetExcelContentProperty(
            AnnotatedTypeDescriptor typeDescriptor, AnnotatedFieldDescriptor fieldDescriptor) {
        ExcelContentProperty headExcelContentProperty = doDeclaredFieldContent(typeDescriptor, fieldDescriptor);
        ExcelContentProperty combineExcelContentProperty = new ExcelContentProperty();

        combineExcelContentProperty(combineExcelContentProperty, headExcelContentProperty);
        return combineExcelContentProperty;
    }

    public static void combineExcelContentProperty(
            ExcelContentProperty combineExcelContentProperty, ExcelContentProperty excelContentProperty) {
        if (excelContentProperty == null) {
            return;
        }
        if (excelContentProperty.getField() != null) {
            combineExcelContentProperty.setField(excelContentProperty.getField());
        }
        if (excelContentProperty.getConverter() != null) {
            combineExcelContentProperty.setConverter(excelContentProperty.getConverter());
        }
        if (excelContentProperty.getDateTimeFormatProperty() != null) {
            combineExcelContentProperty.setDateTimeFormatProperty(excelContentProperty.getDateTimeFormatProperty());
        }
        if (excelContentProperty.getNumberFormatProperty() != null) {
            combineExcelContentProperty.setNumberFormatProperty(excelContentProperty.getNumberFormatProperty());
        }
        if (excelContentProperty.getContentStyleProperty() != null) {
            combineExcelContentProperty.setContentStyleProperty(excelContentProperty.getContentStyleProperty());
        }
        if (excelContentProperty.getContentFontProperty() != null) {
            combineExcelContentProperty.setContentFontProperty(excelContentProperty.getContentFontProperty());
        }
    }

    private static ExcelContentProperty doDeclaredFieldContent(
            AnnotatedTypeDescriptor typeDescriptor, AnnotatedFieldDescriptor fieldDescriptor) {
        Class<?> clazz = typeDescriptor.getAnnotatedElement();
        if (clazz == null) {
            return null;
        }

        AnnotationAttributes parentContentStyle = typeDescriptor.getAnnotation(ContentStyle.class);
        AnnotationAttributes parentContentFontStyle = typeDescriptor.getAnnotation(ContentFontStyle.class);

        ExcelContentProperty excelContentProperty = new ExcelContentProperty();
        excelContentProperty.setField(fieldDescriptor.getAnnotatedElement());

        if (fieldDescriptor.hasAnnotation(ExcelProperty.class)) {
            AnnotationAttributes attrs = fieldDescriptor.getAnnotation(ExcelProperty.class);

            @SuppressWarnings("unchecked")
            Class<? extends Converter<?>> convertClazz = attrs.getRequiredAttribute("converter", Class.class);
            if (convertClazz != AutoConverter.class) {
                try {
                    Converter<?> converter =
                            convertClazz.getDeclaredConstructor().newInstance();
                    excelContentProperty.setConverter(converter);
                } catch (Exception e) {
                    throw new ExcelCommonException("Can not instance custom converter:" + convertClazz.getName());
                }
            }
        }

        AnnotationAttributes contentStyle = fieldDescriptor.getAnnotation(ContentStyle.class);
        if (contentStyle == null) {
            contentStyle = parentContentStyle;
        }
        excelContentProperty.setContentStyleProperty(StyleProperty.build(contentStyle));

        AnnotationAttributes contentFontStyle = fieldDescriptor.getAnnotation(ContentFontStyle.class);
        if (contentFontStyle == null) {
            contentFontStyle = parentContentFontStyle;
        }
        excelContentProperty.setContentFontProperty(FontProperty.build(contentFontStyle));

        excelContentProperty.setDateTimeFormatProperty(
                DateTimeFormatProperty.build(fieldDescriptor.getAnnotation(DateTimeFormat.class)));
        excelContentProperty.setNumberFormatProperty(
                NumberFormatProperty.build(fieldDescriptor.getAnnotation(NumberFormat.class)));
        return excelContentProperty;
    }

    /**
     * Parsing field in the class
     *
     * @param clazz               Need to parse the class
     * @param configurationHolder configuration
     */
    public static CachedFields declaredFields(
            Class<?> clazz, Function<Field, AnnotationMap> resolver, ConfigurationHolder configurationHolder) {
        switch (configurationHolder.globalConfiguration().getFiledCacheLocation()) {
            case THREAD_LOCAL:
                Map<FieldCacheKey, CachedFields> fieldsCacheMap = FIELD_THREAD_LOCAL.get();
                if (fieldsCacheMap == null) {
                    fieldsCacheMap = MapUtils.newHashMap();
                    FIELD_THREAD_LOCAL.set(fieldsCacheMap);
                }
                return fieldsCacheMap.computeIfAbsent(new FieldCacheKey(clazz, configurationHolder), key -> {
                    return doDeclaredFields(clazz, resolver, configurationHolder);
                });
            case MEMORY:
                return FIELD_CACHE.computeIfAbsent(new FieldCacheKey(clazz, configurationHolder), key -> {
                    return doDeclaredFields(clazz, resolver, configurationHolder);
                });
            case NONE:
                return doDeclaredFields(clazz, resolver, configurationHolder);
            default:
                throw new UnsupportedOperationException("unsupported enum");
        }
    }

    private static CachedFields doDeclaredFields(
            Class<?> clazz, Function<Field, AnnotationMap> resolver, ConfigurationHolder configurationHolder) {
        List<Field> tempFieldList = new ArrayList<>();
        Map<String, Field> fieldNameToField = new HashMap<>();
        Class<?> tempClass = clazz;
        // Prefer subclass fields, only process the bottom-most (subclass) definition for fields with the same name
        while (tempClass != null) {
            for (Field field : tempClass.getDeclaredFields()) {
                String fieldName = FieldUtils.resolveCglibFieldName(field);
                if (!fieldNameToField.containsKey(fieldName)) {
                    fieldNameToField.put(fieldName, field);
                    tempFieldList.add(field);
                }
            }
            tempClass = tempClass.getSuperclass();
        }
        ExcelIgnoreUnannotated excelIgnoreUnannotated = clazz.getAnnotation(ExcelIgnoreUnannotated.class);
        Set<String> ignoreSet = new HashSet<>();
        // First collect all field names annotated with ExcelIgnore (including subclass overrides)
        for (Field field : tempFieldList) {
            if (field.getAnnotation(ExcelIgnore.class) != null) {
                ignoreSet.add(FieldUtils.resolveCglibFieldName(field));
            }
        }
        Map<Integer, List<AnnotatedFieldDescriptor>> orderFieldMap = new TreeMap<>();
        Map<Integer, AnnotatedFieldDescriptor> indexFieldMap = new TreeMap<>();
        for (Field field : tempFieldList) {
            String fieldName = FieldUtils.resolveCglibFieldName(field);
            // Skip if ignored
            if (ignoreSet.contains(fieldName)) {
                continue;
            }
            declaredOneField(field, orderFieldMap, indexFieldMap, ignoreSet, resolver, excelIgnoreUnannotated);
        }
        Map<Integer, AnnotatedFieldDescriptor> sortedFieldMap = buildSortedAllFieldMap(orderFieldMap, indexFieldMap);
        CachedFields cachedFields = new CachedFields(sortedFieldMap, indexFieldMap);

        if (!(configurationHolder instanceof WriteHolder)) {
            return cachedFields;
        }

        WriteHolder writeHolder = (WriteHolder) configurationHolder;

        boolean needIgnore = !CollectionUtils.isEmpty(writeHolder.excludeColumnFieldNames())
                || !CollectionUtils.isEmpty(writeHolder.excludeColumnIndexes())
                || !CollectionUtils.isEmpty(writeHolder.includeColumnFieldNames())
                || !CollectionUtils.isEmpty(writeHolder.includeColumnIndexes());

        if (!needIgnore) {
            return cachedFields;
        }
        // ignore filed
        Map<Integer, AnnotatedFieldDescriptor> tempSortedFieldMap = MapUtils.newHashMap();
        int index = 0;
        for (Map.Entry<Integer, AnnotatedFieldDescriptor> entry : sortedFieldMap.entrySet()) {
            Integer key = entry.getKey();
            AnnotatedFieldDescriptor field = entry.getValue();

            // The current field needs to be ignored
            if (writeHolder.ignore(field.getFieldName(), entry.getKey())) {
                ignoreSet.add(field.getFieldName());
                indexFieldMap.remove(index);
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
        cachedFields.setSortedFieldMap(tempSortedFieldMap);

        // resort field
        resortField(writeHolder, cachedFields);
        return cachedFields;
    }

    /**
     * it only works when {@link WriteHolder#includeColumnFieldNames()}  or
     * {@link WriteHolder#includeColumnIndexes()}  has value
     * and {@link WriteHolder#orderByIncludeColumn()}  is true
     **/
    private static void resortField(WriteHolder writeHolder, CachedFields cachedFields) {
        if (!writeHolder.orderByIncludeColumn()) {
            return;
        }
        Map<Integer, AnnotatedFieldDescriptor> indexFieldMap = cachedFields.getIndexFieldMap();

        Collection<String> includeColumnFieldNames = writeHolder.includeColumnFieldNames();
        if (!CollectionUtils.isEmpty(includeColumnFieldNames)) {
            // Field sorted map
            Map<String, Integer> filedIndexMap = MapUtils.newHashMap();
            int fieldIndex = 0;
            for (String includeColumnFieldName : includeColumnFieldNames) {
                filedIndexMap.put(includeColumnFieldName, fieldIndex++);
            }

            // rebuild sortedFieldMap
            Map<Integer, AnnotatedFieldDescriptor> tempSortedFieldMap = MapUtils.newHashMap();
            cachedFields.getSortedFieldMap().forEach((index, field) -> {
                Integer tempFieldIndex = filedIndexMap.get(field.getFieldName());
                if (tempFieldIndex != null) {
                    tempSortedFieldMap.put(tempFieldIndex, field);

                    //  The user has redefined the ordering and the ordering of annotations needs to be invalidated
                    if (!tempFieldIndex.equals(index)) {
                        indexFieldMap.remove(index);
                    }
                }
            });
            cachedFields.setSortedFieldMap(tempSortedFieldMap);
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
            Map<Integer, AnnotatedFieldDescriptor> tempSortedFieldMap = MapUtils.newHashMap();
            cachedFields.getSortedFieldMap().forEach((index, field) -> {
                Integer tempFieldIndex = filedIndexMap.get(index);

                //  The user has redefined the ordering and the ordering of annotations needs to be invalidated
                if (tempFieldIndex != null) {
                    tempSortedFieldMap.put(tempFieldIndex, field);
                }
            });
            cachedFields.setSortedFieldMap(tempSortedFieldMap);
        }
    }

    private static Map<Integer, AnnotatedFieldDescriptor> buildSortedAllFieldMap(
            Map<Integer, List<AnnotatedFieldDescriptor>> orderFieldMap,
            Map<Integer, AnnotatedFieldDescriptor> indexFieldMap) {

        Map<Integer, AnnotatedFieldDescriptor> sortedAllFieldMap =
                new HashMap<>((orderFieldMap.size() + indexFieldMap.size()) * 4 / 3 + 1);

        Map<Integer, AnnotatedFieldDescriptor> tempIndexFieldMap = new HashMap<>(indexFieldMap);
        int index = 0;
        for (List<AnnotatedFieldDescriptor> fieldList : orderFieldMap.values()) {
            for (AnnotatedFieldDescriptor field : fieldList) {
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
            Map<Integer, List<AnnotatedFieldDescriptor>> orderFieldMap,
            Map<Integer, AnnotatedFieldDescriptor> indexFieldMap,
            Set<String> ignoreSet,
            Function<Field, AnnotationMap> resolver,
            ExcelIgnoreUnannotated excelIgnoreUnannotated) {
        String fieldName = FieldUtils.resolveCglibFieldName(field);
        // skip if the field is in ignoreSet
        if (ignoreSet.contains(fieldName)) {
            return;
        }

        AnnotatedFieldDescriptor fieldDescriptor =
                new AnnotatedFieldDescriptor(field, fieldName, resolver.apply(field));
        AnnotationAttributes excelProperty = fieldDescriptor.getAnnotation(ExcelProperty.class);

        if (excelProperty == null) {
            if (excelIgnoreUnannotated != null || isStaticFinalOrTransient(field)) {
                ignoreSet.add(fieldName);
                return;
            }
        }

        if (excelProperty != null) {
            Integer index = excelProperty.getRequiredAttribute("index", Integer.class);
            if (index >= 0) {
                if (indexFieldMap.containsKey(index)) {
                    throw new ExcelCommonException("The index of '"
                            + indexFieldMap.get(index).getFieldName() + "' and '" + field.getName()
                            + "' must be inconsistent");
                }
                indexFieldMap.put(index, fieldDescriptor);
                return;
            }
        }

        int order = Integer.MAX_VALUE;
        if (excelProperty != null) {
            order = excelProperty.getRequiredAttribute("order", Integer.class);
        }

        List<AnnotatedFieldDescriptor> orderFieldList =
                orderFieldMap.computeIfAbsent(order, key -> ListUtils.newArrayList());
        orderFieldList.add(fieldDescriptor);
    }

    private static boolean isStaticFinalOrTransient(Field field) {
        return (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                || Modifier.isTransient(field.getModifiers());
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
        FIELD_THREAD_LOCAL.remove();
        CLASS_CONTENT_THREAD_LOCAL.remove();
        CONTENT_THREAD_LOCAL.remove();
    }
}

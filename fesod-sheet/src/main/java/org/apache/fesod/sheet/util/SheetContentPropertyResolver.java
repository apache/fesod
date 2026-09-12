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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.converters.AutoConverter;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.metadata.property.FontProperty;
import org.apache.fesod.sheet.metadata.property.NumberFormatProperty;
import org.apache.fesod.sheet.metadata.property.StyleProperty;
import org.apache.fesod.sheet.util.ClassUtils.ContentPropertyKey;

/**
 * Internal helper used by {@link ClassUtils} for resolving the converter, format and style of a
 * single field, merged from the head class and the runtime class of the data.
 * <p>
 * Not intended for direct use; use {@link ClassUtils} as the primary entry point instead.
 * </p>
 */
final class SheetContentPropertyResolver {

    private static final MetadataCaches<ContentPropertyKey, ExcelContentProperty> CONTENT_CACHES =
            new MetadataCaches<>();

    private static final MetadataCaches<Class<?>, Map<String, ExcelContentProperty>> CLASS_CONTENT_CACHES =
            new MetadataCaches<>();

    private SheetContentPropertyResolver() {}

    /**
     * Calculate the configuration information for the class
     *
     * @param dataMap
     * @param headClazz
     * @param fieldName
     * @return
     */
    static ExcelContentProperty resolve(
            Map<?, ?> dataMap, Class<?> headClazz, String fieldName, ConfigurationHolder configurationHolder) {
        Class<?> clazz = null;
        if (dataMap instanceof BeanMap) {
            Object bean = ((BeanMap) dataMap).getBean();
            if (bean != null) {
                clazz = bean.getClass();
            }
        }
        return getExcelContentProperty(clazz, headClazz, fieldName, configurationHolder);
    }

    static ConcurrentHashMap<ContentPropertyKey, ExcelContentProperty> contentCache() {
        return CONTENT_CACHES.memoryCache();
    }

    static ConcurrentHashMap<Class<?>, Map<String, ExcelContentProperty>> classContentCache() {
        return CLASS_CONTENT_CACHES.memoryCache();
    }

    static void clearThreadLocalCache() {
        CLASS_CONTENT_CACHES.clearThreadLocal();
        CONTENT_CACHES.clearThreadLocal();
    }

    static void clearInMemoryCache() {
        CLASS_CONTENT_CACHES.clearInMemory();
        CONTENT_CACHES.clearInMemory();
    }

    private static ExcelContentProperty getExcelContentProperty(
            Class<?> clazz, Class<?> headClass, String fieldName, ConfigurationHolder configurationHolder) {
        return CONTENT_CACHES.get(
                configurationHolder,
                buildKey(clazz, headClass, fieldName),
                key -> doGetExcelContentProperty(clazz, headClass, fieldName, configurationHolder));
    }

    private static ExcelContentProperty doGetExcelContentProperty(
            Class<?> clazz, Class<?> headClass, String fieldName, ConfigurationHolder configurationHolder) {
        ExcelContentProperty excelContentProperty = Optional.ofNullable(
                        declaredFieldContentMap(clazz, configurationHolder))
                .map(map -> map.get(fieldName))
                .orElse(null);
        ExcelContentProperty headExcelContentProperty = Optional.ofNullable(
                        declaredFieldContentMap(headClass, configurationHolder))
                .map(map -> map.get(fieldName))
                .orElse(null);
        ExcelContentProperty combineExcelContentProperty = new ExcelContentProperty();

        combineExcelContentProperty(combineExcelContentProperty, headExcelContentProperty);
        if (clazz != headClass) {
            combineExcelContentProperty(combineExcelContentProperty, excelContentProperty);
        }
        return combineExcelContentProperty;
    }

    static void combineExcelContentProperty(
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

    private static ContentPropertyKey buildKey(Class<?> clazz, Class<?> headClass, String fieldName) {
        return new ContentPropertyKey(clazz, headClass, fieldName);
    }

    private static Map<String, ExcelContentProperty> declaredFieldContentMap(
            Class<?> clazz, ConfigurationHolder configurationHolder) {
        if (clazz == null) {
            return null;
        }
        return CLASS_CONTENT_CACHES.get(configurationHolder, clazz, key -> doDeclaredFieldContentMap(clazz));
    }

    private static Map<String, ExcelContentProperty> doDeclaredFieldContentMap(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Field> tempFieldList = FieldUtils.resolveAllFields(clazz);

        ContentStyle parentContentStyle = clazz.getAnnotation(ContentStyle.class);
        ContentFontStyle parentContentFontStyle = clazz.getAnnotation(ContentFontStyle.class);
        Map<String, ExcelContentProperty> fieldContentMap = MapUtils.newHashMapWithExpectedSize(tempFieldList.size());
        for (Field field : tempFieldList) {
            ExcelContentProperty excelContentProperty = new ExcelContentProperty();
            excelContentProperty.setField(field);

            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                Class<? extends Converter<?>> convertClazz = excelProperty.converter();
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

            ContentStyle contentStyle = field.getAnnotation(ContentStyle.class);
            if (contentStyle == null) {
                contentStyle = parentContentStyle;
            }
            excelContentProperty.setContentStyleProperty(StyleProperty.build(contentStyle));

            ContentFontStyle contentFontStyle = field.getAnnotation(ContentFontStyle.class);
            if (contentFontStyle == null) {
                contentFontStyle = parentContentFontStyle;
            }
            excelContentProperty.setContentFontProperty(FontProperty.build(contentFontStyle));

            excelContentProperty.setDateTimeFormatProperty(
                    DateTimeFormatProperty.build(field.getAnnotation(DateTimeFormat.class)));
            excelContentProperty.setNumberFormatProperty(
                    NumberFormatProperty.build(field.getAnnotation(NumberFormat.class)));

            fieldContentMap.put(field.getName(), excelContentProperty);
        }
        return fieldContentMap;
    }
}

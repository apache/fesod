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

package org.apache.fesod.sheet.metadata.property;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.annotation.AnnotatedFieldDescriptor;
import org.apache.fesod.sheet.annotation.AnnotatedTypeDescriptor;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.AnnotationMetadataReader;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.enums.HeadKindEnum;
import org.apache.fesod.sheet.metadata.CachedFields;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.util.AnnotatedClassUtils;
import org.apache.fesod.sheet.write.metadata.holder.AbstractWriteHolder;

/**
 * Define the header attribute of excel
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@Slf4j
public class ExcelHeadProperty {

    /**
     * The types of head
     */
    private HeadKindEnum headKind;

    /**
     * Custom class descriptor
     */
    private AnnotatedTypeDescriptor typeDescriptor;
    /**
     * The number of rows in the line with the most rows
     */
    private int headRowNumber;
    /**
     * Configuration header information
     */
    private Map<Integer, Head> headMap;

    private AnnotationMetadataReader metadataReader;

    public ExcelHeadProperty(ConfigurationHolder configurationHolder, Class<?> headClazz, List<List<String>> head) {
        metadataReader = new AnnotationMetadataReader(
                configurationHolder.globalConfiguration().getEnableMetaMarked());
        headMap = new TreeMap<>();
        headKind = HeadKindEnum.NONE;
        headRowNumber = 0;
        if (head != null && !head.isEmpty()) {
            int headIndex = 0;
            for (int i = 0; i < head.size(); i++) {
                if (configurationHolder instanceof AbstractWriteHolder) {
                    if (((AbstractWriteHolder) configurationHolder).ignore(null, i)) {
                        continue;
                    }
                }
                headMap.put(headIndex, new Head(headIndex, null, head.get(i), Boolean.FALSE, Boolean.TRUE));
                headIndex++;
            }
            headKind = HeadKindEnum.STRING;
        }
        // convert headClazz to head
        initColumnProperties(headClazz, configurationHolder);

        initHeadRowNumber();
        if (log.isDebugEnabled()) {
            log.debug("The initialization sheet/table 'ExcelHeadProperty' is complete , head kind is {}", headKind);
        }
    }

    private void initHeadRowNumber() {
        headRowNumber = 0;
        for (Head head : headMap.values()) {
            List<String> list = head.getHeadNameList();
            if (list != null && list.size() > headRowNumber) {
                headRowNumber = list.size();
            }
        }
        for (Head head : headMap.values()) {
            List<String> list = head.getHeadNameList();
            if (list != null && !list.isEmpty() && list.size() < headRowNumber) {
                int lack = headRowNumber - list.size();
                int last = list.size() - 1;
                for (int i = 0; i < lack; i++) {
                    list.add(list.get(last));
                }
            }
        }
    }

    private void initColumnProperties(Class<?> headClazz, ConfigurationHolder configurationHolder) {
        if (headClazz == null) {
            this.typeDescriptor = AnnotatedTypeDescriptor.EMPTY;
            return;
        }

        this.typeDescriptor = new AnnotatedTypeDescriptor(headClazz, metadataReader.read(headClazz));
        CachedFields cachedFields =
                AnnotatedClassUtils.declaredFields(headClazz, metadataReader::read, configurationHolder);

        for (Map.Entry<Integer, AnnotatedFieldDescriptor> entry :
                cachedFields.getSortedFieldMap().entrySet()) {
            initOneColumnProperty(
                    entry.getKey(),
                    entry.getValue(),
                    cachedFields.getIndexFieldMap().containsKey(entry.getKey()));
        }
        headKind = HeadKindEnum.CLASS;
    }

    /**
     * Initialization column property
     *
     * @param index
     * @param fieldDescriptor
     * @param forceIndex
     * @return Ignore current field
     */
    private void initOneColumnProperty(int index, AnnotatedFieldDescriptor fieldDescriptor, Boolean forceIndex) {
        List<String> tmpHeadList = new ArrayList<>();
        String[] heads = getHeads(fieldDescriptor);
        boolean notForceName = heads.length == 0 || (heads.length == 1 && StringUtils.isEmpty(heads[0]));

        if (headMap.containsKey(index)) {
            tmpHeadList.addAll(headMap.get(index).getHeadNameList());
        } else {
            if (notForceName) {
                tmpHeadList.add(fieldDescriptor.getFieldName());
            } else {
                Collections.addAll(tmpHeadList, heads);
            }
        }

        Head head = new Head(index, fieldDescriptor, tmpHeadList, forceIndex, !notForceName);
        headMap.put(index, head);
    }

    private static String[] getHeads(AnnotatedFieldDescriptor fieldDescriptor) {
        if (fieldDescriptor.getAnnotationCount() == 0) {
            return new String[0];
        }
        if (fieldDescriptor.hasAnnotation(ExcelProperty.class)) {
            AnnotationAttributes attrs = fieldDescriptor.getAnnotation(ExcelProperty.class);
            return attrs.getRequiredAttribute("value", String[].class);
        }
        return new String[0];
    }

    public boolean hasHead() {
        return headKind != HeadKindEnum.NONE;
    }

    public AnnotationAttributes findClazzAnnotation(Class<? extends Annotation> clazz) {
        if (HeadKindEnum.CLASS.equals(headKind)) {
            return typeDescriptor.getAnnotation(clazz);
        }
        return null;
    }

    public Class<?> getHeadClazz() {
        if (HeadKindEnum.CLASS.equals(headKind)) {
            return typeDescriptor.getAnnotatedElement();
        }
        return null;
    }
}

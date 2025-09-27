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

package org.apache.fesod.excel.metadata.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.enums.HeadKindEnum;
import org.apache.fesod.excel.metadata.ConfigurationHolder;
import org.apache.fesod.excel.metadata.FieldCache;
import org.apache.fesod.excel.metadata.FieldWrapper;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.util.ClassUtils;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.fesod.excel.write.metadata.holder.AbstractWriteHolder;

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
     * Custom class
     */
    private Class<?> headClazz;
    /**
     * The types of head
     */
    private HeadKindEnum headKind;
    /**
     * The number of rows in the line with the most rows
     */
    private int headRowNumber;
    /**
     * Configuration header information
     */
    private Map<Integer, Head> headMap;

    public ExcelHeadProperty(ConfigurationHolder configurationHolder, Class<?> headClazz, List<List<String>> head) {
        this.headClazz = headClazz;
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
                headMap.put(headIndex, new Head(headIndex, null, null, head.get(i), Boolean.FALSE, Boolean.TRUE));
                headIndex++;
            }
            headKind = HeadKindEnum.STRING;
        }
        // convert headClazz to head
        initColumnProperties(configurationHolder);

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

    private void initColumnProperties(ConfigurationHolder configurationHolder) {
        if (headClazz == null) {
            return;
        }
        FieldCache fieldCache = ClassUtils.declaredFields(headClazz, configurationHolder);

        for (Map.Entry<Integer, FieldWrapper> entry :
                fieldCache.getSortedFieldMap().entrySet()) {
            initOneColumnProperty(
                    entry.getKey(),
                    entry.getValue(),
                    fieldCache.getIndexFieldMap().containsKey(entry.getKey()));
        }
        headKind = HeadKindEnum.CLASS;
    }

    /**
     * Initialization column property
     *
     * @param index
     * @param field
     * @param forceIndex
     * @return Ignore current field
     */
    private void initOneColumnProperty(int index, FieldWrapper field, Boolean forceIndex) {
        List<String> tmpHeadList = new ArrayList<>();
        boolean notForceName = field.getHeads() == null
                || field.getHeads().length == 0
                || (field.getHeads().length == 1 && StringUtils.isEmpty(field.getHeads()[0]));
        if (headMap.containsKey(index)) {
            tmpHeadList.addAll(headMap.get(index).getHeadNameList());
        } else {
            if (notForceName) {
                tmpHeadList.add(field.getFieldName());
            } else {
                Collections.addAll(tmpHeadList, field.getHeads());
            }
        }
        Head head = new Head(index, field.getField(), field.getFieldName(), tmpHeadList, forceIndex, !notForceName);
        headMap.put(index, head);
    }

    public boolean hasHead() {
        return headKind != HeadKindEnum.NONE;
    }
}

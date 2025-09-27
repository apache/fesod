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

package org.apache.fesod.excel.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.metadata.property.ColumnWidthProperty;
import org.apache.fesod.excel.metadata.property.FontProperty;
import org.apache.fesod.excel.metadata.property.LoopMergeProperty;
import org.apache.fesod.excel.metadata.property.StyleProperty;

/**
 * excel head
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
public class Head {
    /**
     * Column index of head
     */
    private Integer columnIndex;
    /**
     * It only has values when passed in {@link Sheet#setClazz(Class)} and {@link Table#setClazz(Class)}
     */
    private Field field;
    /**
     * It only has values when passed in {@link Sheet#setClazz(Class)} and {@link Table#setClazz(Class)}
     */
    private String fieldName;
    /**
     * Head name
     */
    private List<String> headNameList;
    /**
     * Whether index is specified
     */
    private Boolean forceIndex;
    /**
     * Whether to specify a name
     */
    private Boolean forceName;

    /**
     * column with
     */
    private ColumnWidthProperty columnWidthProperty;

    /**
     * Loop merge
     */
    private LoopMergeProperty loopMergeProperty;
    /**
     * Head style
     */
    private StyleProperty headStyleProperty;
    /**
     * Head font
     */
    private FontProperty headFontProperty;

    public Head(
            Integer columnIndex,
            Field field,
            String fieldName,
            List<String> headNameList,
            Boolean forceIndex,
            Boolean forceName) {
        this.columnIndex = columnIndex;
        this.field = field;
        this.fieldName = fieldName;
        if (headNameList == null) {
            this.headNameList = new ArrayList<>();
        } else {
            this.headNameList = headNameList;
            for (String headName : headNameList) {
                if (headName == null) {
                    throw new ExcelGenerateException("head name can not be null.");
                }
            }
        }
        this.forceIndex = forceIndex;
        this.forceName = forceName;
    }
}

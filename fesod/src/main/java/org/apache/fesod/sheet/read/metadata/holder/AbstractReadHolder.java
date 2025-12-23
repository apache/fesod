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

package org.apache.fesod.sheet.read.metadata.holder;

import java.util.HashMap;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.enums.HolderEnum;
import org.apache.fesod.sheet.metadata.AbstractHolder;
import org.apache.fesod.sheet.read.listener.ModelBuildEventListener;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.read.metadata.ReadBasicParameter;
import org.apache.fesod.sheet.read.metadata.ReadWorkbook;
import org.apache.fesod.sheet.read.metadata.property.ExcelReadHeadProperty;
import org.apache.fesod.sheet.util.ListUtils;

/**
 * Read Holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public abstract class AbstractReadHolder extends AbstractHolder implements ReadHolder {
    /**
     * Count the number of added heads when read sheet.
     *
     * <p>
     * 0 - This Sheet has no head ,since the first row are the data
     * <p>
     * 1 - This Sheet has one row head , this is the default
     * <p>
     * 2 - This Sheet has two row head ,since the third row is the data
     */
    private Integer headRowNumber;
    /**
     * Excel head property
     */
    private ExcelReadHeadProperty excelReadHeadProperty;
    /**
     * Read listener
     */
    private List<ReadListener<?>> readListenerList;

    public AbstractReadHolder(ReadBasicParameter readBasicParameter, AbstractReadHolder parentAbstractReadHolder) {
        super(readBasicParameter, parentAbstractReadHolder);

        if (readBasicParameter.getUseScientificFormat() == null) {
            if (parentAbstractReadHolder != null) {
                getGlobalConfiguration()
                        .setUseScientificFormat(parentAbstractReadHolder
                                .getGlobalConfiguration()
                                .getUseScientificFormat());
            }
        } else {
            getGlobalConfiguration().setUseScientificFormat(readBasicParameter.getUseScientificFormat());
        }

        // Initialization property
        this.excelReadHeadProperty = new ExcelReadHeadProperty(this, getClazz(), getHead());
        if (readBasicParameter.getHeadRowNumber() == null) {
            if (parentAbstractReadHolder == null) {
                if (excelReadHeadProperty.hasHead()) {
                    this.headRowNumber = excelReadHeadProperty.getHeadRowNumber();
                } else {
                    this.headRowNumber = 1;
                }
            } else {
                this.headRowNumber = parentAbstractReadHolder.getHeadRowNumber();
            }
        } else {
            this.headRowNumber = readBasicParameter.getHeadRowNumber();
        }

        if (parentAbstractReadHolder == null) {
            this.readListenerList = ListUtils.newArrayList();
        } else {
            this.readListenerList = ListUtils.newArrayList(parentAbstractReadHolder.getReadListenerList());
        }
        if (HolderEnum.WORKBOOK.equals(holderType())) {
            Boolean useDefaultListener = ((ReadWorkbook) readBasicParameter).getUseDefaultListener();
            if (useDefaultListener == null || useDefaultListener) {
                readListenerList.add(new ModelBuildEventListener());
            }
        }
        if (readBasicParameter.getCustomReadListenerList() != null
                && !readBasicParameter.getCustomReadListenerList().isEmpty()) {
            this.readListenerList.addAll(readBasicParameter.getCustomReadListenerList());
        }

        if (parentAbstractReadHolder == null) {
            setConverterMap(new HashMap<>(DefaultConverterLoader.loadDefaultReadConverter()));
        } else {
            setConverterMap(new HashMap<>(parentAbstractReadHolder.getConverterMap()));
        }
        if (readBasicParameter.getCustomConverterList() != null
                && !readBasicParameter.getCustomConverterList().isEmpty()) {
            for (Converter<?> converter : readBasicParameter.getCustomConverterList()) {
                getConverterMap()
                        .put(
                                ConverterKeyBuild.buildKey(
                                        converter.supportJavaTypeKey(), converter.supportExcelTypeKey()),
                                converter);
            }
        }
    }

    @Override
    public List<ReadListener<?>> readListenerList() {
        return getReadListenerList();
    }

    @Override
    public ExcelReadHeadProperty excelReadHeadProperty() {
        return getExcelReadHeadProperty();
    }
}

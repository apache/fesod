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

import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.converters.ConverterKeyBuild;

/**
 * Write/read holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public abstract class AbstractHolder implements ConfigurationHolder {
    /**
     * Record whether it's new or from cache
     */
    private Boolean newInitialization;
    /**
     * You can only choose one of the {@link AbstractHolder#head} and {@link AbstractHolder#clazz}
     */
    private List<List<String>> head;
    /**
     * You can only choose one of the {@link AbstractHolder#head} and {@link AbstractHolder#clazz}
     */
    private Class<?> clazz;
    /**
     * Some global variables
     */
    private GlobalConfiguration globalConfiguration;
    /**
     * <p>
     * Read key:
     * <p>
     * Write key:
     */
    private Map<ConverterKeyBuild.ConverterKey, Converter<?>> converterMap;

    public AbstractHolder(BasicParameter basicParameter, AbstractHolder prentAbstractHolder) {
        this.newInitialization = Boolean.TRUE;
        if (basicParameter.getHead() == null && basicParameter.getClazz() == null && prentAbstractHolder != null) {
            this.head = prentAbstractHolder.getHead();
        } else {
            this.head = basicParameter.getHead();
        }
        if (basicParameter.getHead() == null && basicParameter.getClazz() == null && prentAbstractHolder != null) {
            this.clazz = prentAbstractHolder.getClazz();
        } else {
            this.clazz = basicParameter.getClazz();
        }
        this.globalConfiguration = new GlobalConfiguration();
        if (basicParameter.getAutoTrim() == null) {
            if (prentAbstractHolder != null) {
                globalConfiguration.setAutoTrim(
                        prentAbstractHolder.getGlobalConfiguration().getAutoTrim());
            }
        } else {
            globalConfiguration.setAutoTrim(basicParameter.getAutoTrim());
        }

        if (basicParameter.getAutoStrip() == null) {
            if (prentAbstractHolder != null) {
                globalConfiguration.setAutoStrip(
                        prentAbstractHolder.getGlobalConfiguration().getAutoStrip());
            }
        } else {
            globalConfiguration.setAutoStrip(basicParameter.getAutoStrip());
        }

        if (basicParameter.getUse1904windowing() == null) {
            if (prentAbstractHolder != null) {
                globalConfiguration.setUse1904windowing(
                        prentAbstractHolder.getGlobalConfiguration().getUse1904windowing());
            }
        } else {
            globalConfiguration.setUse1904windowing(basicParameter.getUse1904windowing());
        }

        if (basicParameter.getLocale() == null) {
            if (prentAbstractHolder != null) {
                globalConfiguration.setLocale(
                        prentAbstractHolder.getGlobalConfiguration().getLocale());
            }
        } else {
            globalConfiguration.setLocale(basicParameter.getLocale());
        }

        if (basicParameter.getFiledCacheLocation() == null) {
            if (prentAbstractHolder != null) {
                globalConfiguration.setFiledCacheLocation(
                        prentAbstractHolder.getGlobalConfiguration().getFiledCacheLocation());
            }
        } else {
            globalConfiguration.setFiledCacheLocation(basicParameter.getFiledCacheLocation());
        }
    }

    @Override
    public Map<ConverterKeyBuild.ConverterKey, Converter<?>> converterMap() {
        return getConverterMap();
    }

    @Override
    public GlobalConfiguration globalConfiguration() {
        return getGlobalConfiguration();
    }

    @Override
    public boolean isNew() {
        return getNewInitialization();
    }
}

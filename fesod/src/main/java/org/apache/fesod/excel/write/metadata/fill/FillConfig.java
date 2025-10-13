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

package org.apache.fesod.excel.write.metadata.fill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.enums.WriteDirectionEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill config
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillConfig {
    public static final String DEFAULT_DYNAMIC_INFO_KEY = "default";
    private WriteDirectionEnum direction;
    /**
     * Create a new row each time you use the list parameter.The default create if necessary.
     * <p>
     * Warnning:If you use <code>forceNewRow</code> set true, will not be able to use asynchronous write file, simply
     * say the whole file will be stored in memory.
     */
    private Boolean forceNewRow;

    /**
     * Automatically inherit style
     *
     * default true.
     */
    private Boolean autoStyle;

    private boolean hasInit;

    /**
     * dynamic column info
     * */
    private Map<String,DynamicColumnInfo> dynamicColumnInfoMap;

    /**
     * get dynamic column info
     *
     * if field name is null or not exist, return default dynamic column info
     * else return dynamic column info by field name
     *
     * @param fieldName field name nullable
     * @return dynamic column info
     * */
    public DynamicColumnInfo getDynamicColumnInfo(String fieldName) {
        if (null == dynamicColumnInfoMap) {
            return null;
        }
        if (null == fieldName || !dynamicColumnInfoMap.containsKey(fieldName)) {
            return dynamicColumnInfoMap.get(DEFAULT_DYNAMIC_INFO_KEY);
        }else{
            return dynamicColumnInfoMap.get(fieldName);
        }
    }

    public void init() {
        if (hasInit) {
            return;
        }
        if (direction == null) {
            direction = WriteDirectionEnum.VERTICAL;
        }
        if (forceNewRow == null) {
            forceNewRow = Boolean.FALSE;
        }
        if (autoStyle == null) {
            autoStyle = Boolean.TRUE;
        }
        hasInit = true;
    }

    public static class FillConfigBuilder {
        public FillConfigBuilder addDynamicInfo(List<String> keys, Integer groupSize, String fieldName) {
            if (null == dynamicColumnInfoMap) {
                dynamicColumnInfoMap = new HashMap<>();
            }
            dynamicColumnInfoMap.put(fieldName, new DynamicColumnInfo(keys, groupSize));
            return this;
        }

        public FillConfigBuilder addDefaultDynamicInfo(List<String> keys) {
            return addDynamicInfo(keys, 1, DEFAULT_DYNAMIC_INFO_KEY);
        }

        public FillConfigBuilder addDefaultDynamicInfo(List<String> keys, Integer groupSize) {
            return addDynamicInfo(keys, groupSize, DEFAULT_DYNAMIC_INFO_KEY);
        }

    }
}

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

package org.apache.fesod.excel.converters;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.util.MapUtils;

/**
 * Converter unique key.Consider that you can just use class as the key.
 */
public class ConverterKeyBuild {

    private static final Map<Class<?>, Class<?>> BOXING_MAP = MapUtils.newHashMap();

    static {
        BOXING_MAP.put(int.class, Integer.class);
        BOXING_MAP.put(byte.class, Byte.class);
        BOXING_MAP.put(long.class, Long.class);
        BOXING_MAP.put(double.class, Double.class);
        BOXING_MAP.put(float.class, Float.class);
        BOXING_MAP.put(char.class, Character.class);
        BOXING_MAP.put(short.class, Short.class);
        BOXING_MAP.put(boolean.class, Boolean.class);
    }

    public static ConverterKey buildKey(Class<?> clazz) {
        return buildKey(clazz, null);
    }

    public static ConverterKey buildKey(Class<?> clazz, CellDataTypeEnum cellDataTypeEnum) {
        Class<?> boxingClass = BOXING_MAP.get(clazz);
        if (boxingClass != null) {
            return new ConverterKey(boxingClass, cellDataTypeEnum);
        }
        return new ConverterKey(clazz, cellDataTypeEnum);
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class ConverterKey {
        private Class<?> clazz;
        private CellDataTypeEnum cellDataTypeEnum;
    }
}

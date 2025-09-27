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

package org.apache.fesod.excel.util;

import cn.idev.excel.support.cglib.beans.BeanMap;
import cn.idev.excel.support.cglib.core.DefaultNamingPolicy;

/**
 * bean utils
 *
 *
 */
public class BeanMapUtils {

    /**
     * Helper method to create a new <code>BeanMap</code>.  For finer
     * control over the generated instance, use a new instance of
     * <code>BeanMap.Generator</code> instead of this static method.
     *
     * Custom naming policy to prevent null pointer exceptions.
     *
     * @param bean the JavaBean underlying the map
     * @return a new <code>BeanMap</code> instance
     */
    public static BeanMap create(Object bean) {
        BeanMap.Generator gen = new BeanMap.Generator();
        gen.setBean(bean);
        gen.setContextClass(bean.getClass());
        gen.setNamingPolicy(FastExcelNamingPolicy.INSTANCE);
        return gen.create();
    }

    public static class FastExcelNamingPolicy extends DefaultNamingPolicy {
        public static final FastExcelNamingPolicy INSTANCE = new FastExcelNamingPolicy();

        @Override
        protected String getTag() {
            return "ByFastExcelCGLIB";
        }
    }
}

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

package org.apache.fesod.beans.cglib;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fesod.common.beans.BeanWrapper;
import org.apache.fesod.common.util.ValidateUtils;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.apache.fesod.shaded.cglib.core.DefaultNamingPolicy;

/**
 * A {@link BeanWrapper} implementation backed by CGLIB's {@link BeanMap}.
 */
public final class CglibBeanWrapper implements BeanWrapper {

    private static final Map<Class<?>, BeanMap> BEAN_MAP_CACHE = new ConcurrentHashMap<>();
    private final BeanMap delegate;

    public CglibBeanWrapper(Object bean) {
        ValidateUtils.notNull(bean, "The bean instance must not be null");

        this.delegate = initBeanMap(bean);
    }

    private BeanMap initBeanMap(Object bean) {
        BeanMap beanMap = BEAN_MAP_CACHE.computeIfAbsent(bean.getClass(), clazz -> {
            BeanMap.Generator gen = new BeanMap.Generator();
            gen.setBeanClass(clazz);
            gen.setContextClass(clazz);
            gen.setNamingPolicy(FesodSheetNamingPolicy.INSTANCE);
            return gen.create();
        });

        return beanMap.newInstance(bean);
    }

    @Override
    public Object getProperty(String propertyName) {
        return delegate.get(propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        delegate.put(propertyName, value);
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        delegate.putAll(properties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getPropertyNames() {
        return delegate.keySet();
    }

    @Override
    public boolean containsProperty(String propertyName) {
        return delegate.containsKey(propertyName);
    }

    @Override
    public int getPropertySize() {
        return delegate.size();
    }

    @Override
    public Class<?> getPropertyType(String propertyName) {
        return delegate.getPropertyType(propertyName);
    }

    @Override
    public Object unwrap() {
        return delegate.getBean();
    }

    public static class FesodSheetNamingPolicy extends DefaultNamingPolicy {
        public static final FesodSheetNamingPolicy INSTANCE = new FesodSheetNamingPolicy();

        @Override
        protected String getTag() {
            return "ByFesodCGLIB";
        }
    }
}

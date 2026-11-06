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

package org.apache.fesod.common.beans;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.fesod.common.util.ValidateUtils;

/**
 * A {@link BeanWrapper} implementation that adapts a {@link Map} into a bean property accessor.
 */
public final class MapBeanWrapper implements BeanWrapper {

    private final Map<String, Object> delegate;

    public MapBeanWrapper(Map<String, Object> map) {
        this.delegate = ValidateUtils.notNull(map, "The map must not be null");
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
        if (properties != null && !properties.isEmpty()) {
            delegate.putAll(properties);
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(delegate.keySet());
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
        Object value = delegate.get(propertyName);
        return value != null ? value.getClass() : null;
    }

    @Override
    public Object unwrap() {
        return delegate;
    }
}

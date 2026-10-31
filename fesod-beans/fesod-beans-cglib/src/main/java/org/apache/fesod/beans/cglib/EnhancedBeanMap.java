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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copied from {@link org.apache.fesod.shaded.cglib.beans.BeanMap}.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class EnhancedBeanMap implements Map {

    public static final int REQUIRE_GETTER = 1;

    public static final int REQUIRE_SETTER = 2;

    public static EnhancedBeanMap create(Object bean) {
        // APACHE FESOD PATCH BEGIN
        EnhancedBeanMapGenerator gen = new EnhancedBeanMapGenerator();
        // APACHE FESOD PATCH END
        gen.setBean(bean);
        return gen.create();
    }

    // APACHE FESOD PATCH BEGIN
    public abstract EnhancedBeanMap newInstance(Object bean);
    // APACHE FESOD PATCH END

    public abstract Class getPropertyType(String name);

    protected Object bean;

    protected EnhancedBeanMap() {}

    protected EnhancedBeanMap(Object bean) {
        setBean(bean);
    }

    @Override
    public Object get(Object key) {
        return get(bean, key);
    }

    @Override
    public Object put(Object key, Object value) {
        return put(bean, key, value);
    }

    // APACHE FESOD PATCH BEGIN
    public void set(Object key, Object value) {
        set(bean, key, value);
    }
    // APACHE FESOD PATCH END

    public abstract Object get(Object bean, Object key);

    public abstract Object put(Object bean, Object key, Object value);

    // APACHE FESOD PATCH BEGIN
    /**
     * Set the property of a bean. This is a convenience variant of
     * {@link #put(Object, Object, Object)} that performs the same assignment
     * but does not return the old value.
     *
     * @param key must be a String
     */
    public abstract void set(Object bean, Object key, Object value);
    // APACHE FESOD PATCH END

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Iterator it = keySet().iterator(); it.hasNext(); ) {
            Object v = get(it.next());
            if (((value == null) && (v == null)) || (value != null && value.equals(v))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map t) {
        for (Object key : t.keySet()) {
            put(key, t.get(key));
        }
    }

    // APACHE FESOD PATCH BEGIN
    public void setAll(Map t) {
        for (Object key : t.keySet()) {
            set(key, t.get(key));
        }
    }
    // APACHE FESOD PATCH END

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Map)) {
            return false;
        }
        Map other = (Map) o;
        if (size() != other.size()) {
            return false;
        }
        for (Object key : keySet()) {
            if (!other.containsKey(key)) {
                return false;
            }
            Object v1 = get(key);
            Object v2 = other.get(key);
            if (!((v1 == null) ? v2 == null : v1.equals(v2))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (Object key : keySet()) {
            Object value = get(key);
            code += ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
        }
        return code;
    }

    @Override
    public Set entrySet() {
        HashMap copy = new HashMap();
        for (Object key : keySet()) {
            copy.put(key, get(key));
        }
        return Collections.unmodifiableMap(copy).entrySet();
    }

    @Override
    public Collection values() {
        Set keys = keySet();
        List values = new ArrayList(keys.size());
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            values.add(get(it.next()));
        }
        return Collections.unmodifiableCollection(values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (Iterator it = keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            sb.append(key);
            sb.append('=');
            sb.append(get(key));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}

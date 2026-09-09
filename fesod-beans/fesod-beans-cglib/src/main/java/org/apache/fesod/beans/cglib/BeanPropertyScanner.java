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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.fesod.shaded.cglib.core.CodeGenerationException;

/**
 * Utility class for Bean property introspection.
 *
 * <p>
 * Extends the standard JavaBean introspection ({@link Introspector}) to further support fluent-style accessors.
 * </p>
 * <ul>
 *     <li>Standard accessors: {@code getName()} / {@code setName(String)}.</li>
 *     <li>Fluent accessors: {@code name()} / {@code name(String)} / {@code T name(String)}.</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BeanPropertyScanner {

    private static final PropertyDescriptor[] EMPTY_DESCRIPTORS = new PropertyDescriptor[0];

    /**
     * Retrieve descriptors for all readable properties (including standard getters and fluent getters) of the target class.
     *
     * @param type target class
     */
    public static PropertyDescriptor[] getBeanGetters(Class<?> type) {
        return getBeanProperties(type, true, false);
    }

    /**
     * Retrieve descriptors for all writeable properties (including standard setters and fluent setters) of the target class.
     *
     * @param type target class
     */
    public static PropertyDescriptor[] getBeanSetters(Class<?> type) {
        return getBeanProperties(type, false, true);
    }

    private static PropertyDescriptor[] getBeanProperties(Class<?> type, boolean read, boolean write) {
        try {
            Map<String, PropertyDescriptor> propertyMap = new LinkedHashMap<>();

            BeanInfo info = Introspector.getBeanInfo(type, Object.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                propertyMap.put(pd.getName(), pd);
            }

            // Check for fluent-style accessors without prefix: for example, "lastName()", "lastName(String lastName)"
            collectFluentAccessors(type, propertyMap);

            if (propertyMap.isEmpty()) {
                return EMPTY_DESCRIPTORS;
            }

            List<PropertyDescriptor> properties = new ArrayList<>(propertyMap.size());
            for (PropertyDescriptor pd : propertyMap.values()) {
                if ((read && pd.getReadMethod() != null) || (write && pd.getWriteMethod() != null)) {
                    properties.add(pd);
                }
            }

            return properties.toArray(EMPTY_DESCRIPTORS);
        } catch (IntrospectionException e) {
            throw new CodeGenerationException(e);
        }
    }

    private static void collectFluentAccessors(Class<?> type, Map<String, PropertyDescriptor> propertyMap)
            throws IntrospectionException {
        Map<String, Field> fieldMap = getAllFields(type);

        for (Method method : type.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())
                    || method.isSynthetic()
                    || method.getDeclaringClass() == Object.class
                    || method.getDeclaringClass() == Class.class) {
                continue;
            }

            Field field = fieldMap.get(method.getName());
            if (field == null) {
                continue;
            }

            if (isFluentGetter(method, field)) {
                PropertyDescriptor pd = propertyMap.get(method.getName());
                if (pd == null) {
                    pd = new PropertyDescriptor(method.getName(), type, null, null);
                    propertyMap.put(method.getName(), pd);
                }
                if (pd.getReadMethod() == null) {
                    pd.setReadMethod(method);
                }
            } else if (isFluentSetter(method, field, type)) {
                PropertyDescriptor pd = propertyMap.get(method.getName());
                if (pd == null) {
                    pd = new PropertyDescriptor(method.getName(), type, null, null);
                    propertyMap.put(method.getName(), pd);
                }
                if (pd.getWriteMethod() == null) {
                    pd.setWriteMethod(method);
                }
            }
        }
    }

    private static boolean isFluentSetter(Method method, Field field, Class<?> targetType) {
        return method.getParameterCount() == 1
                && method.getParameterTypes()[0] == field.getType()
                && (method.getReturnType() == void.class
                        || method.getReturnType().isAssignableFrom(targetType)
                        || method.getReturnType().isAssignableFrom(method.getDeclaringClass()));
    }

    private static boolean isFluentGetter(Method method, Field field) {
        return method.getParameterCount() == 0 && method.getReturnType() == field.getType();
    }

    private static Map<String, Field> getAllFields(Class<?> type) {
        Map<String, Field> fields = new HashMap<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
                    fields.putIfAbsent(field.getName(), field);
                }
            }
            current = current.getSuperclass();
        }
        return fields;
    }
}

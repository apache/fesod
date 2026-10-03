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

package org.apache.fesod.sheet.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A collection of attribute methods for a specific annotation type.
 * provides indexed access and alias compatibility validation.
 */
class AttributeMethods {

    private static final Map<Class<? extends Annotation>, AttributeMethods> attributeMethodsCache =
            new ConcurrentHashMap<>();

    private final Class<? extends Annotation> type;

    private final List<Method> methods;

    private final Map<String, Method> methodMap;

    private AttributeMethods(Class<? extends Annotation> annotationType) {
        this.type = annotationType;
        Method[] declaredMethods = annotationType.getDeclaredMethods();
        List<Method> tmpMethods = new ArrayList<>(declaredMethods.length);
        this.methodMap = new HashMap<>(declaredMethods.length);

        for (Method method : declaredMethods) {
            if (isAttributeMethod(method)) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                tmpMethods.add(method);
                this.methodMap.put(method.getName(), method);
            }
        }
        this.methods = Collections.unmodifiableList(tmpMethods);
    }

    public static AttributeMethods from(Class<? extends Annotation> annotationType) {
        return attributeMethodsCache.computeIfAbsent(annotationType, AttributeMethods::new);
    }

    static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    public Method getMethod(String attributeName) {
        return methodMap.get(attributeName);
    }

    public List<Method> getAttributeMethods() {
        return methods;
    }

    public void validateAliasFor(Method attribute, String attributeName) {
        Method target = getMethod(attributeName);
        if (target == null) {
            throw new IllegalStateException(String.format(
                    "Annotation [%s] does not declare attribute [%s] referenced by @AliasFor",
                    type.getName(), attributeName));
        }
        if (!isCompatibleReturnType(attribute.getReturnType(), target.getReturnType())) {
            throw new IllegalStateException(String.format(
                    "Return type of attribute [%s#%s()] must match return type of target attribute [%s#%s()]",
                    attribute.getDeclaringClass().getName(),
                    attribute.getName(),
                    target.getDeclaringClass().getName(),
                    attributeName));
        }
    }

    private boolean isCompatibleReturnType(Class<?> attributeType, Class<?> targetType) {
        return (attributeType == targetType || attributeType == targetType.getComponentType());
    }
}

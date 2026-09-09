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
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.commons.lang3.ClassUtils;

/**
 * {@link InvocationHandler} implementation used to support synthesized annotation proxy
 * instances created from {@link AnnotationAttributes}.
 */
class SynthesizedAnnotationInvocationHandler implements InvocationHandler {

    private final Class<? extends Annotation> type;
    private final AnnotationAttributes attributes;

    public SynthesizedAnnotationInvocationHandler(
            Class<? extends Annotation> annotationType, AnnotationAttributes attributes) {
        this.type = annotationType;
        this.attributes = attributes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = attributes.getAttribute(method.getName(), method.getReturnType());
        if (result != null) {
            return result;
        }

        if (method.getParameterCount() == 0) {
            switch (method.getName()) {
                case "annotationType":
                    return this.type;
                case "hashCode":
                    return handleHashCode();
                case "toString":
                    return handleToString();
            }
        }
        if (method.getParameterCount() == 1
                && "equals".equals(method.getName())
                && method.getParameterTypes()[0] == Object.class) {
            return handleEquals(proxy, args[0]);
        }

        throw new UnsupportedOperationException(
                String.format("Method [%s] is unsupported for synthesized annotation type [%s]", method, this.type));
    }

    private boolean handleEquals(Object proxy, Object other) {
        if (proxy == other) {
            return true;
        }
        if (!this.type.isInstance(other)) {
            return false;
        }
        if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(other);
            if (handler instanceof SynthesizedAnnotationInvocationHandler) {
                return this.attributes.equals(((SynthesizedAnnotationInvocationHandler) handler).attributes);
            }
        }

        AttributeMethods attributeMethods = AttributeMethods.from(this.type);
        for (Map.Entry<String, Object> entry : attributes.asImmutableMap().entrySet()) {
            try {
                Method m = attributeMethods.getMethod(entry.getKey());
                if (!Objects.deepEquals(entry.getValue(), m.invoke(other))) {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private int handleHashCode() {
        int hashCode = 0;
        for (Map.Entry<String, Object> entry : attributes.asImmutableMap().entrySet()) {
            hashCode += (127 * entry.getKey().hashCode()) ^ calcAttributeValueHashCode(entry.getValue());
        }
        return hashCode;
    }

    private int calcAttributeValueHashCode(Object value) {
        if (!value.getClass().isArray()) {
            return Objects.hashCode(value);
        }

        if (value instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) value);
        }
        if (value instanceof byte[]) {
            return Arrays.hashCode((byte[]) value);
        }
        if (value instanceof short[]) {
            return Arrays.hashCode((short[]) value);
        }
        if (value instanceof int[]) {
            return Arrays.hashCode((int[]) value);
        }
        if (value instanceof long[]) {
            return Arrays.hashCode((long[]) value);
        }
        if (value instanceof float[]) {
            return Arrays.hashCode((float[]) value);
        }
        if (value instanceof double[]) {
            return Arrays.hashCode((double[]) value);
        }
        if (value instanceof char[]) {
            return Arrays.hashCode((char[]) value);
        }
        return Arrays.hashCode((Object[]) value);
    }

    private String handleToString() {
        Iterator<Entry<String, Object>> item =
                attributes.asImmutableMap().entrySet().iterator();
        StringBuilder sb = new StringBuilder()
                .append('@')
                .append(ClassUtils.getCanonicalName(type))
                .append('(');

        if (!item.hasNext()) {
            return sb.append(')').toString();
        }

        for (; ; ) {
            Map.Entry<String, Object> e = item.next();
            String key = e.getKey();
            Object value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(toString(value));
            if (!item.hasNext()) {
                return sb.append(')').toString();
            }
            sb.append(',').append(' ');
        }
    }

    private String toString(Object value) {
        Class<?> valueType = value.getClass();
        if (valueType.isArray()) {
            StringBuilder builder = new StringBuilder("{");
            int arrayLength = Array.getLength(value);
            for (int i = 0; i < arrayLength; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(toString(Array.get(value, i)));
            }
            builder.append('}');
            return builder.toString();
        }
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        if (valueType == Class.class) {
            return ClassUtils.getCanonicalName((Class<?>) value) + ".class";
        }
        return String.valueOf(value);
    }
}

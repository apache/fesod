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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

/**
 * Resolved key-value pairs attributes for an annotation.
 * Provides type-safe lookup of annotation attributes.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnnotationAttributes {

    /**
     * The type of the annotation, represented by this {@code AnnotationAttributes}.
     */
    @Getter
    @EqualsAndHashCode.Include
    private final Class<? extends Annotation> annotationType;

    /**
     * The class name of the annotation type.
     */
    @Getter
    private final String annotationName;

    /**
     * The key-value attribute pairs declared on the annotation.
     */
    @EqualsAndHashCode.Include
    private final Map<String, Object> attributes;

    AnnotationAttributes(Class<? extends Annotation> annotationType, Map<String, Object> attrs) {
        this.annotationType = annotationType;
        this.annotationName = annotationType.getName();
        this.attributes = new LinkedHashMap<>(attrs);
    }

    public boolean isAnnotationTypeEqual(Class<? extends Annotation> annotationType) {
        return this.annotationType.equals(annotationType);
    }

    void put(String attrName, Object value) {
        attributes.put(attrName, value);
    }

    /**
     * Get an attribute value from the annotation.
     *
     * @param attributeName the attribute name
     * @return the attribute value or {@code null} if not found
     */
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Get an attribute value from the annotation.
     *
     * @param attributeName the attribute name
     * @param type the attribute type
     * @return the attribute value or {@code null} if not found
     * @throws IllegalArgumentException if the value cannot be converted/cast to the target type
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String attributeName, Class<T> type) {
        Object result = getAttribute(attributeName);
        if (Objects.isNull(result)) {
            return null;
        }

        Class<?> wrapped = ClassUtils.primitiveToWrapper(type);

        if (!wrapped.isInstance(result)
                && type.isArray()
                && ClassUtils.primitiveToWrapper(type.getComponentType()).isInstance(result)) {
            Object array = Array.newInstance(type.getComponentType(), 1);
            Array.set(array, 0, result);
            result = array;
        }
        if (!wrapped.isInstance(result)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected for annotation [%s]",
                    attributeName, result.getClass().getSimpleName(), type.getSimpleName(), annotationName));
        }

        return (T) result;
    }

    /**
     * Get a required attribute value from the annotation.
     *
     * @param attributeName the attribute name
     * @param type the attribute type
     * @return the attribute value
     * @throws NullPointerException if the {@code attributeName} is {@code null}
     * @throws IllegalArgumentException if the {@code attributeName} is blank or attribute does not exist
     */
    public <T> T getRequiredAttribute(String attributeName, Class<T> type) {
        Validate.notBlank(attributeName, "attributeName must not be null or blank");
        T result = getAttribute(attributeName, type);
        if (Objects.isNull(result)) {
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' not found for annotation '%s'", attributeName, annotationName));
        }
        return result;
    }

    /**
     * Returns an unmodifiable view of the attributes map.
     */
    public Map<String, Object> asImmutableMap() {
        return Collections.unmodifiableMap(attributes);
    }
}

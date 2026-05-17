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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

/**
 * Implement key-value pairs of annotation attributes based on {@link LinkedHashMap}.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class AnnotationAttributes extends LinkedHashMap<String, Object> {

    private final Class<? extends Annotation> annotationType;
    private final String annotationName;

    @Setter
    private int distance;

    public AnnotationAttributes(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
        this.annotationName = annotationType.getCanonicalName();
        this.distance = 0;
    }

    public AnnotationAttributes(Class<? extends Annotation> annotationType, Map<String, Object> attrs) {
        super(attrs);
        this.annotationType = annotationType;
        this.annotationName = annotationType.getCanonicalName();
        this.distance = 0;
    }

    @SuppressWarnings("unchecked")
    public <T> T getRequiredAttribute(String attrName, Class<T> type) {
        Validate.notBlank(attrName, "attributeName must not be null or blank");
        Object result = get(attrName);

        if (Objects.isNull(result)) {
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' not found for annotation '%s'", attrName, annotationName));
        }
        if (!type.isInstance(result)
                && type.isArray()
                && type.getComponentType().isInstance(result)) {
            Object array = Array.newInstance(type.getComponentType(), 1);
            Array.set(array, 0, result);
            result = array;
        }
        if (!type.isInstance(result)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected for annotation [%s]",
                    attrName, result.getClass().getSimpleName(), type.getSimpleName(), annotationName));
        }

        return (T) result;
    }
}

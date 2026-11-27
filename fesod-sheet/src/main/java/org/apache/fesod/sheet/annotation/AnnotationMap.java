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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Validate;

/**
 * A wrapper class for all annotation (include composable annotation) attribute key-value pairs
 * associated with {@link AnnotatedElement}.
 */
@EqualsAndHashCode
public class AnnotationMap {

    public static final AnnotationMap EMPTY = new AnnotationMap(Collections.emptyMap());

    private final Map<Class<? extends Annotation>, AnnotationAttributes> annotations;

    AnnotationMap(Map<Class<? extends Annotation>, AnnotationAttributes> annotations) {
        this.annotations = annotations;
    }

    /**
     * Check if the annotations map is empty.
     */
    public boolean isEmpty() {
        return MapUtils.isEmpty(annotations);
    }

    /**
     * Returns the number of annotations.
     */
    public int size() {
        return annotations.size();
    }

    /**
     * Returns whether the specified annotation type is present.
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return !isEmpty() && annotations.containsKey(annotationType);
    }

    /**
     * Get the {@link AnnotationAttributes} for the specified annotation type.
     *
     * @param annotationType the annotation type
     * @return the {@link AnnotationAttributes} or {@code null} if not found
     */
    public AnnotationAttributes getAttributes(Class<? extends Annotation> annotationType) {
        if (isEmpty()) {
            return null;
        }
        return annotations.get(annotationType);
    }

    /**
     * Create a synthesized annotation proxy of the specified annotation type.
     *
     * @param annotationType the annotation type
     * @return the synthesized annotation proxy, or {@code null} if annotation is not present
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T synthesize(Class<T> annotationType) {
        AnnotationAttributes attributes = getAttributes(annotationType);
        if (attributes == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(
                annotationType.getClassLoader(),
                new Class[] {annotationType},
                new SynthesizedAnnotationInvocationHandler(annotationType, attributes));
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final Map<Class<? extends Annotation>, AnnotationAttributes> ann;

        Builder() {
            this.ann = new LinkedHashMap<>(8);
        }

        Builder putIfAbsent(Class<? extends Annotation> annotationType, AnnotationAttributes attributes) {
            Validate.notNull(annotationType, "annotationType must not be null");
            Validate.notNull(attributes, "attributes must not be null");

            ann.putIfAbsent(annotationType, attributes);
            return this;
        }

        AnnotationMap build() {
            return new AnnotationMap(ann);
        }
    }
}

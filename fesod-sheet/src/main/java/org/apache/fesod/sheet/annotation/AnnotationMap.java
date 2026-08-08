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
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Validate;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

/**
 * A wrapper class for all annotation (include composable annotation) attribute key-value pairs
 * associated with {@link AnnotatedElement}.
 */
@EqualsAndHashCode
public class AnnotationMap {

    private final Map<Class<? extends Annotation>, AnnotationAttributes> annotations;

    public AnnotationMap(Map<Class<? extends Annotation>, AnnotationAttributes> annotations) {
        this.annotations = annotations;
    }

    public boolean isEmpty() {
        return MapUtils.isEmpty(annotations);
    }

    public int size() {
        return annotations.size();
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return !isEmpty() && annotations.containsKey(annotationType);
    }

    public AnnotationAttributes getAttributes(Class<? extends Annotation> annotationType) {
        if (isEmpty()) {
            return null;
        }
        return annotations.get(annotationType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Class<? extends Annotation>, AnnotationAttributes> ann;

        public Builder() {
            this.ann = new ConcurrentHashMap<>(8);
        }

        public Builder put(Class<? extends Annotation> annotationType, AnnotationAttributes attributes) {
            Validate.notNull(annotationType, "annotationType must not be null");
            Validate.notNull(attributes, "attributes must not be null");

            ann.put(annotationType, attributes);
            return this;
        }

        public Builder merge(Class<? extends Annotation> annotationType, AnnotationAttributes attributes) {
            Validate.notNull(annotationType, "annotationType must not be null");
            Validate.notNull(attributes, "attributes must not be null");

            AnnotationAttributes oldAttrs = ann.get(annotationType);
            if (oldAttrs == null) {
                ann.put(annotationType, attributes);
            } else if (attributes.getDistance() < oldAttrs.getDistance()) {
                oldAttrs.putAll(attributes);
            }
            return this;
        }

        public AnnotationMap build() {
            return new AnnotationMap(ann);
        }
    }
}

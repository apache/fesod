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
import java.util.Objects;

/**
 * Descriptor abstract base class, providing generic annotation extraction logic.
 */
public abstract class AbstractAnnotatedElementDescriptor<E extends AnnotatedElement>
        implements AnnotatedElementDescriptor<E> {

    protected final E annotatedElement;
    protected final AnnotationMap annotationMap;

    protected AbstractAnnotatedElementDescriptor(E annotatedElement, AnnotationMap annotationMap) {
        this.annotatedElement = annotatedElement;
        this.annotationMap = annotationMap;
    }

    /**
     * Get the original annotated element.
     */
    @Override
    public E getAnnotatedElement() {
        return annotatedElement;
    }

    /**
     * Get a wrapper for all annotation (include composable annotation) attribute key-value pairs.
     */
    @Override
    public AnnotationMap getAnnotationMap() {
        return annotationMap;
    }

    /**
     * Determine whether the specified annotation exists on this element.
     */
    @Override
    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return Objects.nonNull(annotationMap) && annotationMap.hasAnnotation(type);
    }

    /**
     * Get the number of annotations.
     */
    @Override
    public int getAnnotationCount() {
        return Objects.nonNull(annotationMap) ? annotationMap.size() : 0;
    }

    /**
     * Get the attributes of a specified annotation.
     */
    @Override
    public AnnotationAttributes getAnnotation(Class<? extends Annotation> type) {
        return Objects.nonNull(annotationMap) ? annotationMap.getAttributes(type) : null;
    }
}

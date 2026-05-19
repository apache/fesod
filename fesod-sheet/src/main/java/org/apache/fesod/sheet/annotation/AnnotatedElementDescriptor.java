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

/**
 * A generic interface for describing annotation elements.
 *
 * @param <E> The specific type of annotated element, such as {@code Class}, {@code Field}.
 */
public interface AnnotatedElementDescriptor<E extends AnnotatedElement> {

    /**
     * Get the original annotated element.
     */
    E getAnnotatedElement();

    /**
     * Get a wrapper for all annotation (include composable annotation) attribute key-value pairs.
     */
    AnnotationMap getAnnotationMap();

    /**
     * Determine whether the specified annotation exists on this element.
     */
    boolean hasAnnotation(Class<? extends Annotation> type);

    /**
     * Get the number of annotations.
     */
    int getAnnotationCount();

    /**
     * Get the attributes of a specified annotation.
     */
    AnnotationAttributes getAnnotation(Class<? extends Annotation> type);
}

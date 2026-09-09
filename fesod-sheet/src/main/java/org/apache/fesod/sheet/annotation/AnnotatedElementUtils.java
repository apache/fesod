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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility methods for finding and resolving composable annotations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotatedElementUtils {

    private static final AnnotationMetadataReader reader = new AnnotationMetadataReader();

    /**
     * Get the merged annotation metadata for the given element.
     *
     * @param element the annotated element
     */
    public static AnnotationMap getAnnotationMap(AnnotatedElement element) {
        if (element == null) {
            return AnnotationMap.EMPTY;
        }
        AnnotationMap map = reader.read(element);
        return map != null ? map : AnnotationMap.EMPTY;
    }

    /**
     * Get the merged annotation of the specified {@code annotationType} on the supplied element.
     *
     * @param element        the annotated element
     * @param annotationType the target annotation type
     * @param <T>            the annotation type
     * @return the synthesized annotation instance or null if not found
     */
    public static <T extends Annotation> T getMergedAnnotation(AnnotatedElement element, Class<T> annotationType) {
        return getAnnotationMap(element).synthesize(annotationType);
    }

    /**
     * Retrieve the merged {@link AnnotationAttributes} for the specified {@code annotationType} on the supplied element.
     *
     * @param element        the annotated element
     * @param annotationType the target annotation type
     * @return the merged {@link AnnotationAttributes} or {@code null} if the annotation is not present
     */
    public static AnnotationAttributes getMergedAnnotationAttributes(
            AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getAnnotationMap(element).getAttributes(annotationType);
    }

    /**
     * Determine whether the given annotation type is present either directly declared or as a
     * meta-annotation on the supplied element.
     *
     * @param element        the annotated element
     * @param annotationType the target annotation type
     */
    public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getAnnotationMap(element).hasAnnotation(annotationType);
    }
}

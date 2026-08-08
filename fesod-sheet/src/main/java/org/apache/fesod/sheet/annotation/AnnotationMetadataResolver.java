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

/**
 * Strategy interface for resolving and introspecting annotation metadata.
 */
public interface AnnotationMetadataResolver {

    /**
     * Determine if the given annotation type should be ignored by the scanner.
     *
     * @param type the type to check
     * @return {@code true} if the annotation should be skipped
     */
    boolean shouldIgnore(Class<? extends Annotation> type);

    /**
     * Determine if the annotation is a framework-intrinsic "Inner" annotation.
     *
     * @param ann the annotation instance to check
     * @return {@code true} if it is a framework-internal
     */
    boolean isInnerAnnotated(Annotation ann);

    /**
     * Determine if the annotation is marked with the core meta-protocol.
     *
     * @param ann the annotation instance to check
     * @return {@code true} if it is a composable meta-annotation
     */
    boolean isMetaMarked(Annotation ann);

    /**
     * Resolve a raw {@link Annotation} into a {@link AnnotationMetadata} object.
     *
     * @param ann the annotation instance to resolve
     * @return the resolved metadata
     */
    AnnotationMetadata resolve(Annotation ann);
}

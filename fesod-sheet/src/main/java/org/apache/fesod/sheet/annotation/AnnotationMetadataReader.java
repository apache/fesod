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

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import org.apache.commons.collections4.map.ConcurrentReferenceHashMap;

/**
 * A coordinator for discovering and reading annotation metadata from {@link AnnotatedElement}s.
 */
public class AnnotationMetadataReader extends HierarchicalAnnotationScanner {

    private final Map<AnnotatedElement, AnnotationMap> elementAnnotation;

    public AnnotationMetadataReader() {
        this(Boolean.TRUE);
    }

    public AnnotationMetadataReader(Boolean enableMetaMarked) {
        this(
                new DefaultAnnotationMetadataResolver(),
                enableMetaMarked,
                ConcurrentReferenceHashMap.<AnnotatedElement, AnnotationMap>builder()
                        .get());
    }

    public AnnotationMetadataReader(
            AnnotationMetadataResolver resolver,
            Boolean enableMetaMarked,
            Map<AnnotatedElement, AnnotationMap> elementAnnotation) {
        super(resolver, enableMetaMarked);
        this.elementAnnotation = elementAnnotation;
    }

    /**
     * Read the merged annotation metadata for the given element.
     *
     * @param element the class, field
     * @return the resolved {@link AnnotationMap}
     */
    public AnnotationMap read(AnnotatedElement element) {
        return elementAnnotation.computeIfAbsent(element, super::scan);
    }
}

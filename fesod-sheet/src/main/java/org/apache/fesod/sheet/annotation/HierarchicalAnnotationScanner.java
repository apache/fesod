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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Abstract base class for scanning and storing composable annotations.
 */
abstract class HierarchicalAnnotationScanner {

    protected final AnnotationMetadataResolver metadataResolver = new AnnotationMetadataResolver();

    protected HierarchicalAnnotationScanner() {}

    protected AnnotationMap scan(AnnotatedElement element) {
        Annotation[] annotations = element.getAnnotations();
        if (ArrayUtils.isEmpty(annotations)) {
            return AnnotationMap.EMPTY;
        }

        AnnotationMap.Builder builder = AnnotationMap.builder();
        Queue<AnnotationNode> queue = new LinkedList<>();
        Set<Class<? extends Annotation>> rootAnnTypes = new HashSet<>(annotations.length);

        for (Annotation root : annotations) {
            rootAnnTypes.add(root.annotationType());
            queue.add(new AnnotationNode(root, Collections.emptyList()));
        }

        while (!queue.isEmpty()) {
            AnnotationNode current = queue.poll();
            Class<? extends Annotation> type = current.annotationType();

            if (metadataResolver.shouldIgnore(type)) {
                continue;
            }

            AnnotationMetadata metadata = metadataResolver.resolve(current.annotation);

            // Apply aliases
            applyAliasesIfNecessary(metadata, current.aliases);

            // Handle composable-annotations (low-level attribute value)
            if (metadataResolver.isMetaMarked(current.annotation)) {
                for (Annotation metaAnn : type.getAnnotations()) {
                    if (metadataResolver.shouldIgnore(metaAnn.annotationType())
                            || rootAnnTypes.contains(metaAnn.annotationType())
                            || current.isVisited(metaAnn.annotationType())) {
                        continue;
                    }
                    queue.add(current.next(metaAnn, metadata.getAliases()));
                }
            }

            builder.putIfAbsent(type, metadata.getAttributes());
        }

        return builder.build();
    }

    /**
     * Apply alias mapping rules ({@link AliasFor}) to the target annotation metadata.
     *
     * @param metadata the target annotation metadata
     * @param aliases the aliases inherited from the declaring annotation
     */
    private void applyAliasesIfNecessary(AnnotationMetadata metadata, List<AliasFor> aliases) {
        if (CollectionUtils.isEmpty(aliases)) {
            return;
        }

        for (AliasFor aliasFor : aliases) {
            metadata.applyAliasFor(aliasFor);
        }
    }

    private static class AnnotationNode {
        final Annotation annotation;
        final List<AliasFor> aliases;
        // Record visited annotation, to avoid circular dependencies (like: @A -> @B, @B -> @A)
        final Set<Class<? extends Annotation>> path;

        AnnotationNode(Annotation annotation, List<AliasFor> aliases, Set<Class<? extends Annotation>> path) {
            this.annotation = annotation;
            this.aliases = aliases;
            this.path = path;
        }

        AnnotationNode(Annotation annotation, List<AliasFor> aliases) {
            this(annotation, aliases, new HashSet<>());
        }

        Class<? extends Annotation> annotationType() {
            return annotation.annotationType();
        }

        boolean isVisited(Class<? extends Annotation> type) {
            return path.contains(type);
        }

        AnnotationNode next(Annotation annotation, List<AliasFor> aliases) {
            Set<Class<? extends Annotation>> fullPath = new HashSet<>(path);
            fullPath.add(annotationType());
            return new AnnotationNode(annotation, aliases, fullPath);
        }
    }
}

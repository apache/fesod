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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fesod.common.util.StringUtils;

/**
 * Providing introspection and resolution of annotation metadata.
 */
class AnnotationMetadataResolver {

    private static final String JAVA_LANG_ANNOTATION_PACKAGE_PREFIX = "java.lang.annotation";

    private final Map<Class<?>, Boolean> metaMarkedMap = new ConcurrentHashMap<>();
    private final Map<AnnotatedElement, Boolean> metaAliasMap = new ConcurrentHashMap<>();

    /**
     * Determine if the given annotation type should be ignored by the scanner:
     * JDK-standard meta-annotations such as {@code @Target} or {@code @Retention},
     * and the {@code @FesodMarked} protocol marker itself.
     *
     * @param type the type to check
     * @return {@code true} if the annotation should be skipped
     */
    public boolean shouldIgnore(Class<? extends Annotation> type) {
        return type == FesodMarked.class || type.getName().startsWith(JAVA_LANG_ANNOTATION_PACKAGE_PREFIX);
    }

    /**
     * Determine if the annotation is marked ({@code @FesodMarked}) with the core meta-protocol.
     *
     * @param ann the annotation instance to check
     * @return {@code true} if it is a composable meta-annotation
     */
    public boolean isMetaMarked(Annotation ann) {
        Class<? extends Annotation> type = ann.annotationType();
        return metaMarkedMap.computeIfAbsent(type, k -> type.getAnnotation(FesodMarked.class) != null);
    }

    /**
     * Resolve a raw {@link Annotation} into a {@link AnnotationMetadata} object.
     *
     * @param ann the annotation instance to resolve
     * @return the resolved metadata
     */
    public AnnotationMetadata resolve(Annotation ann) {
        Map<Class<? extends Annotation>, AttributeMethods> markedAnnMap = new HashMap<>();
        if (isMetaMarked(ann)) {
            Annotation[] annotations = ann.annotationType().getAnnotations();
            for (Annotation markedAnn : annotations) {
                if (!shouldIgnore(markedAnn.annotationType())) {
                    markedAnnMap.put(markedAnn.annotationType(), AttributeMethods.from(markedAnn.annotationType()));
                }
            }
        }

        List<AliasFor> aliases = new ArrayList<>();
        Map<String, Object> attr = new LinkedHashMap<>();

        AttributeMethods attributeMethods = AttributeMethods.from(ann.annotationType());
        for (Method method : attributeMethods.getAttributeMethods()) {
            String attrName = method.getName();
            try {
                Object result = method.invoke(ann);

                // Handle @FesodMarked.AliasFor
                if (isMetaAlias(method)) {
                    FesodMarked.AliasFor aliasFor = method.getAnnotation(FesodMarked.AliasFor.class);

                    AttributeMethods targetAttrMethods = markedAnnMap.get(aliasFor.annotation());
                    if (targetAttrMethods == null) {
                        if (!isMetaMarked(ann)) {
                            throw new IllegalStateException(String.format(
                                    "The custom-annotation '%s' declares @FesodMarked.AliasFor but is not annotated with @FesodMarked",
                                    ann.annotationType().getName()));
                        }

                        throw new IllegalStateException(String.format(
                                "The alias annotation '%s' is not marked on the custom-annotation '%s'",
                                aliasFor.annotation().getName(),
                                ann.annotationType().getName()));
                    }

                    String targetAttrName =
                            StringUtils.isNotBlank(aliasFor.attribute()) ? aliasFor.attribute() : attrName;
                    targetAttrMethods.validateAliasFor(method, targetAttrName);

                    aliases.add(new AliasFor(
                            ann.annotationType(), aliasFor.annotation(), attrName, targetAttrName, result));
                }
                attr.put(attrName, result);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new IllegalStateException(
                        String.format(
                                "Failed to invoke annotation [%s] method [%s]",
                                ann.annotationType().getName(), attrName),
                        ex);
            }
        }
        return new AnnotationMetadata(new AnnotationAttributes(ann.annotationType(), attr), aliases);
    }

    private boolean isMetaAlias(AnnotatedElement element) {
        return metaAliasMap.computeIfAbsent(element, k -> element.getAnnotation(FesodMarked.AliasFor.class) != null);
    }
}

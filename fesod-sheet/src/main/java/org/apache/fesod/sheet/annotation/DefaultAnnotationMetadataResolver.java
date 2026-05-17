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
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Native;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link AnnotationMetadataResolver} interface,
 * providing introspection and resolution of annotation metadata.
 */
public class DefaultAnnotationMetadataResolver implements AnnotationMetadataResolver {

    private static final Set<Class<? extends Annotation>> IGNORE_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> INNER_ANNOTATIONS;

    private final Map<Class<?>, Boolean> metaMakedMap = new ConcurrentHashMap<>();
    private final Map<AnnotatedElement, Boolean> metaAliasMap = new ConcurrentHashMap<>();

    static {
        Set<Class<? extends Annotation>> ignoreTmp = new HashSet<>();
        ignoreTmp.add(Target.class);
        ignoreTmp.add(Retention.class);
        ignoreTmp.add(Documented.class);
        ignoreTmp.add(Repeatable.class);
        ignoreTmp.add(Native.class);
        ignoreTmp.add(Inherited.class);
        IGNORE_ANNOTATIONS = Collections.unmodifiableSet(ignoreTmp);

        Set<Class<? extends Annotation>> innerTmp = new HashSet<>();
        innerTmp.add(ExcelProperty.class);
        innerTmp.add(ExcelIgnoreUnannotated.class);
        innerTmp.add(ExcelIgnore.class);
        innerTmp.add(DateTimeFormat.class);
        innerTmp.add(NumberFormat.class);
        innerTmp.add(ColumnWidth.class);
        innerTmp.add(ContentFontStyle.class);
        innerTmp.add(ContentLoopMerge.class);
        innerTmp.add(ContentRowHeight.class);
        innerTmp.add(ContentStyle.class);
        innerTmp.add(HeadFontStyle.class);
        innerTmp.add(HeadRowHeight.class);
        innerTmp.add(HeadStyle.class);
        innerTmp.add(OnceAbsoluteMerge.class);
        INNER_ANNOTATIONS = Collections.unmodifiableSet(innerTmp);
    }

    /**
     * Determine if the given annotation type should be ignored by the scanner.
     * used to filter out JDK-standard meta-annotations such as {@code @Target} or {@code @Retention}.
     *
     * @param type the type to check
     * @return {@code true} if the annotation should be skipped
     */
    @Override
    public boolean shouldIgnore(Class<? extends Annotation> type) {
        return IGNORE_ANNOTATIONS.contains(type);
    }

    /**
     * Determine if the annotation is a framework-intrinsic "Inner" annotation.
     * Such as {@code ExcelProperty} or {@code DateTimeFormat}...
     *
     * @param ann the annotation instance to check
     * @return {@code true} if it is a framework-internal
     */
    @Override
    public boolean isInnerAnnotated(Annotation ann) {
        return INNER_ANNOTATIONS.contains(ann.annotationType());
    }

    /**
     * Determine if the annotation is marked ({@code @FesodMarked}) with the core meta-protocol.
     *
     * @param ann the annotation instance to check
     * @return {@code true} if it is a composable meta-annotation
     */
    @Override
    public boolean isMetaMarked(Annotation ann) {
        Class<? extends Annotation> type = ann.annotationType();
        return metaMakedMap.computeIfAbsent(type, k -> type.getAnnotation(FesodMarked.class) != null);
    }

    /**
     * Resolve a raw {@link Annotation} into a {@link AnnotationMetadata} object.
     *
     * @param ann the annotation instance to resolve
     * @return the resolved metadata
     */
    @Override
    public AnnotationMetadata resolve(Annotation ann) {
        Set<String> markedAnnNames = new HashSet<>();
        if (isMetaMarked(ann)) {
            Annotation[] annotations = ann.annotationType().getAnnotations();
            for (Annotation markedAnn : annotations) {
                markedAnnNames.add(markedAnn.annotationType().getName());
            }
        }

        Method[] methods = ann.annotationType().getDeclaredMethods();

        List<AliasFor> aliases = new ArrayList<>();
        Map<String, Object> attr = Arrays.stream(methods)
                .filter(this::isEffectMethod)
                .collect(Collectors.toMap(Method::getName, method -> {
                    try {
                        Object result = Optional.ofNullable(method.invoke(ann)).orElseGet(method::getDefaultValue);

                        // Handle @FesodMarked.AliasFor
                        if (isMetaAlias(method)) {
                            FesodMarked.AliasFor aliasFor = method.getAnnotation(FesodMarked.AliasFor.class);
                            if (!markedAnnNames.contains(aliasFor.annotation().getName())) {
                                throw new IllegalStateException(String.format(
                                        "The alias annotation '%s' is not marked on the custom-annotation '%s'",
                                        aliasFor.annotation().getName(),
                                        ann.annotationType().getName()));
                            }

                            aliases.add(new AliasFor(
                                    ann.annotationType(), aliasFor.annotation(), aliasFor.attribute(), result));
                        }
                        return result;
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new IllegalStateException(
                                String.format(
                                        "Failed to invoke annotation [%s] method [%s]",
                                        ann.annotationType().getName(), method.getName()),
                                ex);
                    }
                }));
        return new AnnotationMetadata(new AnnotationAttributes(ann.annotationType(), attr), aliases);
    }

    private boolean isEffectMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != void.class;
    }

    private boolean isMetaAlias(AnnotatedElement element) {
        return metaAliasMap.computeIfAbsent(element, k -> element.getAnnotation(FesodMarked.AliasFor.class) != null);
    }
}

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

package org.apache.fesod.sheet.annotation.composite;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Consumer;
import org.apache.fesod.sheet.annotation.AnnotatedElementDescriptor;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.AnnotationMap;
import org.junit.jupiter.api.Assertions;

/**
 * A simple assertion tool for {@link AnnotatedElementDescriptor} + {@link AnnotationMap}.
 */
class AnnotatedDescriptorAssertions {

    public static <T extends AnnotatedElementDescriptor<?>> AnnotatedElementDescriptorAsserts<T> assertThat(
            T descriptor) {
        return new AnnotatedElementDescriptorAsserts<>(descriptor);
    }

    static class AnnotatedElementDescriptorAsserts<T extends AnnotatedElementDescriptor<?>> {

        private final T descriptor;

        AnnotatedElementDescriptorAsserts(T descriptor) {
            this.descriptor = descriptor;
        }

        public AnnotatedElementDescriptorAsserts<T> isNull() {
            Assertions.assertNull(descriptor, "AnnotatedElementDescriptor should be null");
            return this;
        }

        public AnnotatedElementDescriptorAsserts<T> isNotNull() {
            Assertions.assertNotNull(descriptor, "AnnotatedElementDescriptor should not be null");
            return this;
        }

        public AnnotatedElementDescriptorAsserts<T> doesNotHaveAnnotationMap() {
            Assertions.assertNull(descriptor.getAnnotationMap(), "AnnotationMap should be null");
            return this;
        }

        public AnnotatedElementDescriptorAsserts<T> hasAnnotationCount(int annotationCount) {
            Assertions.assertEquals(annotationCount, descriptor.getAnnotationCount(), "AnnotationCount mismatch");
            return this;
        }

        public AnnotatedElementDescriptorAsserts<T> isAnnotatedElementEquals(AnnotatedElement element) {
            Assertions.assertEquals(element, descriptor.getAnnotatedElement(), "AnnotatedElement type mismatch");
            return this;
        }

        public AnnotatedElementDescriptorAsserts<T> satisfies(Consumer<T> consumer) {
            consumer.accept(descriptor);
            return this;
        }

        public AnnotationMapAsserts extractingAnnotations() {
            AnnotationMap annotationMap = descriptor.getAnnotationMap();
            Assertions.assertNotNull(annotationMap, "AnnotationMap should not be null");
            return new AnnotationMapAsserts(this, annotationMap);
        }
    }

    static class AnnotationMapAsserts {
        private final AnnotatedElementDescriptorAsserts<?> parent;
        private final AnnotationMap annotationMap;

        AnnotationMapAsserts(AnnotatedElementDescriptorAsserts<?> parent, AnnotationMap annotationMap) {
            this.parent = parent;
            this.annotationMap = annotationMap;
        }

        public AnnotationMapAsserts hasSize(int expected) {
            Assertions.assertEquals(expected, annotationMap.size(), "Annotation size mismatch");
            return this;
        }

        public AnnotationMapAsserts doesNotHaveAnnotation(Class<? extends Annotation> annotation) {
            Assertions.assertFalse(
                    annotationMap.hasAnnotation(annotation),
                    String.format(
                            "Expected annotation <%s> to be absent, but it was found.", annotation.getCanonicalName()));
            return this;
        }

        public AnnotationAttributesAsserts hasAnnotation(Class<? extends Annotation> annotation) {
            Assertions.assertTrue(
                    annotationMap.hasAnnotation(annotation),
                    String.format(
                            "Expected annotation <%s> to be present, but it was missing.",
                            annotation.getCanonicalName()));

            AnnotationAttributes attrs = annotationMap.getAttributes(annotation);
            return new AnnotationAttributesAsserts(this, attrs.getAnnotationName(), attrs);
        }

        public AnnotatedElementDescriptorAsserts<?> and() {
            return parent;
        }
    }

    static class AnnotationAttributesAsserts {
        private final AnnotationMapAsserts parent;
        private final String annotationName;
        private final AnnotationAttributes attrs;

        AnnotationAttributesAsserts(AnnotationMapAsserts parent, String annotationName, AnnotationAttributes attrs) {
            this.parent = parent;
            this.annotationName = annotationName;
            this.attrs = attrs;
        }

        public AnnotationAttributesAsserts hasAttributeSize(int expected) {
            Assertions.assertEquals(
                    expected, attrs.size(), String.format("Attribute size in @%s mismatch", annotationName));
            return this;
        }

        public AnnotationAttributesAsserts containsAttribute(String name) {
            Assertions.assertTrue(
                    attrs.containsKey(name), String.format("Attribute '%s' in @%s mismatch", name, annotationName));
            return this;
        }

        public AnnotationAttributesAsserts hasAttributeWithValue(String name, Object expected) {
            containsAttribute(name);

            Object actual = attrs.getRequiredAttribute(name, expected.getClass());

            if (expected instanceof Object[]) {
                Assertions.assertArrayEquals(
                        (Object[]) expected,
                        (Object[]) actual,
                        String.format("Attribute '%s' in @%s mismatch", name, annotationName));
            } else {
                Assertions.assertEquals(
                        expected, actual, String.format("Attribute '%s' in @%s mismatch", name, annotationName));
            }
            return this;
        }

        public AnnotationAttributesAsserts satisfies(Consumer<AnnotationAttributes> consumer) {
            consumer.accept(attrs);
            return this;
        }

        public AnnotationAttributesAsserts hasAnnotation(Class<? extends Annotation> annotationName) {
            return parent.hasAnnotation(annotationName);
        }

        public AnnotationMapAsserts and() {
            return parent;
        }
    }
}

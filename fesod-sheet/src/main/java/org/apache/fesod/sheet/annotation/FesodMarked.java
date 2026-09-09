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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code FesodMarked} is a meta-annotation (annotations used on other annotations)
 * used for indicating that instead of using target annotation
 * (annotation annotated with this annotation),
 * Fesod should use meta-annotations it has.
 * This can be useful in creating "Composable Annotations" by having
 * a container annotation, which needs to be annotated with this
 * annotation as well as all annotations it 'contains'.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FesodMarked {

    /**
     * {@code @AliasFor} is an annotation that is used to declare aliases for
     * annotation attributes.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasFor {

        /**
         * The type of annotation in which the aliased attribute() is declared.
         */
        Class<? extends Annotation> annotation();

        /**
         * The name of the attribute that this attribute is an alias for.
         * <p>Defaults to {@code ""}, meaning that the aliased attribute has the same
         * name as the attribute that declares this {@code @AliasFor} annotation.
         */
        String attribute() default "";
    }
}

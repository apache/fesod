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
import lombok.Getter;

/**
 * A value object representing a declarative attribute aliasing instruction.
 */
@Getter
public class AliasFor {

    /**
     * The source annotation that declares the alias.
     */
    private final Class<? extends Annotation> marked;

    /**
     * The target meta-annotation being aliased.
     */
    private final Class<? extends Annotation> target;

    /**
     * The name of the attribute in the source annotation.
     */
    private final String customAttribute;

    /**
     * The name of the attribute in the target annotation to be overridden.
     */
    private final String attribute;

    /**
     * The value of the attribute in the target annotation to be overridden.
     */
    private final Object value;

    public AliasFor(
            Class<? extends Annotation> marked, Class<? extends Annotation> target, String attribute, Object value) {
        this(marked, target, attribute, attribute, value);
    }

    public AliasFor(
            Class<? extends Annotation> marked,
            Class<? extends Annotation> target,
            String customAttribute,
            String attribute,
            Object value) {
        this.marked = marked;
        this.target = target;
        this.customAttribute = customAttribute;
        this.attribute = attribute;
        this.value = value;
    }
}
